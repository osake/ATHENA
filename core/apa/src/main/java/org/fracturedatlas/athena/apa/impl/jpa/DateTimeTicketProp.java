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
import java.text.ParseException;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.fracturedatlas.athena.apa.exception.InvalidValueException;
import org.fracturedatlas.athena.util.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("DATETIME")
public class DateTimeTicketProp extends TicketProp implements Serializable {

    @Transient
    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="valueDateTime")
    Date value;

    public DateTimeTicketProp() {
      super();
    }

    public DateTimeTicketProp(PropField propField, Date value) {
      super();
      setValue(value);
      setPropField(propField);
    }

    public Date getValue() {
        return (Date)value;
    }

    public void setValue(Object o) {
        setValue((Date)o);
    }

    public void setValue(Date value) {
        this.value = value;
    }

    public void setValue(String s) throws InvalidValueException {
        try {
            setValue(DateUtil.parseDate(s));
        } catch (ParseException pe) {
            throw new InvalidValueException(buildExceptionMessage(s, propField), pe);
        }
    }

    public String getValueAsString() {
        return DateUtil.formatDate(value);
    }

    @Override
    public int compareTo(Object o) throws ClassCastException {
        try {
            Date date = DateUtil.parseDate((String)o);
            log.debug("Comparing value [{}] to input of [{}]", value, date);
            return value.compareTo(date);
        } catch (ParseException pe) {
            throw new ClassCastException("Could not make a date out of this [" + o + "]");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DateTimeTicketProp other = (DateTimeTicketProp) obj;

        if (this.value!=null) {
            if (other.value!=null) {
                final Long thisDate = this.getValue().getTime();
                final Long otherDate = other.getValue().getTime();
                if (!thisDate.equals(otherDate)) {
                    return false;
                }
            } else {
                return false;
            }
        } else if (other.value!=null) {
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
