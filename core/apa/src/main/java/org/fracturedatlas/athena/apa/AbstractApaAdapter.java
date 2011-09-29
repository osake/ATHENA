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
package org.fracturedatlas.athena.apa;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.PropValue;
import org.fracturedatlas.athena.apa.impl.jpa.TicketProp;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractApaAdapter implements ApaAdapter {
    
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    
    @Override
    public PTicket getRecord(String type, Object id) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    /* TODO: Can this be eliminated */
    @Override
    public PTicket saveRecord(String type, PTicket record) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public PTicket saveRecord(PTicket record) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public PTicket patchRecord(Object idToPatch, String type, PTicket patchRecord) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Boolean deleteRecord(String type, Object id) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Boolean deleteRecord(PTicket t) {
        return deleteRecord(t.getType(), t.getId());
    }

    @Override
    public Set<PTicket> findTickets(AthenaSearch search) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Set<String> getTypes() {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void deleteTicketProp(TicketProp prop) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
    
    @Override
    public TicketProp getTicketProp(Object id) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public TicketProp getTicketProp(String fieldName, String type, Object ticketId) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public List<TicketProp> getTicketProps(String fieldName, String type, Object ticketId) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public PropField getPropField(Object id) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public PropField getPropField(String name) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public List<TicketProp> getTicketProps(String fieldName) {
      throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Boolean deletePropField(Object id) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Boolean deletePropField(PropField propField) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Collection<PropField> getPropFields() {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public PropField savePropField(PropField propField) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Collection<PropValue> getPropValues(Object propFieldId) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void deletePropValue(PropValue propValue) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
    
    @Override
    public void deletePropValue(Object propFieldId, Object propValueId) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
    
    @Override
    public PropValue savePropValue(PropValue propValue) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Set<PTicket> loadRelationships(Set<PTicket> records, List<String> relationships) {
        for(PTicket record : records) {
            record = loadRelationships(record, relationships);
        }
            
        return records;
    }
    
    @Override
    public PTicket loadRelationships(PTicket record, List<String> relationships) {
        if(record == null) {
            return record;
        }
        
        for(String relationship : relationships) {
            String id = record.get(relationship + "Id");
            logger.debug("Looking for [{}] with id [{}]", relationship, id);
            PTicket child = getRecord(relationship, id);
            record.putRecord(relationship, child);
        }
        
        return record;
    }
    
}
