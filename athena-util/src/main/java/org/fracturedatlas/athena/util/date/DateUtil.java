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

package org.fracturedatlas.athena.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DateUtil {
    private static SimpleDateFormat iso8061Formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat shortDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

    public static Date parseDate(String date) throws ParseException {
        if(date == null) {
            return null;
        }

        try {
            return iso8061Formatter.parse(date);
        } catch (ParseException pe) {
            return shortDateFormatter.parse(date);
        }
    } 

    public static String formatDate(Date date) {
        if(date == null) {
            return null;
        } else {
            return iso8061Formatter.format(date);
        }
    }

    public static String formatTime(Date date) {
        if(date == null) {
            return null;
        } else {
            return iso8061Formatter.format(date);
        }
    }
}
