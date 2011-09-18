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
package org.fracturedatlas.athena.plugin.doorlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.model.Ticket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.fracturedatlas.athena.web.manager.AbstractAthenaSubResource;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DoorlistSubResource extends AbstractAthenaSubResource {
    
    @Autowired
    RecordManager recordManager;
    
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public List<PTicket> find(String parentType, 
                                 Object parentId,
                                 String subResourceType,
                                 Map<String, List<String>> queryParams,
                                 String username) {
        
        AthenaSearch search = new AthenaSearch.Builder()
                                              .type("ticket")
                                              .and("performanceId", Operator.EQUALS, IdAdapter.toString(parentId))
                                              .build();
        
        List<Ticket> tickets = Ticket.fromSet(recordManager.findRecords("ticket", search));
        List<PTicket> doorListRows = new ArrayList<PTicket>();
        
        //for each ticket, if sold
        for(Ticket ticket : tickets) {
            if("sold".equals(ticket.getState())) {
                PTicket doorListRow = recordManager.getRecord("person", ticket.getBuyerId());
                doorListRow.put("price", Integer.toString(ticket.getPrice())); 
                doorListRow.put("section", ticket.getSection());
                doorListRows.add(doorListRow);
            }
        }
        return doorListRows;
    }
}
