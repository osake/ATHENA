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

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
@SuppressWarnings("StaticNonFinalUsedInInitialization")
public class AuditFilter implements ContainerRequestFilter {

//    protected static Properties props;
//    protected static WebResource component;
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    protected Logger auditLog = LoggerFactory.getLogger("AuditFile");

    protected Gson gson = JsonUtil.getGson();
    protected static String uri = null;
    static ExecutorService executor;


//     static {
//         props = new Properties();
//         ClassPathResource cpr = new ClassPathResource("athena-audit.properties");
//         try{
//             InputStream in = cpr.getInputStream();
//             props.load(in);
//             in.close();
//             executor = Executors.newFixedThreadPool( Integer.parseInt(props.getProperty("audit.numthreads", "10")));
//             uri = "http://" + props.getProperty("audit.hostname") + ":" + props.getProperty("audit.port") + "/" + props.getProperty("audit.componentName") + "/";
//             ClientConfig cc = new DefaultClientConfig();
//             Client c = Client.create(cc);
//             component = c.resource(uri);
//
//         } catch (Exception e) {
//             Logger log2 = LoggerFactory.getLogger(AuditFilter.class);
//             log2.error(e.getMessage(),e);
//         }
//     }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
               try {

            String user = request.getUserPrincipal() + ":" ;
            //Action
            String action = request.getMethod();
            //Resource
            String resource = request.getRequestUri().toString();
            //Message
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream in = request.getEntityInputStream();
            ReaderWriter.writeTo(in, baos);
            byte[] requestEntity = baos.toByteArray();
            StringBuilder message = new StringBuilder();
            message.append(new String(requestEntity));
            PublicAuditMessage pam = new PublicAuditMessage(user, action, resource, message.toString());                         
            request.setEntityInputStream(new ByteArrayInputStream(requestEntity));
            auditLog.info(pam.toString());
//            Runnable worker = new SendAuditMessage(pam);
//            worker.run();
//            executor.execute(worker);
            return request;
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            return request;
        }
        
    }

//    public class SendAuditMessage implements Runnable {
//
//        final String path = "audit/";
//        PublicAuditMessage pam;
//
//        SendAuditMessage(PublicAuditMessage pam) {
//            this.pam = pam;
//        }
//
//        @Override
//        public void run() {
//            String recordJson = gson.toJson(pam);
//            component.path(path).type("application/json").post(String.class, recordJson);
//        }
//    }


}
