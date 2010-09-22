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

import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StrictType;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.TicketProp;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class ApaAdapterGetPropTest extends BaseApaAdapterTest {

    public ApaAdapterGetPropTest() throws Exception {
        super();
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

    @Test
    public void testFindProp() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));

        Ticket ticket = new Ticket();
        ticket.setName("hockey");
        ticket.addTicketProp(new StringTicketProp(field, "03"));
        ticket.addTicketProp(new StringTicketProp(field1, "13"));
        ticket.addTicketProp(new StringTicketProp(field2, "23"));
        ticketsToDelete.add(ticket);
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        ticket = apa.saveTicket(ticket);

        TicketProp prop = apa.getTicketProp(field.getName(), ticket.getId());
        assertNotNull(prop);
        assertEquals(field.getName(), prop.getPropField().getName());
        assertEquals(field.getValueType(), prop.getPropField().getValueType());
        assertEquals("03", prop.getValue());
        assertEquals(ticket.getId(), prop.getTicket().getId());

        prop = apa.getTicketProp(field1.getName(), ticket.getId());
        assertNotNull(prop);
        assertEquals(field1.getName(), prop.getPropField().getName());
        assertEquals(field1.getValueType(), prop.getPropField().getValueType());
        assertEquals("13", prop.getValue());
        assertEquals(ticket.getId(), prop.getTicket().getId());

        prop = apa.getTicketProp(field2.getName(), ticket.getId());
        assertNotNull(prop);
        assertEquals(field2.getName(), prop.getPropField().getName());
        assertEquals(field2.getValueType(), prop.getPropField().getValueType());
        assertEquals("23", prop.getValue());
        assertEquals(ticket.getId(), prop.getTicket().getId());

    }

    @Test
    public void testFindPropDoesntExist() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));

        Ticket ticket = new Ticket();
        ticket.setName("hockey");
        ticket.addTicketProp(new StringTicketProp(field, "03"));
        ticket.addTicketProp(new StringTicketProp(field1, "13"));
        ticket.addTicketProp(new StringTicketProp(field2, "23"));
        ticketsToDelete.add(ticket);
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        ticket = apa.saveTicket(ticket);

        TicketProp prop = apa.getTicketProp("NO_CHANCE", ticket.getId());
        assertNull(prop);
    }

    @Test
    public void testFindPropTicketDoesntExist() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));

        Ticket ticket = new Ticket();
        ticket.setName("hockey");
        ticket.addTicketProp(new StringTicketProp(field, "03"));
        ticket.addTicketProp(new StringTicketProp(field1, "13"));
        ticket.addTicketProp(new StringTicketProp(field2, "23"));
        ticketsToDelete.add(ticket);
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        ticket = apa.saveTicket(ticket);

        TicketProp prop = apa.getTicketProp("SEAT", null);
        assertNull(prop);
    }
}
