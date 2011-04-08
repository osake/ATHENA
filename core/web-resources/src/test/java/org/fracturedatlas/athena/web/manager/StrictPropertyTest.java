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

package org.fracturedatlas.athena.web.manager;

import com.google.gson.Gson;
import org.fracturedatlas.athena.apa.exception.ApaException;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.apa.impl.jpa.IntegerTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.PropValue;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.StringTicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.web.util.BaseManagerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;


public class StrictPropertyTest extends BaseManagerTest {

    RecordManager manager;
    Gson gson = JsonUtil.getGson();

    public StrictPropertyTest() throws Exception {
        super();
        manager = (RecordManager)context.getBean("recordManager");
    }

    @After
    public void tearDown() {
        teardownTickets();
    }

//    @Test
//    public void testMarkBooleanFieldStrict() throws Exception {
//        try {
//            PropField pf = apa.savePropField(new PropField(ValueType.BOOLEAN, "STRICT_PROP", StrictType.STRICT));
//            fail("Should have gotten an InvalidValueException");
//        } catch (ApaException ae) {
//            //pass
//        }
//    }
//
//    @Test
//    public void testUpdateTicketStrictProperty() throws Exception {
//
//        JpaRecord t = new JpaRecord();
//
//        PropField pf = apa.savePropField(new PropField(ValueType.STRING, "STRICT_PROP", StrictType.STRICT));
//        PropValue v1 = apa.savePropValue(new PropValue(pf, "WXYZ"));
//        PropValue v2 = apa.savePropValue(new PropValue(pf, "UPDATED"));
//        propFieldsToDelete.add(pf);
//        t.setType("ticket");
//        StringTicketProp prop = new StringTicketProp(pf, "WXYZ");
//        t.addTicketProp(prop);
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        PTicket expectedPTicket = t.toClientTicket();
//        expectedPTicket.put(pf.getName(), "UPDATED");
//        PTicket savedTicket = manager.createRecord("ticket", expectedPTicket);
//        assertTrue(expectedPTicket.equals(savedTicket));
//    }
//
//    @Test
//    public void testUpdateTicketStrictPropertyInvalid() throws Exception {
//
//        JpaRecord t = new JpaRecord();
//
//        PropField pf = apa.savePropField(new PropField(ValueType.STRING, "STRICT_PROP", StrictType.STRICT));
//        PropValue v1 = apa.savePropValue(new PropValue(pf, "WXYZ"));
//        PropValue v2 = apa.savePropValue(new PropValue(pf, "UPDATED"));
//        propFieldsToDelete.add(pf);
//
//
//        StringTicketProp prop = new StringTicketProp(pf, "WXYZ");
//        t.addTicketProp(prop);
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        PTicket expectedPTicket = t.toClientTicket();
//        expectedPTicket.put(pf.getName(), "THIS_SHOULD_FAIL");
//        try {
//            PTicket savedTicket = manager.createRecord("ticket", expectedPTicket);
//            fail("Should have gotten an InvalidValueException");
//        } catch (InvalidValueException ive) {
//            //pass
//        }
//    }
//
//    @Test
//    public void testUpdateTicketStrictPropertyInteger() throws Exception {
//
//        JpaRecord t = new JpaRecord();
//
//        PropField pf = apa.savePropField(new PropField(ValueType.INTEGER, "NUM", StrictType.STRICT));
//        PropValue v1 = apa.savePropValue(new PropValue(pf, "1"));
//        PropValue v2 = apa.savePropValue(new PropValue(pf, "2"));
//        PropValue v3 = apa.savePropValue(new PropValue(pf, "3"));
//        PropValue v4 = apa.savePropValue(new PropValue(pf, "4"));
//        PropValue v5 = apa.savePropValue(new PropValue(pf, "5"));
//        propFieldsToDelete.add(pf);
//
//        IntegerTicketProp prop = new IntegerTicketProp(pf, 2);
//        t.addTicketProp(prop);
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        PTicket expectedPTicket = t.toClientTicket();
//        expectedPTicket.put(pf.getName(), "5");
//        PTicket savedTicket = manager.createRecord("ticket", expectedPTicket);
//        assertTrue(expectedPTicket.equals(savedTicket));
//    }
//
//    @Test
//    public void testUpdateTicketStrictPropertyInvalidInteger() throws Exception {
//
//        JpaRecord t = new JpaRecord();
//
//        PropField pf = apa.savePropField(new PropField(ValueType.INTEGER, "NUM", StrictType.STRICT));
//        PropValue v1 = apa.savePropValue(new PropValue(pf, "1"));
//        PropValue v2 = apa.savePropValue(new PropValue(pf, "2"));
//        PropValue v3 = apa.savePropValue(new PropValue(pf, "3"));
//        PropValue v4 = apa.savePropValue(new PropValue(pf, "4"));
//        PropValue v5 = apa.savePropValue(new PropValue(pf, "5"));
//        propFieldsToDelete.add(pf);
//
//        IntegerTicketProp prop = new IntegerTicketProp(pf, 2);
//        t.addTicketProp(prop);
//        t.setType("ticket");
//        t = apa.saveTicket(t);
//        ticketsToDelete.add(t);
//
//        PTicket expectedPTicket = t.toClientTicket();
//        expectedPTicket.put(pf.getName(), "6");
//        try {
//            PTicket savedTicket = manager.createRecord("ticket", expectedPTicket);
//            fail("Should have gotten an InvalidValueException");
//        } catch (InvalidValueException ive) {
//            //pass
//        }
//    }
}
