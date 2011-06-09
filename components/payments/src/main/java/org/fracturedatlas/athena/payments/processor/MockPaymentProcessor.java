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

import com.sun.jersey.api.NotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.fracturedatlas.athena.payments.model.AuthorizationRequest;
import org.fracturedatlas.athena.payments.model.AuthorizationResponse;

/**
 * A mock payment processor so that people can get up and running without signing up for
 * an actual payment processor.  To use this mock processor, edit the value
 *
 * athena.payments.processor
 *
 * in src/main/resources/processor.properties
 *
 * This mock processor isn't entirely accurate.  All transactions are approved.  Customers
 * and cards are stored in HashMaps and are not persisted across JVM sessions.
 *
 * With some work, this processor could be brought to better standing with:
 * * approving or declining transactions based on card #
 * * saving transactions to be retrieved later
 *
 * @author gary
 */
public class MockPaymentProcessor implements PaymentProcessor {

    HashMap<String, org.fracturedatlas.athena.payments.model.Customer> customers = 
            new HashMap<String, org.fracturedatlas.athena.payments.model.Customer>();
    HashMap<String, org.fracturedatlas.athena.payments.model.CreditCard> cards =
            new HashMap<String, org.fracturedatlas.athena.payments.model.CreditCard>();

    static final List<String> VALID_CARD_NUMBERS = new ArrayList<String>();

    static {
        VALID_CARD_NUMBERS.add("4111111111111111");
        VALID_CARD_NUMBERS.add("4005519200000004");
        VALID_CARD_NUMBERS.add("4009348888881881");
        VALID_CARD_NUMBERS.add("4012000033330026");
        VALID_CARD_NUMBERS.add("4012000077777777");
        VALID_CARD_NUMBERS.add("4012888888881881");
        VALID_CARD_NUMBERS.add("4217651111111119");
        VALID_CARD_NUMBERS.add("4500600000000061");
        VALID_CARD_NUMBERS.add("5555555555554444");
        VALID_CARD_NUMBERS.add("378282246310005");
        VALID_CARD_NUMBERS.add("371449635398431");
        VALID_CARD_NUMBERS.add("6011111111111117");
        VALID_CARD_NUMBERS.add("3530111333300000");
    }

    public MockPaymentProcessor() {
    }

    /* TODO: This is a hack workaround until I get the bean definitions sorted out. */
    public MockPaymentProcessor(String environment, String merchantId, String publicKey, String privateKey) {
    }


    @Override
    public AuthorizationResponse authorizePayment(AuthorizationRequest authorizationRequest) {
        AuthorizationResponse authorizationResponse = new AuthorizationResponse();
        String someId = UUID.randomUUID().toString();

        if(VALID_CARD_NUMBERS.contains(authorizationRequest.getCreditCard().getCardNumber())) {        
            authorizationResponse.setSuccess(Boolean.FALSE);
            authorizationResponse.setMessage("Declined");
            return authorizationResponse;
        } else {
            authorizationResponse.setSuccess(true);
            authorizationResponse.setTransactionId(someId);
        }
        
        if(authorizationRequest.getStoreCard()) {
            org.fracturedatlas.athena.payments.model.CreditCard card = authorizationRequest.getCreditCard();
            String token = UUID.randomUUID().toString();
            card.setToken(token);
            card.setId(token);
            cards.put(card.getId(), card);
            authorizationResponse.setCreditCard(card);

            org.fracturedatlas.athena.payments.model.Customer customer = authorizationRequest.getCustomer();
            String customerId = UUID.randomUUID().toString();
            customer.setId(customerId);
            customers.put(customer.getId(), customer);
            authorizationResponse.setCustomer(customer);

        }

        authorizationResponse.setMessage("Success");
        return authorizationResponse;
    }


    @Override
    public AuthorizationResponse settle(AuthorizationRequest authroizationRequest) {
        String transactionId = authroizationRequest.getTransactionId();
        AuthorizationResponse authorizationResponse = new AuthorizationResponse();
        String someId = UUID.randomUUID().toString();

        authorizationResponse.setSuccess(true);
        authorizationResponse.setMessage("Settled");
        authorizationResponse.setTransactionId(someId);
        return authorizationResponse;
    }

    @Override
    public AuthorizationResponse refund(AuthorizationRequest authroizationRequest) {
        String transactionId = authroizationRequest.getTransactionId();
        AuthorizationResponse authorizationResponse = new AuthorizationResponse();
        String someId = UUID.randomUUID().toString();

        authorizationResponse.setSuccess(true);
        authorizationResponse.setMessage("Refunded");
        authorizationResponse.setTransactionId(someId);

        return authorizationResponse;
    }

    @Override
    public AuthorizationResponse voidPayment(AuthorizationRequest authroizationRequest) {
        String transactionId = authroizationRequest.getTransactionId();
        AuthorizationResponse authorizationResponse = new AuthorizationResponse();
        String someId = UUID.randomUUID().toString();

        authorizationResponse.setSuccess(true);
        authorizationResponse.setMessage("Voided");
        authorizationResponse.setTransactionId(someId);

        return authorizationResponse;
    }

    @Override
    public org.fracturedatlas.athena.payments.model.Customer getCustomer(String id) {
        org.fracturedatlas.athena.payments.model.Customer c = customers.get(id);
        if(c == null) {
            throw new NotFoundException("Customer with id [" + id + "] was not found");
        } else {
            for(org.fracturedatlas.athena.payments.model.CreditCard card : cards.values()) {
                if(card.getCustomer().getId().equals(c.getId())) {
                    c.getCreditCards().add(card);
                }
            }

            return c;
        }
    }

    @Override
    public org.fracturedatlas.athena.payments.model.Customer saveCustomer(org.fracturedatlas.athena.payments.model.Customer customer) {
        org.fracturedatlas.athena.payments.model.Customer existingCustomer = customers.get(customer.getId());

        if(existingCustomer == null) {
            customer.setId(UUID.randomUUID().toString());
        } else {
            customer.setId(existingCustomer.getId());
            customers.remove(existingCustomer.getId());
        }
        customers.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public void deleteCustomer(String id) {
        if(customers.remove(id) == null) {
            throw new NotFoundException("Customer with id [" + id + "] was not found");
        }
        
    }

    @Override
    public org.fracturedatlas.athena.payments.model.CreditCard getCard(String id) {
        org.fracturedatlas.athena.payments.model.CreditCard athenaCard = cards.get(id);
        
        if(athenaCard == null) {
            throw new NotFoundException("Card with id [" + id + "] was not found");
        }

        athenaCard.setCustomer(customers.get(athenaCard.getCustomer().getId()));

        return athenaCard;
    }

    @Override
    public org.fracturedatlas.athena.payments.model.CreditCard saveCard(org.fracturedatlas.athena.payments.model.CreditCard card) {
        org.fracturedatlas.athena.payments.model.CreditCard savedCard = cards.get(card.getId());
        
        if(savedCard == null) {       
            String someId = UUID.randomUUID().toString();
            card.setId(someId);
            card.setToken(someId);
        } else {
            cards.remove(card.getId());
        }

        cards.put(card.getId(), card);
        
        return card;
    }

    @Override
    public void deleteCard(String id) {
        if(cards.remove(id) == null) {
            throw new NotFoundException("Card with id [" + id + "] was not found");
        }
    }

}
