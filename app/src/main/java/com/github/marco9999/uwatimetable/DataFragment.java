package com.github.marco9999.uwatimetable;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by msatti on 16/04/16.
 */
public class DataFragment extends Fragment {

    // data object we want to retain
    private HelperTimetableDatabase helperTDB;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment.
        setRetainInstance(true);
    }

    public void setHelperTimetableDatabase(HelperTimetableDatabase helperTDB) {
        this.helperTDB = helperTDB;
    }

    public HelperTimetableDatabase getHelperTimetableDatabase() {
        return helperTDB;
    }

}
