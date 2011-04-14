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
package org.fracturedatlas.athena.helper.codes.web;

import java.util.HashSet;
import java.util.Set;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.codes.manager.CodeManager;
import org.fracturedatlas.athena.helper.codes.model.Code;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CodeManagerTest {

    CodeManager manager = new CodeManager();
    Code code = new Code();
    PTicket sampleTicket = new PTicket();

    //the saved version of sampleTicket
    PTicket targetTicket = new PTicket();
    
    PTicket samplePerformance = new PTicket();
    PTicket targetCode = new PTicket();
    PTicket targetCodeWithId = new PTicket();
    private static final String SAMPLE_CODE = "SAMPLE_CODE";
    private static final String SAMPLE_ID = "34";
    private static final String SAMPLE_PERFORMANCE_ID = "id10t";
    private static final Integer SAMPLE_PRICE = 95;
    @Mock private RecordManager mockRecordManager;
    @Mock private AthenaComponent mockStage;
    @Mock private ApaAdapter mockApa;

    @Test
    public void testGetCode() throws Exception {
        Code savedCode = manager.getCode(SAMPLE_ID);
        verify(mockRecordManager, times(1)).getTicket(CodeManager.CODE, SAMPLE_ID);
        assertNotNull(savedCode);
        assertNotNull(savedCode.getId());
        assertSavedcodeIsCorrect(savedCode, code);
    }

    @Test
    public void testCreateCode() throws Exception {
        Code createdCode = manager.createCode(code);
        assertNotNull(createdCode);
        assertSavedcodeIsCorrect(createdCode, code);
        assertEquals(createdCode.getTickets(), code.getTickets());

        verify(mockRecordManager, times(1)).getTicket("ticket", sampleTicket.getId());
        verify(mockRecordManager, times(code.getTickets().size())).updateRecord("ticket", targetTicket);
        verify(mockRecordManager, times(1)).createRecord(CodeManager.CODE, targetCode);
    }

    @Test
    public void testCreateCodeWithPerformance() throws Exception {
        PTicket sampleTicket2 = new PTicket("ticket");
        sampleTicket2.setId("sampleTicket2Id");
        sampleTicket2.put("performanceId", SAMPLE_PERFORMANCE_ID);

        AthenaSearchConstraint con1 = new AthenaSearchConstraint("performanceId", Operator.EQUALS, SAMPLE_PERFORMANCE_ID);
        AthenaSearch athenaSearch = new AthenaSearch.Builder(con1).type("ticket").build();
        Set<PTicket> tickets = new HashSet<PTicket>();
        tickets.add(sampleTicket);
        tickets.add(sampleTicket2);
        when(mockApa.findTickets(athenaSearch)).thenReturn(tickets);

        code.setTickets(null);
        code.getPerformances().add(samplePerformance.getIdAsString());
        Code createdCode = manager.createCode(code);
        assertNotNull(createdCode);
        assertSavedcodeIsCorrect(createdCode, code);

        //Should be three tickets on this code, two fromt he perf and one on the code
        Set<String> targetTicketIds = new HashSet<String>();
        targetTicketIds.add(IdAdapter.toString(sampleTicket.getId()));
        targetTicketIds.add(IdAdapter.toString(sampleTicket2.getId()));
        assertEquals(createdCode.getTickets(), targetTicketIds);

        verify(mockRecordManager, times(1)).createRecord(CodeManager.CODE, targetCode);
        verify(mockApa, times(1)).findTickets(athenaSearch);
        verify(mockRecordManager, times(1)).updateRecord("ticket", targetTicket);
        verify(mockRecordManager, times(1)).updateRecord("ticket", sampleTicket2);
    }

    @Test
    public void testCreateCodeTwoPerformances() throws Exception {
        PTicket sampleTicket2 = new PTicket("ticket");
        sampleTicket2.setId("sampleTicket2Id");
        sampleTicket2.put("performanceId", "samplePerformanceId2");

        Set<String> performanceIds = new HashSet<String>();
        performanceIds.add(SAMPLE_PERFORMANCE_ID);
        performanceIds.add("samplePerformanceId2");
        code.setPerformances(performanceIds);

        AthenaSearch athenaSearch = new AthenaSearch.Builder().type("ticket").build();
        athenaSearch.addConstraint("performanceId", Operator.IN, performanceIds);
        Set<PTicket> tickets = new HashSet<PTicket>();
        tickets.add(sampleTicket);
        tickets.add(sampleTicket2);
        when(mockApa.findTickets(athenaSearch)).thenReturn(tickets);

        code.setTickets(null);
        code.getPerformances().add(samplePerformance.getIdAsString());
        Code createdCode = manager.createCode(code);
        assertNotNull(createdCode);
        assertSavedcodeIsCorrect(createdCode, code);
        Set<String> targetTicketIds = new HashSet<String>();
        targetTicketIds.add(IdAdapter.toString(sampleTicket.getId()));
        targetTicketIds.add(IdAdapter.toString(sampleTicket2.getId()));
        assertEquals(createdCode.getTickets(), targetTicketIds);

        verify(mockRecordManager, times(1)).createRecord(CodeManager.CODE, targetCode);
        verify(mockApa, times(1)).findTickets(athenaSearch);
        verify(mockRecordManager, times(1)).updateRecord("ticket", targetTicket);
        verify(mockRecordManager, times(1)).updateRecord("ticket", sampleTicket2);
    }

    @Test
    public void testCreateCodeTwoPerformancesAndTwoEvents() throws Exception {
        PTicket sampleTicket2 = new PTicket("ticket");
        sampleTicket2.setId("sampleTicket2Id");
        sampleTicket2.put("performanceId", "samplePerformanceId2");

        Set<String> performanceIds = new HashSet<String>();
        performanceIds.add(SAMPLE_PERFORMANCE_ID);
        performanceIds.add("samplePerformanceId2");
        performanceIds.add("performance_19");
        code.setPerformances(performanceIds);

        Set<String> eventIds = new HashSet<String>();
        eventIds.add("EVENT1");
        eventIds.add("EVENT2");
        code.setEvents(eventIds);

        PTicket samplePerformanceForEvent = new PTicket("performance");
        samplePerformanceForEvent.setId("performance_19");

        PTicket sampleTicketForPerformance = new PTicket("ticket");
        sampleTicketForPerformance.setId("ticket_19");

        //setup the performances by events search
        AthenaSearch athenaPerformanceSearch = new AthenaSearch.Builder().type("performance").build();
        athenaPerformanceSearch.addConstraint("eventId", Operator.IN, eventIds);
        Set<PTicket> perfs = new HashSet<PTicket>();
        perfs.add(samplePerformanceForEvent);
        when(mockStage.find("performance", athenaPerformanceSearch)).thenReturn(perfs);

        //setup the tickets by performance search
        AthenaSearch athenaTicketSearch = new AthenaSearch.Builder().type("ticket").build();
        athenaTicketSearch.addConstraint("performanceId", Operator.IN, performanceIds);
        Set<PTicket> tickets = new HashSet<PTicket>();
        tickets.add(sampleTicket);
        tickets.add(sampleTicket2);
        tickets.add(sampleTicketForPerformance);
        when(mockApa.findTickets(athenaTicketSearch)).thenReturn(tickets);

        code.setTickets(null);
        code.getPerformances().add(samplePerformance.getIdAsString());
        Code createdCode = manager.createCode(code);
        assertNotNull(createdCode);
        assertSavedcodeIsCorrect(createdCode, code);
        Set<String> targetTicketIds = new HashSet<String>();
        targetTicketIds.add(IdAdapter.toString(sampleTicket.getId()));
        targetTicketIds.add(IdAdapter.toString(sampleTicket2.getId()));
        targetTicketIds.add(IdAdapter.toString(sampleTicketForPerformance.getId()));
        assertEquals(createdCode.getTickets(), targetTicketIds);

        verify(mockRecordManager, times(1)).createRecord(CodeManager.CODE, targetCode);
        verify(mockStage, times(1)).find("performance", athenaPerformanceSearch);
        verify(mockApa, times(1)).findTickets(athenaTicketSearch);
        verify(mockRecordManager, times(1)).updateRecord("ticket", targetTicket);
        verify(mockRecordManager, times(1)).updateRecord("ticket", sampleTicket2);
        verify(mockRecordManager, times(1)).updateRecord("ticket", sampleTicketForPerformance);
    }

    @Test
    public void testCreateCodeWithNullTickets() throws Exception {
        code.setTickets(null);
        Code createdCode = manager.createCode(code);
        assertNotNull(createdCode);
        assertSavedcodeIsCorrect(createdCode, code);

        verify(mockRecordManager, times(1)).createRecord(CodeManager.CODE, targetCode);
    }

    public void buildCode() throws Exception {
        code = new Code();
        code.setCode(SAMPLE_CODE);
        code.setPrice(SAMPLE_PRICE);
        code.setDescription("Sample description");
        code.setStartDate(DateUtil.parseDate("2011-09-09T05:05:03Z"));
        code.setEndDate(DateUtil.parseDate("2011-09-19T05:05:03Z"));
        code.getTickets().add(IdAdapter.toString(sampleTicket.getId()));
        targetCode = code.toRecord();
        targetCodeWithId = code.toRecord();
        targetCodeWithId.setId(SAMPLE_ID);
    }

    public void createSampleObjects() throws Exception {
        sampleTicket.setId("400");
        samplePerformance.setId(SAMPLE_PERFORMANCE_ID);
        sampleTicket.put("performanceId", SAMPLE_PERFORMANCE_ID);
        
        targetTicket.setId(sampleTicket.getId());
        targetTicket.put(SAMPLE_CODE, Integer.toString(SAMPLE_PRICE));
        targetTicket.put("performanceId", SAMPLE_PERFORMANCE_ID);

        buildCode();
    }

    @Before
    public void mockupTix() throws Exception {
        MockitoAnnotations.initMocks(this);
        createSampleObjects();

        when(mockRecordManager.getTicket("ticket", sampleTicket.getId())).thenReturn(sampleTicket);
        when(mockRecordManager.getTicket(CodeManager.CODE, SAMPLE_ID)).thenReturn(targetCodeWithId);
        when(mockRecordManager.updateRecord("ticket", targetTicket)).thenReturn(targetTicket);
        when(mockRecordManager.createRecord(CodeManager.CODE, targetCode)).thenReturn(targetCodeWithId);

        manager.setRecordManager(mockRecordManager);
        manager.setAthenaStage(mockStage);
        manager.setApa(mockApa);
    }

    /*
     * Will match savedcode to targetCode on the following outbound fields:
     * - tickets
     * - id
     * - code
     * - startDate
     * - endDate
     * - price
     * - description
     * - enabled
     */
    private void assertSavedcodeIsCorrect(Code savedCode, Code targetCode) {
        assertEquals(savedCode.getEnabled(), targetCode.getEnabled());
        assertEquals(savedCode.getCode(), targetCode.getCode());
        assertEquals(savedCode.getStartDate(), targetCode.getStartDate());
        assertEquals(savedCode.getEndDate(), targetCode.getEndDate());
        assertEquals(savedCode.getDescription(), targetCode.getDescription());
        assertEquals(savedCode.getPrice(), targetCode.getPrice());
    }
}
