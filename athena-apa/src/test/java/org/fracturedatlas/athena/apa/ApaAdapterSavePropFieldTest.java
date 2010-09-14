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

import java.text.ParseException;
import org.fracturedatlas.athena.apa.exception.ApaException;
import org.fracturedatlas.athena.apa.exception.ImmutableObjectException;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.model.DateTimeTicketProp;
import org.fracturedatlas.athena.apa.model.IntegerTicketProp;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.PropValue;
import org.fracturedatlas.athena.apa.model.StrictType;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ApaAdapterSavePropFieldTest extends BaseApaAdapterTest {
    
    public ApaAdapterSavePropFieldTest() throws Exception {
        super();
    }

    @After
    public void teardownTickets() {
        for (Ticket t : ticketsToDelete) {
            try {
                apa.deleteTicket(t);
            } catch (Exception ignored) {
                    ignored.printStackTrace();
            }
        }

        for (PropField pf : propFieldsToDelete) {
            try {
                    apa.deletePropField(pf);
            } catch (Exception ignored) {
                    ignored.printStackTrace();
            }
        }
    }

    @Test
    public void testSavePropField() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "TEST", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        PropField savedField = apa.getPropField(field.getId());
        assertEquals(field, savedField);
    }

    @Test
    public void testSaveStrictPropField() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "TEST", StrictType.STRICT));
        propFieldsToDelete.add(field);
        PropField savedField = apa.getPropField(field.getId());
        System.out.println(field.getPropValues());
        System.out.println(savedField.getPropValues());
        assertEquals(field, savedField);
    }

    @Test
    public void testSaveStrictBooleanPropField() {
        try{
            PropField field = apa.savePropField(new PropField(ValueType.BOOLEAN, "TEST", StrictType.STRICT));
            propFieldsToDelete.add(field);
            fail("Should have thrown ApaException");
        } catch (ApaException ae) {
            //pass!
        }

    }

    @Test
    public void testSavePropFieldAndAddSomeValues() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "TEST", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);

        PropValue value = new PropValue(field, "TEST");
        PropValue value1 = new PropValue(field, "TEST1");
        PropValue value2 = new PropValue(field, "TEST2");

        field.addPropValue(value);
        field.addPropValue(value1);
        field.addPropValue(value2);

        field = apa.savePropField(field);
        PropField savedField = apa.getPropField(field.getId());
        assertEquals(field, savedField);
    }

    @Test
    public void testSavePropDuplicateValues() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "TEST", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);

        PropValue value = new PropValue(field, "TEST");
        PropValue value1 = new PropValue(field, "TEST1");
        PropValue value2 = new PropValue(field, "TEST2");

        field.addPropValue(value);
        field.addPropValue(value1);
        field.addPropValue(value2);

        field = apa.savePropField(field);
        PropField savedField = apa.getPropField(field.getId());
        assertEquals(field, savedField);

        PropValue dupe = new PropValue(field, "TEST1");
        field.addPropValue(dupe);
        try {
            field = apa.savePropField(field);
            fail("Should have rejected the duplicate");
        } catch (ApaException ae) {
            //pass
        }
    }

    @Test
    public void testUpdatePropFieldName() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "TEST", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        field.setName("CHANGED_NAME");
        field = apa.savePropField(field);
        PropField savedField = apa.getPropField(field.getId());
        assertEquals(field.getName(), savedField.getName());
        assertEquals(field, savedField);
    }

    @Test
    public void testUpdatePropFieldType() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "TEST", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        field.setValueType(ValueType.INTEGER);
        try{
            field = apa.savePropField(field);
            fail("Should have thrown ImmutableObjectException");
        } catch (ImmutableObjectException ae) {
            //pass
        }
    }

    @Test
    public void testUpdatePropFieldStrictness() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "TEST", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        field.setStrict(StrictType.STRICT);
        try{
            field = apa.savePropField(field);
            fail("Should have thrown ImmutableObjectException");
        } catch (ImmutableObjectException ae) {
            //pass
        }
    }

    @Test
    public void testUpdatePropFieldStrictness2() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "TEST", StrictType.STRICT));
        propFieldsToDelete.add(field);
        field.setStrict(StrictType.NOT_STRICT);
        try{
            field = apa.savePropField(field);
            fail("Should have thrown ImmutableObjectException");
        } catch (ImmutableObjectException ae) {
            //pass
        }
    }

    @Test
    public void testSavePropFieldDuplicatePropField() {
        PropField field = apa.savePropField(new PropField(ValueType.STRING, "TEST", StrictType.NOT_STRICT));
        propFieldsToDelete.add(field);
        try{
            PropField fail = apa.savePropField(new PropField(ValueType.STRING, "TEST", StrictType.NOT_STRICT));
            fail("Should have thrown ApaException");
        } catch (ApaException ae) {
            //pass!
        }

    }
}
