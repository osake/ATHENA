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

import org.fracturedatlas.athena.apa.exception.ApaException;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StrictType;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.TicketProp;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class ApaAdapterDeletePropTest extends BaseApaAdapterTest {
    public ApaAdapterDeletePropTest() throws Exception {
        super();
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

    @Test
    public void testDeleteProp() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        Ticket ticket = new Ticket();
        ticket.setType("hockey");
        TicketProp seatProp = new StringTicketProp(field, "03");

        //deleting SEAT
        ticket.addTicketProp(seatProp);
        ticket.addTicketProp(new StringTicketProp(field1, "13"));
        ticket.addTicketProp(new StringTicketProp(field2, "23"));

        ticket = apa.saveTicket(ticket);
        ticketsToDelete.add(ticket);

        seatProp = apa.getTicketProp("SEAT", ticket.getId());
        apa.deleteTicketProp(seatProp);

        ticket = apa.getTicket(ticket.getType(), ticket.getId());
        assertEquals(2, ticket.getTicketProps().size());

        for(TicketProp prop : ticket.getTicketProps()) {
            if(seatProp.getPropField().getName().equals(prop.getPropField().getName())) {
                fail("Should have been deleted");
            } else if ("SEAT1".equals(prop.getPropField().getName())) {
                assertEquals(field1.getName(), prop.getPropField().getName());
                assertEquals(field1.getValueType(), prop.getPropField().getValueType());
                assertEquals("13", prop.getValue());
                assertEquals(ticket.getId(), prop.getTicket().getId());
            } else if ("SEAT2".equals(prop.getPropField().getName())) {
                assertEquals(field2.getName(), prop.getPropField().getName());
                assertEquals(field2.getValueType(), prop.getPropField().getValueType());
                assertEquals("23", prop.getValue());
                assertEquals(ticket.getId(), prop.getTicket().getId());
            } else {
                fail("Found a prop that should not be here");
            }
        }
    }

    @Test
    public void testDeleteProp2() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));

        Ticket ticket = new Ticket();
        ticket.setType("hockey");
        TicketProp seatProp = new StringTicketProp(field, "03");

        //deleting SEAT
        ticket.addTicketProp(seatProp);
        ticket.addTicketProp(new StringTicketProp(field1, "13"));
        ticket.addTicketProp(new StringTicketProp(field2, "23"));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        ticket = apa.saveTicket(ticket);


        Ticket ticket2 = new Ticket();
        ticket2.setType("dos");
        ticket2.addTicketProp(new StringTicketProp(field, "ddd"));
        ticket2.addTicketProp(new StringTicketProp(field1, "fff"));
        ticket2.addTicketProp(new StringTicketProp(field2, "ggg"));

        ticket = apa.saveTicket(ticket);
        ticketsToDelete.add(ticket);
        ticket2 = apa.saveTicket(ticket2);
        ticketsToDelete.add(ticket2);

        seatProp = apa.getTicketProp("SEAT", ticket.getId());
        apa.deleteTicketProp(seatProp);

        ticket = apa.getTicket(ticket.getType(), ticket.getId());
        assertEquals(2, ticket.getTicketProps().size());

        for(TicketProp prop : ticket.getTicketProps()) {
            if(seatProp.getPropField().getName().equals(prop.getPropField().getName())) {
                fail("Should have been deleted");
            } else if ("SEAT1".equals(prop.getPropField().getName())) {
                assertEquals(field1.getName(), prop.getPropField().getName());
                assertEquals(field1.getValueType(), prop.getPropField().getValueType());
                assertEquals("13", prop.getValue());
                assertEquals(ticket.getId(), prop.getTicket().getId());
            } else if ("SEAT2".equals(prop.getPropField().getName())) {
                assertEquals(field2.getName(), prop.getPropField().getName());
                assertEquals(field2.getValueType(), prop.getPropField().getValueType());
                assertEquals("23", prop.getValue());
                assertEquals(ticket.getId(), prop.getTicket().getId());
            } else {
                fail("Found a prop that should not be here");
            }
        }

        ticket2 = apa.getTicket(ticket2.getType(), ticket2.getId());
        assertEquals(3, ticket2.getTicketProps().size());

        for(TicketProp prop : ticket2.getTicketProps()) {
             if ("SEAT".equals(prop.getPropField().getName())) {
                assertEquals(field.getName(), prop.getPropField().getName());
                assertEquals(field.getValueType(), prop.getPropField().getValueType());
                assertEquals("ddd", prop.getValue());
                assertEquals(ticket2.getId(), prop.getTicket().getId());
            }else if ("SEAT1".equals(prop.getPropField().getName())) {
                assertEquals(field1.getName(), prop.getPropField().getName());
                assertEquals(field1.getValueType(), prop.getPropField().getValueType());
                assertEquals("fff", prop.getValue());
                assertEquals(ticket2.getId(), prop.getTicket().getId());
            } else if ("SEAT2".equals(prop.getPropField().getName())) {
                assertEquals(field2.getName(), prop.getPropField().getName());
                assertEquals(field2.getValueType(), prop.getPropField().getValueType());
                assertEquals("ggg", prop.getValue());
                assertEquals(ticket2.getId(), prop.getTicket().getId());
            } else {
                fail("Found a prop that should not be here");
            }
        }
    }

    @Test
    public void testDeletePropDoesntExist() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));

        Ticket ticket = new Ticket();
        ticket.setType("hockey");
        TicketProp seatProp = new StringTicketProp(field, "03");
        ticket.addTicketProp(seatProp);
        ticket.addTicketProp(new StringTicketProp(field1, "13"));
        ticket.addTicketProp(new StringTicketProp(field2, "23"));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        ticket = apa.saveTicket(ticket);
        ticketsToDelete.add(ticket);

        TicketProp fakeProp = new StringTicketProp(field, "49949");

        try{
            apa.deleteTicketProp(fakeProp);
        } catch (ApaException toe) {
            //this is cool.  Hibernate is complaining that we're deleting an unsaved transient blah blah blah...
        }

        ticket = apa.getTicket(ticket.getType(), ticket.getId());
        assertEquals(3, ticket.getTicketProps().size());

        for(TicketProp prop : ticket.getTicketProps()) {
             if ("SEAT".equals(prop.getPropField().getName())) {
                assertEquals(field.getName(), prop.getPropField().getName());
                assertEquals(field.getValueType(), prop.getPropField().getValueType());
                assertEquals("03", prop.getValue());
                assertEquals(ticket.getId(), prop.getTicket().getId());
            }else if ("SEAT1".equals(prop.getPropField().getName())) {
                assertEquals(field1.getName(), prop.getPropField().getName());
                assertEquals(field1.getValueType(), prop.getPropField().getValueType());
                assertEquals("13", prop.getValue());
                assertEquals(ticket.getId(), prop.getTicket().getId());
            } else if ("SEAT2".equals(prop.getPropField().getName())) {
                assertEquals(field2.getName(), prop.getPropField().getName());
                assertEquals(field2.getValueType(), prop.getPropField().getValueType());
                assertEquals("23", prop.getValue());
                assertEquals(ticket.getId(), prop.getTicket().getId());
            } else {
                fail("Found a prop that should not be here");
            }
        }
    }
}
