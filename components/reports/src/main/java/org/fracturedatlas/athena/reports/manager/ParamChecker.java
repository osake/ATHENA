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
package org.fracturedatlas.athena.reports.manager;

import java.util.List;
import java.util.Map;
import org.fracturedatlas.athena.web.exception.AthenaException;

public class ParamChecker {
    
    /**
     * If any Strings in requiredParams do not exist in queryParams
     * this method will throw an AthenaException.  
     */
    public static Boolean check(Map<String, List<String>> queryParams, 
                                String... requiredParams) {
        if(requiredParams.length == 0) {
            return true;
        }
        
        boolean hasErrors = false;
        StringBuilder missingParams = new StringBuilder();
        for(int i=0; i<requiredParams.length; i++) {
            if(queryParams.get(requiredParams[i]) == null) {
                missingParams.append("[").append(requiredParams[i]).append("] ");
                hasErrors = true;
            }
        }
        
        if(hasErrors) {
            throw new AthenaException("Missing the following required parameters " + missingParams.toString());
        } else {
            return true;
        }
    }
}
