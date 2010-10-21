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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.fracturedatlas.athena.apa.model.DateTimeTicketProp;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class ApaAdapterDeleteTicketsTest extends BaseApaAdapterTest {

    Ticket testTicket = new Ticket();
    Logger logger = Logger.getLogger(ApaAdapterDeleteTicketsTest.class);

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
        Ticket t = new Ticket();

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

        assertTrue(apa.deleteTicket(t.getId()));

        Ticket shouldBeDeleted = apa.getTicket(t.getId());
        assertNull(shouldBeDeleted);
    }


    @Test
    public void testDeleteTicketPassingTicket() {
        Ticket t = new Ticket();

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

        assertTrue(apa.deleteTicket(t));

        Ticket shouldBeDeleted = apa.getTicket(t.getId());
        assertNull(shouldBeDeleted);
    }

    @Test
    public void testDeleteTicket2() throws ParseException {


        Ticket t = new Ticket();
        Ticket t2 = new Ticket();
        Ticket t3 = new Ticket();
        Ticket t4 = new Ticket();

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

        assertTrue(apa.deleteTicket(t2.getId()));

        Ticket shouldBeDeleted = apa.getTicket(t2.getId());
        assertNull(shouldBeDeleted);
        Ticket expected = apa.getTicket(t.getId());
        assertEquals(expected, t);
        expected = apa.getTicket(t3.getId());
        assertEquals(expected, t3);
        expected = apa.getTicket(t4.getId());
        assertEquals(expected, t4);
    }

    @Test
    public void testDeleteAFewTickets() throws ParseException {


        Ticket t = new Ticket();
        Ticket t2 = new Ticket();
        Ticket t3 = new Ticket();
        Ticket t4 = new Ticket();
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

        assertTrue(apa.deleteTicket(t.getId()));
        assertTrue(apa.deleteTicket(t2.getId()));
        assertTrue(apa.deleteTicket(t3.getId()));

        Ticket shouldBeDeleted = apa.getTicket(t.getId());
        assertNull(shouldBeDeleted);
        shouldBeDeleted = apa.getTicket(t2.getId());
        assertNull(shouldBeDeleted);
        shouldBeDeleted = apa.getTicket(t3.getId());
        assertNull(shouldBeDeleted);

        Ticket expected = apa.getTicket(t4.getId());
        assertEquals(expected, t4);

    }

    @Test
    public void testDeleteTicketNullTicket() {
        Ticket t = new Ticket();

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
        Ticket t = new Ticket();

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
