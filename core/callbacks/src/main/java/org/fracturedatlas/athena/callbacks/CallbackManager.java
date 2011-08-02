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

import java.util.HashMap;
import java.util.List;
import org.fracturedatlas.athena.client.PTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CallbackManager {
    Logger logger = LoggerFactory.getLogger(CallbackManager.class);
    
    @Autowired
    @javax.ws.rs.core.Context
    ApplicationContext applicationContext;
    
    public CallbackManager() { 
    }
    
    public void beforeSave(String type, PTicket record) {
        String beanName = type + "Callbacks";
        HashMap<String, List<AthenaCallback>> callbackMap = (HashMap<String, List<AthenaCallback>>)applicationContext.getBean(beanName);
        List<AthenaCallback> callbacks = callbackMap.get("beforeSave");
        
        for(AthenaCallback callback : callbacks) {
            callback.beforeSave(record);
        }
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
