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

package org.fracturedatlas.athena.apa;

import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class ApaAdapterSystemPropsTest extends BaseApaAdapterTest {

    public ApaAdapterSystemPropsTest() throws Exception {
        super();
    }

    @After
    public void teardown() {
        super.teardownTickets();
    }

    @Test
    public void testSaveTicket() {
        addPropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SOME_APP:SEAT2", StrictType.NOT_STRICT);

        PTicket ticket = new PTicket();
        ticket.setType("record");
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");
        ticket.getSystemProps().putSingle("SOME_APP:SEAT2", "FOO");

        ticket = apa.saveRecord(ticket);
        assertNotNull(ticket.getId());
        ticketsToDelete.add(ticket);

        ticket = apa.getRecord(ticket.getType(), ticket.getId());

        assertNotNull(ticket.getId());
        assertEquals(3, ticket.getProps().size());
        assertEquals("03", ticket.get("SEAT"));
        assertEquals("13", ticket.get("SEAT1"));
        assertEquals("23", ticket.get("SEAT2"));

        assertEquals(1, ticket.getSystemProps().size());
        assertEquals("FOO", ticket.getSystemProps().getFirst("SOME_APP:SEAT2"));

    }

    @Test
    public void testUpdateTicket() {
        addPropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SOME_APP:SEAT2", StrictType.NOT_STRICT);

        PTicket ticket = new PTicket();
        ticket.setType("record");
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");

        ticket = apa.saveRecord(ticket);
        assertEquals(0, ticket.getSystemProps().size());
        assertNotNull(ticket.getId());
        ticketsToDelete.add(ticket);

        ticket.put("SEAT", "ELEPHANT");
        ticket.getSystemProps().putSingle("SOME_APP:SEAT2", "FOO");

        PTicket savedTicket = apa.saveRecord(ticket);
        assertEquals(savedTicket.get("SEAT"), "ELEPHANT");
        assertEquals(ticket, savedTicket);
        assertEquals(1, ticket.getSystemProps().size());
        assertEquals("FOO", ticket.getSystemProps().getFirst("SOME_APP:SEAT2"));
    }
}
