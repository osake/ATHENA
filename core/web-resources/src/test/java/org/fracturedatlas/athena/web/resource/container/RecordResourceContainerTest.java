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
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.Test;


public class RecordResourceContainerTest extends BaseTixContainerTest {

    JpaRecord testTicket = new JpaRecord();
    String testTicketJson = "";
    ObjectMapper mapper = new ObjectMapper();
    Gson gson = JsonUtil.getGson();

    public RecordResourceContainerTest() throws Exception {
        super();
    }

    @After
    public void teardown() {
        super.teardownRecords();
    }

    @Test
    public void testGetRecordJson() {

        addPropField(ValueType.STRING,"SEAT_NUMBER",Boolean.FALSE);
        addPropField(ValueType.STRING,"SECTION",Boolean.FALSE);

        PTicket t = addRecord("ticket",
                              "SEAT_NUMBER", "3D");

        String path = RECORDS_PATH + t.getId() + ".json";

        String ticketString = tix.path(path).get(String.class);
        assertNotNull(ticketString);
        PTicket pTicket = gson.fromJson(ticketString, PTicket.class);
        assertRecordsEqual(t, pTicket, true);
    }

    @Test
    public void testGetRecordBooleanProp() {
        addPropField(ValueType.STRING,"SEAT_NUMBER",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"SECTION",Boolean.FALSE);

        PTicket t = addRecord("ticket",
                              "SEAT_NUMBER", "3D",
                              "SECTION", "true");

        String path = RECORDS_PATH + t.getId() + ".json";

        String ticketString = tix.path(path).get(String.class);
        assertNotNull(ticketString);
        PTicket pTicket = gson.fromJson(ticketString, PTicket.class);
        assertRecordsEqual(t, pTicket, true);
    }

    @Test
    public void testGetRecordDateTimeProp() throws Exception {
        addPropField(ValueType.DATETIME,"PERFORMANCE",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"SECTION",Boolean.FALSE);
        DateTime dt = new DateTime();
        PTicket t = addRecord("ticket",
                              "PERFORMANCE", dt.toString(ISODateTimeFormat.dateTimeNoMillis()),
                              "SECTION", "true");

        String path = RECORDS_PATH + t.getId() + ".json";

        String ticketString = tix.path(path).get(String.class);
        assertNotNull(ticketString);
        PTicket pTicket = gson.fromJson(ticketString, PTicket.class);
        assertRecordsEqual(t, pTicket, true);
    }

    @Test
    public void testGetRecordIntegerProp() {
        addPropField(ValueType.INTEGER,"SEAT_NUMBER",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"SECTION",Boolean.FALSE);

        PTicket t = addRecord("ticket",
                              "SEAT_NUMBER", "8",
                              "SECTION", "true");

        String path = RECORDS_PATH + t.getId() + ".json";

        String ticketString = tix.path(path).get(String.class);
        assertNotNull(ticketString);
        PTicket pTicket = gson.fromJson(ticketString, PTicket.class);
        assertRecordsEqual(t, pTicket, true);
    }

    @Test
    public void testGetRecordThatDoesntExist() {
        String path = RECORDS_PATH + "0.json";
        ClientResponse response = tix.path(path).get(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testSearchWithNoParams() {
        String path = RECORDS_PATH;
        ClientResponse response = tix.path(path).get(ClientResponse.class);
        assertEquals(ClientResponse.Status.FORBIDDEN,
                     ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testDeleteRecord() {
        addPropField(ValueType.INTEGER,"SEAT_NUMBER",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"SECTION",Boolean.FALSE);

        PTicket t = addRecord("ticket",
                              "SEAT_NUMBER", "8",
                              "SECTION", "true");

        String path = RECORDS_PATH + t.getId() + ".json";
        ClientResponse response = tix.path(path).delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NO_CONTENT,
                ClientResponse.Status.fromStatusCode(response.getStatus()));

        PTicket shouldBeDeleted = apa.getRecord(t.getType(), t.getId());
        assertNull(shouldBeDeleted);

        path = "tickets/" + t.getId() + ".json";
        response = tix.path(path).get(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testDeleteAFewRecords() throws ParseException {
        addPropField(ValueType.INTEGER,"SEAT_NUMBER",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"SECTION",Boolean.FALSE);

        PTicket t1 = addRecord("ticket",
                              "SEAT_NUMBER", "8",
                              "SECTION", "true");

        PTicket t2 = addRecord("ticket",
                              "SEAT_NUMBER", "18",
                              "SECTION", "true");

        PTicket t3 = addRecord("ticket",
                              "SEAT_NUMBER", "28",
                              "SECTION", "true");

        PTicket t4 = addRecord("ticket",
                              "SEAT_NUMBER", "38",
                              "SECTION", "true");

        String path = RECORDS_PATH + t3.getId() + ".json";
        ClientResponse response = tix.path(path).delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NO_CONTENT,
                ClientResponse.Status.fromStatusCode(response.getStatus()));

        path = RECORDS_PATH + t4.getId() + ".json";
        response = tix.path(path).delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NO_CONTENT,
                ClientResponse.Status.fromStatusCode(response.getStatus()));

        PTicket shouldBeDeleted = apa.getRecord(t3.getType(), t3.getId());
        assertNull(shouldBeDeleted);
        PTicket expected = apa.getRecord(t1.getType(), t1.getId());
        assertEquals(expected, t1);
        expected = apa.getRecord(t2.getType(), t2.getId());
        assertEquals(expected, t2);

        path = RECORDS_PATH + t3.getId() + ".json";
        response = tix.path(path).get(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));

        path = RECORDS_PATH + t4.getId() + ".json";
        response = tix.path(path).get(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testDeleteRecordDoesntExist() {

        addPropField(ValueType.INTEGER,"SEAT_NUMBER",Boolean.FALSE);
        addPropField(ValueType.BOOLEAN,"SECTION",Boolean.FALSE);

        PTicket t = addRecord("ticket",
                              "SEAT_NUMBER", "8",
                              "SECTION", "true");

        String path = RECORDS_PATH + "0.json";
        ClientResponse response = tix.path(path).delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND,
                ClientResponse.Status.fromStatusCode(response.getStatus()));

        PTicket shouldStillExist = apa.getRecord(t.getType(), t.getId());
        assertEquals(t, shouldStillExist);
    }
}
