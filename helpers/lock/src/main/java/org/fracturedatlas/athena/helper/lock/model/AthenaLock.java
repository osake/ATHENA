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

package org.fracturedatlas.athena.helper.lock.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


public class AthenaLock {
    Set<String> tickets;
    Date lockExpires;
    String id;
    String lockedByApi;
    String lockedByIp;
    String status;

    public AthenaLock() {
        tickets = new HashSet<String>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLockExpires() {
        return lockExpires;
    }

    public void setLockExpires(Date lockExpires) {
        this.lockExpires = lockExpires;
    }

    public String getLockedByApi() {
        return lockedByApi;
    }

    public void setLockedByApi(String lockedByApi) {
        this.lockedByApi = lockedByApi;
    }

    public String getLockedByIp() {
        return lockedByIp;
    }

    public void setLockedByIp(String lockedByIp) {
        this.lockedByIp = lockedByIp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<String> getTickets() {
        return tickets;
    }

    public void setTickets(Set<String> ticketIds) {
        this.tickets = ticketIds;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append(id).append(lockedByApi).append(lockedByIp).append(status).append(lockExpires).append(tickets);
        return builder.toString();
    }
}
