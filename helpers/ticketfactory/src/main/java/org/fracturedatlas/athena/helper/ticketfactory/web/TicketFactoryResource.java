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
package org.fracturedatlas.athena.helper.ticketfactory.web;

import com.google.gson.Gson;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.ticketfactory.manager.TicketFactoryManager;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/meta/ticketfactory")
@Consumes({"application/json"})
@Produces({"application/json"})
@Component
public class TicketFactoryResource {

    Gson gson = JsonUtil.getGson();
    
    @Autowired
    TicketFactoryManager ticketFactoryManager;

    @POST
    @Path("")
    public Object createTickets(PTicket pTicket) throws Exception {
        ticketFactoryManager.createTickets(pTicket);
        return Response.status(Response.Status.OK).
                type("text/plain").
                build();
    }
}
