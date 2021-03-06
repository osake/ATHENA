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

import com.sun.jersey.api.NotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fracturedatlas.athena.reports.model.AthenaReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ReportManager {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    @javax.ws.rs.core.Context
    ApplicationContext applicationContext;
    
    private static HashMap<String, Reporter> cachedReporters = new HashMap<String, Reporter>(); 
    
    public AthenaReport getReport(String reportType, Map<String, List<String>> queryParams) {
        try{
            logger.debug("Looking up reporter");
            long start = System.currentTimeMillis();
            
            String reporterName = reportType + "Reporter";
            
            Reporter reporter = cachedReporters.get(reporterName);
            
            if(reporter == null) {
                reporter = (Reporter)applicationContext.getBean(reporterName);
            }
            long end = System.currentTimeMillis();
            logger.debug("Found reporter in [{}ms], reporting", end-start);
            return reporter.getReport(queryParams);
        } catch (NoSuchBeanDefinitionException noBean) {
            throw new NotFoundException("Could not find a report named " + reportType);
        }
    }
}
