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
import java.util.List;
import java.util.Set;
import org.fracturedatlas.athena.apa.exception.ImmutableObjectException;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.PropValue;
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;
import org.fracturedatlas.athena.apa.impl.jpa.TicketProp;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;

/**
 * The interface from Parakeet to a data store.
 *
 * Exceptions that extend from ApaException are RuntimeExceptions.  (TODO) They are declared in
 * the method declatations for convenience.  Implementors are <i>strongly encouraged</i> to throw the
 * exceptions where appropriate but they cannot be forced to do so.  Implementors that do not
 * throw exceptions noted in this interface should note that in their documentation.
 *
 * Implementors are free to throw other ApaExceptions where necessary.
 *
 * For methods that take (Object id) as a parameter, implementors are responsible for casting this
 * value to the id format for their implementation.  If the id is of an invalid format, implementors
 * can throw a sensible subclass of ApaException.
 *
 */
public interface ApaAdapter {

    /**
     * Get a record from the data store
     * @param id the id of the record
     * @return the record or null if the record is not found
     */
    public PTicket getRecord(String type, Object id);

    /**
     * Save a record to the data store.  If the record exists, its props will be overwritten
     * by props contained in record.  Props that exist on the existing record and not on the incoming record will be deleted.
     *
     * @param record
     * @return the saved record
     * @throws InvalidValueExcepion if this record contains a field/value pairing that is invalid
     */
    public PTicket saveRecord(PTicket record);

    /**
     * Save a record to the data store
     * @param record
     * @return the saved record
     * @throws InvalidValueExcepion if this record contains a field/value pairing that is invalid
     */
    public PTicket saveRecord(String type, PTicket record);

    /**
     * Update a record identified by idToPAtch with the props contained in patchRecord.
     * Props not included in patchRecord will be unchanged
     *
     * If patchRecord is null or empty, implementors must return the existing record unchanged
     *
     * @param patchRecord
     * @return the saved record
     * @throws InvalidValueExcepion if this record contains a field/value pairing that is invalid
     */
    public PTicket patchRecord(Object idToPatch, String type, PTicket patchRecord);

    /**
     * Delete a ticket from the data store.  Implementors should delete all props associated with this ticket.
     * Passing null to this method will return false
     * @param id the id to delete
     * @return true if the delete succeeded, false otherwise
     */
    public Boolean deleteRecord(String type, Object id);

    /**
     * Delete a ticket from the data store.  This is a convenience method to call deleteTicket(type, id).
     * Any contracts made in deleteTicket(type, id) should also be enforced here.
     *
     * Passing null to this method will result in a NullPointerException
     *
     * @param t the ticket to delete
     * @return true if the delete succeeded, false otherwise
     */
    public Boolean deleteRecord(PTicket t);

    /**
     * Search for tickets that match all criteria in search.
     *
     * Type must be specified (and if type is not provided, throw an ApaException), but search constraints may be empty.  This method must honor start, end, and limit modifiers.
     *
     * Implementors may decide to not allow searching if the underlying type is not conducive to seearching (either for performance or otherwise)
     * One example is ValueText.TEXT.  In a RDBMS, TEXT is usually mapped to TEXT, CLOB, or LONG VARCHAR types which are not
     * usually allowed to be compared.
     *
     * @param searchParams the search criteria.  Criteria should be in the format: key = prop, value = prop value
     * @return matching tickets, empty List if no tickets found
     */
    public Set<PTicket> findTickets(AthenaSearch search);
    
    /**
     * Return a Set of distinct record types that are currently stored
     * @returna Set of distinct types
     */
    public Set<String> getTypes();

    /**
     * get the ticketProp for the given id
     * @param id the id
     * @return the ticket prop if found, null otherwise
     */
    public TicketProp getTicketProp(Object id);


    /**
     * get the ticketProp for the given ticket and field name
     * @param id the id
     * @return the ticket prop if found, null otherwise
     */
    public TicketProp getTicketProp(String fieldName, String type, Object ticketId);
    public List<TicketProp> getTicketProps(String fieldName, String type, Object ticketId);

    /**
     * get the ticketProps for the given field name
     * @param id the id
     * @return List of the ticket prop if found, null otherwise
     */
    public List<TicketProp> getTicketProps(String fieldName);

    /**
     * Get a PropField
     * @param id
     * @return the PropField with this id, null otherwise
     */
    public PropField getPropField(Object id);

    /**
     * Get a PropField
     * @param name
     * @return the PropField with this name, null otherwise
     */
    public PropField getPropField(String name);

    /**
     * Save the propfield.
     *
     * This method should guarantee that propField.getPropValues() will not be null.  If there are no values
     * then propField.getPropValues() will be empty.
     *
     * Boolean PropFields cannot be marked as strict.
     *
     * This method should prevent a PropField from having duplicate PropValues.  If duplicates are detected,
     * implementors should throw an ApaException and fail to save the object.
     *
     * @param propField
     * @return the saved PropField
     * @throws ImmutableObjectException Once saved, a propField cannot change its ValueType or Strictness
     */
    public PropField savePropField(PropField propField);

    /**
     * Delete a PropField
     * @param propField
     * @return the saved propField
      * @return true if propField is deleted. False if propField does not exist
     */
    public Boolean deletePropField(PropField propField);

    /**
     * Delete a PropField
     * @param propField
     * @return the saved propField
      * @return true if propField is deleted. False if propField does not exist
     */
    public Boolean deletePropField(Object id);

    /*
     * This may need to be refactored to (Ticket t, TicketProp ticketProp)
     * to make it easier on non-relational data stores
     */
    public void deleteTicketProp(TicketProp prop);

    /**
     * get a List of propFields
     * @param propField
     * @return the full List of PropFields
     */
    public Collection<PropField> getPropFields();

    /**
     * Save a PropValue.  PropValue.propField must be set before calling this method
     * 
     * @param propValue
     * @return the saved PropValue
     */
    public PropValue savePropValue(PropValue propValue);

    /**
     * get a List of propFields
     * @param propField
     * @return the full List of PropFields
     */
    public Collection<PropValue> getPropValues(Object propFieldId);

    /**
     * Delete a PropValue
     * @param propValue
      * @return true if propValue is deleted. False if propValue does not exist
    */
    public void deletePropValue(PropValue propValue);

    /**
     * Delete a PropValue
     * @param propValue
     * @return the saved PropValue
      * @return true if propValue is deleted. False if propValue does not exist
     */
    public void deletePropValue(Object propFieldId, Object propValueId);

}
