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

import org.fracturedatlas.athena.reports.model.statement.Expenses;
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
    
    PTicket performance;
    List<PTicket> tickets;
    List<PTicket> items;
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


        AthenaSearch itemSearch = new AthenaSearch.Builder()
                                 .type("item")
                                 .and("performanceId", Operator.EQUALS, "20")
                                 .and("productType", Operator.EQUALS, "AthenaTicket")
                                 .build();  
        when(mockOrders.find("item", itemSearch)).thenReturn(items);
        
        Statement statement = (Statement)reporter.getReport(queryParams);
        assertEquals(1, statement.getSales().getPerformances().size());
        SalesRow row = statement.getSales().getPerformances().get(0);
        assertEquals(performanceTime.withMillisOfSecond(0), row.getDatetime());
        assertEquals(new Integer(4), row.getTicketsSold());
        assertEquals(new Integer(1), row.getTicketsComped());
        assertEquals(new Integer(28000), row.getPotentialRevenue());
        assertEquals(new Integer(13000), row.getGrossRevenue());
        assertEquals(new Integer(11574), row.getNetRevenue());
        
        Expenses expenses = statement.getExpenses();
        assertNotNull(expenses);
        assertEquals(2, expenses.getExpenses().size());
        
        assertEquals("Ticket fees", expenses.getExpenses().get(0).getDescription());
        assertEquals("4", expenses.getExpenses().get(0).getUnits());
        assertEquals("$2.00", expenses.getExpenses().get(0).getRate());
        assertEquals(new Integer(800), expenses.getExpenses().get(0).getExpense());
        
        assertEquals("Credit card processing", expenses.getExpenses().get(1).getDescription());
        assertEquals("$122.00", expenses.getExpenses().get(1).getUnits());
        assertEquals("3.5%", expenses.getExpenses().get(1).getRate());
        assertEquals(new Integer(427), expenses.getExpenses().get(1).getExpense());
        
        assertEquals("Total", expenses.getTotal().getDescription());
        assertEquals("", expenses.getTotal().getUnits());
        assertEquals("", expenses.getTotal().getRate());
        assertEquals(new Integer(1227), expenses.getTotal().getExpense());
    }
    
    @Before
    public void injectMocks() {
        reporter.setAthenaStage(mockStage);
        reporter.setAthenaTix(mockTix);
        reporter.setAthenaOrders(mockOrders);
    }
    
    @Before
    public void createTickets() {
        tickets = new ArrayList<PTicket>();
        items = new ArrayList<PTicket>();
        
        performance = makeRecord("performance",
                                    "eventId", "100",
                                    "state", "on_sale",
                                    "id", "20");
        
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
         * And their corresponding order items
         */
        items.add(makeRecord("item",
                            "price","4000",
                            "performanceId","20",
                            "net","3667",
                            "orderId","32768",
                            "productId","32768",
                            "productType","AthenaTicket",
                            "realizedPrice","3800"));
        items.add(makeRecord("item",
                            "price","4000",
                            "performanceId","20",
                            "net","3667",
                            "orderId","32768",
                            "productId","32768",
                            "productType","AthenaTicket",
                            "realizedPrice","3800"));
        
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
         * And their corresponding order items
         */
        items.add(makeRecord("item",
                            "price","2500",
                            "performanceId","20",
                            "net","2120",
                            "orderId","32768",
                            "productId","32768",
                            "productType","AthenaTicket",
                            "realizedPrice","2300"));
        items.add(makeRecord("item",
                            "price","2500",
                            "performanceId","20",
                            "net","2120",
                            "orderId","32768",
                            "productId","32768",
                            "productType","AthenaTicket",
                            "realizedPrice","2300"));
        
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
