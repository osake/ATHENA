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
import org.fracturedatlas.athena.reports.model.GlanceEventReport;
import org.fracturedatlas.athena.reports.model.GlancePerformanceReport;
import org.fracturedatlas.athena.reports.model.GrossComped;
import org.fracturedatlas.athena.reports.model.GrossNet;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.web.exception.AthenaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GlanceReporter implements Reporter {

    @Autowired
    AthenaComponent athenaStage;

    @Autowired
    AthenaComponent athenaTix;


    @Override
    public AthenaReport getReport(Map<String, List<String>> queryParameters) {

        AthenaReport report;

        if(isEmpty(queryParameters)) {
            throw new AthenaException("Please specify either eventId or performanceId");
        } else if (isEventQuery(queryParameters)) {
            String eventId = queryParameters.get("eventId").get(0);
            String organizationId = queryParameters.get("organizationId").get(0);
            report = loadGlanceEventReport(eventId, organizationId);
        } else if (isPerformanceQuery(queryParameters)) {
            String performanceId = queryParameters.get("performanceId").get(0);
            String organizationId = queryParameters.get("organizationId").get(0);
            report = loadGlancePerformanceReport(performanceId, organizationId);
        } else {
            throw new AthenaException("Please specify either eventId or performanceId");
        }
        
        return report;
    }

    public Boolean isEventQuery(Map<String, List<String>> queryParameters) {
        return queryParameters.get("eventId") != null;
    }

    public Boolean isPerformanceQuery(Map<String, List<String>> queryParameters) {
        return queryParameters.get("performanceId") != null;
    }

    public GlancePerformanceReport loadGlancePerformanceReport(String performanceId, String organizationId) {

        GlancePerformanceReport report = new GlancePerformanceReport();

        AthenaSearch search = new AthenaSearch.Builder()
                                              .type("ticket")
                                              .and(performanceId, Operator.EQUALS, performanceId)
                                              .and(organizationId, Operator.EQUALS, organizationId)
                                              .build();
        Collection<PTicket> tickets = athenaTix.find("ticket", search);
        Double totalSales = 0D;
        for(PTicket ticket : tickets) {
            if("sold".equals(ticket.get("state"))) {
                totalSales += Double.parseDouble(ticket.get("price"));
            }
        }

        report.getRevenue().setAdvanceSales(new GrossNet(300, 270));
        report.getRevenue().setSoldToday(new GrossNet(90, 81));
        report.getRevenue().setPotentialRemaining(new GrossNet(2885.74, 2558.33));
        report.getRevenue().setOriginalPotential(new GrossNet(29635.55, 19885.02));
        report.getRevenue().setTotalSales(new GrossNet(totalSales, 0D));

        report.getTickets().setSold(new GrossComped(100, 20));
        report.getTickets().setSoldToday(new GrossComped(10, 0));
        report.getTickets().setAvailable(65);

        return report;
    }
    
    public GlanceEventReport loadGlanceEventReport(String eventId, String organizationId) {

        GlanceEventReport report = new GlanceEventReport();
        
        report.setPerformancesOnSale(40);
        report.getRevenue().setAdvanceSales(new GrossNet(300, 270));
        report.getRevenue().setSoldToday(new GrossNet(90, 81));
        report.getRevenue().setPotentialRemaining(new GrossNet(2885.74, 2558.33));
        report.getRevenue().setOriginalPotential(new GrossNet(29635.55, 19885.02));
        report.getRevenue().setTotalSales(new GrossNet(9959.99, 4562.25));
        report.getRevenue().setTotalPlayed(new GrossNet(4500.44, 4000.80));

        report.getTickets().setSold(new GrossComped(100, 20));
        report.getTickets().setSoldToday(new GrossComped(10, 0));
        report.getTickets().setPlayed(new GrossComped(9, null));
        report.getTickets().setAvailable(65);
        
        return report;
    }

    private boolean isEmpty(Map map) {
        return (map != null) && (map.keySet().size() == 0);
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
