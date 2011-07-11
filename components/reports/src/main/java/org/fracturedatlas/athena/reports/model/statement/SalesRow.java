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

import org.joda.time.DateTime;

public class SalesRow {
    DateTime datetime;
    Integer ticketsSold;
    Integer ticketsComped;
    Integer potentialRevenue;
    Integer grossRevenue;
    Integer netRevenue;

    public SalesRow() {
    }

    public SalesRow(DateTime datetime, Integer ticketsSold, Integer ticketsComped, Integer potentialRevenue, Integer grossRevenue, Integer netRevenue) {
        this.datetime = datetime;
        this.ticketsSold = ticketsSold;
        this.ticketsComped = ticketsComped;
        this.potentialRevenue = potentialRevenue;
        this.grossRevenue = grossRevenue;
        this.netRevenue = netRevenue;
    }

    public Integer getPotentialRevenue() {
        return potentialRevenue;
    }

    public void setPotentialRevenue(Integer potentialRevenue) {
        this.potentialRevenue = potentialRevenue;
    }
    
    public DateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(DateTime datetime) {
        this.datetime = datetime;
    }

    public Integer getGrossRevenue() {
        return grossRevenue;
    }

    public void setGrossRevenue(Integer grossRevenue) {
        this.grossRevenue = grossRevenue;
    }

    public Integer getNetRevenue() {
        return netRevenue;
    }

    public void setNetRevenue(Integer netRevenue) {
        this.netRevenue = netRevenue;
    }

    public Integer getTicketsComped() {
        return ticketsComped;
    }

    public void setTicketsComped(Integer ticketsComped) {
        this.ticketsComped = ticketsComped;
    }

    public Integer getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(Integer ticketsSold) {
        this.ticketsSold = ticketsSold;
    }
    
    
}
