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

package org.fracturedatlas.athena.client;

import com.google.gson.Gson;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.*;
import java.util.Collection;
import org.fracturedatlas.athena.search.AthenaSearch;

public class JsonAthenaComponent implements AthenaComponent {

    WebResource component;

    public JsonAthenaComponent(String hostname, String port, String componentName, String apiKey) {
        String uri = "http://" + hostname + ":" + port + "/" + componentName + "/";

        ClientConfig cc = new DefaultClientConfig();
        Client c = Client.create(cc);
        component = c.resource(uri);
        component.addFilter(new LoggingFilter());
    }

    public PTicket get(String type, Object id) {
        String json = component.path(type).get(String.class);
        return new PTicket();
    }

    public Collection<PTicket> find(AthenaSearch athenaSearch) {
        throw new UnsupportedOperationException();
    }
}
