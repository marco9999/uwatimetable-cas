package com.github.marco9999.uwatimetable;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Marco on 15/05/2016.
 */
public class ActivityTimetableDetails extends AppCompatActivity {

    static final String LOG_TAG = "Timetable_Detail";
    static final String KEY_TIMETABLE_ENTRY_ID = "entry_id";

    String timetableEntryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_detail);

        // Toolbar setup.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert (getSupportActionBar() != null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_timetable_detail);

        // Get ID of timetable entry.
        timetableEntryId = getIntent().getStringExtra(KEY_TIMETABLE_ENTRY_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Find the util Fragment.
        FragmentManager fm = getSupportFragmentManager();
        // Create the util fragments for the first time if they dont exist.
        UtilFragment utilFragment = (UtilFragment) fm.findFragmentByTag(Tag.Fragment.UTIL);
        if (utilFragment == null) {
            // add the fragment
            utilFragment = new UtilFragment();
            fm.beginTransaction().add(utilFragment, Tag.Fragment.UTIL).commit();
        }
        fm.executePendingTransactions(); // Needed as commit() doesn't execute in time for when access is needed.

        // Get ID details.
        HolderTimetableEntry entry = utilFragment.getHelperTimetableDatabase().readTimetableDBEntry(timetableEntryId);

        // Put class details into view.
        String fieldString;
        TextView fieldView;
        for (int i = 0; i < ContractTimetableDatabase.SET_COLUMN_NAMES_ID.length; i++) {
            // For now, skip over null fields (means it hasn't been implemented yet in the UI).
            if (ContractTimetableDatabase.SET_TIMETABLE_DB_ENTRY_DETAILS_ID[i] == null) continue;
            fieldString = entry.get(ContractTimetableDatabase.SET_COLUMN_NAMES_ID[i]);
            fieldView = (TextView) findViewById(ContractTimetableDatabase.SET_TIMETABLE_DB_ENTRY_DETAILS_ID[i]);
            assert (fieldView != null);
            fieldView.setText(fieldString);
        }

        // Put week details into view.
        LinearLayout dateLayout = (LinearLayout) findViewById(R.id.date_details);
        String dateString = entry.get(ContractTimetableDatabase.COLUMN_CLASS_WEEKS);
        String[] dateBlocks = dateString.split(",");
        TextView dateView;
        String dateBlock;
        for (int i = 0; i < dateBlocks.length; i++) {
            dateBlock = dateBlocks[i];
            dateView = new TextView(this);
            // Only add padding if its not the last date to add in.
            if (!(i == dateBlocks.length - 1)) dateView.setPadding(0,0,0,getResources().getDimensionPixelSize(R.dimen.entry_vertical_padding));
            dateView.setText(dateBlock.replace("-"," - "));
            assert (dateLayout != null);
            dateLayout.addView(dateView);
        }
    }
}
