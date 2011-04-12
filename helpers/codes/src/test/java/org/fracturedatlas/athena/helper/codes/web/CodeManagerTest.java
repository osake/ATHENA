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

import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.codes.manager.CodeManager;
import org.fracturedatlas.athena.helper.codes.model.Code;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CodeManagerTest {
    
    CodeManager manager = new CodeManager();
    
    @Mock private RecordManager mockRecordManager;

    PTicket sampleTicket = new PTicket();

    @Test
    public void testCreateCode() throws Exception {
        Code code = buildCode();
        Code createdCode = manager.createCode(code);
        assertNotNull(createdCode);

        verify(mockRecordManager, times(1)).getTicket("ticket", sampleTicket.getId());
        verify(mockRecordManager, times(code.getTickets().size())).updateRecord("ticket", sampleTicket);
    }

    public void createSampleObjects() {
        sampleTicket.setId("400");
    }

    public Code buildCode() {
        Code code = new Code();
        code.getTickets().add(IdAdapter.toString(sampleTicket.getId()));
        return code;
    }

    @Before
    public void mockupTix() {
        MockitoAnnotations.initMocks(this);
        createSampleObjects();

        when(mockRecordManager.getTicket("ticket", sampleTicket.getId())).thenReturn(sampleTicket);
        when(mockRecordManager.updateRecord("ticket", sampleTicket)).thenReturn(sampleTicket);

        manager.setRecordManager(mockRecordManager);
    }
}
