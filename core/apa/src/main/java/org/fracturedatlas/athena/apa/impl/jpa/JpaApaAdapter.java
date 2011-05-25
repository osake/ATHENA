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
package org.fracturedatlas.athena.apa.impl.jpa;

import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fracturedatlas.athena.apa.AbstractApaAdapter;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.apa.exception.ApaException;
import org.fracturedatlas.athena.apa.exception.ImmutableObjectException;
import org.fracturedatlas.athena.apa.exception.InvalidFieldException;
import org.fracturedatlas.athena.apa.exception.InvalidPropException;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.apa.impl.LongUserType;
import org.fracturedatlas.athena.apa.impl.jpa.TicketProp;
import org.springframework.beans.factory.annotation.Autowired;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.Operator;

public class JpaApaAdapter extends AbstractApaAdapter implements ApaAdapter {

    private Boolean BLOW = false;

    @Autowired
    private EntityManagerFactory emf;
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @PersistenceUnit
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public PTicket getRecord(String type, Object id) {
        JpaRecord r = getTicket(type, id);
        if(r != null) {
            return r.toClientTicket();
        } else {
            return null;
        }
    }


    private  JpaRecord getTicket(String type, Object id) {
        return getTicket(null, type, id);
    }

    private JpaRecord getTicket(EntityManager em, String type, Object id) {
        boolean closeTransaction = (em == null);
        if(em == null) {
            em = this.emf.createEntityManager();
        }
        try {
            Long longId = LongUserType.massageToLong(id);
            if (longId == null) {
                return null;
            } else {
                try {
                    JpaRecord t = (JpaRecord) em.createQuery("from JpaRecord as ticket where id=:id AND type=:ticketType").setParameter("id", longId).setParameter("ticketType", type).getSingleResult();
                    return t;
                } catch (NoResultException nre) {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(closeTransaction) {
                cleanup(em);
            }
        }
    }

    private JpaRecord saveRecord(JpaRecord t, EntityManager em) {
        boolean useTransaction = (em == null);

        if(em == null) {
            em = this.emf.createEntityManager();
        }
        try {
            if(useTransaction) {
                em.getTransaction().begin();
            }
            t.setId(LongUserType.massageToLong(t.getId()));
            // Make sure the TicketProps are set to this existingTicket
            // If the caller forgets to do it, and we don't do it here, then JPA
            // will bonk
            if (t.getTicketProps() != null) {
                for (TicketProp prop : t.getTicketProps()) {
                    enforceStrict(prop.getPropField(), prop.getValueAsString());
                    enforceCorrectValueType(prop.getPropField(), prop, em);
                    prop.setTicket(t);
                }
            }
            t = (JpaRecord) em.merge(t);
            if(useTransaction) {
                
                em.getTransaction().commit();
            }
            return t;
        } catch (ApaException e) {
            if(useTransaction) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            if(useTransaction) {
                cleanup(em);
            }
        }
    }

    private JpaRecord saveRecord(JpaRecord t) {
        return saveRecord(t, null);
    }

    @Override
    public PTicket saveRecord(PTicket record) {
        return saveRecord(record.getType(), record);
    }

    @Override
    public PTicket saveRecord(String type, PTicket record) {
        if(type == null) {
            throw new ApaException("Cannot save a record without a type");
        }

        //if this existingTicket has an id
        if (record.getId() != null) {
            return updateTicketFromClientTicket(type, record, record.getId()).toClientTicket();
        } else {
            return createAndSaveTicketFromClientTicket(type, record).toClientTicket();
        }
    }

    @Override
    public PTicket patchRecord(Object idToPatch, String type, PTicket patchRecord) {
        
        EntityManager em = this.emf.createEntityManager();

        try {
            JpaRecord existingRecord = getTicket(type, idToPatch);
            if(existingRecord == null) {
                throw new ApaException("Record with id ["+idToPatch+"] was not found to patch");
            }
            if(patchRecord == null) {
                return existingRecord.toClientTicket();
            }

            em.getTransaction().begin();

            //delete properties on the patch
            for(Entry<String, List<String>> entry : patchRecord.getProps().entrySet()) {
                TicketProp prop = getTicketProp(entry.getKey(), type, idToPatch);
                if(prop != null) {
                    prop = em.merge(prop);
                    existingRecord.getTicketProps().remove(prop);
                    em.remove(prop);
                    existingRecord = em.merge(existingRecord);
                }
            }

            //Now patch the records
            List<TicketProp> propsToSave = buildProps(type, existingRecord, patchRecord, em);
            for (TicketProp prop : propsToSave) {

                //TODO: This should be done when we're building the prop
                enforceStrict(prop.getPropField(), prop.getValueAsString());
                prop = (TicketProp) em.merge(prop);
                existingRecord.addTicketProp(prop);
            }
            existingRecord = saveRecord(existingRecord, em);
            em.getTransaction().commit();
            return existingRecord.toClientTicket();
        } finally {
            cleanup(em);
        }
    }

    /*
     * updateTicketFromClientTicket assumes that PTicket has been sent with an ID.
     * updateTicketFromClientTicket will load a existingTicket with that ID.
     *
     * Any system props will be updated by the props on this PTicket.
     * No system props will be deleted
     * All regular props will be deleted
     * New props will be created from this pTicket
     *
     * basic algorithm:
     * - get all props on this existingTicket
     * - delete all that are not system props
     * - create and dave new props on this pTicket
     * - for all system props on pTicket
     * - if prop exists, update it
     * - else, save a new one
     *
     * It would be smarter to bulk delete all props on the existingTicket, then just create new props
     * But, JPA/Hibernate chokes on the query to delete all props, prob something to do with the
     * STI on TicketProp
     */
    public JpaRecord updateTicketFromClientTicket(String type, PTicket clientTicket, Object idToUpdate) throws InvalidPropException, InvalidValueException {
        
        EntityManager em = this.emf.createEntityManager();

        try {
            em.getTransaction().begin();
            JpaRecord existingTicket  = getTicket(em, type, idToUpdate);
            deletePropsFromRecord(existingTicket, clientTicket, em);
            List<TicketProp> propsToSave = buildProps(type, existingTicket, clientTicket, em);
            
            for (TicketProp prop : propsToSave) {

                //TODO: This should be done when we're building the prop
                enforceStrict(prop.getPropField(), prop.getValueAsString());
                prop = (TicketProp) em.merge(prop);
                existingTicket.addTicketProp(prop);
            }
            existingTicket = saveRecord(existingTicket, em);
            em.getTransaction().commit();
            return existingTicket;
        } catch (ApaException e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            cleanup(em);
        }
    }

    /*
     * createAndSaveTicketFromClientTicket assumes that PTicket has been sent WITHOUT an ID.
     * createAndSaveTicketFromClientTicket will create a new existingTicket using magic and wizardry
     */
    private JpaRecord createAndSaveTicketFromClientTicket(String type, PTicket clientTicket) throws InvalidPropException, InvalidValueException {

        JpaRecord ticket  = new JpaRecord();

        //for all props on this pTicket, create new props with apa
        Set<String> keys = clientTicket.getProps().keySet();
        for (String key : keys) {
            for(String val : clientTicket.getProps().get(key)) {
                buildPropOntoTicket(ticket, key, val);
            }
        }
        keys = clientTicket.getSystemProps().keySet();
        for (String key : keys) {
            for(String val : clientTicket.getSystemProps().get(key)) {
                buildPropOntoTicket(ticket, key, val);
            }
        }
        ticket.setType(type);
        ticket = saveRecord(ticket);
        return ticket;
    }

    private List<TicketProp> buildProps(String type, JpaRecord existingRecord, PTicket newRecord, EntityManager em) {
        List<TicketProp> propsToSave = new ArrayList<TicketProp>();
        Set<String> keys = newRecord.getProps().keySet();
        for (String key : keys) {
            for(String val : newRecord.getProps().get(key)) {
                propsToSave.add(buildNewProp(em, existingRecord, type, key, val));
            }
        }

        /*
         * Save all the new system props
         */
        keys = newRecord.getSystemProps().keySet();
        for (String key : keys) {
            String val = newRecord.getSystemProps().getFirst(key);
            propsToSave.add(buildNewProp(em, existingRecord, type, key, val));
        }

        return propsToSave;
    }

    /*
     * Delete anything that isn't a system prop
     * Also delete system props that exist on the incoming record
     * This method DOES NOT manage transactions
     */
    private void deletePropsFromRecord(JpaRecord existingRecord, PTicket newRecord, EntityManager em) {
        Iterator<TicketProp> iter = existingRecord.getTicketProps().iterator();
        List<TicketProp> propsToDelete = new ArrayList<TicketProp>();
        while(iter.hasNext()) {
            TicketProp prop = (TicketProp)iter.next();
            if(!prop.isSystemProp()) {
                propsToDelete.add(prop);
            } else {
                List<String> values = newRecord.getSystemProps().get(prop.getPropField().getName());
                if(values != null) {
                    propsToDelete.add(prop);
                }
            }
        }

        for(TicketProp prop : propsToDelete) {
            deleteTicketProp(prop, em);
        }
    }

    private TicketProp buildNewProp(EntityManager em, JpaRecord ticket, String type, String key, String val) {
        PropField propField = getPropField(key, em);
        validatePropField(propField, key, val);
        TicketProp ticketProp = propField.getValueType().newTicketProp();
        ticketProp.setPropField(propField);
        ticketProp.setTicket(ticket);
        ticketProp.setValue(val);
        return ticketProp;
    }

    private TicketProp buildExistingProp(JpaRecord ticket, String type, String key, String val) {

        TicketProp ticketProp = getTicketProp(key, type, ticket.getId());

        if (ticketProp == null) {
            PropField propField = getPropField(key);
            validatePropField(propField, key, val);

            ticketProp = propField.getValueType().newTicketProp();
            ticketProp.setPropField(propField);
            ticketProp.setTicket(ticket);
        }

        ticketProp.setValue(val);

        return ticketProp;
    }

    private JpaRecord buildPropOntoTicket(JpaRecord ticket, String key, String val) {
        logger.debug("Creating property: {}={}", key, val);
        PropField propField = getPropField(key);
        logger.debug("Found PropField: {}", propField);
        validatePropField(propField, key, val);
        TicketProp ticketProp = propField.getValueType().newTicketProp();
        ticketProp.setPropField(propField);
        ticketProp.setValue(val);
        ticketProp.setTicket(ticket);
        logger.debug("Creating TicketProp: [{}]", ticketProp.getClass().getName());
        logger.debug("{}={}", ticketProp.getPropField().getName(), ticketProp.getValueAsString());
        ticket.addTicketProp(ticketProp);
        return ticket;
    }

    /**
     * This method will throw ObjectNotFoundException if propField is null.
     *
     * @param propField the prop field to validate
     * @param key the name of the propField.  Used to validate that propField exists and is correct.
     * @param value the value that will be validated if propField is strict
     * @throws PropFieldNotFoundException
     */
    private void validatePropField(PropField propField, String key, String value) throws InvalidPropException {
        if (propField == null) {
            throw new InvalidPropException("Field with name [" + key + "] does not exist");
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
    public Boolean deleteRecord(String type, Object id) {
        logger.debug("Deleting ticket: " + id);
        if (id == null) {
            return false;
        }
        EntityManager em = this.emf.createEntityManager();
        try {
            Long longId = LongUserType.massageToLong(id);
            em.getTransaction().begin();
            JpaRecord t = em.find(JpaRecord.class, longId);
            logger.trace("Deleting ticket: " + t);
            em.remove(t);
            logger.trace("Deleted ticket: " + longId);
            em.getTransaction().commit();
            return true;
        } finally {
            cleanup(em);
        }
    }

    /**
     * Find tickets according to the AtheaSearch.
     * Type must be specified, but search constraints may be empty.  This method honors start, end, and limit modifiers.
     *
     * If one of the fields is ValueType.TEXT, this method will throw an ApaException
     *
     * @param athenaSearch a set of search constraints and search modifiers
     * @return Set of tickets whose Props match the athenaSearch
     */
    @Override
    public Set<PTicket> findTickets(AthenaSearch athenaSearch) {
        logger.debug("Searching for tickets:");
        logger.debug("{}", athenaSearch);

        checkValueTypes(athenaSearch);

        EntityManager em = this.emf.createEntityManager();
        Query query = null;
        Collection<JpaRecord> finishedTicketsList = null;
        Set<JpaRecord> finishedTicketsSet = null;
        Collection<JpaRecord> ticketsList = null;
        try {
            //if there are no modifiers, grab all records of (type)
            if (CollectionUtils.isEmpty(athenaSearch.getConstraints())) {
                logger.debug("No modifiers, getting all records of specified type");
                finishedTicketsSet = getRecordsByType(athenaSearch, em);
            } else {
                //else, search with the modifiers
                //TODO: This block runs independent searches for each constraint, then just smashes those lists together
                //Smarter way would be to run the first constraint, then search THAT list for the next constraint
                //Even smarter: be clever about which constraint we search for first.

                for (AthenaSearchConstraint apc : athenaSearch.getConstraints()) {
                    logger.debug("Searching on modifier: {}", apc);

                    if(apc.getOper().equals(Operator.MATCHES)) {
                        if(apc.getValue().equals(AthenaSearch.ANY_VALUE)) {
                            ticketsList = getRecordsWithFieldDefined(athenaSearch.getType(), apc, em);
                        } else {
                            throw new UnsupportedOperationException("Regex searching is not supported");
                        }
                    } else {
                        ticketsList = getRecordsForConstraint(athenaSearch.getType(), apc, em);
                    }

                    logger.debug("Found {} tickets", ticketsList.size());
                    if (finishedTicketsList == null) {
                        finishedTicketsList = ticketsList;
                    } else {
                        logger.debug("Smashing together ticket lists");
                        finishedTicketsList = CollectionUtils.intersection(finishedTicketsList, ticketsList);
                    }
                    logger.debug("{} tickets remain", finishedTicketsList.size());
                }
                if (finishedTicketsList == null) {
                    finishedTicketsList = new ArrayList<JpaRecord>();
                }
                Integer limit = athenaSearch.getLimit();
                Integer start = athenaSearch.getStart();

                finishedTicketsSet = new HashSet<JpaRecord>();
                finishedTicketsSet.addAll(finishedTicketsList);
                finishedTicketsSet = enforceStartAndLimit(finishedTicketsSet, start, limit);
            }

            logger.debug("Returning {} tickets", finishedTicketsSet.size());
            return convert(finishedTicketsSet);
        } catch (ApaException ex) {
            
            
            throw ex;
        } finally {
            cleanup(em);
        }
    }

    private void checkValueTypes(AthenaSearch athenaSearch) {
        for (AthenaSearchConstraint apc : athenaSearch.getConstraints()) {
            String fieldName = apc.getParameter();

            //TODO: This is done twice (see checkValueTypes), a bit of a waste of time
            PropField pf = getPropField(fieldName);
            if (pf != null) {
                ValueType vt = pf.getValueType();
                if(vt.equals(ValueType.TEXT)) {
                    throw new ApaException("You cannot search on TEXT fields");
                }
            } else {
                throw new InvalidFieldException("No Property Field called " + fieldName + " exists.");
            }
        }
    }

    private Set<PTicket> convert(Set<JpaRecord> jpaRecords) {
        Set<PTicket> out = new HashSet<PTicket>();
        for(JpaRecord r : jpaRecords) {
            out.add(r.toClientTicket());
        }
        return out;
    }

    private Set<JpaRecord> enforceStartAndLimit(Set<JpaRecord> ticketSet, Integer start, Integer limit) {

        Integer from = 0;
        Integer to = 0;

        from = (start == null) ? 0 : start;

        //I'm not sure this statement could be any more unclear.  It looks like Haskell
        //If limit isn't set, or it is set higher than the number of tickets + start_offset, then set the "to" to the number of tickets we have
        //otherwise, set "to" to limit+start
        to = (limit == null || limit + from > ticketSet.size()) ? ticketSet.size() : limit + from;

        logger.debug("Enforcing limit:");
        logger.debug("FROM: {}", from);
        logger.debug("TO:   {}", to);

        //short circuit all of this if we can.  If they've asked for more tickets than we found, punch out
        if(from == 0 && to >= ticketSet.size()) {
            return ticketSet;
        }

        //if the start is greater than the number of tickets we've found, return nothing
        if(from > to) {
            return new HashSet<JpaRecord>();
        }

        JpaRecord[] ticketArray = new JpaRecord[ticketSet.size()];
        ticketArray = ticketSet.toArray(ticketArray);
        JpaRecord[] outTickets = Arrays.copyOfRange(ticketArray, from, to);
        ticketSet = new HashSet(Arrays.asList(outTickets));
        return ticketSet;
    }

    private Set<JpaRecord> getRecordsByType(AthenaSearch athenaSearch, EntityManager em) {
        Set<JpaRecord> finishedTicketsSet = null;

        Query query = em.createQuery("from JpaRecord as ticket where type=:ticketType").setParameter("ticketType", athenaSearch.getType());

        if (athenaSearch.getLimit() != null) {
            query.setMaxResults(athenaSearch.getLimit());
        }

        if (athenaSearch.getStart() != null) {
            query.setFirstResult(athenaSearch.getStart());
        }

        finishedTicketsSet = new HashSet<JpaRecord>(query.getResultList());
        return finishedTicketsSet;
    }

    private Collection<JpaRecord> getRecordsWithFieldDefined(String type, AthenaSearchConstraint apc, EntityManager em) {
        Set<JpaRecord> tickets = new HashSet<JpaRecord>();
        Collection<TicketProp> props = getTicketPropsForType(type, apc.getParameter());
        for(TicketProp prop : props) {
            tickets.add(prop.getTicket());
        }
        return tickets;
    }

    private Collection<TicketProp> getTicketPropsForType(String type, String fieldName) {
        EntityManager em = this.emf.createEntityManager();

        try {
            //TODO: Would this be faster to first select propFields with fieldName, then use that id to
            //search ticketProp?
            Query query = em.createQuery("FROM TicketProp ticketProp WHERE ticketProp.propField.name=:fieldName AND ticketProp.ticket.type=:type");
            query.setParameter("type", type);
            query.setParameter("fieldName", fieldName);

            //TODO: There must be a better way of getting asingle result in JPA.
            //Using exceptions as flow control is kinda lame
            try {
                List ticketProp = query.getResultList();
                return ticketProp;
            } catch (javax.persistence.NoResultException nre) {
                return null;
            }
        } finally {
            cleanup(em);
        }
    }

    private Collection<JpaRecord> getRecordsForConstraint(String type, AthenaSearchConstraint apc, EntityManager em) {

        PropField pf = null;
        ValueType vt = null;
        String fieldName = null;
        List<TicketProp> props = null;
        JpaRecord tempTicket = null;
        Collection<JpaRecord> ticketsList = null;

        fieldName = apc.getParameter();

        //TODO: This is done twice (see checkValueTypes), a bit of a waste of time
        pf = getPropField(fieldName);
        if (pf != null) {
            vt = pf.getValueType();
        } else {
            throw new InvalidFieldException("No Property Field called " + fieldName + " exists.");
        }

        logger.debug("{}", apc);

        TicketProp prop = vt.newTicketProp();
        prop.setPropField(pf);
        Query query = buildQuery(type, apc, pf, vt, em);
        ticketsList = new ArrayList<JpaRecord>();
        
        if(query != null) {
            props = query.getResultList();
            for (TicketProp tp : props) {
                tempTicket = tp.getTicket();
                ticketsList.add(tempTicket);
            }
        }

        return ticketsList;
    }

    private Query buildQuery(String type,
                             AthenaSearchConstraint apc,
                             PropField pf,
                             ValueType vt,
                             EntityManager em) {

        Query query = null;
        String queryString = null;
        String singleValue = null;
        Iterator<String> it = null;
        Set<Object> valuesAsObjects = null;
        
        TicketProp prop = vt.newTicketProp();
        prop.setPropField(pf);
        Set<String> values = apc.getValueSet();

        if (values.size() > 1) {
            it = values.iterator();
            valuesAsObjects = new HashSet<Object>();
            while (it.hasNext()) {
                singleValue = it.next();
                try {
                    prop.setValue(singleValue);
                    valuesAsObjects.add(prop.getValue());
                } catch (Exception ex) {
                    //TODO: This is bad. We should blow up here
                    
                    
                    
                }
            }
            queryString = "FROM " + prop.getClass().getName()
                    + " ticketProp WHERE ticketProp.propField.name=:fieldName AND ticketProp.value "
                    + apc.getOper().getOperatorString();

            if (type != null) {
                queryString += " AND ticketProp.ticket.type=:ticketType ";
            }

            query = em.createQuery(queryString);
            query.setParameter("value", valuesAsObjects);
            query.setParameter("fieldName", apc.getParameter());

            if (type != null) {
                query.setParameter("ticketType", type);
            }


        } else {
            try {
                prop.setValue(values.iterator().next());
                queryString = "FROM " + prop.getClass().getName()
                        + " ticketProp WHERE ticketProp.propField.name=:fieldName AND ticketProp.value "
                        + apc.getOper().getOperatorString();

                if (type != null) {
                    queryString += " AND ticketProp.ticket.type=:ticketType ";
                }

                query = em.createQuery(queryString);
                query.setParameter("value", prop.getValue());
                query.setParameter("fieldName", apc.getParameter());

                if (type != null) {
                    query.setParameter("ticketType", type);
                }
            } catch (InvalidValueException e) {
                //this is cool, continue
            }
        }

        return query;
    }

    private List<TicketProp> saveTicketProps(List<TicketProp> props) {
        List<TicketProp> outProps = new ArrayList<TicketProp>();
        EntityManager em = this.emf.createEntityManager();
        try {
            em.getTransaction().begin();

            for (TicketProp prop : props) {
                enforceStrict(prop.getPropField(), prop.getValueAsString());
                enforceCorrectValueType(prop.getPropField(), prop, em);
                prop.setId(LongUserType.massageToLong(prop.getId()));
                prop = (TicketProp) em.merge(prop);
            }
            em.getTransaction().commit();
            return outProps;
        } catch (ApaException e) {
            
            em.getTransaction().rollback();
            throw e;
        } finally {
            cleanup(em);
        }

    }

    private TicketProp saveTicketProp(TicketProp prop) throws InvalidValueException {
        EntityManager em = this.emf.createEntityManager();
        try {
            em.getTransaction().begin();
            enforceStrict(prop.getPropField(), prop.getValueAsString());
            enforceCorrectValueType(prop.getPropField(), prop, em);
            prop.setId(LongUserType.massageToLong(prop.getId()));
            prop = (TicketProp) em.merge(prop);
            em.getTransaction().commit();
            return prop;
        } finally {
            cleanup(em);
        }
    }

    /**
     * This method will not hydrate TicketProp.geTTicket because no type information is available
     * @param id
     * @return the ticketProp, null if not found
     */
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

    /**
     * Return the first ticketprop where name=fieldName and ticket_id = ticketId.
     *
     * Callers of this method are assuming that there is only one prop that meets the above conditions
     *
     * If ticketId is null, this method will return null.  If no existingTicket prop
     * is found for the given conditions, this method will return null;
     *
     * This method WILL hydrate TicketProp.getTicket().  
     *
     * @param fieldName
     * @param type
     * @param ticketId
     * @return the ticketprop, null if not found.
     */
    @Override
    public TicketProp getTicketProp(String fieldName, String type, Object ticketId) {

        //This is to get around a bug in Derby that prevents us selecting on a null
        if(ticketId == null) {
            return null;
        }

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
                //ticketProp.setTicket(getTicket(type, ticketId));
                return ticketProp;
            } catch (javax.persistence.NoResultException nre) {
                return null;
            }
        } finally {
            cleanup(em);
        }
    }

    @Override
    public List getTicketProps(String fieldName, String type, Object ticketId) {
        EntityManager em = this.emf.createEntityManager();

        //This is to get around a bug in Derby that prevents us selecting on a null
        if(ticketId == null) {
            return null;
        }
        
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
                List ticketProp = query.getResultList();
                return ticketProp;
            } catch (javax.persistence.NoResultException nre) {
                return null;
            }
        } finally {
            cleanup(em);
        }
    }

    @Override
    public List getTicketProps(String fieldName) {
        EntityManager em = this.emf.createEntityManager();

        try {
            //TODO: Would this be faster to first select propFields with fieldName, then use that id to
            //search ticketProp?
            Query query = em.createQuery("FROM TicketProp ticketProp WHERE ticketProp.propField.name=:fieldName");
            query.setParameter("fieldName", fieldName);

            //TODO: There must be a better way of getting asingle result in JPA.
            //Using exceptions as flow control is kinda lame
            try {
                List ticketProp = query.getResultList();
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
            return pf;
        } finally {
            cleanup(em);
        }
    }

    public PropField getPropField(String name, EntityManager em) {
        boolean closeEm = false;
        if(em == null) {
            closeEm = true;
            em = this.emf.createEntityManager();
        }
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
            if(closeEm) {
                cleanup(em);
            }
        }
    }
    
    @Override
    public PropField getPropField(String name) {
        return getPropField(name, null);
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

        if (propField.getStrict() && propField.getValueType().equals(ValueType.TEXT)) {
            throw new ApaException("Text fields cannot be marked as strict");
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

    private void checkForDuplicatePropValue(PropField propField) throws ApaException {
        if (propField.getPropValues() == null) {
            return;
        }

        Set<PropValue> duplicates = new TreeSet<PropValue>(new PropValue.PropValueComparator());
        for (PropValue value : propField.getPropValues()) {
            if (!duplicates.add(value)) {
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
        deleteTicketProp(prop, null);

    }
    
    public void deleteTicketProp(TicketProp prop, EntityManager em) {
        boolean useTransaction = (em == null);
        if(em == null) {
            em = this.emf.createEntityManager();
        }
        try {
            if(useTransaction) {
                em.getTransaction().begin();
            }
            prop = em.merge(prop);

            if (prop == null) {
                throw new ApaException("Cannot delete prop.  Prop was not found.");
            }

            JpaRecord t = prop.getTicket();

            if (t == null) {
                throw new ApaException("Cannot delete prop.  This prop has not been assigned to a ticket.");
            }
            t.getTicketProps().remove(prop);
            em.remove(prop);
            t = em.merge(t);
            if (useTransaction) {
                em.getTransaction().commit();
            }
        } finally {
            if(useTransaction) {
                cleanup(em);
            }
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
    public Collection<PropField> getPropFields() {
        EntityManager em = this.emf.createEntityManager();
        try {
            Query query = em.createQuery("FROM PropField pf");
            return query.getResultList();
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
    public void deletePropValue(Object propFieldId, Object propValueId) {
        EntityManager em = this.emf.createEntityManager();
        try {
            Long longId = LongUserType.massageToLong(propValueId);
            PropValue pv = em.find(PropValue.class, longId);
            if (pv != null) {
                PropField propField = getPropField(pv.getPropField().getId());
                propField.getPropValues().remove(pv);
                em.getTransaction().begin();
                em.remove(pv);
                propField = em.merge(propField);
                em.getTransaction().commit();
            }
        } finally {
            cleanup(em);
        }
    }

    @Override
    public void deletePropValue(PropValue propValue) {
        if (propValue != null) {
            deletePropValue(propValue.getPropField(), propValue.getId());
        }
    }
    
    private void enforceCorrectValueType(PropField propField, TicketProp prop, EntityManager em) throws InvalidValueException {
        Long longId = LongUserType.massageToLong(propField.getId());
        propField = em.find(PropField.class, longId);
        if (!propField.getValueType().newTicketProp().getClass().getName().equals(prop.getClass().getName())) {
            String err = "Value [" + prop.getValueAsString() + "] is not a valid value for the field [" + propField.getName() + "].  ";
            err += "Field is of type [" + propField.getValueType().name() + "].";
            throw new InvalidValueException(err);
        }
    }

    private void cleanup(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }
}
