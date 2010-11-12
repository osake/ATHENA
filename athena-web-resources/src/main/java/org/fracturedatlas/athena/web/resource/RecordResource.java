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

import com.google.gson.Gson;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.fracturedatlas.athena.web.manager.RecordManager;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.jersey.api.NotFoundException;
import java.util.Collection;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.web.exception.ForbiddenException;
import org.fracturedatlas.athena.web.exception.ObjectNotFoundException;
import org.fracturedatlas.athena.apa.model.TicketProp;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("")
@Consumes({"application/json"})
@Produces({"application/json"})
public class RecordResource {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Autowired
    RecordManager recordManager;

    Gson gson = JsonUtil.getGson();

    @GET
    @Path("{type}/{id}")
    public Object get(@PathParam("id") String id) throws NotFoundException {
        Ticket ticket = recordManager.getTicket(id);
        if (ticket == null) {
            throw new NotFoundException("Ticket with id [" + id + "] was not found");
        } else {
            return ticket;
        }
    }

    /**
     * This returns TicketProp[] mostly because of Java TypeErasure + Jersey's MessageBodyWriter
     * See: JsonTicketCollectionSerializer, JsonPropsSerializer
     * @param id
     * @return
     * @throws NotFoundException
     */
    @GET
    @Path("{type}/{id}/props")
    public TicketProp[] getProps(@PathParam("id") String id) throws NotFoundException {
        Ticket ticket = recordManager.getTicket(id);
        if (ticket == null) {
            throw new NotFoundException("Ticket with id [" + id + "] was not found");
        } else {
            return (TicketProp[])ticket.getTicketProps().toArray(new TicketProp[0]);
        }
    }

    @DELETE
    @Path("{type}/{id}")
    public void delete(@PathParam("id") String id) throws NotFoundException {
        Ticket ticket = recordManager.getTicket(id);
        if (ticket == null) {
            throw new NotFoundException("Ticket with id [" + id + "] was not found");
        } else {
            recordManager.deleteTicket(ticket);
        }
    }

    @DELETE
    @Path("{type}/{id}/props/{name}")
    public void delete(@PathParam("id") String id,
                       @PathParam("name") String name) throws NotFoundException {
        try {
            recordManager.deletePropertyFromTicket(name, id);
        } catch (ObjectNotFoundException onfe) {
            //Catching this because ONFE maps to a BAD_REQUEST.  Since the id's are on the URL
            //We want to throw a 404 instead
            logger.error(onfe.getMessage(),onfe);
            throw new NotFoundException(onfe.getMessage());
        }
    }

    /**
     * Get tickets based on criteria specified in ui.getQueryParameters()
     * All search parameters will be bundled together as AND queries
     * Calls to this method with blank query parameters (trying to get a list of all tix)
     * will be returned a 405 (Method not allowed)
     * @param ui
     * @return
     */
    @GET
    @Path("{type}/")
    public Collection<Ticket> search(@Context UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();

        if (queryParams.isEmpty()) {
            throw new ForbiddenException("You must specify at least one query parameter when searching for tickets");
        } 

        return recordManager.findTickets(queryParams);
    }

    /**
     * Save a ticket.  If a property is specified twice, the latter value will be used.
     *
     * @param json the json representation of a client ticket (PTicket
     * @return the saved ticket
     * @throws Exception if the json was malformed
     */
    @POST
    @Path("{type}/")
    public Object save(PTicket pTicket) throws Exception {
        Ticket ticket = recordManager.saveTicketFromClientRequest(pTicket);
        return ticket;
    }

    /**
     * Save a ticket.  If a property is specified twice, the latter value will be used.
     *
     * @param json the json representation of a client ticket (PTicket
     * @return the saved ticket
     * @throws Exception if the json was malformed
     */
    @PUT
    @Path("{type}/{id}")
    public Object update(@PathParam("id") String id, PTicket pTicket) throws Exception {
        Ticket ticket = recordManager.updateTicketFromClientTicket(pTicket, id);
        return ticket;
    }
}