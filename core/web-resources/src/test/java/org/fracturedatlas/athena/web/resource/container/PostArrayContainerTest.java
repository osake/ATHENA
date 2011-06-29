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
import java.util.Set;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PostArrayContainerTest extends BaseTixContainerTest {

    String path = RECORDS_PATH;
    Gson gson = JsonUtil.getGson();
    
    public PostArrayContainerTest() throws Exception {
        super();
    }

    @After
    public void teardown() {
        super.teardownRecords();
    }

    @Test
    public void testPostRecordWithOneTicket() {
        List<PTicket> ticketList = new ArrayList<PTicket>();
        PTicket ticket = createRecord("ticket",
                                      "performanceId", "8",
                                      "eventId", "true",
                                      "price", "50");

        String jsonResponse = tix.path(path)
                                     .type("application/json")
                                     .post(String.class, gson.toJson(ticket));

        PTicket pTicket = gson.fromJson(jsonResponse,  PTicket.class);
        pTicket.setType("ticket");
        recordsToDelete.add(pTicket);
        assertNotNull(pTicket);
    }

    @Test
    public void testPostRecordWithOneTicketInArray() {
        List<PTicket> ticketList = new ArrayList<PTicket>();
        PTicket ticket = createRecord("ticket",
                                      "performanceId", "8",
                                      "eventId", "true",
                                      "price", "50");
        ticketList.add(ticket);
        PTicket[] recordArray = ticketList.toArray(new PTicket[0]);

        String jsonResponse = tix.path(path)
                                     .type("application/json")
                                     .post(String.class, gson.toJson(recordArray));

        PTicket[] tickets = gson.fromJson(jsonResponse,  PTicket[].class);
        List<PTicket> savedTicketList = Arrays.asList(tickets);
        queueForDeletionAndAddType(savedTicketList, "ticket");
        assertEquals(1, savedTicketList.size());
        assertNotNull(savedTicketList.get(0).getId());
        assertRecordsEqual(ticket, savedTicketList.get(0), Boolean.FALSE);
    }

    @Test
    public void testPostRecordWithTwoTickets() {
        List<PTicket> ticketList = new ArrayList<PTicket>();
        PTicket ticket = createRecord("ticket",
                                      "performanceId", "81",
                                      "eventId", "true",
                                      "price", "50");
        PTicket ticket2 = createRecord("ticket",
                                      "performanceId", "80",
                                      "eventId", "true",
                                      "price", "50");
        ticketList.add(ticket);
        ticketList.add(ticket2);
        PTicket[] recordArray = ticketList.toArray(new PTicket[0]);

        String jsonResponse = tix.path(path)
                                     .type("application/json")
                                     .post(String.class, gson.toJson(recordArray));

        PTicket[] tickets = gson.fromJson(jsonResponse,  PTicket[].class);
        List<PTicket> savedTicketList = Arrays.asList(tickets);
        queueForDeletionAndAddType(savedTicketList, "ticket");
        assertEquals(2, savedTicketList.size());

        for(PTicket t : savedTicketList) {
            if(t.get("performanceId").equals(ticket.get("performanceId"))) {
                assertRecordsEqual(t, ticket, Boolean.FALSE);
                PTicket savedTicket = apa.getRecord("ticket", t.getId());
                assertNotNull(savedTicket);
                assertRecordsEqual(savedTicket, ticket, Boolean.FALSE);
            } else if(t.get("performanceId").equals(ticket2.get("performanceId"))) {
                assertRecordsEqual(t, ticket2, Boolean.FALSE);
                PTicket savedTicket = apa.getRecord("ticket", t.getId());
                assertNotNull(savedTicket);
                assertRecordsEqual(savedTicket, ticket2, Boolean.FALSE);
            } else {
                fail("One of the returned tickets does not match");
            }
        }
    }

    @Test
    public void testPostRecordWithOneBadTicket() {
        List<PTicket> ticketList = new ArrayList<PTicket>();
        PTicket ticket = createRecord("ticket",
                                      "performanceId", "81",
                                      "eventId", "true",
                                      "price", "50");
        PTicket ticket2 = createRecord("ticket",
                                      "performanceId", "80",
                                      "unknownProp", "true",
                                      "price", "50");
        ticketList.add(ticket);
        ticketList.add(ticket2);
        PTicket[] recordArray = ticketList.toArray(new PTicket[0]);

        ClientResponse response = tix.path(path)
                                     .type("application/json")
                                     .post(ClientResponse.class, gson.toJson(recordArray));

        assertBadRequest(response);

        //make sure nothing got saved
        AthenaSearch search = new AthenaSearch.Builder().type("ticket").build();
        Set<PTicket> savedTickets = apa.findTickets(search);
        assertEquals(0, savedTickets.size());
    }

    @Before
    public void setupFields() {
        addPropField(ValueType.STRING,"performanceId",Boolean.FALSE);
        addPropField(ValueType.STRING,"eventId",Boolean.FALSE);
        addPropField(ValueType.INTEGER,"price",Boolean.FALSE);
    }

}
