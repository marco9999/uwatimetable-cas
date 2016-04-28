package com.github.marco9999.uwatimetable;

import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by msatti on 16/04/16.
 */
public class UtilFragment extends Fragment {

    // FragmentTimetable

    public FragmentTimetable findFragmentTimetable() {
       return (FragmentTimetable) getFragmentManager().findFragmentByTag(Tag.Fragment.TIMETABLE);
    }

    // HelperTimetableDatabase

    private HelperTimetableDatabase helperTDB; // Database is automatically shutdown on exit so no cleanup code is needed.

    public HelperTimetableDatabase getHelperTimetableDatabase() {
        // Create the database helper object if it doesn't exist.
        if (helperTDB == null) {
            helperTDB = new HelperTimetableDatabase(this);
        }

        // Open the timetable database for writing if its not open already.
        if (helperTDB.getDB() == null) {
            boolean openResult = helperTDB.openDB();
            if (!openResult) {
                Log.d(Tag.LOG, "ActivityUWATimetable: onCreate: Couldn't open DB");
            } else {
                Log.d(Tag.LOG, "ActivityUWATimetable: onCreate: Opened DB ok.");
            }
        }

        return helperTDB;
    }

    // AdapterTimetableList

    private AdapterTimetableList adapterTL;

    public AdapterTimetableList getAdapterTimetableList() {
        if (adapterTL == null) {
            adapterTL = new AdapterTimetableList(this);
        }

        return adapterTL;
    }

    // AdapterSpinnerDay

    private AdapterSpinnerDay adapterSD;

    public AdapterSpinnerDay getAdapterSpinnerDay() {
        if (adapterSD == null) {
            CharSequence[] dayArray = getResources().getStringArray(R.array.day_spinner_array);
            adapterSD = new AdapterSpinnerDay(this, android.R.layout.simple_spinner_item, dayArray);
            adapterSD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        return adapterSD;
    }

    // AdapterSpinnerWeek

    private AdapterSpinnerWeek adapterSW;

    public AdapterSpinnerWeek getAdapterSpinnerWeek() {
        if (adapterSW == null) {
            CharSequence[] weekArray = getResources().getStringArray(R.array.week_spinner_array);
            adapterSW = new AdapterSpinnerWeek(this, android.R.layout.simple_spinner_item, weekArray);
            adapterSW.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        return adapterSW;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
