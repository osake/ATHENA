/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fracturedatlas.athena.audit.serialization;

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
import org.fracturedatlas.athena.client.audit.PublicAuditMessage;
/**
 *
 * @author fintan
 */
@Provider
@Produces({"application/json"})
public class PublicAuditMessageSerializer implements MessageBodyWriter<PublicAuditMessage> {
    @Override
    public long getSize(PublicAuditMessage value, Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType) {
        //TODO: Do this
        return -1L;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType) {
        return (PublicAuditMessage.class.isAssignableFrom(type));
    }

    @Override
    public void writeTo(PublicAuditMessage value, Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        JsonObject jsonAuditMessage = new JsonObject();
        jsonAuditMessage.addProperty("id", value.getId());
        jsonAuditMessage.addProperty("dateTime", value.getDateTime());
        jsonAuditMessage.addProperty("user", value.getUser());
        jsonAuditMessage.addProperty("action", value.getAction());
        jsonAuditMessage.addProperty("resource", value.getResource());
        jsonAuditMessage.addProperty("message", value.getMessage());

        out.write(jsonAuditMessage.toString().getBytes());
    }
}


