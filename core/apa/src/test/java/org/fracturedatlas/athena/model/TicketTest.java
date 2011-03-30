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

package org.fracturedatlas.athena.model;

import org.fracturedatlas.athena.apa.BaseApaAdapterTest;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.StringTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;
import org.fracturedatlas.athena.apa.impl.jpa.TicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class TicketTest extends BaseApaAdapterTest {

    public TicketTest() throws Exception {
        super();
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

    @Test
    public void getTicketProp() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        JpaRecord ticket = new JpaRecord();
        ticket.setType("record");
        ticket.addTicketProp(new StringTicketProp(field, "03"));
        ticket.addTicketProp(new StringTicketProp(field1, "13"));
        ticket.addTicketProp(new StringTicketProp(field2, "23"));

        ticket = apa.saveTicket(ticket);
        ticketsToDelete.add(ticket);

        TicketProp prop = ticket.getTicketProp("SEAT1");
        assertEquals("13", prop.getValueAsString());
    }

    @Test
    public void setTicketProp() throws Exception {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        JpaRecord ticket = new JpaRecord();
        ticket.setType("record");
        TicketProp testProp = new StringTicketProp(field1, "13");
        ticket.addTicketProp(new StringTicketProp(field, "03"));
        ticket.addTicketProp(testProp);
        ticket.addTicketProp(new StringTicketProp(field2, "23"));

        ticket = apa.saveTicket(ticket);
        ticketsToDelete.add(ticket);

        TicketProp prop = ticket.getTicketProp("SEAT1");
        assertEquals("13", prop.getValueAsString());

        testProp.setValue("NEW_VALUE");
        ticket.setTicketProp(testProp);
        ticket = apa.saveTicket(ticket);
        prop = ticket.getTicketProp("SEAT1");
        assertEquals("NEW_VALUE", prop.getValueAsString());

        PTicket pTicket = ticket.toClientTicket();
        assertNotNull(pTicket.getId());
        assertEquals(3, pTicket.getProps().size());
        assertEquals("03", pTicket.get("SEAT"));
        assertEquals("NEW_VALUE", pTicket.get("SEAT1"));
        assertEquals("23", pTicket.get("SEAT2"));


    }

    @Test
    public void testEquals() throws Exception {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        JpaRecord ticket = new JpaRecord();
        ticket.setType("record");
        TicketProp testProp = new StringTicketProp(field1, "13");
        ticket.setTicketProp(new StringTicketProp(field, "03"));
        ticket.setTicketProp(testProp);
        ticket.setTicketProp(new StringTicketProp(field2, "23"));

        ticket = apa.saveTicket(ticket);
        ticketsToDelete.add(ticket);
        JpaRecord ticket2 = apa.getTicket(ticket.getType(), ticket.getId());
        assertTrue(ticket.equals(ticket2));
        assertTrue(ticket2.equals(ticket));

    }

    @Test
    public void testEqualsUnsavedTickets() throws Exception {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        JpaRecord ticket = new JpaRecord();
        ticket.setType("record");
        TicketProp testProp = new StringTicketProp(field1, "13");
        ticket.setTicketProp(new StringTicketProp(field, "03"));
        ticket.setTicketProp(testProp);
        ticket.setTicketProp(new StringTicketProp(field2, "23"));

        JpaRecord ticket2 = new JpaRecord();
        ticket2.setType("record");
        ticket2.setTicketProp(new StringTicketProp(field, "03"));
        ticket2.setTicketProp(testProp);
        ticket2.setTicketProp(new StringTicketProp(field2, "23"));

        assertTrue(ticket.equals(ticket2));
        assertTrue(ticket2.equals(ticket));

    }

    @Test
    public void testEqualNoProps() throws Exception {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        JpaRecord ticket = new JpaRecord();
        ticket.setType("record");

        JpaRecord ticket2 = new JpaRecord();
        ticket2.setType("record");

        ticket = apa.saveTicket(ticket);
        ticketsToDelete.add(ticket);
        ticket2 = apa.saveTicket(ticket2);
        ticketsToDelete.add(ticket2);
        assertFalse(ticket.equals(ticket2));
        assertFalse(ticket2.equals(ticket));

    }

    @Test
    public void testNotEqual() throws Exception {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        JpaRecord ticket = new JpaRecord();
        ticket.setType("record");
        TicketProp testProp = new StringTicketProp(field1, "13");
        ticket.setTicketProp(new StringTicketProp(field, "03"));
        ticket.setTicketProp(testProp);
        ticket.setTicketProp(new StringTicketProp(field2, "23"));

        JpaRecord ticket2 = new JpaRecord();
        ticket2.setType("record");
        ticket2.setTicketProp(new StringTicketProp(field, "03"));
        ticket2.setTicketProp(testProp);
        ticket2.setTicketProp(new StringTicketProp(field2, "23"));

        ticket = apa.saveTicket(ticket);
        ticketsToDelete.add(ticket);
        ticket2 = apa.saveTicket(ticket2);
        ticketsToDelete.add(ticket2);
        assertFalse(ticket.equals(ticket2));
        assertFalse(ticket2.equals(ticket));

    }

    @Test
    public void testNotEqualEditedName() throws Exception {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        JpaRecord ticket = new JpaRecord();
        ticket.setType("record");
        TicketProp testProp = new StringTicketProp(field1, "13");
        ticket.setTicketProp(new StringTicketProp(field, "03"));
        ticket.setTicketProp(testProp);
        ticket.setTicketProp(new StringTicketProp(field2, "23"));

        ticket = apa.saveTicket(ticket);
        JpaRecord savedTicket = apa.getTicket(ticket.getType(), ticket.getId());
        ticketsToDelete.add(savedTicket);
        ticket.setType("foo");
        assertFalse(ticket.equals(savedTicket));
        assertFalse(savedTicket.equals(ticket));

        JpaRecord newTicket = apa.saveTicket(ticket);

        assertTrue(newTicket.equals(ticket));
        assertTrue(ticket.equals(newTicket));

    }

    @Test
    public void testNotEqualEditedProp() throws Exception {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        JpaRecord ticket = new JpaRecord();
        ticket.setType("record");
        TicketProp testProp = new StringTicketProp(field1, "13");
        ticket.setTicketProp(new StringTicketProp(field, "03"));
        ticket.setTicketProp(testProp);
        ticket.setTicketProp(new StringTicketProp(field2, "23"));

        ticket = apa.saveTicket(ticket);
        JpaRecord savedTicket = apa.getTicket(ticket.getType(), ticket.getId());
        ticketsToDelete.add(savedTicket);
        ticket.setTicketProp(new StringTicketProp(field2, "123"));
        assertFalse(ticket.equals(savedTicket));
        assertFalse(savedTicket.equals(ticket));

        JpaRecord newTicket = apa.saveTicket(ticket);

        assertTrue(newTicket.equals(ticket));
        assertTrue(ticket.equals(newTicket));

    }

}
