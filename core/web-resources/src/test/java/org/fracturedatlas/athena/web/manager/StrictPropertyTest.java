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

import java.util.List;
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

    @Test
    public void testMarkBooleanFieldStrict() throws Exception {
        try {
            PropField pf = apa.savePropField(new PropField(ValueType.BOOLEAN, "STRICT_PROP", StrictType.STRICT));
            fail("Should have gotten an InvalidValueException");
        } catch (ApaException ae) {
            //pass
        }
    }

    @Test
    public void testUpdateTicketStrictProperty() throws Exception {

        PTicket t = new PTicket();

        PropField pf = apa.savePropField(new PropField(ValueType.STRING, "STRICT_PROP", StrictType.STRICT));
        PropValue v1 = apa.savePropValue(new PropValue(pf, "WXYZ"));
        PropValue v2 = apa.savePropValue(new PropValue(pf, "UPDATED"));
        propFieldsToDelete.add(pf);
        t.setType("ticket");
        t.put("STRICT_PROP", "WXYZ");
        t = apa.saveRecord(t);
        ticketsToDelete.add(t);

        t.put(pf.getName(), "UPDATED");
        PTicket savedTicket = manager.createRecord("ticket", t);
        assertTrue(t.equals(savedTicket));
    }

    @Test
    public void testUpdateTicketStrictPropertyInvalid() throws Exception {


        PropField pf = apa.savePropField(new PropField(ValueType.STRING, "STRICT_PROP", StrictType.STRICT));
        PropValue v1 = apa.savePropValue(new PropValue(pf, "WXYZ"));
        PropValue v2 = apa.savePropValue(new PropValue(pf, "UPDATED"));
        propFieldsToDelete.add(pf);
        PTicket t = new PTicket();
        t.setType("ticket");
        t.put("STRICT_PROP", "WXYZ");
        t = apa.saveRecord(t);
        ticketsToDelete.add(t);

        t.put("STRICT_PROP", "NOT_VALID");
        try {
            manager.createRecord("ticket", t);
            fail("Should have thrown IVE");
        } catch (InvalidValueException ive) {
            System.out.println(ive.getMessage());
        }

        //return it to the correct value
        t.put("STRICT_PROP", "WXYZ");

        PTicket savedTicket = (PTicket)manager.getRecords("ticket", t.getId());
        savedTicket.setType("ticket");
        System.out.println(savedTicket);
        System.out.println(t);
        assertTrue(t.equals(savedTicket));
    }

    @Test
    public void testSsaveTicketStrictPropertyInvalidInteger() throws Exception {

        PTicket t = new PTicket();

        PropField pf = apa.savePropField(new PropField(ValueType.INTEGER, "NUM", StrictType.STRICT));
        PropValue v1 = apa.savePropValue(new PropValue(pf, "1"));
        PropValue v2 = apa.savePropValue(new PropValue(pf, "2"));
        PropValue v3 = apa.savePropValue(new PropValue(pf, "3"));
        PropValue v4 = apa.savePropValue(new PropValue(pf, "4"));
        PropValue v5 = apa.savePropValue(new PropValue(pf, "5"));
        propFieldsToDelete.add(pf);

        IntegerTicketProp prop = new IntegerTicketProp(pf, 2);
        t.put("NUM", "6");
        t.setType("ticket");

        try {
            PTicket savedTicket = manager.createRecord("ticket", t);
            fail("Should have gotten an InvalidValueException");
        } catch (InvalidValueException ive) {
            //pass
        }
    }
}
