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

package org.fracturedatlas.athena.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class AthenaSearchBuilderTest {

    @Test
    public void testBuilderOneTerm() {
        AthenaSearchConstraint con = new AthenaSearchConstraint("FOO", Operator.EQUALS, "BAR");
        AthenaSearch search = new AthenaSearch.Builder(con).build();
        assertEquals(1, search.getConstraints().size());
        assertEquals(con, search.getConstraints().get(0));

        Map<String, String> mods = search.getSearchModifiers();
        assertTrue(mods.keySet().isEmpty());
    }

    @Test
    public void testBuilderTwoTerms() {
        AthenaSearchConstraint con1 = new AthenaSearchConstraint("FOO", Operator.EQUALS, "BAR");
        AthenaSearchConstraint con2 = new AthenaSearchConstraint("BIZ", Operator.GREATER_THAN, "BLAH");
        List<AthenaSearchConstraint> cons = new ArrayList<AthenaSearchConstraint>();
        cons.add(con1);
        cons.add(con2);

        AthenaSearch search = new AthenaSearch.Builder(con1)
                               .and(con2)
                               .build();

        assertEquals(2, search.getConstraints().size());
        assertEquals(cons, search.getConstraints());

        Map<String, String> mods = search.getSearchModifiers();
        assertTrue(mods.keySet().isEmpty());
    }

    @Test
    public void testBuilderTwoTermsAndALimit() {
        AthenaSearchConstraint con1 = new AthenaSearchConstraint("FOO", Operator.EQUALS, "BAR");
        AthenaSearchConstraint con2 = new AthenaSearchConstraint("BIZ", Operator.GREATER_THAN, "BLAH");
        List<AthenaSearchConstraint> cons = new ArrayList<AthenaSearchConstraint>();
        cons.add(con1);
        cons.add(con2);

        AthenaSearch search = new AthenaSearch.Builder(con1)
                               .and(con2)
                               .limit(3)
                               .build();

        assertEquals(2, search.getConstraints().size());
        assertEquals(cons, search.getConstraints());

        Map<String, String> mods = search.getSearchModifiers();
        assertEquals("3", mods.get(AthenaSearch.LIMIT));
    }
}
