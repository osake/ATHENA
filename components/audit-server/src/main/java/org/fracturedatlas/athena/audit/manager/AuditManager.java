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
package org.fracturedatlas.athena.audit.manager;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang.StringUtils;
import org.fracturedatlas.athena.audit.model.AuditMessage;
import org.fracturedatlas.athena.audit.persist.AuditPersistance;
import org.fracturedatlas.athena.client.audit.PublicAuditMessage;
import org.fracturedatlas.athena.search.AthenaSearch;
import org.fracturedatlas.athena.search.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditManager {

    @Autowired
    AuditPersistance auditPersistance;

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());


    public PublicAuditMessage saveAuditMessage(PublicAuditMessage auditMessage) throws Exception {
        AuditMessage am = new AuditMessage(auditMessage);
        am = auditPersistance.saveAuditMessage(am);
        return am.toPublicMessage();
    }

    public AuditMessage[] getAuditMessages(MultivaluedMap<String, String> queryParams) {
        List<String> values = null;
        Operator operator;
        String value;
        Set<String> valueSet = null;
        AthenaSearch athenaSearch = new AthenaSearch();
        for (String fieldName : queryParams.keySet()) {
            values = queryParams.get(fieldName);
            for (String operatorPrefixedValue : values) {
                if (fieldName.startsWith("_")) {
                    athenaSearch.setSearchModifier(fieldName, operatorPrefixedValue);
                } else {
                    operator = Operator.fromType(operatorPrefixedValue.substring(0, 2));
                    value = operatorPrefixedValue.substring(2, operatorPrefixedValue.length());
                    valueSet = parseValues(value);
                    athenaSearch.addConstraint(fieldName, operator, valueSet);
                }
            }
        }
        return auditPersistance.getAuditMessages(athenaSearch).toArray(new AuditMessage[0]);
    }

    static Set<String> parseValues(String valueString) {
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
