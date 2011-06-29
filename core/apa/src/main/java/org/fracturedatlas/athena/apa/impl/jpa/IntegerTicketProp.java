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
import org.fracturedatlas.athena.apa.exception.InvalidValueException;

@Entity
@DiscriminatorValue("INTEGER")
public class IntegerTicketProp extends TicketProp implements Serializable {

    @Column(name="valueInteger")
    Integer value;

    public IntegerTicketProp() {
      super();
    }

    public IntegerTicketProp(PropField propField, Integer value) {
      super();
      setValue(value);
      setPropField(propField);
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Object o) {
        if(o == null) {
            value = null;
        } else {
            try {
                if(o.getClass().isAssignableFrom(Integer.class)) {
                    setValue((Integer)o);
                } else if(o.getClass().isAssignableFrom(String.class)) {
                    setValue((String)o); 
                } else {
                    //this'll get caught in the exception block below
                    throw new Exception("Unable to parse value into Integer");
                }
            } catch (Exception e) {
                throw new InvalidValueException(buildExceptionMessage(o.toString(), propField), e);
            }
        }
    }

    public void setValue(String s) {
        try {
            setValue(Integer.parseInt(s));
        } catch (NumberFormatException nfe) {
            throw new InvalidValueException(buildExceptionMessage(s, propField), nfe);
        }
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public int compareTo(Object o) throws ClassCastException {
        Integer i = Integer.parseInt((String)o);
        return value.compareTo(i);
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
        final IntegerTicketProp other = (IntegerTicketProp) obj;
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
