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
package org.fracturedatlas.athena.reports.model.statement;

import java.util.ArrayList;
import java.util.List;

public class Expenses {
    List<ExpensesRow> expenses;
    ExpensesRow total;

    public Expenses() {
        expenses = new ArrayList<ExpensesRow>();
    }

    public List<ExpensesRow> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpensesRow> expenses) {
        this.expenses = expenses;
    }

    public ExpensesRow getTotal() {
        return total;
    }

    public void setTotal(ExpensesRow total) {
        this.total = total;
    }
    
    public void addExpense(ExpensesRow row) {
        expenses.add(row);
    }
}
