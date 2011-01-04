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
package org.fracturedatlas.athena.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for filtering allowed characters
 */
public class AllowedCharacterCheck {

	private final static Pattern forbiddenChar = Pattern.compile("\\W");
    private static final Integer MAX_STRING_LENGTH = 45;
    static Logger logger = LoggerFactory.getLogger(AllowedCharacterCheck.class.getName());

	/**
	 * Checks that the String:
         *  - is not a "Reserved" field name.  Right now the only one we prevent is "id"
         *  - is not null
         *  - is not blank
         *  - does not contain characters other than [0-9a-zA-Z\_]
         *  - is no longer than 45 characters
	 * 
	 * @param str
	 * @return true if str does not contain characters other than [0-9a-zA-Z\_] 
	 */  
	public static boolean confirm(String str) {
        logger.info("Confirming [{}]", str);
        
            if(str == null) {
                logger.info("String is null");
                return false;
            }

            //TODO: This should be moved to some config file
            if("id".equals(str)) {
                logger.info("This string equals [id]");
                return false;
            }

            Matcher m = forbiddenChar.matcher(str);
            logger.info("Checking against regex \\w");
            boolean worked = (!m.find() && !StringUtils.isBlank(str) && str.length() < MAX_STRING_LENGTH);

            if(!worked) {
                logger.info("Regex failed to match");
            } else {
                logger.info("Field name check passed");
            }

            return worked;
	}
}
