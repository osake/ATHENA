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
    public void teardownTickets() {
        super.teardownTickets();
    }


    @Test
    public void testDeleteTicketPassingObjectId() {
        PropField field = new PropField(ValueType.STRING, "WXYZ", Boolean.FALSE);
        PropField pf = apa.savePropField(field);
        PTicket ticket = new PTicket();
        ticket.put("WXYZ", "WXYZ");
        ticket = apa.saveRecord("record", ticket);

        ticketsToDelete.add(ticket);
        propFieldsToDelete.add(pf);

        assertTrue(apa.deleteTicket(ticket.getType(), ticket.getId()));

        JpaRecord shouldBeDeleted = apa.getTicket(t.getType(), t.getId());
        assertNull(shouldBeDeleted);
    }


    //@Test
    public void testDeleteTicketPassingTicket() {
        JpaRecord t = new JpaRecord();
        t.setType("foo");

        PropField field = new PropField(ValueType.STRING, "WXYZ", Boolean.FALSE);
        PropField pf = apa.savePropField(field);
        PTicket ticket = new PTicket();
        ticket.put("WXYZ", "WXYZ");
        ticket = apa.saveRecord("record", ticket);

        ticketsToDelete.add(t);
        propFieldsToDelete.add(pf);

        assertTrue(apa.deleteTicket(t));

        JpaRecord shouldBeDeleted = apa.getTicket(t.getType(), t.getId());
        assertNull(shouldBeDeleted);
    }

    @Test
    public void testDeleteTicket2() throws ParseException {


        JpaRecord t = new JpaRecord();
        JpaRecord t2 = new JpaRecord();
        JpaRecord t3 = new JpaRecord();
        JpaRecord t4 = new JpaRecord();

        t.setType("ticket");
        t2.setType("ticket");
        t3.setType("ticket");
        t4.setType("ticket");

        PropField pf3 = apa.savePropField(new PropField(ValueType.DATETIME, "Date", Boolean.FALSE));
        propFieldsToDelete.add(pf3);

        t.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t = apa.saveTicket(t);

        t2.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t2 = apa.saveTicket(t2);

        t3.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t3 = apa.saveTicket(t3);

        t4.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t4 = apa.saveTicket(t4);


        ticketsToDelete.add(t);
        ticketsToDelete.add(t2);
        ticketsToDelete.add(t3);
        ticketsToDelete.add(t4);
        assertTrue(apa.deleteTicket(t2.getType(), t2.getId()));

        JpaRecord shouldBeDeleted = apa.getTicket(t2.getType(), t2.getId());
        assertNull(shouldBeDeleted);
        JpaRecord expected = apa.getTicket(t.getType(), t.getId());
        assertEquals(expected, t);
        expected = apa.getTicket(t3.getType(), t3.getId());
        assertEquals(expected, t3);
        expected = apa.getTicket(t4.getType(), t4.getId());
        assertEquals(expected, t4);
    }

    @Test
    public void testDeleteAFewTickets() throws ParseException {


        JpaRecord t = new JpaRecord();
        JpaRecord t2 = new JpaRecord();
        JpaRecord t3 = new JpaRecord();
        JpaRecord t4 = new JpaRecord();

        t.setType("ticket");
        t2.setType("ticket");
        t3.setType("ticket");
        t4.setType("ticket");
        
        PropField pf3 = apa.savePropField(new PropField(ValueType.DATETIME, "Date", Boolean.FALSE));
        propFieldsToDelete.add(pf3);

        t.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t = apa.saveTicket(t);

        t2.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t2 = apa.saveTicket(t2);

        t3.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t3 = apa.saveTicket(t3);

        t4.addTicketProp(new DateTimeTicketProp(pf3, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t4 = apa.saveTicket(t4);


        ticketsToDelete.add(t);
        ticketsToDelete.add(t2);
        ticketsToDelete.add(t3);
        ticketsToDelete.add(t4);

        assertTrue(apa.deleteTicket(t.getType(), t.getId()));
        assertTrue(apa.deleteTicket(t2.getType(), t2.getId()));
        assertTrue(apa.deleteTicket(t3.getType(), t3.getId()));

        JpaRecord shouldBeDeleted = apa.getTicket(t.getType(), t.getId());
        assertNull(shouldBeDeleted);
        shouldBeDeleted = apa.getTicket(t2.getType(), t2.getId());
        assertNull(shouldBeDeleted);
        shouldBeDeleted = apa.getTicket(t3.getType(), t3.getId());
        assertNull(shouldBeDeleted);

        JpaRecord expected = apa.getTicket(t4.getType(), t4.getId());
        assertEquals(expected, t4);

    }

    @Test
    public void testDeleteTicketNullTicket() {
        JpaRecord t = new JpaRecord();
        t.setType("clown");    

        PropField field = new PropField();
        field.setValueType(ValueType.STRING);
        field.setName("WXYZ");
        field.setStrict(Boolean.FALSE);
        PropField pf = apa.savePropField(field);

        StringTicketProp prop = new StringTicketProp();
        prop.setPropField(pf);
        prop.setValue("WXYZ");
        t.addTicketProp(prop);
        t = apa.saveTicket(t);

        ticketsToDelete.add(t);
        propFieldsToDelete.add(pf);

        try{
            apa.deleteTicket(null);
            fail("Should have thrown NPE");
        } catch (NullPointerException pass) {
            //pass!
        }
    }

    @Test
    public void testDeleteTicketNotYetPersisted() {
        JpaRecord t = new JpaRecord();
        t.setType("clown");

        PropField field = new PropField();
        field.setValueType(ValueType.STRING);
        field.setName("WXYZ");
        field.setStrict(Boolean.FALSE);
        PropField pf = apa.savePropField(field);

        StringTicketProp prop = new StringTicketProp();
        prop.setPropField(pf);
        prop.setValue("WXYZ");
        t.addTicketProp(prop);

        propFieldsToDelete.add(pf);

        assertFalse(apa.deleteTicket(t));
    }
}
