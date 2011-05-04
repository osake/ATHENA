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

package org.fracturedatlas.athena.helper.bulk.web;

import com.google.gson.Gson;
import com.sun.jersey.core.impl.provider.entity.Inflector;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.fracturedatlas.athena.helper.bulk.manager.BulkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Path("/bulk")
@Consumes({"application/json"})
@Produces({"application/json"})
@Component
public class BulkResource {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    BulkManager bulkManager;

    Gson gson = JsonUtil.getGson();

    /**
     * Apply the properties to the ids listed in the URL
     *
     * Ids should be listed in a semicolon delimited list
     *
     * @param json the fields to update
     * @return the saved ticket
     * @throws Exception if the json was malformed
     */
    @PUT
    @Path("{type}/{ids}")
    public Object update(@PathParam("type") String type, @PathParam("ids") String ids, PTicket pTicket) throws Exception {
        type = Inflector.getInstance().singularize(type);

        List<PTicket> records  = bulkManager.updateRecords(type, ids, pTicket);
        return records;
    }
}
