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
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.StringTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.TicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
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

        PTicket ticket = new PTicket();
        ticket.setType("hockey");
        TicketProp seatProp = new StringTicketProp(field, "03");

        //deleting SEAT
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");

        ticket = apa.saveRecord(ticket);
        ticketsToDelete.add(ticket);

        seatProp = apa.getTicketProp("SEAT", ticket.getType(), ticket.getId());
        apa.deleteTicketProp(seatProp);

        ticket = apa.getRecord(ticket.getType(), ticket.getId());
        assertEquals(2, ticket.getProps().size());

        for(String key : ticket.getProps().keySet()) {
            if("SEAT".equals(key)) {
                fail("Should have been deleted");
            } else if ("SEAT1".equals(key)) {
                assertEquals(field1.getName(), key);
                assertEquals("13", ticket.get(key));
            } else if ("SEAT2".equals(key)) {
                assertEquals(field2.getName(), key);
                assertEquals("23", ticket.get(key));
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

        PTicket ticket = new PTicket();
        ticket.setType("hockey");
        TicketProp seatProp = new StringTicketProp(field, "03");

        //deleting SEAT
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        PTicket ticket2 = new PTicket();
        ticket2.setType("dos");
        ticket2.put("SEAT", "ddd");
        ticket2.put("SEAT1", "fff");
        ticket2.put("SEAT2", "ggg");

        ticket = apa.saveRecord(ticket);
        ticketsToDelete.add(ticket);
        ticket2 = apa.saveRecord(ticket2);
        ticketsToDelete.add(ticket2);

        seatProp = apa.getTicketProp("SEAT", ticket.getType(), ticket.getId());
        apa.deleteTicketProp(seatProp);

        ticket = apa.getRecord(ticket.getType(), ticket.getId());
        assertEquals(2, ticket.getProps().size());

        for(String key : ticket.getProps().keySet()) {
            if("SEAT".equals(key)) {
                fail("Should have been deleted");
            } else if ("SEAT1".equals(key)) {
                assertEquals(field1.getName(), key);
                assertEquals("13", ticket.get(key));
            } else if ("SEAT2".equals(key)) {
                assertEquals(field2.getName(), key);
                assertEquals("23", ticket.get(key));
            } else {
                fail("Found a prop that should not be here");
            }
        }

        ticket2 = apa.getRecord(ticket2.getType(), ticket2.getId());
        assertEquals(3, ticket2.getProps().size());

        for(String key : ticket.getProps().keySet()) {
            if("SEAT".equals(key)) {
                assertEquals(field.getName(), key);
                assertEquals("ddd", ticket.get(key));
            } else if ("SEAT1".equals(key)) {
                assertEquals(field1.getName(), key);
                assertEquals("13", ticket.get(key));
            } else if ("SEAT2".equals(key)) {
                assertEquals(field2.getName(), key);
                assertEquals("23", ticket.get(key));
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
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        PTicket ticket = new PTicket();
        ticket.setType("hockey");
        TicketProp seatProp = new StringTicketProp(field, "03");

        //deleting SEAT
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");

        ticket = apa.saveRecord(ticket);
        ticketsToDelete.add(ticket);

        TicketProp fakeProp = new StringTicketProp(field, "49949");

        try{
            apa.deleteTicketProp(fakeProp);
        } catch (ApaException toe) {
            //this is cool.  Hibernate is complaining that we're deleting an unsaved transient blah blah blah...
        }

        seatProp = apa.getTicketProp("SEAT", ticket.getType(), ticket.getId());
        apa.deleteTicketProp(seatProp);

        ticket = apa.getRecord(ticket.getType(), ticket.getId());
        assertEquals(2, ticket.getProps().size());

        for(String key : ticket.getProps().keySet()) {
            if("SEAT".equals(key)) {
                assertEquals(field.getName(), key);
                assertEquals("03", ticket.get(key));
            } else if ("SEAT1".equals(key)) {
                assertEquals(field1.getName(), key);
                assertEquals("13", ticket.get(key));
            } else if ("SEAT2".equals(key)) {
                assertEquals(field2.getName(), key);
                assertEquals("23", ticket.get(key));
            } else {
                fail("Found a prop that should not be here");
            }
        }
    }
}
