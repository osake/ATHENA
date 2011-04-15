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

package org.fracturedatlas.athena.helper.codes.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.fracturedatlas.athena.helper.codes.model.Code;
import org.fracturedatlas.athena.web.util.JsonUtil;

@Provider
@Produces({"application/json"})
public class AthenaCodeSerializer implements MessageBodyWriter<Code>, MessageBodyReader<Code> {

    @Override
    public long getSize(Code t, Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType) {
        //TODO: Do this
        return -1L;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType) {
        return (Code.class.isAssignableFrom(type));
    }

    @Override
    public boolean isReadable(java.lang.Class<?> type, java.lang.reflect.Type genericType, java.lang.annotation.Annotation[] annotations, MediaType mediaType) {
        return (Code.class.isAssignableFrom(type));
    }

    @Override
    public Code readFrom(java.lang.Class<Code> type, java.lang.reflect.Type genericType, java.lang.annotation.Annotation[] annotations, MediaType mediaType, MultivaluedMap<java.lang.String,java.lang.String> httpHeaders, java.io.InputStream entityStream) {
        Gson gson = JsonUtil.getGson();
        try{
            Code tran = gson.fromJson(new InputStreamReader(entityStream), Code.class);
            return tran;
        } catch (JsonParseException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void writeTo(Code t, Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        Gson gson = JsonUtil.getGson();
        out.write(gson.toJson(t).getBytes());
    }

}
