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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.LongSerializationPolicy;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.List;
import java.util.Map;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.util.date.DateTypeConverter;
import org.fracturedatlas.athena.util.date.DateTimeTypeConverter;
import org.fracturedatlas.athena.web.serialization.JsonTicketSerializer;

public class JsonUtil {

    static GsonBuilder gb = new GsonBuilder()
                                .serializeNulls()
                                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                                .registerTypeAdapter(PTicket.class, new JsonTicketSerializer())
                                .registerTypeAdapter(java.util.Date.class, new DateTypeConverter())
                                .registerTypeAdapter(org.joda.time.DateTime.class, new DateTimeTypeConverter());

    static GsonBuilder gbWithoutNulls = new GsonBuilder()
                                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                                .registerTypeAdapter(PTicket.class, new JsonTicketSerializer())
                                .registerTypeAdapter(java.util.Date.class, new DateTypeConverter())
                                .registerTypeAdapter(org.joda.time.DateTime.class, new DateTimeTypeConverter());

    static Gson gson;
    static Gson gsonWithoutNulls;

    public static Gson getGson() {
        if (gson == null) {
            gson = gb.create();
        }

        return gson;
    }

    public static Gson getGsonWithoutNulls() {
        if (gsonWithoutNulls == null) {
            gsonWithoutNulls = gbWithoutNulls.create();
        }

        return gsonWithoutNulls;
    }

    public synchronized static JsonObject recordMapToJson(JsonObject jsonMap, Map<String, PTicket> map) {
        for(String key : map.keySet()) {
            PTicket record = map.get(key);
            if(record == null) {
                continue;
            }
            JsonObject recordJson = mapToJson(record.getProps());
            recordJson.addProperty("id", IdAdapter.toString(record.getId()));
            jsonMap.add(key, recordJson);
        }
        return jsonMap;
    }

    public synchronized static JsonObject mapToJson(MultivaluedMapImpl map) {
        JsonObject jsonObject = new JsonObject();
        for(String key : map.keySet()) {
            List<String> valueList = map.get(key);
            if(valueList.size() < 2) {
                jsonObject.addProperty(key, valueList.get(0));
            } else {
                JsonArray jsonArray = new JsonArray();
                for(String val : valueList) {
                    jsonArray.add(new JsonPrimitive(val));
                }
                jsonObject.add(key, jsonArray);
            }
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
