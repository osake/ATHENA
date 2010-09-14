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
import java.util.Collection;
import java.util.List;

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

public class MongoApaAdapter extends AbstractApaAdapter implements ApaAdapter {

    static int nextId = 1;
    DB db = null;
    DBCollection dbc = null;
    
    public MongoApaAdapter() throws UnknownHostException {
        Mongo m = new Mongo( "localhost" , 27017 );
        db = m.getDB( "tix" );
        dbc = db.getCollection("tt3");
    }

    public Ticket getTicket(Object id) {
        BasicDBObject query = new BasicDBObject();
        ObjectId oid = ObjectId.massageToObjectId(id);
        System.out.println("Getting, Massaged to " + oid);
        query.put("_id", oid);
        DBCursor cur = dbc.find(query);

        Ticket t = null;
        while(cur.hasNext()) {
            DBObject dbo = cur.next();
            System.out.println(dbo);
            t = new Ticket();
            t.setId(dbo.get("_id"));
            t.setName((String)dbo.get("name"));
        }

        return t;
    }
    
    public Ticket saveTicket(Ticket t) {
        BasicDBObject doc = new BasicDBObject();

        ObjectId oid = new ObjectId();
        t.setId(oid);
        doc.put("_id", t.getId());
        doc.put("name", t.getName());       
        dbc.insert(doc);

        return t;
    }

    public PropField getPropField(Object id) {
        BasicDBObject query = new BasicDBObject();
        System.out.println("Looking for: " + id);
        query.put("_id", ObjectId.massageToObjectId(id));
        DBObject doc = dbc.findOne(query);

        if(doc == null) {
            return null;
        } else {
            PropField propField = new PropField();
            propField.setId(doc.get("_id"));
            propField.setName((String)doc.get("name"));
            propField.setStrict((Boolean)doc.get("strict"));
            return propField;
        }


    }

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
        dbc.save(doc);

        return propField;
    }

	@Override
	public PropValue savePropValue(PropValue propValue) {
		// TODO Auto-generated method stub
		return null;
	}



}
