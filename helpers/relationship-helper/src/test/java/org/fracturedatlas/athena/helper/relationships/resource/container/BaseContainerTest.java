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

package org.fracturedatlas.athena.helper.relationships.resource.container;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseContainerTest extends JerseyTest {

    protected WebResource tix;
    protected EntityManagerFactory emf;
    protected ApaAdapter apa;
    protected final static String TIX_URI = "http://localhost:9998/test";
    protected final static String PATH = "/relationships";

    protected List<Ticket> ticketsToDelete = new ArrayList<Ticket>();
    protected List<PropField> propFieldsToDelete = new ArrayList<PropField>();
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public BaseContainerTest() {

      super(new WebAppDescriptor.Builder("org.fracturedatlas.athena")
        .contextPath("test")
        .contextParam("contextConfigLocation", "classpath:athenatest-applicationContext.xml")
        .servletClass(SpringServlet.class)
        .contextListenerClass(ContextLoaderListener.class)
        .contextParam("javax.ws.rs.Application", "org.fracturedatlas.athena.web.config.AthenaWebConfig")
        .build());

        ClientConfig cc = new DefaultClientConfig();
        Client c = Client.create(cc);
        tix = c.resource(TIX_URI);
        tix.addFilter(new LoggingFilter());

        ApplicationContext context = new ClassPathXmlApplicationContext("athenatest-applicationContext.xml");
        apa = (ApaAdapter)context.getBean("apa");
    }

    public void teardownTickets() {
        for (Ticket t : ticketsToDelete) {
            try {
                apa.deleteTicket(t);
            } catch (Exception ignored) {
                    logger.error(ignored.getMessage(), ignored);
            }
        }

        for (PropField pf : propFieldsToDelete) {
            try {
                    apa.deletePropField(pf);
            } catch (Exception ignored) {
                    logger.error(ignored.getMessage(), ignored);
            }
        }
    }

    public void assertTicketsEqual(Ticket t, PTicket pTicket, Boolean includeId) {
        if(includeId) {
            assertTrue(IdAdapter.isEqual(t.getId(), pTicket.getId()));
        }

        assertEquals(t.getTicketProps().size(), pTicket.getProps().size());

        for(TicketProp ticketProp : t.getTicketProps()) {
            String value = pTicket.get(ticketProp.getPropField().getName());
            assertEquals(ticketProp.getValueAsString(), value);
        }
    }

    public void assertTicketsEqual(Ticket t, PTicket pTicket) {
        assertTicketsEqual(t, pTicket, Boolean.TRUE);
    }
}


