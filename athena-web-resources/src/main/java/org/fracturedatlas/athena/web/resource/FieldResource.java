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
import com.sun.jersey.api.NotFoundException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.fracturedatlas.athena.web.exception.AthenaException;
import org.fracturedatlas.athena.web.manager.PropFieldManager;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.PropValue;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/fields")
@Consumes({"application/json"})
@Produces({"application/json"})
public class FieldResource {

    @Autowired
    PropFieldManager propFieldManager;
    Gson gson = JsonUtil.getGson();
    Logger logger = Logger.getLogger(FieldResource.class);

    @GET
    @Path("{id}")
    public Object get(@PathParam("id") String id) throws Exception {
        PropField propField = propFieldManager.getPropField(id);
        System.out.println(propField);
        if (propField == null) {
            throw new NotFoundException("Field with id [" + id + "] was not found");
        } else {
            return propField;
        }
    }

    @GET
    public PropField[] list() {
        PropField[] fields = propFieldManager.findPropFields();
        return fields;
    }

    @GET
    @Path("/{propFieldId}/values/{propValueId}")
    public PropValue getValue(@PathParam("propFieldId") String propFieldId,
                              @PathParam("propValueId") String propValueId) {
        PropValue propValue = propFieldManager.getPropValue(propFieldId, propValueId);
        if (propValue == null) {
            throw new NotFoundException("Value with id [" + propValueId + "] was not found");
        } else {
            return propValue;
        }
    }

    @GET
    @Path("{propFieldId}/values")
    public PropValue[] getValueList(@PathParam("propFieldId") String propFieldId) {
        PropValue[] propValues = propFieldManager.getPropValueList(propFieldId);

        return propValues;
    }

    /**
     * Save or update the propField contained in this JSON string
     *
     * Note that values cannot be updated from this endpoint.  Instead, post new values
     * to /{fieldId}/values
     *
     * Clients are required to pass valueType, name, and strict
     * strict must be wither "false" or "true"
     * valueType must be: STRING, INTEGER, DATETIME, BOOLEAN
     *
     */
    @POST
    public Object saveField(String json) throws Exception {
        PropField propField = gson.fromJson(json, PropField.class);

        if(propField == null) {
            throw new AthenaException("Sent a blank or malformed request body.  Could not make a field out of it.");
        }

        propField = propFieldManager.savePropField(propField);
        return propField;
    }

    /**
     * Add a new value to this propField.
     *
     * You cannot update a sepcific propValue from this endpoint.  You must first delete the value then add the new one.
     */
    @POST
    @Path("/{propFieldId}/values")
    public PropValue saveValue(String json, @PathParam("propFieldId") String propFieldId) throws Exception {
        try {
            PropValue propValue = gson.fromJson(json, PropValue.class);
            
            if(propValue.getId() != null) {
                throw new AthenaException("Cannot update values on a field.  First delete the value then add a new value.");
            }

            propValue = propFieldManager.savePropValue(propFieldId, propValue);
            return propValue;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @DELETE
    @Path("/{propFieldId}/values/{propValueId}")
    public Object deleteValue(@PathParam("propFieldId") String propFieldId, @PathParam("propValueId") String propValueId) throws Exception {
        if (propFieldManager.deletePropValue(propFieldId, propValueId)) {
            return Response.noContent().build();
        } else {
            return Response.status(404).build();
        }
    }

    @DELETE
    @Path("/{propFieldId}")
    public Object deleteField(@PathParam("propFieldId") String propFieldId) throws Exception {
        if (propFieldManager.deletePropField(propFieldId)) {
            return Response.noContent().build();
        } else {
            return Response.status(404).build();
        }
    }
}

