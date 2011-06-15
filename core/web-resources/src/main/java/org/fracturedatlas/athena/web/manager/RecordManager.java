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
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang.StringUtils;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.apa.exception.ApaException;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.web.exception.ObjectNotFoundException;
import org.fracturedatlas.athena.apa.impl.jpa.TicketProp;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.web.exception.AthenaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;

public class RecordManager {

    @Autowired
    ApaAdapter apa;
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    @javax.ws.rs.core.Context
    ApplicationContext applicationContext;

    @Autowired
    SecurityContextHolderStrategy contextHolderStrategy;

    public static final String ID_DELIMITER = ",";

    /**
     * Checks to see if a spring bean is registered at "{id}SubCollection"
     * If so, relevant info is passed to it then execute is called
     * 
     * If not, lookup for a record with id = id
     * 
     * @param type
     * @param id
     * @return 
     */
    public Object getRecords(String type, 
                             String idOrSubCollectionName, 
                             Map<String, List<String>> queryParams) {
        
        //load the plugin.  If the plugin is found, let it do its thing.
        AthenaSubCollection plugin = null;
        try{
             plugin = (AthenaSubCollection)applicationContext.getBean(idOrSubCollectionName + "SubCollection");
        } catch (NoSuchBeanDefinitionException noBean) {
            //it's okay
        }
        if(plugin != null) {
            String username = getCurrentUsername();
            return plugin.execute(type, idOrSubCollectionName, queryParams, username);
        } else {        
            PTicket ticket = apa.getRecord(type, idOrSubCollectionName);

            if(ticket == null) {
                type = StringUtils.capitalize(type);
                throw new NotFoundException(type + " with id [" + idOrSubCollectionName + "] was not found");
            } else {
                return ticket;
            }
        }
    }

    public void deleteRecord(String type, Object id) {
        PTicket ticket = apa.getRecord(type, id);
        if (ticket == null) {
            throw new NotFoundException(type + " with id [" + id + "] was not found");
        } else {
            apa.deleteRecord(ticket);
        }
    }

    public void deletePropertyFromRecord(String type, String propName, Object ticketId)
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
    
    public Collection<PTicket> findSubResources(String parentType,
                                                 Object id,
                                                 String childType,
                                                 Map<String, List<String>> queryParams) {

        //Check to see if the parent record exists
        PTicket ticket  = apa.getRecord(parentType, id);
        if(ticket == null) {
            throw new NotFoundException(StringUtils.capitalize(parentType) + " witn id [" + id + "] was not found");
        }

        //load the plugin.  If the plugin is found, let it do its thing.
        AthenaSubResource plugin = null;
        try{
             plugin = (AthenaSubResource)applicationContext.getBean(childType + "SubResource");
        } catch (NoSuchBeanDefinitionException noBean) {
            //it's okay
        }
        if(plugin != null) {
            String username = getCurrentUsername();
            return plugin.execute(parentType, id, childType, queryParams, username);
        } else {
            //If no plugin was found, look for sub-resources in apa
            //TODO: move this somewhere sensible
            String parentField = parentType + "Id";

            AthenaSearch athenaSearch = new AthenaSearch
                    .Builder(new AthenaSearchConstraint(parentField, Operator.EQUALS, IdAdapter.toString(id)))
                    .type(childType)
                    .build();

            return apa.findTickets(athenaSearch);
        }
    }
    
    private String getCurrentUsername() {
        Authentication authentication = contextHolderStrategy.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() != null
                                  && User.class.isAssignableFrom(authentication.getPrincipal().getClass()) ) {
            User user = (User) authentication.getPrincipal();
            return user.getUsername();
        } else {
            return null;
        }
    }

    public Set<PTicket> findRecords(String type, MultivaluedMap<String, String> queryParams) {

        List<String> values = null;
        Operator operator;
        String value;
        Set<String> valueSet = null;
        AthenaSearch apaSearch = new AthenaSearch();
        apaSearch.setType(type);
        for (String fieldName : queryParams.keySet()) {
            values = queryParams.get(fieldName);

            if(values == null || values.size() == 0) {
                throw new AthenaException("Found no values for search parameter ["+fieldName+"]");
            }

            for (String operatorPrefixedValue : values) {
                if(StringUtils.isBlank(operatorPrefixedValue)) {
                    throw new AthenaException("Found no values for search parameter ["+fieldName+"]");
                }
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
                    if(StringUtils.isBlank(value)) {
                        throw new AthenaException("Found no values for search parameter ["+fieldName+"]");
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

    /**
     * TODO: This method saves the records, then if one save fails it rolls back and deletes any records that it has saved to this point.
     * This is an edge case where it saves a record, some other client modifies the record, then this thread deletes that record in a rollback.
     * @param type
     * @param records
     * @return
     */
    public List<PTicket> createRecords(String type, List<PTicket> records) {
        List<PTicket> outRecords = new ArrayList<PTicket>();
        try {
            for(PTicket record : records) {
                outRecords.add(apa.saveRecord(type, record));
            }
        } catch (RuntimeException e) {
            logger.error("Exception while saving records [{}]", e);
            logger.error("Rolling back");
            for(PTicket t : outRecords) {
                try{
                    apa.deleteRecord(t);
                } catch (Exception exception) {
                    logger.error("Could not rollback record [{}]", t);
                    logger.error("[{}]", e.getMessage());
                }
            }
            throw e;
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

    public List<PTicket> updateRecords(String type, List<String> idList, PTicket patch) throws ObjectNotFoundException {
        List<PTicket> outRecords = new ArrayList<PTicket>();
        patch.setId(null);
        logger.debug("Applying [{}] to [{}]", patch, idList);
        for(String id : idList) {
            logger.debug("Applying patch to [{}]", id);

            try {
                outRecords.add(apa.patchRecord(id, type, patch));
            } catch (ApaException ae) {
                throw new ObjectNotFoundException(ae.getMessage());
            }
        }

        return outRecords;
    }

    public PTicket updateRecord(String type, PTicket record, String idToUpdate) {

        PTicket ticket  = apa.getRecord(type, idToUpdate);

        if (idToUpdate == null || ticket == null) {
            throw new NotFoundException();
        }

        logger.debug("Updating record [{}]", ticket);
        logger.debug("With record record [{}]", record);


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

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public SecurityContextHolderStrategy getContextHolderStrategy() {
        return contextHolderStrategy;
    }

    public void setContextHolderStrategy(SecurityContextHolderStrategy contextHolderStrategy) {
        this.contextHolderStrategy = contextHolderStrategy;
    }
}
