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
package org.fracturedatlas.athena.web;

import java.util.Collection;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of AthenaComponent where the target component is bundled together in 
 * the same war file
 */
public class IntimateAthenaComponent implements AthenaComponent {
    
    @Autowired
    RecordManager recordManager;

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    
    public IntimateAthenaComponent() {
        
    }

    /**
     * Get a record.
     *
     * @param type the type of record
     * @param id the id of the record
     * @return the record, null if not found
     */
    public PTicket get(String type, Object id) {
        logger.debug("Called get in IAR");
        return recordManager.getRecord(type, id);
    }

    /**
     * Save a record.  If the record includes an id
     * @param type
     * @param record the record to be saved
     * @return the saved record
     */
    public PTicket save(String type, PTicket record) {
        logger.debug("Called save in IAR");
        if(record.getId() == null) {
            return recordManager.createRecord(type, record);
        } else {
            return recordManager.updateRecord(type, record);
        }
    }

    /**
     * Search for a record.  This method does not yet properly execute athena searches.
     * Qualifiers are not supported.
     *
     * @param type
     * @param athenaSearch
     * @return the records
     */
    public Collection<PTicket> find(String type, AthenaSearch athenaSearch) {
        logger.debug("Called find in IAR");
        return recordManager.findRecords(type, athenaSearch);
    }

    public PTicket invoke(String method, String type, PTicket record) {
        throw new UnsupportedOperationException("Invoke is not allowed on Json components");
    }
}
