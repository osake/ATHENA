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
package org.fracturedatlas.athena.callbacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.fracturedatlas.athena.client.PTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CallbackManager {
    static Logger logger = LoggerFactory.getLogger(CallbackManager.class);
    
    @Autowired
    @javax.ws.rs.core.Context
    ApplicationContext applicationContext;
    
    public PTicket beforeSave(String type, PTicket record) {
        try{
            List<AthenaCallback> callbacks = loadCallbacks("beforeSave", type);
        
            for(AthenaCallback callback : callbacks) {
                record = callback.beforeSave(type, record);
            }
        } catch (NoSuchBeanDefinitionException noBean) {
            logger.debug("No callbacks found for type [{}]", type);
        }
        
        return record;
    }
    
    public PTicket afterSave(String type, PTicket record) {
        try{
            List<AthenaCallback> callbacks = loadCallbacks("afterSave", type);
        
            for(AthenaCallback callback : callbacks) {
                record = callback.afterSave(type, record);
            }
        } catch (NoSuchBeanDefinitionException noBean) {
            logger.debug("No callbacks found for type [{}]", type);
        }
        
        return record;
    }
    
    public List<AthenaCallback> loadCallbacks(String callbackType, String type) {
        String beanName = type + "Callbacks";
        HashMap<String, List<AthenaCallback>> callbackMap = (HashMap<String, List<AthenaCallback>>)applicationContext.getBean(beanName);

        if(callbackMap == null) {
            return new ArrayList<AthenaCallback>();
        }

        List<AthenaCallback> callbacks = callbackMap.get(callbackType);   
        
        if(callbacks == null) {
            return new ArrayList<AthenaCallback>();
        }
        
        return callbacks;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
