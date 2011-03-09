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

package org.fracturedatlas.athena.web.filter;

import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import javax.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * A server-side filter to enable digest authorization.  This class extends Spring Security's
 * DigestAuthenticationFilter and allows the implementor to turn security on or off
 * by setting the securityEnabled property of this class
 *
 * @author gary.moore@fracturedatlas.org
 */
public class SwitchableDigestAuthenticationFilter extends DigestAuthenticationFilter {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private Boolean securityEnabled = Boolean.TRUE;

    public Boolean getSecurityEnabled() {
        return securityEnabled;
    }

    /**
     *
     * Set a boolean flag to indicate that requests should be authenticated.  Obviously,
     * <b>turning security off would be very bad</b>.  This value should only be set to false in
     * a development environment where the data is neither important nor secure.
     *
     * @param securityEnabled true is authentication should be enforced, false otherwise
     */
    public void setSecurityEnabled(Boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws java.io.IOException, ServletException {

        logger.debug("In Athena security filter");

        if(securityEnabled) {
            logger.debug("Security is enabled, securing...");
            super.doFilter(req, res, chain);
        } else {
            logger.debug("Security is off, proceeding with filter chain");
            chain.doFilter(req, res);
        }
    }

}
