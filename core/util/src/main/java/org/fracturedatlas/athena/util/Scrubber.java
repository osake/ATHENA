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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses regular expressions to replace values of fields in JSON
 */
public class Scrubber {

    public static String SCRUBBED = "**********";

    /**
     * Scrub a JSON string.  For every field in fieldsToScrub, replace the value with SCRUBBED.
     * Both double quoted and single quoted JSON strings are supported
     *
     * Regarding RegEx, for fieldName = foo, this pattern will match:
     * foo":"some_value"
     * and group like so
     * (foo":")(some_value)(")
     *
     * @param stringToScrub the string to scrub
     * @param fieldsToScrub a List of fields in this JSON.  Their values will be replaced with SCRUBBED
     * @return the scrubbed string.
     */
    public static String scrubJson(String stringToScrub, List<String> fieldsToScrub) {

        if(stringToScrub == null || fieldsToScrub == null || fieldsToScrub.size() == 0) {
            return stringToScrub;
        }

        for(String fieldName : fieldsToScrub) {
            Pattern pattern = Pattern.compile("(" + fieldName + "[\\\\]*[\"|'][\\s]*:[\\s]*[\\\\]*[\"|'])([^\"|^\\\\]*)([\\\\]*[\"|'])");
            Matcher matcher = pattern.matcher(stringToScrub);
            stringToScrub = matcher.replaceAll("$1" + SCRUBBED + "$3");
        }

        return stringToScrub;
    }
}
