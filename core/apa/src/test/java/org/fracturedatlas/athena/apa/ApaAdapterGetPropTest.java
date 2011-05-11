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

import java.util.List;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.StringTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.TicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ApaAdapterGetPropTest extends BaseApaAdapterTest {

    PropField field;
    PropField field1;
    PropField field2;
    PropField field3;
    PTicket ticket;

    public ApaAdapterGetPropTest() throws Exception {
        super();
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

    @Before
    public void addTicket() {
        field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));
        field3 = apa.savePropField(new PropField(ValueType.STRING, "SEAT_ARRAY", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);
        propFieldsToDelete.add(field3);

        ticket = new PTicket();
        ticket.setType("hockey");

        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");
        ticket.getProps().add("SEAT_ARRAY", "ARY1");
        ticket.getProps().add("SEAT_ARRAY", "ARY2");

        ticket = apa.saveRecord(ticket);

        ticketsToDelete.add(ticket);
    }

    @Test
    public void testFindProp() {
        TicketProp prop = apa.getTicketProp(field.getName(), ticket.getType(), ticket.getId());
        assertNotNull(prop);
        assertEquals(field.getName(), prop.getPropField().getName());
        assertEquals(field.getValueType(), prop.getPropField().getValueType());
        assertEquals("03", prop.getValue());
        assertTrue(IdAdapter.isEqual(ticket.getId(), prop.getTicket().getId()));

        prop = apa.getTicketProp(field1.getName(), ticket.getType(), ticket.getId());
        assertNotNull(prop);
        assertEquals(field1.getName(), prop.getPropField().getName());
        assertEquals(field1.getValueType(), prop.getPropField().getValueType());
        assertEquals("13", prop.getValue());
        assertTrue(IdAdapter.isEqual(ticket.getId(), prop.getTicket().getId()));

        prop = apa.getTicketProp(field2.getName(), ticket.getType(), ticket.getId());
        assertNotNull(prop);
        assertEquals(field2.getName(), prop.getPropField().getName());
        assertEquals(field2.getValueType(), prop.getPropField().getValueType());
        assertEquals("23", prop.getValue());
        assertTrue(IdAdapter.isEqual(ticket.getId(), prop.getTicket().getId()));


        List<TicketProp> props = apa.getTicketProps(field3.getName(), ticket.getType(), ticket.getId());
        assertNotNull(props);
        assertEquals(field3.getName(), props.get(0).getPropField().getName());
        assertEquals(field3.getValueType(), props.get(0).getPropField().getValueType());
        assertEquals(2, props.size());
        assertTrue(IdAdapter.isEqual(ticket.getId(), props.get(0).getTicket().getId()));
    }

    @Test
    public void testFindPropDoesntExist() {
        TicketProp prop = apa.getTicketProp("NO_CHANCE", ticket.getType(), ticket.getId());
        assertNull(prop);
    }

    @Test
    public void testFindPropTicketDoesntExist() {
        TicketProp prop = apa.getTicketProp("SEAT", ticket.getType(), 5665L);
        assertNull(prop);
    }

    @Test
    public void testFindPropNullTicket() {
        TicketProp prop = apa.getTicketProp("SEAT", ticket.getType(), null);
        assertNull(prop);
    }
}
