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
package org.fracturedatlas.athena.apa.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.fracturedatlas.athena.id.IdAdapter;
import org.fracturedatlas.athena.client.PTicket;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "TICKETS")
public class Ticket extends TixEntity implements Serializable {

    @Id
    @Type(type = "org.fracturedatlas.athena.apa.impl.LongUserType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Object id;

    @OneToMany(mappedBy = "ticket",
        targetEntity = TicketProp.class,
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL)
    Collection<TicketProp> ticketProps;

    String name;

    public Ticket() {
        ticketProps = new ArrayList<TicketProp>();
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<TicketProp> getTicketProps() {
        return ticketProps;
    }

    public void setTicketProps(Collection<TicketProp> ticketProps) {
        this.ticketProps = ticketProps;
    }

    /**
     * Add a prop to this ticket.  If the prop exists on this ticket already, it will be replaced
     * @param prop
     */
    @Transient
    public void setTicketProp(TicketProp prop) throws Exception {
        String propName = prop.getPropField().getName();
        TicketProp existingProp = getTicketProp(propName);
        if(existingProp == null) {
            addTicketProp(prop);
        } else {
            existingProp.setValue(prop.getValue());
        }
    }

    /**
     * Add a prop to this ticket's props EVEN IF THIS PROP DUPLICATES AN EXISTING PROP
     * To avoid this, either use apa.savePropValue OR use ticket.setTicketProp
     *
     * @param prop
     */
    public void addTicketProp(TicketProp prop) {
        if (this.ticketProps == null) {
            this.ticketProps = new ArrayList<TicketProp>();
        }
        prop.setTicket(this);
        this.ticketProps.add(prop);
    }

    public TicketProp getTicketProp(String propName) {
        for(TicketProp prop : this.ticketProps) {
            if(propName.equals(prop.getPropField().getName())) {
                return prop;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        final Ticket other = (Ticket) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }

        return IdAdapter.isEqual(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append(id).append(name);


        if (ticketProps != null) {
            for (TicketProp prop : ticketProps) {
                String propAndValue = "(" + prop.getPropField().getId() + ") " + prop.getPropField().getName() + ": [" + prop.getValueAsString() + "]";
                builder.append(propAndValue);
            }
        }
        return builder.toString();
    }

    public PTicket toClientTicket() {
        PTicket pTicket = new PTicket();

        pTicket.setName(this.getName());
        if(this.getId() != null) {
            pTicket.setId(this.getId().toString());
        } else {
            pTicket.setId(null);
        }


        for(TicketProp prop : this.getTicketProps()) {
            pTicket.getProps().put(prop.getPropField().getName(), prop.getValueAsString());
        }

        return pTicket;
    }
}
