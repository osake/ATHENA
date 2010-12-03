/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fracturedatlas.athena.audit.manager;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import org.fracturedatlas.athena.audit.model.AuditMessage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fintan
 */
public class AuditManagerTest {

    AuditManager auditManager;

    public AuditManagerTest() {
          ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext.xml");
          auditManager = (AuditManager) context.getBean("auditManager");

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
     * Test of saveAuditMessage method, of class AuditManager.
     */
    @Test
    public void testSaveAuditMessage() throws Exception {
        System.out.println("saveAuditMessage");
        AuditMessage auditMessage = new AuditMessage("Tom", "delete", "file", "so what");
        auditManager.saveAuditMessage(auditMessage);

    }

    /**
     * Test of getAuditMessages method, of class AuditManager.
     */
    @Test
    public void testGetAuditMessages() {
        System.out.println("getAuditMessages");
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("User", "eqTom");
        List expResult = null;
        List<AuditMessage> result = auditManager.getAuditMessages(queryParams);
        assertTrue(result.size()>0);
        for (AuditMessage auditMessage: result) {
          assertTrue(auditMessage.getUser().equals("Tom"));
        }
    }


}