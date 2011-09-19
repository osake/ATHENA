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
package org.fracturedatlas.athena.client;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RecordUtilTest {
    PTicket record;
    
    @Before
    public void setup() {
        record = new PTicket();
    }
    
    @Test
    public void testHasNoPeopleInformation() {
        record.put("orderId", "349409409"); 
        record.put("organizationId", "1"); 
        assertFalse(RecordUtil.hasAnyPersonInformation(record));
    } 
    
    @Test
    public void testHasAnyPeopleInformation() {
        record.add("firstName", "Joe");
        record.add("lastName", "Smith");
        assertTrue(RecordUtil.hasAnyPersonInformation(record));
    } 
    
    @Test
    public void testHasAnyPeopleInformation2() {
        record.add("firstName", "Joe");
        assertTrue(RecordUtil.hasAnyPersonInformation(record));
    } 
    
    @Test
    public void testHasPeopleInformation() {
        record.add("firstName", "Joe");
        record.add("lastName", "Smith");
        record.add("email", "test@example.com");
        assertTrue(RecordUtil.hasCompletePersonInformation(record));
    } 
    
    @Test
    public void testHasIncompletePeopleInformation() {
        record.add("firstName", "Joe");
        record.add("email", "test@example.com");
        assertFalse(RecordUtil.hasCompletePersonInformation(record));
    } 
    
    @Test
    public void testHasPeopleInformationAndOtherStuff() {
        record.add("firstName", "Joe");
        record.add("lastName", "Smith");
        record.add("email", "test@example.com");
        record.add("height", "74");
        record.add("orders", "109");
        assertTrue(RecordUtil.hasCompletePersonInformation(record));
    } 
    
    @Test
    public void testHasPeopleInformationNullRecord() {
        record = null;
        assertFalse(RecordUtil.hasCompletePersonInformation(record));
    } 
    
    @Test
    public void testHasIncompletePeopleInformationAndOtherStuff() {
        record.add("firstName", "Joe");
        record.add("email", "test@example.com");
        record.add("height", "74");
        record.add("orders", "109");
        assertFalse(RecordUtil.hasCompletePersonInformation(record));
    }
}
