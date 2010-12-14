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

package org.fracturedatlas.athena.helper.ticketfactory.resource.container;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javax.ws.rs.core.MediaType;
import org.fracturedatlas.athena.helper.lock.resource.container.BaseContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class TicketFactoryContainerTest extends BaseContainerTest {
    Gson gson = JsonUtil.getGson();
    String path = "/";

    protected final static String TICKET_FACTORY_PATH = "/meta/ticketfactory";
    public static final String API_KEY = "SAMPLEAPIKEYfowihe9338833wehhfhf";

    @Test
    public void createTickets() throws Exception {
        String jsonPost = "";

        ClientResponse response = tix.path(TICKET_FACTORY_PATH)
                                   .type("application/json")
                                   .header("X-ATHENA-Key", API_KEY)
                                   .post(ClientResponse.class, gson.toJson(jsonPost));
    }

    @Test
    public void createTicketsForPerformanceAlreadyOnSale() throws Exception {
        String jsonPost = "";

        ClientResponse response = tix.path(TICKET_FACTORY_PATH)
                                   .type("application/json")
                                   .header("X-ATHENA-Key", API_KEY)
                                   .post(ClientResponse.class, gson.toJson(jsonPost));
    }

    @Test
    public void createTicketsForPerformanceThatDoesNotExist() throws Exception {
        String jsonPost = "";

        ClientResponse response = tix.path(TICKET_FACTORY_PATH)
                                   .type("application/json")
                                   .header("X-ATHENA-Key", API_KEY)
                                   .post(ClientResponse.class, gson.toJson(jsonPost));
    }

      @Test
      public void shouldMakeAHttpCall() {
        WebResource wr = mock(WebResource.class);
        ClientResponse fakeResponce = mock(ClientResponse.class);
        doReturn(fakeResponce).when(wr).get(ClientResponse.class);

        ClientResponse cr = wr.get(ClientResponse.class);
      }

    //@Before
    //public PTicket createSamplePerformance() {
    //}

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }
}
