package com.github.marco9999.uwatimetable;

import android.content.ContentValues;

/**
 * Created by msatti on 14/04/16.
 */
public class HolderTimetableEntry {

    ContentValues holderInfo = null;
    boolean hasID = false;

    HolderTimetableEntry(String[] info, boolean hasID) throws IllegalArgumentException {
        if (info != null) {
            this.hasID = hasID;
            if (hasID && info.length == ContractTimetableDatabase.SET_COLUMN_NAMES_ID.length) {
                allocCV();
                for (int i = 0; i < ContractTimetableDatabase.SET_COLUMN_NAMES_ID.length; i++) {
                    holderInfo.put(ContractTimetableDatabase.SET_COLUMN_NAMES_ID[i], info[i]);
                }
            }
            else if (!hasID && info.length == ContractTimetableDatabase.SET_COLUMN_NAMES.length) {
                allocCV();
                for (int i = 0; i < ContractTimetableDatabase.SET_COLUMN_NAMES.length; i++) {
                    holderInfo.put(ContractTimetableDatabase.SET_COLUMN_NAMES[i], info[i]);
                }
            }
            else {
                throw new IllegalArgumentException("String array passed not the correct length. Check ContractTimetableDatabase.SET_COLUMN_NAMES[_ID].length for the required length.");
            }
        }
        else {
            throw new IllegalArgumentException("String array passed was null.");
        }
    }

    HolderTimetableEntry(ContentValues info, boolean hasID) throws IllegalArgumentException {
        if (info != null) {
            this.hasID = hasID;
            if (hasID && info.size() == ContractTimetableDatabase.SET_COLUMN_NAMES_ID.length) {
                holderInfo = info;
            }
            else if (!hasID && info.size() == ContractTimetableDatabase.SET_COLUMN_NAMES.length) {
                holderInfo = info;
            }
            else {
                throw new IllegalArgumentException("String array passed not the correct length. Check ContractTimetableDatabase.SET_COLUMN_NAMES[_ID].length for the required length.");
            }
        }
        else {
            throw new IllegalArgumentException("String array passed was null.");
        }
    }

    void allocCV() {
        holderInfo = new ContentValues();
    }

    boolean getHasID() {
        return hasID;
    }

    void put(String key, String value) {
        holderInfo.put(key, value);
    }

    String get(String key) {
        return holderInfo.getAsString(key);
    }

    void putAll(ContentValues otherCV) {
        holderInfo.putAll(otherCV);
    }

    ContentValues getContentValues() {
        return holderInfo;
    }
}
