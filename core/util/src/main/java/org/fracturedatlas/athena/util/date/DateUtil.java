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
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DateUtil {
    public static DateTimeFormatter ATHENA_DATE_TIME_FORMAT = ISODateTimeFormat.dateTimeNoMillis();
    public static DateTimeFormatter ATHENA_DATE_FORMAT = ISODateTimeFormat.date();

    DateTime datetime;

    /**
     * Will parse a date formatted in iso8061 (example: 2010-10-01T13:33:50-04:00) into a java.util.Date.  If parsing fails,
     * this method will attempt to parse using the format 'yyyy-MM-dd".  If both fail, this method will
     * throw a ParseException
     *
     * @param iso8061StrDateTime the date to format
     * @return a java.util.Date representing iso8061StrDateTime
     * @throws ParseException if the date isn't formatte properly
     */
    public static Date parseDate(String iso8061StrDateTime) throws ParseException {
        if(iso8061StrDateTime == null) {
            return null;
        }

        try{
            return ATHENA_DATE_TIME_FORMAT.parseDateTime(iso8061StrDateTime).toDate();
        } catch (IllegalArgumentException iae) {
            return ATHENA_DATE_FORMAT.parseDateTime(iso8061StrDateTime).toDate();
        }
    }

    public static DateTime parseDateTime(String iso8061StrDateTime) throws ParseException {
        if(iso8061StrDateTime == null) {
            return null;
        }

        try{
            return ATHENA_DATE_TIME_FORMAT.parseDateTime(iso8061StrDateTime);
        } catch (IllegalArgumentException iae) {
            return ATHENA_DATE_FORMAT.parseDateTime(iso8061StrDateTime);
        }
    }

    public static String formatDate(Date date) {
        if(date == null) {
            return null;
        } else {
            return ATHENA_DATE_TIME_FORMAT.print(date.getTime());
        }
    }

    public static String formatTime(Date date) {
        if(date == null) {
            return null;
        } else {
            return ATHENA_DATE_TIME_FORMAT.print(date.getTime());
        }
    }

    public static String formatDate(DateTime date) {
        if(date == null) {
            return null;
        } else {
            return ATHENA_DATE_TIME_FORMAT.print(date);
        }
    }

    public static String formatTime(DateTime date) {
        if(date == null) {
            return null;
        } else {
            return ATHENA_DATE_TIME_FORMAT.print(date);
        }
    }
}
