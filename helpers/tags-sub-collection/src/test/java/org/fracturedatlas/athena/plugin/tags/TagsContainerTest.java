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
package org.fracturedatlas.athena.plugin.tags;

import org.fracturedatlas.athena.plugin.tags.model.Tag;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.List;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TagsContainerTest extends BaseTixContainerTest {

    String path = RECORDS_PATH;
    Gson gson = JsonUtil.getGson();
    Type listType = new TypeToken<List<Tag>>(){}.getType();
    
    public TagsContainerTest() throws Exception {
        super();
    }

    @After
    public void teardown() {
        super.teardownRecords();
    }

    @Test
    public void testTagsSubCollection() {
        
        String jsonResponse = tix.path(path + "tags")
                                     .type("application/json")
                                     .get(String.class);
        
        List<Tag> tags = gson.fromJson(jsonResponse, listType);
        assertNotNull(tags);
        assertEquals(5, tags.size());
        assertTrue(tags.contains(new Tag("standard", 5)));
        assertTrue(tags.contains(new Tag("sro", 2)));
        assertTrue(tags.contains(new Tag("ada", 1)));
        assertTrue(tags.contains(new Tag("obstructed", 1)));
        assertTrue(tags.contains(new Tag("foo", 1)));
    }

    @Test
    public void testTagsSubCollectionWithSearch() {
        
        String jsonResponse = tix.path(path + "tags").queryParam("performanceId", "1")
                                     .type("application/json")
                                     .get(String.class);
        
        List<Tag> tags = gson.fromJson(jsonResponse, listType);
        assertNotNull(tags);
        assertEquals(2, tags.size());
        assertTrue(tags.contains(new Tag("standard", 1)));
        assertTrue(tags.contains(new Tag("foo", 1)));
    }

    @Test
    public void testTagsSubCollectionNoTags() {
        
        addRecord("untagged", "performanceId", "2");
        addRecord("untagged", "performanceId", "2");
        addRecord("untagged", "performanceId", "2");
        addRecord("untagged", "performanceId", "2");
        addRecord("untagged", "performanceId", "2");
        addRecord("untagged", "performanceId", "2");
        
        String jsonResponse = tix.path("untagged/tags")
                                     .type("application/json")
                                     .get(String.class);
        
        Type listType = new TypeToken<List<String>>(){}.getType();
        List<Tag> tags = gson.fromJson(jsonResponse, listType);
        assertNotNull(tags);
        assertEquals(0, tags.size());
    }

    @Test
    public void testTagsSubCollectionUnknownType() {
        
        String jsonResponse = tix.path("unknowntypes/tags")
                                     .type("application/json")
                                     .get(String.class);
        
        Type listType = new TypeToken<List<String>>(){}.getType();
        List<Tag> tags = gson.fromJson(jsonResponse, listType);
        assertNotNull(tags);
        assertEquals(0, tags.size());
    }
    
    @Before
    public void setup() {
        addPropField(ValueType.STRING,"tags",Boolean.FALSE);
        addPropField(ValueType.STRING,"performanceId",Boolean.FALSE);
        
        PTicket t1 = new PTicket("ticket");
        PTicket t2 = new PTicket("ticket");
        PTicket t3 = new PTicket("ticket");
        PTicket t4 = new PTicket("ticket");
        PTicket t5 = new PTicket("ticket");
        PTicket t6 = new PTicket("ticket");

        t1.getProps().put("tags", Arrays.asList("standard", "sro"));
        t1.put("performanceId", "3");
        t2.getProps().put("tags", Arrays.asList("standard", "sro"));
        t2.put("performanceId", "3");
        t3.getProps().put("tags", Arrays.asList("standard", "ada"));
        t3.put("performanceId", "3");
        t4.getProps().put("tags", Arrays.asList("standard", "obstructed"));
        t4.put("performanceId", "3");
        t5.getProps().put("tags", Arrays.asList("standard", "foo"));
        t5.put("performanceId", "1");
        
        //6 has no tags   
        t6.put("performanceId", "1");

        t1 = apa.saveRecord(t1);
        t2 = apa.saveRecord(t2);
        t3 = apa.saveRecord(t3);
        t4 = apa.saveRecord(t4);
        t5 = apa.saveRecord(t5);
        t6 = apa.saveRecord(t6);

        recordsToDelete.add(t1);
        recordsToDelete.add(t2);
        recordsToDelete.add(t3);
        recordsToDelete.add(t4);
        recordsToDelete.add(t5);
        recordsToDelete.add(t6);        
    }
}
