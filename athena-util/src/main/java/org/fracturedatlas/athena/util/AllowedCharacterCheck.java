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

/**
 * Utility methods for filtering allowed characters
 */
public class AllowedCharacterCheck {

	private final static Pattern forbiddenChar = Pattern.compile("\\W");
        private static final Integer MAX_STRING_LENGTH = 45;

	/**
	 * Checks that the String:
         *  - is not null
         *  - is not blank
         *  - does not contain characters other than [0-9a-zA-Z\_]
         *  - is no longer than 45 characters
	 * 
	 * @param str
	 * @return true if str does not contain characters other than [0-9a-zA-Z\_] 
	 */  
	public static boolean confirm(String str) {
            if(str == null) {
                return false;
            }

            Matcher m = forbiddenChar.matcher(str);
            return (!m.find() && !StringUtils.isBlank(str) && str.length() < MAX_STRING_LENGTH);
	}
}
