package com.github.marco9999.uwatimetable;

import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Marco on 24/04/2016.
 */
class Util {

    final static String DAY_SPECIAL_ALLDAYS = "All Days";
    final static String DAY_SPECIAL_WEEKEND = "Weekend";

    static String getDayOfWeek() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String day = dayFormat.format(Calendar.getInstance().getTime());
        if (day.equals("Saturday") || day.equals("Sunday")) day = "Weekend"; // Special case.
        return day;
    }

    static String getWeekOfYear() {
        // Have to use the UK locale to get the correct week... No Australia locale?
        int week = Calendar.getInstance(Locale.UK).get(Calendar.WEEK_OF_YEAR);
        return Integer.toString(week);
    }

    // Used to format sql queries into the WHERE clause.
    @Nullable
    static String formatSQLDay(String day) {
        if (day.equals(DAY_SPECIAL_ALLDAYS)) {
            return null;
        }
        else if (day.equals(DAY_SPECIAL_WEEKEND)) {
            return ContractTimetableDatabase.COLUMN_CLASS_DAY + " IS \"Saturday\" OR \"Sunday\"";
        }
        else {
            return ContractTimetableDatabase.COLUMN_CLASS_DAY + " IS \"" + day + "\"";
        }
    }

    static String formatSQLWeek(String week) {
        //TODO not implemented.
        return null;
    }

}
