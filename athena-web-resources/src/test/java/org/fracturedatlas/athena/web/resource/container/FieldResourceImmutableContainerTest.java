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
import org.fracturedatlas.athena.client.PField;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StrictType;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;


/*
 * This tests immutability at both the POST and the PUT endpoints
 */
public class FieldResourceImmutableContainerTest extends BaseTixContainerTest {

    public FieldResourceImmutableContainerTest() throws Exception {
        super();
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

    @Test
    public void testImmutableFieldsName() {
        String path = "fields";
        Gson gson = JsonUtil.getGson();
        PropField field = apa.savePropField(new PropField(ValueType.BOOLEAN, "TESTONE", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);

        PField pField = field.toClientField();
        pField.setValueType(ValueType.STRING.toString());
        pField.setStrict(StrictType.NOT_STRICT);
        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, gson.toJson(pField));
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
        response = tix.path(path + "/" + field.getId()).type("application/json").put(ClientResponse.class, gson.toJson(pField));
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));

    }

    @Test
    public void testChangeStrict() {
        String path = "fields";
        Gson gson = JsonUtil.getGson();
        PropField field = apa.savePropField(new PropField(ValueType.DATETIME, "TESTONE", StrictType.STRICT));
        propFieldsToDelete.add(field);

        PField pField = field.toClientField();
        pField.setStrict(StrictType.NOT_STRICT);
        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, gson.toJson(pField));
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
        response = tix.path(path + "/" + field.getId()).type("application/json").put(ClientResponse.class, gson.toJson(pField));
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));

    }

    @Test
    public void testChangeValueType() {
        String path = "fields";
        Gson gson = JsonUtil.getGson();
        PropField field = apa.savePropField(new PropField(ValueType.BOOLEAN, "TESTONE", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);

        PField pField = field.toClientField();
        pField.setValueType(ValueType.INTEGER.toString());
        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, gson.toJson(pField));
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
        response = tix.path(path + "/" + field.getId()).type("application/json").put(ClientResponse.class, gson.toJson(pField));
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));

    }

    @Test
    public void testChangeToDuplicateName() {
        String path = "fields";
        Gson gson = JsonUtil.getGson();
        PropField field = apa.savePropField(new PropField(ValueType.BOOLEAN, "TESTONE", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);

        PField pField = field.toClientField();
        pField.setValueType(ValueType.STRING.toString());
        pField.setStrict(StrictType.NOT_STRICT);
        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, gson.toJson(pField));
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
        response = tix.path(path + "/" + field.getId()).type("application/json").put(ClientResponse.class, gson.toJson(pField));
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));

    }
}
