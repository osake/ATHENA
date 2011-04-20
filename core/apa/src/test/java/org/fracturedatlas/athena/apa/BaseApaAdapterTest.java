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
package org.fracturedatlas.athena.apa;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.fracturedatlas.athena.apa.impl.jpa.*;
import org.fracturedatlas.athena.client.PField;
import org.fracturedatlas.athena.client.PTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

public abstract class BaseApaAdapterTest {

    protected ApaAdapter apa;
    protected List<PTicket> ticketsToDelete = new ArrayList<PTicket>();
    protected List<PropField> propFieldsToDelete = new ArrayList<PropField>();
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public BaseApaAdapterTest() {
        ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext.xml");
        apa = (ApaAdapter) context.getBean("apa");
    }

    public void teardownTickets() {
        for (PTicket t : ticketsToDelete) {
            try {
                logger.debug("Cleaning up ticket [{}]", t.getId());
                apa.deleteRecord(t.getType(), t.getId());
            } catch (Exception ignored) {
                logger.error(ignored.getMessage(), ignored);
            }
        }

        for (PropField pf : propFieldsToDelete) {
            try {
                apa.deletePropField(pf);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    /**
     * Compares two lists for equality ignoring order.
     * @param col1
     * @param col2
     */
    public void doCollectionsContainSameElements(Collection col1, Collection col2) {

        if (col1 == null) {
            assertNull(col2);
        } else if (col2 == null) {
            fail("One list is null and the other is not");
        }

        assertEquals(col1.size(), col2.size());

        for (Object o : col2) {
            col1.remove(o);
        }

        assertEquals(0, col1.size());
    }

    public PField addPropField(ValueType valueType, String name, Boolean strict) {
        PropField pf = apa.savePropField(new PropField(valueType, name, strict));
        propFieldsToDelete.add(pf);
        return pf.toClientField();
    }
}
