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

import org.fracturedatlas.athena.exception.AthenaException;
import com.google.gson.Gson;
import com.sun.jersey.api.NotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.lock.exception.TicketsLockedException;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class AthenaLockManagerTest extends BaseLockManagerTest {
    Gson gson = JsonUtil.getGson();
    String path = "/";

    PTicket t1 = null;
    PTicket t2 = null;
    PTicket t3 = null;
    PTicket t4 = null;
    PTicket t5 = null;

    PropField seatNumberProp = null;
    PropField lockIdProp = null;
    PropField statusProp = null;
    PropField lockedByApiProp = null;
    PropField lockedByIpProp = null;
    PropField lockExpiresProp = null;
    PropField lockTimesProp = null;
    PropField someOtherFieldProp = null;

    @Mock private SecurityContextHolderStrategy mockSecurityContextHolderStrategy;
    @Mock private SecurityContext mockSecurityContext;
    @Mock private User mockUser;
    @Mock private Authentication mockAuthentication;
    @Mock private HttpServletRequest mockHttpServletRequest;

    AthenaLockManager manager;

    public static final String CLIENT_IP = "192.168.4.90";
    public static final String TEST_USERNAME = "jimjim7";

    public AthenaLockManagerTest() throws Exception {
        super();
    }

    @After
    public void teardown() {
        super.teardownRecords();
    }

    @Test
    public void testGetTransaction() throws Exception {

        String testTransactionId = "TEST_TRANSACTION_ID";
        Date originalLockExpires = DateUtil.parseDate("2010-05-05T05:05:33-04:00");

        /* SETUP THE TRANSACTION IN THE DB */
        List<String> ticketIdsInTransaction = new ArrayList<String>();
        int i=0;
        for(PTicket t : recordsToDelete) {

            //put three tickets in this transaction, one ticket in another transaction, and another ticket just hanging out
            if(i<3) {
                t.put(AthenaLockManager.LOCK_ID, testTransactionId);
                t.put("status", "on_sale");
                t.put(AthenaLockManager.LOCK_EXPIRES, "2010-05-05T05:05:33-04:00");
                t.put(AthenaLockManager.LOCKED_BY_API_KEY, TEST_USERNAME);
                t.put(AthenaLockManager.LOCKED_BY_IP, CLIENT_IP);
                t = apa.saveRecord(t);
                ticketIdsInTransaction.add(t.getId().toString());
            } else if (i<4) {
                t.put(AthenaLockManager.LOCK_ID, "FAKE_TRAN_ID");
                t.put("status", "on_sale");
                t.put(AthenaLockManager.LOCK_EXPIRES, "2011-05-05T05:05:33-04:00");
                t.put(AthenaLockManager.LOCKED_BY_API_KEY, TEST_USERNAME);
                t.put(AthenaLockManager.LOCKED_BY_IP, CLIENT_IP);
                t = apa.saveRecord(t);
            } else {
                t = apa.getRecord("ticket", t.getId());
                t.put("status", "on_sale");
                t = apa.saveRecord(t);
            }
            i++;
        }

        AthenaLock tran = manager.getLock(testTransactionId, mockHttpServletRequest);
        for(String id : tran.getTickets()) {
            assertTrue(ticketIdsInTransaction.contains(id));
        }
        assertEquals(ticketIdsInTransaction.size(), tran.getTickets().size());
        assertEquals(testTransactionId, tran.getId());
        assertEquals(TEST_USERNAME, tran.getLockedByApi());
        assertEquals(CLIENT_IP, tran.getLockedByIp());
        assertEquals(originalLockExpires, tran.getLockExpires());
    }

    @Test
    public void testGetTransactionNotFound() throws Exception {

        /* Get the transaction */
        try {
            AthenaLock savedTran = manager.getLock("FAKE_ID", mockHttpServletRequest);
            fail("Should have thrown NotFoundException");
        } catch (NotFoundException nfe) {
            //pass!
        }
    }

    @Test
    public void createNewTransaction() throws Exception {

        Set<String> ticketIdsInTransaction = new HashSet<String>();
        ticketIdsInTransaction.add(t2.getId().toString());
        ticketIdsInTransaction.add(t3.getId().toString());

        AthenaLock tran = new AthenaLock();
        tran.setTickets(ticketIdsInTransaction);

        AthenaLock savedTran = manager.createLock(mockHttpServletRequest, tran);

        assertNotNull(savedTran);
        assertNotNull(savedTran.getId());
        assertEquals(savedTran.getLockedByApi(), TEST_USERNAME);
        assertEquals(savedTran.getLockedByIp(), CLIENT_IP);
        assertEquals(savedTran.getStatus(), AthenaLockStatus.OK);
        DateTime originalLockExpires = new DateTime(savedTran.getLockExpires().getTime());

        //TODO: for now, just assure that the lock expires sometime in the future
        assertTrue(originalLockExpires.isAfterNow());

        //check the three tickets involved in this transaction make sure that
        //they aren't marked
        PTicket savedT1 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t1.getId());
        savedT1.setType(AthenaLockManager.LOCK_TYPE);
        assertFalse(savedTran.getId().equals(savedT1.get(AthenaLockManager.LOCK_ID)));
        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_IP));
        assertRecordsEqual(t1, savedT1, true);

        PTicket savedT4 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t4.getId());
        savedT4.setType(AthenaLockManager.LOCK_TYPE);
        assertFalse(savedTran.getId().equals(savedT4.get(AthenaLockManager.LOCK_ID)));
        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_IP));
        assertRecordsEqual(t4, savedT4, true);

        PTicket savedT5 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t5.getId());
        savedT5.setType(AthenaLockManager.LOCK_TYPE);
        assertFalse(savedTran.getId().equals(savedT5.get(AthenaLockManager.LOCK_ID)));
        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_IP));
        assertRecordsEqual(t5, savedT5, true);

        //now check the two correct tickets
        PTicket savedT2 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t2.getId());
        assertEquals(savedTran.getId(), savedT2.get(AthenaLockManager.LOCK_ID));
        assertEquals(savedTran.getLockedByApi(), savedT2.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(savedTran.getLockedByIp(), savedT2.get(AthenaLockManager.LOCKED_BY_IP));
        assertEquals(DateUtil.formatDate(savedTran.getLockExpires()), savedT2.get(AthenaLockManager.LOCK_EXPIRES));

        PTicket savedT3 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t3.getId());
        assertEquals(savedTran.getId(), savedT3.get(AthenaLockManager.LOCK_ID));
        assertEquals(savedTran.getLockedByApi(), savedT3.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(savedTran.getLockedByIp(), savedT3.get(AthenaLockManager.LOCKED_BY_IP));
        assertEquals(DateUtil.formatDate(savedTran.getLockExpires()), savedT3.get(AthenaLockManager.LOCK_EXPIRES));
    }

    @Test
    public void createNewTransactionThenLockAgain() throws Exception {

        Set<String> ticketIdsInTransaction = new HashSet<String>();
        ticketIdsInTransaction.add(t2.getId().toString());
        ticketIdsInTransaction.add(t3.getId().toString());

        AthenaLock tran = new AthenaLock();
        tran.setTickets(ticketIdsInTransaction);

        AthenaLock savedTran = manager.createLock(mockHttpServletRequest, tran);
        assertNotNull(savedTran);
        assertNotNull(savedTran.getId());
        assertEquals(savedTran.getLockedByApi(), TEST_USERNAME);
        assertEquals(savedTran.getLockedByIp(), CLIENT_IP);
        assertEquals(savedTran.getStatus(), AthenaLockStatus.OK);
        DateTime originalLockExpires = new DateTime(savedTran.getLockExpires().getTime());

        //TODO: for now, just assure that the lock expires sometime in the future
        assertTrue(originalLockExpires.isAfterNow());

        //now try to lock them again
        createMockUser("newuser40");
        try {
            manager.createLock(mockHttpServletRequest, tran);
            fail("Tickets should already be locked");
        } catch (TicketsLockedException tle) {
            //pass
        }

    }

    @Test
    public void createNewTransactionTicketsAlreadyLocked() throws Exception {

        //lock ticket2
        PropField transactionIdField = apa.getPropField(AthenaLockManager.LOCK_ID);
        PropField lockExpiresField = apa.getPropField(AthenaLockManager.LOCK_EXPIRES);
        t2.put(AthenaLockManager.LOCK_ID, "30303030033");
        DateTime expires = new DateTime().plusMinutes(2);
        t2.put(AthenaLockManager.LOCK_EXPIRES, DateUtil.formatDate(expires.toDate()));
        t2 = apa.saveRecord(t2);

        //Now try to start the transaction with t2 already locked
        Set<String> ticketIdsInTransaction = new HashSet<String>();
        ticketIdsInTransaction.add(t2.getId().toString());
        ticketIdsInTransaction.add(t3.getId().toString());

        AthenaLock tran = new AthenaLock();
        tran.setTickets(ticketIdsInTransaction);

        try{
            AthenaLock savedTran = manager.createLock(mockHttpServletRequest, tran);
            fail("Tickets should already be locked");
        } catch (TicketsLockedException tle) {
            //pass
        }

        //check the three tickets involved in this transaction make sure that
        //they aren't marked
        assertTicketUnchanged(t1);
        assertTicketUnchanged(t3);
        assertTicketUnchanged(t4);
        assertTicketUnchanged(t5);

        //check to make sure t2 doesn't have a new lock on it
        PTicket savedT2 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t2.getId());
        assertEquals("30303030033", savedT2.get(AthenaLockManager.LOCK_ID));
    }

    private void assertTicketUnchanged(PTicket t1) {
        PTicket savedT1 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t1.getId());
        savedT1.setType(AthenaLockManager.LOCK_TYPE);
        assertFalse("30303030033".equals(savedT1.get(AthenaLockManager.LOCK_ID)));
        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_IP));
        assertRecordsEqual(t1, savedT1, true);
    }

    @Test
    public void createNewTransactionWithUnknownTickets() throws Exception {
        Set<String> ticketIdsInTransaction = new HashSet<String>();
        ticketIdsInTransaction.add(t2.getId().toString());
        ticketIdsInTransaction.add(t3.getId().toString());
        ticketIdsInTransaction.add("0");

        AthenaLock tran = new AthenaLock();
        tran.setTickets(ticketIdsInTransaction);

        try{
            AthenaLock savedTran = manager.createLock(mockHttpServletRequest, tran);
            fail("Tickets should not be locked because of bad id");
        } catch (TicketsLockedException tle) {
            //pass
        }

        //check the three tickets involved in this transaction make sure that
        //they aren't marked
        assertTicketUnchanged(t1);

        PTicket savedT2 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t2.getId());
        savedT2.setType(AthenaLockManager.LOCK_TYPE);
        assertNull(savedT2.get(AthenaLockManager.LOCK_ID));
        assertEquals(null, savedT2.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT2.get(AthenaLockManager.LOCKED_BY_IP));
        assertRecordsEqual(t2, savedT2, true);
        
        assertTicketUnchanged(t3);
        assertTicketUnchanged(t4);

        PTicket savedT5 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t5.getId());
        savedT5.setType(AthenaLockManager.LOCK_TYPE);
        assertNull(savedT5.get(AthenaLockManager.LOCK_ID));
        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_IP));
        assertRecordsEqual(t5, savedT5, true);
    }

    @Test
    public void updateTransactionWithPut() throws Exception {

        Set<String> ticketIdsInTransaction = new HashSet<String>();
        ticketIdsInTransaction.add(t2.getId().toString());
        ticketIdsInTransaction.add(t3.getId().toString());

        AthenaLock tran = new AthenaLock();
        tran.setTickets(ticketIdsInTransaction);

        AthenaLock savedTran = manager.createLock(mockHttpServletRequest, tran);
        assertNotNull(savedTran);
        assertNotNull(savedTran.getId());
        assertEquals(savedTran.getLockedByApi(), TEST_USERNAME);
        assertEquals(savedTran.getLockedByIp(), CLIENT_IP);
        assertEquals(savedTran.getStatus(), AthenaLockStatus.OK);
        DateTime originalLockExpires = new DateTime(savedTran.getLockExpires().getTime());

        //TODO: for now, just assure that the lock expires sometime in the future
        assertTrue(originalLockExpires.isAfterNow());

        savedTran.setStatus(AthenaLockStatus.RENEW);

        AthenaLock updatedTran = manager.updateLock(savedTran.getId(), mockHttpServletRequest, savedTran);
        assertNotNull(updatedTran);
        assertNotNull(updatedTran.getId());
        assertEquals(updatedTran.getLockedByApi(), TEST_USERNAME);
        assertEquals(updatedTran.getLockedByIp(), CLIENT_IP);
        assertEquals(updatedTran.getStatus(), AthenaLockStatus.OK);
        DateTime updatedLockExpires = new DateTime(updatedTran.getLockExpires().getTime());

        //TODO: for now, just assure that the lock expires sometime in the future
        assertTrue(updatedLockExpires.isAfterNow());
        assertTrue(originalLockExpires.isBefore(updatedLockExpires.toInstant()));
    }

    @Test
    public void updateTransactionWithPutAddSomeMoreTickets() throws Exception {

        Set<String> ticketIdsInTransaction = new HashSet<String>();
        ticketIdsInTransaction.add(t2.getId().toString());
        ticketIdsInTransaction.add(t3.getId().toString());

        AthenaLock tran = new AthenaLock();
        tran.setTickets(ticketIdsInTransaction);

        AthenaLock savedTran = manager.createLock(mockHttpServletRequest, tran);
        assertNotNull(savedTran);
        assertNotNull(savedTran.getId());
        assertEquals(savedTran.getLockedByApi(), TEST_USERNAME);
        assertEquals(savedTran.getLockedByIp(), CLIENT_IP);
        assertEquals(savedTran.getStatus(), AthenaLockStatus.OK);
        DateTime originalLockExpires = new DateTime(savedTran.getLockExpires().getTime());

        //TODO: for now, just assure that the lock expires sometime in the future
        assertTrue(originalLockExpires.isAfterNow());

        savedTran.getTickets().add(t1.getId().toString());
        savedTran.setStatus(AthenaLockStatus.RENEW);

        AthenaLock updatedTran = manager.updateLock(savedTran.getId(), mockHttpServletRequest, savedTran);
        assertNotNull(updatedTran);
        assertNotNull(updatedTran.getId());
        assertEquals(updatedTran.getLockedByApi(), TEST_USERNAME);
        assertEquals(updatedTran.getLockedByIp(), CLIENT_IP);
        assertEquals(updatedTran.getStatus(), AthenaLockStatus.OK);
        DateTime updatedLockExpires = new DateTime(updatedTran.getLockExpires().getTime());

        //TODO: for now, just assure that the lock expires sometime in the future
        assertTrue(updatedLockExpires.isAfterNow());
        assertTrue(originalLockExpires.isBefore(updatedLockExpires.toInstant()));

        PTicket savedT1 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t1.getId());
        savedT1.setType(AthenaLockManager.LOCK_TYPE);
        assertFalse(savedTran.getId().equals(savedT1.get(AthenaLockManager.LOCK_ID)));
        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_IP));
        assertEquals(null, savedT1.get(AthenaLockManager.LOCK_EXPIRES));
        assertRecordsEqual(t1, savedT1, true);

        PTicket savedT4 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t4.getId());
        savedT4.setType(AthenaLockManager.LOCK_TYPE);
        assertFalse(savedTran.getId().equals(savedT4.get(AthenaLockManager.LOCK_ID)));
        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_IP));
        assertRecordsEqual(t4, savedT4, true);

        PTicket savedT5 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t5.getId());
        savedT5.setType(AthenaLockManager.LOCK_TYPE);
        assertFalse(savedTran.getId().equals(savedT5.get(AthenaLockManager.LOCK_ID)));
        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_IP));
        assertRecordsEqual(t5, savedT5, true);

        //now check the two correct tickets
        PTicket savedT2 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t2.getId());
        savedT2.setType(AthenaLockManager.LOCK_TYPE);
        assertEquals(savedTran.getId(), savedT2.get(AthenaLockManager.LOCK_ID));
        assertEquals(savedTran.getLockedByApi(), savedT2.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(savedTran.getLockedByIp(), savedT2.get(AthenaLockManager.LOCKED_BY_IP));
        DateTime oldExpires = new DateTime(savedTran.getLockExpires());
        DateTime newExpires = new DateTime(DateUtil.parseDate(savedT2.get(AthenaLockManager.LOCK_EXPIRES)));
        assertTrue(originalLockExpires.isBefore(newExpires));

        PTicket savedT3 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t3.getId());
        savedT3.setType(AthenaLockManager.LOCK_TYPE);
        assertEquals(savedTran.getId(), savedT3.get(AthenaLockManager.LOCK_ID));
        assertEquals(savedTran.getLockedByApi(), savedT3.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(savedTran.getLockedByIp(), savedT3.get(AthenaLockManager.LOCKED_BY_IP));
        newExpires = new DateTime(DateUtil.parseDate(savedT3.get(AthenaLockManager.LOCK_EXPIRES)));
        assertTrue(originalLockExpires.isBefore(newExpires));
    }

    @Test
    public void updateTransactionWithPutOverTheLimit() throws Exception {

        Set<String> ticketIdsInTransaction = new HashSet<String>();
        ticketIdsInTransaction.add(t2.getId().toString());
        ticketIdsInTransaction.add(t3.getId().toString());

        AthenaLock tran = new AthenaLock();
        tran.setTickets(ticketIdsInTransaction);

        //get the lock
        AthenaLock savedTran = manager.createLock(mockHttpServletRequest, tran);

        savedTran.setStatus(AthenaLockStatus.RENEW);
        //renew the lock
        AthenaLock updatedTran = manager.updateLock(savedTran.getId(), mockHttpServletRequest, savedTran);

        savedTran.setStatus(AthenaLockStatus.RENEW);
        //now try to renew again

        try {
            manager.updateLock(savedTran.getId(), mockHttpServletRequest, savedTran);
            fail("Should not have locked");
        } catch (AthenaException ae) {
            //pass
        }
    }

    @Test
    public void deleteUnknownTransaction() throws Exception {
        manager.deleteLock("UNKNOWN", mockHttpServletRequest);
        //nothing should happen here.  We are intentionaly returning a 204 in all cases.
    }

    @Test
    public void deleteTransaction() throws Exception {

        Set<String> ticketIdsInTransaction = new HashSet<String>();
        ticketIdsInTransaction.add(t2.getId().toString());
        ticketIdsInTransaction.add(t3.getId().toString());

        AthenaLock tran = new AthenaLock();
        tran.setTickets(ticketIdsInTransaction);

        AthenaLock savedTran = manager.createLock(mockHttpServletRequest, tran);

        manager.deleteLock(savedTran.getId(), mockHttpServletRequest);

        PTicket savedT1 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t1.getId());
        savedT1.setType(AthenaLockManager.LOCK_TYPE);
        assertFalse(savedTran.getId().equals(savedT1.get(AthenaLockManager.LOCK_ID)));
        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT1.get(AthenaLockManager.LOCKED_BY_IP));
        assertEquals(null, savedT1.get(AthenaLockManager.LOCK_EXPIRES));
        assertRecordsEqual(t1, savedT1, true);

        PTicket savedT2 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t2.getId());
        assertEquals(null, savedT2.get(AthenaLockManager.LOCK_ID));
        assertEquals(null, savedT2.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT2.get(AthenaLockManager.LOCKED_BY_IP));

        PTicket savedT3 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t3.getId());
        assertEquals(null, savedT3.get(AthenaLockManager.LOCK_ID));
        assertEquals(null, savedT3.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT3.get(AthenaLockManager.LOCKED_BY_IP));

        PTicket savedT4 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t4.getId());
        savedT4.setType(AthenaLockManager.LOCK_TYPE);
        assertFalse(savedTran.getId().equals(savedT4.get(AthenaLockManager.LOCK_ID)));
        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT4.get(AthenaLockManager.LOCKED_BY_IP));
        assertRecordsEqual(t4, savedT4, true);

        PTicket savedT5 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t5.getId());
        savedT5.setType(AthenaLockManager.LOCK_TYPE);
        assertFalse(savedTran.getId().equals(savedT5.get(AthenaLockManager.LOCK_ID)));
        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(null, savedT5.get(AthenaLockManager.LOCKED_BY_IP));
        assertRecordsEqual(t5, savedT5, true);
    }

    @Test
    public void updateTransactionCheckout() throws Exception {

        Set<String> ticketIdsInTransaction = new HashSet<String>();
        ticketIdsInTransaction.add(t2.getId().toString());
        ticketIdsInTransaction.add(t3.getId().toString());

        AthenaLock tran = new AthenaLock();
        tran.setTickets(ticketIdsInTransaction);

        AthenaLock savedTran = manager.createLock(mockHttpServletRequest, tran);

        savedTran.setStatus(AthenaLockStatus.RENEW);

        AthenaLock updatedTran = manager.updateLock(savedTran.getId(), mockHttpServletRequest, savedTran);

        updatedTran.setStatus(AthenaLockStatus.COMPLETE);

        updatedTran = manager.updateLock(updatedTran.getId(), mockHttpServletRequest, savedTran);

        //now check the two correct tickets
        PTicket savedT2 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t2.getId());
        savedT2.setType(AthenaLockManager.LOCK_TYPE);
        assertEquals(savedTran.getId(), savedT2.get(AthenaLockManager.LOCK_ID));
        assertEquals(savedTran.getLockedByApi(), savedT2.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(savedTran.getLockedByIp(), savedT2.get(AthenaLockManager.LOCKED_BY_IP));
        assertTrue("sold".equals(savedT2.get("status")));

        PTicket savedT3 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t3.getId());
        savedT3.setType(AthenaLockManager.LOCK_TYPE);
        assertEquals(savedTran.getId(), savedT3.get(AthenaLockManager.LOCK_ID));
        assertEquals(savedTran.getLockedByApi(), savedT3.get(AthenaLockManager.LOCKED_BY_API_KEY));
        assertEquals(savedTran.getLockedByIp(), savedT3.get(AthenaLockManager.LOCKED_BY_IP));
        assertTrue("sold".equals(savedT3.get("status")));
    }

    @Test
    public void getDeletedTransaction() throws Exception {

        Set<String> ticketIdsInTransaction = new HashSet<String>();
        ticketIdsInTransaction.add(t2.getId().toString());
        ticketIdsInTransaction.add(t3.getId().toString());

        AthenaLock tran = new AthenaLock();
        tran.setTickets(ticketIdsInTransaction);

        AthenaLock savedTran = manager.createLock(mockHttpServletRequest, tran);

        manager.deleteLock(tran.getId(), mockHttpServletRequest);

        try {
            manager.getLock(tran.getId(), mockHttpServletRequest);
            fail("Should have thrown NotFoundException");
        } catch (NotFoundException nfe) {
            //pass!
        }

    }

    @Test
    public void getExpiredTransaction() throws Exception {

        Set<String> ticketIdsInTransaction = new HashSet<String>();
        ticketIdsInTransaction.add(t2.getId().toString());
        ticketIdsInTransaction.add(t3.getId().toString());

        AthenaLock tran = new AthenaLock();
        tran.setTickets(ticketIdsInTransaction);

        AthenaLock savedTran = manager.createLock(mockHttpServletRequest, tran);

        DateTime yesterday = new DateTime().minusDays(1);
        t2 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t2.getId());
        t2.put(AthenaLockManager.LOCK_EXPIRES, DateUtil.formatDate(yesterday.toDate()));
        t2 = apa.saveRecord(t2);
        t3 = apa.getRecord(AthenaLockManager.LOCK_TYPE, t3.getId());
        t3.put(AthenaLockManager.LOCK_EXPIRES, DateUtil.formatDate(yesterday.toDate()));
        t3 = apa.saveRecord(t3);

        tran = manager.getLock(savedTran.getId(), mockHttpServletRequest);
        for(String id : tran.getTickets()) {
            assertTrue(ticketIdsInTransaction.contains(id));
        }
        assertEquals(ticketIdsInTransaction.size(), tran.getTickets().size());
        assertEquals(savedTran.getId(), tran.getId());
        DateTime time = new DateTime(tran.getLockExpires());
        assertTrue(time.isBeforeNow());
    }

    @Test
    public void getSomeoneElsesTransaction() throws Exception {

        Set<String> ticketIdsInTransaction = new HashSet<String>();
        ticketIdsInTransaction.add(t2.getId().toString());
        ticketIdsInTransaction.add(t3.getId().toString());

        AthenaLock tran = new AthenaLock();
        tran.setTickets(ticketIdsInTransaction);

        AthenaLock savedTran = manager.createLock(mockHttpServletRequest, tran);

        createMockUser("notherightguy4");
        try {
            manager.getLock(tran.getId(), mockHttpServletRequest);
            fail("Should have thrown NotFoundException");
        } catch (NotFoundException nfe) {
            //pass!
        }
    }

    @Before
    public void setupContext() throws Exception {
        setupMocking();
        
        manager = new AthenaLockManager();
        ApplicationContext context = new ClassPathXmlApplicationContext("athenatest-applicationContext.xml");
        apa = (ApaAdapter)context.getBean("apa");        
        manager.setApa(apa);

        when(mockHttpServletRequest.getRemoteAddr()).thenReturn(CLIENT_IP);

        addTickets();

        createMockUser(TEST_USERNAME);
    }

    public void createMockUser(String username) {
        when(mockUser.getUsername()).thenReturn(username);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUser);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockSecurityContextHolderStrategy.getContext()).thenReturn(mockSecurityContext);

        manager.setContextHolderStrategy(mockSecurityContextHolderStrategy);
    }

    public void setupMocking() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    public void addTickets() throws Exception {
        t1 = new PTicket("ticket");
        t2 = new PTicket("ticket");
        t3 = new PTicket("ticket");
        t4 = new PTicket("ticket");
        t5 = new PTicket("ticket");

        addPropField(ValueType.INTEGER, "SEAT_NUMBER", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, AthenaLockManager.LOCK_ID, StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "status", StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, AthenaLockManager.LOCKED_BY_API_KEY, StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, AthenaLockManager.LOCKED_BY_IP, StrictType.NOT_STRICT);
        addPropField(ValueType.DATETIME, AthenaLockManager.LOCK_EXPIRES, StrictType.NOT_STRICT);
        addPropField(ValueType.INTEGER, AthenaLockManager.LOCK_TIMES, StrictType.NOT_STRICT);
        addPropField(ValueType.STRING, "SOME_OTHER_FIELD", StrictType.NOT_STRICT);

        t1.put("SEAT_NUMBER", "3");
        t2.put("SEAT_NUMBER", "4");
        t3.put("SEAT_NUMBER", "5");
        t4.put("SEAT_NUMBER", "6");
        t5.put("SEAT_NUMBER", "7");

        t1.put("SOME_OTHER_FIELD", "$");
        t2.put("SOME_OTHER_FIELD", "$");
        t3.put("SOME_OTHER_FIELD", "$");
        t4.put("SOME_OTHER_FIELD", "$");
        t5.put("SOME_OTHER_FIELD", "$");

        t1 = apa.saveRecord(t1);
        t2 = apa.saveRecord(t2);
        t3 = apa.saveRecord(t3);
        t4 = apa.saveRecord(t4);
        t5 = apa.saveRecord(t5);

        recordsToDelete.add(t1);
        recordsToDelete.add(t2);
        recordsToDelete.add(t3);
        recordsToDelete.add(t4);
        recordsToDelete.add(t5);
    }
}
