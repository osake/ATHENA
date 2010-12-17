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

import java.util.ArrayList;
import java.util.Collection;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.PTicketMatcher;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.mockito.Mockito.*;

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

    @Mock private AthenaComponent mockStage;
    @Mock private RecordManager mockTicketManager;

    @Test
    public void createTickets() throws Exception {
        manager.createTickets(samplePerformance);
        verify(mockStage).get("performance", samplePerformance.get("id"));
        verify(mockStage).get("chart", sampleSeatChart.get("id"));
        verify(mockStage).get("event", sampleEvent.get("id"));
        verify(mockStage).find(athenaSearch);
        verify(mockTicketManager, times(totalNumberOfTickets)).saveTicketFromClientRequest(eq("ticket"), argThat(isAPTicket));
    }

    public void createSampleObjects() {
        createSampleEvent();
        createSampleSeatChart();
        createSearchForSections();
        createSampleSections();
        createSamplePerformance();
    }

    public void createSearchForSections() {
       AthenaSearchConstraint con1 = new AthenaSearchConstraint("chartId", Operator.EQUALS, sampleSeatChart.get("id"));
       athenaSearch = new AthenaSearch.Builder(con1).build();
    }

    public void createSampleEvent() {
        sampleEvent = new PTicket();
        sampleEvent.put("id", "31");
        sampleEvent.put("venue", "The Test Theater");
        sampleEvent.put("name", "The Test Tour");
    }

    public void createSampleSeatChart() {
        sampleSeatChart = new PTicket();
        sampleSeatChart.put("id", "900");
    }

    public void createSampleSections() {
        Integer orchestraSeats = 5;
        Integer balconySeats = 2;

        sections = new ArrayList<PTicket>();

        sampleSection1 = new PTicket();
        sampleSection1.put("id", "24");
        sampleSection1.put("capacity", orchestraSeats.toString());
        sampleSection1.put("price", "25");
        sampleSection1.put("name", "Orchestra");
        sections.add(sampleSection1);

        sampleSection2 = new PTicket();
        sampleSection2.put("id", "25");
        sampleSection2.put("capacity", balconySeats.toString());
        sampleSection2.put("price", "10");
        sampleSection2.put("name", "Balcony");
        sections.add(sampleSection2);

        totalNumberOfTickets = orchestraSeats + balconySeats;
    }

    public void createSamplePerformance() {
        samplePerformance = new PTicket();
        samplePerformance.put("id", "4");
        samplePerformance.put("chartId", sampleSeatChart.get("id"));
        samplePerformance.put("eventId", sampleEvent.get("id"));
        samplePerformance.put("datetime", "2010-03-20T20:20:11-04:00");
    }

    @Before
    public void mockupStage() {
        MockitoAnnotations.initMocks(this);
        createSampleObjects();
        when(mockStage.get("performance", samplePerformance.get("id"))).thenReturn(samplePerformance);
        when(mockStage.get("chart", sampleSeatChart.get("id"))).thenReturn(sampleSeatChart);
        when(mockStage.get("event", sampleEvent.get("id"))).thenReturn(sampleEvent);
        when(mockStage.find(athenaSearch)).thenReturn(sections);

        manager.setAthenaStage(mockStage);
        manager.setTicketManager(mockTicketManager);
    }
}
