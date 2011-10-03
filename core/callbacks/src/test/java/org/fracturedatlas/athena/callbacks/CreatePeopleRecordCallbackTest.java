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
package org.fracturedatlas.athena.callbacks;

import java.util.ArrayList;
import java.util.List;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.junit.Test;
import org.fracturedatlas.athena.client.PTicket;
import org.junit.Before;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class CreatePeopleRecordCallbackTest {

    @Mock private AthenaComponent mockPeople;
    
    PTicket inboundPerson;
    PTicket savedPerson;
    
    CreatePeopleRecordCallback callback = new CreatePeopleRecordCallback();
    
    @Test
    public void saveOrderWithPeopleId() {
        PTicket order = new PTicket();
        order.put("firstName", "Jim");
        order.put("lastName", "Smith");
        order.put("email", "jim@example.com");
        order.put("personId", "95159");
        order.put("organizationId", "1");
        order.put("orderId", "349409409");        
        
        AthenaSearch search = new AthenaSearch.Builder()
                                  .type("person")
                                  .and("email", Operator.EQUALS, "jim@example.com")
                                  .and("organizationId", Operator.EQUALS, "1")
                                  .build();
        
        List<PTicket> noResults = new ArrayList<PTicket>();
        
        order = callback.beforeSave("order", order);
        verify(mockPeople, times(0)).find("person", search);
        
        assertEquals(order.get("personId"), "95159");
        assertNull(order.get("firstName"));
        assertNull(order.get("lastName"));
        assertNull(order.get("email"));
    }
    
    @Test
    public void saveOrderWithPeopleIdConsiderOrganization() {
        PTicket order = new PTicket();
        order.put("firstName", "Jim");
        order.put("lastName", "Smith");
        order.put("email", "jim@example.com");
        order.put("organizationId", "1");
        order.put("orderId", "349409409");
        
        AthenaSearch search = new AthenaSearch.Builder()
                                  .type("person")
                                  .and("email", Operator.EQUALS, "jim@example.com")
                                  .and("organizationId", Operator.EQUALS, "1")
                                  .build();
        
        List<PTicket> noResults = new ArrayList<PTicket>();
        when(mockPeople.find("person", search)).thenReturn(noResults);
        
        order = callback.beforeSave("order", order);
        
        verify(mockPeople, times(1)).find("person", search);
        verify(mockPeople, times(1)).save("person", inboundPerson);
        assertEquals(order.get("personId"), "45");
        assertNull(order.get("firstName"));
        assertNull(order.get("lastName"));
        assertNull(order.get("email"));
    }
    
    @Test
    public void saveOrderWithPeopleIdNoOrganization() {
        PTicket order = new PTicket();
        order.put("firstName", "Jim");
        order.put("lastName", "Smith");
        order.put("email", "jim@example.com");
        order.put("orderId", "349409409");
        
        AthenaSearch search = new AthenaSearch.Builder()
                                  .type("person")
                                  .and("email", Operator.EQUALS, "jim@example.com")
                                  .build();
        
        List<PTicket> results = new ArrayList<PTicket>();
        results.add(savedPerson);
        when(mockPeople.find("person", search)).thenReturn(results);
        
        order = callback.beforeSave("order", order);
        
        verify(mockPeople, times(1)).find("person", search);
        verify(mockPeople, times(0)).save("person", inboundPerson);
        assertEquals(order.get("personId"), "45");
        assertNull(order.get("firstName"));
        assertNull(order.get("lastName"));
        assertNull(order.get("email"));
    }

    @Test
    public void saveOrderAndCreatePersonRecordWithoutEmail() {
        PTicket order = new PTicket();
        order.put("firstName", "Jim");
        order.put("lastName", "Smith");
        order.put("orderId", "349409409"); 
        order.put("organizationId", "1");       
        
        AthenaSearch search = new AthenaSearch.Builder()
                                  .type("person")
                                  .and("email", Operator.EQUALS, "")
                                  .and("organizationId", Operator.EQUALS, "1")
                                  .build();
        
        verify(mockPeople, times(0)).find("person", search);
        
        
        PTicket aPerson = new PTicket();
        aPerson.put("firstName", "Jim");
        aPerson.put("lastName", "Smith");
        aPerson.put("organizationId", "1");
        
        PTicket anotherPerson = new PTicket();
        anotherPerson.setId("45");
        anotherPerson.put("firstName", "Jim");
        anotherPerson.put("lastName", "Smith");
        anotherPerson.put("organizationId", "1");
        when(mockPeople.save("person", aPerson)).thenReturn(anotherPerson);
        
        order = callback.beforeSave("order", order);
        
        PTicket partialPerson = new PTicket();
        partialPerson.put("firstName", "Jim");
        partialPerson.put("lastName", "Smith");
        partialPerson.put("organizationId", "1");
        verify(mockPeople, times(1)).save("person", partialPerson);
        
        assertEquals("45", order.get("personId"));
        assertNull(order.get("firstName"));
        assertNull(order.get("lastName"));
        assertNull(order.get("email"));
    }
    
    @Test
    public void saveOrderAndCreatePersonRecord() {
        PTicket order = new PTicket();
        order.put("firstName", "Jim");
        order.put("lastName", "Smith");
        order.put("email", "jim@example.com");
        order.put("organizationId", "1");
        order.put("orderId", "349409409");        
        
        AthenaSearch search = new AthenaSearch.Builder()
                                  .type("person")
                                  .and("email", Operator.EQUALS, "jim@example.com")
                                  .and("organizationId", Operator.EQUALS, "1")
                                  .build();
        
        List<PTicket> noResults = new ArrayList<PTicket>();
        when(mockPeople.find("person", search)).thenReturn(noResults);
        
        order = callback.beforeSave("order", order);
        
        assertEquals(order.get("personId"), "45");
        assertNull(order.get("firstName"));
        assertNull(order.get("lastName"));
        assertNull(order.get("email"));
    }
    
    @Test
    public void saveOrderAndCreatePersonRecordAlreadyExists() {
        PTicket order = new PTicket();
        order.put("firstName", "Jim");
        order.put("lastName", "Smith");
        order.put("email", "jim@example.com");
        order.put("organizationId", "1");
        order.put("orderId", "349409409");
        
        AthenaSearch search = new AthenaSearch.Builder()
                                  .type("person")
                                  .and("email", Operator.EQUALS, "jim@example.com")
                                  .and("organizationId", Operator.EQUALS, "1")
                                  .build();
        
        List<PTicket> results = new ArrayList<PTicket>();
        results.add(savedPerson);
        when(mockPeople.find("person", search)).thenReturn(results);
        
        order = callback.beforeSave("order", order);
        
        
        verify(mockPeople, times(1)).find("person", search);
        verify(mockPeople, times(0)).save("person", inboundPerson);
        assertEquals(order.get("personId"), "45");
        assertNull(order.get("firstName"));
        assertNull(order.get("lastName"));
        assertNull(order.get("email"));
    }
    
    @Test
    public void saveOrderWithoutPeopleInformation() {
        PTicket order = new PTicket();
        order.put("orderId", "349409409"); 
        order.put("organizationId", "1"); 
        
        AthenaSearch search = new AthenaSearch.Builder()
                                  .type("person")
                                  .and("email", Operator.EQUALS, "")
                                  .and("organizationId", Operator.EQUALS, "1")
                                  .build();
        
        order = callback.beforeSave("order", order);
        verify(mockPeople, times(0)).find("person", search);
        
        assertNull(order.get("personId"));
        assertNull(order.get("firstName"));
        assertNull(order.get("lastName"));
        assertNull(order.get("email"));
    }
    
    @Before
    public void mockupPeople() {
        MockitoAnnotations.initMocks(this);
        createSampleObjects();
        when(mockPeople.save("person", inboundPerson)).thenReturn(savedPerson);
        callback.setAthenaPeople(mockPeople);
    }    
    
    public void createSampleObjects() {
        inboundPerson = new PTicket();
        inboundPerson.put("firstName", "Jim");
        inboundPerson.put("lastName", "Smith");
        inboundPerson.put("email", "jim@example.com");
        inboundPerson.put("organizationId", "1");
        
        savedPerson = new PTicket();
        savedPerson.setId("45");
        savedPerson.put("firstName", "Jim");
        savedPerson.put("lastName", "Smith");
        savedPerson.put("email", "jim@example.com");
        inboundPerson.put("organizationId", "1");
    }
}
