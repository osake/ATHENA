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

package org.fracturedatlas.athena.payments.web.resource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.fracturedatlas.athena.payments.web.AthenaPayments;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ContextLoaderListener;


public abstract class BasePaymentsTest extends JerseyTest {

    private WebResource webResource;
    protected AthenaPayments payments;
    protected final static String BASE_URI = "http://localhost:9998/payments";
    protected final static String AUTHORIZATION_PATH = "/transactions/authorize";
    protected final static String SETTLE_PATH = "/transactions/settle";
    protected final static String VOID_PATH = "/transactions/void";

    public BasePaymentsTest() {

      super(new WebAppDescriptor.Builder("org.fracturedatlas.athena.web.resource")
        .contextPath("payments")
        .contextParam("contextConfigLocation", "classpath:testApplicationContext.xml")
        .servletClass(SpringServlet.class)
        .contextListenerClass(ContextLoaderListener.class)
        .contextParam("javax.ws.rs.Application", "org.fracturedatlas.athena.web.config.ParakeetConfig")
        .build());

        ClientConfig cc = new DefaultClientConfig();
        Client c = Client.create(cc);
        webResource = c.resource(BASE_URI);
        webResource.addFilter(new LoggingFilter());
        payments = new AthenaPayments(webResource);

        ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext.xml");
    }
}
