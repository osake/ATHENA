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
import org.fracturedatlas.athena.reports.model.AthenaReport;
import org.fracturedatlas.athena.reports.model.GlanceEventReport;
import org.fracturedatlas.athena.reports.model.GlancePerformanceReport;
import org.fracturedatlas.athena.reports.model.GrossComped;
import org.fracturedatlas.athena.reports.model.GrossNet;
import org.fracturedatlas.athena.web.exception.AthenaException;
import org.springframework.stereotype.Component;

@Component
public class GlanceReporter implements Reporter {

    @Override
    public AthenaReport getReport(Map<String, List<String>> queryParameters) {

        AthenaReport report;

        if(isEmpty(queryParameters)) {
            throw new AthenaException("Please specify either eventId or performanceId");
        } else if (isEventQuery(queryParameters)) {
            String eventId = queryParameters.get("eventId").get(0);
            report = loadGlanceEventReport(eventId);
        } else if (isPerformanceQuery(queryParameters)) {
            String performanceId = queryParameters.get("performanceId").get(0);
            report = loadGlancePerformanceReport(performanceId);
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

    public GlancePerformanceReport loadGlancePerformanceReport(String eventId) {

        GlancePerformanceReport report = new GlancePerformanceReport();

        report.getRevenue().setAdvanceSales(new GrossNet(300, 270));
        report.getRevenue().setSoldToday(new GrossNet(90, 81));
        report.getRevenue().setPotentialRemaining(new GrossNet(2885.74, 2558.33));
        report.getRevenue().setOriginalPotential(new GrossNet(29635.55, 19885.02));
        report.getRevenue().setTotalSales(new GrossNet(9959.99, 4562.25));

        report.getTickets().setSold(new GrossComped(100, 20));
        report.getTickets().setSoldToday(new GrossComped(10, 0));
        report.getTickets().setAvailable(65);

        return report;
    }
    
    public GlanceEventReport loadGlanceEventReport(String eventId) {

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
}
