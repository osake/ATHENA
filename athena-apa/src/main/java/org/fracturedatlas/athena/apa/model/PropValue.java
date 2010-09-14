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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fracturedatlas.athena.id.IdAdapter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROP_VALUES")
public class PropValue extends TixEntity implements Serializable {

    @Id
    @Type(type = "org.fracturedatlas.athena.apa.impl.LongUserType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlJavaTypeAdapter(IdAdapter.class)
    Object id;

    @ManyToOne
    @JoinColumn(name="PROP_FIELD_ID")
    PropField propField;

    String propValue;

    public PropValue() {
    }

    public PropValue(PropField propField, String propValue) {
        this.propField = propField;
        this.propValue = propValue;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public PropField getPropField() {
        return propField;
    }

    public void setPropField(PropField propField) {
        this.propField = propField;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PropValue other = (PropValue) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (propField == null) {
            if (other.propField != null) {
                return false;
            }
        } else if (!propField.equals(other.propField)) {
            return false;
        }
        if (propValue == null) {
            if (other.propValue != null) {
                return false;
            }
        } else if (!propValue.equals(other.propValue)) {
            return false;
        }
        return IdAdapter.isEqual(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((propField == null) ? 0 : propField.hashCode());
        result = prime * result
                + ((propValue == null) ? 0 : propValue.hashCode());
        return result;
    }
}
