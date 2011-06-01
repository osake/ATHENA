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

package org.fracturedatlas.athena.reports.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.reports.model.GlanceEventReport;
import org.fracturedatlas.athena.reports.model.GlancePerformanceReport;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.joda.time.DateTime;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GlanceReporterTest {

    GlanceReporter reporter = new GlanceReporter();

    @Mock private AthenaComponent mockStage;
    @Mock private AthenaComponent mockTix;

    List<PTicket> performances;
    List<PTicket> tickets;
    List<PTicket> performance19Tickets;

    @Test
    public void testGlancePerformanceReport() {
        createTickets();
        Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
        queryParams.put("performanceId", Arrays.asList("19"));
        queryParams.put("organizationId", Arrays.asList("33"));


        AthenaSearch athenaSearch = new AthenaSearch.Builder()
                                              .type("ticket")
                                              .and("performanceId", Operator.EQUALS, "19")
                                              .and("organizationId", Operator.EQUALS, "33")
                                              .build();

        when(mockTix.find("ticket", athenaSearch)).thenReturn(performance19Tickets);

        GlancePerformanceReport report = (GlancePerformanceReport)reporter.getReport(queryParams);
        
        verify(mockTix, times(1)).find("ticket", athenaSearch);
        assertNotNull(report);

        //TODO: Advance Sales, total played

        assertEquals(new Double(170), report.getRevenue().getTotalSales().getGross());
        assertEquals(new Double(50), report.getRevenue().getSoldToday().getGross());
        assertEquals(new Double(120), report.getRevenue().getPotentialRemaining().getGross());
        assertEquals(new Double(480), report.getRevenue().getOriginalPotential().getGross());
        assertEquals(new Integer(9), report.getTickets().getSold().getGross());
        assertEquals(new Integer(4), report.getTickets().getSold().getComped());
        assertEquals(new Integer(5), report.getTickets().getSoldToday().getGross());
        assertEquals(new Integer(3), report.getTickets().getSoldToday().getComped());
        assertEquals(new Integer(2), report.getTickets().getAvailable());

    }

    @Test
    public void testGlanceEventReport() {
        createTickets();
        Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
        queryParams.put("eventId", Arrays.asList("100"));
        queryParams.put("organizationId", Arrays.asList("33"));


        AthenaSearch athenaSearch = new AthenaSearch.Builder()
                                              .type("ticket")
                                              .and("eventId", Operator.EQUALS, "100")
                                              .and("organizationId", Operator.EQUALS, "33")
                                              .build();

        when(mockTix.find("ticket", athenaSearch)).thenReturn(tickets);


        AthenaSearch performancesSearch = new AthenaSearch.Builder()
                                              .type("performance")
                                              .and("eventId", Operator.EQUALS, "100")
                                              .and("state", Operator.EQUALS, "on_sale")
                                              .and("organizationId", Operator.EQUALS, "33")
                                              .build();

        when(mockStage.find("performance", performancesSearch)).thenReturn(performances);

        GlanceEventReport report = (GlanceEventReport)reporter.getReport(queryParams);

        verify(mockTix, times(1)).find("ticket", athenaSearch);
        verify(mockStage, times(1)).find("performance", performancesSearch);
        assertNotNull(report);

        assertEquals(new Integer(2), report.getPerformancesOnSale());

        assertEquals(new Double(220), report.getRevenue().getTotalSales().getGross());
        assertEquals(new Double(100), report.getRevenue().getSoldToday().getGross());
        assertEquals(new Double(360), report.getRevenue().getPotentialRemaining().getGross());
        assertEquals(new Double(800), report.getRevenue().getOriginalPotential().getGross());

        assertEquals(new Integer(11), report.getTickets().getSold().getGross());
        assertEquals(new Integer(4), report.getTickets().getSold().getComped());
        assertEquals(new Integer(7), report.getTickets().getSoldToday().getGross());
        assertEquals(new Integer(3), report.getTickets().getSoldToday().getComped());
        assertEquals(new Integer(8), report.getTickets().getAvailable());

    }

    public PTicket makeRecord(String type, String... keyValues) {
        PTicket t = new PTicket(type);
        for(int i=0; i < keyValues.length; i+=2) {
            t.put(keyValues[i], keyValues[i+1]);
        }
        return t;
    }

    //called from the before method
    public void createSampleObjects() throws Exception {

    }

    @Before
    public void mockit() throws Exception {
        MockitoAnnotations.initMocks(this);
        createSampleObjects();

        reporter.setAthenaStage(mockStage);
        reporter.setAthenaTix(mockTix);
    }

    public void createTickets() {
        tickets = new ArrayList<PTicket>();
        performance19Tickets = new ArrayList<PTicket>();
        performances = new ArrayList<PTicket>();

        DateTime lastWeek = new DateTime().minusWeeks(1);
        DateTime fourHoursAgo = new DateTime().minusHours(4);

        /*
         * THREE PERFORMANCES 19, 20, 21
         * Event 100
         */
        performances.add(makeRecord("performance",
                                    "eventId", "100",
                                    "state", "on_sale"));
        performances.add(makeRecord("performance",
                                    "eventId", "100",
                                    "state", "on_sale"));
        //21 is implied as off_sale or whatever


        /*
         * PERFORMANCE 19, Event 100
         */

        /*
         * Three tickets sold last week for $40
         */
        performance19Tickets.add(makeRecord("ticket",
                               "performanceId", "19",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "sold",
                               "soldPrice", "40.00",
                               "soldAt", DateUtil.formatDate(lastWeek)));
        performance19Tickets.add(makeRecord("ticket",
                               "performanceId", "19",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "sold",
                               "soldPrice", "40.00",
                               "soldAt", DateUtil.formatDate(lastWeek)));
        performance19Tickets.add(makeRecord("ticket",
                               "performanceId", "19",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "sold",
                               "soldPrice", "40.00",
                               "soldAt", DateUtil.formatDate(lastWeek)));

        /*
         * Two tickets sold four hours ago for $25
         */
        performance19Tickets.add(makeRecord("ticket",
                               "performanceId", "19",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "sold",
                               "soldPrice", "25.00",
                               "soldAt", DateUtil.formatDate(fourHoursAgo)));
        performance19Tickets.add(makeRecord("ticket",
                               "performanceId", "19",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "sold",
                               "soldPrice", "25.00",
                               "soldAt", DateUtil.formatDate(fourHoursAgo)));

        /*
         * One comped last week
         */
        performance19Tickets.add(makeRecord("ticket",
                               "performanceId", "19",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "comped",
                               "soldPrice", "0",
                               "soldAt", DateUtil.formatDate(lastWeek)));

        /*
         * Three comped four hours ago
         */
        performance19Tickets.add(makeRecord("ticket",
                               "performanceId", "19",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "comped",
                               "soldPrice", "0",
                               "soldAt", DateUtil.formatDate(fourHoursAgo)));
        performance19Tickets.add(makeRecord("ticket",
                               "performanceId", "19",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "comped",
                               "soldPrice", "0",
                               "soldAt", DateUtil.formatDate(fourHoursAgo)));
        performance19Tickets.add(makeRecord("ticket",
                               "performanceId", "19",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "comped",
                               "soldPrice", "0",
                               "soldAt", DateUtil.formatDate(fourHoursAgo)));


        /*
         * One off_sale
         */
        performance19Tickets.add(makeRecord("ticket",
                               "performanceId", "19",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "off_sale"));

        /*
         * Two on_sale
         */
        performance19Tickets.add(makeRecord("ticket",
                               "performanceId", "19",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "on_sale"));
        performance19Tickets.add(makeRecord("ticket",
                               "performanceId", "19",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "on_sale"));

        tickets.addAll(performance19Tickets);

        /*
         * PERFORMANCE 20, Event 100
         */

        /*
         * Two tickets sold four hours ago for $25
         */
        tickets.add(makeRecord("ticket",
                               "performanceId", "20",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "sold",
                               "soldPrice", "25.00",
                               "soldAt", DateUtil.formatDate(fourHoursAgo)));
        tickets.add(makeRecord("ticket",
                               "performanceId", "20",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "sold",
                               "soldPrice", "25.00",
                               "soldAt", DateUtil.formatDate(fourHoursAgo)));

        /*
         * Two on_sale
         */
        tickets.add(makeRecord("ticket",
                               "performanceId", "20",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "on_sale"));
        tickets.add(makeRecord("ticket",
                               "performanceId", "20",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "on_sale"));

        /*
         * PERFORMANCE 21, Event 100
         */

        /*
         * Four on_sale
         */
        tickets.add(makeRecord("ticket",
                               "performanceId", "21",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "on_sale"));
        tickets.add(makeRecord("ticket",
                               "performanceId", "21",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "on_sale"));
        tickets.add(makeRecord("ticket",
                               "performanceId", "21",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "on_sale"));
        tickets.add(makeRecord("ticket",
                               "performanceId", "21",
                               "organizationId", "33",
                               "price", "40.00",
                               "state", "on_sale"));

    }
}
