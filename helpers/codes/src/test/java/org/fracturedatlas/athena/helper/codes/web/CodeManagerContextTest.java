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

package org.fracturedatlas.athena.helper.codes.web;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.Set;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.codes.manager.CodeManager;
import org.fracturedatlas.athena.helper.codes.model.Code;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.web.exception.ObjectNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CodeManagerContextTest extends BaseManagerTest {

    CodeManager manager;
    PTicket t1;
    PTicket t2;
    PTicket t3;

    public CodeManagerContextTest() throws Exception {
        super();
        manager = (CodeManager)context.getBean("codeManager");
    }

    @Test
    public void testGetCodeDoesNotExist() throws Exception {
        assertNull(manager.getCode("32orino3n"));
    }   

    @Test
    public void testSaveGetDeleteCode() throws Exception {
        Code code = new Code();
        code.setCode("codedcode");
        code.setPrice(300);
        code.getTickets().add(IdAdapter.toString(t1.getId()));
        code.getTickets().add(IdAdapter.toString(t2.getId()));
        code = manager.saveCode(code);

        assertEquals(Integer.toString(300), apa.getRecord(CodeManager.CODED_TYPE, t1.getId()).get(code.getCodeAsFieldName()));
        assertEquals(Integer.toString(300), apa.getRecord(CodeManager.CODED_TYPE, t2.getId()).get(code.getCodeAsFieldName()));

        Code savedCode = manager.getCode(code.getId());
        assertEquals(savedCode.getCode(), code.getCode());
        assertEquals(2, savedCode.getTickets().size());

        manager.deleteCode(code.getId());

        assertNull(manager.getCode(code.getId()));
        assertNull(apa.getRecord(CodeManager.CODED_TYPE, t1.getId()).get(code.getCodeAsFieldName()));
        assertNull(apa.getRecord(CodeManager.CODED_TYPE, t2.getId()).get(code.getCodeAsFieldName()));

    }

    @Test
    public void findTicketsOnCode() throws Exception {
        Code code = new Code();
        code.setCode("codedcode");
        code.setPrice(300);
        code.getTickets().add(IdAdapter.toString(t1.getId()));
        code.getTickets().add(IdAdapter.toString(t2.getId()));
        code = manager.saveCode(code);

        MultivaluedMapImpl queryParams = new MultivaluedMapImpl();
        Set<PTicket> tickets = manager.findTickets(code.getId(), queryParams);
        assertEquals(2, tickets.size());
    }

    public void setupCodeFields() throws Exception {

        addPropField(ValueType.STRING, "code", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "description", StrictType.NOT_STRICT);
        addPropField(ValueType.DATETIME, "startDate", StrictType.NOT_STRICT);
        addPropField(ValueType.DATETIME, "endDate", StrictType.NOT_STRICT);
        addPropField(ValueType.INTEGER, "price", StrictType.NOT_STRICT);
        addPropField(ValueType.BOOLEAN, "enabled", StrictType.NOT_STRICT);
    }

    @Before
    public void addTickets() throws Exception {

        setupCodeFields();

        t1 = new PTicket(CodeManager.CODED_TYPE);
        t2 = new PTicket(CodeManager.CODED_TYPE);
        t3 = new PTicket(CodeManager.CODED_TYPE);

        addPropField(ValueType.INTEGER, "SEAT_NUMBER", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SECTION", StrictType.NOT_STRICT);
        addPropField(ValueType.DATETIME, "PERFORMANCE", StrictType.NOT_STRICT);

        t1.put("SEAT_NUMBER", "3");
        t1.put("SECTION" , "A");
        t1.put("PERFORMANCE" , "2010-10-01T13:33:50-04:00");

        t2.put("SEAT_NUMBER", "3");
        t2.put("SECTION" , "A");
        t2.put("PERFORMANCE" , "2010-10-02T13:33:50-04:00");

        t3.put("SEAT_NUMBER", "3");
        t3.put("SECTION" , "A");
        t3.put("PERFORMANCE" , "2010-10-02T13:33:50-04:00");

        t1 = apa.saveRecord(t1);
        t2 = apa.saveRecord(t2);
        t3 = apa.saveRecord(t3);

        ticketsToDelete.add(t1);
        ticketsToDelete.add(t2);
        ticketsToDelete.add(t3);
    }

    @After
    public void tearDown() {
        teardownTickets();
    }
}
