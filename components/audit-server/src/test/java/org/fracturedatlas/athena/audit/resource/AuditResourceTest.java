/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fracturedatlas.athena.audit.resource;

import javax.ws.rs.core.MultivaluedMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.fracturedatlas.athena.audit.model.AuditMessage;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;


public class AuditResourceTest extends JerseyTest {

    protected WebResource tix;
    protected final static String TIX_URI = "http://localhost:9998/tix";
    protected final static String AUDIT_PATH = "/audit/";
    protected final static String FIELDS_PATH = "/meta/fields/";

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    Gson gson  = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING)
             .create();



    public AuditResourceTest() {

      super(new WebAppDescriptor.Builder("org.fracturedatlas.athena.web.resource")
        .contextPath("tix")
        .contextParam("contextConfigLocation", "classpath:testApplicationContext.xml")
        .servletClass(SpringServlet.class)
        .contextListenerClass(ContextLoaderListener.class)
        .contextParam("javax.ws.rs.Application", "org.fracturedatlas.athena.audit.web.config.AuditConfig")
        .build());

        ClientConfig cc = new DefaultClientConfig();
        Client c = Client.create(cc);
        tix = c.resource(TIX_URI);
        tix.addFilter(new LoggingFilter());

        ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext.xml");
    }



 
   



    /**
     * Test of saveAuditMessage method, of class AuditResource.
     */
    @Test
    public void testSaveAuditMessage() throws Exception {
        String path = AUDIT_PATH;
        System.out.println("saveAuditMessage");
        String json = gson.toJson(new AuditMessage("Tom", "created", "Ticket", "ticketdetails"));
        String jsonResponse = tix.path(path).type("application/json").post(String.class, json);
        System.out.println("audit is " + jsonResponse);
        AuditMessage am = gson.fromJson(jsonResponse, AuditMessage.class);
        assertEquals(am.getUser(), "Tom");
        assertEquals(am.getAction(), "created");
        assertEquals(am.getResource(), "Ticket");
        assertEquals(am.getMessage(), "ticketdetails");
        assertNotNull(am.getId());

    }

    /**
     * Test of getAuditMessages method, of class AuditResource.
     */
 //   @Test
    public void testGetAuditMessages() {
        System.out.println("getAuditMessages");
        String path = AUDIT_PATH;
        String json = gson.toJson(new AuditMessage("Tom", "created", "Ticket", "ticketdetails"));
        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, json);

        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams = new MultivaluedMapImpl();
        queryParams.add("user", "eqTom");
        json = tix.path(path).queryParams(queryParams).get(String.class);

        AuditMessage[] am = gson.fromJson(json, AuditMessage[].class);
        assertTrue(am.length>0);

    }


       /**
     * Test of getAuditMessages method, of class AuditResource.
     */
//    @Test
    public void testGetMoreMessages() {
        System.out.println("getAuditMessages");
        String path = AUDIT_PATH;
        String json = gson.toJson(new AuditMessage("Tom", "created", "Ticket", "ticketdetails"));
        ClientResponse response = tix.path(path).type("application/json").post(ClientResponse.class, json);

        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams = new MultivaluedMapImpl();
        queryParams.add("dateTime", "gt1292253992498");
        json = tix.path(path).queryParams(queryParams).get(String.class);

        AuditMessage[] am = gson.fromJson(json, AuditMessage[].class);
        assertTrue(am.length>0);

    }
}