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
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StrictType;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
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
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT));
        PropField field1 = apa.savePropField(new PropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT));
        PropField field2 = apa.savePropField(new PropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        propFieldsToDelete.add(field1);
        propFieldsToDelete.add(field2);

        Ticket ticket = new Ticket();
        ticket.setType("record");
        ticket.addTicketProp(new StringTicketProp(field, "03"));
        ticket.addTicketProp(new StringTicketProp(field1, "13"));
        ticket.addTicketProp(new StringTicketProp(field2, "23"));

        ticket = apa.saveTicket(ticket);
        assertNotNull(ticket.getId());
        ticketsToDelete.add(ticket);

        ticket = apa.getTicket(ticket.getId());

        PTicket pTicket = ticket.toClientTicket();
        assertNotNull(pTicket.getId());
        assertEquals("record", pTicket.getType());
        assertEquals(3, pTicket.getProps().size());
        assertEquals("03", pTicket.get("SEAT"));
        assertEquals("13", pTicket.get("SEAT1"));
        assertEquals("23", pTicket.get("SEAT2"));

    }
}
