/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fracturedatlas.athena.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import com.google.gson.Gson;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fracturedatlas.athena.client.audit.PublicAuditMessage;
import org.fracturedatlas.athena.util.Scrubber;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class AuditFilter implements ContainerRequestFilter {

    protected static Properties props;
    protected static WebResource component;
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    protected Gson gson = JsonUtil.getGson();
    protected static String uri = null;
    protected static List<String> fieldsToScrub = new ArrayList<String>();
    protected static String AUDIT_PATH = "/audit";

    static {
        props = new Properties();
        ClassPathResource cpr = new ClassPathResource("athena-audit.properties");
        try {
            InputStream in = cpr.getInputStream();
            props.load(in);
            in.close();
            uri = "http://" + props.getProperty("audit.hostname") + ":" + props.getProperty("audit.port") + "/" + props.getProperty("audit.componentName") + "/";
            ClientConfig cc = new DefaultClientConfig();
            Client c = Client.create(cc);
            component = c.resource(uri);

        } catch (Exception e) {
            Logger log2 = LoggerFactory.getLogger(AuditFilter.class);
            log2.error(e.getMessage(), e);
        }

        fieldsToScrub.add("cardNumber");
        fieldsToScrub.add("cvv");
        fieldsToScrub.add("expirationDate");
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = request.getEntityInputStream();
            ReaderWriter.writeTo(in, out);
            byte[] requestEntity = out.toByteArray();
            String message = out.toString();
            message = Scrubber.scrubJson(message, fieldsToScrub);
            request.setEntityInputStream(new ByteArrayInputStream(requestEntity));
            PublicAuditMessage pam = constructPublicAuditMessage(request, message);
            sendAuditMessage(pam);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return request;
    }

    private void sendAuditMessage(PublicAuditMessage pam) {
        String recordJson = gson.toJson(pam);
        component.path(AUDIT_PATH).type("application/json").post(String.class, recordJson);
    }

    private PublicAuditMessage constructPublicAuditMessage(ContainerRequest request,
                                                           String message) {

        //TODO: Update this to work with security
        String user = request.getUserPrincipal() + ":";
        String action = request.getMethod();
        String resource = request.getRequestUri().toString();
        return new PublicAuditMessage(user, action, resource, message);
    }
}
