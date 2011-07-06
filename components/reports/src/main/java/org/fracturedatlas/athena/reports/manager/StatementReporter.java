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
        
        athenaTix.find("ticket", search);
        
        Integer totalTicketsSold = 0;
        Integer totalTicketsComped = 0;
        Integer originalPotential = 0;
        Integer grossRevenue = 0;

        logger.debug("Searching for tickets matching {}", search);
        Collection<PTicket> tickets = athenaTix.find("ticket", search);
        logger.debug("Found {} tickets", tickets.size());
        DateTime now = new DateTime();
        for(PTicket ticket : tickets) {
            originalPotential += Integer.parseInt(ticket.get("price"));
            if("sold".equals(ticket.get("state"))) {
                totalTicketsSold++;
            } else if ("comped".equals(ticket.get("state"))) {
                totalTicketsComped++;
            }
        }
        
        
        sales.addPerformance(new SalesRow(new DateTime(), 
                                          totalTicketsSold, 
                                          totalTicketsComped, 5900, 4000, 3000));
        
        /*
         * EXPENSES
         */
        Expenses expenses = new Expenses();
        Integer ticketFees = (totalTicketsSold - totalTicketsComped) * 2;
        expenses.addExpense(new ExpensesRow("Ticket fees", Integer.toString(totalTicketsSold), "$2.00", ticketFees));
        Integer creditCardProcessingFees = 3456;
        expenses.addExpense(new ExpensesRow("Credit card processing", "$" + Integer.toString(grossRevenue), "3.5%", creditCardProcessingFees));
        expenses.setTotal(new ExpensesRow("Total", "", "", ticketFees + creditCardProcessingFees));
        
        /**
         * TODO: 
         * should tickets sold include comps?  It DOES in the glace remport, but feels wrong here
         * CCprocessing calculation
         * Potential, gross, net revs
         * Finish tests
         */
        
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
    
}
