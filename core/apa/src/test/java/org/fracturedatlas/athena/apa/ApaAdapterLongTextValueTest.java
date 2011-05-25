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

import java.util.Random;
import org.fracturedatlas.athena.apa.exception.ApaException;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ApaAdapterLongTextValueTest extends BaseApaAdapterTest {

    Logger logger = LoggerFactory.getLogger(ApaAdapterLongTextValueTest.class);

    public ApaAdapterLongTextValueTest() throws Exception {
        super();
    }

    @Before
    public void addTickets() {
    }

    @After
    public void teardown() {
        super.teardownTickets();
    }

    @Test
    public void testSaveALongValueMan() {
        Random r = new Random();

        addPropField(ValueType.TEXT, "longTextField", StrictType.NOT_STRICT);

        String longTextVal = new String();
        String alphabet = "0123456789qwertyuioplkjhgfdsazxcvbnm ";
        for(int i=0; i<20000; i++) {
            longTextVal = longTextVal.concat(Character.toString(alphabet.charAt(r.nextInt(alphabet.length()))));
        }
        PTicket ticket = new PTicket();
        ticket.setType("record");
        ticket.put("longTextField", longTextVal);
        ticket = apa.saveRecord(ticket);
        assertNotNull(ticket.getId());
        ticketsToDelete.add(ticket);
        assertEquals(ticket.get("longTextField"), longTextVal);
    }

    @Test
    public void testSearchOnALongValue() {
        Random r = new Random();

        addPropField(ValueType.TEXT, "longTextField", StrictType.NOT_STRICT);

        AthenaSearch search = new AthenaSearch.Builder().type("ticket").and("longTextField", Operator.EQUALS, "food?").build();
        try{
            apa.findTickets(search);
            fail("Should have thrown ApaException");
        } catch (ApaException ae) {
            //pass!
        }
    }
}
