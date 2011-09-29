/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fracturedatlas.athena.apa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

public class ApaAdapterIncludeTest extends BaseApaAdapterTest {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    PTicket jim;
    PTicket bo;
    
    public ApaAdapterIncludeTest() {
        super();
    }

    @Before
    public void setupRecords() {
        addPropField(ValueType.STRING,"name",Boolean.FALSE);
        addPropField(ValueType.STRING,"animal",Boolean.FALSE);
        addPropField(ValueType.STRING,"ownerId",Boolean.FALSE);
        
        jim = addRecord("owner",
                        "name", "Jim");
        
        bo = addRecord("pet",
                        "animal", "dog",
                        "name", "Bo",
                        "ownerId", jim.getIdAsString());
        
    }

    @After
    public void teardown() {
        super.teardownTickets();
    }
    
    @Test
    public void testFindRelationship() {
        List<String> relationships = new ArrayList<String>();
        relationships.add("owner");
        PTicket newBo = apa.loadRelationships(bo, relationships);
        PTicket owner = newBo.getRecord("owner");
        assertNotNull(owner);
        assertEquals(jim.getIdAsString(), owner.getIdAsString());
        assertEquals(jim.get("name"), owner.get("name"));
    }
    
    
    @Test
    public void includeOwner() {
        AthenaSearch search = new AthenaSearch.Builder()
                                              .type("pet")
                                              .and("animal", Operator.EQUALS, "dog")
                                              .include("owner")
                                              .build();
        Set<PTicket> pets = apa.findTickets(search);
        assertEquals(pets.size(), 1);
        PTicket pet = pets.iterator().next();
        assertNotNull(pet.getRecord("owner"));
        assertEquals(jim.getIdAsString(), pet.get("ownerId"));
        assertEquals(jim.getIdAsString(), pet.getRecord("owner").getIdAsString());
        assertEquals(jim.get("name"), pet.getRecord("owner").get("name"));
    }
    
    
    @Test
    public void includeUnknownRelationship() {
        AthenaSearch search = new AthenaSearch.Builder()
                                              .type("pet")
                                              .and("animal", Operator.EQUALS, "dog")
                                              .include("company")
                                              .build();
        Set<PTicket> pets = apa.findTickets(search);
        assertEquals(pets.size(), 1);
        PTicket pet = pets.iterator().next();
        assertNull(pet.getRecord("owner"));
    }
}
