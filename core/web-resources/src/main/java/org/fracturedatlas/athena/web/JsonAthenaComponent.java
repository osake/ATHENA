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

import com.google.gson.Gson;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.*;
import com.sun.jersey.core.impl.provider.entity.Inflector;
import java.util.Arrays;
import java.util.Collection;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.web.util.JsonUtil;

/**
 * Implementation of AthenaComponent so that components can talk to other components over HTTP
 */
public class JsonAthenaComponent implements AthenaComponent {

    WebResource component;
    Client c;
    String uri;

    Gson gson = JsonUtil.getGson();

    public JsonAthenaComponent(String hostname, String port, String componentName) {
        
        //TODO: Use a URL builder
        uri = "http://" + hostname + ":" + port + "/" + componentName + "/";

        ClientConfig cc = new DefaultClientConfig();
        c = Client.create(cc);
    }

    /**
     * Get a record.
     *
     * @param type the type of record
     * @param id the id of the record
     * @return the record, null if not found
     */
    public PTicket get(String type, Object id) {

        //TODO: needs to be cleaned up.  No need for this to create a new
        //resource every time
        component = c.resource(uri);
        
        type = Inflector.getInstance().pluralize(type);
        String json = component.path(type + "/" + id).get(String.class);
        return gson.fromJson(json, PTicket.class);
    }

    /**
     * Save a record.  If the record includes an id, this will post a PUT to the component,
     * otherwise, the opject will be POSTed
     * @param type
     * @param record the record to be saved
     * @return the saved record
     */
    public PTicket save(String type, PTicket record) {

        //TODO: needs to be cleaned up.  No need for this to create a new
        //resource every time
        component = c.resource(uri);

        type = Inflector.getInstance().pluralize(type);
        String jsonResponse;
        String path = type;
        String recordJson = gson.toJson(record);

        if(record.getId() != null) {
            path = "/" + type + "/" + record.getId();
            jsonResponse = component.path(path)
                                    .type("application/json")
                                    .put(String.class, recordJson);
        } else {
            path = "/" + type;
            jsonResponse = component.path(path)
                                    .type("application/json")
                                    .post(String.class, recordJson);
        }

        return gson.fromJson(jsonResponse, PTicket.class);
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
        component = c.resource(uri);
        type = Inflector.getInstance().pluralize(type);
        component = component.path(type + "/");

        for(AthenaSearchConstraint con : athenaSearch.getConstraints()) {
            String val = con.getOper().getOperatorType() + con.getValue();
            component = component.queryParam(con.getParameter(), val);
        }

        String json = component.get(String.class);
        PTicket[] ticketArray = gson.fromJson(json, PTicket[].class);
        return Arrays.asList(ticketArray);
    }

    public PTicket invoke(String method, String type, PTicket record) {
        throw new UnsupportedOperationException("Invoke is not allowed on Json components");
    }
}
