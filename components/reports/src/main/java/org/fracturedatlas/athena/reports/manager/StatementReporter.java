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

import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.reports.model.AthenaReport;
import org.fracturedatlas.athena.reports.model.statement.Expenses;
import org.fracturedatlas.athena.reports.model.statement.ExpensesRow;
import org.fracturedatlas.athena.reports.model.statement.Sales;
import org.fracturedatlas.athena.reports.model.statement.SalesRow;
import org.fracturedatlas.athena.reports.model.statement.Statement;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatementReporter implements Reporter {

    @Autowired
    AthenaComponent athenaStage;

    @Autowired
    AthenaComponent athenaTix;

    @Autowired
    AthenaComponent athenaOrders;

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());


    @Override
    public AthenaReport getReport(Map<String, List<String>> queryParameters) {
        
        ParamChecker.check(queryParameters, "organizationId", "performanceId");
        String performanceId = queryParameters.get("performanceId").get(0);
        String organizationId = queryParameters.get("organizationId").get(0);
        
        /*
         * PERFORMANCES
         */
        Sales sales = new Sales();
        
        AthenaSearch search = new AthenaSearch.Builder()
                                              .type("ticket")
                                              .and("performanceId", Operator.EQUALS, performanceId)
                                              .and("organizationId", Operator.EQUALS, organizationId)
                                              .build();
        
        logger.debug("Searching for tickets matching {}", search);
        Collection<PTicket> tickets = athenaTix.find("ticket", search);
        
        Integer totalTicketsSold = 0;
        Integer totalTicketsComped = 0;
        Integer originalPotential = 0;
        DateTime performanceDate = null;

        logger.debug("Found {} tickets", tickets.size());
        DateTime now = new DateTime();
        for(PTicket ticket : tickets) {
            if("sold".equals(ticket.get("state"))) {
                totalTicketsSold++;
            } else if ("comped".equals(ticket.get("state"))) {
                totalTicketsComped++;
            }
            
            originalPotential += Integer.parseInt(ticket.get("price"));
            
            //HACK: This saves another call to athenaStage to get the date.  Just pull the date off of the ticket.
            if(performanceDate == null) {
                try{
                    performanceDate = DateUtil.parseDateTime(ticket.get("performance"));
                } catch (ParseException pe) {
                    logger.error("Performance date on ticket [{}] is malformed: [{}]", ticket.getId(), ticket.get("performance"));
                    logger.error("{}", pe);
                    performanceDate = null;
                }
            }
            
        }
        
        search = new AthenaSearch.Builder()
                                 .type("item")
                                 .and("performanceId", Operator.EQUALS, performanceId)
                                 .and("productType", Operator.EQUALS, "AthenaTicket")
                                 .build();  
        
        Collection<PTicket> items = athenaOrders.find("item", search);
        
        Integer grossRevenue = 0;
        Integer netRevenue = 0;
        
        for(PTicket item : items) {
            String state = item.get("state");
            
            /*
             * TODO: Brittle
             * state == null means purchased item
             * state == returned is a ticket that has been exchanged
             * state == refunded is a returned item
             * state == refund is THE ITEM THAT REPRESENTS THE RETURN ON THE ORDER PAGE         
             * 
             * We need to consider, null, refunded (which will have a positive price), refund (which will have a negative price)
             * returned items have no corresponding negative entry, so we need to skip those
             */
            if(state == null || state.equals("refunded") || state.equals("refund")) {
                netRevenue += Integer.parseInt(item.get("net"));
                grossRevenue += Integer.parseInt(item.get("price"));
            }
        }
        
        
        sales.addPerformance(new SalesRow(performanceDate, 
                                          totalTicketsSold, 
                                          totalTicketsComped, 
                                          originalPotential, grossRevenue, netRevenue));
        
        /*
         * EXPENSES
         */
        Expenses expenses = new Expenses();
        Integer ticketFees = (totalTicketsSold) * 200;
        expenses.addExpense(new ExpensesRow("Ticket fees", Integer.toString(totalTicketsSold), "$2.00", ticketFees));
        
        Integer revenueSubjectToCCFees = grossRevenue - ticketFees;
        Double doubleRevenueSubjectToCCFees = (double)revenueSubjectToCCFees / 100;
        String strRevenueSubjectToCCFees = String.format("%.2f", doubleRevenueSubjectToCCFees);
        
        Double creditCardProcessingFees = revenueSubjectToCCFees * .035;
        Long longCCfees = Math.round(creditCardProcessingFees);
        Integer intCCFees = longCCfees.intValue();
        expenses.addExpense(new ExpensesRow("Credit card processing", "$" + strRevenueSubjectToCCFees, "3.5%", intCCFees));
        expenses.setTotal(new ExpensesRow("Total", "", "", ticketFees + intCCFees));
        
        Statement s = new Statement(sales, expenses);
        
        return s;
    }

    public AthenaComponent getAthenaStage() {
        return athenaStage;
    }

    public void setAthenaStage(AthenaComponent athenaStage) {
        this.athenaStage = athenaStage;
    }

    public AthenaComponent getAthenaTix() {
        return athenaTix;
    }

    public void setAthenaTix(AthenaComponent athenaTix) {
        this.athenaTix = athenaTix;
    }

    public AthenaComponent getAthenaOrders() {
        return athenaOrders;
    }

    public void setAthenaOrders(AthenaComponent athenaOrders) {
        this.athenaOrders = athenaOrders;
    }
    
}
