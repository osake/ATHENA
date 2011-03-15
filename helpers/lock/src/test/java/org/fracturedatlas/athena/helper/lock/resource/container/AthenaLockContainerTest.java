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

package org.fracturedatlas.athena.helper.lock.resource.container;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fracturedatlas.athena.apa.model.BooleanTicketProp;
import org.fracturedatlas.athena.apa.model.DateTimeTicketProp;
import org.fracturedatlas.athena.apa.model.IntegerTicketProp;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.StrictType;
import org.fracturedatlas.athena.apa.model.StringTicketProp;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.lock.manager.AthenaLockManager;
import org.fracturedatlas.athena.helper.lock.model.AthenaLock;
import org.fracturedatlas.athena.helper.lock.model.AthenaLockStatus;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class AthenaLockContainerTest extends BaseContainerTest {
    Gson gson = JsonUtil.getGson();
    String path = "/";

    Ticket t1 = null;
    Ticket t2 = null;
    Ticket t3 = null;
    Ticket t4 = null;
    Ticket t5 = null;

    PropField seatNumberProp = null;
    PropField lockIdProp = null;
    PropField soldProp = null;
    PropField lockedByApiProp = null;
    PropField lockedByIpProp = null;
    PropField lockExpiresProp = null;
    PropField lockTimesProp = null;
    PropField someOtherFieldProp = null;

    @Mock private SecurityContextHolderStrategy mockSecurityContextHolderStrategy;
    @Mock private SecurityContext mockSecurityContext;
    @Mock private User mockUser;
    @Mock private Authentication mockAuthentication;

    public static final String API_KEY = "SAMPLEAPIKEYfowihe9338833wehhfhf";

    public AthenaLockContainerTest() throws Exception {
        super();
    }

    @After
    public void teardownTickets() {
        super.teardownTickets();
    }

    @Test
    public void testGetTransaction() throws Exception {

        String testTransactionId = "TEST_TRANSACTION_ID";
        String testUsername = AthenaLockManager.NO_USER;
        String testIp = "192.168.0.1";
        Date originalLockExpires = DateUtil.parseDate("2010-05-05T05:05:33-04:00");

        /* SETUP THE TRANSACTION IN THE DB */
        List<String> ticketIdsInTransaction = new ArrayList<String>();
        int i=0;
        for(Ticket t : ticketsToDelete) {

            //put three tickets in this transaction, one ticket in another transaction, and another ticket just hanging out
            if(i<3) {
                t.addTicketProp(new StringTicketProp(lockIdProp, testTransactionId));
                t.addTicketProp(new BooleanTicketProp(soldProp, Boolean.FALSE));
                t.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-05-05T05:05:33-04:00")));
                t.addTicketProp(new StringTicketProp(lockedByApiProp, testUsername));
                t.addTicketProp(new StringTicketProp(lockedByIpProp, testIp));
                t = apa.saveTicket(t);
                ticketIdsInTransaction.add(t.getId().toString());
            } else if (i<4) {
                t.addTicketProp(new StringTicketProp(lockIdProp, "FAKE_TRAN_ID"));
                t.addTicketProp(new BooleanTicketProp(soldProp, Boolean.FALSE));
                t.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-05-05T05:05:33-04:00")));
                t.addTicketProp(new StringTicketProp(lockedByApiProp, testUsername));
                t.addTicketProp(new StringTicketProp(lockedByIpProp, testIp));
                t = apa.saveTicket(t);
            } else {
                t.addTicketProp(new StringTicketProp(lockIdProp, null));
                t.addTicketProp(new BooleanTicketProp(soldProp, Boolean.FALSE));
                t.addTicketProp(new DateTimeTicketProp(lockExpiresProp, null));
                t.addTicketProp(new StringTicketProp(lockedByApiProp, null));
                t.addTicketProp(new StringTicketProp(lockedByIpProp, null));
                t = apa.saveTicket(t);
            }
            i++;
        }

        /* Get the transaction */
        path = TRANSACTIONS_PATH + "/" + testTransactionId;
        AthenaLock tran = gson.fromJson(tix.path(path).type("application/json").get(String.class), AthenaLock.class);
        for(String id : tran.getTickets()) {
            assertTrue(ticketIdsInTransaction.contains(id));
        }
        assertEquals(ticketIdsInTransaction.size(), tran.getTickets().size());
        assertEquals(testTransactionId, tran.getId());
        assertEquals(testUsername, tran.getLockedByApi());
        assertEquals(testIp, tran.getLockedByIp());
        assertEquals(originalLockExpires, tran.getLockExpires());
    }

    @Test
    public void testGetTransactionNotFound() throws Exception {

        String testTransactionId = "TEST_TRANSACTION_ID";
        String testUsername = AthenaLockManager.NO_USER;
        String testIp = "192.168.0.1";
        Date originalLockExpires = DateUtil.parseDate("2010-05-05T05:05:33-04:00");

        /* SETUP THE TRANSACTION IN THE DB */
        List<String> ticketIdsInTransaction = new ArrayList<String>();
        int i=0;
        for(Ticket t : ticketsToDelete) {

            //put three tickets in this transaction, one ticket in another transaction, and another ticket just hanging out
            if(i<3) {
                t.addTicketProp(new StringTicketProp(lockIdProp, testTransactionId));
                t.addTicketProp(new BooleanTicketProp(soldProp, Boolean.FALSE));
                t.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-05-05T05:05:33-04:00")));
                t.addTicketProp(new StringTicketProp(lockedByApiProp, testUsername));
                t.addTicketProp(new StringTicketProp(lockedByIpProp, testIp));
                t = apa.saveTicket(t);
                ticketIdsInTransaction.add(t.getId().toString());
            } else if (i<4) {
                t.addTicketProp(new StringTicketProp(lockIdProp, "FAKE_TRAN_ID"));
                t.addTicketProp(new BooleanTicketProp(soldProp, Boolean.FALSE));
                t.addTicketProp(new DateTimeTicketProp(lockExpiresProp, DateUtil.parseDate("2010-05-05T05:05:33-04:00")));
                t.addTicketProp(new StringTicketProp(lockedByApiProp, testUsername));
                t.addTicketProp(new StringTicketProp(lockedByIpProp, testIp));
                t = apa.saveTicket(t);
            } else {
                t.addTicketProp(new StringTicketProp(lockIdProp, null));
                t.addTicketProp(new BooleanTicketProp(soldProp, Boolean.FALSE));
                t.addTicketProp(new DateTimeTicketProp(lockExpiresProp, null));
                t.addTicketProp(new StringTicketProp(lockedByApiProp, null));
                t.addTicketProp(new StringTicketProp(lockedByIpProp, null));
                t = apa.saveTicket(t);
            }
                i++;
        }

        /* Get the transaction */
        path = TRANSACTIONS_PATH + "/" + "FAKE_ID";
        ClientResponse response = tix.path(path).header("X-ATHENA-Key", API_KEY).type("application/json").get(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
    }

    @Test
    public void createNewTransaction() throws Exception {

        Set<String> ticketIdsInTransaction = new HashSet<String>();
        ticketIdsInTransaction.add(t2.getId().toString());
        ticketIdsInTransaction.add(t3.getId().toString());

        AthenaLock tran = new AthenaLock();
        tran.setTickets(ticketIdsInTransaction);

        AthenaLock savedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH)
                                                       .type("application/json")
                                                       .header("X-ATHENA-Key", API_KEY)
                                                       .post(String.class, gson.toJson(tran)),
                                                       AthenaLock.class);
        assertNotNull(savedTran);
        assertNotNull(savedTran.getId());
        assertEquals(savedTran.getLockedByApi(), API_KEY);
        assertEquals(savedTran.getLockedByIp(), "127.0.0.1");
        assertEquals(savedTran.getStatus(), AthenaLockStatus.OK);
        DateTime originalLockExpires = new DateTime(savedTran.getLockExpires().getTime());

        //TODO: for now, just assure that the lock expires sometime in the future
        assertTrue(originalLockExpires.isAfterNow());

        //check the three tickets involved in this transaction make sure that
        //they aren't marked
        PTicket savedT1 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t1.getId()).toClientTicket();
        assertFalse(savedTran.getId().equals(savedT1.get(AthenaLockManager.LOCK_ID)));
        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_IP));
        assertTicketsEqual(t1, savedT1);

        PTicket savedT4 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t4.getId()).toClientTicket();
        assertFalse(savedTran.getId().equals(savedT4.get(AthenaLockManager.LOCK_ID)));
        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_IP));
        assertTicketsEqual(t4, savedT4);

        PTicket savedT5 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t5.getId()).toClientTicket();
        assertFalse(savedTran.getId().equals(savedT5.get(AthenaLockManager.LOCK_ID)));
        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_IP));
        assertTicketsEqual(t5, savedT5);

        //now check the two correct tickets
        PTicket savedT2 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t2.getId()).toClientTicket();
        assertEquals(savedTran.getId(), savedT2.get(AthenaLockManager.LOCK_ID));
        assertEquals(savedTran.getLockedByApi(), savedT2.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(savedTran.getLockedByIp(), savedT2.get(AthenaLockManager.LOCKED_BY_IP));
        assertEquals(DateUtil.formatDate(savedTran.getLockExpires()), savedT2.get(AthenaLockManager.LOCK_EXPIRES));

        PTicket savedT3 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t3.getId()).toClientTicket();
        assertEquals(savedTran.getId(), savedT3.get(AthenaLockManager.LOCK_ID));
        assertEquals(savedTran.getLockedByApi(), savedT3.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(savedTran.getLockedByIp(), savedT3.get(AthenaLockManager.LOCKED_BY_IP));
        assertEquals(DateUtil.formatDate(savedTran.getLockExpires()), savedT3.get(AthenaLockManager.LOCK_EXPIRES));
    }
//
//    @Test
//    public void createNewTransactionThenLockAgain() throws Exception {
//
//        Set<String> ticketIdsInTransaction = new HashSet<String>();
//        ticketIdsInTransaction.add(t2.getId().toString());
//        ticketIdsInTransaction.add(t3.getId().toString());
//
//        AthenaLock tran = new AthenaLock();
//        tran.setTickets(ticketIdsInTransaction);
//
//        AthenaLock savedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH)
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .post(String.class, gson.toJson(tran)),
//                                                       AthenaLock.class);
//        assertNotNull(savedTran);
//        assertNotNull(savedTran.getId());
//        assertEquals(savedTran.getLockedByApi(), API_KEY);
//        assertEquals(savedTran.getLockedByIp(), "127.0.0.1");
//        assertEquals(savedTran.getStatus(), AthenaLockStatus.OK);
//        DateTime originalLockExpires = new DateTime(savedTran.getLockExpires().getTime());
//
//        //TODO: for now, just assure that the lock expires sometime in the future
//        assertTrue(originalLockExpires.isAfterNow());
//
//        //check the three tickets involved in this transaction make sure that
//        //they aren't marked
//        PTicket savedT1 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t1.getId()).toClientTicket();
//        assertFalse(savedTran.getId().equals(savedT1.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t1, savedT1);
//
//        PTicket savedT4 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t4.getId()).toClientTicket();
//        assertFalse(savedTran.getId().equals(savedT4.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t4, savedT4);
//
//        PTicket savedT5 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t5.getId()).toClientTicket();
//        assertFalse(savedTran.getId().equals(savedT5.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t5, savedT5);
//
//        //now check the two correct tickets
//        PTicket savedT2 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t2.getId()).toClientTicket();
//        assertEquals(savedTran.getId(), savedT2.get(AthenaLockManager.LOCK_ID));
//        assertEquals(null, savedT2.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(savedTran.getLockedByIp(), savedT2.get(AthenaLockManager.LOCKED_BY_IP));
//        assertEquals(DateUtil.formatDate(savedTran.getLockExpires()), savedT2.get(AthenaLockManager.LOCK_EXPIRES));
//
//        PTicket savedT3 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t3.getId()).toClientTicket();
//        assertEquals(savedTran.getId(), savedT3.get(AthenaLockManager.LOCK_ID));
//        assertEquals(null, savedT3.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(savedTran.getLockedByIp(), savedT3.get(AthenaLockManager.LOCKED_BY_IP));
//        assertEquals(DateUtil.formatDate(savedTran.getLockExpires()), savedT3.get(AthenaLockManager.LOCK_EXPIRES));
//
//
//        //now try to lock them again
//        ClientResponse response = tix.path(TRANSACTIONS_PATH)
//                                     .type("application/json")
//                                     .header("X-ATHENA-Key", API_KEY)
//                                     .post(ClientResponse.class, gson.toJson(tran));
//        assertEquals(ClientResponse.Status.CONFLICT, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//    }
//
//    @Test
//    public void createNewTransactionTicketsAlreadyLocked() throws Exception {
//
//        //lock ticket2
//        PropField transactionIdField = apa.getPropField(AthenaLockManager.LOCK_ID);
//        PropField lockExpiresField = apa.getPropField(AthenaLockManager.LOCK_EXPIRES);
//        t2.getTicketProps().add(new StringTicketProp(transactionIdField, "30303030033"));
//        DateTime expires = new DateTime().plusMinutes(2);
//        t2.getTicketProps().add(new DateTimeTicketProp(lockExpiresField, expires.toDate()));
//        t2 = apa.saveTicket(t2);
//
//        //Now try to start the transaction with t2 already locked
//        Set<String> ticketIdsInTransaction = new HashSet<String>();
//        ticketIdsInTransaction.add(t2.getId().toString());
//        ticketIdsInTransaction.add(t3.getId().toString());
//
//        AthenaLock tran = new AthenaLock();
//        tran.setTickets(ticketIdsInTransaction);
//
//        ClientResponse response = tix.path(TRANSACTIONS_PATH)
//                                     .type("application/json")
//                                     .header("X-ATHENA-Key", API_KEY)
//                                     .post(ClientResponse.class, gson.toJson(tran));
//        assertEquals(ClientResponse.Status.CONFLICT, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        //check the three tickets involved in this transaction make sure that
//        //they aren't marked
//        PTicket savedT1 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t1.getId()).toClientTicket();
//        assertFalse("30303030033".equals(savedT1.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t1, savedT1);
//
//        PTicket savedT3 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t3.getId()).toClientTicket();
//        assertFalse("30303030033".equals(savedT3.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT3.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT3.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t3, savedT3);
//
//        PTicket savedT4 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t4.getId()).toClientTicket();
//        assertFalse("30303030033".equals(savedT4.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t4, savedT4);
//
//        PTicket savedT5 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t5.getId()).toClientTicket();
//        assertFalse("30303030033".equals(savedT5.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t5, savedT5);
//
//        //check to make sure t2 doesn't have a new lock on it
//        PTicket savedT2 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t2.getId()).toClientTicket();
//        assertEquals("30303030033", savedT2.get(AthenaLockManager.LOCK_ID));
//
//
//        //TODO
//        //assertEquals(expires.toDate(), DateUtil.parseDate(savedT2.get(AthenaLockManager.LOCK_EXPIRES)));
//    }
//
//    @Test
//    public void createNewTransactionWithUnknownTickets() throws Exception {
//        Set<String> ticketIdsInTransaction = new HashSet<String>();
//        ticketIdsInTransaction.add(t2.getId().toString());
//        ticketIdsInTransaction.add(t3.getId().toString());
//        ticketIdsInTransaction.add("0");
//
//        AthenaLock tran = new AthenaLock();
//        tran.setTickets(ticketIdsInTransaction);
//
//        ClientResponse response = tix.path(TRANSACTIONS_PATH)
//                                     .type("application/json")
//                                     .header("X-ATHENA-Key", API_KEY)
//                                     .post(ClientResponse.class, gson.toJson(tran));
//        assertEquals(ClientResponse.Status.CONFLICT, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        //check the three tickets involved in this transaction make sure that
//        //they aren't marked
//        PTicket savedT1 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t1.getId()).toClientTicket();
//        assertFalse("30303030033".equals(savedT1.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t1, savedT1);
//
//        PTicket savedT2 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t2.getId()).toClientTicket();
//        assertNull(savedT2.get(AthenaLockManager.LOCK_ID));
//        assertEquals(null, savedT2.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT2.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t2, savedT2);
//
//        PTicket savedT3 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t3.getId()).toClientTicket();
//        assertFalse("30303030033".equals(savedT3.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT3.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT3.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t3, savedT3);
//
//        PTicket savedT4 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t4.getId()).toClientTicket();
//        assertFalse("30303030033".equals(savedT4.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t4, savedT4);
//
//        PTicket savedT5 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t5.getId()).toClientTicket();
//        assertNull(savedT5.get(AthenaLockManager.LOCK_ID));
//        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t5, savedT5);
//    }
//
//    @Test
//    public void renewLockWithPut() throws Exception {
//
//        //lock ticket2
//        PropField transactionIdField = apa.getPropField(AthenaLockManager.LOCK_ID);
//        PropField lockExpiresField = apa.getPropField(AthenaLockManager.LOCK_EXPIRES);
//        t2.getTicketProps().add(new StringTicketProp(transactionIdField, "30303030033"));
//        DateTime expires = new DateTime().minusMinutes(1);
//        t2.getTicketProps().add(new DateTimeTicketProp(lockExpiresField, expires.toDate()));
//        t2 = apa.saveTicket(t2);
//
//        //Now try to start the transaction with t2 already locked
//        Set<String> ticketIdsInTransaction = new HashSet<String>();
//        ticketIdsInTransaction.add(t2.getId().toString());
//        ticketIdsInTransaction.add(t3.getId().toString());
//
//        AthenaLock tran = new AthenaLock();
//        tran.setTickets(ticketIdsInTransaction);
//
//        AthenaLock savedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH)
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .post(String.class, gson.toJson(tran)),
//                                                       AthenaLock.class);
//        assertNotNull(savedTran);
//        assertNotNull(savedTran.getId());
//        assertEquals(savedTran.getLockedByApi(), API_KEY);
//        assertEquals(savedTran.getLockedByIp(), "127.0.0.1");
//        assertEquals(savedTran.getStatus(), AthenaLockStatus.OK);
//        DateTime originalLockExpires = new DateTime(savedTran.getLockExpires().getTime());
//
//        //TODO: for now, just assure that the lock expires sometime in the future
//        assertTrue(originalLockExpires.isAfterNow());
//
//        //check the three tickets involved in this transaction make sure that
//        //they aren't marked
//        PTicket savedT1 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t1.getId()).toClientTicket();
//        assertFalse(savedTran.getId().equals(savedT1.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t1, savedT1);
//
//        PTicket savedT4 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t4.getId()).toClientTicket();
//        assertFalse(savedTran.getId().equals(savedT4.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t4, savedT4);
//
//        PTicket savedT5 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t5.getId()).toClientTicket();
//        assertFalse(savedTran.getId().equals(savedT5.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t5, savedT5);
//
//        //now check the two correct tickets
//        PTicket savedT2 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t2.getId()).toClientTicket();
//        assertEquals(savedTran.getId(), savedT2.get(AthenaLockManager.LOCK_ID));
//        assertEquals(savedTran.getLockedByApi(), savedT2.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(savedTran.getLockedByIp(), savedT2.get(AthenaLockManager.LOCKED_BY_IP));
//        assertEquals(DateUtil.formatDate(savedTran.getLockExpires()), savedT2.get(AthenaLockManager.LOCK_EXPIRES));
//
//        PTicket savedT3 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t3.getId()).toClientTicket();
//        assertEquals(savedTran.getId(), savedT3.get(AthenaLockManager.LOCK_ID));
//        assertEquals(savedTran.getLockedByApi(), savedT3.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(savedTran.getLockedByIp(), savedT3.get(AthenaLockManager.LOCKED_BY_IP));
//        assertEquals(DateUtil.formatDate(savedTran.getLockExpires()), savedT3.get(AthenaLockManager.LOCK_EXPIRES));
//    }
//
//
//    @Test
//    public void updateTransactionWithPutBadPut() throws Exception {
//
//        Set<String> ticketIdsInTransaction = new HashSet<String>();
//        ticketIdsInTransaction.add(t2.getId().toString());
//        ticketIdsInTransaction.add(t3.getId().toString());
//
//        AthenaLock tran = new AthenaLock();
//        tran.setTickets(ticketIdsInTransaction);
//
//        AthenaLock savedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH)
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .post(String.class, gson.toJson(tran)),
//                                                       AthenaLock.class);
//        assertNotNull(savedTran);
//        assertNotNull(savedTran.getId());
//        assertEquals(savedTran.getLockedByApi(), API_KEY);
//        assertEquals(savedTran.getLockedByIp(), "127.0.0.1");
//        assertEquals(savedTran.getStatus(), AthenaLockStatus.OK);
//        DateTime originalLockExpires = new DateTime(savedTran.getLockExpires().getTime());
//
//        //TODO: for now, just assure that the lock expires sometime in the future
//        assertTrue(originalLockExpires.isAfterNow());
//
//        ClientResponse response = tix.path(TRANSACTIONS_PATH + "/" + savedTran.getId())
//                                     .type("application/json")
//                                     .header("X-ATHENA-Key", API_KEY)
//                                     .put(ClientResponse.class, gson.toJson(tran));
//        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//
//    @Test
//    public void updateTransactionWithPut() throws Exception {
//
//        Set<String> ticketIdsInTransaction = new HashSet<String>();
//        ticketIdsInTransaction.add(t2.getId().toString());
//        ticketIdsInTransaction.add(t3.getId().toString());
//
//        AthenaLock tran = new AthenaLock();
//        tran.setTickets(ticketIdsInTransaction);
//
//        AthenaLock savedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH)
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .post(String.class, gson.toJson(tran)),
//                                                       AthenaLock.class);
//        assertNotNull(savedTran);
//        assertNotNull(savedTran.getId());
//        assertEquals(savedTran.getLockedByApi(), API_KEY);
//        assertEquals(savedTran.getLockedByIp(), "127.0.0.1");
//        assertEquals(savedTran.getStatus(), AthenaLockStatus.OK);
//        DateTime originalLockExpires = new DateTime(savedTran.getLockExpires().getTime());
//
//        //TODO: for now, just assure that the lock expires sometime in the future
//        assertTrue(originalLockExpires.isAfterNow());
//
//        savedTran.setStatus(AthenaLockStatus.RENEW);
//
//        AthenaLock updatedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH + "/" + savedTran.getId())
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .put(String.class, gson.toJson(savedTran)),
//                                                        AthenaLock.class);
//        assertNotNull(updatedTran);
//        assertNotNull(updatedTran.getId());
//        assertEquals(updatedTran.getLockedByApi(), API_KEY);
//        assertEquals(updatedTran.getLockedByIp(), "127.0.0.1");
//        assertEquals(updatedTran.getStatus(), AthenaLockStatus.OK);
//        DateTime updatedLockExpires = new DateTime(updatedTran.getLockExpires().getTime());
//
//        //TODO: for now, just assure that the lock expires sometime in the future
//        assertTrue(updatedLockExpires.isAfterNow());
//        assertTrue(originalLockExpires.isBefore(updatedLockExpires.toInstant()));
//    }
//
//
//
//
//    @Test
//    public void updateTransactionWithPutAddSomeMoreTickets() throws Exception {
//
//        Set<String> ticketIdsInTransaction = new HashSet<String>();
//        ticketIdsInTransaction.add(t2.getId().toString());
//        ticketIdsInTransaction.add(t3.getId().toString());
//
//        AthenaLock tran = new AthenaLock();
//        tran.setTickets(ticketIdsInTransaction);
//
//        AthenaLock savedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH)
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .post(String.class, gson.toJson(tran)),
//                                                       AthenaLock.class);
//        assertNotNull(savedTran);
//        assertNotNull(savedTran.getId());
//        assertEquals(savedTran.getLockedByApi(), API_KEY);
//        assertEquals(savedTran.getLockedByIp(), "127.0.0.1");
//        assertEquals(savedTran.getStatus(), AthenaLockStatus.OK);
//        DateTime originalLockExpires = new DateTime(savedTran.getLockExpires().getTime());
//
//        //TODO: for now, just assure that the lock expires sometime in the future
//        assertTrue(originalLockExpires.isAfterNow());
//
//        savedTran.getTickets().add(t1.getId().toString());
//        savedTran.setStatus(AthenaLockStatus.RENEW);
//
//        AthenaLock updatedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH + "/" + savedTran.getId())
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .put(String.class, gson.toJson(savedTran)),
//                                                        AthenaLock.class);
//        assertNotNull(updatedTran);
//        assertNotNull(updatedTran.getId());
//        assertEquals(updatedTran.getLockedByApi(), API_KEY);
//        assertEquals(updatedTran.getLockedByIp(), "127.0.0.1");
//        assertEquals(updatedTran.getStatus(), AthenaLockStatus.OK);
//        DateTime updatedLockExpires = new DateTime(updatedTran.getLockExpires().getTime());
//
//        //TODO: for now, just assure that the lock expires sometime in the future
//        assertTrue(updatedLockExpires.isAfterNow());
//        assertTrue(originalLockExpires.isBefore(updatedLockExpires.toInstant()));
//
//        PTicket savedT1 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t1.getId()).toClientTicket();
//        assertFalse(savedTran.getId().equals(savedT1.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_IP));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCK_EXPIRES));
//        assertTicketsEqual(t1, savedT1);
//
//        PTicket savedT4 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t4.getId()).toClientTicket();
//        assertFalse(savedTran.getId().equals(savedT4.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t4, savedT4);
//
//        PTicket savedT5 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t5.getId()).toClientTicket();
//        assertFalse(savedTran.getId().equals(savedT5.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t5, savedT5);
//
//        //now check the two correct tickets
//        PTicket savedT2 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t2.getId()).toClientTicket();
//        assertEquals(savedTran.getId(), savedT2.get(AthenaLockManager.LOCK_ID));
//        assertEquals(savedTran.getLockedByApi(), savedT2.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(savedTran.getLockedByIp(), savedT2.get(AthenaLockManager.LOCKED_BY_IP));
//        DateTime oldExpires = new DateTime(savedTran.getLockExpires());
//        DateTime newExpires = new DateTime(DateUtil.parseDate(savedT2.get(AthenaLockManager.LOCK_EXPIRES)));
//        assertTrue(oldExpires.isBefore(newExpires));
//
//        PTicket savedT3 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t3.getId()).toClientTicket();
//        assertEquals(savedTran.getId(), savedT3.get(AthenaLockManager.LOCK_ID));
//        assertEquals(savedTran.getLockedByApi(), savedT3.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(savedTran.getLockedByIp(), savedT3.get(AthenaLockManager.LOCKED_BY_IP));
//        newExpires = new DateTime(DateUtil.parseDate(savedT3.get(AthenaLockManager.LOCK_EXPIRES)));
//        assertTrue(oldExpires.isBefore(newExpires));
//    }
//
//    @Test
//    public void updateTransactionWithPutOverTheLimit() throws Exception {
//
//        Set<String> ticketIdsInTransaction = new HashSet<String>();
//        ticketIdsInTransaction.add(t2.getId().toString());
//        ticketIdsInTransaction.add(t3.getId().toString());
//
//        AthenaLock tran = new AthenaLock();
//        tran.setTickets(ticketIdsInTransaction);
//
//        //get the lock
//        AthenaLock savedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH)
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .post(String.class, gson.toJson(tran)),
//                                                       AthenaLock.class);
//
//        savedTran.setStatus(AthenaLockStatus.RENEW);
//        //renew the lock
//        AthenaLock updatedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH + "/" + savedTran.getId())
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .put(String.class, gson.toJson(savedTran)),
//                                                        AthenaLock.class);
//
//        savedTran.setStatus(AthenaLockStatus.RENEW);
//        //now try to renew again
//        ClientResponse response = tix.path(TRANSACTIONS_PATH + "/" + savedTran.getId())
//                                     .type("application/json")
//                                     .header("X-ATHENA-Key", API_KEY)
//                                     .put(ClientResponse.class, gson.toJson(savedTran));
//        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        response = tix.path(TRANSACTIONS_PATH + "/" + savedTran.getId())
//                      .type("application/json")
//                      .header("X-ATHENA-Key", API_KEY)
//                      .put(ClientResponse.class, gson.toJson(updatedTran));
//        assertEquals(ClientResponse.Status.BAD_REQUEST, ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }
//
//    @Test
//    public void deleteTransaction() throws Exception {
//
//        Set<String> ticketIdsInTransaction = new HashSet<String>();
//        ticketIdsInTransaction.add(t2.getId().toString());
//        ticketIdsInTransaction.add(t3.getId().toString());
//
//        AthenaLock tran = new AthenaLock();
//        tran.setTickets(ticketIdsInTransaction);
//
//        AthenaLock savedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH)
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .post(String.class, gson.toJson(tran)),
//                                                       AthenaLock.class);
//
//        ClientResponse response = tix.path(TRANSACTIONS_PATH + "/" + savedTran.getId())
//                                     .type("application/json")
//                                     .header("X-ATHENA-Key", API_KEY)
//                                     .delete(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NO_CONTENT, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        PTicket savedT1 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t1.getId()).toClientTicket();
//        assertFalse(savedTran.getId().equals(savedT1.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_IP));
//        assertEquals(null, savedT1.get(AthenaLockManager.LOCK_EXPIRES));
//        assertTicketsEqual(t1, savedT1);
//
//        PTicket savedT2 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t2.getId()).toClientTicket();
//        assertEquals(null, savedT2.get(AthenaLockManager.LOCK_ID));
//        assertEquals(null, savedT2.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT2.get(AthenaLockManager.LOCKED_BY_IP));
//
//        PTicket savedT3 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t3.getId()).toClientTicket();
//        assertEquals(null, savedT3.get(AthenaLockManager.LOCK_ID));
//        assertEquals(null, savedT3.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT3.get(AthenaLockManager.LOCKED_BY_IP));
//
//        PTicket savedT4 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t4.getId()).toClientTicket();
//        assertFalse(savedTran.getId().equals(savedT4.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t4, savedT4);
//
//        PTicket savedT5 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t5.getId()).toClientTicket();
//        assertFalse(savedTran.getId().equals(savedT5.get(AthenaLockManager.LOCK_ID)));
//        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTicketsEqual(t5, savedT5);
//    }
//
//    @Test
//    public void updateTransactionCheckout() throws Exception {
//
//        Set<String> ticketIdsInTransaction = new HashSet<String>();
//        ticketIdsInTransaction.add(t2.getId().toString());
//        ticketIdsInTransaction.add(t3.getId().toString());
//
//        AthenaLock tran = new AthenaLock();
//        tran.setTickets(ticketIdsInTransaction);
//
//        AthenaLock savedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH)
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .post(String.class, gson.toJson(tran)),
//                                                       AthenaLock.class);
//
//        savedTran.setStatus(AthenaLockStatus.RENEW);
//
//        AthenaLock updatedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH + "/" + savedTran.getId())
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .put(String.class, gson.toJson(savedTran)),
//                                                        AthenaLock.class);
//
//        updatedTran.setStatus(AthenaLockStatus.COMPLETE);
//
//        updatedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH + "/" + savedTran.getId())
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .put(String.class, gson.toJson(updatedTran)),
//                                                        AthenaLock.class);
//
//        //now check the two correct tickets
//        PTicket savedT2 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t2.getId()).toClientTicket();
//        assertEquals(savedTran.getId(), savedT2.get(AthenaLockManager.LOCK_ID));
//        assertEquals(savedTran.getLockedByApi(), savedT2.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(savedTran.getLockedByIp(), savedT2.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTrue(Boolean.parseBoolean(savedT2.get("sold")));
//
//        PTicket savedT3 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t3.getId()).toClientTicket();
//        assertEquals(savedTran.getId(), savedT3.get(AthenaLockManager.LOCK_ID));
//        assertEquals(savedTran.getLockedByApi(), savedT3.get(AthenaLockManager.LOCKED_BY_API_KEY));
//        assertEquals(savedTran.getLockedByIp(), savedT3.get(AthenaLockManager.LOCKED_BY_IP));
//        assertTrue(Boolean.parseBoolean(savedT3.get("sold")));
//    }
//
//    @Test
//    public void getDeletedTransaction() throws Exception {
//
//        Set<String> ticketIdsInTransaction = new HashSet<String>();
//        ticketIdsInTransaction.add(t2.getId().toString());
//        ticketIdsInTransaction.add(t3.getId().toString());
//
//        AthenaLock tran = new AthenaLock();
//        tran.setTickets(ticketIdsInTransaction);
//
//        AthenaLock savedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH)
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .post(String.class, gson.toJson(tran)),
//                                                       AthenaLock.class);
//
//        ClientResponse response = tix.path(TRANSACTIONS_PATH + "/" + savedTran.getId())
//                                     .type("application/json")
//                                     .header("X-ATHENA-Key", API_KEY)
//                                     .delete(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NO_CONTENT, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//        response = tix.path(TRANSACTIONS_PATH + "/" + savedTran.getId())
//                                     .header("X-ATHENA-Key", API_KEY)
//                                     .type("application/json")
//                                     .get(ClientResponse.class);
//        assertEquals(ClientResponse.Status.NOT_FOUND, ClientResponse.Status.fromStatusCode(response.getStatus()));
//
//    }
//
//    @Test
//    public void getExpiredTransaction() throws Exception {
//
//        Set<String> ticketIdsInTransaction = new HashSet<String>();
//        ticketIdsInTransaction.add(t2.getId().toString());
//        ticketIdsInTransaction.add(t3.getId().toString());
//
//        AthenaLock tran = new AthenaLock();
//        tran.setTickets(ticketIdsInTransaction);
//
//        AthenaLock savedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH)
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .post(String.class, gson.toJson(tran)),
//                                                       AthenaLock.class);
//
//        DateTime yesterday = new DateTime().minusDays(1);
//        t2 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t2.getId());
//        t2.setTicketProp(new DateTimeTicketProp(lockExpiresProp, yesterday.toDate()));
//        t2 = apa.saveTicket(t2);
//        t3 = apa.getTicket(AthenaLockManager.LOCK_TYPE, t3.getId());
//        t3.setTicketProp(new DateTimeTicketProp(lockExpiresProp, yesterday.toDate()));
//        t3 = apa.saveTicket(t3);
//
//        path = TRANSACTIONS_PATH + "/" + savedTran.getId();
//        tran = gson.fromJson(tix.path(path).header("X-ATHENA-Key", API_KEY).type("application/json").get(String.class), AthenaLock.class);
//        for(String id : tran.getTickets()) {
//            assertTrue(ticketIdsInTransaction.contains(id));
//        }
//        assertEquals(ticketIdsInTransaction.size(), tran.getTickets().size());
//        assertEquals(savedTran.getId(), tran.getId());
//        DateTime time = new DateTime(tran.getLockExpires());
//        assertTrue(time.isBeforeNow());
//    }
//
//    @Test
//    public void getSomeoneElsesTransaction() throws Exception {
//
//        Set<String> ticketIdsInTransaction = new HashSet<String>();
//        ticketIdsInTransaction.add(t2.getId().toString());
//        ticketIdsInTransaction.add(t3.getId().toString());
//
//        AthenaLock tran = new AthenaLock();
//        tran.setTickets(ticketIdsInTransaction);
//
//        AthenaLock savedTran = gson.fromJson(tix.path(TRANSACTIONS_PATH)
//                                                       .type("application/json")
//                                                       .header("X-ATHENA-Key", API_KEY)
//                                                       .post(String.class, gson.toJson(tran)),
//                                                       AthenaLock.class);
//
//        path = TRANSACTIONS_PATH + "/" + savedTran.getId();
//        ClientResponse response = tix.path(path)
//                                       .type("application/json")
//                                       .header("X-ATHENA-Key", "THEWRONGKEY")
//                                       .get(ClientResponse.class);
//
//        assertEquals(ClientResponse.Status.NOT_FOUND,
//                     ClientResponse.Status.fromStatusCode(response.getStatus()));
//    }

    public void setupMockUser(String username) {
        when(mockUser.getUsername()).thenReturn(username);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUser);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockSecurityContextHolderStrategy.getContext()).thenReturn(mockSecurityContext);
    }

    @Before
    public void setupMocking() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void addTickets() throws Exception {
        t1 = new Ticket();
        t2 = new Ticket();
        t3 = new Ticket();
        t4 = new Ticket();
        t5 = new Ticket();

        seatNumberProp = apa.savePropField(new PropField(ValueType.INTEGER, "SEAT_NUMBER", StrictType.NOT_STRICT));
        lockIdProp = apa.savePropField(new PropField(ValueType.STRING, AthenaLockManager.LOCK_ID, StrictType.NOT_STRICT));
        soldProp = apa.savePropField(new PropField(ValueType.BOOLEAN, "sold", StrictType.NOT_STRICT));
        lockedByApiProp = apa.savePropField(new PropField(ValueType.STRING, AthenaLockManager.LOCKED_BY_API_KEY, StrictType.NOT_STRICT));
        lockedByIpProp = apa.savePropField(new PropField(ValueType.STRING, AthenaLockManager.LOCKED_BY_IP, StrictType.NOT_STRICT));
        lockExpiresProp = apa.savePropField(new PropField(ValueType.DATETIME, AthenaLockManager.LOCK_EXPIRES, StrictType.NOT_STRICT));
        lockTimesProp = apa.savePropField(new PropField(ValueType.INTEGER, AthenaLockManager.LOCK_TIMES, StrictType.NOT_STRICT));
        someOtherFieldProp = apa.savePropField(new PropField(ValueType.STRING, "SOME_OTHER_FIELD", StrictType.NOT_STRICT));

        propFieldsToDelete.add(seatNumberProp);
        propFieldsToDelete.add(lockIdProp);
        propFieldsToDelete.add(soldProp);
        propFieldsToDelete.add(lockedByApiProp);
        propFieldsToDelete.add(lockedByIpProp);
        propFieldsToDelete.add(lockExpiresProp);
        propFieldsToDelete.add(lockTimesProp);
        propFieldsToDelete.add(someOtherFieldProp);

        t1.setType("ticket");
        t2.setType("ticket");
        t3.setType("ticket");
        t4.setType("ticket");
        t5.setType("ticket");

        t1.addTicketProp(new IntegerTicketProp(seatNumberProp, 3));
        t2.addTicketProp(new IntegerTicketProp(seatNumberProp, 4));
        t3.addTicketProp(new IntegerTicketProp(seatNumberProp, 5));
        t4.addTicketProp(new IntegerTicketProp(seatNumberProp, 6));
        t5.addTicketProp(new IntegerTicketProp(seatNumberProp, 7));

        t1.addTicketProp(new StringTicketProp(someOtherFieldProp, "$"));
        t2.addTicketProp(new StringTicketProp(someOtherFieldProp, "$"));
        t3.addTicketProp(new StringTicketProp(someOtherFieldProp, "$"));
        t4.addTicketProp(new StringTicketProp(someOtherFieldProp, "$"));
        t5.addTicketProp(new StringTicketProp(someOtherFieldProp, "$"));

        t1 = apa.saveTicket(t1);
        t2 = apa.saveTicket(t2);
        t3 = apa.saveTicket(t3);
        t4 = apa.saveTicket(t4);
        t5 = apa.saveTicket(t5);

        ticketsToDelete.add(t1);
        ticketsToDelete.add(t2);
        ticketsToDelete.add(t3);
        ticketsToDelete.add(t4);
        ticketsToDelete.add(t5);
    }
}
