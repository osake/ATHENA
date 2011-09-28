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

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.ArrayList;
import javax.ws.rs.core.MultivaluedMap;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.exception.AthenaException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.*;

public class FindTicketsManagerTest {

    RecordManager manager;
    @Mock private ApaAdapter mockApa;

    public FindTicketsManagerTest() {
        manager = new RecordManager();
    }

    //Mimics this: /tix/tickets?something=4&somethingElse=
    @Test
    public void testFindTicketsNullOrBlankValue() {
        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.put("searchParam", null);
        try{
            manager.findRecords("someType", queryParams);
            fail("Looking for AthenaException");
        } catch (AthenaException ae) {
            //cool
        }

        queryParams.put("searchParam", new ArrayList());
        try{
            manager.findRecords("someType", queryParams);
            fail("Looking for AthenaException");
        } catch (AthenaException ae) {
            //cool
        }

        ArrayList vals = new ArrayList();
        vals.add("");
        queryParams.put("searchParam", vals);
        try{
            manager.findRecords("someType", queryParams);
            fail("Looking for AthenaException");
        } catch (AthenaException ae) {
            //cool
        }

        vals = new ArrayList();
        vals.add("eq");
        queryParams.put("searchParam", vals);
        try{
            manager.findRecords("someType", queryParams);
            fail("Looking for AthenaException");
        } catch (AthenaException ae) {
            //cool
        }

    }

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        manager.setApa(mockApa);
    }
}
