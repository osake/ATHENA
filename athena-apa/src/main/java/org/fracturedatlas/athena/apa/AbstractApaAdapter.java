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
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.PropValue;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.TicketProp;

/**
 * Abstract implementation of ApaAdapter as a convenience for developers seeking
 * to implement only a sub-set of features of ApaAdapter.  Also handy during development
 * so you don't need to stub a bunch of methods.
 *
 * If this were Scala, and interfaces were allowed to also define methods, we'd be
 * on the beach drinking rum by now.
 */
public abstract class AbstractApaAdapter implements ApaAdapter {
    
    @Override
    public Ticket getTicket(Object id) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
    
    @Override
    public Ticket saveTicket(Ticket t) throws InvalidValueException {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Boolean deleteTicket(Object id) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Boolean deleteTicket(Ticket t) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void deleteTicketProp(TicketProp prop) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Set<Ticket> findTickets(HashMap<String, String> searchParams) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public TicketProp saveTicketProp(TicketProp tp)  throws InvalidValueException {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public List<TicketProp> saveTicketProps(List<TicketProp> props) throws InvalidValueException {
        throw new UnsupportedOperationException("Unsupported operation");
    }
    
    @Override
    public TicketProp getTicketProp(Object id) {
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
    public TicketProp getTicketProp(String fieldName, Object ticketId) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public boolean deletePropField(Object id) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public boolean deletePropField(PropField propField) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
    
    @Override
    public PropValue getPropValue(Object propValueId) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Collection<PropValue> getPropValues(Object propFieldId) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
    
    @Override
    public boolean deletePropValue(Object id) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public boolean deletePropValue(PropValue propValue) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public List<PropField> getPropFields() {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public PropField savePropField(PropField propField) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

 
}
