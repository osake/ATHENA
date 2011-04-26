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
import javax.ws.rs.core.MultivaluedMap;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.TicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.codes.model.Code;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.web.exception.AthenaConflictException;
import org.fracturedatlas.athena.web.exception.AthenaException;
import org.fracturedatlas.athena.web.exception.ObjectNotFoundException;
import org.fracturedatlas.athena.web.manager.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CodeManager {

    @Autowired
    RecordManager recordManager;

    @Autowired
    PropFieldManager fieldManager;

    @Autowired
    AthenaComponent athenaStage;

    @Autowired
    ApaAdapter apa;

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public static final String CODE = "code";
    public static final String CODED_TYPE = "ticket";

    public Code getCode(Object id, Boolean loadTicketsIdsToo) {
        logger.debug("Geting code with id [{}]", id);
        PTicket t = recordManager.getTicket(CODE, id);
        if(t == null) {
            return null;
        } else {
            logger.debug("Found record:");
            logger.debug("{}", t.toString());
            Code code = new Code(t);
            
            if(loadTicketsIdsToo) {
                logger.debug("Loading tickets");
                Set<PTicket> ticketsOnCode = loadTicketsForCode(code);
                for(PTicket ticket : ticketsOnCode) {
                    code.getTickets().add(ticket.getIdAsString());
                }
            }
            return code;
        }
    }
    
    public Code getCode(Object id) {
        return getCode(id, true);
    }

    public Set<PTicket> findTickets(String codeId, MultivaluedMap<String, String> queryParams) throws ObjectNotFoundException {
        Code code = getCode(codeId, false);
        if(code == null) {
            throw new ObjectNotFoundException("Code with id ["+ codeId +"] was not found");
        }
        queryParams.add(code.getCodeAsFieldName(), Operator.MATCHES.getOperatorType() + ".*");
        Set<PTicket> tickets = recordManager.findTickets(CODED_TYPE, queryParams);
        return tickets;
    }

    /**
     * This will find tickets in the data store that are linked to this ticket and return their ids
     * @param code
     * @return
     */
    private Set<PTicket> loadTicketsForCode(Code code) {
        AthenaSearch search = new AthenaSearch.Builder()
                                              .type(CODED_TYPE)
                                              .and(code.getCodeAsFieldName(), Operator.MATCHES, AthenaSearch.ANY_VALUE)
                                              .build();
        Set<PTicket> ticketsOnCode = apa.findTickets(search);
        logger.debug("Found [{}] tickets with code [{}]", ticketsOnCode.size(), code.getCode());
        return ticketsOnCode;
    }

    /**
     * This will get the ticket ids from code.getTickets() and load them fromt he datastore
     * @param code
     * @return
     */
    private Set<PTicket> getTicketsOnCode(Code code) {
        Set<PTicket> tickets = new HashSet<PTicket>();

        //Load the ticketIds from this code
        if(code.getTickets() != null) {
            for(String ticketId : code.getTickets()) {
                logger.debug("Looking up ticket with id [{}]", ticketId);
                PTicket t = recordManager.getTicket(CODED_TYPE, ticketId);
                if( t != null ) {
                    logger.debug("Found ticket for this code:");
                    logger.debug("{}", t.toString());
                    tickets.add(t);
                }
            }
        }

        return tickets;
    }

    public void deleteCode(Object id) {
        Code code = getCode(id);

        Set<PTicket> ticketsOnCode = loadTicketsForCode(code);
        for(PTicket ticket : ticketsOnCode) {
            deleteCodeFromTicket(code, ticket.getId());
        }
        apa.deleteRecord(CODE, id);
    }

    public void deleteCodeFromTicket(Code code, Object ticketId) {
        logger.debug("Deleting code [{}] from this ticket [{}]", code.getCode(), ticketId);
        TicketProp prop = apa.getTicketProp(code.getCodeAsFieldName(), CODE, ticketId);
        if(prop != null) {
            logger.debug("Deleting prop [{}]", prop.getId());
            apa.deleteTicketProp(prop);
        } else {
            logger.info("Code [{}] was supposed to be on ticket [{}], but no prop was found to delete", code.getCode(), ticketId);
        }
    }

    public void deleteCodeFromTicket(Object codeId, Object ticketId) {
        Code code = getCode(IdAdapter.toString(codeId));
        deleteCodeFromTicket(code, ticketId);
    }

    /**
     * Create a code
     *
     * tickets, performances, or events may be empty or null.
     *
     * This method does not fail if a performance or event is not found.  Processing will continue with other valid performance, events, or tickets.
     *
     * code.code cannot be updated.  If an existing code is sent with a different code.code, this method will throw an AthenaException
     *
     * @param code
     * @return the created code with an id sassigned
     */
    public Code saveCode(Code code) {
        verifyCode(code);
        PTicket codeRecord = code.toRecord();

        if(code.getId() == null) {
            checkIfThisCodeExists(code);
            codeRecord = recordManager.createRecord(CODE, codeRecord);
        } else {
            checkForCodeImmutability(code);
            codeRecord = recordManager.updateRecord(CODE, codeRecord);
        }
        
        Set<PTicket> ticketsForThisCode = new HashSet<PTicket>();
        if(code.getPerformances() == null) {
            code.setPerformances(new HashSet<String>());
        }

        code.getPerformances().addAll(getPerformanceIdsForEvents(code));
        ticketsForThisCode.addAll(getTicketsForPerformances(code));
        ticketsForThisCode.addAll(getTicketsOnCode(code));

        logger.debug("Adding code to tickets: {}", ticketsForThisCode);
        processTickets(ticketsForThisCode, code);

        Code savedCode = new Code(codeRecord);
        savedCode.setTickets(getIds(ticketsForThisCode));
        return savedCode;
    }

    private void checkForCodeImmutability(Code code) {
        PTicket codeRecord = recordManager.getTicket(CODE, code.getId());
        if(codeRecord == null) {
            throw new AthenaException("Trying to update code [" + code.getId() + "] but this code was not found");
        } else {
            if(code.getCode() == null || !code.getCode().equals(codeRecord.get("code"))) {
                throw new AthenaException("Cannot change code on [" + code.getId() + "].  Tried to change ["+codeRecord.get("code")+"] to ["+code.getCode()+"]");
            }
        }
    }

    private void checkIfThisCodeExists(Code code) {
        AthenaSearch search = new AthenaSearch.Builder()
                                              .type(CODE)
                                              .and("code", Operator.EQUALS, code.getCode())
                                              .build();
        Set<PTicket> results = apa.findTickets(search);
        if(results.size() > 0) {
            throw new AthenaConflictException("Code ["+code.getCode()+"] already exists");
        }
    }

    private Set<String> getIds(Collection<PTicket> records) {
        Set<String> ids = new HashSet<String>();

        for(PTicket r : records) {
            ids.add(r.getIdAsString());
        }

        return ids;
    }

    private void addFieldForThisCodeIfItDoesntExist(Code code) {
        PropField pf = apa.getPropField(code.getCodeAsFieldName());
        if(pf == null) {
            pf = new PropField(ValueType.INTEGER, code.getCodeAsFieldName(), Boolean.FALSE);
            apa.savePropField(pf);
        }
    }

    private Set<PTicket> processTickets(Set<PTicket> tickets, Code code) {
        addFieldForThisCodeIfItDoesntExist(code);
        for(PTicket ticket : tickets) {
            ticket.put(code.getCodeAsFieldName(), Integer.toString(code.getPrice()));
            logger.debug("Updating: " + ticket);
            recordManager.updateRecord(CODED_TYPE, ticket);
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

    public PropFieldManager getFieldManager() {
        return fieldManager;
    }

    public void setFieldManager(PropFieldManager fieldManager) {
        this.fieldManager = fieldManager;
    }
}
