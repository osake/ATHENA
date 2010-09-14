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
package org.fracturedatlas.athena.tix.util;

 
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.*;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import org.springframework.context.ApplicationContext;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import static org.junit.Assert.*;
import org.fracturedatlas.athena.apa.model.*;
import org.fracturedatlas.athena.client.*;
import org.fracturedatlas.athena.id.*;
import org.junit.After;

public abstract class BaseTixContainerTest extends JerseyTest {

    protected WebResource tix;
    protected EntityManagerFactory emf;
    protected ApaAdapter apa;
    protected final static String TIX_URI = "http://localhost:9998/parakeet";

    protected List<Ticket> ticketsToDelete = new ArrayList<Ticket>();
    protected List<PropField> propFieldsToDelete = new ArrayList<PropField>();

    public BaseTixContainerTest() {

      super(new WebAppDescriptor.Builder("org.fracturedatlas.athena.tix.resource")
        .contextPath("parakeet")
        .contextParam("contextConfigLocation", "classpath:testApplicationContext.xml")
        .servletClass(SpringServlet.class)
        .contextListenerClass(ContextLoaderListener.class)
        .contextParam("javax.ws.rs.Application", "org.fracturedatlas.athena.tix.config.ParakeetConfig")
        .build());

        ClientConfig cc = new DefaultClientConfig();
        Client c = Client.create(cc);
        tix = c.resource(TIX_URI);
        tix.addFilter(new LoggingFilter());

        ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext.xml");
        apa = (ApaAdapter)context.getBean("apa");
    }

    public void assertTicketsEqual(Ticket t, PTicket pTicket, Boolean includeId) {
        if(includeId) {
            assertTrue(IdAdapter.isEqual(t.getId(), pTicket.getId()));
        }
        
        assertEquals(t.getName(), pTicket.getName());
        assertEquals(t.getTicketProps().size(), pTicket.getProps().size());

        for(TicketProp ticketProp : t.getTicketProps()) {
            String value = pTicket.get(ticketProp.getPropField().getName());
            assertEquals(ticketProp.getValueAsString(), value);
        }
    }

    public void assertTicketsEqual(Ticket t, PTicket pTicket) {
        assertTicketsEqual(t, pTicket, Boolean.TRUE);
    }
    
    public void assertFieldsEqual(PropField field, PField pField) {
        assertTrue(IdAdapter.isEqual(field.getId(), pField.getId()));
        assertEquals(field.getName(), pField.getName());
        assertEquals(field.getValueType().toString(), pField.getValueType());
        assertEquals(field.getStrict() , pField.getStrict());

        assertEquals(field.getPropValues().size(), pField.getPropValues().size());
        for(PropValue value : field.getPropValues()) {
            String name = value.getPropValue();
            boolean found = false;
            for(String pName : pField.getPropValues()) {
                if(name.equals(pName)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                fail("Fields are not equals, values differ");
            }
        }
    }
}
