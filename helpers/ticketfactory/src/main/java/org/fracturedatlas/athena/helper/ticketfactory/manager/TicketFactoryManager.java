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

import java.util.ArrayList;
import java.util.Collection;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketFactoryManager {

    @Autowired
    private AthenaComponent athenaStage;

    @Autowired
    private RecordManager ticketManager;

    public void createTickets(PTicket pTicket) {
        String performanceId = pTicket.get("id");
        PTicket performance = athenaStage.get("performance", performanceId);
        String chartId = performance.get("chartId");
        String eventId = performance.get("eventId");
        PTicket chart = athenaStage.get("chart", chartId);
        PTicket event = athenaStage.get("event", eventId);

        AthenaSearchConstraint sectionSearch = new AthenaSearchConstraint("chartId", Operator.EQUALS, chartId);
        AthenaSearch athenaSearch = new AthenaSearch.Builder(sectionSearch).build();
        Collection<PTicket> sections = athenaStage.find(athenaSearch);

        ArrayList<PTicket> ticketsToCreate = new ArrayList<PTicket>();

        //for each section
        for(PTicket section : sections) {
            Integer capacity = Integer.parseInt(section.get("capacity"));
            for(int seatNum = 0; seatNum < capacity; seatNum++) {
                PTicket ticket = new PTicket();
                ticket.put("price", section.get("price"));
                ticket.put("performanceId", performanceId);
                ticket.put("performance", performance.get("datetime"));
                ticket.put("onSale", "false");
                ticket.put("sold", "false");
                ticket.put("section", section.get("name"));
                ticket.put("venue", event.get("venue"));
                ticket.put("event", event.get("name"));
                ticket.put("eventId", eventId);

                ticketsToCreate.add(ticket);

            }
        }

        for(PTicket ticket : ticketsToCreate) {
            try{
                ticketManager.saveTicketFromClientRequest("ticket", ticket);
            } catch (Exception e) {
                //TODO: Cleanup tickets that we created
                //TODO: Once the managers and exceptios get refactored out of
                //web-resources, throw a better exception.
                //Finally, an exception here is something that we can't recover from, so it's okay to throw
                //our hands up in the air and cry with a HTTP 500
                throw new RuntimeException(e);
            }
        }

        //mark performance as "tickets_created"
    }

    public AthenaComponent getAthenaStage() {
        return athenaStage;
    }

    public void setAthenaStage(AthenaComponent athenaStage) {
        this.athenaStage = athenaStage;
    }

    public RecordManager getTicketManager() {
        return ticketManager;
    }

    public void setTicketManager(RecordManager ticketManager) {
        this.ticketManager = ticketManager;
    }



    
}
