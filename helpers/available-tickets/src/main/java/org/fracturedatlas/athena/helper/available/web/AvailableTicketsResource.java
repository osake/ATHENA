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

package org.fracturedatlas.athena.helper.available.web;

import com.google.gson.Gson;
import com.sun.jersey.api.NotFoundException;
import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.available.manager.AvailableTicketsManager;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Path("/available")
@Consumes({"application/json"})
@Produces({"application/json"})
@Component
public class AvailableTicketsResource {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    AvailableTicketsManager manager;

    Gson gson = JsonUtil.getGson();

    public static final String CODE_TYPE = "code";

    @GET
    @Path("")
    public Collection<PTicket> search(@Context UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        return manager.findTickets("ticket", queryParams);
    }
}
