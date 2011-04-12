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
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RelationshipHelperContainerTest extends BaseContainerTest {

    Gson gson = new Gson();

    String path;

    PTicket jimmy = null;
    PTicket bobby = null;
    PTicket t3 = null;
    PTicket t4 = null;
    PTicket t5 = null;

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
        jimmy = new PTicket("person");
        bobby = new PTicket("person");
        t3 = new PTicket("relationship");
        t4 = new PTicket("relationship");
        t5 = new PTicket("relationship");

        addPropField(ValueType.STRING, "leftSideId", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "relationshipType", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "rightSideId", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "inverseType", StrictType.NOT_STRICT);

        jimmy = apa.saveRecord(jimmy);
        bobby = apa.saveRecord(bobby);

        /*
         * Jimmy is left side on one relationship
         * Bobby is left isde on one relationship and right side on two
         */
        t3.put("leftSideId", IdAdapter.toString(jimmy.getId()));
        t3.put("relationshipType", "father");
        t3.put("rightSideId", IdAdapter.toString(bobby.getId()));
        t3.put("inverseType", "son");

        t4.put("leftSideId", "SOME_ID");
        t4.put("relationshipType", "boss");
        t4.put("rightSideId", IdAdapter.toString(bobby.getId()));
        t4.put("inverseType", "subordinate");

        t5.put("leftSideId", IdAdapter.toString(bobby.getId()));
        t5.put("relationshipType", "husband");
        t5.put("rightSideId", "WIFEY");
        t5.put("inverseType", "wife");

        t3 = apa.saveRecord(t3);
        t4 = apa.saveRecord(t4);
        t5 = apa.saveRecord(t5);

        recordsToDelete.add(jimmy);
        recordsToDelete.add(bobby);
        recordsToDelete.add(t3);
        recordsToDelete.add(t4);
        recordsToDelete.add(t5);
    }

    @After
    public void teardown() {
        super.teardownRecords();
    }
}
