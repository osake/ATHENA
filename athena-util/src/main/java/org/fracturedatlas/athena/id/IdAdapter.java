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
package org.fracturedatlas.athena.id;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class IdAdapter extends XmlAdapter<String, Object> {
    public Object unmarshal(String s) {
        return s;
    }
     
    public String marshal(Object o) {
        if(Long.class.isAssignableFrom(o.getClass())) {
            return Long.toString((Long)o);
        } if(Integer.class.isAssignableFrom(o.getClass())) {
            return Integer.toString((Integer)o);
        } else if (String.class.isAssignableFrom(o.getClass())) {
            return (String)o;
        } else {
            throw new RuntimeException("Unable to unmarshall type: " + o.getClass().getName());
        }
    }

    /**
     * Test equality between two Athena Ids
     *
     * TODO: This should use the .equals method of the underlying class
     */
    public static boolean isEqual(Object id1, Object id2) {
        if (id1 == null && id2 != null) {
            return false;
        } else if (id1 != null && id2 == null) {
            return false;
        } else if (id1 == null && id2 == null) {
            return true;
        }

        String id1Str;
        String id2Str;

        if(Long.class.isAssignableFrom(id1.getClass())) {
            id1Str = Long.toString((Long)id1);
        } else if (Integer.class.isAssignableFrom(id1.getClass())) {
            id1Str = Integer.toString((Integer)id1);
        } else {
            id1Str = (String)id1.toString();
        }

        if(Long.class.isAssignableFrom(id2.getClass())) {
            id2Str = Long.toString((Long)id2);
        } else if (Integer.class.isAssignableFrom(id1.getClass())) {
            id2Str = Integer.toString((Integer)id2);
        } else {
            id2Str = (String)id2.toString();
        }

        return id1Str.equals(id2Str);
    }
}
