package com.github.marco9999.uwatimetable;

import android.provider.BaseColumns;

/**
 * Created by msatti on 12/04/16.
 */
class ContractTimetableDatabase implements BaseColumns{
    static final String TABLE_NAME = "uwatimetable_timetable";
    static final String COLUMN_CLASS_UNIT = "class_unit";
    static final String COLUMN_CLASS_UNIT_DESCRIPTION = "class_unit_description";
    static final String COLUMN_CLASS_VENUE = "class_venue";
    static final String COLUMN_CLASS_TYPE = "class_type";
    static final String COLUMN_CLASS_DAY = "class_day";
    static final String COLUMN_CLASS_START_DATE = "class_start_date";
    static final String COLUMN_CLASS_START_TIME = "class_start_time";
    static final String COLUMN_CLASS_END_TIME = "class_end_time";
    static final String COLUMN_CLASS_STREAM = "class_stream";
    static final String COLUMN_CLASS_WEEKS = "class_weeks";

    static final String[] SET_COLUMN_NAMES = {COLUMN_CLASS_UNIT, COLUMN_CLASS_UNIT_DESCRIPTION, COLUMN_CLASS_VENUE, COLUMN_CLASS_TYPE, COLUMN_CLASS_DAY, COLUMN_CLASS_START_DATE, COLUMN_CLASS_START_TIME, COLUMN_CLASS_END_TIME, COLUMN_CLASS_STREAM, COLUMN_CLASS_WEEKS};
    static final String[] SET_COLUMN_NAMES_ID = {_ID, COLUMN_CLASS_UNIT, COLUMN_CLASS_UNIT_DESCRIPTION, COLUMN_CLASS_VENUE, COLUMN_CLASS_TYPE, COLUMN_CLASS_DAY, COLUMN_CLASS_START_DATE, COLUMN_CLASS_START_TIME, COLUMN_CLASS_END_TIME, COLUMN_CLASS_STREAM, COLUMN_CLASS_WEEKS};

    static final Integer[] SET_TIMETABLE_DB_ENTRY = {R.id.unit, null, R.id.venue, R.id.type, null, null, R.id.start, R.id.end, R.id.stream, R.id.weeks};
    static final Integer[] SET_TIMETABLE_DB_ENTRY_ID = {null, R.id.unit, null, R.id.venue, R.id.type, null, null, R.id.start, R.id.end, R.id.stream, R.id.weeks};


    static final String SQL_CREATE_COMMAND =
            "CREATE TABLE " + TABLE_NAME + " (" +
            ContractTimetableDatabase._ID + " INTEGER PRIMARY KEY, " +
            ContractTimetableDatabase.COLUMN_CLASS_UNIT + " TEXT, " +
            ContractTimetableDatabase.COLUMN_CLASS_UNIT_DESCRIPTION + " TEXT, " +
            ContractTimetableDatabase.COLUMN_CLASS_VENUE + " TEXT, " +
            ContractTimetableDatabase.COLUMN_CLASS_TYPE + " TEXT, " +
            ContractTimetableDatabase.COLUMN_CLASS_DAY + " TEXT, " +
            ContractTimetableDatabase.COLUMN_CLASS_START_DATE + " TEXT, " +
            ContractTimetableDatabase.COLUMN_CLASS_START_TIME + " TEXT, " +
            ContractTimetableDatabase.COLUMN_CLASS_END_TIME + " TEXT, " +
            ContractTimetableDatabase.COLUMN_CLASS_STREAM + " TEXT, " +
            ContractTimetableDatabase.COLUMN_CLASS_WEEKS + " TEXT" +
            ")";

    static final String SQL_DROP_COMMAND =
            "DROP TABLE IF EXISTS " + ContractTimetableDatabase.TABLE_NAME;
}
