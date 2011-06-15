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
package org.fracturedatlas.athena.web.resource.container;

import com.google.gson.Gson;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Arrays;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SearchArrayContainsContainerTest extends BaseTixContainerTest {

    String path = RECORDS_PATH;
    Gson gson = JsonUtil.getGson();
    
    public SearchArrayContainsContainerTest() throws Exception {
        super();
    }

    @After
    public void teardown() {
        super.teardownRecords();
    }

    @Test
    public void testFindRecordsWithArrayValues() {
        
        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("tags", "3");

        String jsonString = tix.path(path).queryParams(queryParams).get(String.class);
        PTicket[] tickets = gson.fromJson(jsonString,  PTicket[].class);
        assertNotNull(tickets);
        assertEquals(5, tickets.length);
        
        queryParams = new MultivaluedMapImpl();
        queryParams.add("tags", "400");
        jsonString = tix.path(path).queryParams(queryParams).get(String.class);
        tickets = gson.fromJson(jsonString,  PTicket[].class);
        assertNotNull(tickets);
        assertEquals(2, tickets.length);
        
        queryParams = new MultivaluedMapImpl();
        queryParams.add("tags", "whatever");
        jsonString = tix.path(path).queryParams(queryParams).get(String.class);
        tickets = gson.fromJson(jsonString,  PTicket[].class);
        assertNotNull(tickets);
        assertEquals(0, tickets.length);
    }
    
    @Before
    public void setup() {
        addPropField(ValueType.STRING,"tags",Boolean.FALSE);
        
        PTicket t1 = new PTicket("ticket");
        PTicket t2 = new PTicket("ticket");
        PTicket t3 = new PTicket("ticket");
        PTicket t4 = new PTicket("ticket");
        PTicket t5 = new PTicket("ticket");

        t1.getProps().put("tags", Arrays.asList("3", "4"));
        t2.getProps().put("tags", Arrays.asList("3", "400"));
        t3.getProps().put("tags", Arrays.asList("3", "1023"));
        t4.getProps().put("tags", Arrays.asList("3", "400"));
        t5.getProps().put("tags", Arrays.asList("3", "xyz"));

        t1 = apa.saveRecord(t1);
        t2 = apa.saveRecord(t2);
        t3 = apa.saveRecord(t3);
        t4 = apa.saveRecord(t4);
        t5 = apa.saveRecord(t5);

        recordsToDelete.add(t1);
        recordsToDelete.add(t2);
        recordsToDelete.add(t3);
        recordsToDelete.add(t4);
        recordsToDelete.add(t5);        
    }
}
