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
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.web.exception.AthenaException;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketFactoryManager {

    @Autowired
    private AthenaComponent athenaStage;

    @Autowired
    private RecordManager ticketManager;
    private static String INITIAL_STATE = "off_sale";

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public void createTickets(PTicket pTicket) {
        String performanceId = (String)pTicket.getId();
        PTicket performance = athenaStage.get("performance", performanceId);

        //If the performace isn't found, throw a bad request
        if(performance == null) {
            throw new AthenaException("Performance with id [" + performanceId + "] was not found");
        }

        String chartId = performance.get("chartId");
        String eventId = performance.get("eventId");
        PTicket chart = athenaStage.get("chart", chartId);
        PTicket event = athenaStage.get("event", eventId);

        AthenaSearchConstraint sectionSearch = new AthenaSearchConstraint("chartId", Operator.EQUALS, chartId);
        AthenaSearch athenaSearch = new AthenaSearch.Builder(sectionSearch).build();
        Collection<PTicket> sections = athenaStage.find("section", athenaSearch);

        ArrayList<PTicket> ticketsToCreate = new ArrayList<PTicket>();

        logger.info("[{}] sections", sections.size());

        //for each section
        for(PTicket section : sections) {
            Integer capacity = Integer.parseInt(section.get("capacity"));
            logger.info("capacity of section [{}] is [{}]", section.getId(), capacity);
            for(int seatNum = 0; seatNum < capacity; seatNum++) {
                PTicket ticket = new PTicket();
                ticket.put("price", section.get("price"));
                ticket.put("performanceId", performanceId);
                ticket.put("performance", performance.get("datetime"));
                ticket.put("section", section.get("name"));
                ticket.put("venue", event.get("venue"));
                ticket.put("event", event.get("name"));
                ticket.put("eventId", eventId);
                ticket.put("state", INITIAL_STATE);

                logger.info("Creating ticket, save below: ");
                logger.info(ticket.toString());
                ticketsToCreate.add(ticket);

            }
        }

        logger.info("[{}] tickets to create", ticketsToCreate.size());

        for(PTicket ticket : ticketsToCreate) {
            try{
                logger.info("Saving ticket: ");
                logger.info(ticket.toString());
                ticketManager.saveTicketFromClientRequest("ticket", ticket);
            } catch (Exception e) {
                //TODO: Cleanup tickets that we created
                //Finally, an exception here is something that we can't recover from, so it's okay to throw
                //our hands up in the air and cry with a HTTP 500
                throw new RuntimeException(e);
            }
        }
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
