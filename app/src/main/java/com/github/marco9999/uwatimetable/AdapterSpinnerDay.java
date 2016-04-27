package com.github.marco9999.uwatimetable;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

/**
 * Created by Marco on 27/04/2016.
 */
public class AdapterSpinnerDay extends ArrayAdapter<CharSequence> implements AdapterView.OnItemSelectedListener {


    public AdapterSpinnerDay(Context context, int textViewResourceId, CharSequence[] objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}