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
package org.fracturedatlas.athena.helper.ticketfactory.manager;

import java.util.Collection;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.springframework.stereotype.Component;

@Component
public class TicketFactoryManager {

    private AthenaComponent athenaStage;

    public void createTickets(PTicket pTicket) {
        String performanceId = pTicket.get("id");
        PTicket performance = athenaStage.get("performance", performanceId);
        String chartId = performance.get("chartId");
        PTicket chart = athenaStage.get("chart", chartId);

        AthenaSearchConstraint sectionSearch = new AthenaSearchConstraint("chartId", Operator.EQUALS, chartId);
        AthenaSearch athenaSearch = new AthenaSearch.Builder(sectionSearch).build();
        Collection<PTicket> sections = athenaStage.find(athenaSearch);

        //for each section
            //for capacity
                //build ticket, load performance, event, venue, etc...
                //on_sale = false

        //for all tickets built
            //post to tix apa

        //mark performance as "tickets_created"
    }

    public AthenaComponent getAthenaStage() {
        return athenaStage;
    }

    public void setAthenaStage(AthenaComponent athenaStage) {
        this.athenaStage = athenaStage;
    }
}
