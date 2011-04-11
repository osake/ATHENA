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

import org.fracturedatlas.athena.search.AthenaSearch;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.impl.jpa.BooleanTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.DateTimeTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.IntegerTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.StringTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class SaveTicketContainerTest extends BaseTixContainerTest {

    Gson gson = JsonUtil.getGson();
    String path = RECORDS_PATH;

    public SaveTicketContainerTest() throws Exception {
        super();
    }

    @After
    public void teardownRecords() {
        super.teardownRecords();
    }

//    @Test
//    public void postRecordWithNullId() {
//        PTicket pTicket = new PTicket("ticket");
//
//        PropField pf = apa.savePropField(new PropField(ValueType.STRING, "temp", StrictType.NOT_STRICT));
//        propFieldsToDelete.add(pf);
//
//        String ticketJson = "{\"id\":null,\"temp\":\"34\"}";
//
//        String updatedTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
//        PTicket savedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
//        assertNotNull(savedPTicket.getId());
//        assertEquals(savedPTicket.get("temp"), "34");
//        apa.deleteTicket(pTicket.getType(), savedPTicket.getId());
//    }
//
//    @Test
//    public void testCreateTicketWithNoProps() {
//        PTicket pTicket = new PTicket("ticket");
//        String ticketJson = gson.toJson(pTicket);
//        String updatedTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
//        PTicket savedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
//        assertNotNull(savedPTicket.getId());
//        assertRecordsEqual(pTicket, savedPTicket, false);
//        apa.deleteTicket(savedPTicket.getType(), savedPTicket.getId());
//    }
//
//    @Test
//    public void testCreateTicketBadIntegerValue() {
//        PTicket pTicket = createSampleTicket(false);
//        PropField pf = apa.savePropField(new PropField(ValueType.INTEGER, "FOO_INT", Boolean.FALSE));
//        propFieldsToDelete.add(pf);
//        pTicket.put("FOO_INT", "NaN");
//
//        String ticketJson = gson.toJson(pTicket);
//
//        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, ticketJson);
//        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//    @Test
//    public void testCreateTicketBadDateTimeValue() {
//        PTicket pTicket = createSampleTicket(false);
//        PropField pf = apa.savePropField(new PropField(ValueType.DATETIME, "FOO_DATE", Boolean.FALSE));
//        propFieldsToDelete.add(pf);
//        pTicket.put("FOO_INT", "NaD");
//
//        String ticketJson = gson.toJson(pTicket);
//
//        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, ticketJson);
//        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//    @Test
//    public void testCreateTicketBadBooleanValue() {
//        PTicket pTicket = createSampleTicket(false);
//        PropField pf = apa.savePropField(new PropField(ValueType.BOOLEAN, "FOO_BOOL", Boolean.FALSE));
//        propFieldsToDelete.add(pf);
//        pTicket.put("FOO_BOOL", "notabool");
//
//        String ticketJson = gson.toJson(pTicket);
//        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
//        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
//        assertEquals("false", savedPTicket.get("FOO_BOOL"));
//        recordsToDelete.add(savedPTicket);
//    }
//
//    @Test
//    public void testCreateTicket() {
//        PTicket pTicket = createSampleTicket(false);
//        String ticketJson = gson.toJson(pTicket);
//        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
//        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
//        assertNotNull(savedPTicket.getId());
//        assertRecordsEqual(pTicket, savedPTicket, false);
//        recordsToDelete.add(savedPTicket);
//    }
//
//    //TODO: Makes no sense
//    //@Test
//    public void testUpdateTicketUnknownField() {
//        PTicket pTicket = createSampleTicket(false);
//        pTicket.put("BAD_FIELD", "BAD_FISH");
//
//        String ticketJson = gson.toJson(pTicket);
//        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, ticketJson);
//        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        //make sure the ticket hasn't changed
//        PTicket savedTicket = apa.getTicket(pTicket.getType(), pTicket.getId()).toClientTicket();
//        recordsToDelete.add(savedTicket);
//        assertRecordsEqual(pTicket, savedTicket, false);
//    }
//
//    @Test
//    public void testCreateTicketUnknownField() {
//        PTicket pTicket = createSampleTicket(false);
//        pTicket.put("BAD_FIELD", "BAD_FISH");
//
//        String ticketJson = gson.toJson(pTicket);
//        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, ticketJson);
//        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        //make sure nothing got saved
//        AthenaSearch as = new AthenaSearch();
//        as.addConstraint("SECTION", Operator.EQUALS, "ORCHESTRA");
//        assertEquals(0, apa.findTickets(as).size());
//
//        as = new AthenaSearch();
//        as.addConstraint("SEAT_NUMBER", Operator.EQUALS, "3D");
//        assertEquals(0, apa.findTickets(as).size());
//    }

//    @Test
//    public void testCreateTicketBooleanProp() {
//
//        PTicket pTicket = new PTicket("ticket");
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
//        IntegerTicketProp prop = new IntegerTicketProp();
//        prop.setPropField(pf);
//        prop.setValue(490);
//        pTicket.put("SEAT_NUMBER", "490");
//
//        BooleanTicketProp prop2 = new BooleanTicketProp();
//        prop2.setPropField(pf2);
//        prop2.setValue(true);
//        pTicket.put("SECTION", "true");
//        String ticketJson = gson.toJson(pTicket);
//
//        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
//        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
//        assertNotNull(savedPTicket.getId());
//        assertTicketsEqual(t, savedPTicket, false);
//
//        PTicket savedTicket = apa.getTicket(pTicket.getType(), savedPTicket.getId());
//        assertTicketsEqual(savedTicket, savedPTicket);
//
//        ticketsToDelete.add(savedTicket);
//        propFieldsToDelete.add(pf);
//        propFieldsToDelete.add(pf2);
//    }
//
//    @Test
//    public void testCreateTicketDateTimeProp() throws Exception {
//        JpaRecord t = new JpaRecord();
//        t.setType("ticket");
//
//        PropField field = new PropField();
//        field.setValueType(ValueType.STRING);
//        field.setName("PERFORMANCE");
//        field.setStrict(Boolean.FALSE);
//        PropField pf = apa.savePropField(field);
//
//        field = new PropField();
//        field.setValueType(ValueType.STRING);
//        field.setName("SECTION");
//        field.setStrict(Boolean.FALSE);
//        PropField pf2 = apa.savePropField(field);
//
//        DateTimeTicketProp prop = new DateTimeTicketProp();
//        prop.setPropField(pf);
//        prop.setValue(DateUtil.parseDate("2010-10-14T13:33:50-04:00"));
//        t.addTicketProp(prop);
//
//        BooleanTicketProp prop2 = new BooleanTicketProp();
//        prop2.setPropField(pf2);
//        prop2.setValue(true);
//        t.addTicketProp(prop2);
//
//
//        PTicket pTicket = t.toClientTicket();
//
//        String ticketJson = gson.toJson(pTicket);
//
//        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
//        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
//        assertNotNull(savedPTicket.getId());
//        assertTicketsEqual(t, savedPTicket, false);
//
//        JpaRecord savedTicket = apa.getTicket(t.getType(), savedPTicket.getId());
//        assertTicketsEqual(savedTicket, savedPTicket);
//
//        ticketsToDelete.add(savedTicket);
//        propFieldsToDelete.add(pf);
//        propFieldsToDelete.add(pf2);
//    }
//
//    //@Test
//    //Disabling this so that it doesn't run during the normal "mvn test" cycle
//    //It does indeed work, though.
//    public void testCreateTicketTenProps() throws Exception {
//
//        PropField seatNumberField = apa.savePropField(new PropField(ValueType.INTEGER, "SEAT_NUMBER", Boolean.FALSE));
//        PropField sectionField = apa.savePropField(new PropField(ValueType.STRING, "SECTION", Boolean.FALSE));
//        PropField soldField = apa.savePropField(new PropField(ValueType.BOOLEAN, "SOLD", Boolean.FALSE));
//        PropField tierField = apa.savePropField(new PropField(ValueType.STRING, "TIER", Boolean.FALSE));
//        PropField priceField = apa.savePropField(new PropField(ValueType.STRING, "PRICE", Boolean.FALSE));
//        PropField performanceField = apa.savePropField(new PropField(ValueType.DATETIME, "PERFORMANCE", Boolean.FALSE));
//        PropField venueField = apa.savePropField(new PropField(ValueType.STRING, "VENUE", Boolean.FALSE));
//        PropField eventField = apa.savePropField(new PropField(ValueType.STRING, "EVENT", Boolean.FALSE));
//        PropField lockedField = apa.savePropField(new PropField(ValueType.BOOLEAN, "LOCKED", Boolean.FALSE));
//        PropField redeemedField = apa.savePropField(new PropField(ValueType.BOOLEAN, "REDEEMED", Boolean.FALSE));
//
//        propFieldsToDelete.add(seatNumberField);
//        propFieldsToDelete.add(sectionField);
//        propFieldsToDelete.add(soldField);
//        propFieldsToDelete.add(tierField);
//        propFieldsToDelete.add(priceField);
//        propFieldsToDelete.add(performanceField);
//        propFieldsToDelete.add(venueField);
//        propFieldsToDelete.add(eventField);
//        propFieldsToDelete.add(lockedField);
//        propFieldsToDelete.add(redeemedField);
//
//
//        PTicket pTicket = new PTicket();
//
//        pTicket.getProps().put(seatNumberField.getName(), "34");
//        pTicket.getProps().put(sectionField.getName(), "CCC");
//        pTicket.getProps().put(soldField.getName(), "false");
//        pTicket.getProps().put(tierField.getName(), "GOLD");
//        pTicket.getProps().put(priceField.getName(), "3000");
//        pTicket.getProps().put(performanceField.getName(), "2010-10-14T13:33:50-04:00");
//        pTicket.getProps().put(venueField.getName(), "Everyman Theater");
//        pTicket.getProps().put(eventField.getName(), "World Tour 2004");
//        pTicket.getProps().put(lockedField.getName(), "false");
//        pTicket.getProps().put(redeemedField.getName(), "false");
//
//        String ticketJson = gson.toJson(pTicket);
//
//        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
//        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
//        assertNotNull(savedPTicket.getId());
//        pTicket.setId(savedPTicket.getId());
//        assertTrue(pTicket.equals(savedPTicket));
//
//        JpaRecord savedTicket = apa.getTicket("ticket", savedPTicket.getId());
//        assertTicketsEqual(savedTicket, savedPTicket);
//
//        ticketsToDelete.add(savedTicket);
//    }
//
//    //@Test
//    //Disabling this so that it doesn't run during the normal "mvn test" cycle
//    //It does indeed work, though.
//    public void testCreateManyProps() throws Exception {
//        //the actual number of props sent will be 4*NUMBER_OF_PROPS.  Bad name.
//        final Integer NUMBER_OF_PROPS = 1000;
//
//        List<PropField> propFields = new ArrayList<PropField>();
//
//
//        PTicket pTicket = new PTicket();
//
//        for (int i = 0; i < NUMBER_OF_PROPS; i++) {
//            PropField randomField = apa.savePropField(new PropField(
//                    ValueType.STRING,
//                    UUID.randomUUID().toString().substring(2, 8),
//                    Boolean.FALSE));
//            propFields.add(randomField);
//            propFieldsToDelete.add(randomField);
//
//            pTicket.getProps().put(randomField.getName(), UUID.randomUUID().toString().substring(2, 8));
//        }
//
//
//        GregorianCalendar cal = new GregorianCalendar();
//        for (int i = 0; i < NUMBER_OF_PROPS; i++) {
//            cal.add(Calendar.DAY_OF_YEAR, 1);
//            PropField randomField = apa.savePropField(new PropField(
//                    ValueType.DATETIME,
//                    UUID.randomUUID().toString().substring(3, 18),
//                    Boolean.FALSE));
//            propFields.add(randomField);
//            propFieldsToDelete.add(randomField);
//
//            pTicket.getProps().put(randomField.getName(), DateUtil.formatDate(cal.getTime()));
//        }
//
//        for (int i = 0; i < NUMBER_OF_PROPS; i++) {
//            Random random = new Random();
//            PropField randomField = apa.savePropField(new PropField(
//                    ValueType.INTEGER,
//                    UUID.randomUUID().toString().substring(3, 18),
//                    Boolean.FALSE));
//            propFields.add(randomField);
//            propFieldsToDelete.add(randomField);
//
//            pTicket.getProps().put(randomField.getName(), Integer.toString(random.nextInt()));
//        }
//
//        for (int i = 0; i < NUMBER_OF_PROPS; i++) {
//            Boolean flipper = Boolean.TRUE;
//            PropField randomField = apa.savePropField(new PropField(
//                    ValueType.BOOLEAN,
//                    UUID.randomUUID().toString().substring(3, 18),
//                    Boolean.FALSE));
//            propFields.add(randomField);
//            propFieldsToDelete.add(randomField);
//
//            pTicket.getProps().put(randomField.getName(), Boolean.toString(flipper));
//            flipper = !flipper;
//        }
//
//        String ticketJson = gson.toJson(pTicket);
//
//        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
//        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
//        assertNotNull(savedPTicket.getId());
//        pTicket.setId(savedPTicket.getId());
//        assertTrue(pTicket.equals(savedPTicket));
//
//        JpaRecord savedTicket = apa.getTicket("ticket", savedPTicket.getId());
//        assertTicketsEqual(savedTicket, savedPTicket);
//
//        ticketsToDelete.add(savedTicket);
//    }

    public PTicket createSampleTicket(Boolean saveItToo) {
        PTicket t = new PTicket();
        t.setType("ticket");

        PropField field = new PropField();
        field.setValueType(ValueType.STRING);
        field.setName("SEAT_NUMBER");
        field.setStrict(Boolean.FALSE);
        PropField pf = apa.savePropField(field);
        propFieldsToDelete.add(pf);

        field = new PropField();
        field.setValueType(ValueType.STRING);
        field.setName("SECTION");
        field.setStrict(Boolean.FALSE);
        PropField pf2 = apa.savePropField(field);
        propFieldsToDelete.add(pf2);

        t.put("SEAT_NUMBER","3D");
        t.put("SECTION","ORCHESTRA");

        if (saveItToo) {
            t = apa.saveRecord(t.getType(), t);
            recordsToDelete.add(t);
        }


        return t;
    }
}
