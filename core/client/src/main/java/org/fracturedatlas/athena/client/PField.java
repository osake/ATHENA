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
/**
 * This is the client-representation of a PropField
 */
package org.fracturedatlas.athena.client;

import java.util.ArrayList;
import java.util.Collection;

public class PField {
    Object id;
    String name;
    Boolean strict;
    String valueType;
    Collection<String> propValues;

    public PField() {
        propValues = new ArrayList<String>();
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

    public Collection<String> getPropValues() {
        return propValues;
    }

    public void setPropValues(Collection<String> propValues) {
        this.propValues = propValues;
    }

    public Boolean getStrict() {
        return strict;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String type) {
        this.valueType = type;
    }
}
