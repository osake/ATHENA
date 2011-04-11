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
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.StringTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
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
    PTicket t1 = null;
    PTicket t2 = null;
    PTicket t3 = null;
    PTicket t4 = null;

    @Test
    public void testFindTicketsByRelationship() {
        path = "/companies/" + IdAdapter.toString(t1.getId()) + "/employees";
        String jsonString = tix.path(path).get(String.class);
        PTicket[] tickets = gson.fromJson(jsonString, PTicket[].class);
        assertEquals(3, tickets.length);

        List<PTicket> ticketList = Arrays.asList(tickets);
        for(PTicket t : ticketList) {
            if("Jim".equals(t.get("name"))) {
                assertRecordsEqual(t2, t, true);
            } else if("Bill".equals(t.get("name"))) {
                assertRecordsEqual(t3, t, true);
            } else if("Joe".equals(t.get("name"))) {
                assertRecordsEqual(t4, t, true);
            } else {
                fail("Found a ticket that I shouldn't have: " + t);
            }
        }
    }

    @Test
    public void testFindTicketsNotFound() {
        assertNotFound("/companies/0/employees");
    }

    @Test
    public void testFindTicketsNoRelationship() {
        assertNotFound("/employees/" + IdAdapter.toString(t3.getId()) + "/companies");
    }

    @Test
    public void testFindTicketsUnknownRelationship() {
        assertNotFound("/employees/" + IdAdapter.toString(t3.getId()) + "/NOT_A_REAL_RELATION");
    }

    @Test
    public void testFindTicketsUnknownRelationship2() {
        assertNotFound("/FAKE/" + IdAdapter.toString(t3.getId()) + "/NOT_A_REAL_RELATION");
    }

    @Before
    public void addTickets() throws Exception {
        t1 = new PTicket();
        t2 = new PTicket();
        t3 = new PTicket();
        t4 = new PTicket();

        t1.setType("company");
        t2.setType("employee");
        t3.setType("employee");
        t4.setType("employee");

        addPropField(ValueType.STRING, "name", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "companyId", StrictType.NOT_STRICT);


        t1 = addRecord("company",
                       "name", "Initrode");
        t2 = addRecord("employee",
                       "name", "Jim",
                       "companyId", IdAdapter.toString(t1.getId()));
        t3 = addRecord("employee",
                       "name", "Bill",
                       "companyId", IdAdapter.toString(t1.getId()));
        t4 = addRecord("employee",
                       "name", "Joe",
                       "companyId", IdAdapter.toString(t1.getId()));
    }

    @After
    public void teardownRecords() {
        super.teardownRecords();
    }
}
