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

import java.util.Arrays;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ApaAdapterArrayTest extends BaseApaAdapterTest {

    PTicket jim;
    PTicket bob;
    PTicket hank;
    PTicket ravens;
    PTicket chiefs;
    PTicket raiders;

    public ApaAdapterArrayTest() throws Exception {
        super();
    }

    @After
    public void teardown() {
        //super.teardownTickets();
    }

    @Test
    public void testSaveRecordWithArrayValue() {
        jim.getProps().put("teams", Arrays.asList(ravens.getIdAsString(), chiefs.getIdAsString()));

        jim = apa.saveRecord(jim);
        assertEquals(2, jim.getProps().get("teams").size());
        assertTrue(jim.getProps().get("teams").contains(ravens.getIdAsString()));
        assertTrue(jim.getProps().get("teams").contains(chiefs.getIdAsString()));

        jim = apa.getRecord(jim.getType(), jim.getId());
        assertEquals(2, jim.getProps().get("teams").size());
        assertTrue(jim.getProps().get("teams").contains(ravens.getIdAsString()));
        assertTrue(jim.getProps().get("teams").contains(chiefs.getIdAsString()));
    }
//
//    @Test
//    public void testSaveRecordAddArrayValue() {
//        jim.getProps().put("teams", Arrays.asList(ravens.getIdAsString(), chiefs.getIdAsString()));
//        jim = apa.saveRecord(jim);
//        jim.getProps().add("teams", raiders.getIdAsString());
//        jim = apa.saveRecord(jim);
//        assertEquals(3, jim.getProps().get("teams").size());
//        assertTrue(jim.getProps().get("teams").contains(ravens.getIdAsString()));
//        assertTrue(jim.getProps().get("teams").contains(chiefs.getIdAsString()));
//        assertTrue(jim.getProps().get("teams").contains(raiders.getIdAsString()));
//    }
//
//    @Test
//    public void testSaveRecordReduceArrayValues() {
//        jim.getProps().add("teams", ravens.getIdAsString());
//        jim.getProps().add("teams", chiefs.getIdAsString());
//        jim.getProps().add("teams", raiders.getIdAsString());
//        jim = apa.saveRecord(jim);
//        assertEquals(3, jim.getProps().get("teams").size());
//        jim.getProps().put("teams", Arrays.asList(ravens.getIdAsString(), chiefs.getIdAsString()));
//        jim = apa.saveRecord(jim);
//        assertEquals(2, jim.getProps().get("teams").size());
//        assertTrue(jim.getProps().get("teams").contains(ravens.getIdAsString()));
//        assertTrue(jim.getProps().get("teams").contains(chiefs.getIdAsString()));
//    }
//
//    @Test
//    public void testSaveRecordDeleteArrayValue() {
//        jim.getProps().add("teams", ravens.getIdAsString());
//        jim.getProps().add("teams", chiefs.getIdAsString());
//        jim.getProps().add("teams", raiders.getIdAsString());
//        jim = apa.saveRecord(jim);
//        assertEquals(3, jim.getProps().get("teams").size());
//        jim.getProps().remove("teams");
//        jim = apa.saveRecord(jim);
//        assertNull(jim.getProps().get("teams"));
//    }
//
//    @Test
//    public void testSaveRecordWithInvalidArrayValue() {
//        ravens.getProps().put("gameTimes", Arrays.asList("2010-04-04T02:02:02Z", "notadate"));
//        try{
//            jim = apa.saveRecord(ravens);
//            fail("Needed Apa Exception");
//        } catch (InvalidValueException e) {
//            //pass
//        }
//    }
//
//    @Test
//    public void testUpdateRecordWithInvalidArrayValue() {
//        ravens.getProps().put("gameTimes", Arrays.asList("2010-04-04T02:02:02Z"));
//        ravens = apa.saveRecord(ravens);
//        ravens.getProps().add("gameTimes", "notadate");
//        try{
//            ravens = apa.saveRecord(ravens);
//            fail("Needed Apa Exception");
//        } catch (InvalidValueException e) {
//            //pass
//        }
//    }
//
    @Before
    public void setup() {
        /* fans */
        addPropField(ValueType.STRING,"name",Boolean.FALSE);
        addPropField(ValueType.STRING,"teams",Boolean.FALSE);

        /* teams */
        //implied
        //addPropField(ValueType.STRING,"name",Boolean.FALSE);
        addPropField(ValueType.INTEGER,"fans",Boolean.FALSE);
        addPropField(ValueType.DATETIME,"gameTimes",Boolean.FALSE);

        jim = addRecord("fan",
                        "name", "Jim");
        bob = addRecord("fan",
                        "name", "Bob");
        hank = addRecord("fan",
                         "name", "Hank");
        ravens = addRecord("team",
                           "name", "Baltimore Ravens");
        chiefs = addRecord("team",
                           "name", "Kansas City Chiefs");
        raiders = addRecord("team",
                            "name", "Oakland Raiders");
    }

}
