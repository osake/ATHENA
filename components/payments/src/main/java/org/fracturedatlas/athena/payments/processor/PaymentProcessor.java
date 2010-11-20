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

package org.fracturedatlas.athena.payments.processor;

import org.fracturedatlas.athena.payments.model.AuthorizationRequest;
import org.fracturedatlas.athena.payments.model.AuthorizationResponse;

public interface PaymentProcessor {
    public AuthorizationResponse authorizePayment(AuthorizationRequest authroizationRequest);

    public AuthorizationResponse settle(AuthorizationRequest authroizationRequest);

    public AuthorizationResponse refund(AuthorizationRequest authroizationRequest);

    public AuthorizationResponse voidPayment(AuthorizationRequest authroizationRequest);

    public org.fracturedatlas.athena.payments.model.Customer getCustomer(String id);

    public org.fracturedatlas.athena.payments.model.Customer saveCustomer(org.fracturedatlas.athena.payments.model.Customer customer);

    public void deleteCustomer(String id);

    public org.fracturedatlas.athena.payments.model.CreditCard getCard(String id);

    public org.fracturedatlas.athena.payments.model.CreditCard saveCard(org.fracturedatlas.athena.payments.model.CreditCard card);

    public void deleteCard(String id);
}
