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

import java.util.List;
import java.util.Map;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.reports.model.AthenaReport;
import org.fracturedatlas.athena.reports.model.statement.Expenses;
import org.fracturedatlas.athena.reports.model.statement.ExpensesRow;
import org.fracturedatlas.athena.reports.model.statement.Sales;
import org.fracturedatlas.athena.reports.model.statement.SalesRow;
import org.fracturedatlas.athena.reports.model.statement.Statement;
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
        
        /*
         * PERFORMANCES
         */
        Sales sales = new Sales();
        sales.addPerformance(new SalesRow(new DateTime(), 22, 2, 5900, 4000, 3000));
        
        /*
         * EXPENSES
         */
        Expenses expenses = new Expenses();
        expenses.addExpense(new ExpensesRow("Ticket fees", "50", "$2.00", 100));
        expenses.addExpense(new ExpensesRow("Credit card processing", "49", "10%", 490));
        expenses.setTotal(new ExpensesRow("Total", "", "", 590));
        
        
        Statement s = new Statement(sales, expenses);
        
        return s;
    }
    
}
