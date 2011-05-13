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
import java.util.List;
import static org.junit.Assert.*;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Test;

public class PostRecordWithArrayValueContainerTest extends BaseTixContainerTest {

    JpaRecord testTicket = new JpaRecord();
    String testTicketJson = "";
    Gson gson = JsonUtil.getGson();

    public PostRecordWithArrayValueContainerTest() throws Exception {
        super();
    }

    @After
    public void teardown() {
        super.teardownRecords();
    }

    @Test
    public void testGetRecord() {

        addPropField(ValueType.STRING,"SEAT_NUMBER",Boolean.FALSE);
        addPropField(ValueType.STRING,"SECTION",Boolean.FALSE);
        addPropField(ValueType.STRING,"TIER",Boolean.FALSE);

        PTicket t = createRecord("ticket",
                                 "SEAT_NUMBER", "3D");
        t.getProps().add("TIER", "GOLD");
        t.getProps().add("TIER", "SILVER");
        String path = RECORDS_PATH;
        String jsonResponse = tix.path(path)
                                     .type("application/json")
                                     .post(String.class, gson.toJson(t));
        PTicket pTicket = gson.fromJson(jsonResponse,  PTicket.class);
        assertNotNull(pTicket);
        recordsToDelete.add(pTicket);
        List<String> tiers = t.getProps().get("TIER");
        assertTrue(tiers.contains("GOLD"));
        assertTrue(tiers.contains("SILVER"));
        assertEquals("3D", t.get("SEAT_NUMBER"));
    }

    @Test
    public void testUpdateRecord() {

        addPropField(ValueType.STRING,"SEAT_NUMBER",Boolean.FALSE);
        addPropField(ValueType.STRING,"SECTION",Boolean.FALSE);
        addPropField(ValueType.STRING,"TIER",Boolean.FALSE);

        PTicket t = createRecord("ticket",
                                 "SEAT_NUMBER", "3D");
        t.getProps().add("TIER", "GOLD");
        t.getProps().add("TIER", "SILVER");
        String path = RECORDS_PATH;
        String jsonResponse = tix.path(path)
                                     .type("application/json")
                                     .post(String.class, gson.toJson(t));
        PTicket savedTicket = gson.fromJson(jsonResponse,  PTicket.class);
        recordsToDelete.add(savedTicket);
        savedTicket.put("TIER", "NONE");
        jsonResponse = tix.path(path + savedTicket.getId())
                          .type("application/json")
                          .put(String.class, gson.toJson(savedTicket));
        PTicket updatedTicket = gson.fromJson(jsonResponse,  PTicket.class);
        assertEquals("3D", updatedTicket.get("SEAT_NUMBER"));
        assertEquals("NONE", updatedTicket.get("TIER"));
    }

    @Test
    public void testUpdateRecord2() {

        addPropField(ValueType.STRING,"SEAT_NUMBER",Boolean.FALSE);
        addPropField(ValueType.STRING,"SECTION",Boolean.FALSE);
        addPropField(ValueType.STRING,"TIER",Boolean.FALSE);

        PTicket t = createRecord("ticket",
                                 "SEAT_NUMBER", "3D");
        t.getProps().add("TIER", "GOLD");
        t.getProps().add("TIER", "SILVER");
        String path = RECORDS_PATH;
        String jsonResponse = tix.path(path)
                                     .type("application/json")
                                     .post(String.class, gson.toJson(t));
        PTicket savedTicket = gson.fromJson(jsonResponse,  PTicket.class);
        recordsToDelete.add(savedTicket);
        savedTicket.getProps().add("SEAT_NUMBER", "ANOTHER");
        jsonResponse = tix.path(path + savedTicket.getId())
                          .type("application/json")
                          .put(String.class, gson.toJson(savedTicket));
        PTicket updatedTicket = gson.fromJson(jsonResponse,  PTicket.class);
        List<String> tiers = t.getProps().get("TIER");
        assertTrue(tiers.contains("GOLD"));
        assertTrue(tiers.contains("SILVER"));
        List<String> seats = updatedTicket.getProps().get("SEAT_NUMBER");
        assertTrue(seats.contains("3D"));
        assertTrue(seats.contains("ANOTHER"));
    }
}
