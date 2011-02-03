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
package org.fracturedatlas.athena.helper.relationships.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.apa.model.Ticket;
import org.fracturedatlas.athena.client.AthenaComponent;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.AthenaSearchConstraint;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.web.exception.AthenaException;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RelationshipHelperManager {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private ApaAdapter apa;

    public Set<Ticket> findRelationships(String type, String id) {
        Set<Ticket> tickets = new HashSet<Ticket>();

        AthenaSearch athenaSearch = new AthenaSearch
                .Builder(new AthenaSearchConstraint("leftSideId", Operator.EQUALS, IdAdapter.toString(id)))
                .type("relationship")
                .build();

        tickets.addAll(apa.findTickets(athenaSearch));

        athenaSearch = new AthenaSearch
                .Builder(new AthenaSearchConstraint("rightSideId", Operator.EQUALS, IdAdapter.toString(id)))
                .type("relationship")
                .build();

        tickets.addAll(apa.findTickets(athenaSearch));

        return tickets;

    }

    public ApaAdapter getApa() {
        return apa;
    }

    public void setApa(ApaAdapter apa) {
        this.apa = apa;
    }


}
