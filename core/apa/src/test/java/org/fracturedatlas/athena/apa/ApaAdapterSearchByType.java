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


import java.util.Collection;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.junit.*;
import static org.junit.Assert.*;


public class ApaAdapterSearchByType extends BaseApaAdapterTest {

    AthenaSearch search;

    public ApaAdapterSearchByType() {
        super();
    }

    @Test
    public void testFindRecordsByType() {

        search = new AthenaSearch.Builder()
                              .type("event")
                              .and(new AthenaSearchConstraint("SOLD", Operator.EQUALS, "true"))
                              .build();
        Collection<PTicket> tickets = apa.findTickets(search);
        assertNotNull(tickets);
        assertEquals(4, tickets.size());
    }

    @Test
    public void testFindRecordsByType2() {

        search = new AthenaSearch.Builder()
                              .type("performance")
                              .and(new AthenaSearchConstraint("SOLD", Operator.EQUALS, "true"))
                              .build();
        Collection<PTicket> tickets = apa.findTickets(search);
        assertNotNull(tickets);
        assertEquals(1, tickets.size());
    }

    @Before
    public void addTickets() throws Exception {

        PTicket t1 = new PTicket("record");
        PTicket t2 = new PTicket("record");
        PTicket t3 = new PTicket("record");
        PTicket t4 = new PTicket("record");
        PTicket t5 = new PTicket("record");
        PTicket t6 = new PTicket("event");
        PTicket t7 = new PTicket("event");
        PTicket t8 = new PTicket("event");
        PTicket t9 = new PTicket("event");
        PTicket t10 = new PTicket("performance");

        addPropField(ValueType.INTEGER, "SEAT_NUMBER", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SECTION", StrictType.NOT_STRICT);
        addPropField(ValueType.DATETIME, "PERFORMANCE", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "TIER", StrictType.NOT_STRICT);
        addPropField(ValueType.BOOLEAN, "LOCKED", StrictType.NOT_STRICT);
        addPropField(ValueType.BOOLEAN, "SOLD", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "LOCKED_BY_API_KEY", StrictType.NOT_STRICT);
        addPropField(ValueType.DATETIME, "LOCK_EXPIRES", StrictType.NOT_STRICT);
        addPropField(ValueType.INTEGER, "PRICE", StrictType.NOT_STRICT);
        addPropField(ValueType.BOOLEAN, "HALF_PRICE_AVAILABLE", StrictType.NOT_STRICT);

        /*
         * SECTION, A=5, B=4, C=1
         * PRICE, "50" = 6, "25"=3, 150=1,
         * LOCKED=3
         * SOLD=5
         * LOCKED & SOLD = 4
         * GOLD=7, SILVER =2, BRONZE=1
         *
         *
         */

        t1.put("SEAT_NUMBER", "3");
        t1.put("SECTION" , "A");
        t1.put("PERFORMANCE" , "2010-10-01T13:33:50-04:00");
        t1.put("TIER" , "SILVER");
        t1.put("LOCKED" , "false");
        t1.put("SOLD" , "true");
        t1.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t1.put("LOCK_EXPIRES" , "2010-10-01T13:33:50-04:00");
        t1.put("PRICE" , "50");
        t1.put("HALF_PRICE_AVAILABLE" , "true");

        t2.put("SEAT_NUMBER", "3");
        t2.put("SECTION" , "A");
        t2.put("PERFORMANCE" , "2010-10-02T13:33:50-04:00");
        t2.put("TIER" , "SILVER");
        t2.put("LOCKED" , "true");
        t2.put("SOLD" , "true");
        t2.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t2.put("LOCK_EXPIRES" , "2010-10-02T13:33:50-04:00");
        t2.put("PRICE" , "50");
        t2.put("HALF_PRICE_AVAILABLE" , "true");

        t3.put("SEAT_NUMBER", "3");
        t3.put("SECTION" , "A");
        t3.put("PERFORMANCE" , "2010-10-03T13:33:50-04:00");
        t3.put("TIER" , "BRONZE");
        t3.put("LOCKED" , "true");
        t3.put("SOLD" , "true");
        t3.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t3.put("LOCK_EXPIRES" , "2010-10-03T13:33:50-04:00");
        t3.put("PRICE" , "50");
        t3.put("HALF_PRICE_AVAILABLE" , "true");

        t4.put("SEAT_NUMBER", "3");
        t4.put("SECTION" , "A");
        t4.put("PERFORMANCE" , "2010-10-04T13:33:50-04:00");
        t4.put("TIER" , "GOLD");
        t4.put("LOCKED" , "false");
        t4.put("SOLD" , "true");
        t4.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t4.put("LOCK_EXPIRES" , "2010-10-04T13:33:50-04:00");
        t4.put("PRICE" , "50");
        t4.put("HALF_PRICE_AVAILABLE" , "true");

        t5.put("SEAT_NUMBER", "3");
        t5.put("SECTION" , "A");
        t5.put("PERFORMANCE" , "2010-10-05T13:33:50-04:00");
        t5.put("TIER" , "GOLD");
        t5.put("LOCKED" , "false");
        t5.put("SOLD" , "true");
        t5.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t5.put("LOCK_EXPIRES" , "2010-10-05T13:33:50-04:00");
        t5.put("PRICE" , "50");
        t5.put("HALF_PRICE_AVAILABLE" , "true");

        t6.put("SEAT_NUMBER", "3");
        t6.put("SECTION" , "B");
        t6.put("PERFORMANCE" , "2010-10-06T13:33:50-04:00");
        t6.put("TIER" , "GOLD");
        t6.put("LOCKED" , "true");
        t6.put("SOLD" , "true");
        t6.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t6.put("LOCK_EXPIRES" , "2010-10-06T13:33:50-04:00");
        t6.put("PRICE" , "50");
        t6.put("HALF_PRICE_AVAILABLE" , "true");

        t7.put("SEAT_NUMBER", "3");
        t7.put("SECTION" , "B");
        t7.put("PERFORMANCE" , "2010-10-07T13:33:50-04:00");
        t7.put("TIER" , "GOLD");
        t7.put("LOCKED" , "false");
        t7.put("SOLD" , "true");
        t7.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t7.put("LOCK_EXPIRES" , "2010-10-07T13:33:50-04:00");
        t7.put("PRICE" , "25");
        t7.put("HALF_PRICE_AVAILABLE" , "true");

        t8.put("SEAT_NUMBER", "3");
        t8.put("SECTION" , "B");
        t8.put("PERFORMANCE" , "2010-10-08T13:33:50-04:00");
        t8.put("TIER" , "GOLD");
        t8.put("LOCKED" , "false");
        t8.put("SOLD" , "true");
        t8.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t8.put("LOCK_EXPIRES" , "2010-10-08T13:33:50-04:00");
        t8.put("PRICE" , "150");
        t8.put("HALF_PRICE_AVAILABLE" , "true");

        t9.put("SEAT_NUMBER", "3");
        t9.put("SECTION" , "B");
        t9.put("PERFORMANCE" , "2010-10-09T13:33:50-04:00");
        t9.put("TIER" , "GOLD");
        t9.put("LOCKED" , "false");
        t9.put("SOLD" , "true");
        t9.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t9.put("LOCK_EXPIRES" , "2010-10-09T13:33:50-04:00");
        t9.put("PRICE" , "25");
        t9.put("HALF_PRICE_AVAILABLE" , "true");

        t10.put("SEAT_NUMBER", "3");
        t10.put("SECTION" , "C");
        t10.put("PERFORMANCE" , "2010-10-10T13:33:50-04:00");
        t10.put("TIER" , "GOLD");
        t10.put("LOCKED" , "false");
        t10.put("SOLD" , "true");
        t10.put("LOCKED_BY_API_KEY" , "SAMPLE_API_KEY");
        t10.put("LOCK_EXPIRES" , "2010-10-10T13:33:50-04:00");
        t10.put("PRICE" , "25");
        t10.put("HALF_PRICE_AVAILABLE" , "true");

        t1 = apa.saveRecord(t1);
        t2 = apa.saveRecord(t2);
        t3 = apa.saveRecord(t3);
        t4 = apa.saveRecord(t4);
        t5 = apa.saveRecord(t5);
        t6 = apa.saveRecord(t6);
        t7 = apa.saveRecord(t7);
        t8 = apa.saveRecord(t8);
        t9 = apa.saveRecord(t9);
        t10 = apa.saveRecord(t10);

        ticketsToDelete.add(t1);
        ticketsToDelete.add(t2);
        ticketsToDelete.add(t3);
        ticketsToDelete.add(t4);
        ticketsToDelete.add(t5);
        ticketsToDelete.add(t6);
        ticketsToDelete.add(t7);
        ticketsToDelete.add(t8);
        ticketsToDelete.add(t9);
        ticketsToDelete.add(t10);
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }
}
