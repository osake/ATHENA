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

package org.fracturedatlas.athena.reports.model;

public class GlanceReport implements AthenaReport {
    Revenue revenue;
    Tickets tickets;

    public GlanceReport() {
        revenue = new Revenue();
        tickets = new Tickets();
    }

    public Revenue getRevenue() {
        return revenue;
    }

    public void setRevenue(Revenue revenue) {
        this.revenue = revenue;
    }

    public Tickets getTickets() {
        return tickets;
    }

    public void setTickets(Tickets tickets) {
        this.tickets = tickets;
    }

    public class Tickets {
        GrossComped sold;
        GrossComped soldToday;
        GrossComped played;
        Integer available;

        public Integer getAvailable() {
            return available;
        }

        public void setAvailable(Integer available) {
            this.available = available;
        }

        public GrossComped getPlayed() {
            return played;
        }

        public void setPlayed(GrossComped played) {
            this.played = played;
        }

        public GrossComped getSold() {
            return sold;
        }

        public void setSold(GrossComped sold) {
            this.sold = sold;
        }

        public GrossComped getSoldToday() {
            return soldToday;
        }

        public void setSoldToday(GrossComped soldToday) {
            this.soldToday = soldToday;
        }
    }

    public class Revenue {
        GrossNet advanceSales;
        GrossNet soldToday;
        GrossNet potentialRemaining;
        GrossNet originalPotential;
        GrossNet totalSales;
        GrossNet totalPlayed;

        public GrossNet getAdvanceSales() {
            return advanceSales;
        }

        public void setAdvanceSales(GrossNet advanceSales) {
            this.advanceSales = advanceSales;
        }

        public GrossNet getOriginalPotential() {
            return originalPotential;
        }

        public void setOriginalPotential(GrossNet originalPotential) {
            this.originalPotential = originalPotential;
        }

        public GrossNet getPotentialRemaining() {
            return potentialRemaining;
        }

        public void setPotentialRemaining(GrossNet potentialRemaining) {
            this.potentialRemaining = potentialRemaining;
        }

        public GrossNet getSoldToday() {
            return soldToday;
        }

        public void setSoldToday(GrossNet soldToday) {
            this.soldToday = soldToday;
        }

        public GrossNet getTotalPlayed() {
            return totalPlayed;
        }

        public void setTotalPlayed(GrossNet totalPlayed) {
            this.totalPlayed = totalPlayed;
        }

        public GrossNet getTotalSales() {
            return totalSales;
        }

        public void setTotalSales(GrossNet totalSales) {
            this.totalSales = totalSales;
        }
    }

}
