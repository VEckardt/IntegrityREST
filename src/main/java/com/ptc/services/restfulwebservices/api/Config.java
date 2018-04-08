/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.restfulwebservices.api;

import java.text.DateFormat;
import java.util.Locale;

/**
 *
 * @author veckardt
 */
public class Config {

    // http://wayback.archive.org/web/20141015235927/http://blog.ej-technologies.com/2011/12/default-locale-changes-in-java-7.html
    public static Locale locale = Locale.getDefault(); // null; // Locale.getDefault();

    public static DateFormat dfTime = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault());
    public static DateFormat dfDay = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
    public static DateFormat dfDayTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
    public static DateFormat dfDayTimeShort = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
    public static DateFormat dfDayTimeShortUS = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);

    public static final String defaultAppDateFormat = "dd-MM-yyyy";

    public static Locale getLocale() {
        if (locale == null) {
            locale = new Locale(System.getProperty("user.language.format"), System.getProperty("user.country.format"));
        }
        System.out.println("locale: " + locale.toString());
        return locale;
    }

}
