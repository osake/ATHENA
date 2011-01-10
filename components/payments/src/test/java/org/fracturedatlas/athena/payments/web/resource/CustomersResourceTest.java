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

public class CustomersResourceTest extends BasePaymentsTest {

    Gson gson = new Gson();

    @Test
    public void getCustomer() {
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
        card = payments.saveCreditCard(card);
        
        CreditCard card2 = new CreditCard();
        card2.setCardNumber("4012000077777777");
        card2.setExpirationDate("09/2011");
        card2.setCardholderName("Joeseph L Cool");
        card2.setCustomer(savedCustomer);
        card2 = payments.saveCreditCard(card2);
        
        Customer gettedCustomer = payments.getCustomer(savedCustomer.getId());
        assertTrue(savedCustomer.equals(gettedCustomer));

        assertEquals(2, gettedCustomer.getCreditCards().size());

        for(CreditCard storedCard : gettedCustomer.getCreditCards()) {
            if("Joeseph L Cool".equals(storedCard.getCardholderName())) {
                card2.setId(storedCard.getId());
                card2.setToken(storedCard.getToken());
                assertTrue(card2.equals(storedCard));
            } else if ("Joe Cool".equals(storedCard.getCardholderName())) {
                card.setId(storedCard.getId());
                card.setToken(storedCard.getToken());
                assertTrue(card.equals(storedCard));
            }
        }

        payments.deleteCustomer(savedCustomer);

    }

    @Test
    public void addCardToExistingCustomer() {
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
        card = payments.saveCreditCard(card);

        CreditCard card2 = new CreditCard();
        card2.setCardNumber("4012000077777777");
        card2.setExpirationDate("09/2011");
        card2.setCardholderName("Joeseph L Cool");
        card2.setCustomer(savedCustomer);
        card2 = payments.saveCreditCard(card2);

        Customer gettedCustomer = payments.getCustomer(savedCustomer.getId());
        assertTrue(savedCustomer.equals(gettedCustomer));

        assertEquals(2, gettedCustomer.getCreditCards().size());

        for(CreditCard storedCard : gettedCustomer.getCreditCards()) {
            if("Joeseph L Cool".equals(storedCard.getCardholderName())) {
                card2.setId(storedCard.getId());
                card2.setToken(storedCard.getToken());
                assertTrue(card2.equals(storedCard));
            } else if ("Joe Cool".equals(storedCard.getCardholderName())) {
                card.setId(storedCard.getId());
                card.setToken(storedCard.getToken());
                assertTrue(card.equals(storedCard));
            }
        }

        CreditCard newCard = new CreditCard();
        newCard.setCardNumber("4009348888881881");
        newCard.setExpirationDate("05/2013");
        newCard.setCardholderName("Jose R Cool");
        newCard.setCustomer(savedCustomer);
        CreditCard newSavedCard = payments.saveCreditCard(newCard);

        gettedCustomer = payments.getCustomer(savedCustomer.getId());

        for(CreditCard storedCard : gettedCustomer.getCreditCards()) {
            if("Joeseph L Cool".equals(storedCard.getCardholderName())) {
                card2.setId(storedCard.getId());
                card2.setToken(storedCard.getToken());
                assertTrue(card2.equals(storedCard));
            } else if ("Joe Cool".equals(storedCard.getCardholderName())) {
                card.setId(storedCard.getId());
                card.setToken(storedCard.getToken());
                assertTrue(card.equals(storedCard));
            } else if ("Jose R Cool".equals(storedCard.getCardholderName())) {
                newSavedCard.setId(storedCard.getId());
                newSavedCard.setToken(storedCard.getToken());
                assertTrue(newSavedCard.equals(storedCard));
            }
        }

        payments.deleteCustomer(savedCustomer);

    }

    @Test
    public void saveCustomer() {
        Customer customer = new Customer();

        customer.setCompany("Bad Company");
        customer.setEmail("test@test.com");
        customer.setFirstName("Joe");
        customer.setLastName(("Tester"));
        customer.setPhone("410-909-9090");

        Customer savedCustomer = payments.saveCustomer(customer);
        assertNotNull(savedCustomer.getId());
        customer.setId(savedCustomer.getId());
        assertTrue(savedCustomer.equals(customer));
        payments.deleteCustomer(savedCustomer);
    }

    @Test
    public void updateCustomer() {
        Customer customer = new Customer();

        customer.setCompany("Bad Company");
        customer.setEmail("test@test.com");
        customer.setFirstName("Joe");
        customer.setLastName(("Tester"));
        customer.setPhone("410-909-9090");

        Customer savedCustomer = payments.saveCustomer(customer);
        savedCustomer.setFirstName("Jose");
        Customer updatedCustomer = payments.saveCustomer(savedCustomer);
        assertTrue(updatedCustomer.equals(savedCustomer));
        payments.deleteCustomer(savedCustomer);
    }

    @Test
    public void deleteCustomer() {
        Customer customer = new Customer();

        customer.setCompany("Bad Company");
        customer.setEmail("test@test.com");
        customer.setFirstName("Joe");
        customer.setLastName(("Tester"));
        customer.setPhone("410-909-9090");

        Customer savedCustomer = payments.saveCustomer(customer);
        Customer gettedCustomer = payments.getCustomer(savedCustomer.getId());
        assertTrue(savedCustomer.equals(gettedCustomer));

        payments.deleteCustomer(savedCustomer);

        Customer shouldBeDeleted = payments.getCustomer(savedCustomer.getId());
        assertNull(shouldBeDeleted);
    }
}
