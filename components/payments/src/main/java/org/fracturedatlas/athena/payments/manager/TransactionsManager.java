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

import org.fracturedatlas.athena.payments.model.AuthorizationRequest;
import org.fracturedatlas.athena.payments.model.AuthorizationResponse;
import org.fracturedatlas.athena.payments.processor.PaymentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionsManager {

    @Autowired
    PaymentProcessor processor;

    public AuthorizationResponse authorizePayment(AuthorizationRequest paymentDetails) {

        return processor.authorizePayment(paymentDetails);
    }

    public AuthorizationResponse settle(AuthorizationRequest paymentDetails) {
        return processor.settle(paymentDetails);
    }

    public AuthorizationResponse refund(AuthorizationRequest paymentDetails) {
        return processor.refund(paymentDetails);
    }

    public AuthorizationResponse voidPayment(AuthorizationRequest paymentDetails) {
        return processor.voidPayment(paymentDetails);
    }
}
