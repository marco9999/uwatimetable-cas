package com.github.marco9999.uwatimetable;

import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Marco on 24/04/2016.
 */
class Util {



    static String getDayOfWeek() {
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern("EEEE");
        String day = DateTime.now().toString(dayFormat);
        if (day.equals("Saturday") || day.equals("Sunday")) day = "Weekend"; // Special case.
        return day;
    }

    static String getWeekOfYear() {
        int week = DateTime.now().getWeekOfWeekyear();
        return Integer.toString(week);
    }

    static DateTime getDateTimeFromDayString(String day) {
        DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("EEEE");
        DateTime dt = DateTime.now().withDayOfWeek(dayFormatter.parseDateTime(day).getDayOfWeek()).withTime(0,0,0,0);
        return dt;
    }

    static DateTime getDateTimeFromDateString(String date) {
        DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("yyyy/MM/dd");
        return dayFormatter.parseDateTime(date);
    }
}




































