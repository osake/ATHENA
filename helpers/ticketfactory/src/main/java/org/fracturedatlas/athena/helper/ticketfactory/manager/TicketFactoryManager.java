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
import java.util.List;
import java.util.Map;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.exception.AthenaException;
import org.fracturedatlas.athena.model.Ticket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.web.exception.ObjectNotFoundException;
import org.fracturedatlas.athena.web.manager.AbstractAthenaSubResource;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("createticketSubResource")
public class TicketFactoryManager extends AbstractAthenaSubResource {

    @Autowired
    private AthenaComponent athenaStage;

    @Autowired
    private RecordManager ticketManager;
    public static String INITIAL_STATE = "off_sale";

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /*
     * sectionBody must be a valid section AND it must have performanceId included in
     * it's entity body
     */
    @Override
    public List<PTicket> save(String parentType, 
                                 Object parentId,
                                 String subResourceType,
                                 Map<String, List<String>> queryParams,
                                 PTicket sectionBody,
                                 String username) throws ObjectNotFoundException {
        List<Ticket> createdTickets = new ArrayList<Ticket>();
        
        if("section".equals(parentType)) {
            PTicket section = athenaStage.get("section", parentId);
            if(section == null) {
                throw new ObjectNotFoundException("No section found with id ["+parentId+"]");
            }

            if(!parentId.equals(sectionBody.getIdAsString())) {
                throw new AthenaException("Requested to create tickets for section ["+parentId+"] but sent section with id ["+sectionBody.getIdAsString()+"]");
            }
            
            if(sectionBody.get("performanceId") == null) {
                throw new AthenaException("Must include performanceId in request body");
            }

            createdTickets = createTicketsForSection(sectionBody);
        } else if ("performance".equals(parentType)) {
            PTicket performance = athenaStage.get("performance", parentId);
            createdTickets = createTickets(performance);
        } else {
            throw new ObjectNotFoundException("Cannot create tickets for [" + parentType + "]");
        }
        
        List<PTicket> pTickets = Ticket.toCollection(createdTickets);
        
        return pTickets;
    }
    
    public List<Ticket> createTicketsForSection(PTicket section) {
        PTicket chart = athenaStage.get("chart", section.get("chartId"));
        PTicket performance = athenaStage.get("performance", section.get("performanceId"));
        PTicket event = athenaStage.get("event", performance.get("eventId"));            
        
        ArrayList<Ticket> ticketsToCreate = new ArrayList<Ticket>();
        Integer capacity = Integer.parseInt(section.get("capacity"));
        logger.debug("Capacity is [{}], creating tickets", capacity);
        for(int seatNum = 0; seatNum < capacity; seatNum++) {
            Ticket ticket = new Ticket(section, performance, event, INITIAL_STATE);
            ticketsToCreate.add(ticket);
        }

        return saveTickets(ticketsToCreate);
    }

    public List<Ticket> createTickets(PTicket pTicket) {
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
        logger.debug("Finding sections for chart [{}]", chartId);
        Collection<PTicket> sections = athenaStage.find("section", athenaSearch);
        logger.debug("Found [{}] sections", sections.size());

        ArrayList<Ticket> ticketsToCreate = new ArrayList<Ticket>();

        //for each section
        for(PTicket section : sections) {
            Integer capacity = Integer.parseInt(section.get("capacity"));
            logger.debug("capacity of section [{}] is [{}]", section.getId(), capacity);
            for(int seatNum = 0; seatNum < capacity; seatNum++) {
                Ticket ticket = new Ticket(section, performance, event, INITIAL_STATE);
                ticketsToCreate.add(ticket);

            }
        }

        return saveTickets(ticketsToCreate);
    }
    
    private List<Ticket> saveTickets(List<Ticket> ticketsToCreate) {
        List<PTicket> createdPTickets = ticketManager.createRecords("ticket", Ticket.toCollection(ticketsToCreate));
        return Ticket.fromCollection(createdPTickets);
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
