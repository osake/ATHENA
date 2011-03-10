/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fracturedatlas.athena.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.io.InputStream;
import com.google.gson.Gson;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.fracturedatlas.athena.client.audit.PublicAuditMessage;
import org.fracturedatlas.athena.util.Scrubber;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;


public class AuditFilter implements ContainerRequestFilter {

    protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    protected Gson gson = JsonUtil.getGson();
    protected static String uri = null;
    protected static List<String> fieldsToScrub = null;
    protected static String auditPath = null;
    protected static WebResource component;

    static {
        try {
            Configuration props = new PropertiesConfiguration("audit-client.properties");
            uri = "http://" + props.getString("audit.hostname") + ":"
                            + props.getString("audit.port") + "/"
                            + props.getString("audit.componentName") + "/";
            fieldsToScrub = props.getList("audit.fieldsToScrub");
            ClientConfig cc = new DefaultClientConfig();
            Client c = Client.create(cc);
            component = c.resource(uri);
            auditPath = "/" + props.getString("audit.endpoint");
        } catch (ConfigurationException e) {
            Logger tempLog = LoggerFactory.getLogger(AuditFilter.class);
            tempLog.error(e.getMessage(), e);
        }
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
        component.path(auditPath).type("application/json").post(String.class, recordJson);
    }

    private PublicAuditMessage constructPublicAuditMessage(ContainerRequest request,
                                                           String message) {

        String userName = "";
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken)request.getUserPrincipal();
        if(authToken == null) {
            userName = "[NONE]";
        } else {
            User user = (User)authToken.getPrincipal();
            userName = user.getUsername();
        }
        String action = request.getMethod();
        String resource = request.getRequestUri().toString();
        return new PublicAuditMessage(userName, action, resource, message);
    }


}
