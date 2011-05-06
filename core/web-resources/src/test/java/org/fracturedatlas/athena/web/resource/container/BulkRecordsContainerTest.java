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
import com.sun.jersey.api.client.ClientResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BulkRecordsContainerTest extends BaseTixContainerTest {

    String path = RECORDS_PATH + "patch/";
    Gson gson = JsonUtil.getGson();

    public BulkRecordsContainerTest() throws Exception {
        super();
    }

    @After
    public void teardown() {
        super.teardownRecords();
    }

    @Before
    public void setupFields() {
        addPropField(ValueType.STRING,"performanceId",Boolean.FALSE);
        addPropField(ValueType.STRING,"eventId",Boolean.FALSE);
        addPropField(ValueType.INTEGER,"price",Boolean.FALSE);
        addPropField(ValueType.STRING,"newProp",Boolean.FALSE);
    }

    //@Test
    public void testUpdateOneTicket() {
        List<PTicket> ticketList = new ArrayList<PTicket>();
        PTicket ticket = addRecord("ticket",
                                      "performanceId", "8",
                                      "eventId", "89",
                                      "price", "50");

        path += ticket.getId();
        ticketList.add(ticket);
        String jsonResponse = tix.path(path)
                                 .type("application/json")
                                 .put(String.class, gson.toJson(ticket));

        PTicket[] tickets = gson.fromJson(jsonResponse,  PTicket[].class);
        List<PTicket> savedTicketList = Arrays.asList(tickets);
        assertNotNull(savedTicketList);
        assertEquals(1,savedTicketList.size());
        assertRecordsEqual(ticket, savedTicketList.get(0), Boolean.TRUE);
    }

    @Test
    public void testUpdateTwoTickets() {
        List<PTicket> ticketList = new ArrayList<PTicket>();
        PTicket ticket = addRecord("ticket",
                                      "performanceId", "8",
                                      "eventId", "89",
                                      "price", "50");
        PTicket ticket2 = addRecord("ticket",
                                      "performanceId", "8",
                                      "eventId", "89",
                                      "price", "50");

        PTicket patch = new PTicket();
        patch.put("price", "100");

        path += ticket.getId() + "," + ticket2.getId();
        ticketList.add(ticket);
        ticketList.add(ticket2);
        String jsonResponse = tix.path(path)
                                 .type("application/json")
                                 .put(String.class, gson.toJson(patch));

        PTicket[] tickets = gson.fromJson(jsonResponse,  PTicket[].class);
        List<PTicket> savedTicketList = Arrays.asList(tickets);
        assertNotNull(savedTicketList);
        assertEquals(2,savedTicketList.size());
        for(PTicket t : savedTicketList) {
            assertEquals(t.get("performanceId"), "8");
            assertEquals(t.get("eventId"), "89");
            assertEquals(t.get("price"), "100");
            assertTrue(t.getId().equals(ticket.getId()) || t.getId().equals(ticket2.getId()));
        }
    }

    @Test
    public void testUpdateTwoTicketsWithNewProp() {
        List<PTicket> ticketList = new ArrayList<PTicket>();
        PTicket ticket = addRecord("ticket",
                                      "performanceId", "8",
                                      "eventId", "89",
                                      "price", "50");
        PTicket ticket2 = addRecord("ticket",
                                      "performanceId", "8",
                                      "eventId", "89",
                                      "price", "50");

        PTicket patch = new PTicket();
        patch.put("price", "100");
        patch.put("newProp", "NEW");

        path += ticket.getId() + "," + ticket2.getId();
        ticketList.add(ticket);
        ticketList.add(ticket2);
        String jsonResponse = tix.path(path)
                                 .type("application/json")
                                 .put(String.class, gson.toJson(patch));

        PTicket[] tickets = gson.fromJson(jsonResponse,  PTicket[].class);
        List<PTicket> savedTicketList = Arrays.asList(tickets);
        assertNotNull(savedTicketList);
        assertEquals(2,savedTicketList.size());
        for(PTicket t : savedTicketList) {
            assertEquals(t.get("performanceId"), "8");
            assertEquals(t.get("eventId"), "89");
            assertEquals(t.get("price"), "100");
            assertEquals(t.get("newProp"), "NEW");
            assertTrue(t.getId().equals(ticket.getId()) || t.getId().equals(ticket2.getId()));
        }
    }

    @Test
    public void testUpdateTwoTicketsWithBadProp() {
        List<PTicket> ticketList = new ArrayList<PTicket>();
        PTicket ticket = addRecord("ticket",
                                      "performanceId", "8",
                                      "eventId", "89",
                                      "price", "50");
        PTicket ticket2 = addRecord("ticket",
                                      "performanceId", "8",
                                      "eventId", "89",
                                      "price", "50");

        PTicket patch = new PTicket();
        patch.put("price", "100");
        patch.put("unknownProp", "NEW");

        path += ticket.getId() + "," + ticket2.getId();
        ticketList.add(ticket);
        ticketList.add(ticket2);
        ClientResponse response = tix.path(path)
                                 .type("application/json")
                                 .put(ClientResponse.class, gson.toJson(patch));
        assertBadRequest(response);

        //make sure nothing changed
        assertRecordsEqual(ticket, apa.getRecord("ticket", ticket.getId()), true);
        assertRecordsEqual(ticket2, apa.getRecord("ticket", ticket2.getId()), true);
    }
}
