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
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PluginContainerTest extends BaseTixContainerTest {

    String path = RECORDS_PATH;
    Gson gson = JsonUtil.getGson();
    PTicket jim = null;

    @Test
    public void testUsePlugin() {
        path = "/people/" + IdAdapter.toString(jim.getId()) + "/tweets";
        String jsonString = tix.path(path).get(String.class);
        PTicket twitterFeed = gson.fromJson(jsonString, PTicket.class);
        System.out.println(twitterFeed);
    }

    @Before
    public void addTickets() throws Exception {
        jim = new PTicket();

        jim.setType("person");

        addPropField(ValueType.STRING, "name", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "twitterHandle", StrictType.NOT_STRICT);


        jim = addRecord("person",
                        "name", "Jimmy",
                        "twitterHandle", "gsmoore");
    }

}
