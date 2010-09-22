/*

ATHENA Project: Management Tools for the Cultural Sector
Copyright (C) 2010, Fractured Atlas

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/

*/
package org.fracturedatlas.athena.web.manager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.web.exception.ObjectNotFoundException;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.PropValue;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.TicketProp;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class TicketManager {

    @Autowired
    ApaAdapter apa;
    
    Logger logger = Logger.getLogger(this.getClass().getName());

    public Ticket getTicket(Object id) {
        return apa.getTicket(id);
    }

    public void deleteTicket(Ticket t) {
        apa.deleteTicket(t);
    } 

    public void deleteTicket(Object id) {
        apa.deleteTicket(id);
    }

    public void deletePropertyFromTicket(String propName, Object ticketId)
            throws ObjectNotFoundException{
        TicketProp prop = apa.getTicketProp(propName, ticketId);

        if(prop == null) {
            //no prop found, try and figure out why so we can return a sensible 404
            Ticket t = apa.getTicket(ticketId);
            if(t == null) {
                throw new ObjectNotFoundException("Ticket with id [" + ticketId +"] was not found");
            } else {
                throw new ObjectNotFoundException("Property with name ["+ propName +"] was not found on ticket with id [" + ticketId +"]");
            }
        }

        apa.deleteTicketProp(prop);
    }

    /**
     * Converts the MultivaluedMap to a HashMap and only allows searching on the first
     * value for a given paramater
     *
     * Example.  For this string: ?foo=bar&foo=baz
     *
     * Only foo=bar would be searched on.
     *
     * @param queryParams
     * @return
     */
    public Set<Ticket> findTickets(MultivaluedMap<String, String> queryParams) {
        HashMap<String, String> searchParams = new HashMap<String, String>();

        for(String kee : queryParams.keySet()) {
            List<String> valList = queryParams.get(kee);
            searchParams.put(kee, valList.get(0));
        }

        return apa.findTickets(searchParams);
    }

    public Ticket saveTicketFromClientRequest(PTicket pTicket) throws Exception {
        //if this ticket has an id
        if(pTicket.getId() != null) {
            return updateTicketFromClientTicket(pTicket);
        } else {
            return createAndSaveTicketFromClientTicket(pTicket);
        }
    }

    /*
     * updateTicketFromClientTicket assumes that PTicket has been sent with an ID.
     * updateTicketFromClientTicket will load a ticket with that ID.
     */
    private Ticket updateTicketFromClientTicket(PTicket clientTicket) throws Exception {
        Ticket ticket = apa.getTicket(clientTicket.getId());

        /*
         * If the client sent a ticket with an ID but we didn't fidn the ticket, toss back
         * a ticket not found exception
         */
        if(ticket == null) {
            throw new ObjectNotFoundException("Cannot update ticket with id [" + clientTicket.getId() + "].  The ticket was not found.");
        }

        /*
         * for all props on this pTicket
         * if apa has a prop for it, update it
         * otherwise, create a new one
         */
        Map<String, String> propMap = clientTicket.getProps();
        Set<String> keys = propMap.keySet();
        List<TicketProp> propsToSave = new ArrayList<TicketProp>();
        for(String key : keys) {
            String val = propMap.get(key);

            TicketProp ticketProp = apa.getTicketProp(key, ticket.getId());

            if(ticketProp == null) {
                PropField propField = apa.getPropField(key);
                validatePropField(propField, key, val);

                ticketProp = propField.getValueType().newTicketProp();
                ticketProp.setPropField(propField);
                ticketProp.setTicket(ticket);
            }

            try{
                ticketProp.setValue(val);
            } catch (ParseException re) {
                throw new InvalidValueException(buildExceptionMessage(val, ticketProp.getPropField()));
            } catch (RuntimeException re) {
                throw new InvalidValueException(buildExceptionMessage(val, ticketProp.getPropField()));
            }

            //saving these outside of this loop ensures that all propFields exist before
            //we go saving values.  Sort of a hack transactionality.
            propsToSave.add(ticketProp);
        }

        for(TicketProp ticketProp : propsToSave) {
            apa.saveTicketProp(ticketProp);
        }

        ticket = apa.getTicket(clientTicket.getId());
        ticket.setName(clientTicket.getName());
        ticket = apa.saveTicket(ticket);
        return ticket;
    }

    private String buildExceptionMessage(String val, PropField propField) {
        String err = "Value [" + val + "] is not a valid value for the field [" + propField.getName() + "].  ";
        err += "Field is of type [" + propField.getValueType().name() + "].";
        return err;
    }

    /*
     * createAndSaveTicketFromClientTicket assumes that PTicket has been sent WITHOUT an ID.
     * createAndSaveTicketFromClientTicket will create a new ticket using magic and wizardry
     */
    private Ticket createAndSaveTicketFromClientTicket(PTicket clientTicket) throws Exception {

        Ticket ticket = new Ticket();
        
        //for all props on this pTicket, create new props with apa
        Map<String, String> propMap = clientTicket.getProps();
        Set<String> keys = propMap.keySet();
        for(String key : keys) {
                    
            String val = propMap.get(key);
            PropField propField = apa.getPropField(key);
            validatePropField(propField, key, val);
            TicketProp ticketProp = propField.getValueType().newTicketProp();

            try{
                ticketProp.setValue(val);
            } catch (ParseException re) {
                throw new InvalidValueException(buildExceptionMessage(val, propField));
            } catch (RuntimeException re) {
                throw new InvalidValueException(buildExceptionMessage(val, propField));
            }
            
            ticketProp.setPropField(propField);
            ticketProp.setTicket(ticket);
            ticket.addTicketProp(ticketProp);
        }
        
        ticket.setName(clientTicket.getName());        
        ticket = apa.saveTicket(ticket);
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
    private void validatePropField(PropField propField, String key, String value) throws ObjectNotFoundException {
        if(propField == null) {
            throw new ObjectNotFoundException("Field with name [" + key + "] does not exist");
        }
    }

    public ApaAdapter getApa() {
        return apa;
    }

    public void setApa(ApaAdapter apa) {
        this.apa = apa;
    }
}
