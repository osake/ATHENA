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

package org.fracturedatlas.athena.payments.web.resource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.fracturedatlas.athena.payments.manager.TransactionsManager;
import org.fracturedatlas.athena.payments.model.AuthorizationRequest;
import org.fracturedatlas.athena.payments.model.AuthorizationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/transactions/")
@Consumes({"application/json"})
@Produces({"application/json"})
public class TransactionsResource {

    Gson gson = new Gson();

    @Autowired
    TransactionsManager transactionsManager;

    /**
     * Get state of a payment
     */
    @GET
    @Path("/{id}")
    public Object get() {
        return "Not yet implemented";
    }

    /**
     * Post a payment for approval
     */
    @POST
    @Path("/authorize")
    public Object authorize(String json) {
        AuthorizationRequest paymentDetails = gson.fromJson(json, AuthorizationRequest.class);
        AuthorizationResponse response = transactionsManager.authorizePayment(paymentDetails);
        return gson.toJson(response);

    }

    /**
     * Post a payment for settlement
     */
    @POST
    @Path("/settle")
    public Object settle(String json) {
        AuthorizationRequest paymentDetails = gson.fromJson(json, AuthorizationRequest.class);
        AuthorizationResponse response = transactionsManager.settle(paymentDetails);
        return gson.toJson(response);
    }

    /**
     * If a payment has not yet been settled, then viod the transaction.
     * Else, submit a refund.
     */
    @POST
    @Path("/void")
    public Object voidPayment(String json) {
        AuthorizationRequest paymentDetails = gson.fromJson(json, AuthorizationRequest.class);
        AuthorizationResponse response = transactionsManager.voidPayment(paymentDetails);
        return gson.toJson(response);
    }

    /**
     * If a payment has not yet been settled, then viod the transaction.
     * Else, submit a refund.
     */
    @POST
    @Path("/refund")
    public Object refund(String json) {
        AuthorizationRequest paymentDetails = gson.fromJson(json, AuthorizationRequest.class);
        AuthorizationResponse response = transactionsManager.refund(paymentDetails);
        return gson.toJson(response);
    }
}
