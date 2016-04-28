package com.github.marco9999.uwatimetable;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

/**
 * Created by Marco on 27/04/2016.
 */
public class AdapterSpinnerWeek extends ArrayAdapter<CharSequence> implements AdapterView.OnItemSelectedListener {

    final static String WEEK_SPECIAL_ALLWEEKS = "All Weeks";

    UtilFragment utilFragment;

    public AdapterSpinnerWeek(UtilFragment utilFragment, int textViewResourceId, CharSequence[] objects) {
        super(utilFragment.getContext(), textViewResourceId, objects);
        this.utilFragment = utilFragment;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view != null) {
            utilFragment.findFragmentTimetable().getDatabaseEntriesArrayAndNotify();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}