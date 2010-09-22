package org.fracturedatlas.athena.web.resource.container;
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

import com.google.gson.Gson;
import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.PropValue;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;


import java.util.ArrayList;
import java.util.List;

import org.fracturedatlas.athena.client.PField;
import org.fracturedatlas.athena.apa.model.StrictType;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FieldResourceContainerTest extends BaseTixContainerTest {

    String testFieldJson = "";
    ObjectMapper mapper = JsonUtil.getMapper();
    PropField testField;
    PropValue testValue;
    Logger logger = Logger.getLogger(FieldResourceContainerTest.class);
    Gson gson = JsonUtil.getGson();
    List<PropField> propFieldsToDelete = new ArrayList<PropField>();

    public FieldResourceContainerTest() throws Exception {
        super();
    }

    @Before
    public void addSampleFields() {

        testField = apa.savePropField(new PropField(ValueType.STRING, "SECTION", StrictType.STRICT));

        propFieldsToDelete.add(testField);
        testValue = apa.savePropValue(new PropValue(testField, "AA"));

        testField.addPropValue(testValue);
        testField = apa.savePropField(testField);

        testValue = apa.savePropValue(new PropValue(testField, "BB"));

        testField.addPropValue(testValue);
        testField = apa.savePropField(testField);
    }

    @After
    public void teardownTickets() {
        for (Ticket t : ticketsToDelete) {
            try {
                apa.deleteTicket(t);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        for (PropField pf : propFieldsToDelete) {
            try {
                apa.deletePropField(pf);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    @Test
    public void testGetFieldJson() throws Exception {

        String path = "fields/" + testField.getId() + ".json";
        String propFieldString = tix.path(path).get(String.class);
        assertNotNull(propFieldString);
        PField actualField = mapper.readValue(propFieldString, PField.class);
        assertEquals(testField.getName(), actualField.getName());
        assertEquals(testField.getValueType().toString(), actualField.getValueType());
        assertEquals(testField.getStrict(), actualField.getStrict());
        assertTrue(IdAdapter.isEqual(testField.getId(), actualField.getId()));
    }

    @Test
    public void testGetValueJson() throws Exception {
        String path = "fields/" + testField.getId() + "/values/" + testValue.getId() + ".json";
        PropValue actualValue = null;
        String jsonResponse = tix.path(path).type("application/json").get(String.class);
        actualValue = mapper.readValue(jsonResponse, PropValue.class);
        assertNotNull(jsonResponse);
        assertEquals(testValue.getId().toString(), actualValue.getId().toString());
        assertEquals(testValue.getPropValue(), actualValue.getPropValue());
    }

//    @Test
//    public void testGetAllValuesJson() {
//        String path = "fields/" + testField.getId() + "/values";
//        ClientResponse response = tix.path(path).type("application/json").get(ClientResponse.class);
//        String jsonResponse = response.getEntity(String.class);
//        assertNotNull(jsonResponse);
//        assertEquals(ClientResponse.Status.OK, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//    @Test
//    public void testGetAllFieldsJson() throws Exception {
//        PropField f1 = apa.savePropField(new PropField(ValueType.STRING, "TEMP1", StrictType.NOT_STRICT));
//        propFieldsToDelete.add(f1);
//        PropField f2 = apa.savePropField(new PropField(ValueType.BOOLEAN, "TEMP2", StrictType.NOT_STRICT));
//        propFieldsToDelete.add(f2);
//        PropField f3 = apa.savePropField(new PropField(ValueType.INTEGER, "TEMP3", StrictType.NOT_STRICT));
//        propFieldsToDelete.add(f3);
//        PropField f4 = apa.savePropField(new PropField(ValueType.DATETIME, "TEMP4", StrictType.STRICT));
//        propFieldsToDelete.add(f4);
//
//        String path = "fields.json";
//        ClientResponse response = tix.path(path).type("application/json").get(ClientResponse.class);
//        String jsonResponse = response.getEntity(String.class);
//        assertNotNull(jsonResponse);
//        assertEquals(ClientResponse.Status.OK, ClientResponse.Status.fromStatusCode(response.getStatus()));
//        Gson gson = JsonUtil.getGson();
//        PField[] pFieldArray = gson.fromJson(jsonResponse, PField[].class);
//        List<PField> pFields = Arrays.asList(pFieldArray);
//        assertEquals(5, pFields.size());
//        for(PField pField : pFields) {
//            if(pField.getName().equals("TEMP1")) {
//               assertFieldsEqual(f1, pField);
//            }
//            if(pField.getName().equals("TEMP2")) {
//               assertFieldsEqual(f2, pField);
//            }
//            if(pField.getName().equals("TEMP3")) {
//               assertFieldsEqual(f3, pField);
//            }
//            if(pField.getName().equals("TEMP4")) {
//               assertFieldsEqual(f4, pField);
//            }
//            if(pField.getName().equals("SECTION")) {
//               assertFieldsEqual(testField, pField);
//            }
//        }
//    }
//
//    @Test
//    public void testGetFieldThatDoesntExist() {
//        String path = "fields/0.json";
//        ClientResponse response = tix.path(path).get(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//    @Test
//    public void testCreateFieldBoolean() throws Exception {
//        String path = "fields/";
//        PropField propField = null;
//        testFieldJson = "{\"valueType\":\"BOOLEAN\",\"strict\":\"false\",\"name\":\"BOOL\"}";
//
//        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, testFieldJson);
//        String jsonResponse = response.getEntity(String.class);
//
//        propField = mapper.readValue(jsonResponse, PropField.class);
//        assertEquals(ClientResponse.Status.OK, ClientResponse.Status.fromStatusCode(response.getStatus()));
//        assertEquals(propField.getName(), "BOOL");
//        assertEquals(propField.getValueType(), ValueType.BOOLEAN);
//        assertEquals(propField.getStrict(), false);
//        assertNotNull(propField.getId());
//
//        propFieldsToDelete.add(propField);
//
//    }
//
//    @Test
//    public void testCreateFieldDatetime() throws Exception {
//        String path = "fields/";
//        PropField propField = null;
//        testFieldJson = "{\"valueType\":\"DATETIME\",\"strict\":\"false\",\"name\":\"BOOL\"}";
//        logger.debug("Asking for creation of " + testFieldJson);
//
//        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, testFieldJson);
//        String jsonResponse = response.getEntity(String.class);
//
//        logger.debug("Response is " + jsonResponse);
//        propField = mapper.readValue(jsonResponse, PropField.class);
//        logger.debug("json of propField is " + mapper.writeValueAsString(propField));
//        assertEquals(ClientResponse.Status.OK, ClientResponse.Status.fromStatusCode(response.getStatus()));
//        assertEquals(propField.getName(), "BOOL");
//        assertEquals(propField.getValueType(), ValueType.DATETIME);
//        assertEquals(propField.getStrict(), false);
//        assertNotNull(propField.getId());
//
//        propFieldsToDelete.add(propField);
//    }
//
//    @Test
//    public void testCreateFieldInvalidValueType() throws Exception {
//        String path = "fields/";
//        PropField propField = null;
//        testFieldJson = "{\"valueType\":\"FAKE!\",\"strict\":\"false\",\"name\":\"BOOL\"}";
//
//        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, testFieldJson);
//        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//    }
//
//    @Test
//    public void testCreateField() throws Exception {
//        String path = "fields/";
//        PropField propField = null;
//        testFieldJson = "{\"valueType\":\"STRING\",\"strict\":\"false\",\"name\":\"ARTIST\"}";
//        logger.debug("Asking for creation of " + testFieldJson);
//
//        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, testFieldJson);
//        String jsonResponse = response.getEntity(String.class);
//
//        logger.debug("Response is " + jsonResponse);
//        propField = mapper.readValue(jsonResponse, PropField.class);
//        logger.debug("json of propField is " + mapper.writeValueAsString(propField));
//        assertEquals(ClientResponse.Status.OK, ClientResponse.Status.fromStatusCode(response.getStatus()));
//        assertEquals(propField.getName(), "ARTIST");
//        assertEquals(propField.getValueType(), ValueType.STRING);
//        assertEquals(propField.getStrict(), false);
//        assertNotNull(propField.getId());
//
//        propFieldsToDelete.add(propField);
//
//    }
//
//    @Test
//    public void testCreateFieldStrict() throws Exception {
//        String path = "fields/";
//        PropField propField = null;
//        testFieldJson = "{\"valueType\":\"STRING\",\"strict\":\"true\",\"name\":\"TIRES\"}";
//
//        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, testFieldJson);
//        String jsonResponse = response.getEntity(String.class);
//
//        logger.debug("Response is " + jsonResponse);
//        propField = mapper.readValue(jsonResponse, PropField.class);
//        propFieldsToDelete.add(propField);
//        assertEquals(ClientResponse.Status.OK, ClientResponse.Status.fromStatusCode(response.getStatus()));
//        assertEquals(propField.getName(), "TIRES");
//        assertEquals(propField.getValueType(), ValueType.STRING);
//        assertEquals(propField.getStrict(), true);
//        assertNotNull(propField.getId());
//
//
//    }
//
//    @Test
//    public void testCreateFieldStrictIsEmpty() {
//        String path = "fields/";
//        testFieldJson = "{\"valueType\":\"STRING\",\"strict\":\"\",\"name\":\"TIRES\"}";
//
//        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, testFieldJson);
//        String jsonResponse = response.getEntity(String.class);
//
//        logger.debug("Response is " + jsonResponse);
//        assertEquals(ClientResponse.Status.OK, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        PropField field = gson.fromJson(jsonResponse, PropField.class);
//        propFieldsToDelete.add(field);
//        assertEquals(ValueType.STRING, field.getValueType());
//        assertEquals("TIRES", field.getName());
//        assertEquals(StrictType.NOT_STRICT, field.getStrict());
//
//    }
//
//    @Test
//    public void testCreateFieldWithForbiddenCharacters() throws Exception {
//        String path = "fields/";
//        testFieldJson = "{\"valueType\":\"STRING\",\"strict\":\"true\",\"name\":\"Seat Number\"}";
//
//        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, testFieldJson);
//        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//    }
//
//    @Test
//    public void testDeleteField() throws Exception {
//        String path = "fields/";
//        testFieldJson = "{\"valueType\":\"STRING\",\"strict\":\"true\",\"name\":\"Promoter\"}";
//        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, testFieldJson);
//        String jsonResponse = response.getEntity(String.class);
//
//        PropField propField = mapper.readValue(jsonResponse, PropField.class);
//
//        path = "fields/" + propField.getId().toString();
//
//        response = tix.path(path).type("application/json").delete(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NO_CONTENT, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//    }
//
//    @Test
//    public void testDeleteFieldDoesntExist() throws Exception {
//        String path = "fields/204040404440.json";
//
//        ClientResponse response = tix.path(path).type("application/json").delete(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//    }
}
