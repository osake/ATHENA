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
package org.fracturedatlas.athena.web.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.security.krb5.internal.Ticket;

@Component("tagsSubCollection")
public class TagsSubCollection implements AthenaSubCollection {

    @Autowired
    ApaAdapter apa;
    
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    
    /*
     * TODO: Pass along the query parameters to the apa search
     */
    public Collection execute(String parentType,
                             String subCollectionType,
                             Map<String, List<String>> queryParams,
                             String username) {
        
        AthenaSearch search = new AthenaSearch.Builder().type(parentType).build();
        Set<PTicket> tickets = apa.findTickets(search);
        Set<String> tags = new HashSet<String>();
        for(PTicket ticket : tickets) {
            nullSafeAddAllTags(tags, ticket);
        }
        return tags;
    }
    
    private void nullSafeAddAllTags(Set<String> tags, PTicket ticket) {
        if(ticket != null && ticket.getProps() != null && ticket.getProps().get("tags") != null) {
            tags.addAll(ticket.getProps().get("tags"));
        }
    }
}
