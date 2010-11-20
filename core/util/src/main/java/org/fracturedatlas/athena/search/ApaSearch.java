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

public class ApaSearch {

    ArrayList<ApaSearchConstraint> asc = null;
    Map<String, String> searchModifiers = new HashMap<String, String>();

    String type;

    public ApaSearch() {
        asc = new ArrayList<ApaSearchConstraint>();
    }

    public void addConstraint(String fieldName, Operator operator, String searchValue) {
        asc.add(new ApaSearchConstraint(fieldName, operator, searchValue));
    }

    public void addConstraint(String fieldName, Operator operator, Set<String> searchValues) {
        asc.add(new ApaSearchConstraint(fieldName, operator, searchValues));
    }

    public void addConstraint(ApaSearchConstraint searchConstraint) {
        asc.add(searchConstraint);
    }

    /*
     * TODO: refactor this out
     */
    public List<ApaSearchConstraint> asList() {
        return getConstraints();
    }

    public List<ApaSearchConstraint> getConstraints() {
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
        ApaSearch search;

        public Builder(ApaSearchConstraint sc) {
            this.search = new ApaSearch();
            this.search.addConstraint(sc);
        }

        public Builder() {
            this.search = new ApaSearch();
        }

        public ApaSearch build() {
            return search;
        }

        public ApaSearch.Builder and(ApaSearchConstraint sc) {
            search.addConstraint(sc);
            return this;
        }

        public ApaSearch.Builder limit(Integer limit) {
            search.setSearchModifier("_limit", limit.toString());
            return this;
        }

         public ApaSearch.Builder start(Integer start) {
            search.setSearchModifier("_start", start.toString());
            return this;
        }

         public ApaSearch.Builder type(String type) {
            search.setType(type);
            return this;
        }

    }


}
