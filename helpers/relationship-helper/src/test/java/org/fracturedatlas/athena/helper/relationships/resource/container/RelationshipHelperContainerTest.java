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

package org.fracturedatlas.athena.helper.relationships.resource.container;
import com.sun.jersey.api.client.ClientResponse;
import java.util.Arrays;
import java.util.List;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StrictType;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RelationshipHelperContainerTest extends BaseContainerTest {

    String path;

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

//    @Test
//    public void testFindTicketsNotFound() {
//        path = "/companies/0/employees";
//        ClientResponse response = tix.path(path).get(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }

//    @Test
//    public void testFindTicketsNoRelationship() {
//        path = "/employees/" + IdAdapter.toString(t3.getId()) + "/companies";
//        String jsonString = tix.path(path).get(String.class);
//        PTicket[] tickets = gson.fromJson(jsonString, PTicket[].class);
//        assertEquals(0, tickets.length);
//    }

    @Before
    public void addTickets() throws Exception {
        t1 = new Ticket();
        t2 = new Ticket();
        t3 = new Ticket();
        t4 = new Ticket();

        t1.setType("person");
        t2.setType("person");
        t3.setType("relationship");
        t4.setType("relationship");

        PropField leftSideIdProp = apa.savePropField(new PropField(ValueType.STRING, "leftSideId", StrictType.NOT_STRICT));
        PropField relationshipTypeProp = apa.savePropField(new PropField(ValueType.STRING, "relationshipType", StrictType.NOT_STRICT));
        PropField rightSideIdProp = apa.savePropField(new PropField(ValueType.STRING, "rightSideId", StrictType.NOT_STRICT));
        PropField inverseTypeProp = apa.savePropField(new PropField(ValueType.STRING, "inverseType", StrictType.NOT_STRICT));


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

    @Test
    public void testGetRelationships() throws Exception {

    }

}
