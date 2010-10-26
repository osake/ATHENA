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
import java.text.ParseException;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.apache.log4j.Logger;
import org.fracturedatlas.athena.util.date.DateUtil;

@Entity
@DiscriminatorValue("DATETIME")
public class DateTimeTicketProp extends TicketProp implements Serializable {

    @Transient
    Logger log = Logger.getLogger(this.getClass().getName());

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
        return value;
    }

    public void setValue(Object o) {
        setValue((Date)o);
    }

    public void setValue(Date value) {
        this.value = value;
    }

    public void setValue(String s) throws Exception {
        setValue(DateUtil.parseDate(s));
    }

    public String getValueAsString() {
        return DateUtil.formatDate(value);
    }

    @Override
    public int compareTo(Object o) throws ClassCastException {
        try {
            Date date = DateUtil.parseDate((String)o);
            log.debug("Comparing value [" + value + "] to input of [" + date + "]");
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
