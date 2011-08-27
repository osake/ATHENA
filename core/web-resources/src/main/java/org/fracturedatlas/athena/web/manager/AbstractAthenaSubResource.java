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

package org.fracturedatlas.athena.web.manager;

import java.util.List;
import java.util.Map;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.web.exception.ObjectNotFoundException;


public class AbstractAthenaSubResource implements AthenaSubResource {

    @Override
    public List<PTicket> find(String parentType,
                                 Object parentId,
                                 String subResourceType,
                                 Map<String, List<String>> queryParams,
                                 String username) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<PTicket> save(String parentType,
                                 Object parentId,
                                 String subResourceType,
                                 Map<String, List<String>> queryParams,
                                 PTicket body,
                                 String username)  throws ObjectNotFoundException {
        throw new UnsupportedOperationException("Not implemented");
    }
}
