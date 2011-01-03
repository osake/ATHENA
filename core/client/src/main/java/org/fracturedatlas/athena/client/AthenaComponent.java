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

import java.util.Collection;
import org.fracturedatlas.athena.search.AthenaSearch;

/**
 * An interface for one component to talk to another component.  Implementations should be
 * injected in the core component's spring config.  See: tix
 */
public interface AthenaComponent {

    /**
     * Get the record of this type associated with this id
     * @param type the type of the record to get
     * @param id the id of the record to get
     * @return the record if found, null otherwise
     */
    public PTicket get(String type, Object id);

    /**
     * Save a record.  If this record has not been saved, it will be created.
     * @param type the type of the record to save
     * @param record the full body of the record
     * @return the saved record
     */
    public PTicket save(String type, PTicket record);

    /**
     * Perform an AthenaSearch
     * @param type the type of record to search for
     * @param athenaSearch the search to perform
     * @return A colection of records found,  The collection will be empty if no results were found
     */
    public Collection<PTicket> find(String type, AthenaSearch athenaSearch);
    
    /**
     * Invoke a method on an AthenaComponent that isn't handled by default
     * @param method the method to invoke
     * @param type the type of record we're working with
     * @param record the record to invoke method upon
     * @return the result of the method
     */
    public PTicket invoke(String method, String type, PTicket record);
}
