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
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StrictType;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/*
 * Just a few container tests around relationships.  Most functionality
 * is tested in non-container tests
 */
public class RelationshipContainerTest extends BaseTixContainerTest {

    String path = RECORDS_PATH;
    Gson gson = JsonUtil.getGson();
    Ticket t1 = null;
    Ticket t2 = null;
    Ticket t3 = null;
    Ticket t4 = null;

    @Test
    public void testFindTicketsByRelationship() {
        path = "/companies/" + IdAdapter.toString(t1.getId()) + "/employees";
        String jsonString = tix.path(path).get(String.class);
        PTicket[] tickets = gson.fromJson(jsonString, PTicket[].class);
        assertEquals(3, tickets.length);

        List<PTicket> ticketList = Arrays.asList(tickets);
        for(PTicket t : ticketList) {
            if("Jim".equals(t.get("name"))) {
                assertTicketsEqual(t2, t);
            } else if("Bill".equals(t.get("name"))) {
                assertTicketsEqual(t3, t);
            } else if("Joe".equals(t.get("name"))) {
                assertTicketsEqual(t4, t);
            } else {
                fail("Found a ticket that I shouldn't have: " + t);
            }
        }
    }

    @Test
    public void testFindTicketsNotFound() {
        path = "/companies/0/employees";
        ClientResponse response = tix.path(path).get(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void testFindTicketsNoRelationship() {
        path = "/employees/" + IdAdapter.toString(t3.getId()) + "/companies";
        String jsonString = tix.path(path).get(String.class);
        PTicket[] tickets = gson.fromJson(jsonString, PTicket[].class);
        assertEquals(0, tickets.length);
    }

    @Test
    public void testFindTicketsUnknownRelationship() {
        path = "/employees/" + IdAdapter.toString(t3.getId()) + "/NOT_A_REAL_RELATION";
        String jsonString = tix.path(path).get(String.class);
        PTicket[] tickets = gson.fromJson(jsonString, PTicket[].class);
        assertEquals(0, tickets.length);
    }

    @Test
    public void testFindTicketsUnknownRelationship2() {
        path = "/FAKE/" + IdAdapter.toString(t3.getId()) + "/NOT_A_REAL_RELATION";
        ClientResponse response = tix.path(path).get(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Before
    public void addTickets() throws Exception {
        t1 = new Ticket();
        t2 = new Ticket();
        t3 = new Ticket();
        t4 = new Ticket();

        t1.setType("company");
        t2.setType("employee");
        t3.setType("employee");
        t4.setType("employee");

        PropField nameProp = apa.savePropField(new PropField(ValueType.STRING, "name", StrictType.NOT_STRICT));
        PropField companyIdProp = apa.savePropField(new PropField(ValueType.STRING, "companyId", StrictType.NOT_STRICT));


        propFieldsToDelete.add(nameProp);
        propFieldsToDelete.add(companyIdProp);

        t1.addTicketProp(new StringTicketProp(nameProp, "Initrode"));
        t1 = apa.saveTicket(t1);

        t2.addTicketProp(new StringTicketProp(nameProp, "Jim"));
        t2.addTicketProp(new StringTicketProp(companyIdProp, IdAdapter.toString(t1.getId())));

        t3.addTicketProp(new StringTicketProp(nameProp, "Bill"));
        t3.addTicketProp(new StringTicketProp(companyIdProp, IdAdapter.toString(t1.getId())));
        t4.addTicketProp(new StringTicketProp(nameProp, "Joe"));
        t4.addTicketProp(new StringTicketProp(companyIdProp, IdAdapter.toString(t1.getId())));
        
        t2 = apa.saveTicket(t2);
        t3 = apa.saveTicket(t3);
        t4 = apa.saveTicket(t4);

        ticketsToDelete.add(t1);
        ticketsToDelete.add(t2);
        ticketsToDelete.add(t3);
        ticketsToDelete.add(t4);
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

}
