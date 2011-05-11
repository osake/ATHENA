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
package org.fracturedatlas.athena.apa.impl.jpa;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.client.PTicket;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Having to use ForceDiscriminator here really blows up JPA compatibility.
 * Big mistake on Hibernate's part.  See
 *
 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-4358
 *
 * and
 *
 * http://opensource.atlassian.com/projects/hibernate/browse/ANN-36
 */
@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "ticketProp")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "propType", discriminatorType = DiscriminatorType.STRING)
@Table(name = "TICKET_PROPS")
@org.hibernate.annotations.ForceDiscriminator
public abstract class TicketProp extends TixEntity implements Serializable, Comparable {

    @Id
    @Type(type = "org.fracturedatlas.athena.apa.impl.LongUserType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Object id;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)            //TODO: more JPA breakage, can be mitigated by unraveling all these EAGER associations
    @JoinColumn(name="PROP_FIELD_ID")
    PropField propField;

    @ManyToOne(fetch = FetchType.EAGER,
               cascade=CascadeType.REFRESH)
    @JoinColumn(name="TICKET_ID")
    JpaRecord ticket;

    @XmlElement(name="id")
    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    @XmlElement(name="field")
    public PropField getPropField() {
        return propField;
    }

    public void setPropField(PropField propField) {
        this.propField = propField;
    }

    public JpaRecord getTicket() {
        return ticket;
    }

    public void setTicket(JpaRecord ticket) {
        this.ticket = ticket;
    }

    @Transient
    @XmlElement(name="value")
    public abstract String getValueAsString();

    @Transient
    public abstract Object getValue();

    @Transient
    public abstract void setValue(String s) throws InvalidValueException;

    @Transient
    public abstract void setValue(Object o) throws InvalidValueException;


    public abstract int compareTo(Object o) throws ClassCastException,
            IllegalArgumentException;

    /**
     * A convenience method for calling compareTo.  It wall also conveniently
     * eat any ClassCastExceptions or IllegalArgumentExceptions that arise from the caller sending in
     * uncastable data (which is, unfortunately, a probable scenario)
     */
    public Boolean valueEquals(Object o) {
        Boolean isSame = Boolean.FALSE;
        try {
            isSame = compareTo(o) == 0;
        } catch (ClassCastException e) {
            Logger logger = LoggerFactory.getLogger(this.getClass().getName());
            logger.error(e.getMessage(),e);
        } catch (IllegalArgumentException e) {
            Logger logger = LoggerFactory.getLogger(this.getClass().getName());
            logger.error(e.getMessage(),e);

        }
        return isSame;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TicketProp other = (TicketProp) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.propField != other.propField && (this.propField == null || !this.propField.equals(other.propField))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 71 * hash + (this.propField != null ? this.propField.hashCode() : 0);
        return hash;
    }

    protected String buildExceptionMessage(String val, PropField propField) {
        String propName = "";
        String err = "";
        if(propField != null) {
            propName = propField.getName();
            err = "Value [" + val + "] is not a valid value for the field [" + propField.getName() + "].  ";
            err += "Field is of type [" + propField.getValueType().name() + "].";
        } else {
            err = "Value [" + val + "] is not a valid value for this field";
        }

        return err;
    }

    public Boolean isSystemProp() {
        return this.propField.getName().contains(PTicket.SYSTEM_PROP_DELIMITER);
    }
 
}
