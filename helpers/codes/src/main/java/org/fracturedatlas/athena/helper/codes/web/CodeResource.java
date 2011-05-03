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

import com.google.gson.Gson;
import com.sun.jersey.api.NotFoundException;
import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.codes.model.Code;
import org.fracturedatlas.athena.helper.codes.manager.CodeManager;
import org.fracturedatlas.athena.web.exception.ForbiddenException;
import org.fracturedatlas.athena.web.exception.ObjectNotFoundException;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Path("/codes")
@Consumes({"application/json"})
@Produces({"application/json"})
@Component
public class CodeResource {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    CodeManager codeManager;

    Gson gson = JsonUtil.getGson();

    public static final String CODE_TYPE = "code";

    @GET
    @Path("/{id}")
    public Object get(@PathParam("id") String id) throws NotFoundException {
        Code c = codeManager.getCode(id);
        if(c == null) {
            throw new NotFoundException("Code with id ["+id+"] was not found");
        } else {
            return c;
        }
    }

    @POST
    @Path("")
    public Code create(Code code) throws Exception {
        Code createdCode = codeManager.saveCode(code);
        return createdCode;
    }

    @PUT
    @Path("/{id}")
    public Object update(@PathParam("id") String id, Code code) throws Exception {
        return codeManager.saveCode(code);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") String id) throws Exception {
        codeManager.deleteCode(id);
    }

    @GET
    @Path("/{id}/tickets")
    public Collection<PTicket> findTickets(@PathParam("id") String codeId, @Context UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        try{
            return codeManager.findTickets(codeId, queryParams);
        } catch (ObjectNotFoundException onfe) {
            throw new NotFoundException(onfe.getMessage());
        }
    }

    @DELETE
    @Path("/{id}/tickets/{ticketId}")
    public void delete(@PathParam("id") String id, @PathParam("ticketId") String ticketId) throws Exception {
        codeManager.deleteCodeFromTicket(id, ticketId);
    }

    /**
     * Get tickets based on criteria specified in ui.getQueryParameters()
     * All search parameters will be bundled together as AND queries
     * Calls to this method with blank query parameters (trying to get a list of all records)
     * will be returned a 405 (Method not allowed)
     * @param ui
     * @return
     */
    @GET
    @Path("")
    public Collection<PTicket> search(@Context UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        if (queryParams.isEmpty()) {
            throw new ForbiddenException("You must specify at least one query parameter when searching for codes");
        }

        return codeManager.findCodes(queryParams);
    }
}
