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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.ParseException;
import org.fracturedatlas.athena.apa.impl.jpa.DateTimeTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.StringTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class ApaAdapterDeleteTicketsTest extends BaseApaAdapterTest {

    JpaRecord testTicket = new JpaRecord();
    Logger logger = LoggerFactory.getLogger(ApaAdapterDeleteTicketsTest.class);

    public ApaAdapterDeleteTicketsTest() throws Exception {
        super();
    }

    @Before
    public void addTickets() {
    }

    @After
    public void teardown() {
        super.teardownTickets();
    }

    @Test
    public void testDeleteTicketPassingTicket() {
        addPropField(ValueType.STRING,"WXYZ",Boolean.FALSE);
        PTicket ticket = new PTicket();
        ticket.setType("foo");
        ticket.put("WXYZ", "WXYZ");
        ticket = apa.saveRecord(ticket);

        assertTrue(apa.deleteRecord(ticket));

        PTicket shouldBeDeleted = apa.getRecord(ticket.getType(), ticket.getId());
        assertNull(shouldBeDeleted);
    }

    @Test
    public void testDeleteTicketPassingTypeId() {
        addPropField(ValueType.STRING,"WXYZ",Boolean.FALSE);
        PTicket ticket = new PTicket();
        ticket.setType("foo");
        ticket.put("WXYZ", "WXYZ");
        ticket = apa.saveRecord(ticket);

        assertTrue(apa.deleteRecord(ticket.getType(), ticket.getId()));

        PTicket shouldBeDeleted = apa.getRecord(ticket.getType(), ticket.getId());
        assertNull(shouldBeDeleted);
    }

    @Test
    public void testDeleteAFewTickets() throws ParseException {


        PTicket t = new PTicket("ticket");
        PTicket t2 = new PTicket("ticket");
        PTicket t3 = new PTicket("ticket");
        PTicket t4 = new PTicket("ticket");

        t.setType("ticket");
        t2.setType("ticket");
        t3.setType("ticket");
        t4.setType("ticket");

        t = apa.saveRecord(t);
        t2 = apa.saveRecord(t2);
        t3 = apa.saveRecord(t3);
        t4 = apa.saveRecord(t4);
        ticketsToDelete.add(t4);

        assertTrue(apa.deleteRecord(t.getType(), t.getId()));
        assertTrue(apa.deleteRecord(t2.getType(), t2.getId()));
        assertTrue(apa.deleteRecord(t3.getType(), t3.getId()));

        PTicket shouldBeDeleted = apa.getRecord(t.getType(), t.getId());
        assertNull(shouldBeDeleted);
        shouldBeDeleted = apa.getRecord(t2.getType(), t2.getId());
        assertNull(shouldBeDeleted);
        shouldBeDeleted = apa.getRecord(t3.getType(), t3.getId());
        assertNull(shouldBeDeleted);

        PTicket expected = apa.getRecord(t4.getType(), t4.getId());
        assertEquals(expected, t4);

    }

    @Test
    public void testDeleteTicketNullTicket() {
        try{
            apa.deleteRecord(null);
            fail("Should have thrown NPE");
        } catch (NullPointerException pass) {
            //pass!
        }
    }

    @Test
    public void testDeleteTicketNotYetPersisted() {
        addPropField(ValueType.STRING,"WXYZ",Boolean.FALSE);

        PTicket t = new PTicket();
        t.put("WXYZ", "something");
        assertFalse(apa.deleteRecord(t));
    }
}
