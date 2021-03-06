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
import org.fracturedatlas.athena.callbacks.CallbackManager;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.exception.AthenaException;
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
    
    @Autowired
    CallbackManager callbackManager;

    public static final String ID_DELIMITER = ",";

    /**
     * Checks to see if a spring bean is registered at "{idOrSubCollectionName}SubCollection"
     * If so, relevant info is passed to it then get is called
     * 
     * If not, lookup for a record with id = id.  If nothing is found, this method will throw a NotFoundEsxception
     *
     */
    public Object getRecords(String type, 
                             String idOrSubCollectionName, 
                             MultivaluedMap<String, String> queryParams) {
        
        //load the plugin.  If the plugin is found, let it do its thing.
        AthenaSubCollection plugin = null;
        String beanName = idOrSubCollectionName + "SubCollection";
        try{
            logger.debug("Looking for sub-collection bean named [{}].", beanName);
            plugin = (AthenaSubCollection)applicationContext.getBean(beanName);
        } catch (NoSuchBeanDefinitionException noBean) {
            logger.debug("No bean found under name [{}].  Will query APA store.", beanName);
        }
        if(plugin != null) {
            String username = getCurrentUsername();
            return plugin.get(type, idOrSubCollectionName, queryParams, username);
        } else {        
            PTicket ticket = getRecord(type, idOrSubCollectionName);

            if(ticket == null) {
                type = StringUtils.capitalize(type);
                throw new NotFoundException(type + " with id [" + idOrSubCollectionName + "] was not found");
            } else {
                return ticket;
            }
        }
    }

    /**
     * Skips sub-collection check and goes right for the tickets.  
     * 
     * This method returns null if no record is found
     * 
     * @param type
     * @param id
     * @return 
     */
    public PTicket getRecord(String type, Object id) {       
        return apa.getRecord(type, id);
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

    public Collection<PTicket> saveSubResource(String parentType,
                                                 Object id,
                                                 String childType,
                                                 Map<String, List<String>> queryParams,
                                                 PTicket body) throws ObjectNotFoundException {

        //Check to see if the parent record exists
        PTicket ticket  = apa.getRecord(parentType, id);
        if(ticket == null) {
            throw new NotFoundException(StringUtils.capitalize(parentType) + " witn id [" + id + "] was not found");
        }

        //load the plugin.  If the plugin is found, let it do its thing.
        AthenaSubResource plugin = null;
        try{
            plugin = (AthenaSubResource)applicationContext.getBean(childType + "SubResource");
            logger.debug("Plugin found [{}]", plugin.getClass().getName());
            String username = getCurrentUsername();
            return plugin.save(parentType, id, childType, queryParams, body, username);
        } catch (NoSuchBeanDefinitionException noBean) {
            throw new AthenaException("Cannot save to sub-resource " + childType);
        }
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
            logger.debug("Plugin found [{}]", plugin.getClass().getName());
            String username = getCurrentUsername();
            return plugin.find(parentType, id, childType, queryParams, username);
        } else {
            logger.debug("No plugin found, searching sub-resources"); 
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
        AthenaSearch search = new AthenaSearch(queryParams);
        return findRecords(type, search);
    }

    public Set<PTicket> findRecords(String type, AthenaSearch search) {
        search.setType(type);
        return apa.findTickets(search);
    }

    /**
     * TODO: This method saves the records, then if one save fails it rolls back and deletes any records that it has saved to this point.
     * There is an edge case where it saves a record, some other client modifies the record, then this thread deletes that record in a rollback.
     * @param type
     * @param records
     * @return
     */
    public List<PTicket> createRecords(String type, List<PTicket> records) {
        List<PTicket> outRecords = new ArrayList<PTicket>();
        try {
            for(PTicket record : records) {
                outRecords.add(saveWithCallbacks(type, record));
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
        return saveWithCallbacks(type, record);
    }
    
    public PTicket saveWithCallbacks(String type, PTicket record) {
        record = callbackManager.beforeSave(type, record);
        PTicket createdRecord = apa.saveRecord(type, record);
        createdRecord = callbackManager.afterSave(type, createdRecord);
        return createdRecord;        
    }

    public PTicket updateRecord(String type, PTicket record) {

        PTicket ticket = apa.getRecord(type, record.getId());

        if (ticket == null) {
            throw new NotFoundException();
        }

        return saveWithCallbacks(type, record);
    }

    public List<PTicket> updateRecords(String type, List<String> idList, PTicket patch) throws ObjectNotFoundException {
        List<PTicket> outRecords = new ArrayList<PTicket>();
        patch.setId(null);
        logger.debug("Applying [{}] to [{}]", patch, idList);
        for(String id : idList) {
            logger.debug("Applying patch to [{}]", id);

            try {
                outRecords.add(apa.patchRecord(id, type, patch));
                //TODO: Rollback
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

        return saveWithCallbacks(type, record);
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
