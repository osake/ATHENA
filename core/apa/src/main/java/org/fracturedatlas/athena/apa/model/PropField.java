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
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.CascadeType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.apache.commons.collections.ListUtils;

import org.fracturedatlas.athena.id.IdAdapter;
import org.hibernate.annotations.Type;
import org.fracturedatlas.athena.client.PField;

@Entity
@Table(name = "PROP_FIELDS")
public class PropField extends TixEntity implements Serializable {

    @Id
    @Type(type = "org.fracturedatlas.athena.apa.impl.LongUserType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Object id;

//    @OneToMany(mappedBy = "propField", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//    Collection<TicketProp> ticketProps;

    @OneToMany(mappedBy = "propField", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Collection<PropValue> propValues;

    @Enumerated(EnumType.STRING)
    ValueType valueType;

    Boolean strict;

    String name;

    public PropField() {
        this.propValues = new ArrayList<PropValue>();
    }

    public PropField(ValueType valueType, String name, Boolean strict) {
        this.valueType = valueType;
        this.name = name;
        this.strict = strict;
        this.propValues = new ArrayList<PropValue>();
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

    /*
     * Renamed because of a wonky problem with the Java bean spec
     */
    public Boolean getStrict() {
        return strict;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }

    public Collection<PropValue> getPropValues() {
        return propValues;
    }

    /*
     * TODO: This should REPLACE the propValues, NOT ammend them.
     */
    public void setPropValues(Collection<PropValue> propValues) {
        if (this.propValues == null) {
            this.propValues = new ArrayList<PropValue>();
        }
        this.propValues.addAll(propValues);
    }

    public void addPropValue(PropValue propValue) {
        if (this.propValues == null) {
            this.propValues = new ArrayList<PropValue>();
        }
        this.propValues.add(propValue);
    }

//    public Collection<TicketProp> getTicketProps() {
//        return ticketProps;
//    }

//    public void setTicketProps(Collection<TicketProp> ticketProps) {
//        this.ticketProps = ticketProps;
//    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public boolean equals(Object obj) {
        
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final PropField other = (PropField) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }

        if (this.strict != other.strict && (this.strict == null || !this.strict.equals(other.strict))) {
            return false;
        }

        if (!ListUtils.isEqualList(this.propValues, other.getPropValues())) {
            return false;
        }

        return IdAdapter.isEqual(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 83 * hash + (this.strict != null ? this.strict.hashCode() : 0);
        return hash;
    }

    public PField toClientField() {
        PField pField = new PField();

        pField.setPropValues(new ArrayList<String>());

        if(this.getId() != null) {
            pField.setId(this.getId().toString());
        } else {
            pField.setId(null);
        }

        pField.setName(this.getName());
        pField.setStrict(this.getStrict());

        if(this.getValueType() != null) {
            pField.setValueType(this.getValueType().toString());
        }

        for(PropValue value : this.getPropValues()) {
            pField.getPropValues().add(value.getPropValue());
        }

        return pField;
    }

//    public synchronized void addTicketProp(TicketProp tp) {
//        if (ticketProps == null) {
//            ticketProps = new Vector<TicketProp>();
//        }
//        ticketProps.add(tp);
//    }
}


