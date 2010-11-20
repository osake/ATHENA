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
import org.fracturedatlas.athena.payments.model.CreditCard;
import org.fracturedatlas.athena.payments.model.Customer;
import org.junit.Test;
import static org.junit.Assert.*;

public class CardsResourceTest extends BasePaymentsTest {

    Gson gson = new Gson();

    private void cardsEqual(CreditCard card1, CreditCard card2) {
        assertEquals(CreditCard.lastFour(card1.getCardNumber()), CreditCard.lastFour(card2.getCardNumber()));
        assertEquals(card1.getCardholderName(), card2.getCardholderName());
        assertEquals(card1.getExpirationDate(), card2.getExpirationDate());
        assertEquals(card1.getCustomer().getId(), card2.getCustomer().getId());
    }

    @Test
    public void createGetDeleteCard() {
        Customer customer = new Customer();

        customer.setCompany("Bad Company");
        customer.setEmail("test@test.com");
        customer.setFirstName("Joe");
        customer.setLastName(("Tester"));
        customer.setPhone("410-909-9090");

        Customer savedCustomer = payments.saveCustomer(customer);

        CreditCard card = new CreditCard();
        card.setCardNumber("4111111111111111");
        card.setExpirationDate("05/2011");
        card.setCardholderName("Joe Cool");        
        card.setCustomer(savedCustomer);

        CreditCard savedCard = payments.saveCreditCard(card);
        assertNotNull(savedCard.getId());
        assertNotNull(savedCard.getToken());
        cardsEqual(savedCard, card);
        CreditCard getCard = payments.getCard(savedCard.getId());
        assertTrue(getCard.equals(savedCard));

        payments.deleteCard(savedCard);

        CreditCard nullCard = payments.getCard(savedCard.getId());
        assertNull(nullCard);

        payments.deleteCustomer(savedCustomer);
    }

    @Test
    public void createUpdateGetDeleteCard() {
        Customer customer = new Customer();

        customer.setCompany("Bad Company");
        customer.setEmail("test@test.com");
        customer.setFirstName("Joe");
        customer.setLastName(("Tester"));
        customer.setPhone("410-909-9090");

        Customer savedCustomer = payments.saveCustomer(customer);

        CreditCard card = new CreditCard();
        card.setCardNumber("4111111111111111");
        card.setExpirationDate("05/2011");
        card.setCardholderName("Joe Cool");
        card.setCustomer(savedCustomer);

        CreditCard savedCard = payments.saveCreditCard(card);
        assertNotNull(savedCard.getId());
        assertNotNull(savedCard.getToken());
        cardsEqual(savedCard, card);
        CreditCard getCard = payments.getCard(savedCard.getId());
        assertTrue(getCard.equals(savedCard));

        savedCard.setExpirationDate("12/2012");
        CreditCard updatedCard = payments.saveCreditCard(savedCard);
        assertEquals(savedCard.getId(), updatedCard.getId());
        assertEquals(savedCard.getToken(), updatedCard.getToken());
        cardsEqual(savedCard, updatedCard);

        payments.deleteCard(savedCard);

        CreditCard nullCard = payments.getCard(savedCard.getId());
        assertNull(nullCard);

        payments.deleteCustomer(savedCustomer);
    }
}
