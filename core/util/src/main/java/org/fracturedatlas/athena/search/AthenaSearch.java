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

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.fracturedatlas.athena.exception.AthenaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AthenaSearch {

    public static final String LIMIT = "_limit";
    public static final String START = "_start";
    public static final String INCLUDE = "_include";
    public static final String QUERY = "_q";
    public static final String ANY_VALUE = ".*";
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    
    ArrayList<AthenaSearchConstraint> asc = new ArrayList<AthenaSearchConstraint>();
    List<String> includes = new ArrayList<String>();
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
    
    public Boolean isQuerySearch() {
        return getSearchModifiers().get(QUERY) != null;
    }
    
    public String getQuery() {
        return getSearchModifier(QUERY);
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

    public List<String> getIncludes() {
        return includes;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
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

        public AthenaSearch.Builder include(String subType) {
            search.getIncludes().add(subType);
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

         public AthenaSearch.Builder query(String searchTerm) {
            search.setSearchModifier(AthenaSearch.QUERY, searchTerm);
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
    
    public AthenaSearch(Map<String, List<String>> queryParams) {
        
        List<String> values = null;
        Operator operator;
        String value;
        Set<String> valueSet = null;
        for (String fieldName : queryParams.keySet()) {
            values = queryParams.get(fieldName);

            if(values == null || values.size() == 0) {
                throw new AthenaException("Found no values for search parameter ["+fieldName+"]");
            }

            for (String operatorPrefixedValue : values) {
                if(StringUtils.isBlank(operatorPrefixedValue)) {
                    throw new AthenaException("Found no values for search parameter ["+fieldName+"]");
                }
                if (fieldName.startsWith("_")) {
                    this.setSearchModifier(fieldName, operatorPrefixedValue);
                } else {
                    int start = 0;

                    if(operatorPrefixedValue.length() < 2) { 
                        operator = Operator.EQUALS;
                        value = operatorPrefixedValue;
                    } else {
                        //If the operator isn't found, this defaults to equals
                        operator = Operator.fromType(operatorPrefixedValue.substring(0, 2));
                        start = 2;

                        if(operator == null) {
                            operator = Operator.EQUALS;
                            start = 0;
                        }
                        value = operatorPrefixedValue.substring(start, operatorPrefixedValue.length());
                    }
                    if(StringUtils.isBlank(value)) {
                        throw new AthenaException("Found no values for search parameter ["+fieldName+"]");
                    }

                    valueSet = parseValues(value);
                    this.addConstraint(fieldName, operator, valueSet);
                }
            }
        }    
    }

    public static Set<String> parseValues(String valueString) {
        HashSet<String> values = new HashSet<String>();
        valueString = StringUtils.trimToEmpty(valueString);
        valueString = StringUtils.strip(valueString, "()");
        valueString = StringUtils.trimToEmpty(valueString);
        CharacterIterator it = new StringCharacterIterator(valueString);
        boolean inString = false;
        int begin = 0;
        int end = 0;
        int numValues = 0;
        StringBuilder sb = new StringBuilder();
        // Iterate over the characters in the forward direction
        for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
            if (ch == '\"') {
                inString = true;
                ch = it.next();
                sb = new StringBuilder();
                for (; ch != CharacterIterator.DONE; ch = it.next()) {
                    if (ch == '\\') {
                        // skip any " in a string
                        sb.append(ch);
                        ch = it.next();
                    } else if (ch == '\"') {
                        break;
                    }
                    sb.append(ch);
                }
                inString = false;
                values.add(StringUtils.trimToEmpty(sb.toString()));
            } else if (ch == ',') {
                // new value
            } else if (" \t\n\r".indexOf(ch) > -1) {
                //skip whitespace
            } else {
                // not a comma, whitespace or a string start
                sb = new StringBuilder();
                for (; ch != CharacterIterator.DONE; ch = it.next()) {
                    if (ch == ',') {
                        break;
                    }
                    sb.append(ch);
                }
                inString = false;
                values.add(StringUtils.trimToEmpty(sb.toString()));

            }
        }

        return values;
    }


}
