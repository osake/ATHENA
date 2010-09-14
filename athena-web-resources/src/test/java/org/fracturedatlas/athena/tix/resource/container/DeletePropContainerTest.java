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

package org.fracturedatlas.athena.tix.resource.container;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import java.util.ArrayList;
import java.util.List;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.tix.util.BaseTixContainerTest;
import org.fracturedatlas.athena.tix.util.JsonUtil;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

public class DeletePropContainerTest extends BaseTixContainerTest {
    Gson gson = JsonUtil.getGson();
    
    public DeletePropContainerTest() throws Exception {
        super();
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
    public void testDeletePropDoesntExist() throws Exception {
        Ticket t = createSampleTicket(true);
        String path = "tickets/"+ t.getId() +"/props/FAKE_PROP.json";

        ClientResponse response = tix.path(path).type("application/json").delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testDeletePropTicketDoesntExist() throws Exception {
        Ticket t = createSampleTicket(true);
        String path = "tickets/949483/props/SEAT_NUMBER.json";

        ClientResponse response = tix.path(path).type("application/json").delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testDeleteProp() throws Exception {
        Ticket t = createSampleTicket(true);
        String path = "tickets/"+ t.getId() +"/props/SEAT_NUMBER.json";

        ClientResponse response = tix.path(path).type("application/json").delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NO_CONTENT, ClientResponse.Status.fromStatusCode(response.getStatus()));

        //check and make sure the prop is deleted
        t = apa.getTicket(t.getId());
        PTicket pTicket = t.toClientTicket();
        assertEquals(1, pTicket.getProps().size());
        assertEquals("ORCHESTRA", pTicket.get("SECTION"));
    }

    @Test
    public void testDeleteTwoProps() throws Exception {
        Ticket t = createSampleTicket(true);
        String path = "tickets/"+ t.getId() +"/props/SEAT_NUMBER.json";

        ClientResponse response = tix.path(path).type("application/json").delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NO_CONTENT, ClientResponse.Status.fromStatusCode(response.getStatus()));

        path = "tickets/"+ t.getId() +"/props/SECTION.json";

        response = tix.path(path).type("application/json").delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NO_CONTENT, ClientResponse.Status.fromStatusCode(response.getStatus()));

        //check and make sure the prop is deleted
        t = apa.getTicket(t.getId());
        PTicket pTicket = t.toClientTicket();
        assertEquals(0, pTicket.getProps().size());

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

        if(saveItToo) {
            t = apa.saveTicket(t);
            ticketsToDelete.add(t);
        }
        
        propFieldsToDelete.add(pf);
        propFieldsToDelete.add(pf2);

        return t;
    }
}
