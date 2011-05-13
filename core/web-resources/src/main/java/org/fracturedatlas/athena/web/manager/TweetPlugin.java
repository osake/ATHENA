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

package org.fracturedatlas.athena.web.manager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class TweetPlugin implements AthenaPlugin {

    @Autowired
    RecordManager recordManager;

    @Override
    public PTicket execute(UriInfo uriInfo) {        
        String[] pathBits = uriInfo.getAbsolutePath().getPath().split("/");
        String personId = pathBits[3];

        PTicket person = recordManager.getTicket("person", personId);
        
        ClientConfig cc = new DefaultClientConfig();
        Client c = Client.create(cc);
        WebResource twitter = c.resource("http://api.twitter.com/1/statuses/user_timeline.json" );
        //twitter.addFilter(new LoggingFilter());

        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("screen_name", person.get("twitterHandle"));

        String jsonResponse = twitter.queryParams(queryParams).get(String.class);
        
        Gson gson = JsonUtil.getGson();
        JsonParser jsonParser = new JsonParser();
        Iterator<JsonElement> tweetIter = jsonParser.parse(jsonResponse).getAsJsonArray().iterator();

        PTicket twitterFeed = new PTicket();
        while(tweetIter.hasNext()) {
            JsonObject tweetObj = tweetIter.next().getAsJsonObject();
            twitterFeed.getProps().add("tweets", tweetObj.get("text"));
        }

        return twitterFeed;
    }
}
