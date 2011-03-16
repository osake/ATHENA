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

package org.fracturedatlas.athena.helper.lock.manager;

import com.sun.jersey.api.NotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.apa.model.BooleanTicketProp;
import org.fracturedatlas.athena.apa.model.DateTimeTicketProp;
import org.fracturedatlas.athena.apa.model.IntegerTicketProp;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.TicketProp;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.lock.exception.TicketsLockedException;
import org.fracturedatlas.athena.helper.lock.model.AthenaLock;
import org.fracturedatlas.athena.helper.lock.model.AthenaLockStatus;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.fracturedatlas.athena.web.exception.AthenaException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class AthenaLockManager {

    public static final String LOCK_TYPE = "ticket";

    public static final String LOCK_ID = "lockId";
    public static final String LOCKED_BY_API_KEY = "lockedByApiKey";
    public static final String LOCKED_BY_IP = "lockedByIp";
    public static final String LOCK_EXPIRES = "lockExpires";
    public static final String LOCK_TIMES = "lockTimes";

    public static final String NO_USER = "[NONE]";

    @Autowired
    ApaAdapter apa;

    @Autowired
    SecurityContextHolderStrategy contextHolderStrategy;

    static Properties props;
    
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    static {
        props = new Properties();
        ClassPathResource cpr = new ClassPathResource("athena-lock.properties");
        try{
            InputStream in = cpr.getInputStream();
            props.load(in);
            in.close();
        } catch (Exception e) {
            Logger log2 = LoggerFactory.getLogger(AthenaLockManager.class);
            log2.error(e.getMessage(),e);
        }
    }

    public AthenaLock getLock(String id, HttpServletRequest request) throws Exception {

        AthenaSearch apaSearch = new AthenaSearch
                                  .Builder(new AthenaSearchConstraint(AthenaLockManager.LOCK_ID, Operator.EQUALS, id))
                                  .and(new AthenaSearchConstraint(AthenaLockManager.LOCKED_BY_API_KEY, Operator.EQUALS, getCurrentUsername()))
                                  .build();

        Collection<Ticket> tickets = apa.findTickets(apaSearch);

        if(tickets == null || tickets.size() == 0) {
            throw new NotFoundException("Transaction with id [" + id + "] was not found");
        }

        PTicket firstTicket = tickets.iterator().next().toClientTicket();

        Set<String> ids = getTicketIds(tickets);


        AthenaLock tran = new AthenaLock();
        tran.setId(id);
        tran.setLockedByApi(firstTicket.get(AthenaLockManager.LOCKED_BY_API_KEY));
        tran.setLockedByIp(firstTicket.get(AthenaLockManager.LOCKED_BY_IP));
        tran.setLockExpires(DateUtil.parseDate(firstTicket.get(AthenaLockManager.LOCK_EXPIRES)));
        tran.setTickets(ids);

        return tran;
    }

    public AthenaLock createLock(HttpServletRequest request, AthenaLock tran) throws Exception {
        //Load all the tickets
        Set<Ticket> tickets = new HashSet<Ticket>();
        Set<String> ticketIds = tran.getTickets();
        for(String id : ticketIds) {

            Ticket t = apa.getTicket(LOCK_TYPE, id);
            if(t == null) {
                throw new TicketsLockedException("Invalid ticket involved with transaction");
            }

            if(isInvolvedInActiveTransaction(t)) {
                throw new TicketsLockedException("Unable to obtain lock on tickets");
            }
        }

        tran.setId(UUID.randomUUID().toString());
        tran.setLockedByApi(getCurrentUsername());
        tran.setLockedByIp(request.getRemoteAddr());
        tran.setStatus(AthenaLockStatus.OK);

        DateTime lockExpires = new DateTime(new Date());
        lockExpires = lockExpires.plusMinutes(Integer.parseInt(props.getProperty("athena.lock.lock_time_in_minutes")));
        tran.setLockExpires(lockExpires.toDate());

        lockTickets(ticketIds, tran);
        return tran;
    }

    private String getCurrentUsername() {
        Authentication authentication = contextHolderStrategy.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() != null
                                  && User.class.isAssignableFrom(authentication.getPrincipal().getClass()) ) {
            User user = (User) authentication.getPrincipal();
            return user.getUsername();
        }

        return NO_USER;
    }

    private void lockTickets(Set<String> ticketIds, AthenaLock tran) throws Exception {
        PropField lockId = apa.getPropField(AthenaLockManager.LOCK_ID);
        PropField lockedByIpField = apa.getPropField(AthenaLockManager.LOCKED_BY_IP);
        PropField lockedByApiKeyField = apa.getPropField(AthenaLockManager.LOCKED_BY_API_KEY);
        PropField lockExpiresField = apa.getPropField(AthenaLockManager.LOCK_EXPIRES);
        PropField lockTimesField = apa.getPropField(AthenaLockManager.LOCK_TIMES);

        for(String id : ticketIds) {
            Ticket t = apa.getTicket(LOCK_TYPE, id);

            //TODO: This might not work
            t.setTicketProp(new StringTicketProp(lockId, tran.getId()));
            t.setTicketProp(new StringTicketProp(lockedByApiKeyField, tran.getLockedByApi()));
            t.setTicketProp(new StringTicketProp(lockedByIpField, tran.getLockedByIp()));
            t.setTicketProp(new IntegerTicketProp(lockTimesField, 1));
            t.setTicketProp(new DateTimeTicketProp(lockExpiresField, tran.getLockExpires()));
            apa.saveTicket(t);
        }
    }

    public AthenaLock updateLock(String id, HttpServletRequest request, AthenaLock tran) throws Exception {
        //Load all the tickets
        Set<String> ticketIds = new HashSet<String>();
        Set<Ticket> ticketsInTransaction = getTicketsInTransaction(tran.getId());

        //This looks a little stupid: loading the tickets from apa even though the client is sending us
        //an array of tickets.  We do this though to prevent the client from adding in extra tickets
        //beyond those that were locked with the initial lock
        for(Ticket ticket : ticketsInTransaction) {
            ticketIds.add(ticket.getId().toString());
        }

        PropField lockTimesField = apa.getPropField(AthenaLockManager.LOCK_TIMES);

        AthenaLock transactionFromTickets = null;

        for(String ticketId : ticketIds) {
            Ticket t = apa.getTicket(LOCK_TYPE, ticketId);

            if(t == null) {
                throw new TicketsLockedException("Invalid ticket involved with transaction");
            }

            TicketProp prop = t.getTicketProp(lockTimesField.getName());
            Integer numTimesLocked = (Integer)prop.getValue();
            logger.debug("", numTimesLocked);
            logger.debug(props.getProperty("athena.transaction.number_of_renewals"));
            logger.debug("", isRenewing(tran));

            if(numTimesLocked > Integer.parseInt(props.getProperty("athena.lock.number_of_renewals"))
               && isRenewing(tran)) {
                throw new AthenaException("Cannot lock tickets");
            }

            transactionFromTickets = loadTransactionFromTicket(t);

            if(!isOwnerOfTransaction(request, transactionFromTickets)) {
                throw new AthenaException("Cannot process the transaction");
            }
        }

        DateTime now = new DateTime();
        DateTime expiresOn = now.plusMinutes(Integer.parseInt(props.getProperty("athena.lock.renewal_time_in_minutes")));
        tran.setLockExpires(expiresOn.toDate());
        tran.setTickets(ticketIds);

        if(isCompleting(tran)) {
            completeTicketPurchase(tran);
        } else if(isRenewing(tran))  {
            renewLockOnTickets(tran);
        } else {
            throw new AthenaException("Did not understand status of [" + tran.getStatus() + "]");
        }

        tran.setStatus(AthenaLockStatus.OK);

        return tran;
    }

    private Boolean isRenewing(AthenaLock tran) {
        if(tran.getStatus() == null) {
            return false;
        }

        return tran.getStatus().equalsIgnoreCase(AthenaLockStatus.RENEW);
    }

    private Boolean isCompleting(AthenaLock tran) {
        if(tran.getStatus() == null) {
            return false;
        }

        return tran.getStatus().equalsIgnoreCase(AthenaLockStatus.COMPLETE);
    }

    private void completeTicketPurchase(AthenaLock tran) throws Exception {
        Set<String> ticketIds = tran.getTickets();
        for(String ticketId : ticketIds) {
            PropField statusField = apa.getPropField("status");

            if(statusField != null) {
                TicketProp statusProp = apa.getTicketProp("status", LOCK_TYPE, ticketId);

                if(statusProp == null) {
                    statusProp = new StringTicketProp(statusField, "sold");
                    statusProp.setTicket(apa.getTicket(LOCK_TYPE, ticketId));
                }
                statusProp.setValue("sold");
                apa.saveTicketProp(statusProp);
            }
        }


    }

    private void renewLockOnTickets(AthenaLock tran) throws Exception {
        Set<String> ticketIds = tran.getTickets();
        for(String ticketId : ticketIds) {
            TicketProp lockExpiresProp = apa.getTicketProp(AthenaLockManager.LOCK_EXPIRES, LOCK_TYPE, ticketId);
            lockExpiresProp.setValue(tran.getLockExpires());
            apa.saveTicketProp(lockExpiresProp);

            TicketProp lockTimesProp = apa.getTicketProp(AthenaLockManager.LOCK_TIMES, LOCK_TYPE, ticketId);
            Integer times = (Integer)lockTimesProp.getValue();
            times++;
            lockTimesProp.setValue(times);
            apa.saveTicketProp(lockTimesProp);
        }
    }

    public void deleteLock(String id, HttpServletRequest request) throws Exception {
        //get the tickets on the tran
        Set<Ticket> ticketsInTransaction = getTicketsInTransaction(id);

        logger.info("TICKETS IN THIS TRANSACTION: {}", ticketsInTransaction);

        if(ticketsInTransaction == null) {
            return;
        }

        PropField lockIdField = apa.getPropField(AthenaLockManager.LOCK_ID);
        PropField lockedByIpField = apa.getPropField(AthenaLockManager.LOCKED_BY_IP);
        PropField lockedByApiKeyField = apa.getPropField(AthenaLockManager.LOCKED_BY_API_KEY);
        PropField lockExpiresField = apa.getPropField(AthenaLockManager.LOCK_EXPIRES);
        PropField lockTimesField = apa.getPropField(AthenaLockManager.LOCK_TIMES);

        AthenaLock transactionFromTickets = loadTransactionFromTicket(ticketsInTransaction.iterator().next());

        if(!isOwnerOfTransaction(request, transactionFromTickets)) {
            throw new AthenaException("Cannot delete the transaction");
        }



        for(Ticket ticket : ticketsInTransaction) {
            TicketProp prop = apa.getTicketProp(AthenaLockManager.LOCK_ID, LOCK_TYPE, ticket.getId());
            prop.setValue(null);
            apa.saveTicketProp(prop);

            prop = apa.getTicketProp(AthenaLockManager.LOCKED_BY_IP, LOCK_TYPE, ticket.getId());
            prop.setValue(null);
            apa.saveTicketProp(prop);

            prop = apa.getTicketProp(AthenaLockManager.LOCKED_BY_API_KEY, LOCK_TYPE, ticket.getId());
            prop.setValue(null);
            apa.saveTicketProp(prop);

            prop = apa.getTicketProp(AthenaLockManager.LOCK_EXPIRES, LOCK_TYPE, ticket.getId());
            prop.setValue(null);
            apa.saveTicketProp(prop);

            prop = apa.getTicketProp(AthenaLockManager.LOCK_TIMES, LOCK_TYPE, ticket.getId());
            prop.setValue(0);
            apa.saveTicketProp(prop);
        }
    }

    private Set<Ticket> getTicketsInTransaction(String lockId) {
        AthenaSearch search = new AthenaSearch();
        search.addConstraint(AthenaLockManager.LOCK_ID, Operator.EQUALS, lockId);
        Set<Ticket> ticketsInTransaction = apa.findTickets(search);
        return ticketsInTransaction;
    }

    private AthenaLock loadTransactionFromTicket(Ticket t) {

        logger.info("LOADING FROM TICKET: {}", t);

        AthenaLock tran = new AthenaLock();
        tran.setId(t.getTicketProp(AthenaLockManager.LOCK_ID).getValueAsString());
        tran.setLockedByApi(t.getTicketProp(AthenaLockManager.LOCKED_BY_API_KEY).getValueAsString());
        tran.setLockedByIp(t.getTicketProp(AthenaLockManager.LOCKED_BY_IP).getValueAsString());
        tran.setLockExpires((Date)(t.getTicketProp(AthenaLockManager.LOCK_EXPIRES)).getValue());

        logger.info("Loaded transaction: {}", tran);


        return tran;
    }

    private Boolean isOwnerOfTransaction(HttpServletRequest request, AthenaLock tran) {
        Boolean checkApiKey = Boolean.parseBoolean(props.getProperty("athena.lock.username_check_enabled"));

        if(!checkApiKey) {

            logger.info("username check is OFF");
            return true;

        }
        String username = getCurrentUsername();
        
        logger.info("Checking API KEY");
        logger.info("Request key  [{}]", username);
        logger.info("Key on tix   [{}]", tran.getLockedByApi());

        if (username == null) {
            return false;
        } else {
            return username.equals(tran.getLockedByApi());
        }
    }

    /**
     * Will determine if the ticket has a lockId and the tran has not expired
     *
     * @param ticket the ticket
     */
    private Boolean isInvolvedInActiveTransaction(Ticket ticket) {
        TicketProp prop = ticket.getTicketProp(AthenaLockManager.LOCK_ID);

        if(prop == null) {
            return false;
        }

        String ticketTranId = prop.getValueAsString();

        if(ticketTranId != null) {
            return (new DateTime(ticket.getTicketProp(AthenaLockManager.LOCK_EXPIRES).getValue())).isAfterNow();
        }

        return false;
    }

    private Set<String> getTicketIds(Collection<Ticket> tickets) {
        Set<String> ids = new HashSet<String>();
        for(Ticket t : tickets) {
            ids.add(t.getId().toString());
        }
        return ids;
    }

    public SecurityContextHolderStrategy getContextHolderStrategy() {
        return contextHolderStrategy;
    }

    public void setContextHolderStrategy(SecurityContextHolderStrategy contextHolderStrategy) {
        this.contextHolderStrategy = contextHolderStrategy;
    }

    public ApaAdapter getApa() {
        return apa;
    }

    public void setApa(ApaAdapter apa) {
        this.apa = apa;
    }
}
