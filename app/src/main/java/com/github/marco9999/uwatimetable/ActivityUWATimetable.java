package com.github.marco9999.uwatimetable;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityUWATimetable extends AppCompatActivity {

    static final String LOG_TAG = "UWATimetable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uwatimetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert (fab != null);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Create the data fragment for the first time if it doesn't exist.
        FragmentManager fm = getSupportFragmentManager();
        DataFragment dataFragment = (DataFragment) fm.findFragmentByTag(FragmentTags.FRAGMENT_TAG_DATA);
        if (dataFragment == null) {
            // add the fragment
            dataFragment = new DataFragment();
            fm.beginTransaction().add(dataFragment, FragmentTags.FRAGMENT_TAG_DATA).commit();
        }

        // Create the database helper object if it doesn't exist.
        if (dataFragment.getHelperTimetableDatabase() == null) {
            dataFragment.setHelperTimetableDatabase(new HelperTimetableDatabase(this));
        }

        // Open the timetable database for writing if its not open already.
        if (dataFragment.getHelperTimetableDatabase().getDB() == null) {
            boolean openResult = dataFragment.getHelperTimetableDatabase().openDB();
            if (!openResult) {
                Log.d(ActivityUWATimetable.LOG_TAG, "ActivityUWATimetable: onCreate: Couldn't open DB");
            } else {
                Log.d(ActivityUWATimetable.LOG_TAG, "ActivityUWATimetable: onCreate: Opened DB ok.");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        // Close & erase the timetable database helper.
        // Find the retained fragment.
        FragmentManager fm = getSupportFragmentManager();
        DataFragment dataFragment = (DataFragment) fm.findFragmentByTag(FragmentTags.FRAGMENT_TAG_DATA);
        assert (dataFragment != null);
        dataFragment.getHelperTimetableDatabase().closeDB();

        super.onDestroy();
    }


}
