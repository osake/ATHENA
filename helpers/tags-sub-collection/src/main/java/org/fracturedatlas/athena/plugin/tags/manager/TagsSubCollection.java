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
package org.fracturedatlas.athena.plugin.tags.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.plugin.tags.model.Tag;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.web.manager.AthenaSubCollection;
import org.fracturedatlas.athena.web.manager.RecordManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("tagsSubCollection")
public class TagsSubCollection implements AthenaSubCollection {

    @Autowired
    ApaAdapter apa;
    
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    
    public Collection get(String parentType,
                             String subCollectionType,
                             MultivaluedMap<String, String> queryParams,
                             String username) {
        
        AthenaSearch search = new AthenaSearch(queryParams);
        search.setType(parentType);
        Set<PTicket> tickets = apa.findTickets(search);
        Map<String, Tag> tagMap = new HashMap<String, Tag>();
        Set<Tag> tags = new HashSet<Tag>();
        for(PTicket ticket : tickets) {
            nullSafeAddAllTags(tagMap, ticket);
        }
        tags.addAll(tagMap.values());
        return tags;
    }
    
    private void nullSafeAddAllTags(Map<String, Tag> tagMap, PTicket ticket) {
        if(ticket != null && ticket.getProps() != null && ticket.getProps().get("tags") != null) {
            for(String tagText : ticket.getProps().get("tags")) {
                Tag tag = tagMap.get(tagText);
                if(tag == null) {
                    tag = new Tag(tagText, 1);
                } else {
                    tag.setOccurrences(tag.getOccurrences() + 1);
                }
                tagMap.put(tagText, tag);
            }
        }
    }
}
