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

package org.fracturedatlas.athena.helper.ticketfactory.manager;

import org.fracturedatlas.athena.web.exception.ObjectNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.PTicketMatcher;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.web.exception.AthenaException;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TicketFactoryManagerTest {

    TicketFactoryManager manager = new TicketFactoryManager();
    PTicket samplePerformance;
    PTicket sampleSeatChart;
    PTicket sampleSection1;
    PTicket sampleSection2;
    PTicket sampleEvent;
    Integer totalNumberOfTickets;
    Collection<PTicket> sections;
    AthenaSearch athenaSearch;
    PTicketMatcher isAPTicket = new PTicketMatcher();

    public static final String SAMPLE_ORG_ID = "org123";

    @Mock private AthenaComponent mockStage;
    @Mock private RecordManager mockTicketManager;

    //For example, POSTing to /people/4444/createtickets
    @Test
    public void testCreateTicketsForUnknownParentType() throws Exception {
        try {
            List<PTicket> createdTickets = manager.save("dolphin", 
                                                    sampleSection1.getIdAsString(), 
                                                    "createticket", 
                                                    null, 
                                                    sampleSection1, 
                                                    null); 
            fail("Should have thrown ONFE");
        } catch (ObjectNotFoundException onfe) {
            //pass
        }       
    }
    
    @Test
    public void testCreateTicketsForSection() throws Exception {
        sampleSection1.put("performanceId", samplePerformance.getIdAsString());
        sampleSection1.put("capacity", "1");
        List<PTicket> createdTickets = manager.save("section", 
                                                    sampleSection1.getIdAsString(), 
                                                    "createticket", 
                                                    null, 
                                                    sampleSection1, 
                                                    null);
        assertEquals(1, createdTickets.size());
        PTicket pTicket = createdTickets.get(0);
        System.out.println(pTicket);
        assertEquals(sampleSection1.get("price"), pTicket.get("price"));
        assertEquals(sampleEvent.getIdAsString(), pTicket.get("eventId"));
        assertEquals(sampleEvent.get("name"), pTicket.get("event"));
        assertEquals(TicketFactoryManager.INITIAL_STATE, pTicket.get("state"));
        assertEquals(samplePerformance.getIdAsString(), pTicket.get("performanceId"));
        assertEquals(samplePerformance.get("datetime"), pTicket.get("performance"));
        assertEquals(sampleEvent.get("venue"), pTicket.get("venue"));
        assertEquals(sampleSection1.get("name"), pTicket.get("section"));
        assertEquals(SAMPLE_ORG_ID, pTicket.get("organizationId"));
        verify(mockStage).get("performance", samplePerformance.getId());
        verify(mockStage).get("chart", sampleSeatChart.getId());
        verify(mockStage).get("event", sampleEvent.getId());
        verify(mockStage).get("section", sampleSection1.getIdAsString());
        verify(mockTicketManager, times(Integer.parseInt(sampleSection1.get("capacity")))).createRecord(eq("ticket"), argThat(isAPTicket));
    }

    @Test
    public void testCreateTickets() throws Exception {
        manager.createTickets(samplePerformance);
        verify(mockStage).get("performance", samplePerformance.getId());
        verify(mockStage).get("chart", sampleSeatChart.getId());
        verify(mockStage).get("event", sampleEvent.getId());
        verify(mockStage).find("section", athenaSearch);
        verify(mockTicketManager, times(totalNumberOfTickets)).createRecord(eq("ticket"), argThat(isAPTicket));
    }

    /**
     * Testing creating tickets for a performance with a performanceId that Athena does not know about
     *
     * @throws Exception
     */
    @Test
    public void testCreateTicketsUnknownPerformance() throws Exception {

        samplePerformance = new PTicket();
        samplePerformance.setId("49");
        samplePerformance.put("chartId", (String)sampleSeatChart.getId());
        samplePerformance.put("organizationId", SAMPLE_ORG_ID);
        samplePerformance.put("eventId", sampleEvent.getIdAsString());
        samplePerformance.put("datetime", "2010-03-20T20:20:11-04:00");
        when(mockStage.get("performance", samplePerformance.getId())).thenReturn(null);
        try{
            manager.createTickets(samplePerformance);
            fail("Should have thrown an AthenaException");
        } catch (AthenaException ae) {
            //pass!
        }
        
        verify(mockStage, times(1)).get("performance", samplePerformance.getId());
        verify(mockTicketManager, never()).createRecord(eq("ticket"), argThat(isAPTicket));
        verify(mockStage, never()).save("performance", samplePerformance);
    }

    /**
     * Testing creating tickets for a performance with a performanceId that Athena does not know about
     *
     * @throws Exception
     */
    @Test
    public void testCreateTicketsNullPerformance() throws Exception {

        samplePerformance = new PTicket();
        samplePerformance.setId(null);
        when(mockStage.get("performance", samplePerformance.getId())).thenReturn(null);
        try{
            manager.createTickets(samplePerformance);
            fail("Should have thrown an AthenaException");
        } catch (AthenaException ae) {
            //pass!
        }

        verify(mockStage, times(1)).get("performance", samplePerformance.getId());
        verify(mockStage, never()).save("performance", samplePerformance);
    }

    public void createSampleObjects() {
        createSampleEvent();
        createSampleSeatChart();
        createSearchForSections();
        createSampleSections();
        createSamplePerformance();
    }

    public void createSearchForSections() {
       AthenaSearchConstraint con1 = new AthenaSearchConstraint("chartId", Operator.EQUALS, (String)sampleSeatChart.getId());
       athenaSearch = new AthenaSearch.Builder(con1).build();
    }

    public void createSampleEvent() {
        sampleEvent = new PTicket();
        sampleEvent.setId("31");
        sampleEvent.put("venue", "The Test Theater");
        sampleEvent.put("name", "The Test Tour");
    }

    public void createSampleSeatChart() {
        sampleSeatChart = new PTicket();
        sampleSeatChart.setId("900");
    }

    public void createSampleSections() {
        Integer orchestraSeats = 5;
        Integer balconySeats = 2;

        sections = new ArrayList<PTicket>();

        sampleSection1 = new PTicket();
        sampleSection1.setId("24");
        sampleSection1.put("chartId", sampleSeatChart.getIdAsString());
        sampleSection1.put("capacity", orchestraSeats.toString());
        sampleSection1.put("price", "25");
        sampleSection1.put("name", "Orchestra");
        sections.add(sampleSection1);

        sampleSection2 = new PTicket();
        sampleSection2.setId("25");
        sampleSection1.put("chartId", sampleSeatChart.getIdAsString());
        sampleSection2.put("capacity", balconySeats.toString());
        sampleSection2.put("price", "10");
        sampleSection2.put("name", "Balcony");
        sections.add(sampleSection2);

        totalNumberOfTickets = orchestraSeats + balconySeats;
    }

    public void createSamplePerformance() {
        samplePerformance = new PTicket();
        samplePerformance.setId("4");
        samplePerformance.put("chartId", (String)sampleSeatChart.getId());
        samplePerformance.put("eventId", (String)sampleEvent.getId());
        samplePerformance.put("datetime", "2010-03-20T20:20:11-04:00");
        samplePerformance.put("organizationId", SAMPLE_ORG_ID);
    }

    @Before
    public void mockupStage() {
        MockitoAnnotations.initMocks(this);
        createSampleObjects();
        when(mockStage.get("performance", samplePerformance.getId())).thenReturn(samplePerformance);
        when(mockStage.get("chart", sampleSeatChart.getId())).thenReturn(sampleSeatChart);
        when(mockStage.get("event", sampleEvent.getId())).thenReturn(sampleEvent);
        when(mockStage.get("section", sampleSection1.getIdAsString())).thenReturn(sampleSection1);
        when(mockStage.find("section", athenaSearch)).thenReturn(sections);
        when(mockStage.save("performance", samplePerformance)).thenReturn(samplePerformance);

        manager.setAthenaStage(mockStage);
        manager.setTicketManager(mockTicketManager);
    }
}
