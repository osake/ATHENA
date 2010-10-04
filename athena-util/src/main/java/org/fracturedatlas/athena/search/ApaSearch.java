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

package org.fracturedatlas.athena.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 *
 */
public class ApaSearch {

    HashMap<String, List<String>> map = null;

    public ApaSearch() {
        map = new HashMap<String, List<String>>();
    }

    public void addTerm(String fieldName, Operator operator, String searchTerm) {
        List<String> termList = map.get(fieldName);
        if(termList == null) {
            termList = new ArrayList<String>();
        }
        termList.add(operator.getOperatorString() + searchTerm);
        map.put(fieldName, termList);
        System.out.println("MAP: " + map);
    }

    public HashMap<String, List<String>> asMap() {
        return map;
    }
}
