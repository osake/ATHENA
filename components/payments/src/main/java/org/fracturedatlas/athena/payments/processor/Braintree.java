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

import com.braintreegateway.*;
import com.sun.jersey.api.NotFoundException;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import org.fracturedatlas.athena.payments.model.AuthorizationRequest;
import org.fracturedatlas.athena.payments.model.AuthorizationResponse;

public class Braintree implements PaymentProcessor {

    BraintreeGateway gateway = null;

    public Braintree(String environment, String merchantId, String publicKey, String privateKey) {

        Environment braintreeEnvironment = Environment.valueOf(environment);

        gateway = new BraintreeGateway(
            braintreeEnvironment,
            merchantId,
            publicKey,
            privateKey
        );
    }


    @Override
    public AuthorizationResponse authorizePayment(AuthorizationRequest authorizationRequest) {
        AuthorizationResponse authorizationResponse = new AuthorizationResponse();

        TransactionRequest request = buildBraintreeRequest(authorizationRequest);

        Result<Transaction> result = gateway.transaction().sale(request);

        authorizationResponse.setSuccess(result.isSuccess());
        if(authorizationResponse.getSuccess()) {
            authorizationResponse.setTransactionId(result.getTarget().getId());

            if(authorizationRequest.getStoreCard()) {
                org.fracturedatlas.athena.payments.model.CreditCard card =
                        new org.fracturedatlas.athena.payments.model.CreditCard();
                card.setToken(result.getTarget().getCreditCard().getToken());
                authorizationResponse.setCreditCard(card);

                org.fracturedatlas.athena.payments.model.Customer customer =
                        new org.fracturedatlas.athena.payments.model.Customer();
                customer.setId(result.getTarget().getCustomer().getId());
                authorizationResponse.setCustomer(customer);

            }

        }
        authorizationResponse.setMessage(result.getMessage());
        return authorizationResponse;
    }

    private TransactionRequest buildBraintreeRequest(AuthorizationRequest authorizationRequest) {
        TransactionRequest request = new TransactionRequest();

        if(authorizationRequest.isUsingStoredToken()) {
            request.amount(authorizationRequest.getAmount()).
                    customerId(authorizationRequest.getCustomer().getId()).
                    paymentMethodToken(authorizationRequest.getCreditCard().getToken());
        } else {
            request.amount(authorizationRequest.getAmount()).
                creditCard().
                    number(authorizationRequest.getCreditCard().getCardNumber()).
                    expirationDate(authorizationRequest.getCreditCard().getExpirationDate()).
                    cardholderName(authorizationRequest.getCreditCard().getCardholderName()).
                    cvv(authorizationRequest.getCreditCard().getCvv()).
                    done().
                customer().
                    firstName(authorizationRequest.getCustomer().getFirstName()).
                    lastName(authorizationRequest.getCustomer().getLastName()).
                    company(authorizationRequest.getCustomer().getCompany()).
                    phone(authorizationRequest.getCustomer().getPhone()).
                    fax(authorizationRequest.getCustomer().getFax()).
                    email(authorizationRequest.getCustomer().getEmail()).
                    done().
                billingAddress().
                    firstName(authorizationRequest.getBillingAddress().getFirstName()).
                    lastName(authorizationRequest.getBillingAddress().getLastName()).
                    company(authorizationRequest.getBillingAddress().getCompany()).
                    streetAddress(authorizationRequest.getBillingAddress().getStreetAddress1()).
                    extendedAddress(authorizationRequest.getBillingAddress().getStreetAddress2()).
                    locality(authorizationRequest.getBillingAddress().getCity()).
                    region(authorizationRequest.getBillingAddress().getState()).
                    postalCode(authorizationRequest.getBillingAddress().getPostalCode()).
                    done().
                options().
                    storeInVault(authorizationRequest.getStoreCard()).
                    done();

            
        }

        return request;
    }



    @Override
    public AuthorizationResponse settle(AuthorizationRequest authroizationRequest) {
        String transactionId = authroizationRequest.getTransactionId();
        AuthorizationResponse authorizationResponse = new AuthorizationResponse();

        Result<Transaction> result = gateway.transaction()
                .submitForSettlement(authroizationRequest.getTransactionId(), authroizationRequest.getAmount());

        authorizationResponse.setSuccess(result.isSuccess());
        authorizationResponse.setMessage(result.getMessage());
        authorizationResponse.setTransactionId(transactionId);
        return authorizationResponse;
    }

    @Override
    public AuthorizationResponse refund(AuthorizationRequest authroizationRequest) {
        String transactionId = authroizationRequest.getTransactionId();
        AuthorizationResponse authorizationResponse = new AuthorizationResponse();

        Result<Transaction> result = gateway.transaction().refund(authroizationRequest.getTransactionId());

        authorizationResponse.setSuccess(result.isSuccess());
        authorizationResponse.setMessage(result.getMessage());
        authorizationResponse.setTransactionId(transactionId);

        return authorizationResponse;
    }

    @Override
    public AuthorizationResponse voidPayment(AuthorizationRequest authroizationRequest) {
        String transactionId = authroizationRequest.getTransactionId();
        AuthorizationResponse authorizationResponse = new AuthorizationResponse();

        Result<Transaction> result = gateway.transaction().voidTransaction(authroizationRequest.getTransactionId());

        authorizationResponse.setSuccess(result.isSuccess());
        authorizationResponse.setMessage(result.getMessage());
        authorizationResponse.setTransactionId(transactionId);

        return authorizationResponse;
    }

    @Override
    public org.fracturedatlas.athena.payments.model.Customer getCustomer(String id) {
        Customer customer = gateway.customer().find(id);
        org.fracturedatlas.athena.payments.model.Customer athenaCustomer =
            new org.fracturedatlas.athena.payments.model.Customer();

        List<CreditCard> creditCards = customer.getCreditCards();

        athenaCustomer.setFirstName(customer.getFirstName());
        athenaCustomer.setLastName(customer.getLastName());
        athenaCustomer.setCompany(customer.getCompany());
        athenaCustomer.setId(customer.getId());
        athenaCustomer.setPhone(customer.getPhone());
        athenaCustomer.setEmail(customer.getEmail());

        for(CreditCard creditCard : creditCards) {
            org.fracturedatlas.athena.payments.model.CreditCard athenaCard = buildCardFromBraintree(creditCard);
            athenaCustomer.getCreditCards().add(athenaCard);
        }

        return athenaCustomer;
    }

    @Override
    public org.fracturedatlas.athena.payments.model.Customer saveCustomer(org.fracturedatlas.athena.payments.model.Customer customer) {
        CustomerRequest request = new CustomerRequest().
                            firstName(customer.getFirstName()).
                            lastName(customer.getLastName()).
                            company(customer.getCompany()).
                            email(customer.getEmail()).
                            phone(customer.getPhone());

        Result<Customer> result;
        if(customer.getId() == null) {
            result = gateway.customer().create(request);
        } else {
            result = gateway.customer().update(customer.getId(), request);
        }

        if(result.isSuccess()) {
            customer.setId(result.getTarget().getId());
            return customer;
        } else {
            throw new WebApplicationException();
        }
    }

    @Override
    public void deleteCustomer(String id) {
        try{
            Result<Customer> result = gateway.customer().delete(id);
        } catch (com.braintreegateway.exceptions.NotFoundException bnfe) {
            throw new NotFoundException("Customer with id [" + id + "] was not found");
        }
    }

    @Override
    public org.fracturedatlas.athena.payments.model.CreditCard getCard(String id) {
        CreditCard creditCard = gateway.creditCard().find(id);
        org.fracturedatlas.athena.payments.model.CreditCard athenaCard = buildCardFromBraintree(creditCard);

        org.fracturedatlas.athena.payments.model.Customer customer =
                new org.fracturedatlas.athena.payments.model.Customer();

        customer.setId(creditCard.getCustomerId());
        athenaCard.setCustomer(customer);

        return athenaCard;
    }

    private org.fracturedatlas.athena.payments.model.CreditCard buildCardFromBraintree(CreditCard creditCard) {
        org.fracturedatlas.athena.payments.model.CreditCard athenaCard =
                new org.fracturedatlas.athena.payments.model.CreditCard();

        athenaCard.setToken(creditCard.getToken());
        athenaCard.setCardNumber(creditCard.getMaskedNumber());
        athenaCard.setCardholderName(creditCard.getCardholderName());
        athenaCard.setExpirationDate(creditCard.getExpirationDate());
        athenaCard.setId(creditCard.getToken());

        return athenaCard;

    }

    @Override
    public org.fracturedatlas.athena.payments.model.CreditCard saveCard(org.fracturedatlas.athena.payments.model.CreditCard card) {
        CreditCardRequest request = new CreditCardRequest().
            customerId(card.getCustomer().getId()).
            number(card.getCardNumber()).
            expirationDate(card.getExpirationDate()).
            cardholderName(card.getCardholderName());

        Result<CreditCard> result;
        if(card.getId() != null) {
            //update
            result = gateway.creditCard().update(card.getId(), request);
        } else {
            //create
            result = gateway.creditCard().create(request);
        }

        if(result.isSuccess()) {
            CreditCard creditCard = result.getTarget();
            org.fracturedatlas.athena.payments.model.CreditCard athenaCard =
                    new org.fracturedatlas.athena.payments.model.CreditCard();
            athenaCard.setToken(creditCard.getToken());
            athenaCard.setId(creditCard.getToken());
            athenaCard.setCardNumber(creditCard.getMaskedNumber());
            athenaCard.setCardholderName(creditCard.getCardholderName());
            athenaCard.setExpirationDate(creditCard.getExpirationDate());


            org.fracturedatlas.athena.payments.model.Customer customer =
                    new org.fracturedatlas.athena.payments.model.Customer();

            customer.setId(creditCard.getCustomerId());
            athenaCard.setCustomer(customer);

            return athenaCard;
        } else {
            System.out.println(result.getMessage());
            throw new WebApplicationException();
        }
    }

    @Override
    public void deleteCard(String id) {
        try{
            Result<CreditCard> result = gateway.creditCard().delete(id);
        } catch (com.braintreegateway.exceptions.NotFoundException bnfe) {
            throw new NotFoundException("Card with id [" + id + "] was not found");
        }
    }
}
