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

import java.util.Set;
import org.fracturedatlas.athena.search.ApaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.search.ApaSearchConstraint;
import org.fracturedatlas.athena.apa.model.IntegerTicketProp;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.apa.model.PropField;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author fintan
 */
public class ApaAdapterFindTicketWithModifiersTest extends BaseApaAdapterTest {

    public ApaAdapterFindTicketWithModifiersTest() {
        super();
    }

    @Before
    public void setupTickets() {
        Ticket t = new Ticket();

        PropField field = new PropField(ValueType.STRING, "Performance", Boolean.FALSE);
        PropField pf1 = apa.savePropField(field);
        propFieldsToDelete.add(pf1);

        field = new PropField(ValueType.INTEGER, "SeatNum", Boolean.FALSE);
        PropField pf2 = apa.savePropField(field);
        propFieldsToDelete.add(pf2);


        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
        t.addTicketProp(new IntegerTicketProp(pf2, 1));
        t.setType("1");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
        t.addTicketProp(new IntegerTicketProp(pf2, 2));
        t.setType("2");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
        t.addTicketProp(new IntegerTicketProp(pf2, 3));
        t.setType("3");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
        t.addTicketProp(new IntegerTicketProp(pf2, 4));
        t.setType("4");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
        t.addTicketProp(new IntegerTicketProp(pf2, 5));
        t.setType("5");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
        t.addTicketProp(new IntegerTicketProp(pf2, 6));
        t.setType("6");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
        t.addTicketProp(new IntegerTicketProp(pf2, 7));
        t.setType("7");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
        t.addTicketProp(new IntegerTicketProp(pf2, 8));
        t.setType("8");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
        t.addTicketProp(new IntegerTicketProp(pf2, 1));
        t.setType("9");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
        t.addTicketProp(new IntegerTicketProp(pf2, 2));
        t.setType("10");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
        t.addTicketProp(new IntegerTicketProp(pf2, 3));
        t.setType("11");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
        t.addTicketProp(new IntegerTicketProp(pf2, 4));
        t.setType("12");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
        t.addTicketProp(new IntegerTicketProp(pf2, 5));
        t.setType("13");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
        t.addTicketProp(new IntegerTicketProp(pf2, 6));
        t.setType("14");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
        t.addTicketProp(new IntegerTicketProp(pf2, 7));
        t.setType("15");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

        t = new Ticket();
        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
        t.addTicketProp(new IntegerTicketProp(pf2, 8));
        t.setType("16");
        t = apa.saveTicket(t);
        ticketsToDelete.add(t);

    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

    @Test
    public void testStartOnly() {

        ApaSearch search = new ApaSearch();
        search.setSearchModifier("_start", "3");

        Set results = apa.findTickets(search);
        assertEquals(0, results.size());

    }

    @Test
    public void testStartAndConstraints() {
        ApaSearchConstraint con1 = new ApaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
        ApaSearchConstraint con2 = new ApaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");

        ApaSearch search = new ApaSearch.Builder(con1).and(con2).start(3).build();

        Set results = apa.findTickets(search);
        assertEquals(4, results.size());


    }

    @Test
    public void testStartLimitAndConstraints() {
        ApaSearchConstraint con1 = new ApaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
        ApaSearchConstraint con2 = new ApaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");

        ApaSearch search = new ApaSearch.Builder(con1).and(con2).start(3).limit(2).build();

        Set results = apa.findTickets(search);
        assertEquals(2, results.size());

    }

    @Test
    public void testStartGreaterThanReturn() {
        ApaSearchConstraint con1 = new ApaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
        ApaSearchConstraint con2 = new ApaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");

        ApaSearch search = new ApaSearch.Builder(con1).and(con2).start(8).build();

        Set results = apa.findTickets(search);
        assertEquals(0, results.size());

    }

    @Test
    public void testStartEqualReturn() {
        ApaSearchConstraint con1 = new ApaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
        ApaSearchConstraint con2 = new ApaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");

        ApaSearch search = new ApaSearch.Builder(con1).and(con2).start(7).build();

        Set results = apa.findTickets(search);
        assertEquals(0, results.size());

    }

    @Test
    public void testInvalidStart() {
        ApaSearchConstraint con1 = new ApaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
        ApaSearchConstraint con2 = new ApaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
        ApaSearch search = new ApaSearch();
        search.setSearchModifier("_start", "dog");
        search.addConstraint(con1);
        search.addConstraint(con2);
        Set results = apa.findTickets(search);
        assertEquals(7, results.size());

    }

    @Test
    public void testStartWithLimitGreaterThanReturn() {
        ApaSearchConstraint con1 = new ApaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
        ApaSearchConstraint con2 = new ApaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");

        ApaSearch search = new ApaSearch.Builder(con1).and(con2).start(3).limit(5).build();

        Set results = apa.findTickets(search);
        assertEquals(4, results.size());

    }

     @Test
   public void testStartofZerotoReturnSize() {
        ApaSearchConstraint con1 = new ApaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
        ApaSearchConstraint con2 = new ApaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
        Set results = null;
        ApaSearch search = null;

        for (int start = 0; start <= 7; start++) {
            search = new ApaSearch.Builder(con1).and(con2).start(start).build();
            results = apa.findTickets(search);
            assertEquals(7 - start, results.size());
        }
    }

    @Test
    public void testStartofZerotoReturnSizeAndLimitofZero() {
        ApaSearchConstraint con1 = new ApaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
        ApaSearchConstraint con2 = new ApaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
        Set results = null;
        ApaSearch search = null;

        int limit = 0;

        for (int start = 0; start <= 7; start++) {
            search = new ApaSearch.Builder(con1).and(con2).start(start).limit(limit).build();
            results = apa.findTickets(search);
            assertEquals(0, results.size());
        }
    }

    @Test
    public void testStartofZerotoReturnSizeAndLimitofOne() {
        ApaSearchConstraint con1 = new ApaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
        ApaSearchConstraint con2 = new ApaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
        Set<Ticket> results = null;
        ApaSearch search = null;
        Ticket t = null;

        int limit = 1;

        for (int start = 0; start <= 6; start++) {
            search = new ApaSearch.Builder(con1).and(con2).start(start).limit(limit).build();
            results = apa.findTickets(search);
            assertEquals(1, results.size());
        }
        search = new ApaSearch.Builder(con1).and(con2).start(7).limit(limit).build();
        results = apa.findTickets(search);
        assertEquals(0, results.size());


    }

    @Test
    public void testStartofZerotoReturnSizeAndLimitofTwo() {
        ApaSearchConstraint con1 = new ApaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
        ApaSearchConstraint con2 = new ApaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
        Set results = null;
        ApaSearch search = null;
        int limit = 2;

        for (int start = 0; start <= 5; start++) {
            search = new ApaSearch.Builder(con1).and(con2).start(start).limit(limit).build();
            results = apa.findTickets(search);
            assertEquals(2, results.size());
        }
        search = new ApaSearch.Builder(con1).and(con2).start(6).limit(limit).build();
        results = apa.findTickets(search);
        assertEquals(1, results.size());

        search = new ApaSearch.Builder(con1).and(con2).start(7).limit(limit).build();
        results = apa.findTickets(search);
        assertEquals(0, results.size());



    }
}
