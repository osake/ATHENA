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
package org.fracturedatlas.athena.helper.relationships.web;


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
import org.fracturedatlas.athena.web.util.JsonUtil;
import com.sun.jersey.core.impl.provider.entity.Inflector;
import org.fracturedatlas.athena.helper.relationships.manager.RelationshipHelperManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Path("/meta/relationships")
@Consumes({"application/json"})
@Produces({"application/json"})
@Component
public class RelationshipHelperResource {

    Gson gson = JsonUtil.getGson();
    
    @Autowired
    RelationshipHelperManager relationshipHelperManager;


    @GET
    @Path("{type}/{id}")
    public Collection<Ticket> search(@PathParam("type") String type,
                                     @PathParam("id") String id) throws NotFoundException {
        type = Inflector.getInstance().singularize(type);
        return relationshipHelperManager.findRelationships(type, id);
    }

}
