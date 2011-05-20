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

package org.fracturedatlas.athena.plugin.twitter;

import com.sun.jersey.api.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class LiveTwitterTest {
    TweetSubResource tweetSubResource = new TweetSubResource();
    
    @Mock private RecordManager mockRecordManager;
    
    public LiveTwitterTest() {
        
    }

    @Test
    public void testGetTweets() {
        PTicket person = new PTicket("person");
        person.setId("3");
        person.put("twitterHandle", "gsmoore");
        PTicket action = new PTicket("action");
        action.setId("355");
        Set<PTicket> actions = new HashSet<PTicket>();
        actions.add(action);

        when(mockRecordManager.getTicket("person", person.getId())).thenReturn(person);
        List<PTicket> tweets = tweetSubResource.execute("person", "3", "tweet", null, null);
        assertNotNull(tweets);
        assertNotNull(tweets.get(0).get("text"));

    }

    @Test
    public void testGetTweetsNotFound() {
        PTicket person = new PTicket("person");
        person.setId("3");
        person.put("twitterHandle", "no_way_someone_has_This_registered");
        PTicket action = new PTicket("action");
        action.setId("355");
        Set<PTicket> actions = new HashSet<PTicket>();
        actions.add(action);

        when(mockRecordManager.getTicket("person", person.getId())).thenReturn(person);
        try {
            List<PTicket> tweets = tweetSubResource.execute("person", "3", "tweet", null, null);
            fail("No NFE");
        } catch (NotFoundException e) {
            //cool
        }

    }

    @Test
    public void testGetTweetsNoTwitterHandleStored() {
        PTicket person = new PTicket("person");
        person.setId("3");
        person.put("twitterHandle", "");
        PTicket action = new PTicket("action");
        action.setId("355");
        Set<PTicket> actions = new HashSet<PTicket>();
        actions.add(action);

        when(mockRecordManager.getTicket("person", person.getId())).thenReturn(person);
        try {
            List<PTicket> tweets = tweetSubResource.execute("person", "3", "tweet", null, null);
            fail("No NFE");
        } catch (NotFoundException e) {
            //cool
        }

    }

    @Test
    public void testGetTweetsNoTwitterProp() {
        PTicket person = new PTicket("person");
        person.setId("3");
        PTicket action = new PTicket("action");
        action.setId("355");
        Set<PTicket> actions = new HashSet<PTicket>();
        actions.add(action);

        when(mockRecordManager.getTicket("person", person.getId())).thenReturn(person);
        try {
            List<PTicket> tweets = tweetSubResource.execute("person", "3", "tweet", null, null);
            fail("No NFE");
        } catch (NotFoundException e) {
            //cool
        }

    }

    @Test
    public void testGetTweetsNoPersonFound() {
        when(mockRecordManager.getTicket("person", "3")).thenReturn(null);
        try {
            List<PTicket> tweets = tweetSubResource.execute("person", "3", "tweet", null, null);
            fail("No NFE");
        } catch (NotFoundException e) {
            //cool
        }

    }

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        tweetSubResource.setRecordManager(mockRecordManager);

    }
}
