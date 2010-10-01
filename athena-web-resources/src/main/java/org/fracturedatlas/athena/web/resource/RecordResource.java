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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.fracturedatlas.athena.web.manager.TicketManager;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.jersey.api.NotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.web.exception.ForbiddenException;
import org.fracturedatlas.athena.web.exception.ObjectNotFoundException;
import org.fracturedatlas.athena.web.exception.AthenaException;
import org.fracturedatlas.athena.apa.model.TicketProp;

@Path("")
@Consumes({"application/json"})
@Produces({"application/json"})
public class RecordResource {

    Logger logger = Logger.getLogger(RecordResource.class);
    @Autowired
    TicketManager ticketManager;

    @GET
    @Path("{id}")
    public Object get(@PathParam("id") String id) throws NotFoundException {
        Ticket ticket = ticketManager.getTicket(id);
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
    @Path("{id}/props")
    public TicketProp[] getProps(@PathParam("id") String id) throws NotFoundException {
        Ticket ticket = ticketManager.getTicket(id);
        if (ticket == null) {
            throw new NotFoundException("Ticket with id [" + id + "] was not found");
        } else {
            return (TicketProp[])ticket.getTicketProps().toArray(new TicketProp[0]);
        }
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") String id) throws NotFoundException {
        Ticket ticket = ticketManager.getTicket(id);
        if (ticket == null) {
            throw new NotFoundException("Ticket with id [" + id + "] was not found");
        } else {
            ticketManager.deleteTicket(ticket);
        }
    }

    @DELETE
    @Path("{id}/props/{name}")
    public void delete(@PathParam("id") String id,
                       @PathParam("name") String name) throws NotFoundException {
        try {
            ticketManager.deletePropertyFromTicket(name, id);
        } catch (ObjectNotFoundException onfe) {
            //Catching this because ONFE maps to a BAD_REQUEST.  Since the id's are on the URL
            //We want to throw a 404 instead
            throw new NotFoundException(onfe.getMessage());
        }
    }

    /**
     * Get tickets based on crieteria specified in ui.getQueryParameters()
     * All search paramteres will be bundled together as AND queries
     * Calls to this method with blank query parameters (trying to get a list of all tix)
     * will be returned a 405 (Method not allowed)
     * @param ui
     * @return
     */
    @GET
    @Path("")
    public Collection<Ticket> search(@Context UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();

        if (queryParams.isEmpty()) {
            throw new ForbiddenException("You must specify at least one query parameter when searching for tickets");
        } 

        return ticketManager.findTickets(queryParams);
    }

    /**
     * Save a ticket.  If a property is specified twice, the latter value will be used.
     *
     * @param json the json representation of a client ticket (PTicket
     * @return the saved ticket
     * @throws Exception if the json was malformed
     */
    @POST
    public Object save(String json) throws Exception {
        PTicket pTicket = stringToPTicket(json);
        Ticket ticket = ticketManager.saveTicketFromClientRequest(pTicket);
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
    @Path("{id}")
    public Object update(@PathParam("id") String id, String json) throws Exception {
        PTicket pTicket = stringToPTicket(json);
        Ticket ticket = ticketManager.updateTicketFromClientTicket(pTicket, id);
        return ticket;
    }

    /*
     * This will reutrn a not-null pTicket.  If null, then will throw AthenaException
     */
    private PTicket stringToPTicket(String json) {
        PTicket pTicket = null;

        try {
            pTicket = JsonUtil.getGson().fromJson(json, PTicket.class);
        } catch (com.google.gson.JsonParseException pe) {
            pe.printStackTrace();
            throw new AthenaException("Sent a blank or malformed request body.  Could not make a ticket out of it.");
        }

        if(pTicket == null) {
            throw new AthenaException("Sent a blank or malformed request body.  Could not make a ticket out of it.");
        }

        return pTicket;
    }
}
