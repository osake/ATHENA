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

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Table;

@Entity
@DiscriminatorValue("BOOLEAN")
public class BooleanTicketProp extends TicketProp implements Serializable {

    @Column(name="valueBoolean")
    Boolean value;

    public BooleanTicketProp() {
      super();
    }

    public BooleanTicketProp(PropField propField, Boolean value) {
      super();
      setValue(value);
      setPropField(propField);
    }

    public Boolean getValue() {
        return value;
    }
    
    public void setValue(String s) {
        setValue(Boolean.parseBoolean(s));
    }

    public void setValue(Object o) {
        setValue((Boolean)o);
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public int compareTo(Object o) throws ClassCastException {
        Boolean b = Boolean.valueOf((String)o);
        return value.compareTo(b);
    }
    
    public String getValueAsString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BooleanTicketProp other = (BooleanTicketProp) obj;
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }


}
