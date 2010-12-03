/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fracturedatlas.athena.audit.serializer;

import org.fracturedatlas.athena.audit.model.AuditMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.LongSerializationPolicy;
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
import org.fracturedatlas.athena.id.IdAdapter;

/**
 *
 * @author fintan
 */
public class AuditMessageSerializer implements MessageBodyWriter<AuditMessage> {

    @Override
    public long getSize(AuditMessage value, Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType) {
        //TODO: Do this
        return -1L;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType) {
        return (AuditMessage.class.isAssignableFrom(type));
    }

    @Override
    public void writeTo(AuditMessage value, Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        JsonObject jsonPropValue = new JsonObject();
        IdAdapter ida = new IdAdapter();
        jsonPropValue.addProperty("id", ida.marshal(value.getId()));
        jsonPropValue.addProperty("dateTime", value.getDateTime());
        jsonPropValue.addProperty("User", value.getUser());
        jsonPropValue.addProperty("Action", value.getAction());
        jsonPropValue.addProperty("Resource", value.getResource());
        jsonPropValue.addProperty("Message", value.getMessage());

        out.write(jsonPropValue.toString().getBytes());
    }
}
