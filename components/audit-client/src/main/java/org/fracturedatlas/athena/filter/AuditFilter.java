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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import org.fracturedatlas.athena.client.audit.PublicAuditMessage;
import org.fracturedatlas.athena.web.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;


/**
 *
 * @author fintan
 */
//@Component
public class AuditFilter implements ContainerRequestFilter {

    protected FilterConfig filterConfig = null;
    protected static Properties props;
    protected static WebResource component;
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    protected Gson gson = JsonUtil.getGson();
    protected static String uri = null;


     static {
         props = new Properties();
         ClassPathResource cpr = new ClassPathResource("athena-audit.properties");
         try{
             InputStream in = cpr.getInputStream();
             props.load(in);
             in.close();
             uri = "http://" + props.getProperty("audit.hostname") + ":" + props.getProperty("audit.port") + "/" + props.getProperty("audit.componentName") + "/";
             ClientConfig cc = new DefaultClientConfig();
             Client c = Client.create(cc);
             component = c.resource(uri);

         } catch (Exception e) {
             Logger log2 = LoggerFactory.getLogger(AuditFilter.class);
             log2.error(e.getMessage(),e);
         }
     }

    public void init(FilterConfig filterConfig)
            throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request,
            ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
       try {
            String user = request.getRemoteAddr() + ":" + request.getRemotePort();
            //Action
            logger.debug(request.getAttributeNames().toString());
            logger.debug(request.getParameterNames().toString());
            String action = "Restful request";
            //Resource
            String resource = request.getLocalAddr() + ":" + request.getLocalPort();
            //Message
            BufferedReader bf = request.getReader();
            StringBuilder message = new StringBuilder();
            while (bf.ready()) {
                message.append(bf.readLine());
            }
            //DateTime
            Long dateTime = System.currentTimeMillis();

            PublicAuditMessage pam = new PublicAuditMessage(user, action, resource, message.toString());
            String jsonResponse;
            String path = "audit/";
            String recordJson = gson.toJson(pam);
            component.path(path).type("application/json")
                                .post(String.class, recordJson);


        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
        }
        chain.doFilter(request, response);
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
               try {

            String user = request.getUserPrincipal() + ":" ;
            //Action
            logger.debug(request.getRequestHeaders().toString());
            logger.debug(request.toString());
            logger.debug(request.getMethod());
            logger.debug(request.getPath());
            logger.debug(request.getEntityInputStream().toString());
            logger.debug(request.getProperties().toString());
            logger.debug(request.getQueryParameters().toString());
            logger.debug(request.getFormParameters().toString());
            logger.debug(request.getRequestUri().toString());
            logger.debug(ContainerRequest.CONTENT_ENCODING);
            String action = request.getMethod();
            //Resource
            String resource = request.getRequestUri().toString();
            //Message
            InputStream is = request.getEntityInputStream();
            final char[] buffer = new char[0x10000];
            StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(is, "UTF-8");
            int read;
            do {
                read = in.read(buffer, 0, buffer.length);
                if (read>0) {
                    out.append(buffer, 0, read);
                }
            } while (read>=0);
            String message = out.toString();
            PublicAuditMessage pam = new PublicAuditMessage(user, action, resource, message.toString());
            String path = "audit/";
            String recordJson = gson.toJson(pam);
            component.path(path).type("application/json")
                                .post(String.class, recordJson);
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
        }
        return request;
    }
}
