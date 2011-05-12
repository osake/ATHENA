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

package org.fracturedatlas.athena.web.resource.container;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import java.text.ParseException;
import static org.junit.Assert.*;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.Test;

public class PostRecordWithArrayValueContainerTest extends BaseTixContainerTest {

    JpaRecord testTicket = new JpaRecord();
    String testTicketJson = "";
    Gson gson = JsonUtil.getGson();

    public PostRecordWithArrayValueContainerTest() throws Exception {
        super();
    }

    @After
    public void teardown() {
        super.teardownRecords();
    }

    @Test
    public void testGetRecordJson() {

        addPropField(ValueType.STRING,"SEAT_NUMBER",Boolean.FALSE);
        addPropField(ValueType.STRING,"SECTION",Boolean.FALSE);
        addPropField(ValueType.STRING,"TIER",Boolean.FALSE);

        PTicket t = createRecord("ticket",
                                 "SEAT_NUMBER", "3D");
        t.getProps().add("TIER", "GOLD");
        t.getProps().add("TIER", "SILVER");
        String path = RECORDS_PATH;
        String jsonResponse = tix.path(path)
                                     .type("application/json")
                                     .post(String.class, gson.toJson(t));
        PTicket pTicket = gson.fromJson(jsonResponse,  PTicket.class);
        assertNotNull(pTicket);
        recordsToDelete.add(pTicket);
    }
}
