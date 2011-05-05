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

    public PTicket getTicket(String type, Object id) {
        return apa.getRecord(type, id);
    }

    public void deleteTicket(PTicket t) {
        apa.deleteRecord(t);
    }

    public void deleteTicket(String type, Object id) {
        apa.deleteRecord(type, id);
    }

    public void deletePropertyFromTicket(String type, String propName, Object ticketId)
            throws ObjectNotFoundException {
        TicketProp prop = apa.getTicketProp(propName, type, ticketId);

        if (prop == null) {
            //no prop found, try and figure out why so we can return a sensible 404
            PTicket t = apa.getRecord(type, ticketId);
            if (t == null) {
                throw new ObjectNotFoundException("JpaRecord with id [" + ticketId + "] was not found");
            } else {
                throw new ObjectNotFoundException("Property with name [" + propName + "] was not found on ticket with id [" + ticketId + "]");
            }
        }

        apa.deleteTicketProp(prop);
    }
    public Set<PTicket> findTicketsByRelationship(String parentType, Object id, String childType) {

        PTicket ticket  = apa.getRecord(parentType, id);
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
    public Set<PTicket> findTickets(String type, MultivaluedMap<String, String> queryParams) {

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
                    int start = 0;

                    if(operatorPrefixedValue.length() < 2) { 
                        operator = Operator.EQUALS;
                        value = operatorPrefixedValue;
                    } else {
                        //If the operator isn't found, this defaults to equals
                        operator = Operator.fromType(operatorPrefixedValue.substring(0, 2));
                        start = 2;

                        if(operator == null) {
                            operator = Operator.EQUALS;
                            start = 0;
                        }
                        value = operatorPrefixedValue.substring(start, operatorPrefixedValue.length());
                    }
                    
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

    public List<PTicket> createRecords(String type, List<PTicket> records) {
        List<PTicket> outRecords = new ArrayList<PTicket>();
        try {
            for(PTicket record : records) {
                outRecords.add(apa.saveRecord(type, record));
            }
        } finally {
            for(PTicket t : outRecords) {
                apa.deleteRecord(t);
            }            
        }
            
        return outRecords;
    }

    public PTicket createRecord(String type, PTicket record) {
        return apa.saveRecord(type, record);
    }

    public PTicket updateRecord(String type, PTicket record) {

        PTicket ticket  = apa.getRecord(type, record.getId());

        if (ticket == null) {
            throw new NotFoundException();
        }

        return apa.saveRecord(type, record);
    }

    public PTicket updateRecord(String type, PTicket record, String idToUpdate) {

        PTicket ticket  = apa.getRecord(type, idToUpdate);

        if (idToUpdate == null || ticket == null) {
            throw new NotFoundException();
        }

        if (!IdAdapter.isEqual(ticket.getId(), record.getId())) {
            throw new AthenaException("Requested update to [" + idToUpdate + "] but sent record with id [" + record.getId() + "]");
        }

        return apa.saveRecord(type, record);
    }

    public ApaAdapter getApa() {
        return apa;
    }

    public void setApa(ApaAdapter apa) {
        this.apa = apa;
    }
}
