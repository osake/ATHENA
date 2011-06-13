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

package org.fracturedatlas.athena.helper.available.web;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.Set;
import org.fracturedatlas.athena.apa.impl.jpa.StrictType;
import org.fracturedatlas.athena.apa.impl.jpa.ValueType;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.available.manager.AvailableTicketsManager;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.web.exception.ObjectNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AvailableTicketsContextTest extends BaseManagerTest {

    AvailableTicketsManager manager;
    PTicket t1;
    PTicket t2;
    PTicket t3;

    public AvailableTicketsContextTest() throws Exception {
        super();
        manager = (AvailableTicketsManager)context.getBean("codeManager");
    }

    @After
    public void tearDown() {
        teardownTickets();
    }
}
