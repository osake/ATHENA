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

package org.fracturedatlas.athena.web.manager;

import org.fracturedatlas.athena.web.manager.PropFieldManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.fracturedatlas.athena.web.exception.InvalidFieldNameException;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.PropValue;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.web.util.BaseManagerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

public class FieldNameManagerTest extends BaseManagerTest {

    PropFieldManager manager;

    String testFieldJson = "";
    PropField testField;
    PropValue testValue;
    Logger logger = Logger.getLogger(FieldNameManagerTest.class);
    String path = "fields.json";
    ObjectMapper mapper = JsonUtil.getMapper();

    public FieldNameManagerTest() throws Exception {
        super();
        manager = (PropFieldManager)context.getBean("propFieldManager");
    }

    @After
    public void tearDown() {
        teardownTickets();
    }

    @Test
    public void testCreateFieldWithForbiddenCharacters() throws Exception {
        PropField field = new PropField(ValueType.STRING, "Seat Number", true);
        try{
            manager.savePropField(field);
            fail("Should have thrown InvalidFieldNameException");
        } catch (InvalidFieldNameException e) {
            //pass
        }

    }

    @Test
    public void testCreateFieldWithForbiddenCharacters2() throws Exception {
        PropField field = new PropField(ValueType.STRING, "Seat%Number", true);
        try{
            manager.savePropField(field);
            fail("Should have thrown InvalidFieldNameException");
        } catch (InvalidFieldNameException e) {
            //pass
        }
    }

    @Test
    public void testCreateFieldWithForbiddenCharacters3() throws Exception {
        PropField field = new PropField(ValueType.STRING, "Seat!Number", true);
        try{
            manager.savePropField(field);
            fail("Should have thrown InvalidFieldNameException");
        } catch (InvalidFieldNameException e) {
            //pass
        }
    }

    @Test
    public void testCreateFieldEmptyName() throws Exception {
        PropField field = new PropField(ValueType.STRING, "", true);
        try{
            manager.savePropField(field);
            fail("Should have thrown InvalidFieldNameException");
        } catch (InvalidFieldNameException e) {
            //pass
        }

    }

    @Test
    public void testCreateFieldValidName() throws Exception {
        PropField field = new PropField(ValueType.STRING, "Seat_Number", true);
        PropField actualField = manager.savePropField(field);
        assertEquals(actualField.getName(), "Seat_Number");
        assertEquals(actualField.getValueType(), ValueType.STRING);
        assertEquals(actualField.getStrict(), true);
        assertNotNull(actualField.getId());

        propFieldsToDelete.add(actualField);

    }

    @Test
    public void testCreateFieldValidName2() throws Exception {
        PropField field = new PropField(ValueType.STRING, "____________", true);
        PropField actualField = manager.savePropField(field);
        assertEquals(actualField.getName(), "____________");
        assertEquals(actualField.getValueType(), ValueType.STRING);
        assertEquals(actualField.getStrict(), true);
        assertNotNull(actualField.getId());

        propFieldsToDelete.add(actualField);

    }

    @Test
    public void testCreateFieldValidName3() throws Exception {
        PropField field = new PropField(ValueType.STRING, "_3_4_5_0_P", true);
        PropField actualField = manager.savePropField(field);
        assertEquals(actualField.getName(), "_3_4_5_0_P");
        assertEquals(actualField.getValueType(), ValueType.STRING);
        assertEquals(actualField.getStrict(), true);
        assertNotNull(actualField.getId());

        propFieldsToDelete.add(actualField);

    }

    @Test
    public void testCreateFieldValidName4() throws Exception {
        PropField field = new PropField(ValueType.STRING, "00000000000000", true);
        PropField actualField = manager.savePropField(field);
        assertEquals(actualField.getName(), "00000000000000");
        assertEquals(actualField.getValueType(), ValueType.STRING);
        assertEquals(actualField.getStrict(), true);
        assertNotNull(actualField.getId());

        propFieldsToDelete.add(actualField);

    }

    @Test
    public void testCreateFieldValidName45Chars() throws Exception {
        PropField field = new PropField(ValueType.STRING, "111111111_1111111111A1111111___11111asf11111", true);
        PropField actualField = manager.savePropField(field);
        assertEquals(actualField.getName(), "111111111_1111111111A1111111___11111asf11111");
        assertEquals(actualField.getValueType(), ValueType.STRING);
        assertEquals(actualField.getStrict(), true);
        assertNotNull(actualField.getId());

        propFieldsToDelete.add(actualField);

    }

    @Test
    public void testCreateFieldInvalidFieldLongName() throws Exception {
        PropField field = new PropField(ValueType.STRING, "Seat%11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111", true);
        try{
            manager.savePropField(field);
            fail("Should have thrown InvalidFieldNameException");
        } catch (InvalidFieldNameException e) {
            //pass
        }
    }
}
