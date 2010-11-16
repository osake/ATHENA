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
import org.fracturedatlas.athena.apa.exception.ImmutableObjectException;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.PropValue;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.apa.model.TicketProp;
import org.fracturedatlas.athena.search.ApaSearch;

/**
 * The interface from Parakeet to a data store.
 *
 * Exceptions that extend from ApaException are RuntimeExceptions.  They are declared in
 * the method declatations for convenience.  Implementors are <i>strongly encouraged</i> to throw the
 * exceptions where appropriate but they cannot be forced to do so.  Implementors that do not
 * throw exceptions noted in this interface should note that in their documentation.
 *
 * Implementors are free to throw other ApaExceptions where necessary.
 *
 * For methods that take (Object id) as a parameter, implementors are responsible for casting this
 * value to the id format for their implementation.  If the id is of an invalid format, implementors
 * can throw a sensible subclass of RuntimeException.
 *
 */
public interface ApaAdapter {

    /**
     * Get a ticket from the database
     * @param id
     * @return the ticket or null if the ticket is not found
     */
    public Ticket getTicket(String type, Object id);

    /**
     * Save a ticket to the database.  This method can be used to save new tickets and update existing tickets.
     *
     * @param t
     * @return the ticket that was just saved
     * @throws InvalidValueException
     */
    public Ticket saveTicket(Ticket t) throws InvalidValueException;

    /**
     * Delete a ticket from the database.  Implementors should delete all props associated with this ticket.
     * Passing null to this method will return false
     * @param id the id to delete
     * @return true if the delete succeeded, false otherwise
     */
    public Boolean deleteTicket(String type, Object id);

    /**
     * Delete a ticket from the database.  This is a convenience method to call deleteTicket(Object).
     * Any contracts made in deleteTicket(id) should also be enforced here.
     *
     * Passing null to this method will result in a NullPointerException
     *
     * @param t the ticket to delete
     * @return true if the delete succeeded, false otherwise
     */
    public Boolean deleteTicket(Ticket t);

    /**
     * Search for tickets that match all criteria in searchParams.
     *
     * @param searchParams the search criteria.  Criteria should be in the format: key = prop, value = prop value
     * @return matching tickets, empty List if no tickets found
     */
    public Set<Ticket> findTickets(HashMap<String, List<String>> searchParams);

    /**
     * Search for tickets that match all criteria in search.
     *
     * @param searchParams the search criteria.  Criteria should be in the format: key = prop, value = prop value
     * @return matching tickets, empty List if no tickets found
     */
    public Set<Ticket> findTickets(ApaSearch search);

    /**
     * Save the ticketProps contained in this list
     * @param prop the ticketProps to save
     * @return the list of saved ticketProps
     * @throws InvalidValueException if one of the props int he list has been amrked strict and its value is not valid
     */
    public List<TicketProp> saveTicketProps(List<TicketProp> prop) throws InvalidValueException;



    /**
     * Save the ticketProp
     * @param prop the ticketProp to save
     * @return the saved ticketProp
     * @throws InvalidValueException if one of the props int he list has been amrked strict and its value is not valid
     */
    public TicketProp saveTicketProp(TicketProp prop) throws InvalidValueException;

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
     * to make it easier on non-relational databases
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
