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
import org.codehaus.jackson.map.ObjectMapper;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.PropValue;
import org.fracturedatlas.athena.apa.model.StrictType;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;

public class FieldResourceValuesContainerTest extends BaseTixContainerTest {
    
    Gson gson = JsonUtil.getGson();
    ObjectMapper mapper = JsonUtil.getMapper();
    PropField testField = null;
    PropValue testValue = null;
    
    public FieldResourceValuesContainerTest() throws Exception {
        super();
    }

    @Before
    public void addSampleFields() {

        testField = apa.savePropField(new PropField(ValueType.STRING, "SECTION", StrictType.STRICT));
        propFieldsToDelete.add(testField);

        testValue = apa.savePropValue(new PropValue(testField, "AA"));
        PropValue testValue2 = apa.savePropValue(new PropValue(testField, "BB"));
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
    public void testDeleteValue() throws Exception {

        String path = "fields/" + testField.getId() + "/values.json";
        PropValue propValue = null;
        String testValueJson = "";
        ClientResponse response;
        String jsonResponse;
        testValueJson = "{\"propValue\":\"XX\"}";
        response = tix.path(path).type("application/json").post(ClientResponse.class, testValueJson);
        jsonResponse = response.getEntity(String.class);

        propValue = mapper.readValue(jsonResponse, PropValue.class);
        path = "fields/" + testField.getId() + "/values/" + propValue.getId().toString();
        response = tix.path(path).type("application/json").delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NO_CONTENT, ClientResponse.Status.fromStatusCode(response.getStatus()));

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

    /*
     * TODO: This should return something other than INTERNAL_SERVER_ERROR
     * Hard to do because JAckson barfs up in the Resource layer
     */
    @Test
    public void testCreateFieldNoData() {
        String path = "fields/";

        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, "");
        String jsonResponse = response.getEntity(String.class);
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));

    }

    @Test
    public void testUpdateValue() throws Exception {
        String path = "fields/" + testField.getId() + "/values.json";
        testValue.setPropValue("DD");
        String testValueJson = "{\"id\":\""+testValue.getId()+"\",\"propValue\":\""+testValue.getPropValue()+"\"}";
        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, testValueJson);
        String jsonResponse = response.getEntity(String.class);

        PropField pf = apa.getPropField(testField.getId());

        PropValue propValue = mapper.readValue(jsonResponse, PropValue.class);
        PropField propField = propValue.getPropField();

        assertEquals(ClientResponse.Status.OK, ClientResponse.Status.fromStatusCode(response.getStatus()));
        assertEquals("DD", propValue.getPropValue());
        assertEquals(propValue.getId().toString(), testValue.getId().toString());
        assertEquals(2, pf.getPropValues().size());

        response = tix.path("fields/" + testField.getId()).type("application/json").get(ClientResponse.class);
        jsonResponse = response.getEntity(String.class);
        assertEquals(jsonResponse,
                "{\"id\":\""+testField.getId()+"\",\"name\":\"SECTION\",\"strict\":true,\"valueType\":\"STRING\",\"propValues\":[\"DD\",\"BB\"]}");


    }

    @Test
    public void testCreateValueWithoutIncludingPropField() throws Exception {
        String path = "fields/" + testField.getId() + "/values";
        PropValue propValue = null;
        String testValueJson = "";

        testValueJson = "{\"propValue\":\"TEST_VALUE\"}";

        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, testValueJson);
        String jsonResponse = response.getEntity(String.class);
        propValue = mapper.readValue(jsonResponse, PropValue.class);

        assertEquals(ClientResponse.Status.OK, ClientResponse.Status.fromStatusCode(response.getStatus()));
        assertEquals(propValue.getPropValue(), "TEST_VALUE");
        assertNotNull(propValue.getId());

    }

    @Test
    public void testCreateDuplicateValue() throws Exception {
        String path = "fields/" + testField.getId() + "/values";
        PropValue propValue = null;
        String testValueJson = "";
        testValueJson = "{\"propValue\":\"AA\"}";
        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, testValueJson);
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }
}
