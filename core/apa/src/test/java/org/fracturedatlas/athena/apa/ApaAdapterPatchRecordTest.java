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

import org.fracturedatlas.athena.apa.exception.ApaException;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.id.IdAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ApaAdapterPatchRecordTest extends BaseApaAdapterTest {

    PTicket ticket;

    public ApaAdapterPatchRecordTest() throws Exception {
        super();
    }

    @After
    public void teardown() {
        super.teardownTickets();
    }
    
    @Before
    public void setupRecord() {
        addPropField(ValueType.STRING, "SEAT", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT1", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SEAT2", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "UNSETPROP", StrictType.NOT_STRICT);

        ticket = new PTicket();
        ticket.setType("record");
        ticket.put("SEAT", "03");
        ticket.put("SEAT1", "13");
        ticket.put("SEAT2", "23");

        ticket = apa.saveRecord(ticket);
        assertNotNull(ticket.getId());
        ticketsToDelete.add(ticket);
    }

    @Test
    public void testPatchRecord() {
        PTicket patch = new PTicket("record");
        patch.put("SEAT", "TWENTYNINE");
        PTicket patchedRecord = apa.patchRecord(ticket.getId(), ticket.getType(), patch);
        assertEquals("13", patchedRecord.get("SEAT1"));
        assertEquals("23", patchedRecord.get("SEAT2"));
        assertEquals("TWENTYNINE", patchedRecord.get("SEAT"));
        assertTrue(IdAdapter.isEqual(ticket.getId(), patchedRecord.getId()));
        assertEquals(3, patchedRecord.getProps().size());
    }

    @Test
    public void testPatchRecordWithNewProp() {
        PTicket patch = new PTicket("record");
        patch.put("UNSETPROP", "new");
        PTicket patchedRecord = apa.patchRecord(ticket.getId(), ticket.getType(), patch);
        assertEquals("13", patchedRecord.get("SEAT1"));
        assertEquals("23", patchedRecord.get("SEAT2"));
        assertEquals("03", patchedRecord.get("SEAT"));
        assertEquals("new", patchedRecord.get("UNSETPROP"));
        assertTrue(IdAdapter.isEqual(ticket.getId(), patchedRecord.getId()));
        assertEquals(4, patchedRecord.getProps().size());
    }

    @Test
    public void testPatchRecordNullPatch() {
        PTicket patch = new PTicket("record");
        patch.put("SEAT", "TWENTYNINE");
        PTicket patchedRecord = apa.patchRecord(ticket.getId(), ticket.getType(), null);
        assertEquals("13", patchedRecord.get("SEAT1"));
        assertEquals("23", patchedRecord.get("SEAT2"));
        assertEquals("03", patchedRecord.get("SEAT"));
        assertTrue(IdAdapter.isEqual(ticket.getId(), patchedRecord.getId()));
        assertEquals(3, patchedRecord.getProps().size());
    }

    @Test
    public void testPatchRecordNullType() {
        PTicket patch = new PTicket("record");
        patch.put("SEAT", "TWENTYNINE");
        try{
            PTicket patchedRecord = apa.patchRecord(ticket.getId(), null, patch);
            fail("Needed ApaException");
        } catch (ApaException ae) {
            //pass
        }
        
    }

    @Test
    public void testPatchRecordInvalidProp() {
        PTicket patch = new PTicket("record");
        patch.put("SEAT", "TWENTYNINE");
        patch.put("NOT_A_PROP", "TWENTYNINE");
        try{
            PTicket patchedRecord = apa.patchRecord(ticket.getId(), null, patch);
            fail("Needed ApaException");
        } catch (ApaException ae) {
            //pass
        }

        //make sure nothing changed
        PTicket unpatchedRecord = apa.getRecord(ticket.getType(), ticket.getId());
        assertEquals("13", unpatchedRecord.get("SEAT1"));
        assertEquals("23", unpatchedRecord.get("SEAT2"));
        assertEquals("03", unpatchedRecord.get("SEAT"));
        assertTrue(IdAdapter.isEqual(ticket.getId(), unpatchedRecord.getId()));
        assertEquals(3, unpatchedRecord.getProps().size());
    }
}
