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
package org.fracturedatlas.athena.callbacks;

import org.fracturedatlas.athena.client.PTicket;

public class AbstractAthenaCallback implements AthenaCallback {
   
    @Override
    public PTicket beforeSave(String type, PTicket record) {
        throw new UnsupportedOperationException("beforeSave is not implemented in this callback");
    }
    
    @Override
    public PTicket afterSave(String type, PTicket record) {
        throw new UnsupportedOperationException("afterSave is not implemented in this callback");
    }
}
