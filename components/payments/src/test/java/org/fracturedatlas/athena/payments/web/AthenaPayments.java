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

package org.fracturedatlas.athena.payments.web;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.fracturedatlas.athena.payments.model.CreditCard;
import org.fracturedatlas.athena.payments.model.Customer;


public class AthenaPayments {

    WebResource webResource;
    Gson gson = new Gson();
    public static final String CUSTOMER_PATH = "customers/";
    public static final String CARD_PATH = "cards/";

    public AthenaPayments(WebResource wr) {
        webResource = wr;
    }

    public CreditCard saveCreditCard(CreditCard card) {

        if(card.getId() == null) {
            return gson.fromJson(webResource.path(CARD_PATH).type("application/json").post(String.class, gson.toJson(card)), CreditCard.class);
        } else {
            return gson.fromJson(webResource.path(CARD_PATH + card.getId()).type("application/json").put(String.class, gson.toJson(card)), CreditCard.class);
        }
    }

    public CreditCard getCard(String cardId) {
        ClientResponse response = webResource.path(CARD_PATH + cardId).type("application/json").get(ClientResponse.class);
        if(ClientResponse.Status.NOT_FOUND.equals(ClientResponse.Status.fromStatusCode(response.getStatus()))) {
            return null;
        } else {
            return gson.fromJson(response.getEntity(String.class), CreditCard.class);
        }
    }

    public void deleteCard(CreditCard card) {
        webResource.path(CARD_PATH + card.getId()).type("application/json").delete(ClientResponse.class);
    }

    public Customer getCustomer(String customerId) {
        ClientResponse response = webResource.path(CUSTOMER_PATH + customerId).type("application/json").get(ClientResponse.class);
        if(ClientResponse.Status.NOT_FOUND.equals(ClientResponse.Status.fromStatusCode(response.getStatus()))) {
            return null;
        } else {
            return gson.fromJson(response.getEntity(String.class), Customer.class);
        }
    }

    public Customer saveCustomer(Customer customer) {
        if(customer.getId() == null) {
            return gson.fromJson(webResource.path(CUSTOMER_PATH).type("application/json").post(String.class, gson.toJson(customer)), Customer.class);
        } else {
            return gson.fromJson(webResource.path(CUSTOMER_PATH + customer.getId()).type("application/json").put(String.class, gson.toJson(customer)), Customer.class);
        }
    }

    public void deleteCustomer(Customer customer) {
        webResource.path(CUSTOMER_PATH + customer.getId()).type("application/json").delete(ClientResponse.class);
    }
}
