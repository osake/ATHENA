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

import java.util.Set;
import org.fracturedatlas.athena.apa.exception.InvalidPropException;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;


public class ApaAdapterSaveTicketsTest extends BaseApaAdapterTest {

    public ApaAdapterSaveTicketsTest() throws Exception {
        super();
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

    @Test
    public void testSaveTicket() {
        addPropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT);

        PTicket ticket = new PTicket();
        ticket.setType("record");
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");

        ticket = apa.saveRecord(ticket);
        assertNotNull(ticket.getId());
        ticketsToDelete.add(ticket);

        ticket = apa.getRecord(ticket.getType(), ticket.getId());

        assertNotNull(ticket.getId());
        assertNotNull(ticket.getIdAsString());
        assertEquals(3, ticket.getProps().size());
        assertEquals("03", ticket.get("SEAT"));
        assertEquals("13", ticket.get("SEAT1"));
        assertEquals("23", ticket.get("SEAT2"));

    }

    @Test
    public void testSaveTicketIncorrectType() {
        addPropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT);
        addPropField(ValueType.DATETIME, "SEAT2", StrictType.NOT_STRICT);

        PTicket ticket = new PTicket();
        ticket.setType("record");
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "notadate");

        try{
            ticket = apa.saveRecord(ticket);
            fail("Needed Apa Exception");
        } catch (InvalidValueException e) {
            //pass
        }

    }

    @Test
    public void testUpdateTicket() {
        addPropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT);

        PTicket ticket = new PTicket();
        ticket.setType("record");
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");

        ticket = apa.saveRecord(ticket);
        assertNotNull(ticket.getId());
        ticketsToDelete.add(ticket);

        ticket.put("SEAT", "ELEPHANT");

        PTicket savedTicket = apa.saveRecord(ticket);
        assertEquals(savedTicket.get("SEAT"), "ELEPHANT");
        assertEquals(ticket, savedTicket);
    }

    @Test
    public void testSaveTicketUnknownField() {
        addPropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT);
        addPropField(ValueType.INTEGER, "SEAT2", StrictType.NOT_STRICT);

        PTicket ticket = new PTicket();
        ticket.setType("record");
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("UNKNOWN_PROP", "23");

        try{
            ticket = apa.saveRecord(ticket);
            fail("Needed Apa Exception");
        } catch (InvalidPropException e) {
            //pass
        }
    }

    @Test
    public void testUpdateTicketUnknownProp() {
        addPropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT);

        PTicket ticket = new PTicket();
        ticket.setType("record");
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");

        ticket = apa.saveRecord(ticket);
        assertNotNull(ticket.getId());
        ticketsToDelete.add(ticket);
        ticket.put("UNKNOWN_PROP", "23");

        try{
            ticket = apa.saveRecord(ticket);
            fail("Needed Apa Exception");
        } catch (InvalidPropException e) {
            //pass
        }
    }

    @Test
    public void testUpdateTicketIncorrectType() {
        addPropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT);
        addPropField(ValueType.INTEGER, "SEAT2", StrictType.NOT_STRICT);

        PTicket ticket = new PTicket();
        ticket.setType("record");
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");

        ticket = apa.saveRecord(ticket);
        assertNotNull(ticket.getId());
        ticketsToDelete.add(ticket);

        ticket.put("SEAT2", "ELEPHANT");

        try{
            ticket = apa.saveRecord(ticket);
            fail("Needed Apa Exception");
        } catch (InvalidValueException e) {
            //pass
        }
    }

    @Test
    public void testUpdateTicketRemoveProp() {
        addPropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT);

        PTicket ticket = new PTicket();
        ticket.setType("record");
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");

        ticket = apa.saveRecord(ticket);
        assertNotNull(ticket.getId());
        ticketsToDelete.add(ticket);

        ticket.deleteProperty("SEAT");

        PTicket savedTicket = apa.saveRecord(ticket);
        assertNull(savedTicket.get("SEAT"));
        assertEquals(ticket, savedTicket);
    }

    @Test
    public void testSaveTwoTicketsSameType() {
        addPropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT);

        PTicket ticket = new PTicket();
        ticket.setType("record");
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");

        ticket = apa.saveRecord(ticket);
        assertNotNull(ticket.getId());
        ticketsToDelete.add(ticket);

        ticket = apa.getRecord(ticket.getType(), ticket.getId());

        assertNotNull(ticket.getId());
        assertEquals(3, ticket.getProps().size());
        assertEquals("03", ticket.get("SEAT"));
        assertEquals("13", ticket.get("SEAT1"));
        assertEquals("23", ticket.get("SEAT2"));


        PTicket ticket2 = new PTicket();
        ticket2.setType("record");
        ticket2.put("SEAT", "033");
        ticket2.put("SEAT1", "133");
        ticket2.put("SEAT2", "233");

        ticket2 = apa.saveRecord(ticket2);
        ticketsToDelete.add(ticket2);
        ticket = apa.getRecord(ticket2.getType(), ticket2.getId());

        assertNotNull(ticket.getId());
        assertEquals(3, ticket.getProps().size());
        assertEquals("033", ticket.get("SEAT"));
        assertEquals("133", ticket.get("SEAT1"));
        assertEquals("233", ticket.get("SEAT2"));
    }

    @Test
    public void testSaveTwoTicketsDifferentTypes() {
        addPropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT);

        PTicket ticket = new PTicket();
        ticket.setType("record");
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");

        ticket = apa.saveRecord(ticket);
        assertNotNull(ticket.getId());
        ticketsToDelete.add(ticket);

        ticket = apa.getRecord(ticket.getType(), ticket.getId());

        assertNotNull(ticket.getId());
        assertEquals(3, ticket.getProps().size());
        assertEquals("03", ticket.get("SEAT"));
        assertEquals("13", ticket.get("SEAT1"));
        assertEquals("23", ticket.get("SEAT2"));


        PTicket ticket2 = new PTicket();
        ticket2.setType("performance");
        ticket2.put("SEAT", "033");
        ticket2.put("SEAT1", "133");
        ticket2.put("SEAT2", "233");

        ticket2 = apa.saveRecord(ticket2);
        ticketsToDelete.add(ticket2);
        ticket = apa.getRecord(ticket2.getType(), ticket2.getId());

        assertNotNull(ticket.getId());
        assertEquals(3, ticket.getProps().size());
        assertEquals("033", ticket.get("SEAT"));
        assertEquals("133", ticket.get("SEAT1"));
        assertEquals("233", ticket.get("SEAT2"));
    }

    @Test
    public void testGetTypes() {
        addPropField(ValueType.STRING, "firstName", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "lastName", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "occupation", StrictType.NOT_STRICT);
        addRecord("person",
                  "firstName", "Ben",
                  "lastName", "Bernake",
                  "occupation", "banker");
        addRecord("octopus",
                  "firstName", "Ben",
                  "lastName", "Affleck",
                  "occupation", "actor");
        addRecord("airplane",
                  "firstName", "Matt",
                  "lastName", "Damon",
                  "occupation", "actor");
        Set<String> types = apa.getTypes();
        assertEquals(3, types.size());
        assertTrue(types.contains("person"));
        assertTrue(types.contains("octopus"));
        assertTrue(types.contains("airplane"));
    }
}
