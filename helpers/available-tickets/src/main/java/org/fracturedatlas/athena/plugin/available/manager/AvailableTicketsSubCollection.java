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

package org.fracturedatlas.athena.plugin.available.manager;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import org.fracturedatlas.athena.apa.ApaAdapter;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.helper.lock.manager.AthenaLockManager;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.fracturedatlas.athena.web.manager.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("availableSubCollection")
public class AvailableTicketsSubCollection implements AthenaSubCollection {

    @Autowired
    RecordManager recordManager;

    @Autowired
    ApaAdapter apa;

    //TODO: COnfigurable
    public static final Integer DEFAULT_LIMIT = 4;

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public Collection get(String parentType,
                             String subCollectionType,
                             MultivaluedMap<String, String> queryParams,
                             String username) {
        Integer limit = DEFAULT_LIMIT;
        if(queryParams.get("_limit") != null) {
            String strLimit = queryParams.get("_limit").get(0);
            limit = Integer.parseInt(strLimit);
            queryParams.remove("_limit");
        }
        
        queryParams.remove("state");
        queryParams.putSingle("state", "on_sale");


        Set<PTicket> tickets = recordManager.findRecords(parentType, queryParams);
        Set<PTicket> outTickets = new HashSet<PTicket>();

        for(PTicket ticket : tickets) {
            if(!isLocked(ticket)) {
                outTickets.add(ticket);
                if(outTickets.size() == limit) {
                    break;
                }
            }
        }

        return outTickets;
    }

    public boolean isLocked(PTicket t) {
        if(t.get(AthenaLockManager.LOCK_EXPIRES) == null) {
            return false;
        } else {
            try{
                DateTime lockExpires = DateUtil.parseDateTime(t.get(AthenaLockManager.LOCK_EXPIRES));
                return lockExpires.isAfterNow();
            } catch (ParseException pe) {
                logger.error("Error parsing lockExpires of [{}], lockExpires was [{}]", t.getIdAsString(), t.get(AthenaLockManager.LOCK_EXPIRES));
                return false;
            }
        }
    }

    public ApaAdapter getApa() {
        return apa;
    }

    public void setApa(ApaAdapter apa) {
        this.apa = apa;
    }

    public RecordManager getRecordManager() {
        return recordManager;
    }

    public void setRecordManager(RecordManager recordManager) {
        this.recordManager = recordManager;
    }
}
