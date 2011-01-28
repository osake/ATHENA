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

import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ScrubberTest {

    String testString = null;
    
    @Before
    public void setUpTestString() {
        StringBuffer testStringBuffer = new StringBuffer();
        testStringBuffer.append("{\"creditCard\":")
                        .append("{")
                        .append("\"expirationDate\":\"05/2012\"")
                        .append(",\"cvv\":\"999\"")
                        .append(",\"cardNumber\":\"4111111111111111\"")
                        .append(",\"cardholderName\":\"John Q Ticketbuyer\"")
                        .append("}")
                        .append(",\"amount\":\"4.0\"}");
        testString = testStringBuffer.toString();
    }

    @Test
    public void testFilterNullString() {
        List<String> testFields = new ArrayList<String>();
        testFields.add("cardNumber");

        String targetString = null;

        String scrubbedString = Scrubber.scrubJson(null, testFields);
        assertEquals(targetString, scrubbedString);
    }

    @Test
    public void testFilterEmptyString() {
        List<String> testFields = new ArrayList<String>();
        testFields.add("cardNumber");

        String targetString = "";

        String scrubbedString = Scrubber.scrubJson("", testFields);
        assertEquals(targetString, scrubbedString);
    }

    @Test
    public void testFilterNullFields() {
        List<String> testFields = null;

        String targetString = new StringBuffer()
                .append("{\"creditCard\":")
                .append("{")
                .append("\"expirationDate\":\"05/2012\"")
                .append(",\"cvv\":\"999\"")
                .append(",\"cardNumber\":\"4111111111111111\"")
                .append(",\"cardholderName\":\"John Q Ticketbuyer\"")
                .append("}")
                .append(",\"amount\":\"4.0\"}")
                .toString();

        String scrubbedString = Scrubber.scrubJson(testString, testFields);
        assertEquals(targetString, scrubbedString);
    }

    @Test
    public void testFilterNoFields() {
        List<String> testFields = new ArrayList<String>();

        String targetString = new StringBuffer()
                .append("{\"creditCard\":")
                .append("{")
                .append("\"expirationDate\":\"05/2012\"")
                .append(",\"cvv\":\"999\"")
                .append(",\"cardNumber\":\"4111111111111111\"")
                .append(",\"cardholderName\":\"John Q Ticketbuyer\"")
                .append("}")
                .append(",\"amount\":\"4.0\"}")
                .toString();

        String scrubbedString = Scrubber.scrubJson(testString, testFields);
        assertEquals(targetString, scrubbedString);
    }

    @Test
    public void testFilterOneField() {
        List<String> testFields = new ArrayList<String>();
        testFields.add("cardNumber");

        String targetString = new StringBuffer()
                .append("{\"creditCard\":")
                .append("{")
                .append("\"expirationDate\":\"05/2012\"")
                .append(",\"cvv\":\"999\"")
                .append(",\"cardNumber\":\""+ Scrubber.SCRUBBED +"\"")
                .append(",\"cardholderName\":\"John Q Ticketbuyer\"")
                .append("}")
                .append(",\"amount\":\"4.0\"}")
                .toString();

        String scrubbedString = Scrubber.scrubJson(testString, testFields);
        assertEquals(targetString, scrubbedString);
    }

    @Test
    public void testFilterOneFieldEscapedQuotes() {
        List<String> testFields = new ArrayList<String>();
        testFields.add("cardNumber");

        StringBuffer testStringBuffer = new StringBuffer();
        testStringBuffer.append("{\\\"creditCard\\\":")
                        .append("{")
                        .append("\\\"expirationDate\\\":\\\"05/2012\\\"")
                        .append(",\\\"cvv\\\":\\\"999\\\"")
                        .append(",\\\"cardNumber\\\":\\\"4111111111111111\\\"")
                        .append(",\\\"cardholderName\\\":\\\"John Q Ticketbuyer\\\"")
                        .append("}")
                        .append(",\\\"amount\\\":\\\"4.0\\\"}");
        testString = testStringBuffer.toString();

        String targetString = new StringBuffer()
                .append("{\\\"creditCard\\\":")
                .append("{")
                .append("\\\"expirationDate\\\":\\\"05/2012\\\"")
                .append(",\\\"cvv\\\":\\\"999\\\"")
                .append(",\\\"cardNumber\\\":\\\""+ Scrubber.SCRUBBED +"\\\"")
                .append(",\\\"cardholderName\\\":\\\"John Q Ticketbuyer\\\"")
                .append("}")
                .append(",\\\"amount\\\":\\\"4.0\\\"}")
                .toString();

        String scrubbedString = Scrubber.scrubJson(testString, testFields);

        System.out.println(targetString);
        System.out.println(scrubbedString);

        assertEquals(targetString, scrubbedString);
    }

    @Test
    public void testFilterTwoFields() {
        List<String> testFields = new ArrayList<String>();
        testFields.add("cardNumber");
        testFields.add("expirationDate");


        String targetString = new StringBuffer()
                .append("{\"creditCard\":")
                .append("{")
                .append("\"expirationDate\":\""+ Scrubber.SCRUBBED +"\"")
                .append(",\"cvv\":\"999\"")
                .append(",\"cardNumber\":\""+ Scrubber.SCRUBBED +"\"")
                .append(",\"cardholderName\":\"John Q Ticketbuyer\"")
                .append("}")
                .append(",\"amount\":\"4.0\"}")
                .toString();

        String scrubbedString = Scrubber.scrubJson(testString, testFields);
        assertEquals(targetString, scrubbedString);
    }

    @Test
    public void testFilterTwoFieldsOneAppearsTwice() {
        List<String> testFields = new ArrayList<String>();
        testFields.add("cardNumber");
        testFields.add("expirationDate");

        StringBuffer testStringBuffer = new StringBuffer();
        testStringBuffer.append("{\"creditCard\":")
                        .append("{")
                        .append("\"expirationDate\":\"05/2012\"")
                        .append(",\"cardNumber\":\"4111111111111111\"")
                        .append(",\"cvv\":\"999\"")
                        .append(",\"cardNumber\":\"4111111111111111\"")
                        .append(",\"cardholderName\":\"John Q Ticketbuyer\"")
                        .append("}")
                        .append(",\"amount\":\"4.0\"}");
        testString = testStringBuffer.toString();

        String targetString = new StringBuffer()
                .append("{\"creditCard\":")
                .append("{")
                .append("\"expirationDate\":\""+ Scrubber.SCRUBBED +"\"")
                .append(",\"cardNumber\":\""+ Scrubber.SCRUBBED +"\"")
                .append(",\"cvv\":\"999\"")
                .append(",\"cardNumber\":\""+ Scrubber.SCRUBBED +"\"")
                .append(",\"cardholderName\":\"John Q Ticketbuyer\"")
                .append("}")
                .append(",\"amount\":\"4.0\"}")
                .toString();

        String scrubbedString = Scrubber.scrubJson(testString, testFields);
        assertEquals(targetString, scrubbedString);
    }

    @Test
    public void testFilterTwoFieldsAndOneThatDoesntExist() {
        List<String> testFields = new ArrayList<String>();
        testFields.add("cardNumber");
        testFields.add("expirationDate");
        testFields.add("fake");

        String targetString = new StringBuffer()
                .append("{\"creditCard\":")
                .append("{")
                .append("\"expirationDate\":\""+ Scrubber.SCRUBBED +"\"")
                .append(",\"cvv\":\"999\"")
                .append(",\"cardNumber\":\""+ Scrubber.SCRUBBED +"\"")
                .append(",\"cardholderName\":\"John Q Ticketbuyer\"")
                .append("}")
                .append(",\"amount\":\"4.0\"}")
                .toString();

        String scrubbedString = Scrubber.scrubJson(testString, testFields);
        assertEquals(targetString, scrubbedString);
    }

}
