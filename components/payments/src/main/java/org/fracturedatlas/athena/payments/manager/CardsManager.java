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

package org.fracturedatlas.athena.payments.manager;

import javax.ws.rs.WebApplicationException;
import com.sun.jersey.api.NotFoundException;
import javax.ws.rs.core.Response;
import org.fracturedatlas.athena.payments.processor.PaymentProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class CardsManager {

    @Autowired
    PaymentProcessor processor;

    public org.fracturedatlas.athena.payments.model.CreditCard get(String id) {
        try{
            return processor.getCard(id);
        } catch (com.braintreegateway.exceptions.NotFoundException bnfe) {
            throw new NotFoundException("Customer with id [" + id + "] was not found");
        }
    }

    public org.fracturedatlas.athena.payments.model.CreditCard save(org.fracturedatlas.athena.payments.model.CreditCard card) {
        if( null != card.getId() && null != card.getCardNumber() ){
             throw new WebApplicationException(new Throwable("Cannot update a " +
                   "card number for a saved card. If a new number is reaquired, a new card must be created."), Response.Status.BAD_REQUEST);
        }

        return processor.saveCard(card);
    }

    public void delete(String id) {
        processor.deleteCard(id);
    }
}

