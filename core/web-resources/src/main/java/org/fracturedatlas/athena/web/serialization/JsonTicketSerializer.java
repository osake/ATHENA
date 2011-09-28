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
package org.fracturedatlas.athena.web.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.exception.AthenaException;
import org.fracturedatlas.athena.web.util.JsonUtil;

@Provider
@Produces("application/json")
@Consumes("application/json")
public class JsonTicketSerializer implements MessageBodyWriter<PTicket>,
                                             MessageBodyReader<PTicket>,
                                             JsonSerializer<PTicket>,
                                             JsonDeserializer<PTicket> {

    @Override
    public long getSize(PTicket t, Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType) {
        return (PTicket.class.isAssignableFrom(type));
    }

    @Override
    public void writeTo(PTicket t, Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        out.write(JsonUtil.getGson().toJson(t).getBytes());
    }
    
    @Override
    public boolean isReadable(java.lang.Class<?> type, java.lang.reflect.Type genericType, java.lang.annotation.Annotation[] annotations, MediaType mediaType) {
        return (PTicket.class.isAssignableFrom(type));
    }

    @Override
    public PTicket readFrom(java.lang.Class<PTicket> type, java.lang.reflect.Type genericType, java.lang.annotation.Annotation[] annotations, MediaType mediaType, MultivaluedMap<java.lang.String,java.lang.String> httpHeaders, java.io.InputStream entityStream) {
        
        Gson gson = JsonUtil.getGson();
        PTicket pTicket;
        try{
            pTicket = gson.fromJson(new InputStreamReader(entityStream), PTicket.class);
            if(pTicket == null) {
                throw new AthenaException("Could not create record from JSON request");
            }
        } catch (JsonParseException jpe) {
            jpe.printStackTrace();
            throw new AthenaException("Could not deserialize JSON request", jpe);
        }

        return pTicket;
    }

    /*
     * These methods are used by GSON, called implicitly from writeTo
     */
    public JsonElement serialize(PTicket pTicket, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonPropMap = JsonUtil.mapToJson(pTicket.getProps());
        jsonPropMap.addProperty("id", IdAdapter.toString(pTicket.getId()));
        return jsonPropMap;

    }

    /*
     * These methods are used by GSON, called implicitly from readFrom
     */
    public PTicket deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try{
            PTicket pTicket = new PTicket();
            JsonObject ticketObj = json.getAsJsonObject();
            pTicket.setId(JsonUtil.nullSafeGetAsString(ticketObj.get("id")));
            ticketObj.remove("id");
            for (Entry<String, JsonElement> entry : ticketObj.entrySet()) {
                JsonElement val = entry.getValue();
                if(val.isJsonArray()) {
                    JsonArray jsonArray = val.getAsJsonArray();
                    Iterator<JsonElement> iter = jsonArray.iterator();
                    while(iter.hasNext()) {
                        pTicket.getProps().add(entry.getKey(), iter.next().getAsString());
                    }
                    
                } else if(val.isJsonNull()) {
                    pTicket.put(entry.getKey(), null);
                } else {
                    pTicket.put(entry.getKey(), val.getAsString());
                }
            }
            return pTicket;
        } catch (JsonParseException jpe) {
            jpe.printStackTrace();
            throw jpe;
        }
    }
}
