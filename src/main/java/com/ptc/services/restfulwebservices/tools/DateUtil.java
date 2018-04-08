/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.restfulwebservices.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import mks.util.CurrentTimeZone;

/**
 *
 * @author veckardt
 */
public class DateUtil {

    private DateUtil() {
    }

    /**
     * subtractDay
     *
     * @param date
     * @param days
     * @return
     */
    public static Date subtractDay(Date date, int days) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days * -1);
        return cal.getTime();
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static SimpleDateFormat getTimestampFormat() {
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATETIMEONLY_FORMAT);
        df.setTimeZone(CurrentTimeZone.get());
        return df;
    }

    public static SimpleDateFormat getDateOnlyFormat(Date d) {
        SimpleDateFormat df = null;
        TimeZone defaultTZ = CurrentTimeZone.get();
        if (d == null) {
            df = new SimpleDateFormat(DEFAULT_DATEONLY_FORMAT);
            df.setTimeZone(defaultTZ);
        } else {
            TimeZone dateTZ = calculateTimeZone(d);
            if (dateTZ.getOffset(d.getTime()) == defaultTZ.getOffset(d.getTime())) {
                df = new SimpleDateFormat(DEFAULT_DATEONLY_FORMAT);
                df.setTimeZone(defaultTZ);
            } else {
                df = new SimpleDateFormat(DEFAULT_DATEZ_FORMAT);
                df.setTimeZone(dateTZ);
            }
        }
        return df;
    }

    public static Date applyTimeZoneToDate(Date orignalDate, TimeZone newTimeZone) {
        Calendar properCal = Calendar.getInstance(newTimeZone);
        properCal.setTimeInMillis(orignalDate.getTime());
        Calendar cal = Calendar.getInstance();
        cal.set(1, properCal.get(1));
        cal.set(2, properCal.get(2));
        cal.set(5, properCal.get(5));
        cal.set(11, properCal.get(11));
        cal.set(12, properCal.get(12));
        cal.set(13, properCal.get(13));
        cal.set(14, properCal.get(14));
        return cal.getTime();
    }

    public static Date convertDateToLocalTime(Date d) {
        if (d != null) {
            TimeZone tz = calculateTimeZone(d);
            return convertDateToLocalTime(d, tz);
        } else {
            return null;
        }
    }

    public static Date convertDateToLocalTime(Date d, TimeZone tz) {
        if (d == null) {
            return null;
        } else {
            return convertDate(d, tz, CurrentTimeZone.get());
        }
    }

    public static TimeZone calculateTimeZone(Date d) {
        TimeZone tz = CurrentTimeZone.get();
        Calendar cal = Calendar.getInstance(tz);
        int offset = tz.getOffset(d.getTime());
        int minTime = (0x25c3f80 + offset) / 1000 / 60;
        cal.setTime(d);
        int hours = cal.get(11);
        int minutes = cal.get(12);
        if (hours * 60 + minutes <= minTime) {
            offset -= hours * 3600 * 1000;
            offset -= minutes * 60 * 1000;
        } else {
            offset -= hours * 3600 * 1000 + minutes * 60 * 1000;
            offset += 0x5265c00;
        }
        StringBuffer ID = new StringBuffer("GMT");
        ID.append(offset >= 0 ? "+" : "-");
        int newhour = Math.abs(offset) / 0x36ee80;
        int newmin = newhour != 0 ? (Math.abs(offset) - newhour * 3600 * 1000) / 60000 : 0;
        if (newhour < 10) {
            ID.append("0");
        }
        ID.append(newhour).append(":");
        if (newmin < 10) {
            ID.append("0");
        }
        ID.append(newmin);
        return new SimpleTimeZone(offset, ID.toString());
    }

    public static TimeZone getTimeZone(Date d) {
        TimeZone myTZ = CurrentTimeZone.get();
        TimeZone calculatedTZ = calculateTimeZone(d);
        if (calculatedTZ.getOffset(d.getTime()) == myTZ.getOffset(d.getTime())) {
            return myTZ;
        } else {
            return calculatedTZ;
        }
    }

    public static Date convertDate(Date d, TimeZone tz1, TimeZone tz2) {
        Calendar cal = Calendar.getInstance(tz2);
        Calendar origCal = Calendar.getInstance(tz1);
        origCal.setTime(d);
        cal.set(origCal.get(1), origCal.get(2), origCal.get(5), 0, 0, 0);
        cal.clear(14);
        return cal.getTime();
    }

    public static Date setDateToMidnight(Date d, TimeZone tz) {
        if (tz == null) {
            tz = CurrentTimeZone.get();
        }
        Calendar cal = Calendar.getInstance(tz);
        if (d != null) {
            cal.setTime(d);
            cal.set(cal.get(1), cal.get(2), cal.get(5), 0, 0, 0);
            cal.clear(14);
            return cal.getTime();
        } else {
            return null;
        }
    }

    public static String getDateDisplayString(Date date) {
        return getDateDisplayString(date, DEFAULT_DATETIMEONLY_FORMAT);
    }

    public static String getDateDisplayString(Date date, String formatString) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatString);
        return sdf.format(date);
    }

    public static long r2s(long time) {
        return roundToSecond(time);
    }

    public static long r2s(Date date) {
        return roundToSecond(date);
    }

    public static long roundToSecond(Date date) {
        return roundToSecond(date.getTime());
    }

    public static long roundToSecond(long time) {
        long a = time % 1000L;
        if (a >= 500L) {
            a = time + (1000L - a);
        } else {
            a = time - a;
        }
        return a;
    }

    public static String DEFAULT_DATEONLY_FORMAT;
    public static String DEFAULT_DATETIMEONLY_FORMAT;
    public static String DEFAULT_TIMEZONEONLY_FORMAT;
    public static String DEFAULT_DATEZ_FORMAT;
    public static String DEFAULT_DATETIMEZ_FORMAT;
    public static String DATETIME_RFC822_FORMAT;
    public static Locale DEFAULT_LOCALE = Locale.getDefault();

    static {
        DEFAULT_DATEONLY_FORMAT = ((SimpleDateFormat) DateFormat.getDateInstance()).toPattern();
        DEFAULT_DATETIMEONLY_FORMAT = ((SimpleDateFormat) DateFormat.getDateTimeInstance()).toPattern();
        DEFAULT_TIMEZONEONLY_FORMAT = (new SimpleDateFormat("z")).toPattern();
        DEFAULT_DATEZ_FORMAT = (new StringBuilder()).append(DEFAULT_DATEONLY_FORMAT).append(" ").append(DEFAULT_TIMEZONEONLY_FORMAT).toString();
        DEFAULT_DATETIMEZ_FORMAT = (new StringBuilder()).append(DEFAULT_DATETIMEONLY_FORMAT).append(" ").append(DEFAULT_TIMEZONEONLY_FORMAT).toString();
        DATETIME_RFC822_FORMAT = (new StringBuilder()).append(DEFAULT_DATETIMEONLY_FORMAT).append(" ").append("Z").toString();
    }
}
