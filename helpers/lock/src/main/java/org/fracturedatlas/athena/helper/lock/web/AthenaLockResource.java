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

package org.fracturedatlas.athena.helper.lock.web;

import com.google.gson.Gson;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


import com.sun.jersey.api.NotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import org.fracturedatlas.athena.helper.lock.manager.AthenaLockManager;
import org.fracturedatlas.athena.helper.lock.model.AthenaLock;
import org.fracturedatlas.athena.web.exception.AthenaException;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/locks")
@Consumes({"application/json"})
@Produces({"application/json"})
@Component
public class AthenaLockResource {

    Gson gson = JsonUtil.getGson();

    @Autowired
    AthenaLockManager athenaLockManager;

    /**
     * Get the details of this transaction.  Returned object will include:
     *
     * tickets: an array of tickets involved in this transaction
     * lockId: the id of this transaction
     * lockExpires: The time that these tickets will be unlocked
     * status:
     *
     * @param id
     * @return
     * @throws NotFoundException
     */
    @GET
    @Path("{id}")
    public Object get(@Context HttpServletRequest request, @PathParam("id") String id) throws Exception {
        return athenaLockManager.getLock(id, request);
    }

    @POST
    @Path("")
    public Object create(@Context HttpServletRequest request, AthenaLock tran) throws Exception {
        return athenaLockManager.createLock(request, tran);
    }

    @PUT
    @Path("{id}")
    public Object update(@PathParam("id") String id, 
                         @Context HttpServletRequest request, 
                         AthenaLock tran) throws Exception {
        if(!id.equals(tran.getId())) {
            throw new AthenaException("Id on request URI does not match id in transaction");
        }

        return athenaLockManager.updateLock(id, request, tran);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") String id,
                       @Context HttpServletRequest request) throws Exception {
        athenaLockManager.deleteLock(id, request);
    }
}
