/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fracturedatlas.athena.audit.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.fracturedatlas.athena.audit.manager.AuditManager;
import org.fracturedatlas.athena.audit.model.AuditMessage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author fintan
 */
public class AuditFilter implements ContainerRequestFilter {

    private FilterConfig filterConfig = null;
    @Autowired
    AuditManager auditManager;

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
 //            if (filterConfig == null) {
//                return;
//            }
            String user = request.getRemoteAddr() + ":" + request.getRemotePort();
            //Action
            System.out.println(request.getAttributeNames());
            System.out.println(request.getParameterNames());
            String action = "Restful request";
            //Resource
            String resource = request.getLocalAddr() + ":" + request.getLocalPort();
            //Message
            BufferedReader bf = request.getReader();
            StringBuffer message = new StringBuffer();
            while (bf.ready()) {
                message.append(bf.readLine());
            }
            //DateTime
            Long dateTime = System.currentTimeMillis();
            AuditMessage am = new AuditMessage(user, action, resource, message.toString());
            auditManager.saveAuditMessage(am);

        } catch (Exception ex) {
            Logger.getLogger(AuditFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        chain.doFilter(request, response);
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
               try {

            String user = request.getUserPrincipal() + ":" ;
            //Action
            System.out.println(request.getRequestHeaders());
            System.out.println(request);
            String action = "Restful request";
            //Resource
            String resource = request.getMethod() + ":" + request.getPath();
            //Message
            InputStream is = request.getEntityInputStream();
            String message = is.toString();
            //DateTime
            Long dateTime = System.currentTimeMillis();
            AuditMessage am = new AuditMessage(user, action, resource, message.toString());
            auditManager.saveAuditMessage(am);

        } catch (Exception ex) {
            Logger.getLogger(AuditFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
               return request;
    }
}
