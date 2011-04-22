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
package org.fracturedatlas.athena.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AthenaSearch {

    public static final String LIMIT = "_limit";
    public static final String START = "_start";
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    
    ArrayList<AthenaSearchConstraint> asc = null;
    Map<String, String> searchModifiers = new HashMap<String, String>();

    String type;

    public AthenaSearch() {
        asc = new ArrayList<AthenaSearchConstraint>();
    }

    public void addConstraint(String fieldName, Operator operator, String searchValue) {
        asc.add(new AthenaSearchConstraint(fieldName, operator, searchValue));
    }

    public void addConstraint(String fieldName, Operator operator, Set<String> searchValues) {
        asc.add(new AthenaSearchConstraint(fieldName, operator, searchValues));
    }

    public void addConstraint(AthenaSearchConstraint searchConstraint) {
        asc.add(searchConstraint);
    }

    public List<AthenaSearchConstraint> getConstraints() {
        return asc;
    }

    public void setSearchModifier(String modifierName, String modifierValue) {
        searchModifiers.put(modifierName, modifierValue);
    }

    public String getSearchModifier(String modifierName) {
        return searchModifiers.get(modifierName);
    }

    public Map<String, String> getSearchModifiers() {
        return searchModifiers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the limit of this search
     * @return the limit, null if limit has not been set
     */
    public Integer getLimit() {
        return getIntegerModifier(LIMIT);
    }

    /**
     * Get the start of this search
     * @return the start, null if start has not been set
     */    
    public Integer getStart() {
        return getIntegerModifier(START);
    }

    private Integer getIntegerModifier(String modifierName) {
        Integer modifierValue = null;
        String stringVal = getSearchModifier(modifierName);
        if (stringVal != null) {
            try {
                modifierValue = Integer.parseInt(stringVal);
            } catch (NumberFormatException ex) {
                logger.info("{} parameter for AthenaSearch was malformed [{}]", modifierName, stringVal);
                logger.info("Continuing with search");
            }
        }

        return modifierValue;
    }

    public static class Builder {
        AthenaSearch search;

        public Builder(AthenaSearchConstraint sc) {
            this.search = new AthenaSearch();
            this.search.addConstraint(sc);
        }

        public Builder() {
            this.search = new AthenaSearch();
        }

        public AthenaSearch build() {
            return search;
        }

        public AthenaSearch.Builder and(AthenaSearchConstraint sc) {
            search.addConstraint(sc);
            return this;
        }

        public AthenaSearch.Builder and(String param, Operator operator, String value) {
            search.addConstraint(new AthenaSearchConstraint(param, operator, value));
            return this;
        }

        public AthenaSearch.Builder limit(Integer limit) {
            search.setSearchModifier(AthenaSearch.LIMIT, limit.toString());
            return this;
        }

         public AthenaSearch.Builder start(Integer start) {
            search.setSearchModifier(AthenaSearch.START, start.toString());
            return this;
        }

         public AthenaSearch.Builder type(String type) {
            search.setType(type);
            return this;
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
        final AthenaSearch other = (AthenaSearch) obj;
        if (this.asc != other.asc && (this.asc == null || !this.asc.equals(other.asc))) {
            return false;
        }
        if (this.searchModifiers != other.searchModifiers && (this.searchModifiers == null || !this.searchModifiers.equals(other.searchModifiers))) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.asc != null ? this.asc.hashCode() : 0);
        hash = 23 * hash + (this.searchModifiers != null ? this.searchModifiers.hashCode() : 0);
        hash = 23 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }


    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("type [").append(getType()).append("]\n");

        for(Entry<String, String> entry : searchModifiers.entrySet()) {
           buf.append(entry.getKey()).append(" [").append(getType()).append("]\n");
        }

        for(AthenaSearchConstraint con : getConstraints()) {
            buf.append(con.toString()).append("\n");
        }

        return buf.toString();
    }


}
