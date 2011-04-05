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
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.apa.impl.jpa.IntegerTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.StringTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
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

    /*
     * Add 15 tickets
     */
//    @Before
//    public void setupTickets() {
//        JpaRecord t = new JpaRecord();
//
//        PropField field = new PropField(ValueType.STRING, "Performance", Boolean.FALSE);
//        PropField pf1 = apa.savePropField(field);
//        propFieldsToDelete.add(pf1);
//
//        field = new PropField(ValueType.INTEGER, "SeatNum", Boolean.FALSE);
//        PropField pf2 = apa.savePropField(field);
//        propFieldsToDelete.add(pf2);
//
//
//        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 1));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 2));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 3));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 4));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 5));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 6));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 7));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "Mac & Mabel"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 8));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 1));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 2));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 3));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 4));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 5));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 6));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 7));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        t = new JpaRecord();
//        t.addTicketProp(new StringTicketProp(pf1, "West Side Story"));
//        t.addTicketProp(new IntegerTicketProp(pf2, 8));
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//    }
//
//    @After
//    public void teardownTickets() {
//        super.teardownTickets();
//    }
//
//    @Test
//    public void testStartOnly() {
//
//        AthenaSearch search = new AthenaSearch();
//        search.setType("ticket");
//        search.setSearchModifier(AthenaSearch.START, "3");
//
//        Set results = apa.findTickets(search);
//        assertEquals(13, results.size());
//    }
//
//    @Test
//    public void testStartAndConstraints() {
//        AthenaSearchConstraint con1 = new AthenaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
//        AthenaSearchConstraint con2 = new AthenaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
//
//        AthenaSearch search = new AthenaSearch.Builder(con1).and(con2).start(3).type("ticket").build();
//
//        Set results = apa.findTickets(search);
//        assertEquals(4, results.size());
//
//
//    }
//
//    @Test
//    public void testStartLimitAndConstraints() {
//        AthenaSearchConstraint con1 = new AthenaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
//        AthenaSearchConstraint con2 = new AthenaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
//
//        AthenaSearch search = new AthenaSearch.Builder(con1).and(con2).start(3).limit(2).type("ticket").build();
//
//        Set results = apa.findTickets(search);
//        assertEquals(2, results.size());
//
//    }
//
//    @Test
//    public void testStartGreaterThanReturn() {
//        AthenaSearchConstraint con1 = new AthenaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
//        AthenaSearchConstraint con2 = new AthenaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
//
//        AthenaSearch search = new AthenaSearch.Builder(con1).and(con2).start(8).type("ticket").build();
//
//        Set results = apa.findTickets(search);
//        assertEquals(0, results.size());
//
//    }
//
//    @Test
//    public void testStartEqualReturn() {
//        AthenaSearchConstraint con1 = new AthenaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
//        AthenaSearchConstraint con2 = new AthenaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
//
//        AthenaSearch search = new AthenaSearch.Builder(con1).and(con2).start(7).type("ticket").build();
//
//        Set results = apa.findTickets(search);
//        assertEquals(0, results.size());
//
//    }
//
//    @Test
//    public void testInvalidStart() {
//        AthenaSearchConstraint con1 = new AthenaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
//        AthenaSearchConstraint con2 = new AthenaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
//        AthenaSearch search = new AthenaSearch();
//        search.setSearchModifier(AthenaSearch.START, "dog");
//        search.addConstraint(con1);
//        search.addConstraint(con2);
//        search.setType("ticket");
//        Set results = apa.findTickets(search);
//        assertEquals(7, results.size());
//
//    }
//
//    @Test
//    public void testStartWithLimitGreaterThanReturn() {
//        AthenaSearchConstraint con1 = new AthenaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
//        AthenaSearchConstraint con2 = new AthenaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
//
//        AthenaSearch search = new AthenaSearch.Builder(con1).and(con2).start(3).limit(5).type("ticket").build();
//
//        Set results = apa.findTickets(search);
//        assertEquals(4, results.size());
//
//    }
//
//     @Test
//   public void testStartofZerotoReturnSize() {
//        AthenaSearchConstraint con1 = new AthenaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
//        AthenaSearchConstraint con2 = new AthenaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
//        Set results = null;
//        AthenaSearch search = null;
//
//        for (int start = 0; start <= 7; start++) {
//            search = new AthenaSearch.Builder(con1).and(con2).start(start).type("ticket").build();
//            results = apa.findTickets(search);
//            assertEquals(7 - start, results.size());
//        }
//    }
//
//    @Test
//    public void testStartofZerotoReturnSizeAndLimitofZero() {
//        AthenaSearchConstraint con1 = new AthenaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
//        AthenaSearchConstraint con2 = new AthenaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
//        Set results = null;
//        AthenaSearch search = null;
//
//        int limit = 0;
//
//        for (int start = 0; start <= 7; start++) {
//            search = new AthenaSearch.Builder(con1).and(con2).start(start).limit(limit).type("ticket").build();
//            results = apa.findTickets(search);
//            assertEquals(0, results.size());
//        }
//    }
//
//    @Test
//    public void testStartofZerotoReturnSizeAndLimitofOne() {
//        AthenaSearchConstraint con1 = new AthenaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
//        AthenaSearchConstraint con2 = new AthenaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
//        Set<JpaRecord> results = null;
//        AthenaSearch search = null;
//        JpaRecord t = null;
//
//        int limit = 1;
//
//        for (int start = 0; start <= 6; start++) {
//            search = new AthenaSearch.Builder(con1).and(con2).start(start).limit(limit).type("ticket").build();
//            results = apa.findTickets(search);
//            assertEquals(1, results.size());
//        }
//        search = new AthenaSearch.Builder(con1).and(con2).start(7).limit(limit).type("ticket").build();
//        results = apa.findTickets(search);
//        assertEquals(0, results.size());
//
//
//    }
//
//    @Test
//    public void testStartofZerotoReturnSizeAndLimitofTwo() {
//        AthenaSearchConstraint con1 = new AthenaSearchConstraint("Performance", Operator.EQUALS, "Mac & Mabel");
//        AthenaSearchConstraint con2 = new AthenaSearchConstraint("SeatNum", Operator.GREATER_THAN, "1");
//        Set results = null;
//        AthenaSearch search = null;
//        int limit = 2;
//
//        for (int start = 0; start <= 5; start++) {
//            search = new AthenaSearch.Builder(con1).and(con2).start(start).limit(limit).type("ticket").build();
//            results = apa.findTickets(search);
//            assertEquals(2, results.size());
//        }
//        search = new AthenaSearch.Builder(con1).and(con2).start(6).limit(limit).type("ticket").build();
//        results = apa.findTickets(search);
//        assertEquals(1, results.size());
//
//        search = new AthenaSearch.Builder(con1).and(con2).start(7).limit(limit).type("ticket").build();
//        results = apa.findTickets(search);
//        assertEquals(0, results.size());
//
//
//
//    }
}
