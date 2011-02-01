/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fracturedatlas.athena.audit.persist.impl;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import java.util.List;
import org.fracturedatlas.athena.audit.model.AuditMessage;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Calendar;
import org.fracturedatlas.athena.audit.persist.AuditPersistance;

/**
 *
 * @author fintan
 */
public class AuditPersistanceImplTest {

    AuditPersistance auditPersistance;

    public AuditPersistanceImplTest() {
        ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext.xml");
        auditPersistance = (AuditPersistance) context.getBean("auditPersistance");


    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of saveAuditMessage method, of class AuditPersistanceImpl.
     */
    @Test
    public void testGetAndSaveAuditMessage() throws Exception {
        System.out.println("saveAuditMessage");
        AuditMessage am = new AuditMessage();
        am.setDateTime(System.currentTimeMillis());
        am.setUser("Tom");
        am.setAction("Save");
        am.setResource("Ticket:Id:34");
        am.setMessage("TicketProps 5,6,");
        AuditMessage result = auditPersistance.saveAuditMessage(am);
        Long id = (Long)result.getId();

        AthenaSearch athenaSearch = new AthenaSearch();
        athenaSearch.addConstraint("id", Operator.EQUALS, ""+id);
        List<AuditMessage> lam = auditPersistance.getAuditMessages(athenaSearch);
//        AuditMessage expResult = lam.get(0);


//        assertEquals(expResult, result);
    }



}