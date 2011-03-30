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

import com.sun.jersey.api.NotFoundException;
import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang.StringUtils;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.web.exception.ObjectNotFoundException;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;
import org.fracturedatlas.athena.apa.impl.jpa.TicketProp;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.web.exception.AthenaException;
import org.springframework.beans.factory.annotation.Autowired;

public class RecordManager {

    @Autowired
    ApaAdapter apa;

    public JpaRecord getTicket(String type, Object id) {
        return apa.getTicket(type, id);
    }

    public void deleteTicket(JpaRecord t) {
        apa.deleteTicket(t);
    }

    public void deleteTicket(String type, Object id) {
        apa.deleteTicket(type, id);
    }

    public void deletePropertyFromTicket(String type, String propName, Object ticketId)
            throws ObjectNotFoundException {
        TicketProp prop = apa.getTicketProp(propName, type, ticketId);

        if (prop == null) {
            //no prop found, try and figure out why so we can return a sensible 404
            JpaRecord t = apa.getTicket(type, ticketId);
            if (t == null) {
                throw new ObjectNotFoundException("JpaRecord with id [" + ticketId + "] was not found");
            } else {
                throw new ObjectNotFoundException("Property with name [" + propName + "] was not found on ticket with id [" + ticketId + "]");
            }
        }

        apa.deleteTicketProp(prop);
    }
    public Set<JpaRecord> findTicketsByRelationship(String parentType, Object id, String childType) {

        JpaRecord ticket  = apa.getTicket(parentType, id);
        if(ticket == null) {
            throw new NotFoundException(StringUtils.capitalize(parentType) + " witn id [" + id + "] was not found");
        }

        //TODO: move this somewhere sensible
        String parentField = parentType + "Id";

        AthenaSearch athenaSearch = new AthenaSearch
                .Builder(new AthenaSearchConstraint(parentField, Operator.EQUALS, IdAdapter.toString(id)))
                .type(childType)
                .build();

        return apa.findTickets(athenaSearch);
    }

    /**
     * @param queryParams
     * @return
     */
    public Set<JpaRecord> findTickets(String type, MultivaluedMap<String, String> queryParams) {

        List<String> values = null;
        Operator operator;
        String value;
        Set<String> valueSet = null;
        AthenaSearch apaSearch = new AthenaSearch();
        apaSearch.setType(type);
        for (String fieldName : queryParams.keySet()) {
            values = queryParams.get(fieldName);
            for (String operatorPrefixedValue : values) {
                if (fieldName.startsWith("_")) {
                    apaSearch.setSearchModifier(fieldName, operatorPrefixedValue);
                } else {

                    //If the operator isn't found, this defaults to equals
                    operator = Operator.fromType(operatorPrefixedValue.substring(0, 2));
                    int start = 2;
                    if(operator == null) {
                        operator = Operator.EQUALS;
                        start = 0;
                    }
                    value = operatorPrefixedValue.substring(start, operatorPrefixedValue.length());
                    valueSet = parseValues(value);
                    apaSearch.addConstraint(fieldName, operator, valueSet);
                }
            }
        }

        return apa.findTickets(apaSearch);
    }

    static Set<String> parseValues(String valueString) {
        HashSet<String> values = new HashSet<String>();
        valueString = StringUtils.trimToEmpty(valueString);
        valueString = StringUtils.strip(valueString, "()");
        valueString = StringUtils.trimToEmpty(valueString);
        CharacterIterator it = new StringCharacterIterator(valueString);
        boolean inString = false;
        int begin = 0;
        int end = 0;
        int numValues = 0;
        StringBuilder sb = new StringBuilder();
        // Iterate over the characters in the forward direction
        for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
            if (ch == '\"') {
                inString = true;
                ch = it.next();
                sb = new StringBuilder();
                for (; ch != CharacterIterator.DONE; ch = it.next()) {
                    if (ch == '\\') {
                        // skip any " in a string
                        sb.append(ch);
                        ch = it.next();
                    } else if (ch == '\"') {
                        break;
                    }
                    sb.append(ch);
                }
                inString = false;
                values.add(StringUtils.trimToEmpty(sb.toString()));
            } else if (ch == ',') {
                // new value
            } else if (" \t\n\r".indexOf(ch) > -1) {
                //skip whitespace
            } else {
                // not a comma, whitespace or a string start
                sb = new StringBuilder();
                for (; ch != CharacterIterator.DONE; ch = it.next()) {
                    if (ch == ',') {
                        break;
                    }
                    sb.append(ch);
                }
                inString = false;
                values.add(StringUtils.trimToEmpty(sb.toString()));

            }
        }

        return values;
    }

    /*
     * TODO: The fact that this throws Exception (and not something more specific)
     * is a crime against humanity
     */
    public JpaRecord saveTicketFromClientRequest(String type, PTicket pTicket) throws Exception {
        //if this ticket has an id
        if (pTicket.getId() != null) {
            return updateTicketFromClientTicket(type, pTicket, pTicket.getId());
        } else {
            return createAndSaveTicketFromClientTicket(type, pTicket);
        }
    }

    /*
     * updateTicketFromClientTicket assumes that PTicket has been sent with an ID.
     * updateTicketFromClientTicket will load a ticket with that ID.
     */
    public JpaRecord updateTicketFromClientTicket(String type, PTicket clientTicket, Object idToUpdate) throws Exception {
        JpaRecord ticket  = apa.getTicket(type, idToUpdate);

        /*
         * If the client ID on the url but we didn't find the ticket, toss back
         * a ticket not found exception
         */
        if (ticket == null) {
            throw new NotFoundException();
        }

        if (!IdAdapter.isEqual(ticket.getId(), clientTicket.getId())) {
            throw new AthenaException("Requested update to [" + idToUpdate + "] but sent record with id [" + clientTicket.getId() + "]");
        }

        /*
         * for all props on this pTicket
         * if apa has a prop for it, update it
         * otherwise, create a new one
         */
        Map<String, String> propMap = clientTicket.getProps();
        Set<String> keys = propMap.keySet();
        List<TicketProp> propsToSave = new ArrayList<TicketProp>();
        for (String key : keys) {
            String val = propMap.get(key);

            TicketProp ticketProp = apa.getTicketProp(key, type, ticket.getId());

            if (ticketProp == null) {
                PropField propField = apa.getPropField(key);
                validatePropField(propField, key, val);

                ticketProp = propField.getValueType().newTicketProp();
                ticketProp.setPropField(propField);
                ticketProp.setTicket(ticket);
            }

            try {
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

        for (TicketProp ticketProp : propsToSave) {
            apa.saveTicketProp(ticketProp);
        }

        ticket = apa.getTicket(type, clientTicket.getId());
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
    private JpaRecord createAndSaveTicketFromClientTicket(String type, PTicket clientTicket) throws Exception {

        JpaRecord ticket  = new JpaRecord();

        //for all props on this pTicket, create new props with apa
        Map<String, String> propMap = clientTicket.getProps();
        Set<String> keys = propMap.keySet();
        for (String key : keys) {

            String val = propMap.get(key);
            PropField propField = apa.getPropField(key);
            validatePropField(propField, key, val);
            TicketProp ticketProp = propField.getValueType().newTicketProp();

            try {
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
        ticket.setType(type);
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
        if (propField == null) {
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
