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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class ApaSearchConstraint {

    Operator oper = null;
    String parameter = null;
    Set<String> valueSet = null;

    public ApaSearchConstraint(String param, Operator operator, String val) {
        parameter = param;
        oper = operator;
        valueSet = new HashSet<String>();
        valueSet.add(val);
    }

    public ApaSearchConstraint(String param, Operator operator, Set<String> values) {
        parameter = param;
        oper = operator;
        valueSet = values;
    }


    public Operator getOper() {
        return oper;
    }

    public void setOper(Operator oper) {
        this.oper = oper;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getValue() {
        return valueSet.iterator().next();
    }

    public Set<String> getValueSet() {
        return valueSet;
    }

    public void setValueSet(Set<String> values) {
        this.valueSet = values;
    }

    @Override
    public String toString() {
            return parameter + " " + oper + " (" + valueSet + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ApaSearchConstraint other = (ApaSearchConstraint) obj;
        if (this.oper != other.oper && (this.oper == null || !this.oper.equals(other.oper))) {
            return false;
        }
        if ((this.parameter == null) ? (other.parameter != null) : !this.parameter.equals(other.parameter)) {
            return false;
        }
        if ((this.valueSet == null) ? (other.valueSet != null) : !this.valueSet.equals(other.valueSet)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.oper != null ? this.oper.hashCode() : 0);
        hash = 61 * hash + (this.parameter != null ? this.parameter.hashCode() : 0);
        hash = 61 * hash + (this.valueSet != null ? this.valueSet.hashCode() : 0);

        return hash;
    }

    
}
