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

package org.fracturedatlas.athena.client;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.fracturedatlas.athena.id.*;

/**
 * This is the client-representation of a record
 */
public class PTicket {

    private Object id;
    private String type;
    private MultivaluedMapImpl props;

    public PTicket() {
        props = new MultivaluedMapImpl();
    }
    public PTicket(String type) {
        this();
        this.type = type;
    }

    public Object getId() {
        return id;
    }

    public String getIdAsString() {
        return IdAdapter.toString(id);
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MultivaluedMapImpl getProps() {
        return props;
    }

    /**
     * Shorthand method to get a property from thie ticket's list of properties
     *
     * @param key
     * @return the value if it exists
     */
    public String get(String key) {
        return getAsString(key);
    }

    /**
     * Shorthand method to get a property from thie ticket's list of properties
     *
     * @param key
     * @return the value if it exists
     */
    public String getAsString(String key) {
        return getProps().getFirst(key);
    }

    public List<String> getAsList(String key) {
        return getProps().get(key);
    }

    /**
     * Shorthand method to set a property to thie ticket's list of properties
     *
     * @param key
     * @return the value if it exists
     */
    public void put(String key, String value) {
        getProps().putSingle(key, value);
    }
    
    public void add(String key, String value) {
        getProps().add(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PTicket other = (PTicket) obj;
        if (this.id != other.id && (this.id == null || !IdAdapter.isEqual(this.id, other.id))) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if (this.props != other.props && (this.props == null || !this.props.equals(other.props))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 79 * hash + (this.props != null ? this.props.hashCode() : 0);
        return hash;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                                                  ToStringStyle.MULTI_LINE_STYLE);
    }
    
}
