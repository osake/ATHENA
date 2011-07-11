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
package org.fracturedatlas.athena.reports.manager;

import java.util.List;
import java.util.Map;
import org.fracturedatlas.athena.web.exception.AthenaException;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.junit.Before;
import static org.junit.Assert.*;

public abstract class ReporterTest {
    @Mock protected AthenaComponent mockStage;
    @Mock protected AthenaComponent mockTix;
    @Mock protected AthenaComponent mockOrders;
    
    @Before
    public void mockit() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    public PTicket makeRecord(String type, String... keyValues) {
        PTicket t = new PTicket(type);
        for(int i=0; i < keyValues.length; i+=2) {
            t.put(keyValues[i], keyValues[i+1]);
        }
        return t;
    }
    
    public void assertAthenaException(Reporter reporter, Map<String, List<String>> queryParams) {
        try {
            reporter.getReport(queryParams);
            fail("Should have thrown AthenaException");
        } catch (AthenaException ae) {
            //pass
        }
    }
}
