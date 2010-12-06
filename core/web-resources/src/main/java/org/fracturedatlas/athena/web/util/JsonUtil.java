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
package org.fracturedatlas.athena.web.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.LongSerializationPolicy;
import java.util.Map;
import java.util.Map.Entry;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.util.date.DateTypeConverter;
import org.fracturedatlas.athena.web.serialization.JsonTicketSerializer;

public class JsonUtil {

    static GsonBuilder gb = new GsonBuilder()
                                .serializeNulls()
                                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                                .registerTypeAdapter(PTicket.class, new JsonTicketSerializer())
                                .registerTypeAdapter(java.util.Date.class, new DateTypeConverter());

    static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            gson = gb.create();
        }

        return gson;
    }
     
    static ObjectMapper mapper;

  
    public static ObjectMapper getMapper() {
        if(mapper == null) {
        	mapper = new ObjectMapper();
        	AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        	// make deserializer use JAXB annotations (only)
        	mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
        	// make serializer use JAXB annotations (only)
        	mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
        }
        return mapper;
    }

    public synchronized static JsonObject mapToJson(Map<String, String> map) {
        JsonObject jsonObject = new JsonObject();
        for(Entry<String, String> entry : map.entrySet()) {
            jsonObject.addProperty(entry.getKey(), entry.getValue());
        }
        return jsonObject;
    }

    public static String nullSafeGetAsString(JsonElement e) {
        if(e == null) {
            return null;
        } else if (e.isJsonNull()) {
            return null;
        } else {
            return e.getAsString();
        }
    }
}
