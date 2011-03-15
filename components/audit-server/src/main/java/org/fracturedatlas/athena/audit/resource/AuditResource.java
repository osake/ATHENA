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
package org.fracturedatlas.athena.audit.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.fracturedatlas.athena.audit.manager.AuditManager;
import org.fracturedatlas.athena.audit.model.AuditMessage;
import org.fracturedatlas.athena.client.audit.PublicAuditMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("audit")
@Consumes({"application/json"})
@Produces({"application/json"})
public class AuditResource {

    static GsonBuilder gb = new GsonBuilder();
    @Autowired
    AuditManager auditManager;
    Gson gson = gb.create();
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Save an audit message.
     */
    @POST
    @Path("")
    public PublicAuditMessage saveAuditMessage(String json) throws Exception {
        try {
            PublicAuditMessage auditMessage = gson.fromJson(json, PublicAuditMessage.class);
            auditMessage = auditManager.saveAuditMessage(auditMessage);
            return auditMessage;
         } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            throw ex;
        }
    }
}

