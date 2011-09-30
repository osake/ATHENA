/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fracturedatlas.athena.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class AthenaSearchModifiersTest {
    @Test
    public void testModifiers() {
        Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
        List<String> includeVals = new ArrayList<String>();
        includeVals.add("dog");
        queryParams.put("_include", includeVals);
        
        AthenaSearch search = new AthenaSearch(queryParams);
        
        assertEquals(1, search.getIncludes().size());
        assertEquals("dog", search.getIncludes().get(0));
    }
}
