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
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PField;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

/*
 * Fields are mostly immutable.  You can't change the id, name, type, or strictnes after it is created.
 * Most of this is tested in FieldResourceImmutableContainerLongNameTest
 */
public class PutFieldContainerTest extends BaseTixContainerTest {

    Gson gson = JsonUtil.getGson();
    String path = FIELDS_PATH;

    public PutFieldContainerTest() throws Exception {
        super();
    }

    @After
    public void teardownRecords() {
        super.teardownRecords();
    }

    @Test
    public void testPutFieldWithoutId() throws Exception {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "TEST", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field);

        PField pField = field.toClientField();
        pField.setValueType(ValueType.STRING.toString());
        pField.setStrict(StrictType.NOT_STRICT);
        ClientResponse response = tix.path(path).type("application/json").put(ClientResponse.class, gson.toJson(pField));
        assertEquals(ClientResponse.Status.METHOD_NOT_ALLOWED, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testPutFieldBadIdInBody() throws Exception {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "TEST", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field);

        PField pField = field.toClientField();
        pField.setId("40404");
        ClientResponse response = tix.path(path + field.getId()).type("application/json").put(ClientResponse.class, gson.toJson(pField));
        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testPutFieldBadIdOnURI() throws Exception {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "TEST", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field);

        PField pField = field.toClientField();
        ClientResponse response = tix.path(path + "/0000.json").type("application/json").put(ClientResponse.class, gson.toJson(pField));
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }
}
