package org.fracturedatlas.athena.filter;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.util.ReaderWriter;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.fracturedatlas.athena.util.Scrubber;

/**
 * This class is largely derived from the source code Jersey's LoggingFilter 
 * (com.sun.jersey.api.container.filter.LoggingFilter) however we wanted to tweak the way logging was done
 * and nearly every method in that class was private so that extending the class to override one method was impossible
 */
public class AuditFilter implements ContainerRequestFilter, ContainerResponseFilter {
    /**
     * If true the request and response entities (if present) will not be logged.
     * If false the request and response entities will be logged.
     * <p>
     * The default value is false.
     */
    public static final String FEATURE_LOGGING_DISABLE_ENTITY
            = "com.sun.jersey.config.feature.logging.DisableEntitylogging";

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditFilter.class.getName());

    private static final String NOTIFICATION_PREFIX = "* ";
    
    private static final String REQUEST_PREFIX = "-> ";
    
    private static final String RESPONSE_PREFIX = "<- ";

    private final Logger logger;

    private @Context HttpContext hc;
    
    private @Context ResourceConfig rc;

    private long id = 0;
    protected static List<String> fieldsToScrub = null;

   static {
        try {
            Configuration props = new PropertiesConfiguration("logging.properties");
             fieldsToScrub = props.getList("audit.fieldsToScrub");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Create a logging filter logging the request and response to
     * a default JDK logger, named as the fully qualified class name of this
     * class.
     */
    public AuditFilter() {
        this(LOGGER);
    }

    public AuditFilter(Logger logger) {
        this.logger = logger;
    }

    private synchronized void setId() {
        if ( hc.getProperties().get("request-id") == null) {
            hc.getProperties().put("request-id", Long.toString(++id));
        }
    }

    private StringBuilder prefixId(StringBuilder b) {
        b.append(hc.getProperties().get("request-id").toString()).
                append(" ");
        return b;
    }
    
    public ContainerRequest filter(ContainerRequest request) {
        setId();

        final StringBuilder b = new StringBuilder();
        printRequestLine(b, request);
        printRequestHeaders(b, request.getRequestHeaders());

        if (rc.getFeature(FEATURE_LOGGING_DISABLE_ENTITY)) {
            logger.info(b.toString());
            return request;
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = request.getEntityInputStream();
            try {
                ReaderWriter.writeTo(in, out);

                String message = out.toString();
                message = Scrubber.scrubJson(message, fieldsToScrub);
                byte[] requestEntity = message.getBytes();
                printEntity(b, requestEntity);

                request.setEntityInputStream(new ByteArrayInputStream(out.toByteArray()));
                return request;
            } catch (IOException ex) {
                throw new ContainerException(ex);
            } finally {
                logger.info(b.toString());
            }
        }
    }
    
    private void printRequestLine(StringBuilder b, ContainerRequest request) {
        prefixId(b).append(NOTIFICATION_PREFIX).append("Server in-bound request").append('\n');
        prefixId(b).append(REQUEST_PREFIX).append(request.getMethod()).append(" ").
                append(request.getRequestUri().toASCIIString()).append('\n');
    }
    
    private void printRequestHeaders(StringBuilder b, MultivaluedMap<String, String> headers) {
        for (Map.Entry<String, List<String>> e : headers.entrySet()) {
            String header = e.getKey();
            for (String value : e.getValue()) {
                prefixId(b).append(REQUEST_PREFIX).append(header).append(": ").
                        append(value).append('\n');
            }
        }
        prefixId(b).append(REQUEST_PREFIX).append('\n');
    }

    private void printEntity(StringBuilder b, byte[] entity) throws IOException {
        if (entity.length == 0)
            return;
        b.append(new String(entity)).append("\n");
    }

    private final class Adapter implements ContainerResponseWriter {
        private final ContainerResponseWriter crw;

        private final boolean disableEntity;

        private long contentLength;

        private ContainerResponse response;

        private ByteArrayOutputStream baos;

        private StringBuilder b = new StringBuilder();

        Adapter(ContainerResponseWriter crw) {
            this.crw = crw;
            this.disableEntity = rc.getFeature(FEATURE_LOGGING_DISABLE_ENTITY);
        }
        
        public OutputStream writeStatusAndHeaders(long contentLength, ContainerResponse response) throws IOException {
            printResponseLine(b, response);
            printResponseHeaders(b, response.getHttpHeaders());

            if (disableEntity) {
                logger.info(b.toString());
                return crw.writeStatusAndHeaders(contentLength, response);
            } else {
                this.contentLength = contentLength;
                this.response = response;
                return this.baos = new ByteArrayOutputStream();
            }
        }

        public void finish() throws IOException {
            if (!disableEntity) {
                byte[] entity = baos.toByteArray();
                printEntity(b, entity);

                // Output to log
                logger.info(b.toString());

                // Write out the headers and buffered entity
                OutputStream out = crw.writeStatusAndHeaders(contentLength, response);
                out.write(entity);
            }
            crw.finish();
        }
    }

    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        setId();
        response.setContainerResponseWriter(
                new Adapter(response.getContainerResponseWriter()));
        return response;
    }
    
    private void printResponseLine(StringBuilder b, ContainerResponse response) {
        prefixId(b).append(NOTIFICATION_PREFIX).
            append("Server out-bound response").append('\n');
        prefixId(b).append(RESPONSE_PREFIX).append(Integer.toString(response.getStatus())).append('\n');
    }
    
    private void printResponseHeaders(StringBuilder b, MultivaluedMap<String, Object> headers) {
        for (Map.Entry<String, List<Object>> e : headers.entrySet()) {
            String header = e.getKey();
            for (Object value : e.getValue()) {
                prefixId(b).append(RESPONSE_PREFIX).append(header).append(": ").
                        append(ContainerResponse.getHeaderValue(value)).append('\n');
            }
        }
        prefixId(b).append(RESPONSE_PREFIX).append('\n');
    } 
}