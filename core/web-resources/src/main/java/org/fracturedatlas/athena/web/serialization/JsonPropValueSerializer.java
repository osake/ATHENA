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
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.fracturedatlas.athena.apa.impl.jpa.PropValue;
import org.fracturedatlas.athena.id.IdAdapter;

import org.fracturedatlas.athena.web.util.JsonUtil;

@Provider
@Produces({"application/json"})
public class JsonPropValueSerializer implements MessageBodyWriter<PropValue> {

    @Override
    public long getSize(PropValue value, Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType) {
        return (PropValue.class.isAssignableFrom(type));
    }

    @Override
    public void writeTo(PropValue value, Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        Gson gson = JsonUtil.getGson();
        JsonObject jsonPropValue = new JsonObject();
        IdAdapter ida = new IdAdapter();
        jsonPropValue.addProperty("id", ida.marshal(value.getId()));
        jsonPropValue.addProperty("propValue", value.getPropValue());

        out.write(jsonPropValue.toString().getBytes());
    }
}
