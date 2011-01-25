/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fracturedatlas.athena.audit.model;

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
public class AuditMessageTest {

    public AuditMessageTest() {
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
     * Test of setId method, of class AuditMessage.
     */
    @Test
    public void testId() {
        System.out.println("1");
        String id = "1";
        AuditMessage instance = new AuditMessage();
        instance.setId(id);
        String expResult = "1";
        Object result = instance.getId();

        assertEquals(expResult, result.toString());
    }

    /**
     * Test of getAction method, of class AuditMessage.
     */
    @Test
    public void testAction() {
        System.out.println("getAction");
        AuditMessage instance = new AuditMessage();
        instance.setAction("getAction");
        String expResult = "getAction";
        String result = instance.getAction();
        assertEquals(expResult, result);
    }

 

    /**
     * Test of getMessage method, of class AuditMessage.
     */
    @Test
    public void testMessage() {
        System.out.println("getMessage");
        AuditMessage instance = new AuditMessage();
        instance.setMessage("setMessage");

        String expResult = "setMessage";
        String result = instance.getMessage();
        assertEquals(expResult, result);
    }


    /**
     * Test of getResource method, of class AuditMessage.
     */
    @Test
    public void testResource() {
        System.out.println("getResource");
        AuditMessage instance = new AuditMessage();
        instance.setResource("getResource");
        String expResult = "getResource";
        String result = instance.getResource();
        assertEquals(expResult, result);
    }

 
    /**
     * Test of getUser method, of class AuditMessage.
     */
    @Test
    public void testUser() {
        System.out.println("getUser");
        AuditMessage instance = new AuditMessage();
        instance.setUser("getUser");
        String expResult = "getUser";
        String result = instance.getUser();
        assertEquals(expResult, result);
    }


   
}