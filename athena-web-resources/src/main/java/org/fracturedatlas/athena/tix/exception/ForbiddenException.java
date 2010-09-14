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
package org.fracturedatlas.athena.tix.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Represents an HTTP 403 - Forbidden
 * Convenience wrapper to wrap rs.WebApplicationException.
 */
public class ForbiddenException extends WebApplicationException {
     public ForbiddenException(String message) {
         super(Response.status(Response.Status.FORBIDDEN)
               .entity(message)
               .type("text/plain")
               .build());
     }
} 
