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
import java.util.Set;

public class AthenaSearch {

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

    /*
     * TODO: refactor this out
     */
    public List<AthenaSearchConstraint> asList() {
        return getConstraints();
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

        public AthenaSearch.Builder limit(Integer limit) {
            search.setSearchModifier("_limit", limit.toString());
            return this;
        }

         public AthenaSearch.Builder start(Integer start) {
            search.setSearchModifier("_start", start.toString());
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




}
