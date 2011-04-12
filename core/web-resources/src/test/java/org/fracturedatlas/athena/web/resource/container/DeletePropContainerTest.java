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
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class DeletePropContainerTest extends BaseTixContainerTest {
    Gson gson = JsonUtil.getGson();
    
    public DeletePropContainerTest() throws Exception {
        super();
    }

    @After
    public void teardownRecords() {
        super.teardownRecords();
    }

    @Test
    public void testDeletePropDoesntExist() throws Exception {
        PTicket t = createSampleTicket(true);
        String path = RECORDS_PATH + t.getId() +"/props/FAKE_PROP.json";

        ClientResponse response = tix.path(path).type("application/json").delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testDeletePropTicketDoesntExist() throws Exception {
        PTicket t = createSampleTicket(true);
        String path = RECORDS_PATH + "0/props/SEAT_NUMBER.json";

        ClientResponse response = tix.path(path).type("application/json").delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testDeleteProp() throws Exception {
        PTicket t = createSampleTicket(true);
        String path = RECORDS_PATH + t.getId() +"/props/SEAT_NUMBER.json";

        ClientResponse response = tix.path(path).type("application/json").delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NO_CONTENT, ClientResponse.Status.fromStatusCode(response.getStatus()));

        //check and make sure the prop is deleted
        t = apa.getRecord(t.getType(), t.getId());
        assertEquals(1, t.getProps().size());
        assertEquals("ORCHESTRA", t.get("SECTION"));
    }

    @Test
    public void testDeleteTwoProps() throws Exception {
        PTicket t = createSampleTicket(true);
        String path = RECORDS_PATH + t.getId() +"/props/SEAT_NUMBER.json";

        ClientResponse response = tix.path(path).type("application/json").delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NO_CONTENT, ClientResponse.Status.fromStatusCode(response.getStatus()));

        path = RECORDS_PATH + t.getId() +"/props/SECTION.json";

        response = tix.path(path).type("application/json").delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NO_CONTENT, ClientResponse.Status.fromStatusCode(response.getStatus()));

        //check and make sure the prop is deleted
        t = apa.getRecord(t.getType(), t.getId());
        assertEquals(0, t.getProps().size());

    }
    
    public PTicket createSampleTicket(Boolean saveItToo) {
        PTicket t = new PTicket();
        t.setType("ticket");

        addPropField(ValueType.STRING,"SEAT_NUMBER",Boolean.FALSE);
        addPropField(ValueType.STRING,"SECTION",Boolean.FALSE);
        t.put("SEAT_NUMBER", "3D");
        t.put("SECTION", "ORCHESTRA");
        if(saveItToo) {
            t = apa.saveRecord(t);
            recordsToDelete.add(t);
        }

        return t;
    }
}
