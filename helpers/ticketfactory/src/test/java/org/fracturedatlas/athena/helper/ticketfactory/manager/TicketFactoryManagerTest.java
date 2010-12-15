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
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.mockito.Mockito.*;

public class TicketFactoryManagerTest {

    ApplicationContext context = new ClassPathXmlApplicationContext("athenatest-applicationContext.xml");
    TicketFactoryManager manager;
    PTicket samplePerformance;
    PTicket sampleSeatChart;
    PTicket sampleSection1;
    PTicket sampleSection2;
    Collection<PTicket> sections;
    AthenaSearch athenaSearch;

    AthenaComponent mockStage;

    @Test
    public void createTickets() throws Exception {
        manager.createTickets(samplePerformance);
        verify(mockStage).get("performance", samplePerformance.get("id"));
        verify(mockStage).get("chart", sampleSeatChart.get("id"));
    }

    public void injectmockStageIntoManager(AthenaComponent mockStage) {
        manager = (TicketFactoryManager)context.getBean("ticketFactoryManager");
        manager.setAthenaStage(mockStage);
    }

    public void createSampleObjects() {
        createSampleSeatChart();
        createSearchForSections();
        createSampleSections();
        createSamplePerformance();
    }

    public void createSearchForSections() {
       AthenaSearchConstraint con1 = new AthenaSearchConstraint("chartId", Operator.EQUALS, sampleSeatChart.get("id"));
       athenaSearch = new AthenaSearch.Builder(con1).build();
    }

    public void createSampleSeatChart() {
        sampleSeatChart = new PTicket();
        sampleSeatChart.put("id", "900");
    }

    public void createSampleSections() {
        sections = new ArrayList<PTicket>();

        sampleSection1 = new PTicket();
        sampleSection1.put("id", "24");
        sections.add(sampleSection1);

        sampleSection2 = new PTicket();
        sampleSection2.put("id", "25");        
        sections.add(sampleSection2);
    }

    public void createSamplePerformance() {
        samplePerformance = new PTicket();
        samplePerformance.put("id", "4");
        samplePerformance.put("chartId", sampleSeatChart.get("id"));
    }

    @Before
    public void mockupStage() {
        createSampleObjects();
        mockStage = mock(AthenaComponent.class);
        when(mockStage.get("performance", samplePerformance.get("id"))).thenReturn(samplePerformance);
        when(mockStage.get("chart", sampleSeatChart.get("id"))).thenReturn(sampleSeatChart);
        when(mockStage.find(athenaSearch)).thenReturn(sections);
        
        injectmockStageIntoManager(mockStage);
    }
}
