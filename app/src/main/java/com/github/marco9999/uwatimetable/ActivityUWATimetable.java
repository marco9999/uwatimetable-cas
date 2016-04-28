package com.github.marco9999.uwatimetable;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityUWATimetable extends AppCompatActivity {

    static final String LOG_TAG = "UWATimetable";

    /////////////////////////
    // Fragment Functions. //
    /////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uwatimetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();

        // Create the util/utilretain fragments for the first time if they dont exist.
        UtilFragment utilFragment = (UtilFragment) fm.findFragmentByTag(Tag.Fragment.UTIL);
        if (utilFragment == null) {
            // add the fragment
            utilFragment = new UtilFragment();
            fm.beginTransaction().add(utilFragment, Tag.Fragment.UTIL).commit();
        }
        UtilRetainFragment utilRetainFragment = (UtilRetainFragment) fm.findFragmentByTag(Tag.Fragment.UTIL_RETAIN);
        if (utilRetainFragment == null) {
            // add the fragment
            utilRetainFragment = new UtilRetainFragment();
            fm.beginTransaction().add(utilRetainFragment, Tag.Fragment.UTIL_RETAIN).commit();
        }
        fm.executePendingTransactions(); // Needed as commit() doesn't execute in time for when the other fragments need the util fragments.

        // Create Timetable fragment.
        FragmentTimetable timetableFragment = (FragmentTimetable) fm.findFragmentByTag(Tag.Fragment.TIMETABLE);
        if (timetableFragment == null) {
            timetableFragment = new FragmentTimetable();
            fm.beginTransaction().add(R.id.coordinatorlayout, timetableFragment, Tag.Fragment.TIMETABLE).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_uwatimetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clearDatabase) {
            action_clearDatabase();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        // Close & erase the timetable database helper.
        // Find the retained fragment.
        FragmentManager fm = getSupportFragmentManager();
        UtilFragment utilFragment = (UtilFragment) fm.findFragmentByTag(Tag.Fragment.UTIL);
        assert (utilFragment != null);
        utilFragment.getHelperTimetableDatabase().closeDB();

        super.onDestroy();
    }

    ///////////////////////
    // Action Functions. //
    ///////////////////////

    private void action_clearDatabase() {
        // Recreate Database.
        DialogClearDatabase dialog = new DialogClearDatabase();
        dialog.show(getSupportFragmentManager(), Tag.Fragment.DIALOG_CLEARDATABASE);
    }
}
