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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.impl.jpa.BooleanTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.DateTimeTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.IntegerTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.StringTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class PutRecordContainerTest extends BaseTixContainerTest {

    Gson gson = JsonUtil.getGson();
    String path = "/";

    public PutRecordContainerTest() throws Exception {
        super();
    }

    @After
    public void teardownRecords() {
        super.teardownRecords();
    }

//    @Test
//    public void testUpdateTicket() {
//        JpaRecord t = createSampleTicket();
//
//        String path = RECORDS_PATH + t.getId() + ".json";
//
//        PTicket pTicket = t.toClientTicket();
//        pTicket.put("PRICE", "2000");
//        String updatedTicketJson = tix.path(path).type("application/json").put(String.class, gson.toJson(pTicket));
//        PTicket updatedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
//        assertTrue(pTicket.equals(updatedPTicket));
//
//        JpaRecord updatedTicket = apa.getTicket(t.getType(), updatedPTicket.getId());
//        assertTicketsEqual(updatedTicket, updatedPTicket);
//    }
//
//    @Test
//    public void testUpdateTicketWithPostIdInUrl() {
//        JpaRecord t = createSampleTicket();
//
//        String path = RECORDS_PATH + t.getId() + ".json";
//
//        PTicket savedPTicket = t.toClientTicket();
//        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, gson.toJson(t.toClientTicket()));
//        assertEquals(ClientResponse.Status.METHOD_NOT_ALLOWED, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//    }
//
//    //For now, this should pass.  Eventually we should turn this support off, return a 409 or maybe a OMFG
//    @Test
//    public void testUpdateTicketWithPost() {
//        JpaRecord t = createSampleTicket();
//
//        String path = RECORDS_PATH;
//
//        PTicket savedPTicket = t.toClientTicket();
//        String updatedTicketJson = tix.path(path).type("application/json").post(String.class, gson.toJson(savedPTicket));
//        PTicket updatedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
//        assertTrue(savedPTicket.equals(updatedPTicket));
//        assertTicketsEqual(t, updatedPTicket, false);
//
//    }
//
//    @Test
//    public void testCreateTicketWithPut() {
//        JpaRecord t = new JpaRecord();
//        t.setType("ticket");
//        PropField pf = apa.savePropField(new PropField(ValueType.INTEGER, "PRICE", Boolean.FALSE));
//        PropField pf2 = apa.savePropField(new PropField(ValueType.BOOLEAN, "SECTION", Boolean.FALSE));
//        IntegerTicketProp prop = new IntegerTicketProp(pf, 4);
//        t.addTicketProp(prop);
//        BooleanTicketProp prop2 = new BooleanTicketProp(pf2, Boolean.TRUE);
//        t.addTicketProp(prop2);
//        propFieldsToDelete.add(pf);
//        propFieldsToDelete.add(pf2);
//        ClientResponse response = tix.path(path).type("application/json").put(ClientResponse.class, gson.toJson(t.toClientTicket()));
//        assertEquals(ClientResponse.Status.METHOD_NOT_ALLOWED, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//    @Test
//    public void testUpdateTicketWithPutNoIdInBody() {
//        JpaRecord t = createSampleTicket();
//
//        String path = RECORDS_PATH + t.getId() + ".json";
//
//        PTicket savedPTicket = t.toClientTicket();
//        String json = "{\"name\":\"ticket\",\"props\":{\"PRICE\":\"4\",\"SECTION\":\"true\"}}";
//        ClientResponse response = tix.path(path).type("application/json").put(ClientResponse.class, gson.toJson(json));
//        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//    }
//
//    @Test
//    public void testCreateTicketWithPutAndBadId() {
//
//        String path = RECORDS_PATH + "0.json";
//
//        JpaRecord t = new JpaRecord();
//        t.setType("ticket");
//        PropField pf = apa.savePropField(new PropField(ValueType.INTEGER, "PRICE", Boolean.FALSE));
//        PropField pf2 = apa.savePropField(new PropField(ValueType.BOOLEAN, "SECTION", Boolean.FALSE));
//        IntegerTicketProp prop = new IntegerTicketProp(pf, 4);
//        t.addTicketProp(prop);
//        BooleanTicketProp prop2 = new BooleanTicketProp(pf2, Boolean.TRUE);
//        t.addTicketProp(prop2);
//        propFieldsToDelete.add(pf);
//        propFieldsToDelete.add(pf2);
//        ClientResponse response = tix.path(path).type("application/json").put(ClientResponse.class, gson.toJson(t.toClientTicket()));
//        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//    //this is not allowed
//    @Test
//    public void testUpdateAndChangeId() {
//        JpaRecord t = createSampleTicket();
//
//        String path = RECORDS_PATH + t.getId() + ".json";
//
//        PTicket savedPTicket = t.toClientTicket();
//        savedPTicket.setId(40000L);
//        ClientResponse response = tix.path(path).type("application/json").put(ClientResponse.class, gson.toJson(savedPTicket));
//        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        //make sure nothing got updated
//        JpaRecord savedTicket = apa.getTicket(t.getType(), t.getId());
//        assertTicketsEqual(t, savedTicket.toClientTicket());
//    }
//
//    //You should be able to put as much as you want and receive the same response
//    @Test
//    public void testPutSeveralTimes() {
//        JpaRecord t = createSampleTicket();
//
//        String path = RECORDS_PATH + t.getId() + ".json";
//
//        PTicket pTicket = t.toClientTicket();
//        pTicket.put("PRICE", "2000");
//        String updatedTicketJson = tix.path(path).type("application/json").put(String.class, gson.toJson(pTicket));
//        PTicket updatedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
//        assertTrue(pTicket.equals(updatedPTicket));
//
//        updatedTicketJson = tix.path(path).type("application/json").put(String.class, gson.toJson(pTicket));
//        updatedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
//        assertTrue(pTicket.equals(updatedPTicket));
//
//        updatedTicketJson = tix.path(path).type("application/json").put(String.class, gson.toJson(pTicket));
//        updatedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
//        assertTrue(pTicket.equals(updatedPTicket));
//
//        updatedTicketJson = tix.path(path).type("application/json").put(String.class, gson.toJson(pTicket));
//        updatedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
//        assertTrue(pTicket.equals(updatedPTicket));
//
//    }
//
//    public JpaRecord createSampleTicket() {
//        JpaRecord t = new JpaRecord();
//        t.setType("ticket");
//
//        PropField pf = apa.savePropField(new PropField(ValueType.INTEGER, "PRICE", Boolean.FALSE));
//        PropField pf2 = apa.savePropField(new PropField(ValueType.BOOLEAN, "SECTION", Boolean.FALSE));
//
//        IntegerTicketProp prop = new IntegerTicketProp(pf, 4);
//        t.addTicketProp(prop);
//
//        BooleanTicketProp prop2 = new BooleanTicketProp(pf2, Boolean.TRUE);
//        t.addTicketProp(prop2);
//
//        t = apa.saveTicket(t);
//
//        ticketsToDelete.add(t);
//        propFieldsToDelete.add(pf);
//        propFieldsToDelete.add(pf2);
//
//        return t;
//    }
}
