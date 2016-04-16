package com.github.marco9999.uwatimetable;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentTimetable extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);

        // Get Timetable ListView and tie AdapterTimetableList to it.
        assert (rootView != null);
        ListView timetableView = (ListView) rootView.findViewById(R.id.fragment_timetable_list);
        timetableView.setAdapter(new AdapterTimetableList());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get timetable data.
        // Find the data Fragment.
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DataFragment dataFragment = (DataFragment) fm.findFragmentByTag(FragmentTags.FRAGMENT_TAG_DATA);
        assert (dataFragment != null);
        HelperTimetableDatabase helperTDB = dataFragment.getHelperTimetableDatabase();

        // Find Adapter for timetable ListView.
        assert (getView() != null);
        AdapterTimetableList adapterTL = (AdapterTimetableList) ((ListView) getView().findViewById(R.id.fragment_timetable_list)).getAdapter();

        // DEBUG
        // String[] test = {"test1", "test2", "test3", "test4", "test5", "test6", "test7", "test8"};
        // helperTDB.writeTimetableDBEntry(new HolderTimetableEntry(test, false));

        // Get & set data and notify to update UI.
        HolderTimetableEntry[] timetableEntries = helperTDB.readAllTimetableDBEntry();
        Log.d(ActivityUWATimetable.LOG_TAG, "FragmentTimetable: onResume: Got timetable data. Number of entries = " + Integer.toString(timetableEntries.length));
        adapterTL.setEntriesArrayAndNotify(timetableEntries);
    }

}
