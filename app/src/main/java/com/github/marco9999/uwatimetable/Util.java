package com.github.marco9999.uwatimetable;

import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.Log;

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
        final String DAY_SPECIAL_ALLDAYS = "All Days";
        final String DAY_SPECIAL_WEEKEND = "Weekend";

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

    static HolderTimetableEntry[] filterEntriesWeek(HolderTimetableEntry[] data, String day, String week) {
        //TODO not a great solution, need a better one, probably want to create a start date and end date column in the sql table as SQLite directly supports checking between values (I think).
        if (week == null || week.equals("All Weeks")) {
            return data;
        }
        if (day.equals("Weekend")) {
            day = "Saturday,Sunday";
        }
        else if (day.equals("All Days")) {
            day = "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday";
        }

        // Create new list to hold filtered data entries, returned number of entries must be equal or less than the current length.
        ArrayList<HolderTimetableEntry> filteredEntries = new ArrayList<>(data.length);

        try {
            // Split day string to support multiple day selections.
            String[] daySplit = day.split(",");
            SimpleDateFormat dayCalendarEnum;
            for (String daySplitEntry : daySplit) {

                // Have to use the UK locale to get the correct week/day... No Australia locale? UK uses ISO-8601 standard, weeks start from Monday -> Sunday?
                Calendar weekDate = Calendar.getInstance(Locale.UK);
                int weekInt = Integer.parseInt(week);
                dayCalendarEnum = new SimpleDateFormat("EEEE", Locale.UK);
                Date weekDay = dayCalendarEnum.parse(daySplitEntry);
                weekDate.set(Calendar.DAY_OF_WEEK, weekDay.getDay() + 1);
                weekDate.set(Calendar.HOUR_OF_DAY, 0);
                weekDate.set(Calendar.MINUTE, 0);
                weekDate.set(Calendar.SECOND, 0);
                weekDate.set(Calendar.MILLISECOND, 0);
                weekDate.set(Calendar.WEEK_OF_YEAR, weekInt);
                //Log.d(Tag.LOG, "weekDate: day = " + weekDate.get(Calendar.DAY_OF_WEEK) + " week = " + weekDate.get(Calendar.WEEK_OF_YEAR) + " gettime = " + weekDate.getTime().getTime() + " gettimems = " + weekDate.getTimeInMillis());


                // Iterate over all entries
                for (HolderTimetableEntry entry : data) {
                    String dateBlocksString = entry.get(ContractTimetableDatabase.COLUMN_CLASS_WEEKS);
                    String[] dateBlocks = dateBlocksString.split(",");
                    Calendar startDate = Calendar.getInstance(Locale.UK);
                    Calendar endDate = Calendar.getInstance(Locale.UK);
                    SimpleDateFormat blockformat = new SimpleDateFormat("yyyy/MM/dd", Locale.UK);
                    String[] dateBlockSplit;
                    for (String dateBlock : dateBlocks) {
                        dateBlockSplit = dateBlock.split("-");
                        startDate.setTime(blockformat.parse(dateBlockSplit[0]));
                        endDate.setTime(blockformat.parse(dateBlockSplit[1]));
                        if (startDate.compareTo(weekDate) <= 0 && endDate.compareTo(weekDate) >= 0) {
                            if (!filteredEntries.contains(entry)) {
                                filteredEntries.add(entry);
                            }
                        }

                    }
                }
            }
        } catch (ParseException e) {
            Log.d(Tag.LOG, "Couldn't convert from day string into Date object. Returning full data set. Requires debugging.");
            Log.d(Tag.LOG, "Also Marco is an idiot for not doing this properly the first time...");
            return data;
        }

        return filteredEntries.toArray(new HolderTimetableEntry[filteredEntries.size()]);
    }

}




































