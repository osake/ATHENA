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
package org.fracturedatlas.athena.client;

public class RecordUtil {
    public static Boolean hasCompletePersonInformation(PTicket record) {
        return ( record != null
              && record.get("firstName") != null
              && record.get("lastName") != null
              && record.get("email") != null);
    }
    
    public static Boolean hasAnyPersonInformation(PTicket record) {
        return ( record != null
              && (record.get("firstName") != null
              || record.get("lastName") != null
              || record.get("email") != null));
    }
}
