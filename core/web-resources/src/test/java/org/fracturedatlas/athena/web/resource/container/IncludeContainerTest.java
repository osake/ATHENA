/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fracturedatlas.athena.web.resource.container;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MultivaluedMap;
import com.google.gson.Gson;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.web.util.BaseTixContainerTest;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class IncludeContainerTest extends BaseTixContainerTest {

    Gson gson = JsonUtil.getGson();    
    PTicket jim;
    PTicket bo;
    PTicket fido;

    public IncludeContainerTest() throws Exception {
        super();
    }

    @Before
    public void setup() {
        addPropField(ValueType.STRING,"name",Boolean.FALSE);
        addPropField(ValueType.STRING,"animal",Boolean.FALSE);
        addPropField(ValueType.STRING,"ownerId",Boolean.FALSE);
        
        jim = addRecord("owner",
                        "name", "Jim");
        
        //Bo is owned by Jim
        bo = addRecord("pet",
                        "animal", "dog",
                        "name", "Bo",
                        "ownerId", jim.getIdAsString());
        
        //Fido isn't owned by anyone
        fido = addRecord("pet",
                        "animal", "dog",
                        "name", "Fido");
    }
    
    @After
    public void teardown() {
        super.teardownRecords();
    }
    
    @Test
    public void testInclude() {
        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("name", "Bo");
        queryParams.add("_include", "owner");
        String path = "/pets";
        String ticketString = tix.path(path).queryParams(queryParams).get(String.class);
        PTicket[] pets = gson.fromJson(ticketString, PTicket[].class);
        PTicket boTicket = pets[0];
        assertEquals(boTicket.get("name"), bo.get("name"));
        assertNotNull(boTicket.getRecord("owner"));
        PTicket owner = boTicket.getRecord("owner");
        assertEquals("Jim", owner.get("name"));
        assertEquals(jim.getIdAsString(), owner.getIdAsString());
    }
    
    @Test
    public void testIncludeNullRelationship() {
        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("name", "Fido");
        queryParams.add("_include", "owner");
        String path = "/pets";
        String ticketString = tix.path(path).queryParams(queryParams).get(String.class);
        PTicket[] pets = gson.fromJson(ticketString, PTicket[].class);
        PTicket fidoRecord = pets[0];
        System.out.println(fidoRecord);
        assertEquals(fidoRecord.get("name"), fido.get("name"));
        assertNull(fidoRecord.getRecord("owner"));
    }
}
