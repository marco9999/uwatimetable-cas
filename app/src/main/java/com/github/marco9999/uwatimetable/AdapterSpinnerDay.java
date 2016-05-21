package com.github.marco9999.uwatimetable;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

/**
 * Created by Marco on 27/04/2016.
 */
public class AdapterSpinnerDay extends ArrayAdapter<CharSequence> implements AdapterView.OnItemSelectedListener {

    UtilFragment utilFragment;

    public AdapterSpinnerDay(UtilFragment utilFragment, int textViewResourceId, CharSequence[] objects) {
        super(utilFragment.getContext(), textViewResourceId, objects);
        this.utilFragment = utilFragment;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view != null) {
            // If view is null, this means that the spinner has not yet displayed. This is to fix the function being called twice.
            // Still has a problem where it is called multiple times, will have to look into it.
            utilFragment.findFragmentTimetable().getDatabaseEntriesArrayAndNotify();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}