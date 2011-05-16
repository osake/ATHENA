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

package org.fracturedatlas.athena.web.resource;

import java.util.TreeSet;
import javax.ws.rs.core.UriInfo;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

public class RecordResourceTest {
    RecordResource resource = new RecordResource();

    @Mock private RecordManager mockRecordManager;
    @Mock private UriInfo mockUriInfo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        resource.setRecordManager(mockRecordManager);
    }

    @Test
    public void testSearchRelationships() {

        when(mockUriInfo.getQueryParameters()).thenReturn(null);
        when(mockRecordManager.findSubResources("company", "1", "employee", null)).thenReturn(new TreeSet<PTicket>());
        resource.search("companies", "1", "employees", mockUriInfo);
        verify(mockRecordManager, times(1)).findSubResources("company", "1", "employee", null);
    }
}
