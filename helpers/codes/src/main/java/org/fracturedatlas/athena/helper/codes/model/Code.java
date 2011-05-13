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

package org.fracturedatlas.athena.helper.codes.model;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Code {

    private transient Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    
    String id;
    String code;
    String description;
    Date startDate;
    Date endDate;
    Integer price;
    Set<String> sellers;
    Set<String> tickets;
    Set<String> performances;
    Set<String> events;
    Boolean enabled;
    
    public static final String FIELD_PREFIX = "athena_code" + PTicket.SYSTEM_PROP_DELIMITER;

    public Code() {
        tickets = new TreeSet<String>();
        performances = new TreeSet<String>();
        events = new TreeSet<String>();
        sellers = new TreeSet<String>();
    }

    public Code (PTicket pTicket) {
        this();

        if(pTicket == null) {
            return;
        }

        if(pTicket.getId() != null) {
            this.id = IdAdapter.toString(pTicket.getId());
        }

        if(pTicket.get("enabled") != null) {
            this.enabled = Boolean.parseBoolean(pTicket.get("enabled"));
        }

        this.description = pTicket.get("description");
        this.code = pTicket.get("code");


        if(pTicket.get("price") != null) {
            this.price = Integer.parseInt(pTicket.get("price"));
        }

        if(pTicket.get("startDate") != null) {
            try {
                this.startDate = DateUtil.parseDate(pTicket.get("startDate"));
            } catch (ParseException ignored) {
                logger.info(ignored.getMessage());
            }
        }

        if(pTicket.get("endDate") != null) {
            try {
                this.endDate = DateUtil.parseDate(pTicket.get("endDate"));
            } catch (ParseException ignored) {
                logger.info(ignored.getMessage());
            }
        }

        if(pTicket.get("sellers") != null) {
            List<String> sellers = pTicket.getProps().get("sellers");
            for(String seller : sellers) {
                this.sellers.add(seller);
            }
        }
    }

    public Set<String> getEvents() {
        return events;
    }

    public void setEvents(Set<String> events) {
        this.events = events;
    }

    public Set<String> getPerformances() {
        return performances;
    }

    public void setPerformances(Set<String> performances) {
        this.performances = performances;
    }

    public String getCode() {
        return code;
    }

    public String getCodeAsFieldName() {
        return FIELD_PREFIX + code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getEligibleClients() {
        return sellers;
    }

    public void setEligibleClients(Set<String> eligibleClients) {
        this.sellers = eligibleClients;
    }

    public Set<String> getTickets() {
        return tickets;
    }

    public void setTickets(Set<String> tickets) {
        this.tickets = tickets;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Set<String> getSellers() {
        return sellers;
    }

    public void setSellers(Set<String> sellers) {
        this.sellers = sellers;
    }

    
    public PTicket toRecord() {
        PTicket pTicket = new PTicket();
        pTicket.setId(id);
        pTicket.setType("code");

        if(enabled != null) {
            pTicket.put("enabled", Boolean.toString(enabled));
        }

        if(description != null) {
            pTicket.put("description", description);
        }
        
        if(code != null) {
            pTicket.put("code", code);
        }

        if(price != null) {
            pTicket.put("price", Integer.toString(price));
        }
        
        if(startDate != null) {
            pTicket.put("startDate", DateUtil.formatDate(startDate));
        }
        if(startDate != null) {
            pTicket.put("endDate", DateUtil.formatDate(endDate));
        }

        for(String seller : sellers) {
            pTicket.getProps().add("sellers", seller);
        }

        return pTicket;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Code other = (Code) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.code == null) ? (other.code != null) : !this.code.equals(other.code)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if (this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals(other.startDate))) {
            return false;
        }
        if (this.endDate != other.endDate && (this.endDate == null || !this.endDate.equals(other.endDate))) {
            return false;
        }
        if (this.price != other.price && (this.price == null || !this.price.equals(other.price))) {
            return false;
        }
        if (this.sellers != other.sellers && (this.sellers == null || !this.sellers.equals(other.sellers))) {
            return false;
        }
        if (this.enabled != other.enabled && (this.enabled == null || !this.enabled.equals(other.enabled))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 11 * hash + (this.code != null ? this.code.hashCode() : 0);
        hash = 11 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 11 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        hash = 11 * hash + (this.endDate != null ? this.endDate.hashCode() : 0);
        hash = 11 * hash + (this.price != null ? this.price.hashCode() : 0);
        hash = 11 * hash + (this.sellers != null ? this.sellers.hashCode() : 0);
        hash = 11 * hash + (this.enabled != null ? this.enabled.hashCode() : 0);
        return hash;
    }

    

}
