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

package org.fracturedatlas.athena.web.manager;


import com.sun.jersey.api.NotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class SubResourceManagerTest {

    RecordManager manager;
    @Mock private ApaAdapter mockApa;

    @Mock private SecurityContextHolderStrategy mockSecurityContextHolderStrategy;
    @Mock private SecurityContext mockSecurityContext;
    @Mock private User mockUser;
    @Mock private Authentication mockAuthentication;
    @Mock private HttpServletRequest mockHttpServletRequest;
    @Mock private ApplicationContext mockApplicationContext;
    @Mock private AbstractAthenaSubResource mockSubResource;

    public SubResourceManagerTest() {
        manager = new RecordManager();
    }

    @Test
    public void testParentResourceNotFound() {
        try{
            Collection<PTicket> records = manager.findSubResources("people", "3", "actions", null);
            fail("Looking for NotFoundException");
        } catch (NotFoundException nfe) {
            //pass!
        }
    }

    @Test
    public void testFindSubResources() {
        PTicket person = new PTicket("person");
        person.setId("3");
        PTicket action = new PTicket("action");
        action.setId("355");
        Set<PTicket> actions = new HashSet<PTicket>();
        actions.add(action);

        when(mockApa.getRecord("person", "3")).thenReturn(person);
        AthenaSearch athenaSearch = new AthenaSearch
                    .Builder(new AthenaSearchConstraint("personId", Operator.EQUALS, "3"))
                    .type("action")
                    .build();
        when(mockApa.findTickets(athenaSearch)).thenReturn(actions);

        Collection<PTicket> records = manager.findSubResources("person", "3", "action", null);
        assertEquals(1, records.size());
        assertEquals("355", records.iterator().next().getId());
        verify(mockApa, times(1)).findTickets(athenaSearch);
    }

    @Test
    public void testFindSubResourcesByPlugin() {
        PTicket person = new PTicket("person");
        person.setId("3");
        PTicket action = new PTicket("blogpost");
        action.setId("231");
        Set<PTicket> actions = new HashSet<PTicket>();
        List<PTicket> actionList = new ArrayList<PTicket>();
        actions.add(action);
        actionList.addAll(actions);

        when(mockApa.getRecord("person", "3")).thenReturn(person);
        when(mockSubResource.find(eq("person"), eq("3"), eq("blogpost"), Matchers.anyMap(), eq("jowens33"))).thenReturn(actionList);
        when(mockApplicationContext.getBean("blogpostSubResource")).thenReturn(mockSubResource);
        
        Collection<PTicket> records = manager.findSubResources("person", "3", "blogpost", null);
        assertEquals(1, records.size());
        assertEquals("231", records.iterator().next().getId());
        verify(mockSubResource, times(1)).find(eq("person"), eq("3"), eq("blogpost"), Matchers.anyMap(), eq("jowens33"));
    }

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        manager.setApa(mockApa);
        manager.setApplicationContext(mockApplicationContext);
        createMockUser("jowens33");
    }

    public void createMockUser(String username) {
        when(mockUser.getUsername()).thenReturn(username);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUser);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockSecurityContextHolderStrategy.getContext()).thenReturn(mockSecurityContext);

        manager.setContextHolderStrategy(mockSecurityContextHolderStrategy);
    }


}
