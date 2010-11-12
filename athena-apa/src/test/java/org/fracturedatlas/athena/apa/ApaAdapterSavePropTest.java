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
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.model.DateTimeTicketProp;
import org.fracturedatlas.athena.apa.model.IntegerTicketProp;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ApaAdapterSavePropTest extends BaseApaAdapterTest {
    public ApaAdapterSavePropTest() throws Exception {
        super();
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

    @Test
    public void testSaveProp() throws ParseException {


        Ticket t = new Ticket();
        t.setType("TEST");
        PropField pf3 = apa.savePropField(new PropField(ValueType.INTEGER, "TESTINT", Boolean.FALSE));
        propFieldsToDelete.add(pf3);
        PropField pf4 = apa.savePropField(new PropField(ValueType.INTEGER, "TESTINT2", Boolean.FALSE));
        propFieldsToDelete.add(pf4);

        t.addTicketProp(new IntegerTicketProp(pf3, 3));
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        IntegerTicketProp newProp = new IntegerTicketProp(pf4, 103);
        newProp.setTicket(t);
        apa.saveTicketProp(newProp);

        Ticket saveTicket = apa.getTicket(t.getId());
        PTicket savedPTicket = saveTicket.toClientTicket();
        assertEquals("103", savedPTicket.get("TESTINT2"));
        assertEquals("3", savedPTicket.get("TESTINT"));
        assertEquals("TEST", savedPTicket.getType());
        assertEquals(2, savedPTicket.getProps().entrySet().size());
    }


    @Test
    public void testSavePropWrongType() throws ParseException {


        Ticket t = new Ticket();
        t.setType("TEST");
        PropField pf3 = apa.savePropField(new PropField(ValueType.INTEGER, "TESTINT", Boolean.FALSE));
        propFieldsToDelete.add(pf3);
        PropField pf4 = apa.savePropField(new PropField(ValueType.INTEGER, "TESTINT2", Boolean.FALSE));
        propFieldsToDelete.add(pf4);

        t.addTicketProp(new IntegerTicketProp(pf3, 3));
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        StringTicketProp newProp = new StringTicketProp(pf4, "103");
        newProp.setTicket(t);
        try{
            apa.saveTicketProp(newProp);
            fail("Should have thrown InvalidValueException");
        } catch (InvalidValueException ive) {
            //pass
        }
    }


    @Test
    public void testSavePropWrongType2() throws ParseException {


        Ticket t = new Ticket();
        t.setType("TEST");
        PropField pf3 = apa.savePropField(new PropField(ValueType.INTEGER, "TESTINT", Boolean.FALSE));
        propFieldsToDelete.add(pf3);
        PropField pf4 = apa.savePropField(new PropField(ValueType.STRING, "TESTINT2", Boolean.FALSE));
        propFieldsToDelete.add(pf4);

        t.addTicketProp(new IntegerTicketProp(pf3, 3));
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        DateTimeTicketProp newProp = new DateTimeTicketProp(pf4, DateUtil.parseDate("2010-10-10T11:34:33-04:00"));
        newProp.setTicket(t);
        try{
            apa.saveTicketProp(newProp);
            fail("Should have thrown InvalidValueException");
        } catch (InvalidValueException ive) {
            //pass
        }
    }
}
