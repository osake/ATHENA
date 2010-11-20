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

package org.fracturedatlas.athena.util;
import org.junit.Test;
import static org.junit.Assert.*;

public class AllowedCharacterCheckTest {

    @Test
    public void testFilterEmptyString() {
        String testString = new String("");
        assertFalse(AllowedCharacterCheck.confirm(testString));
    }

    @Test
    public void testFilterNoIllegalCharacters() {
        String testString = new String("ABCDEFab_cdef049");
        assertTrue(AllowedCharacterCheck.confirm(testString));
    }

    @Test
    public void testFilterOneIllegalCharacter() {
        String testString = new String("ABCDEF$ab_cdef049");
        assertFalse(AllowedCharacterCheck.confirm(testString));
    }

    @Test
    public void testFilterOneIllegalCharacterFront() {
        String testString = new String("&ABCDEFab_cdef049");
        assertFalse(AllowedCharacterCheck.confirm(testString));
    }

    @Test
    public void testFilterOneIllegalCharacterEnd() {
        String testString = new String("ABCDEFab_cdef049?");
        assertFalse(AllowedCharacterCheck.confirm(testString));
    }

    @Test
    public void testFilterOneIllegalCharacterTotal() {
        String testString = new String("#");
        assertFalse(AllowedCharacterCheck.confirm(testString));
    }

    @Test
    public void testFilterAllIllegal() {
        String testString = new String("#@$%^()?&");
        assertFalse(AllowedCharacterCheck.confirm(testString));
    }

    @Test
    public void testFilterManyIllegalCharacters() {
        String testString = new String("-A-B+C==DE>F<a,b_c;d'e\"\"f-049&.");
        assertFalse(AllowedCharacterCheck.confirm(testString));
    }

    @Test
    public void testFilterOneSpace() {
        String testString = new String("ABCDE Fab_cdef049");
        assertFalse(AllowedCharacterCheck.confirm(testString));
    }

    @Test
    public void testFilterManySpaces() {
        String testString = new String("AB CDE Fab_ cde f0 49");
        assertFalse(AllowedCharacterCheck.confirm(testString));
    }

}
