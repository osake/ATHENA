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

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ParakeetException extends RuntimeException
                                       implements ExceptionMapper<ParakeetException> {
    public ParakeetException(String message) {
        super(message);
    }

    public ParakeetException(String message, Throwable cause) {
        super(message, cause);
    }

    public Response toResponse(ParakeetException ex) {
        return Response.status(Response.Status.BAD_REQUEST).
            entity(ex.getMessage()).
            type("text/plain").
            build();
    }
}