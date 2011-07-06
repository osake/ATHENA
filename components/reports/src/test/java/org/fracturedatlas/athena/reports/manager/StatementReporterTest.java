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

import org.junit.Before;
import org.fracturedatlas.athena.reports.model.statement.SalesRow;
import org.fracturedatlas.athena.reports.model.statement.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.joda.time.DateTime;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class StatementReporterTest extends ReporterTest {
    StatementReporter reporter = new StatementReporter();
    
    List<PTicket> tickets;
    DateTime now = new DateTime();
    DateTime lastWeek = now.minusWeeks(1);
    DateTime performanceTime = now.minusDays(1);

    @Test
    public void testStatementReportNoParams() {
        Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
        assertAthenaException(reporter, queryParams);
    }

    @Test
    public void testStatementReportNoOrganizationId() {
        Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
        queryParams.put("performanceId", Arrays.asList("20"));
        assertAthenaException(reporter, queryParams);
    }

    @Test
    public void testStatementReportNoPerformanceId() {
        Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
        queryParams.put("organizationId", Arrays.asList("33"));
        assertAthenaException(reporter, queryParams);
    }

    @Test
    public void testStatementReport() {
        Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
        queryParams.put("performanceId", Arrays.asList("20"));
        queryParams.put("organizationId", Arrays.asList("33"));


        AthenaSearch athenaSearch = new AthenaSearch.Builder()
                                              .type("ticket")
                                              .and("performanceId", Operator.EQUALS, "20")
                                              .and("organizationId", Operator.EQUALS, "33")
                                              .build();

        when(mockTix.find("ticket", athenaSearch)).thenReturn(tickets);
        
        Statement statement = (Statement)reporter.getReport(queryParams);
        assertEquals(1, statement.getSales().getPerformances().size());
        SalesRow row = statement.getSales().getPerformances().get(0);
        assertEquals(new Integer(4), row.getTicketsSold());
        assertEquals(new Integer(1), row.getTicketsComped());
        //assertEquals(new Double(13000), row.getGrossRevenue());
    }
    
    @Before
    public void injectMocks() {
        reporter.setAthenaStage(mockStage);
        reporter.setAthenaTix(mockTix);
    }
    
    @Before
    public void createTickets() {
        tickets = new ArrayList<PTicket>();
        
        /*
         * Two tickets sold for $40 in section ABC
         */
        tickets.add(makeRecord("ticket",
                               "performanceId", "20",
                               "performance", DateUtil.formatDate(performanceTime),
                               "organizationId", "33",
                               "price", "4000",
                               "state", "sold",
                               "soldPrice", "4000",
                               "section", "ABC",
                               "soldAt", DateUtil.formatDate(lastWeek)));
        
        tickets.add(makeRecord("ticket",
                               "performanceId", "20",
                               "performance", DateUtil.formatDate(performanceTime),
                               "organizationId", "33",
                               "price", "4000",
                               "state", "sold",
                               "soldPrice", "4000",
                               "section", "ABC",
                               "soldAt", DateUtil.formatDate(lastWeek)));
        
        /*
         * Two tickets sold for $25 in section XYZ
         */        
        tickets.add(makeRecord("ticket",
                               "performanceId", "20",
                               "performance", DateUtil.formatDate(performanceTime),
                               "organizationId", "33",
                               "price", "4000",
                               "state", "sold",
                               "soldPrice", "2500",
                               "section", "XYZ",
                               "soldAt", DateUtil.formatDate(lastWeek)));
        
        tickets.add(makeRecord("ticket",
                               "performanceId", "20",
                               "performance", DateUtil.formatDate(performanceTime),
                               "organizationId", "33",
                               "price", "4000",
                               "state", "sold",
                               "soldPrice", "2500",
                               "section", "XYZ",
                               "soldAt", DateUtil.formatDate(lastWeek)));
        
        /*
         * One ticket unsold in section ABC
         */         
        
        tickets.add(makeRecord("ticket",
                               "performanceId", "20",
                               "performance", DateUtil.formatDate(performanceTime),
                               "organizationId", "33",
                               "price", "4000",
                               "state", "on_sale",
                               "section", "ABC"));
        
        /*
         * One ticket comped in section ABC
         */         
        
        tickets.add(makeRecord("ticket",
                               "performanceId", "20",
                               "performance", DateUtil.formatDate(performanceTime),
                               "organizationId", "33",
                               "price", "4000",
                               "state", "comped",
                               "section", "ABC"));
        
        /*
         * One ticket off sale in section ABC
         */         
        
        tickets.add(makeRecord("ticket",
                               "performanceId", "20",
                               "performance", DateUtil.formatDate(performanceTime),
                               "organizationId", "33",
                               "price", "4000",
                               "state", "off_sale",
                               "section", "ABC"));
        
    }
}
