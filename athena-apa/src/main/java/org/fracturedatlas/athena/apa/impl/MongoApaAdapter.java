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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import org.fracturedatlas.athena.apa.model.TicketProp;
import org.fracturedatlas.athena.apa.model.ValueType;
import org.fracturedatlas.athena.util.date.DateUtil;

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

        ObjectId oid = new ObjectId();
        t.setId(oid);
        doc.put("_id", t.getId());
        doc.put("name", t.getName());  
        
        BasicDBObject props = new BasicDBObject();
        for(TicketProp prop : t.getTicketProps()) {
            if(prop.getPropField().getValueType().equals(ValueType.DATETIME)) {
                props.put(prop.getPropField().getName(), "2010");
            } else {
                props.put(prop.getPropField().getName(), prop.getValue());
            }
        }
        
        doc.put("props", props);
        
        records.insert(doc);

        return t;
    }

    @Override
    public Set<Ticket> findTickets(HashMap<String, String> searchParams) {
        Set<Ticket> tickets = new HashSet<Ticket>();
        BasicDBObject query = new BasicDBObject();

        for(Entry<String, String> entry : searchParams.entrySet()) {
            PropField field = getPropField(entry.getKey());
            if(field != null) {
                if(field.getValueType().equals(ValueType.DATETIME)) {
//                    try{
                        query.put(PROPS_STRING + "." + entry.getKey(), "2010");
//                    } catch (ParseException e) {
//                        logger.info("Searching for a date field ["+ entry.getKey() +"] with an invalid date [" + entry.getValue());
//                        e.printStackTrace();
//                    }
                } else {
                    query.put(PROPS_STRING + "." + entry.getKey(), entry.getValue());
                }
            } else {
                //TODO: Serching for field that doesn't exist, ignore?
            }
        }

        System.out.println(query);
        DBCursor recordsCursor = records.find(query);
        for(DBObject recordObject : recordsCursor) {
            tickets.add(toRecord(recordObject));
        }
        return tickets;
    }

    /**
     * Find a prop field
     * @param idOrName if the parameter can be massaged to a Mongo id (using ObjectId.massageToObjectId)
     * then it will be used as an id lookup, otherwise it will be used as a name lookup
     * @return
     */
    @Override
    public PropField getPropField(String idOrName) {
        BasicDBObject query = new BasicDBObject();
        
        ObjectId objectId = ObjectId.massageToObjectId(idOrName);
        if (objectId != null) {
            query.put("_id", objectId);
        } else {
            query.put("name", idOrName);
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
            return propField;
        }
    }

    @Override
    public PropField getPropField(Object idOrName) {
        return getPropField((String)idOrName);
    }

    @Override
    public PropField savePropField(PropField propField) {

        //TODO: This really should be a stricter check.  Otherwise, caller
        //specify an id in the request and we'll think that their
        //object will have already been persisted to the DB.
        if(propField.getId() == null) {
            propField.setId(new ObjectId());
        }

        BasicDBObject doc = new BasicDBObject();
        doc.put("_id", propField.getId());
        doc.put("name", propField.getName());
        doc.put("strict", propField.getStrict());
        doc.put("type", propField.getValueType().toString());
        fields.save(doc);

        return propField;
    }

    @Override
    public Boolean deleteTicket(Object id) {
        BasicDBObject query = new BasicDBObject();
        ObjectId oid = ObjectId.massageToObjectId(id);
        query.put("_id", oid);
        records.remove(query);

        //TODO: Return something sensible
        return true;
    }

    @Override
    public Boolean deleteTicket(Ticket t) {
        return deleteTicket(t.getId());
    }

    @Override
    public boolean deletePropField(Object id) {
        BasicDBObject query = new BasicDBObject();
        ObjectId oid = ObjectId.massageToObjectId(id);
        query.put("_id", oid);
        fields.remove(query);

        //TODO: Return something sensible
        return true;
    }

    @Override
    public boolean deletePropField(PropField propField) {
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
                String val = (String)propsObj.get(key);
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
}
