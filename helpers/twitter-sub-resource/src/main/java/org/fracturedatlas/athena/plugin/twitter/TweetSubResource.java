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

package org.fracturedatlas.athena.plugin.twitter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang.StringUtils;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.web.exception.AthenaException;
import org.fracturedatlas.athena.web.manager.AbstractAthenaSubResource;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TweetSubResource extends AbstractAthenaSubResource {

    @Autowired
    RecordManager recordManager;

    @Override
    public List<PTicket> execute(String parentType, 
                                 Object parentId,
                                 String subResourceType,
                                 Map<String, List<String>> queryParams,
                                 String username) {
        List<PTicket> tweets = new ArrayList<PTicket>();
        String personId = IdAdapter.toString(parentId);

        PTicket person = recordManager.getTicket("person", personId);

        if(person == null) {
            throw new NotFoundException();
        }
        
        ClientConfig cc = new DefaultClientConfig();
        Client c = Client.create(cc);

        //TODO: props file
        WebResource twitter = c.resource("http://api.twitter.com/1/statuses/user_timeline.json" );

        MultivaluedMap twitterQueryParams = new MultivaluedMapImpl();
        String twitterHandle = person.get("twitterHandle");
        
        //Punch out if they have to handle set
        if(StringUtils.isBlank(twitterHandle)) {
            throw new NotFoundException();
        }
        
        twitterQueryParams.add("screen_name", person.get("twitterHandle"));

        ClientResponse response = twitter.queryParams(twitterQueryParams).get(ClientResponse.class);
        
        if(ClientResponse.Status.OK.equals(ClientResponse.Status.fromStatusCode(response.getStatus()))) {
            String jsonResponse = response.getEntity(String.class);
            Gson gson = JsonUtil.getGson();
            JsonParser jsonParser = new JsonParser();
            Iterator<JsonElement> tweetIter = jsonParser.parse(jsonResponse).getAsJsonArray().iterator();

            while(tweetIter.hasNext()) {
                JsonObject tweetObj = tweetIter.next().getAsJsonObject();
                PTicket tweet = new PTicket("tweet");
                tweet.put("text", tweetObj.get("text").getAsString());
                tweets.add(tweet);
            }
            return tweets;
        } else if (ClientResponse.Status.NOT_FOUND.equals(ClientResponse.Status.fromStatusCode(response.getStatus()))) {
            throw new NotFoundException();
        } else {
            throw new AthenaException(response.getEntity(String.class));
        }
        


    }

    public RecordManager getRecordManager() {
        return recordManager;
    }

    public void setRecordManager(RecordManager recordManager) {
        this.recordManager = recordManager;
    }
    

}
