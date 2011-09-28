/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fracturedatlas.athena.client;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PTicketTest {

    @Test
    public void testPutRecord() {
        PTicket one = new PTicket();
        one.setType("parent");
        one.setId(40000);
        
        PTicket two = new PTicket();
        two.setType("child");
        two.setId(22222);
        
        one.putRecord("child", two);
        
        PTicket newTwo = one.getRecord("child");
        assertEquals(two.getIdAsString(), newTwo.getIdAsString());
                
    }
}
