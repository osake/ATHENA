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
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.*;
import static org.junit.Assert.*;

public class SearchByTypeContainerTest extends BaseTixContainerTest {

    String bottlesPath = "/bottles/";
    String casesPath = "/cases/";
    Gson gson = JsonUtil.getGson();

    @Test
    public void testFindTickets() {
        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("SOLD", "eqfalse");
        String jsonString = tix.path(bottlesPath).queryParams(queryParams).get(String.class);
        PTicket[] tickets = gson.fromJson(jsonString,  PTicket[].class);
        assertNotNull(tickets);
        assertEquals(0, tickets.length);

        queryParams = new MultivaluedMapImpl();
        queryParams.add("SOLD", "eqfalse");
        jsonString = tix.path(casesPath).queryParams(queryParams).get(String.class);
        tickets = gson.fromJson(jsonString,  PTicket[].class);
        assertNotNull(tickets);
        assertEquals(5, tickets.length);
    }

    @Test
    public void testFindTicketsByTier() {
        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("TIER", "eqSILVER");
        String jsonString = tix.path(bottlesPath).queryParams(queryParams).get(String.class);
        PTicket[] tickets = gson.fromJson(jsonString,  PTicket[].class);
        assertNotNull(tickets);
        assertEquals(2, tickets.length);

        queryParams = new MultivaluedMapImpl();
        queryParams.add("TIER", "eqSILVER");
        jsonString = tix.path(casesPath).queryParams(queryParams).get(String.class);
        tickets = gson.fromJson(jsonString,  PTicket[].class);
        assertNotNull(tickets);
        assertEquals(0, tickets.length);
    }

    @Before
    public void addTickets() throws Exception {

        PTicket t1 = new PTicket("bottle");
        PTicket t2 = new PTicket("bottle");
        PTicket t3 = new PTicket("bottle");
        PTicket t4 = new PTicket("bottle");
        PTicket t5 = new PTicket("bottle");
        PTicket t6 = new PTicket("case");
        PTicket t7 = new PTicket("case");
        PTicket t8 = new PTicket("case");
        PTicket t9 = new PTicket("case");
        PTicket t10 = new PTicket("case");

        addPropField(ValueType.INTEGER, "SEAT_NUMBER", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SECTION", StrictType.NOT_STRICT);
        addPropField(ValueType.DATETIME, "PERFORMANCE", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "TIER", StrictType.NOT_STRICT);
        addPropField(ValueType.BOOLEAN, "LOCKED", StrictType.NOT_STRICT);
        addPropField(ValueType.BOOLEAN, "SOLD", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "LOCKED_BY_API_KEY", StrictType.NOT_STRICT);
        addPropField(ValueType.DATETIME, "LOCK_EXPIRES", StrictType.NOT_STRICT);
        addPropField(ValueType.INTEGER, "PRICE", StrictType.NOT_STRICT);
        addPropField(ValueType.BOOLEAN, "HALF_PRICE_AVAILABLE", StrictType.NOT_STRICT);

        t1.put("SEAT_NUMBER", "3");
        t1.put("SECTION" , "A");
        t1.put("PERFORMANCE" , "2010-10-01T13:33:50-04:00");
        t1.put("TIER" , "SILVER");
        t1.put("LOCKED" , "false");
        t1.put("SOLD" , "true");
        t1.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t1.put("LOCK_EXPIRES" , "2010-10-01T13:33:50-04:00");
        t1.put("PRICE" , "50");
        t1.put("HALF_PRICE_AVAILABLE" , "true");

        t2.put("SEAT_NUMBER", "3");
        t2.put("SECTION" , "A");
        t2.put("PERFORMANCE" , "2010-10-02T13:33:50-04:00");
        t2.put("TIER" , "SILVER");
        t2.put("LOCKED" , "true");
        t2.put("SOLD" , "true");
        t2.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t2.put("LOCK_EXPIRES" , "2010-10-02T13:33:50-04:00");
        t2.put("PRICE" , "50");
        t2.put("HALF_PRICE_AVAILABLE" , "true");

        t3.put("SEAT_NUMBER", "3");
        t3.put("SECTION" , "A");
        t3.put("PERFORMANCE" , "2010-10-03T13:33:50-04:00");
        t3.put("TIER" , "BRONZE");
        t3.put("LOCKED" , "true");
        t3.put("SOLD" , "true");
        t3.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t3.put("LOCK_EXPIRES" , "2010-10-03T13:33:50-04:00");
        t3.put("PRICE" , "50");
        t3.put("HALF_PRICE_AVAILABLE" , "true");

        t4.put("SEAT_NUMBER", "3");
        t4.put("SECTION" , "A");
        t4.put("PERFORMANCE" , "2010-10-04T13:33:50-04:00");
        t4.put("TIER" , "GOLD");
        t4.put("LOCKED" , "false");
        t4.put("SOLD" , "true");
        t4.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t4.put("LOCK_EXPIRES" , "2010-10-04T13:33:50-04:00");
        t4.put("PRICE" , "50");
        t4.put("HALF_PRICE_AVAILABLE" , "true");

        t5.put("SEAT_NUMBER", "3");
        t5.put("SECTION" , "A");
        t5.put("PERFORMANCE" , "2010-10-05T13:33:50-04:00");
        t5.put("TIER" , "GOLD");
        t5.put("LOCKED" , "false");
        t5.put("SOLD" , "true");
        t5.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t5.put("LOCK_EXPIRES" , "2010-10-05T13:33:50-04:00");
        t5.put("PRICE" , "50");
        t5.put("HALF_PRICE_AVAILABLE" , "true");

        t6.put("SEAT_NUMBER", "3");
        t6.put("SECTION" , "B");
        t6.put("PERFORMANCE" , "2010-10-06T13:33:50-04:00");
        t6.put("TIER" , "GOLD");
        t6.put("LOCKED" , "true");
        t6.put("SOLD" , "false");
        t6.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t6.put("LOCK_EXPIRES" , "2010-10-06T13:33:50-04:00");
        t6.put("PRICE" , "50");
        t6.put("HALF_PRICE_AVAILABLE" , "true");

        t7.put("SEAT_NUMBER", "3");
        t7.put("SECTION" , "B");
        t7.put("PERFORMANCE" , "2010-10-07T13:33:50-04:00");
        t7.put("TIER" , "GOLD");
        t7.put("LOCKED" , "false");
        t7.put("SOLD" , "false");
        t7.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t7.put("LOCK_EXPIRES" , "2010-10-07T13:33:50-04:00");
        t7.put("PRICE" , "25");
        t7.put("HALF_PRICE_AVAILABLE" , "true");

        t8.put("SEAT_NUMBER", "3");
        t8.put("SECTION" , "B");
        t8.put("PERFORMANCE" , "2010-10-08T13:33:50-04:00");
        t8.put("TIER" , "GOLD");
        t8.put("LOCKED" , "false");
        t8.put("SOLD" , "false");
        t8.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t8.put("LOCK_EXPIRES" , "2010-10-08T13:33:50-04:00");
        t8.put("PRICE" , "150");
        t8.put("HALF_PRICE_AVAILABLE" , "true");

        t9.put("SEAT_NUMBER", "3");
        t9.put("SECTION" , "B");
        t9.put("PERFORMANCE" , "2010-10-09T13:33:50-04:00");
        t9.put("TIER" , "GOLD");
        t9.put("LOCKED" , "false");
        t9.put("SOLD" , "false");
        t9.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t9.put("LOCK_EXPIRES" , "2010-10-09T13:33:50-04:00");
        t9.put("PRICE" , "25");
        t9.put("HALF_PRICE_AVAILABLE" , "true");

        t10.put("SEAT_NUMBER", "3");
        t10.put("SECTION" , "C");
        t10.put("PERFORMANCE" , "2010-10-10T13:33:50-04:00");
        t10.put("TIER" , "GOLD");
        t10.put("LOCKED" , "false");
        t10.put("SOLD" , "false");
        t10.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t10.put("LOCK_EXPIRES" , "2010-10-10T13:33:50-04:00");
        t10.put("PRICE" , "25");
        t10.put("HALF_PRICE_AVAILABLE" , "true");

        t1 = apa.saveRecord(t1);
        t2 = apa.saveRecord(t2);
        t3 = apa.saveRecord(t3);
        t4 = apa.saveRecord(t4);
        t5 = apa.saveRecord(t5);
        t6 = apa.saveRecord(t6);
        t7 = apa.saveRecord(t7);
        t8 = apa.saveRecord(t8);
        t9 = apa.saveRecord(t9);
        t10 = apa.saveRecord(t10);

        recordsToDelete.add(t1);
        recordsToDelete.add(t2);
        recordsToDelete.add(t3);
        recordsToDelete.add(t4);
        recordsToDelete.add(t5);
        recordsToDelete.add(t6);
        recordsToDelete.add(t7);
        recordsToDelete.add(t8);
        recordsToDelete.add(t9);
        recordsToDelete.add(t10);
    }

    @After
    public void teardownRecords() {
        super.teardownRecords();
    }
}
