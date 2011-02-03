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
import com.google.gson.Gson;
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

    Gson gson = new Gson();

    String path;

    Ticket jimmy = null;
    Ticket bobby = null;
    Ticket t3 = null;
    Ticket t4 = null;
    Ticket t5 = null;

    @Test
    public void testFindTicketsByRelationship() {
        path = "/meta/relationships/people/" + IdAdapter.toString(jimmy.getId());
        String jsonString = tix.path(path).get(String.class);
        PTicket[] tickets = gson.fromJson(jsonString, PTicket[].class);
        assertEquals(1, tickets.length);

    }

    @Test
    public void testFindTicketsByRelationship2() {
        path = "/meta/relationships/people/" + IdAdapter.toString(bobby.getId());
        String jsonString = tix.path(path).get(String.class);
        PTicket[] tickets = gson.fromJson(jsonString, PTicket[].class);
        assertEquals(3, tickets.length);

    }

    @Test
    public void testFindTicketsByRelationshipNone() {
        path = "/meta/relationships/people/0";
        String jsonString = tix.path(path).get(String.class);
        PTicket[] tickets = gson.fromJson(jsonString, PTicket[].class);
        assertEquals(0, tickets.length);
    }

    @Test
    public void testFindTicketsByRelationshipUnknownType() {
        path = "/meta/relationships/monkeys/0";
        String jsonString = tix.path(path).get(String.class);
        PTicket[] tickets = gson.fromJson(jsonString, PTicket[].class);
        assertEquals(0, tickets.length);
    }

    @Before
    public void addTickets() throws Exception {
        jimmy = new Ticket();
        bobby = new Ticket();
        t3 = new Ticket();
        t4 = new Ticket();
        t5 = new Ticket();

        jimmy.setType("person");
        bobby.setType("person");
        t3.setType("relationship");
        t4.setType("relationship");
        t5.setType("relationship");

        PropField leftSideIdProp = apa.savePropField(new PropField(ValueType.STRING, "leftSideId", StrictType.NOT_STRICT));
        PropField relationshipTypeProp = apa.savePropField(new PropField(ValueType.STRING, "relationshipType", StrictType.NOT_STRICT));
        PropField rightSideIdProp = apa.savePropField(new PropField(ValueType.STRING, "rightSideId", StrictType.NOT_STRICT));
        PropField inverseTypeProp = apa.savePropField(new PropField(ValueType.STRING, "inverseType", StrictType.NOT_STRICT));

        propFieldsToDelete.add(leftSideIdProp);
        propFieldsToDelete.add(relationshipTypeProp);
        propFieldsToDelete.add(rightSideIdProp);
        propFieldsToDelete.add(inverseTypeProp);

        jimmy = apa.saveTicket(jimmy);
        bobby = apa.saveTicket(bobby);

        /*
         * Jimmy is left side on one relationship
         * Bobby is left isde on one relationship and right side on two
         */
        t3.addTicketProp(new StringTicketProp(leftSideIdProp, IdAdapter.toString(jimmy.getId())));
        t3.addTicketProp(new StringTicketProp(relationshipTypeProp, "father"));
        t3.addTicketProp(new StringTicketProp(rightSideIdProp, IdAdapter.toString(bobby.getId())));
        t3.addTicketProp(new StringTicketProp(inverseTypeProp, "son"));

        t4.addTicketProp(new StringTicketProp(leftSideIdProp, "SOME_ID"));
        t4.addTicketProp(new StringTicketProp(relationshipTypeProp, "boss"));
        t4.addTicketProp(new StringTicketProp(rightSideIdProp, IdAdapter.toString(bobby.getId())));
        t4.addTicketProp(new StringTicketProp(inverseTypeProp, "subordinate"));

        t5.addTicketProp(new StringTicketProp(leftSideIdProp, IdAdapter.toString(bobby.getId())));
        t5.addTicketProp(new StringTicketProp(relationshipTypeProp, "husband"));
        t5.addTicketProp(new StringTicketProp(rightSideIdProp, "WIFEY"));
        t5.addTicketProp(new StringTicketProp(inverseTypeProp, "wife"));

        t3 = apa.saveTicket(t3);
        t4 = apa.saveTicket(t4);
        t5 = apa.saveTicket(t5);

        ticketsToDelete.add(jimmy);
        ticketsToDelete.add(bobby);
        ticketsToDelete.add(t3);
        ticketsToDelete.add(t4);
        ticketsToDelete.add(t5);
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }
}
