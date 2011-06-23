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
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.apa.impl.jpa.PropField;
import org.fracturedatlas.athena.apa.impl.jpa.PropValue;
import org.fracturedatlas.athena.apa.impl.jpa.JpaRecord;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.fracturedatlas.athena.apa.IndexingApaAdapter;
import org.slf4j.Logger;
import org.fracturedatlas.athena.apa.exception.ApaException;
import org.fracturedatlas.athena.apa.exception.ImmutableObjectException;
import org.fracturedatlas.athena.apa.exception.InvalidFieldException;
import org.fracturedatlas.athena.apa.exception.InvalidPropException;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.apa.impl.jpa.TicketProp;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.slf4j.LoggerFactory;

public class MongoApaAdapter extends IndexingApaAdapter implements ApaAdapter {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    DB db = null;
    DBCollection fields = null;
    String fieldsCollectionName = null;

    //This is the mongo name for the props object within a record
    public static final String PROPS_STRING = "props";

    public MongoApaAdapter(String host,
                           Integer port,
                           String dbName,
                           String fieldsCollectionName) throws UnknownHostException {
        this.fieldsCollectionName = fieldsCollectionName;
        Mongo m = new Mongo(host, port);
        db = m.getDB(dbName);
        fields = db.getCollection(fieldsCollectionName);
        
        initializeIndex();
    }

    public MongoApaAdapter(String host,
                           Integer port,
                           String dbName,
                           String fieldsCollectionName,
                           String user,
                           String pass) throws UnknownHostException {
        this.fieldsCollectionName = fieldsCollectionName;
        Mongo m = new Mongo(host, port);
        db = m.getDB(dbName);
        db.authenticate(user, pass.toCharArray());
        fields = db.getCollection(fieldsCollectionName);
        
        initializeIndex();
    }

    public PTicket getRecord(String type, Object id) {
        return toRecord(getRecordDocument(new BasicDBObject(), type, ObjectId.massageToObjectId(id)), true);
    }

    public PTicket getRecord(String type, Object id, Boolean includeProps) {
        return toRecord(getRecordDocument(new BasicDBObject(), type, ObjectId.massageToObjectId(id)), includeProps);
    }

    @Override
    public Set<String> getTypes(){
        Set <String> types = new HashSet<String>();
        Set <String> outTypes = new HashSet<String>();
        types = db.getCollectionNames();
        types.remove(fieldsCollectionName);
        for(String type : types) {
            if(!type.startsWith("system.")) {
                outTypes.add(type);
            }
        }
        return types;
    }

    @Override
    public PTicket saveRecord(PTicket t) {
        BasicDBObject doc = new BasicDBObject();

        PTicket savedTicket = getRecord(t.getType(), t.getId());

        if(savedTicket == null) {
            ObjectId oid = new ObjectId();
            t.setId(oid.toString());
        }

        doc.put("_id", ObjectId.massageToObjectId(t.getId()));
        doc.put("type", t.getType());

        BasicDBObject props = new BasicDBObject();
        for(String key : t.getProps().keySet()) {
            List<String> vals = t.getProps().get(key);
            List<Object> properlyTypedVals = new ArrayList<Object>();
            if(vals.size() > 1) {
                for(String val : vals) {
                    enforceCorrectValueType(key, val);
                    properlyTypedVals.add(stringToType(key, val));
                }
                props.put(key, properlyTypedVals);
            } else {
                enforceCorrectValueType(key, vals.get(0));
                props.put(key, stringToType(key, vals.get(0)));
            }
        }

        for(String key : t.getSystemProps().keySet()) {
            for(String val : t.getSystemProps().get(key)) {
                enforceCorrectValueType(key, (String)val);
                props.put(key, stringToType(key, (String)val));
            }
        }

        doc.put("props", props);

        db.getCollection(t.getType()).save(doc);
        addToIndex(t);

        return t;
    }
    
    private Boolean enforceCorrectValueType(String key, String value) {
        //hacky, but this will throw an InvalidValueException if it doesn't work
        //so that's good for us
        PropField propField = getPropField(key);

        if(propField == null) {
            throw new InvalidPropException("Field with name [" + key + "] does not exist");
        }

        TicketProp ticketProp = propField.getValueType().newTicketProp();
        ticketProp.setPropField(propField);
        ticketProp.setValue(value);
        
        return true;
    }
    
    @Override
    public PTicket patchRecord(Object idToPatch, String type, PTicket patchRecord) {
        PTicket existingRecord = getRecord(type, idToPatch);
        
        if(existingRecord == null) {
            throw new ApaException("Record with id ["+idToPatch+"] and type ["+ type +"] was not found to patch");
        }
        if(patchRecord == null) {
            return existingRecord;
        }
        
        for(String key : patchRecord.getProps().keySet()) {
            existingRecord.put(key, patchRecord.get(key));
        }
        return saveRecord(existingRecord);
        
    }

    /**
     * Casts a value from a PTicket to proper typing
     * @param val
     * @return the val with proper type
     */
    public Object stringToType(String key, String val) {
        PropField field = getPropField(key);
        TicketProp searchProp = field.getValueType().newTicketProp();
        searchProp.setValue(val);
        return searchProp.getValue();
    }

    /**
     * Casts a value from a PTicket to proper typing
     * @param val
     * @return the val with proper type
     */
    public String coerceToClientTicketValue(String key, Object val) {
        System.out.println("***************************************");
        System.out.println(key);
        PropField field = getPropField(key);
        System.out.println(field);
        TicketProp searchProp = field.getValueType().newTicketProp();
        searchProp.setValue(val);
        return searchProp.getValueAsString();
    }

    @Override
    public Set<PTicket> findTickets(AthenaSearch athenaSearch) {
        if(athenaSearch.getType() == null) {
            throw new ApaException("You must specify a record type when doing a search");
        }

        Set<PTicket> tickets = new HashSet<PTicket>();
        DBObject currentQuery = new BasicDBObject();

        if("0".equals(athenaSearch.getSearchModifiers().get(AthenaSearch.LIMIT))) {
            return tickets;
        }

        if(athenaSearch.isQuerySearch()) {
            Set<Object> ids = searchIndex(athenaSearch);
            for(Object id : ids) {
                PTicket ticket = getRecord(athenaSearch.getType(), id);
                if(ticket != null) {
                    tickets.add(ticket);
                } else {
                    logger.error("Found an id [{}] in the index that wasn't persisted in the DB", id);
                }
            }
            return tickets;
        }
        
        for(AthenaSearchConstraint constraint : athenaSearch.getConstraints()) {
            PropField field = getPropField(constraint.getParameter());
            if(field != null) {
                
                if(field.getValueType().equals(ValueType.TEXT)) {
                    throw new ApaException("Cannot search on a TEXT field");
                }
                
                //load the field's value type so we can apaSearch for it with proper typing
                TicketProp searchProp = field.getValueType().newTicketProp();

                try{
                    searchProp.setValue(constraint.getValue());
                } catch (Exception e) {
                    //searching on a param with a bad type (like apaSearch a boolean field for "4"
                    //TODO: Handle it
                }

                buildMongoQuery(currentQuery, PROPS_STRING + "." + field.getName(), constraint.getOper(), searchProp.getValue());
            } else {
                throw new InvalidFieldException("No Property Field called " + constraint.getParameter() + " exists.");
            }
        }

        DBCursor recordsCursor = db.getCollection(athenaSearch.getType()).find(currentQuery);
        recordsCursor = setLimit(recordsCursor, athenaSearch.getSearchModifiers().get(AthenaSearch.LIMIT));
        recordsCursor = setSkip(recordsCursor, athenaSearch.getSearchModifiers().get(AthenaSearch.START));

        for(DBObject recordObject : recordsCursor) {
            try {
                tickets.add(toRecord(recordObject));
            } catch (ApaException ae) {
                logger.error("ApaException when converting search result to record, skipping");
            }
        }
        return tickets;
    }
    
    /**
     * Takes an athena operator and returns a MOngo operator
     * @param o the Athena operator
     * @return the mongo operator
     */
    public static void buildMongoQuery(DBObject currentQuery, String field, Operator o, Object value) {
        DBObject queryValue = (DBObject)currentQuery.get(field);
        if(queryValue == null) {
            queryValue = new BasicDBObject();
        }
        
        switch (o) {
            case EQUALS:
                currentQuery.put(field, value);
                return;
            case MATCHES:
                queryValue.put("$regex",value);
                currentQuery.put(field, queryValue);
                return;
            case GREATER_THAN:
                queryValue.put("$gt",value);
                currentQuery.put(field, queryValue);
                return;
            case LESS_THAN:
                queryValue.put("$lt",value);
                currentQuery.put(field, queryValue);
                return;
            case IN:
                queryValue.put("$in",value);
                currentQuery.put(field, queryValue);
                return;

        }
        throw new UnsupportedOperationException("Can't translate search operator [" + o.toString() + "]");
    }
    
    private DBCursor setLimit(DBCursor recordsCursor, String limit) {
        if(limit != null) {
            try{
                Integer lim = Integer.parseInt(limit);
                recordsCursor.limit(lim);
            } catch (NumberFormatException nfe) {
                //ignored, no limit will be set
            }            
        }

        return recordsCursor;
    }

    private DBCursor setSkip(DBCursor recordsCursor, String start) {
        if(start != null) {
            try{
                Integer st = Integer.parseInt(start);
                recordsCursor.skip(st);
            } catch (NumberFormatException nfe) {
                //ignored, no limit will be set
            }
        }

        return recordsCursor;
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
            PropField propField = toField(doc);
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
                //TODO: There's a weird case here with propField.getPropValues is a list with null in it.  Check for that here
                //Someday, fix this conditios
                if(propValue != null && !propValues.add(propValue.getPropValue())) {
                    throw new ApaException("Cannot save Field [" + propField.getId() + "] because it contains duplicate values of [" + propValue.getPropValue() + "]");
                }
            }
        }

        doc.put("values", propValues);
        fields.save(doc);

        //TODO: This is loading the prop field twice for each 1 save
        return getPropField(propField.getId());
    }

    private TicketProp saveTicketProp(TicketProp prop) throws InvalidValueException {
        enforceStrict(prop.getPropField(), prop.getValueAsString());
        enforceCorrectValueType(prop.getPropField(), prop);
        PTicket t = getRecord(prop.getTicket().getType(), prop.getTicket().getId());
        t.put(prop.getPropField().getName(), prop.getValueAsString());
        saveRecord(t);
        return null;
    }

    public Boolean deleteRecord(String type, Object id) {
        PTicket t = getRecord(type, id);

        if(t == null) {
            return false;
        } else{

            BasicDBObject query = new BasicDBObject();
            ObjectId oid = ObjectId.massageToObjectId(id);
            query.put("_id", oid);
            db.getCollection(type).remove(query);
            deleteFromIndex(id);
            return true;
        }
    }

    public Boolean deleteTicket(JpaRecord t) {
        return deleteRecord(t.getType(), t.getId());
    }

    @Override
    public Boolean deletePropField(Object id) {
        BasicDBObject query = new BasicDBObject();
        ObjectId oid = ObjectId.massageToObjectId(id);
        query.put("_id", oid);
        fields.remove(query);

        return true;
    }

    @Override
    public Boolean deletePropField(PropField propField) {
        return deletePropField(propField.getId());
    }

    @Override
    public PropValue savePropValue(PropValue propValue) {
        PropField field = getPropField(propValue.getPropField().getId());
        field.addPropValue(propValue);
        savePropField(field);
        propValue.setId(propValue.getPropValue());
        return propValue;
    }

    @Override
    public void deletePropValue(Object propFieldId, Object propValueId) {
        PropField propField = getPropField(propFieldId);
        if(propField != null) {
            Collection<PropValue> propValues = propField.getPropValues();
            Collection<PropValue> outValues = new ArrayList<PropValue>();
            for(PropValue value : propValues) {
                //remember in the Mongo adapter, valueId=valueValue
                if(IdAdapter.isEqual(propValueId, value.getPropValue())) {
                    propField.getPropValues().remove(value);
                    break;
                }
            }

            propField.setPropValues(outValues);
            savePropField(propField);
        }
    }

    @Override
    public List<PropField> getPropFields() {
        DBCursor cur = fields.find();
        List<PropField> fields = new ArrayList<PropField>();

        while(cur.hasNext()) {
            DBObject doc = (DBObject)cur.next();
            fields.add(toField(doc));
        }

        return fields;
    }

    @Override
    public TicketProp getTicketProp(String fieldName, String type, Object ticketId) {
        TicketProp ticketProp = null;
        DBObject recordDoc = getRecordDocument(new BasicDBObject(), type, ObjectId.massageToObjectId(ticketId));

        if(recordDoc != null) {
            DBObject propsObj = (DBObject)recordDoc.get("props");
            if(propsObj.containsField(fieldName)) {
                PropField field = getPropField(fieldName);

                ticketProp = field.getValueType().newTicketProp();
                ticketProp.setId(null);
                ticketProp.setPropField(field);

                try {
                    ticketProp.setValue(propsObj.get(fieldName));
                } catch (Exception e) {
                    //TODO: This should throw something besides Exception
                    logger.error(e.getMessage(),e);
                }

                JpaRecord hugeWorkaround = new JpaRecord(type);
                hugeWorkaround.setId(ticketId);
                ticketProp.setTicket(hugeWorkaround);
            }
        }

        return ticketProp;
    }

    @Override
    public void deleteTicketProp(TicketProp prop) {
        
        if(prop.getTicket() != null) {
            DBObject recordDoc = getRecordDocument(new BasicDBObject(), prop.getTicket().getType(), ObjectId.massageToObjectId(prop.getTicket().getId()));
            String fieldName = prop.getPropField().getName();
            if(recordDoc != null) {
                DBObject propsObj = (DBObject)recordDoc.get("props");
                if(propsObj.containsField(fieldName)) {
                    propsObj.removeField(fieldName);
                }
                recordDoc.put("props", propsObj);
                db.getCollection(prop.getTicket().getType()).save(recordDoc);
            }
        }
    }

    @Override
    public Collection<PropValue> getPropValues(Object propFieldId) {
        Collection<PropValue> propValues = null; 
        
        PropField field = getPropField(propFieldId);
        if(field != null) {
            propValues = field.getPropValues();
        } else {
            propValues = new ArrayList<PropValue>();
        }
        
        return propValues;
    }

    private BasicDBObject buildTicketQuery(ObjectId oid) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", oid);
        return query;
    }

    private DBObject getRecordDocument(BasicDBObject query, String type, ObjectId oid) {
        query.put("_id", oid);
        return db.getCollection(type).findOne(query);
    }

    private PTicket toRecord(DBObject recordObject) {
        return toRecord(recordObject, true);
    }

    private PTicket toJpaRecord(DBObject recordObject) {
        return toRecord(recordObject, true);
    }

    private PTicket toRecord(DBObject recordObject, Boolean includeProps) {
        PTicket t = null;

        if(recordObject != null) {
            try{
                t = new PTicket();
                t.setId(recordObject.get("_id").toString());
                t.setType((String)recordObject.get("type"));

                if(includeProps) {
                    DBObject propsObj = (DBObject)recordObject.get("props");
                    for(String key : propsObj.keySet()) {
                        Object val = propsObj.get(key);
                        if(key.contains(":")) {
                            t.getSystemProps().putSingle(key, coerceToClientTicketValue(key, val));
                        } else {
                            //t.put(key, coerceToClientTicketValue(key, val));
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Problem converting document to record");
                logger.error("This does not appear to be an Athena Record object:");
                logger.error("{}", e);
                logger.error("{}", recordObject);
                throw new ApaException(e.getMessage());
            }
        }

        return t;
    }

    private PropField toField(DBObject fieldObject) {
        PropField propField = new PropField();
        propField.setId(fieldObject.get("_id"));
        propField.setName((String)fieldObject.get("name"));
        propField.setStrict((Boolean)fieldObject.get("strict"));
        propField.setValueType(ValueType.valueOf((String)fieldObject.get("type")));

        List<String> propValues = (List<String>)fieldObject.get("values");
        for(String val : propValues) {
            PropValue propValue = new PropValue();
            propValue.setId(val);
            propValue.setPropValue(val);
            propField.addPropValue(propValue);
        }

        return propField;
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
