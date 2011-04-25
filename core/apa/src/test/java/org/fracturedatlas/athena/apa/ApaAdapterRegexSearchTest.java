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
import org.fracturedatlas.athena.apa.exception.InvalidFieldException;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Currently the only Regex supported is '.*'
 * @author gary
 */
public class ApaAdapterRegexSearchTest extends BaseApaAdapterTest {

    AthenaSearch search = getSearcher();

    public ApaAdapterRegexSearchTest() {
        super();
    }

    public AthenaSearch getSearcher() {
        return new AthenaSearch.Builder().type("ticket").build();
    }

    @Test
    public void testSearchWithRegex() {
        search.addConstraint("TIER", Operator.MATCHES, AthenaSearch.ANY_VALUE);
        Set<PTicket> tickets = apa.findTickets(search);
        assertEquals(2, tickets.size());
    }

    @Test
    public void testSearchWithRegex2() {
        search.addConstraint("SEAT_NUMBER", Operator.MATCHES, AthenaSearch.ANY_VALUE);
        Set<PTicket> tickets = apa.findTickets(search);
        assertEquals(3, tickets.size());
    }

    @Test
    public void testSearchWithRegex3() {
        try{
            search.addConstraint("UNKNOWN", Operator.MATCHES, AthenaSearch.ANY_VALUE);
            Set<PTicket> tickets = apa.findTickets(search);
            fail("Looking for InvalidFieldException");
        } catch (InvalidFieldException e) {
            //pass
        }
    }

    @Test
    public void testSearchWithRegex4() {
        search.addConstraint("PROP_NOT_ON_A_TICKET", Operator.MATCHES, AthenaSearch.ANY_VALUE);
        Set<PTicket> tickets = apa.findTickets(search);
        assertEquals(0, tickets.size());
    }

    @Test
    public void testSearchWithRegexFail() {
        try{
            search.addConstraint("SECTION", Operator.MATCHES, "some_other_regex.*");
            Set<PTicket> tickets = apa.findTickets(search);
            fail("Looking for UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            //pass
        }
    }

    @Before
    public void addTickets() throws Exception {

        PTicket t1 = new PTicket("ticket");
        PTicket t2 = new PTicket("ticket");
        PTicket t3 = new PTicket("ticket");

        addPropField(ValueType.STRING, "SEAT_NUMBER", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SECTION", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "TIER", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "PROP_NOT_ON_A_TICKET", StrictType.NOT_STRICT);

        t1.put("SEAT_NUMBER", "3a");
        t1.put("SECTION" , "A");
        t1.put("TIER" , "GOLD");

        t2.put("SEAT_NUMBER", "3b");
        t2.put("SECTION" , "A");
        t2.put("TIER" , "SILVER");

        t3.put("SEAT_NUMBER", "3c");
        t3.put("SECTION" , "A");

        t1 = apa.saveRecord(t1);
        t2 = apa.saveRecord(t2);
        t3 = apa.saveRecord(t3);

        ticketsToDelete.add(t1);
        ticketsToDelete.add(t2);
        ticketsToDelete.add(t3);
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

}
