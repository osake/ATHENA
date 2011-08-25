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

import org.junit.Ignore;
import org.joda.time.DateTime;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;

@Ignore
public class TimestampCallbackTest {
    CallbackManager manager = new CallbackManager();
    
    public TimestampCallbackTest() {
        
    }
    
    @Before 
    public void loadContext() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager.setApplicationContext(context);
    }   
    
    @Test
    public void testTimestampTypeWithNoCallbacks() throws Exception {
        PTicket record = new PTicket();
        record.setType("phone");
        manager.beforeSave(record.getType(),record);
        assertEquals(0, record.getProps().keySet().size());
    }
    
    @Test
    public void testTimestamp() throws Exception {
        PTicket record = new PTicket();
        record.setType("doc");
        manager.beforeSave(record.getType(),record);
        
        DateTime nowish = new DateTime();
        
        assertEquals(nowish.withMillisOfSecond(0).withSecondOfMinute(0),
                     DateUtil.parseDateTime(record.get("createdAt")).withMillisOfSecond(0).withSecondOfMinute(0));
        assertEquals(nowish.withMillisOfSecond(0).withSecondOfMinute(0),
                     DateUtil.parseDateTime(record.get("updatedAt")).withMillisOfSecond(0).withSecondOfMinute(0));
    }
    
    @Test
    public void testTimestampAlreadySaved() throws Exception {
        PTicket record = new PTicket();
        record.setType("doc");
        
        DateTime now = new DateTime();
        record.put("createdAt", DateUtil.formatDate(now));
        record.put("updatedAt", DateUtil.formatDate(now));
        Thread.sleep(1000);
        manager.beforeSave(record.getType(), record);
        DateTime nowish = new DateTime();
        
        assertEquals(now.withMillisOfSecond(0),
                     DateUtil.parseDateTime(record.get("createdAt")).withMillisOfSecond(0));
        assertEquals(nowish.withMillisOfSecond(0).withSecondOfMinute(0),
                     DateUtil.parseDateTime(record.get("updatedAt")).withMillisOfSecond(0).withSecondOfMinute(0));
        assertFalse(now.equals(DateUtil.parseDateTime(record.get("updatedAt"))));
    }
            
}
