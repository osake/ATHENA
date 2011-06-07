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
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.junit.*;
import static org.junit.Assert.*;

public class ApaAdapterIndexSearchTest extends BaseApaAdapterTest {
    
    PTicket anne;
    PTicket jim;
    
    public ApaAdapterIndexSearchTest() throws Exception {
        super();
    }  
    
    @Test
    public void searchIndex() {
        AthenaSearch search = new AthenaSearch.Builder().type("person").query("Smith").build();
        Set<PTicket> people = apa.findTickets(search);
        assertEquals(2, people.size());
        assertTrue(people.contains(anne));
        assertTrue(people.contains(jim));
    }
    
    @Test
    public void searchIndexAndGetNothing() {
        AthenaSearch search = new AthenaSearch.Builder().type("person").query("crab").build();
        Set<PTicket> people = apa.findTickets(search);
        assertEquals(0, people.size());
    }
    
    @Test
    public void searchIndexOnSpecificField() {
        AthenaSearch search = new AthenaSearch.Builder().type("person").query("occupation:actor").build();
        Set<PTicket> people = apa.findTickets(search);
        assertEquals(2, people.size());
    }
    
    @Test
    public void searchIndexOnSpecificFieldUnknownField() {
        AthenaSearch search = new AthenaSearch.Builder().type("person").query("shoeSize:actor").build();
        Set<PTicket> people = apa.findTickets(search);
        assertEquals(0, people.size());
    }
    
    @Test
    public void searchIndexOnSpecificFieldAndGetNothing() {
        AthenaSearch search = new AthenaSearch.Builder().type("person").query("occupation:racecar").build();
        Set<PTicket> people = apa.findTickets(search);
        assertEquals(0, people.size());
    }
    
    @Test
    public void searchIndexUpdatedIndex() {
        anne.put("lastName", "Trent");
        apa.saveRecord(anne);
        
        AthenaSearch search = new AthenaSearch.Builder().type("person").query("Trent").build();
        Set<PTicket> people = apa.findTickets(search);
        assertTrue(people.contains(anne));
        assertEquals(1, people.size());
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }
    
    @Before
    public void setup() {
        addPropField(ValueType.STRING, "firstName", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "lastName", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "occupation", StrictType.NOT_STRICT);
        
        jim = addRecord("person",
                  "firstName", "Jim",
                  "lastName", "Smith",
                  "occupation", "engineer");
        anne = addRecord("person",
                  "firstName", "Anne",
                  "lastName", "Smith",
                  "occupation", "teacher");
        addRecord("person",
                  "firstName", "Ben",
                  "lastName", "Bernake",
                  "occupation", "banker");
        addRecord("person",
                  "firstName", "Ben",
                  "lastName", "Affleck",
                  "occupation", "actor");
        addRecord("person",
                  "firstName", "Matt",
                  "lastName", "Damon",
                  "occupation", "actor");
    }
            
}
