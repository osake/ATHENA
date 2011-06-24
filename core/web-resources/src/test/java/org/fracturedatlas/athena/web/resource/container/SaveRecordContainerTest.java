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

import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.MediaType;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import java.text.ParseException;
import org.joda.time.DateTime;
import com.google.gson.Gson;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class SaveRecordContainerTest extends BaseTixContainerTest {

    Gson gson = JsonUtil.getGson();
    String path = RECORDS_PATH;

    public SaveRecordContainerTest() throws Exception {
        super();
    }

    @After
    public void teardownRecords() {
        super.teardownRecords();
    }

    @Test
    public void postRecordWithNullId() {
        PTicket pTicket = new PTicket("ticket");
        recordsToDelete.add(pTicket);

        addPropField(ValueType.STRING, "temp", StrictType.NOT_STRICT);

        String ticketJson = "{\"id\":null,\"temp\":\"34\"}";

        String updatedTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
        PTicket savedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
        assertNotNull(savedPTicket.getId());
        assertEquals(savedPTicket.get("temp"), "34");
    }

    @Test
    public void testCreateTicketWithNoProps() {
        PTicket pTicket = new PTicket("ticket");
        recordsToDelete.add(pTicket);
        String ticketJson = gson.toJson(pTicket);
        String updatedTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
        PTicket savedPTicket = gson.fromJson(updatedTicketJson, PTicket.class);
        assertNotNull(savedPTicket.getId());
        assertRecordsEqual(pTicket, savedPTicket, false);
    }

    @Test
    public void testCreateTicketBadIntegerValue() {
        PTicket pTicket = new PTicket("ticket");
        addPropField(ValueType.INTEGER, "FOO_INT", StrictType.NOT_STRICT);
        pTicket.put("FOO_INT", "NaN");

        String ticketJson = gson.toJson(pTicket);

        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, ticketJson);
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testCreateTicketBadDateTimeValue() {
        PTicket pTicket = new PTicket();
        addPropField(ValueType.DATETIME, "FOO_DATE", StrictType.NOT_STRICT);
        pTicket.put("FOO_DATE", "NaD");

        String ticketJson = gson.toJson(pTicket);

        ClientResponse response = tix.path(path).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, ticketJson);
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    //bad boolean values default to false
    @Test
    public void testCreateTicketBadBooleanValue() {
        PTicket pTicket = new PTicket();
        addPropField(ValueType.BOOLEAN, "FOO_BOOL", StrictType.NOT_STRICT);
        pTicket.put("FOO_BOOL", "notabool");
        recordsToDelete.add(pTicket);

        String ticketJson = gson.toJson(pTicket);
        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
        assertEquals("false", savedPTicket.get("FOO_BOOL"));
        recordsToDelete.add(savedPTicket);
    }

    @Test
    public void testCreateTicket() {
        PTicket pTicket = createSampleRecord();
        String ticketJson = gson.toJson(pTicket);
        String createdTicketJson = tix.path(path).type("application/json").post(String.class, ticketJson);
        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
        assertNotNull(savedPTicket.getId());
        assertRecordsEqual(pTicket, savedPTicket, false);
    }

    @Test
    public void testCreateTicketUnknownField() {
        addPropField(ValueType.INTEGER, "PRICE", Boolean.FALSE);
        addPropField(ValueType.BOOLEAN, "SECTION", Boolean.FALSE);
        PTicket pTicket =  new PTicket();
        pTicket.put("PRICE", "4528");
        pTicket.put("SECTION", "true");
        pTicket.put("BAD_FIELD", "BAD_FISH");

        String ticketJson = gson.toJson(pTicket);
        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, ticketJson);
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));

        //make sure nothing got saved
        AthenaSearch as = new AthenaSearch();
        as.setType("ticket");
        as.addConstraint("PRICE", Operator.EQUALS, "4258");
        assertEquals(0, apa.findTickets(as).size());
    }

    @Test
    public void testCreateTicketBooleanProp() {

        PTicket pTicket = createSampleRecord();

        String createdTicketJson = tix.path(path).type("application/json").post(String.class, gson.toJson(pTicket));
        PTicket savedPTicket = gson.fromJson(createdTicketJson, PTicket.class);
        assertNotNull(savedPTicket.getId());
        assertRecordsEqual(pTicket, savedPTicket, false);

        PTicket retrTicket = apa.getRecord("ticket", savedPTicket.getId());
        assertEquals(savedPTicket.get("PRICE"), retrTicket.get("PRICE"));
        assertEquals(savedPTicket.get("SECTION"), retrTicket.get("SECTION"));
        assertEquals(savedPTicket.get("DESCRIPTION"), retrTicket.get("DESCRIPTION"));
        try {
            DateTime one = DateUtil.parseDateTime(savedPTicket.get("TIME"));
            DateTime two = DateUtil.parseDateTime(retrTicket.get("TIME"));
            assertTrue(one.isEqual(two));
        } catch (ParseException pe) {
            fail("Could not parse date returned from ATHENA");
        }
        
    }

    public PTicket createSampleRecord() {
        PTicket t = new PTicket();

        addPropField(ValueType.INTEGER, "PRICE", Boolean.FALSE);
        addPropField(ValueType.BOOLEAN, "SECTION", Boolean.FALSE);
        addPropField(ValueType.DATETIME, "TIME", Boolean.FALSE);
        addPropField(ValueType.TEXT, "DESCRIPTION", Boolean.FALSE);

        return addRecord("ticket",
                          "PRICE", "4",
                          "SECTION", "true",
                          "TIME", "2010-03-03T04:04:04Z",
                          "DESCRIPTION", "2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z2010-03-03T04:04:04Z");
    }
}
