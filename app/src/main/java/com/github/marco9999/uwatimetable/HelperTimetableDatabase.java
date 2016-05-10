package com.github.marco9999.uwatimetable;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by msatti on 12/04/16.
 */
class HelperTimetableDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "timetable.db";

    private SQLiteDatabase database = null;

    UtilFragment utilFragment;

    HelperTimetableDatabase(UtilFragment utilFragment) {
        super(utilFragment.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        this.utilFragment = utilFragment;
    }

    ////////////////////
    // SQL functions. //
    ////////////////////

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ContractTimetableDatabase.SQL_CREATE_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ContractTimetableDatabase.SQL_DROP_COMMAND);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    boolean openDB() {
        boolean isOpen = false;
        try {
            database = getWritableDatabase();
            if (database == null) throw new NullPointerException("HelperTimetableDatabase: openDB: getWritableDatabase() returned null!");
            isOpen = true;
        }
        catch (Exception ex) {
            Log.d(Tag.LOG, "HelperTimetableDatabase: openDB: Failed to open database");
            Log.d(Tag.LOG, ex.getLocalizedMessage());
        }
        return isOpen;
    }

    void closeDB() {
        database.close();
        database = null;
    }

    void recreateDB() {
        if (database != null) {
            database.execSQL(ContractTimetableDatabase.SQL_DROP_COMMAND);
            database.execSQL(ContractTimetableDatabase.SQL_CREATE_COMMAND);
        }
    }

    SQLiteDatabase getDB() {
        return database;
    }

    ///////////////////////////
    // Data Check functions. //
    ///////////////////////////

    void checkEntryData_All(HolderTimetableEntry entry) {
        checkEntryData_Day(entry);
        checkEntryData_Times(entry);
    }

    void checkEntryData_Day(HolderTimetableEntry entry) {
        final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (String day : days) {
            if (entry.get(ContractTimetableDatabase.COLUMN_CLASS_DAY).equals(day)) {
                return;
            }
        }

        throw new IllegalArgumentException("Trying to write entry to database with incorrect Day format. Requires debugging!");
    }

    void checkEntryData_Times(HolderTimetableEntry entry) {
        if (entry.get(ContractTimetableDatabase.COLUMN_CLASS_START_TIME).matches("\\d\\d:\\d\\d")
                && entry.get(ContractTimetableDatabase.COLUMN_CLASS_END_TIME).matches("\\d\\d:\\d\\d")) {
            return;
        }

        throw new IllegalArgumentException("Trying to write entry to database with incorrect Start or End Time format. Requires debugging!");
    }

    /////////////////////////////////
    // SQL query format functions. //
    /////////////////////////////////

    public enum SQLSORT {
        NONE, START_TIME, DAY, DAY_THEN_START_TIME
    }

    private String formatOrderBy(SQLSORT sortType) {
        switch (sortType) {
            case NONE:
                return null;
            case START_TIME:
                return ("CAST(SUBSTR(" + ContractTimetableDatabase.COLUMN_CLASS_START_TIME + ",1,2) AS INTEGER) ASC");
            case DAY:
                return ("(CASE WHEN " + ContractTimetableDatabase.COLUMN_CLASS_DAY + " = 'Sunday' THEN 1 "
                        + "WHEN " + ContractTimetableDatabase.COLUMN_CLASS_DAY + " = 'Monday' THEN 2 "
                        + "WHEN " + ContractTimetableDatabase.COLUMN_CLASS_DAY + " = 'Tuesday' THEN 3 "
                        + "WHEN " + ContractTimetableDatabase.COLUMN_CLASS_DAY + " = 'Wednesday' THEN 4 "
                        + "WHEN " + ContractTimetableDatabase.COLUMN_CLASS_DAY + " = 'Thursday' THEN 5 "
                        + "WHEN " + ContractTimetableDatabase.COLUMN_CLASS_DAY + " = 'Friday' THEN 6 "
                        + "WHEN " + ContractTimetableDatabase.COLUMN_CLASS_DAY + " = 'Saturday' THEN 7 ELSE 8 END)");
            case DAY_THEN_START_TIME:
                return (formatOrderBy(SQLSORT.DAY) + ", " + formatOrderBy(SQLSORT.START_TIME));
            default:
                return null;
        }
    }

    @Nullable
    private String formatWhere(String day) {
        final String DAY_SPECIAL_ALLDAYS = "All Days";
        final String DAY_SPECIAL_WEEKEND = "Weekend";

        switch (day) {
            case DAY_SPECIAL_ALLDAYS:
                return null;
            case DAY_SPECIAL_WEEKEND:
                return ContractTimetableDatabase.COLUMN_CLASS_DAY + " IS \"Saturday\" OR \"Sunday\"";
            default:
                return ContractTimetableDatabase.COLUMN_CLASS_DAY + " IS \"" + day + "\"";
        }
    }

    private HolderTimetableEntry[] filterEntriesWeek(HolderTimetableEntry[] data, String day, String week) {
        if (week == null || week.equals("All Weeks")) {
            return data;
        }

        // Convert string into day array of ints for making times.
        String[] dayStringArray;
        int[] dayIntArray;
        if (day.equals("Weekend")) {
            dayStringArray = new String[]{"Saturday", "Sunday"};
        }
        else if (day.equals("All Days")) {
            dayStringArray = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        }
        else {
            dayStringArray = new String[]{day};
        }

        // Get week as an int.
        int weekInt = Integer.parseInt(week);

        // Create new list to hold filtered data entries, returned number of entries must be equal or less than the current length.
        ArrayList<HolderTimetableEntry> filteredEntries = new ArrayList<>(data.length);

        DateTime timeTest, timeBlockStart, timeBlockEnd;
        String[] datesList, blockDatesList;
        try {
            for (String dayEntry : dayStringArray) {
                timeTest = Util.getDateTimeFromDayString(dayEntry).withWeekOfWeekyear(weekInt);
                for (HolderTimetableEntry entry : data) {
                    datesList = entry.get(ContractTimetableDatabase.COLUMN_CLASS_WEEKS).split(",");
                    for (String date : datesList) {
                        blockDatesList = date.split("-");
                        timeBlockStart = Util.getDateTimeFromDateString(blockDatesList[0]);
                        timeBlockEnd = Util.getDateTimeFromDateString(blockDatesList[1]);
                        if (timeTest.compareTo(timeBlockStart.toInstant()) >= 0 && timeTest.compareTo(timeBlockEnd.toInstant()) <= 0) {
                            if (!filteredEntries.contains(entry)) {
                                filteredEntries.add(entry);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.d(Tag.LOG, "Couldn't filter out entries from the data array. Requires debugging.");
            Log.d(Tag.LOG, ex.getLocalizedMessage());
            return data;
        }

        return filteredEntries.toArray(new HolderTimetableEntry[filteredEntries.size()]);
    }

    /////////////////////////////////////
    // Timetable Read/Write functions. //
    /////////////////////////////////////

    boolean writeTimetableDBEntry(HolderTimetableEntry info) {
        assert (database != null);
        // info contains list of class entry parameters (in the order listed by ContractTimetableDatabase.SET_COLUMN_NAMES) through the ContentValues object underneath it.
        // The ROWID (_ID) is automatically generated by the SQL engine.

        // Insert entry into timetable database.
        checkEntryData_All(info);
        long returnValue = database.insert(ContractTimetableDatabase.TABLE_NAME, null, info.getContentValues());

        // Return false if the insert function returned -1, which indicates an error.
        return !(returnValue == -1);
    }

    boolean writeTimetableDBEntryArray(HolderTimetableEntry[] infoArray) {
        assert (database != null);
        // info contains list of class entry parameters (in the order listed by ContractTimetableDatabase.SET_COLUMN_NAMES) through the ContentValues object underneath it.
        // The ROWID (_ID) is automatically generated by the SQL engine.

        // Insert entry into timetable database.
        boolean hasSucceeded = false;
        for (HolderTimetableEntry info : infoArray) {
            hasSucceeded = writeTimetableDBEntry(info);
        }

        return hasSucceeded;
    }

    HolderTimetableEntry[] readTimetableDBEntry(SQLSORT sortType, String dayParam, String weekParam) {
        Log.d(Tag.LOG, "Executing timetable database query with day = " + dayParam + " and SORT = " + sortType.toString());

        // Format the day and week strings into SQL clauses. Check for appropriate SORT parameter.
        String formatWhereStr = formatWhere(dayParam);
        if (dayParam == null) sortType = SQLSORT.DAY_THEN_START_TIME;
        String formatOrderByStr = formatOrderBy(sortType);

        if (database != null) {
            // Get DB results.
            Cursor results = database.query(ContractTimetableDatabase.TABLE_NAME, null, formatWhereStr, null, null, null, formatOrderByStr, null);

            // Allocate length of entryArray.
            HolderTimetableEntry[] entryArray = new HolderTimetableEntry[results.getCount()];

            // Put cursor results into holders.
            String[] tempStrArrayHolder;
            while (results.moveToNext()) {
                tempStrArrayHolder = new String[ContractTimetableDatabase.SET_COLUMN_NAMES_ID.length];
                for (int i = 0; i < ContractTimetableDatabase.SET_COLUMN_NAMES_ID.length; i++) {
                    tempStrArrayHolder[i] = results.getString(i);
                }
                entryArray[results.getPosition()] = new HolderTimetableEntry(tempStrArrayHolder, true);
            }

            // Close results cursor.
            results.close();

            Log.d(Tag.LOG, "Successfully executed database query. Number of entries = " + String.valueOf(entryArray.length));

            // Return only those within the week specified (too hard to do in SQL, so do it in java).
            entryArray = filterEntriesWeek(entryArray, dayParam, weekParam);

            Log.d(Tag.LOG, "Successfully filtered entries by weeks. Number of entries = " + String.valueOf(entryArray.length));

            return entryArray;
        }
        else {
            Log.d(Tag.LOG, "Database was null when a query was executed. Requires Debugging!");
            throw new RuntimeException("Database was null when a query was executed. Requires Debugging!");
        }
    }

}
