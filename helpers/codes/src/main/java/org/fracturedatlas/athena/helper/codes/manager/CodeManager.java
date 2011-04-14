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

package org.fracturedatlas.athena.helper.codes.manager;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.codes.model.Code;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeManager {

    @Autowired
    RecordManager recordManager;

    @Autowired
    AthenaComponent athenaStage;

    @Autowired
    ApaAdapter apa;

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public static final String CODE = "code";
    public static final String CODED_TYPE = "ticket";

    public Code getCode(Object id) {
        logger.debug("Geting code with id [{}]", id);
        PTicket t = recordManager.getTicket(CODE, id);
        logger.debug("Found record:");
        logger.debug("{}", t.toString());
        return new Code(t);
    }

    /**
     * Create a code
     *
     * tickets, performances, or events may be empty or null.
     *
     * This method does not fail if a performance or event is not found.  Processing will continue with other valid performance, events, or tickets.
     *
     * @param code
     * @return the created code with an id sassigned
     */
    public Code createCode(Code code) {
        verifyCode(code);
        PTicket codeRecord = code.toRecord();
        codeRecord = recordManager.createRecord(CODE, codeRecord);
        
        Set<PTicket> ticketsForThisCode = new HashSet<PTicket>();
        if(code.getPerformances() == null) {
            code.setPerformances(new HashSet<String>());
        }

        code.getPerformances().addAll(getPerformanceIdsForEvents(code));
        ticketsForThisCode.addAll(getTicketsForPerformances(code));
        ticketsForThisCode.addAll(getTicketsOnCode(code));
        processTickets(ticketsForThisCode, code);

        Code savedCode = new Code(codeRecord);
        savedCode.setTickets(getIds(ticketsForThisCode));
        return savedCode;
    }

    private Set<String> getIds(Collection<PTicket> records) {
        Set<String> ids = new HashSet<String>();

        for(PTicket r : records) {
            ids.add(r.getIdAsString());
        }

        return ids;
    }

    private Set<PTicket> processTickets(Set<PTicket> tickets, Code code) {
        for(PTicket ticket : tickets) {
            ticket.put(code.getCode(), Integer.toString(code.getPrice()));
            recordManager.updateRecord(CODED_TYPE, ticket);
        }
        return tickets;
    }

    private Set<PTicket> getTicketsOnCode(Code code) {
        Set<PTicket> tickets = new HashSet<PTicket>();

        //Load the ticketIds from this code
        if(code.getTickets() != null) {
            for(String ticketId : code.getTickets()) {
                logger.debug("Looking up ticket with id [{}]", ticketId);
                PTicket t = recordManager.getTicket(CODED_TYPE, ticketId);
                logger.debug("Found ticket for this code:");
                logger.debug("{}", t.toString());
                tickets.add(t);
            }
        }

        return tickets;
    }

    private Set<String> getPerformanceIdsForEvents(Code code) {
        Set<String> performanceIds = new HashSet<String>();
        if(code.getEvents() != null && code.getEvents().size() > 0) {
            logger.debug("Searching for performances in the following events {}" , code.getPerformances());
            AthenaSearch search = new AthenaSearch.Builder().type("performance").build();
            if(code.getEvents().size() == 1) {
                search.addConstraint("eventId", Operator.EQUALS, code.getEvents().iterator().next());
            } else {
                search.addConstraint("eventId", Operator.IN, code.getEvents());
            }
            Collection<PTicket> performances = athenaStage.find("performance", search);
            performanceIds = getIds(performances);
        }
        return performanceIds;
    }

    private Set<PTicket> getTicketsForPerformances(Code code) {
        Set<PTicket> tickets = new HashSet<PTicket>();
        if(code.getPerformances() != null && code.getPerformances().size() > 0) {
            logger.debug("Searching for tickets in the following performances {}" , code.getPerformances());
            AthenaSearch search = new AthenaSearch.Builder().type("ticket").build();
            if(code.getPerformances().size() == 1) {
                search.addConstraint("performanceId", Operator.EQUALS, code.getPerformances().iterator().next());
            } else {
                search.addConstraint("performanceId", Operator.IN, code.getPerformances());
            }
            tickets.addAll(apa.findTickets(search));
        }
        return tickets;
    }

    public void verifyCode(Code code) {

    }

    public ApaAdapter getApa() {
        return apa;
    }

    public void setApa(ApaAdapter apa) {
        this.apa = apa;
    }

    public RecordManager getRecordManager() {
        return recordManager;
    }

    public void setRecordManager(RecordManager recordManager) {
        this.recordManager = recordManager;
    }

    public AthenaComponent getAthenaStage() {
        return athenaStage;
    }

    public void setAthenaStage(AthenaComponent athenaStage) {
        this.athenaStage = athenaStage;
    }

    

}
