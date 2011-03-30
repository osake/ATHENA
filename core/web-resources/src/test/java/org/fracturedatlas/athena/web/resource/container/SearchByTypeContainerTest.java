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

package org.fracturedatlas.athena.web.resource.container;

import com.google.gson.Gson;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MultivaluedMap;
import org.fracturedatlas.athena.apa.impl.jpa.BooleanTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.DateTimeTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.IntegerTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.StringTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.*;
import static org.junit.Assert.*;

public class SearchByTypeContainerTest extends BaseTixContainerTest {

    String bottlesPath = "/bottles/";
    String casesPath = "/cases/";
    Gson gson = JsonUtil.getGson();

    @Test
    public void testFindTickets() {
        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("SOLD", "eqfalse");
        String jsonString = tix.path(bottlesPath).queryParams(queryParams).get(String.class);
         JpaRecord[] tickets = gson.fromJson(jsonString,  JpaRecord[].class);
        assertNotNull(tickets);
        assertEquals(0, tickets.length);

        queryParams = new MultivaluedMapImpl();
        queryParams.add("SOLD", "eqfalse");
        jsonString = tix.path(casesPath).queryParams(queryParams).get(String.class);
        tickets = gson.fromJson(jsonString,  JpaRecord[].class);
        assertNotNull(tickets);
        assertEquals(5, tickets.length);
    }

    @Test
    public void testFindTicketsByTier() {
        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("TIER", "eqSILVER");
        String jsonString = tix.path(bottlesPath).queryParams(queryParams).get(String.class);
         JpaRecord[] tickets = gson.fromJson(jsonString,  JpaRecord[].class);
        assertNotNull(tickets);
        assertEquals(2, tickets.length);

        queryParams = new MultivaluedMapImpl();
        queryParams.add("TIER", "eqSILVER");
        jsonString = tix.path(casesPath).queryParams(queryParams).get(String.class);
        tickets = gson.fromJson(jsonString,  JpaRecord[].class);
        assertNotNull(tickets);
        assertEquals(0, tickets.length);
    }

    @Before
    public void addTickets() throws Exception {
        JpaRecord t1 = new JpaRecord();
        JpaRecord t2 = new JpaRecord();
        JpaRecord t3 = new JpaRecord();
        JpaRecord t4 = new JpaRecord();
        JpaRecord t5 = new JpaRecord();
        JpaRecord t6 = new JpaRecord();
        JpaRecord t7 = new JpaRecord();
        JpaRecord t8 = new JpaRecord();
        JpaRecord t9 = new JpaRecord();
        JpaRecord t10 = new JpaRecord();

        t1.setType("bottle");
        t2.setType("bottle");
        t3.setType("bottle");
        t4.setType("bottle");
        t5.setType("bottle");
        t6.setType("case");
        t7.setType("case");
        t8.setType("case");
        t9.setType("case");
        t10.setType("case");

        PropField seatNumberProp = apa.savePropField(new PropField(ValueType.INTEGER, "SEAT_NUMBER", StrictType.NOT_STRICT));
        PropField sectionProp = apa.savePropField(new PropField(ValueType.STRING, "SECTION", StrictType.NOT_STRICT));
        PropField performanceProp = apa.savePropField(new PropField(ValueType.DATETIME, "PERFORMANCE", StrictType.NOT_STRICT));
        PropField tierProp = apa.savePropField(new PropField(ValueType.STRING, "TIER", StrictType.NOT_STRICT));
        PropField lockedProp = apa.savePropField(new PropField(ValueType.BOOLEAN, "LOCKED", StrictType.NOT_STRICT));
        PropField soldProp = apa.savePropField(new PropField(ValueType.BOOLEAN, "SOLD", StrictType.NOT_STRICT));
        PropField lockedByProp = apa.savePropField(new PropField(ValueType.STRING, "LOCKED_BY_API_KEY", StrictType.NOT_STRICT));
        PropField lockExpiresProp = apa.savePropField(new PropField(ValueType.DATETIME, "LOCK_EXPIRES", StrictType.NOT_STRICT));
        PropField priceProp = apa.savePropField(new PropField(ValueType.INTEGER, "PRICE", StrictType.NOT_STRICT));
        PropField halfPriceProp = apa.savePropField(new PropField(ValueType.BOOLEAN, "HALF_PRICE_AVAILABLE", StrictType.NOT_STRICT));

        propFieldsToDelete.add(seatNumberProp);
        propFieldsToDelete.add(sectionProp);
        propFieldsToDelete.add(performanceProp);
        propFieldsToDelete.add(tierProp);
        propFieldsToDelete.add(lockedProp);
        propFieldsToDelete.add(soldProp);
        propFieldsToDelete.add(lockedByProp);
        propFieldsToDelete.add(lockExpiresProp);
        propFieldsToDelete.add(priceProp);
        propFieldsToDelete.add(halfPriceProp);

        /*
         * SECTION, A=5, B=4, C=1
         * PRICE, 50 = 6, 25=2, 150=1, 250=1
         * LOCKED=3
         * SOLD=5
         * LOCKED & SOLD = 4
         * GOLD=7, SILVER =2, BRONZE=1
         *
         *
         */

        t1.addTicketProp(new IntegerTicketProp(seatNumberProp, 3));
        t1.addTicketProp(new StringTicketProp(sectionProp, "A"));
        t1.addTicketProp(new DateTimeTicketProp(performanceProp, DateUtil.parseDate("2010-10-13T13:33:50-04:00")));
        t1.addTicketProp(new StringTicketProp(tierProp, "SILVER"));
        t1.addTicketProp(new BooleanTicketProp(lockedProp, Boolean.FALSE));
        t1.addTicketProp(new BooleanTicketProp(soldProp, Boolean.TRUE));
        t1.addTicketProp(new StringTicketProp(lockedByProp, "SAMPLE_API_KEY"));
        t1.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-10-13T13:33:50-04:00")));
        t1.addTicketProp(new IntegerTicketProp(priceProp, 50));
        t1.addTicketProp(new BooleanTicketProp(halfPriceProp, Boolean.TRUE));

        t2.addTicketProp(new IntegerTicketProp(seatNumberProp, 3));
        t2.addTicketProp(new StringTicketProp(sectionProp, "A"));
        t2.addTicketProp(new DateTimeTicketProp(performanceProp, DateUtil.parseDate("2010-10-13T13:33:50-04:00")));
        t2.addTicketProp(new StringTicketProp(tierProp, "SILVER"));
        t2.addTicketProp(new BooleanTicketProp(lockedProp, Boolean.TRUE));
        t2.addTicketProp(new BooleanTicketProp(soldProp, Boolean.TRUE));
        t2.addTicketProp(new StringTicketProp(lockedByProp, "SAMPLE_API_KEY"));
        t2.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-10-13T13:33:50-04:00")));
        t2.addTicketProp(new IntegerTicketProp(priceProp, 50));
        t2.addTicketProp(new BooleanTicketProp(halfPriceProp, Boolean.TRUE));

        t3.addTicketProp(new IntegerTicketProp(seatNumberProp, 3));
        t3.addTicketProp(new StringTicketProp(sectionProp, "A"));
        t3.addTicketProp(new DateTimeTicketProp(performanceProp, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t3.addTicketProp(new StringTicketProp(tierProp, "BRONZE"));
        t3.addTicketProp(new BooleanTicketProp(lockedProp, Boolean.TRUE));
        t3.addTicketProp(new BooleanTicketProp(soldProp, Boolean.TRUE));
        t3.addTicketProp(new StringTicketProp(lockedByProp, "SAMPLE_API_KEY"));
        t3.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t3.addTicketProp(new IntegerTicketProp(priceProp, 50));
        t3.addTicketProp(new BooleanTicketProp(halfPriceProp, Boolean.TRUE));

        t4.addTicketProp(new IntegerTicketProp(seatNumberProp, 3));
        t4.addTicketProp(new StringTicketProp(sectionProp, "A"));
        t4.addTicketProp(new DateTimeTicketProp(performanceProp, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t4.addTicketProp(new StringTicketProp(tierProp, "GOLD"));
        t4.addTicketProp(new BooleanTicketProp(lockedProp, Boolean.FALSE));
        t4.addTicketProp(new BooleanTicketProp(soldProp, Boolean.TRUE));
        t4.addTicketProp(new StringTicketProp(lockedByProp, "SAMPLE_API_KEY"));
        t4.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t4.addTicketProp(new IntegerTicketProp(priceProp, 50));
        t4.addTicketProp(new BooleanTicketProp(halfPriceProp, Boolean.TRUE));

        t5.addTicketProp(new IntegerTicketProp(seatNumberProp, 3));
        t5.addTicketProp(new StringTicketProp(sectionProp, "A"));
        t5.addTicketProp(new DateTimeTicketProp(performanceProp, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t5.addTicketProp(new StringTicketProp(tierProp, "GOLD"));
        t5.addTicketProp(new BooleanTicketProp(lockedProp, Boolean.FALSE));
        t5.addTicketProp(new BooleanTicketProp(soldProp, Boolean.TRUE));
        t5.addTicketProp(new StringTicketProp(lockedByProp, "SAMPLE_API_KEY"));
        t5.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t5.addTicketProp(new IntegerTicketProp(priceProp, 50));
        t5.addTicketProp(new BooleanTicketProp(halfPriceProp, Boolean.TRUE));

        t6.addTicketProp(new IntegerTicketProp(seatNumberProp, 3));
        t6.addTicketProp(new StringTicketProp(sectionProp, "B"));
        t6.addTicketProp(new DateTimeTicketProp(performanceProp, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t6.addTicketProp(new StringTicketProp(tierProp, "GOLD"));
        t6.addTicketProp(new BooleanTicketProp(lockedProp, Boolean.TRUE));
        t6.addTicketProp(new BooleanTicketProp(soldProp, Boolean.FALSE));
        t6.addTicketProp(new StringTicketProp(lockedByProp, "SAMPLE_API_KEY"));
        t6.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t6.addTicketProp(new IntegerTicketProp(priceProp, 50));
        t6.addTicketProp(new BooleanTicketProp(halfPriceProp, Boolean.TRUE));

        t7.addTicketProp(new IntegerTicketProp(seatNumberProp, 3));
        t7.addTicketProp(new StringTicketProp(sectionProp, "B"));
        t7.addTicketProp(new DateTimeTicketProp(performanceProp, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t7.addTicketProp(new StringTicketProp(tierProp, "GOLD"));
        t7.addTicketProp(new BooleanTicketProp(lockedProp, Boolean.FALSE));
        t7.addTicketProp(new BooleanTicketProp(soldProp, Boolean.FALSE));
        t7.addTicketProp(new StringTicketProp(lockedByProp, "SAMPLE_API_KEY"));
        t7.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-10-14T13:33:50-04:00")));
        t7.addTicketProp(new IntegerTicketProp(priceProp, 250));
        t7.addTicketProp(new BooleanTicketProp(halfPriceProp, Boolean.TRUE));

        t8.addTicketProp(new IntegerTicketProp(seatNumberProp, 3));
        t8.addTicketProp(new StringTicketProp(sectionProp, "B"));
        t8.addTicketProp(new DateTimeTicketProp(performanceProp, DateUtil.parseDate("2010-10-16T13:33:50-04:00")));
        t8.addTicketProp(new StringTicketProp(tierProp, "GOLD"));
        t8.addTicketProp(new BooleanTicketProp(lockedProp, Boolean.FALSE));
        t8.addTicketProp(new BooleanTicketProp(soldProp, Boolean.FALSE));
        t8.addTicketProp(new StringTicketProp(lockedByProp, "SAMPLE_API_KEY"));
        t8.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-10-16T13:33:50-04:00")));
        t8.addTicketProp(new IntegerTicketProp(priceProp, 150));
        t8.addTicketProp(new BooleanTicketProp(halfPriceProp, Boolean.TRUE));

        t9.addTicketProp(new IntegerTicketProp(seatNumberProp, 3));
        t9.addTicketProp(new StringTicketProp(sectionProp, "B"));
        t9.addTicketProp(new DateTimeTicketProp(performanceProp, DateUtil.parseDate("2010-10-15T13:33:50-04:00")));
        t9.addTicketProp(new StringTicketProp(tierProp, "GOLD"));
        t9.addTicketProp(new BooleanTicketProp(lockedProp, Boolean.FALSE));
        t9.addTicketProp(new BooleanTicketProp(soldProp, Boolean.FALSE));
        t9.addTicketProp(new StringTicketProp(lockedByProp, "SAMPLE_API_KEY"));
        t9.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-10-15T13:33:50-04:00")));
        t9.addTicketProp(new IntegerTicketProp(priceProp, 25));
        t9.addTicketProp(new BooleanTicketProp(halfPriceProp, Boolean.TRUE));

        t10.addTicketProp(new IntegerTicketProp(seatNumberProp, 3));
        t10.addTicketProp(new StringTicketProp(sectionProp, "C"));
        t10.addTicketProp(new DateTimeTicketProp(performanceProp, DateUtil.parseDate("2010-10-15T13:33:50-04:00")));
        t10.addTicketProp(new StringTicketProp(tierProp, "GOLD"));
        t10.addTicketProp(new BooleanTicketProp(lockedProp, Boolean.FALSE));
        t10.addTicketProp(new BooleanTicketProp(soldProp, Boolean.FALSE));
        t10.addTicketProp(new StringTicketProp(lockedByProp, "SAMPLE_API_KEY"));
        t10.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-10-15T13:33:50-04:00")));
        t10.addTicketProp(new IntegerTicketProp(priceProp, 25));
        t10.addTicketProp(new BooleanTicketProp(halfPriceProp, Boolean.TRUE));

        t1 = apa.saveTicket(t1);
        t2 = apa.saveTicket(t2);
        t3 = apa.saveTicket(t3);
        t4 = apa.saveTicket(t4);
        t5 = apa.saveTicket(t5);
        t6 = apa.saveTicket(t6);
        t7 = apa.saveTicket(t7);
        t8 = apa.saveTicket(t8);
        t9 = apa.saveTicket(t9);
        t10 = apa.saveTicket(t10);

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
