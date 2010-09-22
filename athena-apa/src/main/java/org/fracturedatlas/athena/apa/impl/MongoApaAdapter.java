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
package org.fracturedatlas.athena.apa.impl;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;
import org.fracturedatlas.athena.apa.AbstractApaAdapter;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.apa.model.PropField;
import org.fracturedatlas.athena.apa.model.PropValue;
import org.fracturedatlas.athena.apa.model.Ticket;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import org.fracturedatlas.athena.apa.exception.ApaException;
import org.fracturedatlas.athena.apa.exception.ImmutableObjectException;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.apa.model.TicketProp;
import org.fracturedatlas.athena.apa.model.ValueType;

public class MongoApaAdapter extends AbstractApaAdapter implements ApaAdapter {

    Logger logger = Logger.getLogger(this.getClass().getName());
    DB db = null;
    DBCollection fields = null;
    DBCollection records = null;

    //This is the mongo name for the props object within a record
    public static final String PROPS_STRING = "props";

    public MongoApaAdapter() throws UnknownHostException {
        Mongo m = new Mongo( "localhost" , 27017 );
        db = m.getDB( "tix" );
        records = db.getCollection("records");
        fields = db.getCollection("fields");
    }

    @Override
    public Ticket getTicket(Object id) {
        BasicDBObject query = new BasicDBObject();
        ObjectId oid = ObjectId.massageToObjectId(id);
        query.put("_id", oid);
        return toRecord(records.findOne(query));
    }

    @Override
    public Ticket saveTicket(Ticket t) {
        BasicDBObject doc = new BasicDBObject();

        Ticket savedTicket = getTicket(t.getId());

        if(savedTicket == null) {
            ObjectId oid = new ObjectId();
            t.setId(oid);
            doc.put("_id", t.getId());
        }

        doc.put("_id", t.getId());
        doc.put("name", t.getName());

        BasicDBObject props = new BasicDBObject();
        for(TicketProp prop : t.getTicketProps()) {
            props.put(prop.getPropField().getName(), prop.getValue());
        }

        doc.put("props", props);

        records.save(doc);

        return t;
    }

    @Override
    public Set<Ticket> findTickets(HashMap<String, String> searchParams) {
        Set<Ticket> tickets = new HashSet<Ticket>();
        BasicDBObject query = new BasicDBObject();

        for(Entry<String, String> entry : searchParams.entrySet()) {
            PropField field = getPropField(entry.getKey());
            if(field != null) {
                //load the field's value type so we can search for it with proper typing
                TicketProp searchProp = field.getValueType().newTicketProp();

                try{
                    searchProp.setValue(entry.getValue());
                } catch (Exception e) {
                    //searching on a param with a bad type (like search a boolean field for "4"
                    //TODO: Handle it
                }

                query.put(PROPS_STRING + "." + entry.getKey(), searchProp.getValue());
            } else {
                //TODO: Serching for field that doesn't exist, ignore?
            }
        }

        DBCursor recordsCursor = records.find(query);
        for(DBObject recordObject : recordsCursor) {
            tickets.add(toRecord(recordObject));
        }
        return tickets;
    }

    /**
     * Because of the method of storage in MongoDB, this method WILL NEVER
     * POPULATE propField.getTicketProps().  IT WILL ALWAYS BE NULL.
     *
     * @param idOrName if the parameter can be massaged to a Mongo id (using ObjectId.massageToObjectId)
     * then it will be used as an id lookup, otherwise it will be used as a name lookup
     * @return
     */
    @Override
    public PropField getPropField(Object idOrName) {
        BasicDBObject query = new BasicDBObject();

        ObjectId objectId = ObjectId.massageToObjectId(idOrName);
        if (objectId != null) {
            query.put("_id", objectId);
        } else {
            query.put("name", (String)idOrName);
        }

        DBObject doc = fields.findOne(query);

        if(doc == null) {
            return null;
        } else {
            PropField propField = new PropField();
            propField.setId(doc.get("_id"));
            propField.setName((String)doc.get("name"));
            propField.setStrict((Boolean)doc.get("strict"));
            propField.setValueType(ValueType.valueOf((String)doc.get("type")));

            List<String> propValues = (List<String>)doc.get("values");
            for(String val : propValues) {
                PropValue propValue = new PropValue();
                propValue.setPropValue(val);
                propField.addPropValue(propValue);
            }

            return propField;
        }
    }

    @Override
    public PropField getPropField(String name) {
        return getPropField((Object)name);
    }

    /**
     * propField.getTicketProps WILL NOT BE SAVED via this method.
     *
     * @param propField
     * @return
     */
    @Override
    public PropField savePropField(PropField propField) {

        //type must be set
        if (propField.getValueType() == null) {
            throw new ApaException("Please specify a value type");
        }

        //strict must be set
        if (propField.getStrict() == null) {
            throw new ApaException("Please specify strict as true or false");
        }

        if (propField.getStrict() && propField.getValueType().equals(ValueType.BOOLEAN)) {
            throw new ApaException("Boolean fields cannot be marked as strict");
        }

        PropField existingField = getPropField(propField.getId());

        if(existingField == null) {
            propField.setId(new ObjectId());
            checkForDuplicatePropField(propField.getName());
        } else {
            checkExists(existingField);
            checkImmutability(propField, existingField);
        }

        BasicDBObject doc = new BasicDBObject();
        doc.put("_id", propField.getId());
        doc.put("name", propField.getName());
        doc.put("strict", propField.getStrict());
        doc.put("type", propField.getValueType().toString());

        Set<String> propValues = new HashSet<String>();

        if(propField.getPropValues() != null) {
            for (PropValue propValue : propField.getPropValues()) {
                if(!propValues.add(propValue.getPropValue())) {
                    throw new ApaException("Cannot save Field [" + propField.getId() + "] because it contains duplicate values of [" + propValue.getPropValue() + "]");
                }
            }
        }

        doc.put("values", propValues);
        fields.save(doc);

        //TODO: This is loading the prop field twice for each 1 save
        return getPropField(propField.getId());
    }

    @Override
    public TicketProp saveTicketProp(TicketProp prop) throws InvalidValueException {
        enforceStrict(prop.getPropField(), prop.getValueAsString());
        enforceCorrectValueType(prop.getPropField(), prop);
        Ticket t = getTicket(prop.getTicket().getId());
        t.addTicketProp(prop);
        saveTicket(t);
        return null;
    }

    @Override
    public Boolean deleteTicket(Object id) {
        Ticket t = getTicket(id);

        if(t == null) {
            return false;
        } else{

            BasicDBObject query = new BasicDBObject();
            ObjectId oid = ObjectId.massageToObjectId(id);
            query.put("_id", oid);
            records.remove(query);
            return true;
        }
    }

    @Override
    public Boolean deleteTicket(Ticket t) {
        return deleteTicket(t.getId());
    }

    @Override
    public Boolean deletePropField(Object id) {
        BasicDBObject query = new BasicDBObject();
        ObjectId oid = ObjectId.massageToObjectId(id);
        query.put("_id", oid);
        fields.remove(query);

        //TODO: Return something sensible
        return true;
    }

    @Override
    public Boolean deletePropField(PropField propField) {
        return deletePropField(propField.getId());
    }

    @Override
    public PropValue savePropValue(PropValue propValue) {
            // TODO Auto-generated method stub
            return null;
    }

    @Override
    public List<PropField> getPropFields() {
        DBCursor cur = fields.find();
        List<PropField> fields = new ArrayList<PropField>();

        while(cur.hasNext()) {
            PropField field = new PropField();
            DBObject doc = (DBObject)cur.next();
            field.setId(doc.get("_id"));
            field.setName((String)doc.get("name"));
            field.setStrict((Boolean)doc.get("strict"));
            field.setValueType(ValueType.valueOf((String)doc.get("type")));
            fields.add(field);
        }

        return fields;
    }

    private Ticket toRecord(DBObject recordObject) {
        Ticket t = null;

        if(recordObject != null) {
            t = new Ticket();
            t.setId(recordObject.get("_id"));
            t.setName((String)recordObject.get("name"));
            DBObject propsObj = (DBObject)recordObject.get("props");
            for(String key : propsObj.keySet()) {
                Object val = propsObj.get(key);
                PropField field = getPropField(key);

                TicketProp ticketProp = field.getValueType().newTicketProp();
                ticketProp.setId(null);
                ticketProp.setPropField(field);

                try {
                    ticketProp.setValue(val);
                } catch (Exception e) {
                    //TODO: This should throw something besides Exception
                    e.printStackTrace();
                }

                ticketProp.setTicket(t);
                t.addTicketProp(ticketProp);
            }
        }

        return t;
    }

    private void checkForDuplicatePropField(String name) throws ApaException {
        PropField duplicate = getPropField(name);
        if (duplicate != null) {
            throw new ApaException("Field [" + name + "] already exists.");
        }
    }

    private void checkExists(PropField propField) throws ApaException {
        if (propField == null) {
            throw new ApaException("Cannot update field with Id [" + propField.getId() + "] because the propField was not found");
        }
    }

    private void checkImmutability(PropField newPropField, PropField oldPropField) throws ImmutableObjectException {
        if (!newPropField.getStrict().equals(oldPropField.getStrict())) {
            throw new ImmutableObjectException("You cannot change the strictness of a field after is has been saved");
        } else if (!newPropField.getValueType().equals(oldPropField.getValueType())) {
            throw new ImmutableObjectException("You cannot change the type of a field after is has been saved");
        }
    }

    private void enforceCorrectValueType(PropField propField, TicketProp prop) throws InvalidValueException {

        propField = getPropField(propField.getId());
        if (!propField.getValueType().newTicketProp().getClass().getName().equals(prop.getClass().getName())) {
            String err = "Value [" + prop.getValueAsString() + "] is not a valid value for the field [" + propField.getName() + "].  ";
            err += "Field is of type [" + propField.getValueType().name() + "].";
            throw new InvalidValueException(err);
        }
    }

    private void enforceStrict(PropField propField, String value) throws InvalidValueException {

        if (propField.getStrict()) {

            //Reload the propField because we <3 Hibernate
            propField = getPropField(propField.getId());
            Collection<PropValue> propValues = propField.getPropValues();
            PropValue targetValue = new PropValue(propField, value);

            //TODO: Should be using a .contains method here or something
            for (PropValue propValue : propValues) {
                if (propValue.getPropValue().equals(value)) {
                    return;
                }
            }
            throw new InvalidValueException("Value [" + value + "] is not a valid value for the strict field [" + propField.getName() + "]");
        }
    }
}