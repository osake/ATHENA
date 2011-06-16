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
package org.fracturedatlas.athena.plugin.available.web;

import java.util.Collection;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.plugin.available.manager.AvailableTicketsSubCollection;
import org.fracturedatlas.athena.helper.lock.manager.AthenaLockManager;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class AvailableTicketsManagerTest extends BaseManagerTest {

    AvailableTicketsSubCollection manager = new AvailableTicketsSubCollection();
    
    PTicket samplePerformance = new PTicket();
    private static final String TICKET = "ticket";
    private static final String AVAILABLE = "available";
    private static final String SAMPLE_ID = "34";
    private static final String SAMPLE_PERFORMANCE_ID = "id10t";
    private static final Integer SAMPLE_PRICE = 95;

    Set<PTicket> tickets = new HashSet<PTicket>();

    @Mock private RecordManager mockRecordManager;
    @Mock private ApaAdapter mockApa;

    @Test
    public void testFindAvailableTickets() throws Exception {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        Collection<PTicket> foundTickets = manager.get(TICKET, AVAILABLE, queryParams, null);
        assertEquals(4, foundTickets.size());
    }

    @Test
    public void testFindAvailableTicketsWithLimit() throws Exception {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.putSingle("_limit", "1");
        Collection<PTicket> foundTickets = manager.get(TICKET, AVAILABLE, queryParams, null);
        assertEquals(1, foundTickets.size());
    }

    @Test
    public void testFindAvailableTicketsWithLimitTooHigh() throws Exception {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.putSingle("_limit", "10");
        Collection<PTicket> foundTickets = manager.get(TICKET, AVAILABLE, queryParams, null);
        assertEquals(6, foundTickets.size());
    }

    @Before
    public void mockup() throws Exception {
        MockitoAnnotations.initMocks(this);
        createSampleObjects();
        manager.setRecordManager(mockRecordManager);
        manager.setApa(mockApa);
    }

    public void createSampleObjects() throws Exception {

        DateTime now = new DateTime();
        DateTime fiveMinutesFromNow = now.plusMinutes(5);
        DateTime thirtyMinutesAgo = now.minusMinutes(30);

        /* Performance SAMPLE_PERFORMANCE_ID, two tickets locked, two tickets with lock expired, two never locked */

        tickets.add(createRecord(TICKET,
                                 AthenaLockManager.LOCK_ID, "sampleLockId",
                                 AthenaLockManager.LOCKED_BY_API_KEY, "API",
                                 AthenaLockManager.LOCKED_BY_IP, "1.1.1.1",
                                 AthenaLockManager.LOCK_EXPIRES, DateUtil.formatTime(fiveMinutesFromNow),
                                 AthenaLockManager.LOCK_TIMES, "1",
                                 "performanceId", SAMPLE_PERFORMANCE_ID,
                                 "section", "1",
                                 "state", "on_sale"));
        tickets.add(createRecord(TICKET,
                                 AthenaLockManager.LOCK_ID, "sampleLockId",
                                 AthenaLockManager.LOCKED_BY_API_KEY, "API",
                                 AthenaLockManager.LOCKED_BY_IP, "1.1.1.1",
                                 AthenaLockManager.LOCK_EXPIRES, DateUtil.formatTime(fiveMinutesFromNow),
                                 AthenaLockManager.LOCK_TIMES, "1",
                                 "performanceId", SAMPLE_PERFORMANCE_ID,
                                 "section", "2",
                                 "state", "on_sale"));

        tickets.add(createRecord(TICKET,
                                 AthenaLockManager.LOCK_ID, "sampleLockId",
                                 AthenaLockManager.LOCKED_BY_API_KEY, "API",
                                 AthenaLockManager.LOCKED_BY_IP, "1.1.1.1",
                                 AthenaLockManager.LOCK_EXPIRES, DateUtil.formatTime(thirtyMinutesAgo),
                                 AthenaLockManager.LOCK_TIMES, "1",
                                 "performanceId", SAMPLE_PERFORMANCE_ID,
                                 "section", "3",
                                 "state", "on_sale"));
        tickets.add(createRecord(TICKET,
                                 AthenaLockManager.LOCK_ID, "sampleLockId",
                                 AthenaLockManager.LOCKED_BY_API_KEY, "API",
                                 AthenaLockManager.LOCKED_BY_IP, "1.1.1.1",
                                 AthenaLockManager.LOCK_EXPIRES, DateUtil.formatTime(thirtyMinutesAgo),
                                 AthenaLockManager.LOCK_TIMES, "1",
                                 "performanceId", SAMPLE_PERFORMANCE_ID,
                                 "section", "4",
                                 "state", "on_sale"));

        tickets.add(createRecord(TICKET, "foo", "bah",
                                 "performanceId", SAMPLE_PERFORMANCE_ID,
                                 "section", "5",
                                 "state", "on_sale"));
        tickets.add(createRecord(TICKET, "foo", "bah",
                                 "performanceId", SAMPLE_PERFORMANCE_ID,
                                 "section", "6",
                                 "state", "on_sale"));

        tickets.add(createRecord(TICKET, "foo", "bah",
                                 "performanceId", SAMPLE_PERFORMANCE_ID,
                                 "section", "7",
                                 "state", "something_else"));
        tickets.add(createRecord(TICKET, "foo", "bah",
                                 "performanceId", SAMPLE_PERFORMANCE_ID,
                                 "section", "8",
                                 "state", "off_sale"));

        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.putSingle("state", "on_sale");
        when(mockRecordManager.findRecords(TICKET, queryParams)).thenReturn(tickets);
    }
}
