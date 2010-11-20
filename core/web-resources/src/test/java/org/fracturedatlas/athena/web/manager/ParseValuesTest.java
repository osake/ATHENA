/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fracturedatlas.athena.web.manager;

import org.fracturedatlas.athena.web.manager.RecordManager;
import java.util.Set;
import java.util.HashSet;
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
public class ParseValuesTest {

    public ParseValuesTest() {
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

     @Test
    public void parseValuesTest() {
     Set<String> testSet=new HashSet<String>();
     testSet = RecordManager.parseValues("");
     testSet = RecordManager.parseValues("(ABC,BBC)");
     testSet = RecordManager.parseValues("( ABC ,BBC)");
     testSet = RecordManager.parseValues("( \\\"A BC \\\",  \"BBC\")");
     testSet = RecordManager.parseValues("( \"ABC \",BBC)");
     testSet = RecordManager.parseValues("(\" ABC\", BBC\"");


     }

}