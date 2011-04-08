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
import java.text.ParseException;
import static org.junit.Assert.*;



import org.codehaus.jackson.map.ObjectMapper;
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
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.Test;


public class TicketResourceContainerTest extends BaseTixContainerTest {

    JpaRecord testTicket = new JpaRecord();
    String testTicketJson = "";
    ObjectMapper mapper = new ObjectMapper();
    Gson gson = JsonUtil.getGson();

    public TicketResourceContainerTest() throws Exception {
        super();
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

//    @Test
//    public void testGetTicketJson() {
//        JpaRecord t = new JpaRecord();
//        t.setType("ticket");
//
//        PropField field = new PropField();
//        field.setValueType(ValueType.STRING);
//        field.setName("SEAT_NUMBER");
//        field.setStrict(Boolean.FALSE);
//        PropField pf = apa.savePropField(field);
//
//        field = new PropField();
//        field.setValueType(ValueType.STRING);
//        field.setName("SECTION");
//        field.setStrict(Boolean.FALSE);
//        PropField pf2 = apa.savePropField(field);
//
//        StringTicketProp prop = new StringTicketProp();
//        prop.setPropField(pf);
//        prop.setValue("3D");
//        t.addTicketProp(prop);
//
//        StringTicketProp prop2 = new StringTicketProp();
//        prop2.setPropField(pf2);
//        prop2.setValue("ORCHESTRA");
//        t.addTicketProp(prop2);
//
//        t = apa.saveTicket(t);
//
//        ticketsToDelete.add(t);
//        propFieldsToDelete.add(pf);
//        propFieldsToDelete.add(pf2);
//
//        String path = RECORDS_PATH + t.getId() + ".json";
//
//        String ticketString = tix.path(path).get(String.class);
//        assertNotNull(ticketString);
//        PTicket pTicket = gson.fromJson(ticketString, PTicket.class);
//        assertTicketsEqual(t, pTicket);
//    }
//
//    @Test
//    public void testGetTicketBooleanProp() {
//        JpaRecord t = new JpaRecord();
//        t.setType("ticket");
//
//        PropField pf = apa.savePropField(new PropField(ValueType.STRING, "SEAT_NUMBER", Boolean.FALSE));
//        PropField pf2 = apa.savePropField(new PropField(ValueType.BOOLEAN, "SECTION", Boolean.FALSE));
//
//        StringTicketProp prop = new StringTicketProp();
//        prop.setPropField(pf);
//        prop.setValue("3D");
//        t.addTicketProp(prop);
//
//        BooleanTicketProp prop2 = new BooleanTicketProp();
//        prop2.setPropField(pf2);
//        prop2.setValue(true);
//        t.addTicketProp(prop2);
//
//        t = apa.saveTicket(t);
//
//        ticketsToDelete.add(t);
//        propFieldsToDelete.add(pf);
//        propFieldsToDelete.add(pf2);
//
//        String path = RECORDS_PATH + t.getId() + ".json";
//
//        String ticketString = tix.path(path).get(String.class);
//        assertNotNull(ticketString);
//        PTicket pTicket = gson.fromJson(ticketString, PTicket.class);
//        assertTicketsEqual(t, pTicket);
//    }
//
//    @Test
//    public void testGetTicketDateTimeProp() throws Exception {
//        JpaRecord t = new JpaRecord();
//        t.setType("ticket");
//
//        PropField pf = apa.savePropField(new PropField(ValueType.DATETIME, "PERFORMANCE", Boolean.FALSE));
//        PropField pf2 = apa.savePropField(new PropField(ValueType.BOOLEAN, "SECTION", Boolean.FALSE));
//
//        DateTime dt = new DateTime();
//        DateTimeTicketProp prop = new DateTimeTicketProp();
//        prop.setPropField(pf);
//        prop.setValue(DateUtil.parseDate(dt.toString(ISODateTimeFormat.dateTimeNoMillis())));
//        t.addTicketProp(prop);
//
//        BooleanTicketProp prop2 = new BooleanTicketProp();
//        prop2.setPropField(pf2);
//        prop2.setValue(true);
//        t.addTicketProp(prop2);
//
//        t = apa.saveTicket(t);
//
//        ticketsToDelete.add(t);
//        propFieldsToDelete.add(pf);
//        propFieldsToDelete.add(pf2);
//
//        String path = RECORDS_PATH + t.getId() + ".json";
//
//        String ticketString = tix.path(path).get(String.class);
//        assertNotNull(ticketString);
//        PTicket pTicket = gson.fromJson(ticketString, PTicket.class);
//        assertTicketsEqual(t, pTicket);
//    }
//
//    @Test
//    public void testGetTicketIntegerProp() {
//        JpaRecord t = new JpaRecord();
//        t.setType("ticket");
//        PropField pf = apa.savePropField(new PropField(ValueType.INTEGER, "SEAT_NUMBER", Boolean.FALSE));
//        PropField pf2 = apa.savePropField(new PropField(ValueType.BOOLEAN, "SECTION", Boolean.FALSE));
//
//        IntegerTicketProp prop = new IntegerTicketProp();
//        prop.setPropField(pf);
//        prop.setValue(490);
//        t.addTicketProp(prop);
//
//        BooleanTicketProp prop2 = new BooleanTicketProp();
//        prop2.setPropField(pf2);
//        prop2.setValue(true);
//        t.addTicketProp(prop2);
//
//        t = apa.saveTicket(t);
//
//        ticketsToDelete.add(t);
//        propFieldsToDelete.add(pf);
//        propFieldsToDelete.add(pf2);
//
//        String path = RECORDS_PATH + t.getId() + ".json";
//
//        String ticketString = tix.path(path).get(String.class);
//        assertNotNull(ticketString);
//        PTicket pTicket = gson.fromJson(ticketString, PTicket.class);
//        assertTicketsEqual(t, pTicket);
//    }
//
//    @Test
//    public void testGetTicketProps() {
//        JpaRecord t = new JpaRecord();
//        t.setType("ticket");
//        PropField pf = apa.savePropField(new PropField(ValueType.INTEGER, "SEAT_NUMBER", Boolean.FALSE));
//        propFieldsToDelete.add(pf);
//        PropField pf2 = apa.savePropField(new PropField(ValueType.BOOLEAN, "SECTION", Boolean.FALSE));
//        propFieldsToDelete.add(pf2);
//
//        IntegerTicketProp prop = new IntegerTicketProp();
//        prop.setPropField(pf);
//        prop.setValue(490);
//        t.addTicketProp(prop);
//
//        BooleanTicketProp prop2 = new BooleanTicketProp();
//        prop2.setPropField(pf2);
//        prop2.setValue(true);
//        t.addTicketProp(prop2);
//
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        String path = RECORDS_PATH + t.getId() + "/props";
//
//        String ticketString = tix.path(path).get(String.class);
//        assertNotNull(ticketString);
//        String expectedString = "{\"SECTION\":\"true\",\"SEAT_NUMBER\":\"490\"}";
//        assertEquals(expectedString, ticketString);
//    }
//
//    @Test
//    public void testGetTicketPropsDoesntExist() {
//        String path = "tickets/0/props";
//        ClientResponse response = tix.path(path).get(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//    @Test
//    public void testGetTicketPropsNoProps() {
//        JpaRecord t = new JpaRecord();
//        t.setType("ticket");
//        PropField pf = apa.savePropField(new PropField(ValueType.INTEGER, "SEAT_NUMBER", Boolean.FALSE));
//        propFieldsToDelete.add(pf);
//        PropField pf2 = apa.savePropField(new PropField(ValueType.BOOLEAN, "SECTION", Boolean.FALSE));
//        propFieldsToDelete.add(pf2);
//
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        String path = RECORDS_PATH + t.getId() + "/props";
//
//        String ticketString = tix.path(path).get(String.class);
//        assertNotNull(ticketString);
//        String expectedString = "{}";
//        assertEquals(expectedString, ticketString);
//    }
//
//    @Test
//    public void testGetTicketWithNoProps() {
//        JpaRecord t = new JpaRecord();
//        t.setType("ticket");
//
//        t = apa.saveTicket(t);
//
//        ticketsToDelete.add(t);
//
//        String path = RECORDS_PATH + t.getId() + ".json";
//
//        String ticketString = tix.path(path).get(String.class);
//        assertNotNull(ticketString);
//        PTicket pTicket = gson.fromJson(ticketString, PTicket.class);
//        assertTicketsEqual(t, pTicket);
//    }
//
//    @Test
//    public void testGetTicketThatDoesntExist() {
//        String path = RECORDS_PATH + "0.json";
//        ClientResponse response = tix.path(path).get(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//    @Test
//    public void testSearchWithNoParams() {
//        String path = RECORDS_PATH;
//        ClientResponse response = tix.path(path).get(ClientResponse.class);
//        assertEquals(ClientResponse.Status.FORBIDDEN,
//                     ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//    @Test
//    public void testDeleteTicket() {
//        JpaRecord t = new JpaRecord();
//        t.setType("ticket");
//
//        PropField field = new PropField();
//        field.setValueType(ValueType.STRING);
//        field.setName("WXYZ");
//        field.setStrict(Boolean.FALSE);
//        PropField pf = apa.savePropField(field);
//
//        StringTicketProp prop = new StringTicketProp();
//        prop.setPropField(pf);
//        prop.setValue("WXYZ");
//        t.addTicketProp(prop);
//        t = apa.saveTicket(t);
//
//        ticketsToDelete.add(t);
//        propFieldsToDelete.add(pf);
//
//        String path = RECORDS_PATH + t.getId() + ".json";
//        ClientResponse response = tix.path(path).delete(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NO_CONTENT,
//                ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        JpaRecord shouldBeDeleted = apa.getTicket(t.getType(), t.getId());
//        assertNull(shouldBeDeleted);
//
//        path = "tickets/" + t.getId() + ".json";
//        response = tix.path(path).get(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//    @Test
//    public void testDeleteTicket2() throws ParseException {
//
//
//        JpaRecord t = new JpaRecord();
//        JpaRecord t2 = new JpaRecord();
//        JpaRecord t3 = new JpaRecord();
//        JpaRecord t4 = new JpaRecord();
//
//        PropField field = new PropField();
//        field = new PropField();
//        field.setValueType(ValueType.DATETIME);
//        field.setName("Artist");
//        field.setStrict(Boolean.FALSE);
//        PropField pf3 = apa.savePropField(field);
//
//        t.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//
//        t2.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
//        t2.setType("ticket");
//        t2 = apa.saveTicket(t2);
//
//        t3.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
//        t3.setType("ticket");
//        t3 = apa.saveTicket(t3);
//
//        t4.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
//        t4.setType("ticket");
//        t4 = apa.saveTicket(t4);
//
//
//        ticketsToDelete.add(t);
//        ticketsToDelete.add(t2);
//        ticketsToDelete.add(t3);
//        ticketsToDelete.add(t4);
//        propFieldsToDelete.add(pf3);
//
//        String path = RECORDS_PATH + t3.getId() + ".json";
//        ClientResponse response = tix.path(path).delete(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NO_CONTENT,
//                ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        JpaRecord shouldBeDeleted = apa.getTicket(t3.getType(), t3.getId());
//        assertNull(shouldBeDeleted);
//        JpaRecord expected = apa.getTicket(t.getType(), t.getId());
//        assertEquals(expected, t);
//        expected = apa.getTicket(t2.getType(), t2.getId());
//        assertEquals(expected, t2);
//        expected = apa.getTicket(t4.getType(), t4.getId());
//        assertEquals(expected, t4);
//
//        path = "tickets/" + t3.getId() + ".json";
//        response = tix.path(path).get(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//    @Test
//    public void testDeleteAFewTickets() throws ParseException {
//
//
//        JpaRecord t = new JpaRecord();
//        JpaRecord t2 = new JpaRecord();
//        JpaRecord t3 = new JpaRecord();
//        JpaRecord t4 = new JpaRecord();
//
//        PropField field = new PropField();
//        field = new PropField();
//        field.setValueType(ValueType.DATETIME);
//        field.setName("Artist");
//        field.setStrict(Boolean.FALSE);
//        PropField pf3 = apa.savePropField(field);
//
//        t.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//
//        t2.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
//        t2.setType("ticket");
//        t2 = apa.saveTicket(t2);
//
//        t3.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
//        t3.setType("ticket");
//        t3 = apa.saveTicket(t3);
//
//        t4.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
//        t4.setType("ticket");
//        t4 = apa.saveTicket(t4);
//
//
//        ticketsToDelete.add(t);
//        ticketsToDelete.add(t2);
//        ticketsToDelete.add(t3);
//        ticketsToDelete.add(t4);
//        propFieldsToDelete.add(pf3);
//
//        String path = RECORDS_PATH + t3.getId() + ".json";
//        ClientResponse response = tix.path(path).delete(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NO_CONTENT,
//                ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        path = RECORDS_PATH + t4.getId() + ".json";
//        response = tix.path(path).delete(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NO_CONTENT,
//                ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        JpaRecord shouldBeDeleted = apa.getTicket(t3.getType(), t3.getId());
//        assertNull(shouldBeDeleted);
//        JpaRecord expected = apa.getTicket(t.getType(), t.getId());
//        assertEquals(expected, t);
//        expected = apa.getTicket(t2.getType(), t2.getId());
//        assertEquals(expected, t2);
//
//        path = RECORDS_PATH + t3.getId() + ".json";
//        response = tix.path(path).get(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        path = RECORDS_PATH + t4.getId() + ".json";
//        response = tix.path(path).get(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//    @Test
//    public void testDeleteTicketDoesntExist() {
//        JpaRecord t = new JpaRecord();
//        t.setType("ticket");
//
//        PropField field = new PropField();
//        field.setValueType(ValueType.STRING);
//        field.setName("WXYZ");
//        field.setStrict(Boolean.FALSE);
//        PropField pf = apa.savePropField(field);
//
//        StringTicketProp prop = new StringTicketProp();
//        prop.setPropField(pf);
//        prop.setValue("WXYZ");
//        t.addTicketProp(prop);
//        t = apa.saveTicket(t);
//
//        ticketsToDelete.add(t);
//        propFieldsToDelete.add(pf);
//
//        String path = RECORDS_PATH + "0.json";
//        ClientResponse response = tix.path(path).delete(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NOT_FOUND,
//                ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        JpaRecord shouldStillExist = apa.getTicket(t.getType(), t.getId());
//        assertEquals(t, shouldStillExist);
//    }
}
