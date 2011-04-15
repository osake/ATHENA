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
package org.fracturedatlas.athena.helper.lock.manager;


import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.fracturedatlas.athena.apa.ApaAdapter;
import static org.junit.Assert.*;
import org.fracturedatlas.athena.apa.impl.jpa.*;
import org.fracturedatlas.athena.client.*;
import org.fracturedatlas.athena.id.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseLockManagerTest {

    protected ApaAdapter apa;

    protected List<PTicket> recordsToDelete = new ArrayList<PTicket>();
    protected List<PropField> propFieldsToDelete = new ArrayList<PropField>();
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public BaseLockManagerTest() {
    }

    public void teardownRecords() {
        for (PTicket t : recordsToDelete) {
            try {
                apa.deleteRecord(t.getType(), t.getId());
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        for (PropField pf : propFieldsToDelete) {
            try {
                    apa.deletePropField(pf);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    public void assertTicketsEqual(JpaRecord t, PTicket pTicket, Boolean includeId) {
        if(includeId) {
            assertTrue(IdAdapter.isEqual(t.getId(), pTicket.getId()));
        }

        assertEquals(t.getTicketProps().size(), pTicket.getProps().size());

        for(TicketProp ticketProp : t.getTicketProps()) {
            String value = pTicket.get(ticketProp.getPropField().getName());
            assertEquals(ticketProp.getValueAsString(), value);
        }
    }

    public void assertTicketsEqual(JpaRecord t, PTicket pTicket) {
        assertTicketsEqual(t, pTicket, Boolean.TRUE);
    }

    public PField addPropField(ValueType valueType, String name, Boolean strict) {
        PropField pf = apa.savePropField(new PropField(valueType, name, strict));
        propFieldsToDelete.add(pf);
        return pf.toClientField();
    }

    public PTicket addRecord(String type, String... keyValues) {
        PTicket t = new PTicket(type);
        for(int i=0; i < keyValues.length; i+=2) {
            System.out.println(keyValues[i]);
            t.put(keyValues[i], keyValues[i+1]);
        }
        t = apa.saveRecord(t);
        recordsToDelete.add(t);
        return t;
    }

    public void assertRecordsEqual(PTicket t, PTicket pTicket, Boolean includeId) {
        if(includeId) {
            assertTrue(IdAdapter.isEqual(t.getId(), pTicket.getId()));
        }

        assertEquals(t.getProps().size(), pTicket.getProps().size());

        for(String key : t.getProps().keySet()) {
            assertTrue(pTicket.get(key).equals(t.get(key)));
        }
    }
}

