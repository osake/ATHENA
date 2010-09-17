/*
ATHENA Project: Management Tools for the Cultural Sector
Copyright (C) 2010, Fractured Atlas
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/
 */
package org.fracturedatlas.athena.apa.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.fracturedatlas.athena.apa.AbstractApaAdapter;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.apa.exception.ApaException;
import org.fracturedatlas.athena.apa.exception.ImmutableObjectException;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.PropValue;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.TicketProp;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.springframework.beans.factory.annotation.Autowired;

public class JpaApaAdapter extends AbstractApaAdapter implements ApaAdapter {

    @Autowired
    private EntityManagerFactory emf;
    Logger logger = Logger.getLogger(this.getClass().getName());

    @PersistenceUnit
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Ticket getTicket(Object id) {
        EntityManager em = this.emf.createEntityManager();
        try {
            Long longId = LongUserType.massageToLong(id);
            if (longId == null) {
                return null;
            } else {
                Ticket t = em.find(Ticket.class, longId);
                return t;
            }
        } finally {
            cleanup(em);
        }
    }

    @Override
    public Ticket saveTicket(Ticket t) {
        EntityManager em = this.emf.createEntityManager();
        try {
            em.getTransaction().begin();
            t.setId(LongUserType.massageToLong(t.getId()));
            // Make sure the TicketProps are set to this ticket
            // If the caller forgets to do it, and we don't do it here, then JPA
            // will bonk
            if (t.getTicketProps() != null) {
                for (TicketProp prop : t.getTicketProps()) {
                    enforceStrict(prop.getPropField(), prop.getValueAsString());
                    enforceCorrectValueType(prop.getPropField(), prop);
                    prop.setTicket(t);
                }
            }
            t = (Ticket) em.merge(t);
            em.getTransaction().commit();
            return t;
        } catch (ApaException e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            throw e;
        }
        finally {
            cleanup(em);
        }
    }

    private void enforceCorrectValueType(PropField propField, TicketProp prop) throws InvalidValueException {

        propField = getPropField(propField.getId());
        if (!propField.getValueType().newTicketProp().getClass().getName().equals(prop.getClass().getName())) {
            String err = "Value [" + prop.getValueAsString() + "] is not a valid value for the field [" + propField.getName() + "].  ";
            err += "Field is of type [" + propField.getValueType().name() + "].";
            throw new InvalidValueException(err);
        }
    }

    private void enforceStrict(PropField propField, String value) throws InvalidValueException {

        if (propField.getStrict()) {

            //Reload the propField because we <3 Hibernate
            propField = getPropField(propField.getId());
            Collection<PropValue> propValues = propField.getPropValues();
            PropValue targetValue = new PropValue(propField, value);

            //TODO: Should be using a .contains method here or something
            for (PropValue propValue : propValues) {
                if (propValue.getPropValue().equals(value)) {
                    return;
                }
            }
            throw new InvalidValueException("Value [" + value + "] is not a valid value for the strict field [" + propField.getName() + "]");
        }
    }

    @Override
    public Boolean deleteTicket(Object id) {
        logger.debug("Deleting ticket: " + id);
        if (id == null) {
            return false;
        }
        EntityManager em = this.emf.createEntityManager();
        try {
            Long longId = LongUserType.massageToLong(id);
            em.getTransaction().begin();
            Ticket t = em.find(Ticket.class, longId);
            logger.debug("Deleting ticket: " + t);
            em.remove(t);
            logger.debug("Deleted ticket: " + longId);
            em.getTransaction().commit();
            return true;
        } finally {
            cleanup(em);
        }
    }

    @Override
    public Boolean deleteTicket(Ticket t) {
        return deleteTicket(t.getId());
    }

    /**
     * @param searchParams a key/value list of parameters to search for tickets on
     * @param sortParams a map of sort options.  For a given entry, set the key equal to a ticket property name and the value
     *        to ASC or DESC
     * @return Set of tickets whose Props match the searchParams
     */
    @Override
    public Set<Ticket> findTickets(HashMap<String, String> searchParams) {
        logger.debug("Searching for tickets matching [" + searchParams + "]");
        Set<String> keys = searchParams.keySet();
        Object[] keyArray = keys.toArray();
        EntityManager em = this.emf.createEntityManager();
        try {
            // find all fields with given name
            Query query = em.createQuery("FROM PropField propField WHERE propField.name IN (:names)");
            query.setParameter("names", keys);
            List<PropField> fields = query.getResultList();
            // the tickets that have this property set
            Set<Ticket> propTickets = new HashSet<Ticket>();
            // a temporary list of tickets
            Collection<Ticket> tempTickets = new ArrayList<Ticket>();
            // is this the first field we've inspected?
            boolean firstField = true;
            // for each field
            for (PropField field : fields) {
                String searchValue = searchParams.get(field.getName());

                //TODO: If searchValue is null, punch out.  It's likely a mismatched case
                // like they seatched for "Artist" when the actual key name is "artist"

                logger.debug("Checking field [" + field.getName()
                        + "] for tickets with prop ["
                        + searchValue + "]");
                logger.debug(field.getTicketProps().size()
                        + " properties on this field to check");
                // for each property that has this field name
                for (TicketProp prop : field.getTicketProps()) {
                    // if the value equals the value we're looking for, add it to
                    // the propTickets list
                    if (prop.valueEquals(searchValue)) {
                        propTickets.add(prop.getTicket());
                    }
                }
                logger.debug("Found " + propTickets.size() + " tickets");
                // if we didn't find any tickets for this property, then we're done.
                // Punch out and return an empty set
                if (propTickets.size() == 0) {
                    logger.debug("No tickets found for this property, exiting and returning empty set");
                    tempTickets = new ArrayList<Ticket>();
                    break;
                }
                // if this is the first time through, just put all the tickets we
                // found in our temp list
                if (firstField) {
                    tempTickets.addAll(propTickets);
                    firstField = false;
                } else {
                    // otherwise, find the intersection of our temp tickets list and
                    // the list of tix attached to this property
                    // NOTE: this will get expensive for very large lists
                    tempTickets = CollectionUtils.intersection(tempTickets,
                            propTickets);
                    logger.debug(tempTickets.size()
                            + " tickets remain after intersecting");
                }
                propTickets = new HashSet<Ticket>();
            }
            // the tickets that we'll be returning to the user
            Set<Ticket> tickets = new HashSet<Ticket>();
            tickets.addAll(tempTickets);
            logger.debug("Returning " + tickets.size() + " tickets");
            return tickets;
        } finally {
            cleanup(em);
        }
    }

    @Override
    public List<TicketProp> saveTicketProps(List<TicketProp> props) {
        List<TicketProp> outProps = new ArrayList<TicketProp>();
        EntityManager em = this.emf.createEntityManager();
        try {
            em.getTransaction().begin();

            for (TicketProp prop : props) {
                enforceStrict(prop.getPropField(), prop.getValueAsString());
                enforceCorrectValueType(prop.getPropField(), prop);
                prop.setId(LongUserType.massageToLong(prop.getId()));
                prop = (TicketProp) em.merge(prop);
            }
            em.getTransaction().commit();
            return outProps;
        } catch (ApaException e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            throw e;
        } finally {
            cleanup(em);
        }

    }

    @Override
    public TicketProp saveTicketProp(TicketProp prop) throws InvalidValueException {
        EntityManager em = this.emf.createEntityManager();
        try {
            em.getTransaction().begin();
            enforceStrict(prop.getPropField(), prop.getValueAsString());
            enforceCorrectValueType(prop.getPropField(), prop);
            prop.setId(LongUserType.massageToLong(prop.getId()));
            prop = (TicketProp) em.merge(prop);
            em.getTransaction().commit();
            return prop;
        } finally {
            cleanup(em);
        }
    }

    @Override
    public TicketProp getTicketProp(Object id) {
        EntityManager em = this.emf.createEntityManager();
        try {
            TicketProp prop = em.find(TicketProp.class, LongUserType.massageToLong(id));
            return prop;
        } finally {
            cleanup(em);
        }
    }

    @Override
    public TicketProp getTicketProp(String fieldName, Object ticketId) {
        EntityManager em = this.emf.createEntityManager();

        try {
            Long longTicketId = LongUserType.massageToLong(ticketId);

            //TODO: Would this be faster to first select propFields with fieldName, then use that id to
            //search ticketProp?
            Query query = em.createQuery("FROM TicketProp ticketProp WHERE ticketProp.propField.name=:fieldName AND ticketProp.ticket.id=:ticketId");
            query.setParameter("fieldName", fieldName);
            query.setParameter("ticketId", longTicketId);

            //TODO: There must be a better way of getting asingle result in JPA.
            //Using exceptions as flow control is kinda lame
            try {
                TicketProp ticketProp = (TicketProp) query.getSingleResult();
                return ticketProp;
            } catch (javax.persistence.NoResultException nre) {
                return null;
            }
        } finally {
            cleanup(em);
        }
    }

    @Override
    public PropField getPropField(Object id) {
        EntityManager em = this.emf.createEntityManager();
        try {
            Long longId = LongUserType.massageToLong(id);
            PropField pf = em.find(PropField.class, longId);
            if (pf != null) {
                pf.getTicketProps().size();
            }
            return pf;
        } finally {
            cleanup(em);
        }
    }

    @Override
    public PropField getPropField(String name) {
        EntityManager em = this.emf.createEntityManager();
        try {
            Query query = em.createQuery("FROM PropField pf where pf.name=:name");
            query.setParameter("name", name);

            try {
                PropField propField = (PropField) query.getSingleResult();
                return propField;
            } catch (javax.persistence.NoResultException nre) {
                return null;
            }
        } finally {
            cleanup(em);
        }
    }

    @Override
    public PropField savePropField(PropField propField) throws ImmutableObjectException {

        //strict must be set
        if (propField.getValueType() == null) {
            throw new ApaException("Please specify a value type");
        }

        //strict must be set
        if (propField.getStrict() == null) {
            throw new ApaException("Please specify strict as true or false");
        }

        if (propField.getStrict() && propField.getValueType().equals(ValueType.BOOLEAN)) {
            throw new ApaException("Boolean fields cannot be marked as strict");
        }
        
        //check for immutability
        if (propField.getId() != null) {
            PropField oldPropField = getPropField(propField.getId());
            checkExists(oldPropField);
            checkImmutability(propField, oldPropField);
        } else {
            checkForDuplicatePropField(propField.getName());
        }
        checkForDuplicatePropValue(propField);

        EntityManager em = this.emf.createEntityManager();
        try {
            em.getTransaction().begin();
            propField.setId(LongUserType.massageToLong(propField.getId()));
            // loop through propValue ids to massage to long
            Collection<PropValue> propValues = propField.getPropValues();
            if (propValues != null) {
                for (PropValue propValue : propValues) {
                    propValue.setId(LongUserType.massageToLong(propValue.getId()));
                    propValue.setPropField(propField);
                }
            }
            propField = (PropField) em.merge(propField);

            if (propField.getPropValues() == null) {
                propField.setPropValues(new ArrayList<PropValue>());
            }

            em.getTransaction().commit();
            return propField;
        } finally {
            cleanup(em);
        }
    }

    @Override
    public PropValue savePropValue(PropValue propValue) {
        EntityManager em = this.emf.createEntityManager();
        try {
            PropField tmpPropField = getPropField(propValue.getPropField().getId());
            for (PropValue value : tmpPropField.getPropValues()) {
                if (propValue.getPropValue().equals(value.getPropValue())) {
                    throw new ApaException("Field [" + tmpPropField.getId() + "] already has a value set of [" + value.getPropValue() + "]");
                }
            }

            em.getTransaction().begin();
            if (tmpPropField != null) {
                tmpPropField.setId(LongUserType.massageToLong(tmpPropField.getId()));
                propValue.setPropField(tmpPropField);
            }
            propValue.setId(LongUserType.massageToLong(propValue.getId()));
            propValue = (PropValue) em.merge(propValue);
            em.getTransaction().commit();
            return propValue;
        } finally {
            cleanup(em);
        }
    }

    private class PropValueComparator implements Comparator<PropValue> {
        public int compare(PropValue value1, PropValue value2) {
            return value1.getPropValue().compareTo(value2.getPropValue());
        }
    }

    private void checkForDuplicatePropValue(PropField propField) throws ApaException {
        if(propField.getPropValues() == null) {
            return;
        }

        Set<PropValue> duplicates = new TreeSet<PropValue>(new PropValueComparator());
        for (PropValue value : propField.getPropValues()) {
            if(!duplicates.add(value)) {
                throw new ApaException("Cannot save Field [" + propField.getId() + "] because it contains duplicate values of [" + value.getPropValue() + "]");
            }
        }
    }

    private void checkForDuplicatePropField(String name) throws ApaException {
        PropField duplicate = getPropField(name);
        if (duplicate != null) {
            throw new ApaException("Field [" + name + "] already exists.");
        }
    }

    private void checkExists(PropField propField) throws ApaException {
        if (propField == null) {
            throw new ApaException("Cannot update field with Id [" + propField.getId() + "] because the propField was not found");
        }
    }

    private void checkImmutability(PropField newPropField, PropField oldPropField) throws ImmutableObjectException {
        if (!newPropField.getStrict().equals(oldPropField.getStrict())) {
            throw new ImmutableObjectException("You cannot change the strictness of a field after is has been saved");
        } else if (!newPropField.getValueType().equals(oldPropField.getValueType())) {
            throw new ImmutableObjectException("You cannot change the type of a field after is has been saved");
        }
    }

    /*
     * So nuts that you can't do a em.remove(prop)
     */
    @Override
    public void deleteTicketProp(TicketProp prop) {
        EntityManager em = this.emf.createEntityManager();
        try {
            em.getTransaction().begin();
            prop = em.merge(prop);
            Ticket t = prop.getTicket();
            t.getTicketProps().remove(prop);
            em.remove(prop);
            t = em.merge(t);
            em.getTransaction().commit();
        } finally {
            cleanup(em);
        }
    }

    @Override
    public Boolean deletePropField(Object id) {
        EntityManager em = this.emf.createEntityManager();
        try {
            Long longId = LongUserType.massageToLong(id);
            PropField pf = em.find(PropField.class, longId);
            if (pf != null) {
                em.getTransaction().begin();
                em.remove(pf);
                em.getTransaction().commit();
                return true;
            } else {
                return false;
            }
        } finally {
            cleanup(em);
        }
    }

    @Override
    public Boolean deletePropField(PropField propField) {
        return deletePropField(propField.getId());
    }

    @Override
    public List<PropField> getPropFields() {
        EntityManager em = this.emf.createEntityManager();
        try {
            Query query = em.createQuery("FROM PropField pf");
            return query.getResultList();
        } finally {
            cleanup(em);
        }
    }

    @Override
    public PropValue getPropValue(Object propValueId) {
        EntityManager em = this.emf.createEntityManager();
        try {
            PropValue propValue = em.find(PropValue.class, LongUserType.massageToLong(propValueId));
            return propValue;
        } finally {
            cleanup(em);
        }
    }

    @Override
    public Collection<PropValue> getPropValues(Object propFieldId) {
        EntityManager em = this.emf.createEntityManager();
        try {
            PropField pf = em.find(PropField.class, LongUserType.massageToLong(propFieldId));
            logger.debug("PropField is " + pf.toString());
            Query query = em.createQuery("FROM PropValue propValue WHERE propField IN (:pf)");
            query.setParameter("pf", pf);
            List<PropValue> values = query.getResultList();
            logger.debug("PropValues are " + values.toString());
            return values;
        } finally {
            cleanup(em);
        }
    }

    @Override
    public boolean deletePropValue(Object id) {
        EntityManager em = this.emf.createEntityManager();
        try {
            Long longId = LongUserType.massageToLong(id);
            PropValue pv = em.find(PropValue.class, longId);
            if (pv != null) {
                PropField propField = getPropField(pv.getPropField().getId());
                propField.getPropValues().remove(pv);
                em.getTransaction().begin();
                em.remove(pv);
                propField = em.merge(propField);
                em.getTransaction().commit();
                return true;
            } else {
                return false;
            }
        } finally {
            cleanup(em);
        }
    }

    @Override
    public boolean deletePropValue(PropValue propValue) {
        return deletePropValue(propValue.getId());
    }

    private void cleanup(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }
}
