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
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class PutRecordContainerTest extends BaseTixContainerTest {

    Gson gson = JsonUtil.getGson();

    public PutRecordContainerTest() throws Exception {
        super();
    }

    @After
    public void teardownRecords() {
        super.teardownRecords();
    }

    @Test
    public void testUpdateTicket() {
        PTicket t = createSampleRecord();
        String path = RECORDS_PATH + t.getId() + ".json";
        t.setType(null);
        t.put("PRICE", "2000");
        String updatedTicketJson = tix.path(path).type("application/json").put(String.class, gson.toJson(t));
        PTicket updatedTicket = gson.fromJson(updatedTicketJson, PTicket.class);
        System.out.println(t);
        System.out.println(updatedTicket);
        assertTrue(t.equals(updatedTicket));
        PTicket savedTicket = apa.getRecord("ticket", updatedTicket.getId());
        System.out.println(savedTicket);
        assertRecordsEqual(updatedTicket, savedTicket, true);
    }

    @Test
    public void testUpdateTicketWithPostIdInUrl() {
        PTicket t = createSampleRecord();
        String path = RECORDS_PATH + t.getId() + ".json";
        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, gson.toJson(t));
        assertEquals(ClientResponse.Status.METHOD_NOT_ALLOWED, ClientResponse.Status.fromStatusCode(response.getStatus()));

    }

    //For now, this should pass.  Eventually we should turn this support off, return a 409 or maybe a OMFG
    @Test
    public void testUpdateTicketWithPost() {

        String path = RECORDS_PATH;
        PTicket t = createSampleRecord();
        String updatedTicketJson = tix.path(path).type("application/json").post(String.class, gson.toJson(t));
        PTicket updatedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
        updatedPTicket.setType("ticket");
        assertTrue(t.equals(updatedPTicket));

    }

    @Test
    public void testCreateTicketWithPut() {
        PTicket t = createSampleRecord();
        ClientResponse response = tix.path("/").type("application/json").put(ClientResponse.class, gson.toJson(t));
        assertEquals(ClientResponse.Status.METHOD_NOT_ALLOWED, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testUpdateTicketWithPutNoIdInBody() {
        PTicket t = createSampleRecord();

        String path = RECORDS_PATH + t.getId() + ".json";
        String json = "{\"type\":\"ticket\",\"PRICE\":\"4\",\"SECTION\":\"true\"}";
        ClientResponse response = tix.path(path).type("application/json").put(ClientResponse.class, gson.toJson(json));
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));

    }

    @Test
    public void testPutBadId() {
        String path = RECORDS_PATH + "0.json";
        assertNotFound(path);
    }

    //this is not allowed
    @Test
    public void testUpdateToIdNotFound() {
        PTicket t = createSampleRecord();

        String path = RECORDS_PATH + "4.json";

        ClientResponse response = tix.path(path).type("application/json").put(ClientResponse.class, gson.toJson(t));
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));

        //make sure nothing got updated
        PTicket savedTicket = apa.getRecord("ticket", t.getId());
        savedTicket.setType(t.getType());
        assertRecordsEqual(t, savedTicket, false);
    }

    //this is not allowed
    @Test
    public void testUpdateAndChangeId() {
        PTicket t = createSampleRecord();
        String originalId = IdAdapter.toString(t.getId());

        String path = RECORDS_PATH + t.getId() + ".json";
        t.setId(4005L);
        ClientResponse response = tix.path(path).type("application/json").put(ClientResponse.class, gson.toJson(t));
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));

        //make sure nothing got updated
        PTicket savedTicket = apa.getRecord("ticket", originalId);
        savedTicket.setType(t.getType());
        t.setId(originalId);
        assertRecordsEqual(t, savedTicket, false);
    }

    //You should be able to put as much as you want and receive the same response
    @Test
    public void testPutSeveralTimes() {
        PTicket pTicket = createSampleRecord();

        String path = RECORDS_PATH + pTicket.getId() + ".json";

        pTicket.put("PRICE", "2000");
        String updatedTicketJson = tix.path(path).type("application/json").put(String.class, gson.toJson(pTicket));
        PTicket updatedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
        updatedPTicket.setType("ticket");
        assertTrue(pTicket.equals(updatedPTicket));

        updatedTicketJson = tix.path(path).type("application/json").put(String.class, gson.toJson(pTicket));
        updatedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
        updatedPTicket.setType("ticket");
        assertTrue(pTicket.equals(updatedPTicket));

        updatedTicketJson = tix.path(path).type("application/json").put(String.class, gson.toJson(pTicket));
        updatedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
        updatedPTicket.setType("ticket");
        assertTrue(pTicket.equals(updatedPTicket));

        updatedTicketJson = tix.path(path).type("application/json").put(String.class, gson.toJson(pTicket));
        updatedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
        updatedPTicket.setType("ticket");
        assertTrue(pTicket.equals(updatedPTicket));

    }

    public PTicket createSampleRecord() {
        PTicket t = new PTicket();

        addPropField(ValueType.INTEGER, "PRICE", Boolean.FALSE);
        addPropField(ValueType.BOOLEAN, "SECTION", Boolean.FALSE);

        return addRecord("ticket",
                  "PRICE", "4", 
                  "SECTION", "true");
    }
}
