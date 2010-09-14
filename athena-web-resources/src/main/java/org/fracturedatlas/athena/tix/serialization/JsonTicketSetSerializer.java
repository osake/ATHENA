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
package org.fracturedatlas.athena.tix.serialization;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.tix.util.JsonUtil;

@Provider
@Produces({"application/json"})
public class JsonTicketSetSerializer implements MessageBodyWriter<Set<Ticket>> {

    @Override
    public long getSize(Set<Ticket> t, Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType) {
        //TODO: Do this
        return -1L;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType) {
        return (Collection.class.isAssignableFrom(type));
    }

    @Override
    public void writeTo(Set<Ticket> tickets, Class<?> type, Type type1, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        List<PTicket> pTickets = new ArrayList<PTicket>();
        for (Ticket t : tickets) {
            pTickets.add(t.toClientTicket());
        }
        out.write(JsonUtil.getGson().toJson(pTickets).getBytes());
    }
}
