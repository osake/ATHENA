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

public enum Operator {

    IN("in", "IN (:value)"),
    LESS_THAN("lt", "< :value"),
    GREATER_THAN("gt", "> :value"),

    //For now, only '.*' will be supported
    MATCHES("ma", ""),
    EQUALS("eq", "= :value");

    private String operatorString;
    private String operatorType;

    private Operator(String operatorType, String operatorString) {
        this.operatorType = operatorType;
        this.operatorString = operatorString;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public String getOperatorString() {
        return operatorString;
    }

    public static Operator fromType(String text) {
        if (text != null) {
            for (Operator op : Operator.values()) {
                if (text.equalsIgnoreCase(op.operatorType)) {
                    return op;
                }
            }
        }
        return null;
    }

       public static Operator fromString(String text) {
        if (text != null) {
            for (Operator op : Operator.values()) {
                if (text.equalsIgnoreCase(op.operatorString)) {
                    return op;
                }
            }
        }
        return null;
    }

}
