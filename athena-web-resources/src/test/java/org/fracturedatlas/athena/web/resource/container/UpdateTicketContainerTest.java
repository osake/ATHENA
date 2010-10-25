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

import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.DateTime;
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
import org.fracturedatlas.athena.apa.model.BooleanTicketProp;
import org.fracturedatlas.athena.apa.model.DateTimeTicketProp;
import org.fracturedatlas.athena.apa.model.IntegerTicketProp;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class UpdateTicketContainerTest extends BaseTixContainerTest {

    Gson gson = JsonUtil.getGson();
    String path = "/";

    public UpdateTicketContainerTest() throws Exception {
        super();
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

    @Test
    public void testCreateThenUpdateNoPropsNoChange() {
        Ticket t = new Ticket();
        t.setName("ticket");
        PTicket pTicket = t.toClientTicket();

        String ticketJson = gson.toJson(pTicket);
        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
        assertNotNull(savedPTicket.getId());
        assertTicketsEqual(t, savedPTicket, false);

        String updatedTicketJson = tix.path(path).type("application/json").post(String.class, gson.toJson(savedPTicket));
        PTicket updatedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
        assertTrue(savedPTicket.equals(updatedPTicket));
        assertTicketsEqual(t, updatedPTicket, false);

        apa.deleteTicket(savedPTicket.getId());
    }

    @Test
    public void testCreateThenUpdate2NoPropsNoChange() {
        Ticket t = new Ticket();
        t.setName("ticket");
        PTicket pTicket = t.toClientTicket();

        String ticketJson = gson.toJson(pTicket);
        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
        assertNotNull(savedPTicket.getId());
        assertTicketsEqual(t, savedPTicket, false);

        tix.path(path).type("application/json").post(String.class, gson.toJson(savedPTicket));
        tix.path(path).type("application/json").post(String.class, gson.toJson(savedPTicket));
        tix.path(path).type("application/json").post(String.class, gson.toJson(savedPTicket));

        String updatedTicketJson = tix.path(path).type("application/json").post(String.class, gson.toJson(savedPTicket));
        PTicket updatedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
        assertTrue(savedPTicket.equals(updatedPTicket));
        assertTicketsEqual(t, updatedPTicket, false);

        apa.deleteTicket(savedPTicket.getId());
    }

    @Test
    public void testCreateThenUpdateName() {
        Ticket t = createSampleTicket(true);
        ticketsToDelete.add(t);
        t.setName("updated ticket");
        PTicket pTicket = t.toClientTicket();

        String ticketJson = gson.toJson(pTicket);
        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
        assertNotNull(savedPTicket.getId());
        assertTicketsEqual(t, savedPTicket, true);
    }

    @Test
    public void testUpdateAddNewProp() {

        Ticket t = createSampleTicket(true);
        ticketsToDelete.add(t);

        PropField field = new PropField();
        field.setValueType(ValueType.STRING);
        field.setName("FOO");
        field.setStrict(Boolean.FALSE);
        PropField pf = apa.savePropField(field);
        propFieldsToDelete.add(pf);

        t.setName("updated ticket");
        PTicket pTicket = t.toClientTicket();
        pTicket.put("FOO", "FIGHTERS");

        String ticketJson = gson.toJson(pTicket);

        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
        assertNotNull(savedPTicket.getId());
        assertEquals(pTicket, savedPTicket);
    }

    //We're updating a ticket but only sending one property.  Other properties should remain
    @Test
    public void testUpdateAddNewPropButDontSendOtherProps() {

        Ticket t = createSampleTicket(true);
        ticketsToDelete.add(t);

        PropField field = new PropField();
        field.setValueType(ValueType.STRING);
        field.setName("FOO");
        field.setStrict(Boolean.FALSE);
        PropField pf = apa.savePropField(field);
        propFieldsToDelete.add(pf);

        t.setName("updated ticket");
        PTicket pTicket = t.toClientTicket();
        pTicket.put("FOO", "FIGHTERS");

        PTicket testPTicket = new PTicket();
        testPTicket.setName(pTicket.getName());
        testPTicket.setId(pTicket.getId());
        testPTicket.put("FOO", "FIGHTERS");
        String ticketJson = gson.toJson(testPTicket);

        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
        assertNotNull(savedPTicket.getId());
        assertEquals(pTicket, savedPTicket);
    }

    @Test
    public void testUpdateProp() {

        Ticket t = createSampleTicket(true);
        ticketsToDelete.add(t);

        t.setName("updated ticket");
        PTicket pTicket = t.toClientTicket();
        pTicket.put("SEAT_NUMBER", "3009");
        String ticketJson = gson.toJson(pTicket);

        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
        assertNotNull(savedPTicket.getId());
        assertEquals(pTicket, savedPTicket);
    }

    @Test
    public void testUpdateAllProps() {

        Ticket t = createSampleTicket(true);
        ticketsToDelete.add(t);

        t.setName("updated ticket");
        PTicket pTicket = t.toClientTicket();
        pTicket.put("SEAT_NUMBER", "3009");
        pTicket.put("SECTION", "JIMMY");
        String ticketJson = gson.toJson(pTicket);

        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
        assertNotNull(savedPTicket.getId());
        assertEquals(pTicket, savedPTicket);
    }

    @Test
    public void testUpdateTicketBlankRequest() {

        Ticket t = createSampleTicket(false);
        ticketsToDelete.add(t);

        String ticketJson = "";

        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, ticketJson);
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testUpdateTicketBadJson() {

        Ticket t = createSampleTicket(false);
        ticketsToDelete.add(t);

        String ticketJson = "{BAD_JSON:BAD}";

        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, ticketJson);
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testUpdateTicketNotExist() {

        Ticket t = createSampleTicket(false);
        ticketsToDelete.add(t);

        t.setName("updated ticket");
        PTicket pTicket = t.toClientTicket();
        pTicket.setId(40000L);

        String ticketJson = gson.toJson(pTicket);

        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, ticketJson);
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testUpdateDateTimeProp() throws Exception {

        Ticket t = createSampleTicket(false);

        PropField field = new PropField(ValueType.DATETIME, "PERFORMANCE", Boolean.FALSE);
        PropField pf = apa.savePropField(field);
        propFieldsToDelete.add(pf);
        DateTime dt = new DateTime();
        DateTimeTicketProp prop = new DateTimeTicketProp(pf, dt.toDate());
        t.addTicketProp(prop);

        t = apa.saveTicket(t);
        ticketsToDelete.add(t);
        dt = dt.plusHours(6);
        PTicket pTicket = t.toClientTicket();
        pTicket.put("PERFORMANCE", dt.toString(ISODateTimeFormat.dateTimeNoMillis()));
        String ticketJson = gson.toJson(pTicket);

        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
        assertNotNull(savedPTicket.getId());
        assertEquals(pTicket, savedPTicket);
    }

    @Test
    public void testUpdateDateTimePropNotADate() throws Exception {

        Ticket t = createSampleTicket(false);

        PropField field = new PropField(ValueType.DATETIME, "PERFORMANCE", Boolean.FALSE);
        PropField pf = apa.savePropField(field);
        propFieldsToDelete.add(pf);

        DateTimeTicketProp prop = new DateTimeTicketProp(pf, DateUtil.parseDate("2010-10-14T13:33:50-04:00"));
        t.addTicketProp(prop);

        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        PTicket pTicket = t.toClientTicket();
        pTicket.put("PERFORMANCE", "NOT_A_DATE");
        String ticketJson = gson.toJson(pTicket);

        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, ticketJson);
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }


    @Test
    public void testUpdateIntegerPropNotAnInteger() {

        Ticket t = createSampleTicket(false);

        PropField field = new PropField(ValueType.INTEGER, "PERFORMANCE", Boolean.FALSE);
        PropField pf = apa.savePropField(field);
        propFieldsToDelete.add(pf);

        IntegerTicketProp prop = new IntegerTicketProp(pf, 9);
        t.addTicketProp(prop);

        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        PTicket pTicket = t.toClientTicket();
        pTicket.put("PERFORMANCE", "NaN");
        String ticketJson = gson.toJson(pTicket);

        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, ticketJson);
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    public Ticket createSampleTicket(Boolean saveItToo) {
        Ticket t = new Ticket();
        t.setName("ticket");

        PropField field = new PropField();
        field.setValueType(ValueType.STRING);
        field.setName("SEAT_NUMBER");
        field.setStrict(Boolean.FALSE);
        PropField pf = apa.savePropField(field);

        field = new PropField();
        field.setValueType(ValueType.STRING);
        field.setName("SECTION");
        field.setStrict(Boolean.FALSE);
        PropField pf2 = apa.savePropField(field);

        StringTicketProp prop = new StringTicketProp();
        prop.setPropField(pf);
        prop.setValue("3D");
        t.addTicketProp(prop);

        StringTicketProp prop2 = new StringTicketProp();
        prop2.setPropField(pf2);
        prop2.setValue("ORCHESTRA");
        t.addTicketProp(prop2);

        if (saveItToo) {
            t = apa.saveTicket(t);
            ticketsToDelete.add(t);
        }

        propFieldsToDelete.add(pf);
        propFieldsToDelete.add(pf2);

        return t;
    }
}
