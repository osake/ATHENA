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

package org.fracturedatlas.athena.helper.codes;

import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.codes.model.Code;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.*;

public class CodeTest {
    Code code;

    @Test
    public void testToRecord() throws Exception {
        String testCode = "TEST_CODE";
        String testDescription = "TEST_DESCRIPTON";
        DateTime testStartDate = new DateTime("2013-03-03T03:09:33Z");
        DateTime testEndDate = new DateTime("2013-02-23T03:19:33Z");
        Integer testPrice = 4000;
        Boolean testEnabled = Boolean.FALSE;

        code = new Code();
        code.setCode(testCode);
        code.setDescription(testDescription);
        code.setStartDate(testStartDate.toDate());
        code.setEndDate(testEndDate.toDate());
        code.setPrice(testPrice);
        code.setEnabled(testEnabled);

        PTicket codeRecord = code.toRecord();

        assertEquals(codeRecord.get("code"), testCode);
        assertEquals(codeRecord.get("description"), testDescription);
        DateTime actualStartDate = new DateTime(codeRecord.get("startDate"));
        assertTrue(actualStartDate.isEqual(testStartDate));
        DateTime actualEndDate = new DateTime(codeRecord.get("endDate"));
        assertTrue(actualEndDate.isEqual(testEndDate));
        assertEquals(codeRecord.get("price"), Integer.toString(testPrice));
        assertEquals(codeRecord.get("enabled"), Boolean.toString(testEnabled));

    }

    @Test
    public void testFromRecord() throws Exception {
        String testCode = "TEST_CODE";
        String testDescription = "TEST_DESCRIPTON";
        String testStartDate = "2013-03-03T03:09:33Z";
        String testEndDate = "2013-02-23T03:19:33Z";
        String testPrice = "4000";
        String testEnabled = "true";

        PTicket codeRecord = new PTicket();

        codeRecord.put("code", testCode);
        codeRecord.put("description", testDescription);
        codeRecord.put("startDate", testStartDate);
        codeRecord.put("endDate", testEndDate);
        codeRecord.put("price", testPrice);
        codeRecord.put("enabled", testEnabled);

        Code code = new Code(codeRecord);

        assertEquals(code.getCode(), testCode);
        assertEquals(code.getDescription(), testDescription);
        assertEquals(code.getStartDate(), DateUtil.parseDate(testStartDate));
        assertEquals(code.getEndDate(), DateUtil.parseDate(testEndDate));
        assertEquals(code.getPrice(), (Integer)Integer.parseInt(testPrice));
        assertEquals(code.getEnabled(), Boolean.parseBoolean(testEnabled));
    }
}
