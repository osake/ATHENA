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
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("TEXT")
public class TextTicketProp extends TicketProp implements Serializable {

    //The LONG VARCHAR mapping is for in memory Derby DB.
    //SQL servers should sue the TEXT definition from the ddl
    @Column(name="valueText", columnDefinition="CLOB")
    String value;

    public TextTicketProp() {
      super();
    }

    public TextTicketProp(PropField propField, String value) {
      super();
      setValue(value);
      setPropField(propField);
    }

    public String getValue() {
        return value;
    }

    public void setValue(Object o) {
        setValue((String)o);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueAsString() {
        return value;
    }

    @Override
    public int compareTo(Object o) throws ClassCastException {
        String s = (String)o;
        return value.compareTo(s);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StringTicketProp other = (StringTicketProp) obj;
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
