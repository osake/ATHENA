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
package org.fracturedatlas.athena.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.fracturedatlas.athena.client.PTicket;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ticket extends ApaModel { 

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    
    String id;
    String performanceId;
    DateTime performance;
    String section;
    Integer price;
    String venue;
    String eventId;
    String event;
    String buyerId;
    String state;
    String organizationId;
    String soldPrice;
    DateTime soldAt;
    List<String> tags;
    
    public Ticket(PTicket pTicket) {
        super(pTicket);
        fromPTicket(pTicket);
    }
    
    
    public Ticket(PTicket section, PTicket performance, PTicket event, String state) {
        super();
        this.price = Integer.parseInt(section.get("price"));
        this.performanceId = performance.getIdAsString();
        try {
            this.performance = DateUtil.parseDateTime(performance.get("datetime"));
        } catch (ParseException ex) {
            logger.error("Could not parse parformance date [{}]", performance.get("performance"));
            setPerformance(null);
        }
        this.organizationId = performance.get("organizationId");
        this.section = section.get("name");
        this.venue = event.get("venue");
        this.event = event.get("name");
        this.eventId = event.getIdAsString();
        this.state = state;
        
        this.organizationId = performance.get("organizationId");
    }
    
    public static List<Ticket> fromCollection(List<PTicket> pTickets) {
        List<Ticket> tickets = new ArrayList<Ticket>();
        for(PTicket pTicket : pTickets) {
            tickets.add(new Ticket(pTicket));
        }
        return tickets;
    }
    
    public static List<PTicket> toCollection(List<Ticket> tickets) {
        List<PTicket> pTickets = new ArrayList<PTicket>();
        for(Ticket ticket : tickets) {
            pTickets.add(ticket.toPTicket());
        }
        return pTickets;
    }

    public PTicket toPTicket() {
        PTicket pTicket = new PTicket();
        pTicket.setId(this.id);
        pTicket.putIfNotNull("performanceId", performanceId);
        pTicket.putIfNotNull("performance", DateUtil.formatDate(performance));
        pTicket.putIfNotNull("section", section);
        pTicket.putIfNotNull("price", price.toString());
        pTicket.putIfNotNull("venue", venue);
        pTicket.putIfNotNull("eventId", eventId);
        pTicket.putIfNotNull("event", event);
        pTicket.putIfNotNull("buyerId", buyerId);
        pTicket.putIfNotNull("state", state);
        pTicket.putIfNotNull("organizationId", organizationId);
        pTicket.putIfNotNull("soldPrice", soldPrice);
        pTicket.putIfNotNull("soldAt", DateUtil.formatDate(soldAt));
        if(tags != null) {
            pTicket.getProps().put("tags", tags);
        }
        return pTicket;
    }
    
    public void fromPTicket(PTicket pTicket) {
        setId(pTicket.getIdAsString());
        setPerformanceId(pTicket.get("performanceId"));
        try {
            setPerformance(DateUtil.parseDateTime(pTicket.get("performance")));
        } catch (ParseException ex) {
            logger.error("Could not parse parformance date [{}]", pTicket.get("performance"));
            setPerformance(null);
        }
        setSection(pTicket.get("section"));
        setPrice(Integer.parseInt(pTicket.get("price")));        
        setVenue(pTicket.get("venue"));
        setEventId(pTicket.get("eventId"));
        setEvent(pTicket.get("event"));
        setBuyerId(pTicket.get("buyerId"));
        setState(pTicket.get("state"));
        setOrganizationId(pTicket.get("organizationId"));
        setSoldPrice(pTicket.get("soldPrice"));
        try {
            setSoldAt(DateUtil.parseDateTime(pTicket.get("soldAt")));
        } catch (ParseException ex) {
            logger.error("Could not parse soldAt [{}]", pTicket.get("soldAt"));
            setSoldAt(null);
        }
        setTags(pTicket.getProps().get("tags"));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public DateTime getPerformance() {
        return performance;
    }

    public void setPerformance(DateTime performance) {
        this.performance = performance;
    }

    public String getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(String performanceId) {
        this.performanceId = performanceId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public DateTime getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(DateTime soldAt) {
        this.soldAt = soldAt;
    }

    public String getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(String soldPrice) {
        this.soldPrice = soldPrice;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
    
    
}
