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

import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.PropValue;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.web.util.BaseManagerTest;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class PropValueManagerTest extends BaseManagerTest {

    PropFieldManager manager;

    public PropValueManagerTest() throws Exception {
        super();
        manager = (PropFieldManager) context.getBean("propFieldManager");
    }

    @After
    public void tearDown() {
        teardownTickets();
    }

    @Test
    public void testGetPropValue() throws Exception {
        PropField testField = apa.savePropField(new PropField(ValueType.STRING, "SECTION", StrictType.STRICT));
        propFieldsToDelete.add(testField);

        PropValue testValue = apa.savePropValue(new PropValue(testField, "AA"));
        testField.addPropValue(testValue);
        testField = apa.savePropField(testField);

        PropValue testValue2 = apa.savePropValue(new PropValue(testField, "BB"));
        testField.addPropValue(testValue2);
        testField = apa.savePropField(testField);

        PropValue propValue = manager.getPropValue(testField.getId(), testValue2.getId());
        assertEquals(testValue2.getPropValue(), propValue.getPropValue());
    }
}
