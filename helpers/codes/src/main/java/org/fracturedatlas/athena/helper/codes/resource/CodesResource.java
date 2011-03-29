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

package org.fracturedatlas.athena.helper.codes.resource;

import com.google.gson.Gson;
import com.sun.jersey.api.NotFoundException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/codes")
@Consumes({"application/json"})
@Produces({"application/json"})
public class CodesResource {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    RecordManager recordManager;

    Gson gson = JsonUtil.getGson();

    public static final String CODE_TYPE = "code";

    @GET
    @Path("/{id}")
    public Object get(@PathParam("id") String id) throws NotFoundException {
        String type = CODE_TYPE;
        Ticket ticket = recordManager.getTicket(CODE_TYPE, id);
        if (ticket == null) {
            type = StringUtils.capitalize(type);
            throw new NotFoundException(type + " with id [" + id + "] was not found");
        } else {
            return ticket;
        }
    }

}
