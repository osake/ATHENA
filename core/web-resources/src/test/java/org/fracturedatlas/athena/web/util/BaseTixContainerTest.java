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

 
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.*;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javax.persistence.EntityManagerFactory;
import org.springframework.context.ApplicationContext;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import static org.junit.Assert.*;
import org.fracturedatlas.athena.apa.impl.jpa.*;
import org.fracturedatlas.athena.client.*;
import org.fracturedatlas.athena.id.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTixContainerTest extends JerseyTest {

    protected WebResource tix;
    protected EntityManagerFactory emf;
    protected ApaAdapter apa;
    protected final static String TIX_URI = "http://localhost:9998/tix";
    protected final static String RECORDS_PATH = "/tickets/";
    protected final static String FIELDS_PATH = "/meta/fields/";

    protected List<PTicket> recordsToDelete = new ArrayList<PTicket>();
    protected List<PropField> propFieldsToDelete = new ArrayList<PropField>();
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public BaseTixContainerTest() {

      super(new WebAppDescriptor.Builder("org.fracturedatlas.athena.web.resource")
        .contextPath("tix")
        .contextParam("contextConfigLocation", "classpath:testApplicationContext.xml")
        .servletClass(SpringServlet.class)
        .contextListenerClass(ContextLoaderListener.class)
        .contextParam("javax.ws.rs.Application", "org.fracturedatlas.athena.web.config.AthenaWebConfig")
        .build());

        ClientConfig cc = new DefaultClientConfig();
        Client c = Client.create(cc);
        tix = c.resource(TIX_URI);
        tix.addFilter(new LoggingFilter());

        ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext.xml");
        apa = (ApaAdapter)context.getBean("apa");
    }

    public void teardownTickets() {
        for (PTicket t : recordsToDelete) {
            try {
                apa.deleteRecord(t.getType(), t.getId());
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        for (PropField pf : propFieldsToDelete) {
            try {
                    apa.deletePropField(pf);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    public void assertRecordsEqual(PTicket t, PTicket pTicket, Boolean includeId) {
        if(includeId) {
            assertTrue(IdAdapter.isEqual(t.getId(), pTicket.getId()));
        }

        assertEquals(t.getProps().size(), pTicket.getProps().size());

        for(Entry<String, String> prop : t.getProps().entrySet()) {
            assertTrue(pTicket.get(prop.getKey()).equals(prop.getValue()));
        }
    }

    public void assertTicketsEqual(JpaRecord t, PTicket pTicket, Boolean includeId) {
        if(includeId) {
            assertTrue(IdAdapter.isEqual(t.getId(), pTicket.getId()));
        }
        
        assertEquals(t.getTicketProps().size(), pTicket.getProps().size());

        for(TicketProp ticketProp : t.getTicketProps()) {
            String value = pTicket.get(ticketProp.getPropField().getName());
            assertEquals(ticketProp.getValueAsString(), value);
        }
    }

    public void assertTicketsEqual(JpaRecord t, PTicket pTicket) {
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

    public PField addPropField(ValueType valueType, String name, Boolean strict) {
        PropField pf = apa.savePropField(new PropField(valueType, name, strict));
        propFieldsToDelete.add(pf);
        return pf.toClientField();
    }
}
