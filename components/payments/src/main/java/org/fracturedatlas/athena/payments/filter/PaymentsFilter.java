/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fracturedatlas.athena.payments.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.fracturedatlas.athena.client.audit.PublicAuditMessage;
import com.sun.jersey.api.client.WebResource;
import org.fracturedatlas.athena.filter.AuditFilter;
import org.fracturedatlas.athena.payments.model.AuthorizationRequest;
import org.fracturedatlas.athena.payments.model.CreditCard;
import org.fracturedatlas.athena.payments.model.Customer;

/**
 *
 *
 * @author fintan
 */
public class PaymentsFilter extends AuditFilter {

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        PublicAuditMessage pam = null;

        try {
            String user = request.getUserPrincipal() + ":";
            String action = request.getMethod();
            String resource = request.getRequestUri().toString();
            InputStream is = request.getEntityInputStream();
            final char[] buffer = new char[0x10000];
            StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(is, "UTF-8");
            int read;
            do {
                read = in.read(buffer, 0, buffer.length);
                if (read > 0) {
                    out.append(buffer, 0, read);
                }
            } while (read >= 0);
            String message = out.toString();
            if (action.equalsIgnoreCase("POST") || action.equalsIgnoreCase("PUT")) {
                //find out the resource contacted
                if (resource.indexOf("/customers/") > -1) {
                    Customer customer = gson.fromJson(message, Customer.class);
                    message = customer.toEscapedString();
                } else if (resource.indexOf("/cards/") > -1) {
                    CreditCard card = gson.fromJson(message, CreditCard.class);
                    message = card.toEscapedString();
                } else if (resource.indexOf("/transations/") > -1) {
                    AuthorizationRequest paymentDetails = gson.fromJson(message, AuthorizationRequest.class);
                    message = paymentDetails.toEscapedString();
                }
            }
            pam = new PublicAuditMessage(user, action, resource, message);
            String path = "audit/";
            String recordJson = gson.toJson(pam);
            component.path(path).type("application/json").post(String.class, recordJson);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return request;
    }
}
