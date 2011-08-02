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
import java.util.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.fracturedatlas.athena.web.manager.RecordManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.jersey.api.NotFoundException;
import java.util.Collection;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang.StringUtils;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.web.exception.ForbiddenException;
import org.fracturedatlas.athena.web.exception.ObjectNotFoundException;
import org.fracturedatlas.athena.web.util.JsonUtil;
import com.sun.jersey.core.impl.provider.entity.Inflector;
import javax.ws.rs.core.MediaType;
import org.fracturedatlas.athena.apa.exception.InvalidFieldException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.fracturedatlas.athena.web.manager.AthenaSubResource;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RecordResource {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Autowired
    RecordManager recordManager;

    Gson gson = JsonUtil.getGson();

    @GET
    @Path("{type}/{id}")
    public Object get(@PathParam("type") String type,
                      @PathParam("id") String id,
                      @Context UriInfo ui) throws NotFoundException {
        type = Inflector.getInstance().singularize(type);
        return recordManager.getRecords(type, id, ui.getQueryParameters());
    }

    @DELETE
    @Path("{type}/{id}")
    public void delete(@PathParam("type") String type, @PathParam("id") String id) throws NotFoundException {
        type = Inflector.getInstance().singularize(type);
        recordManager.deleteRecord(type, id);
    }

    @DELETE
    @Path("{type}/{id}/props/{name}")
    public void delete(@PathParam("type") String type,
                       @PathParam("id") String id,
                       @PathParam("name") String name) throws NotFoundException {
        type = Inflector.getInstance().singularize(type);
        try {
            recordManager.deletePropertyFromRecord(type, name, id);
        } catch (ObjectNotFoundException onfe) {
            //Catching this because ONFE maps to a BAD_REQUEST.  Since the id's are on the URL
            //We want to throw a 404 instead
            logger.error(onfe.getMessage(),onfe);
            throw new NotFoundException(onfe.getMessage());
        }
    }

    /**
     * Return records of type {childType} who have a field in the format
     *
     * {parentType}Id
     *
     * and where the value of that field is
     *
     * {id}
     *
     * For example, to get all notes attached to a record of type ticket
     *
     * tickets/30/notes
     *
     * This method is shorthand for this search:
     *
     * /notes?ticketId={id}
     *
     * @param id
     * @return
     * @throws NotFoundException
     */
    @GET
    @Path("{parentType}/{id}/{childType}")
    public Object search(@PathParam("parentType") String parentType,
                         @PathParam("id") String id,
                         @PathParam("childType") String childType,
                         @Context UriInfo ui) throws NotFoundException {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        parentType = Inflector.getInstance().singularize(parentType);
        childType = Inflector.getInstance().singularize(childType);
        try{
            return recordManager.findSubResources(parentType, id, childType, queryParams);
        } catch (InvalidFieldException ife) {
            throw new NotFoundException();
        }
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
    @Path("{type}/")
    public Collection<PTicket> search(@PathParam("type") String type, @Context UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        if (queryParams.isEmpty()) {
            throw new ForbiddenException("You must specify at least one query parameter when searching for " + type);
        } 
        type = Inflector.getInstance().singularize(type);

        return recordManager.findRecords(type, queryParams);
    }

    /**
     * Save a new record.  If a property is specified twice, the latter value will be used.
     *
     * @param json the json representation of a client ticket (PTicket
     * @return the saved ticket
     * @throws Exception if the json was malformed
     */
    /*
     * We can't rely on automatic serialization here, because this method needs to know if the client sent
     * a signle object or an array so that it can return a sigle object or an array.
     *
     * This, we must deserialize manually so that we can serialize correctly
     */
    @POST
    @Path("{type}/")
    public Object save(@PathParam("type") String type, String jsonBody) throws Exception {
        type = Inflector.getInstance().singularize(type);
        JsonParser jp = new JsonParser();
        JsonElement el = jp.parse(jsonBody);
        Object outRecords;
        if(el.isJsonArray()) {
            List<PTicket> savedRecords = new ArrayList<PTicket>();
            PTicket[] recordArray = gson.fromJson(el, PTicket[].class);
            List<PTicket> records = Arrays.asList(recordArray);
            savedRecords = recordManager.createRecords(type, records);
            outRecords = savedRecords;
        } else {
            PTicket pTicket = gson.fromJson(el, PTicket.class);
            pTicket = recordManager.createRecord(type, pTicket);
            outRecords = pTicket;
        }
        return outRecords;
    }

    /**
     * Update an existing record.  If a property is specified twice, the latter value will be used.
     *
     * @param json the json representation of a client ticket (PTicket
     * @return the saved ticket
     * @throws Exception if the json was malformed
     */
    @PUT
    @Path("{type}/{id}")
    public Object update(@PathParam("type") String type, @PathParam("id") String id, PTicket pTicket) throws Exception {
        logger.debug("PUT: [{}]", pTicket);
        type = Inflector.getInstance().singularize(type);
        PTicket ticket  = recordManager.updateRecord(type, pTicket, id);
        return ticket;
    }

    /**
     * Apply the properties in pTicket to the ids listed in the URL
     *
     * Ids should be listed in a comma delimited list
     *
     * The HTTP PATCH method is a better candidate for this, but it isn't implemented yet:  http://tools.ietf.org/html/rfc5789
     *
     * @param json the fields to update
     * @return the saved ticket
     * @throws Exception if the json was malformed
     */
    @PUT
    @Path("{type}/patch/{ids}")
    public Object patch(@PathParam("type") String type, @PathParam("ids") String ids, PTicket pTicket) throws Exception {
        type = Inflector.getInstance().singularize(type);
        String[] idArray = StringUtils.split(ids, RecordManager.ID_DELIMITER);
        List<String> idList = Arrays.asList(idArray);
        Collection<PTicket> records = recordManager.updateRecords(type, idList, pTicket);
        return records;
    }

    public RecordManager getRecordManager() {
        return recordManager;
    }

    public void setRecordManager(RecordManager recordManager) {
        this.recordManager = recordManager;
    }
}