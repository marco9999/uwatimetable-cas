package com.github.marco9999.uwatimetable;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by msatti on 16/04/16.
 */
public class AdapterTimetableList extends BaseAdapter {

    UtilFragment utilFragment;

    private HolderTimetableEntry[] entriesArray = {};

    AdapterTimetableList(UtilFragment utilFragment) {
        if (utilFragment == null) throw new IllegalArgumentException("utilFragment was null! Needs to be a valid object for callbacks.");
        this.utilFragment = utilFragment;
    }

    @Override
    public int getCount() {
        return entriesArray.length;
    }

    @Override
    public Object getItem(int position) {
        return entriesArray[position];
    }

    @Override
    public long getItemId(int position) {
        if (entriesArray[position].getHasID()) {
            return entriesArray[position].getContentValues().getAsInteger(ContractTimetableDatabase._ID);
        }
        else return -1; // Unknown ID.
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get initial View.
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View entryView = convertView;
        if (entryView == null) {
            entryView = layoutInflater.inflate(R.layout.fragment_timetable_list_entry, parent, false);
        }

        // Get array and R.id's to use based on getHasID(), and fill in the values.
        TextView fieldView;
        String fieldString;
        if (entriesArray[position].getHasID()) {
            // Put in values.
            for (int i = 0; i < ContractTimetableDatabase.SET_COLUMN_NAMES_ID.length; i++) {
                //todo: implement code for displaying id's for debugging.
                // For now, skip over null fields (means it hasn't been implemented yet in the UI).
                if (ContractTimetableDatabase.SET_TIMETABLE_DB_ENTRY_ID[i] == null) continue;
                fieldString = entriesArray[position].get(ContractTimetableDatabase.SET_COLUMN_NAMES_ID[i]);
                fieldView = (TextView) entryView.findViewById(ContractTimetableDatabase.SET_TIMETABLE_DB_ENTRY_ID[i]);
                fieldView.setText(fieldString);
            }
        }
        else {
            // Put in values.
            for (int i = 0; i < ContractTimetableDatabase.SET_COLUMN_NAMES.length; i++) {
                // For now, skip over null fields (means it hasn't been implemented yet in the UI).
                if (ContractTimetableDatabase.SET_TIMETABLE_DB_ENTRY[i] == null) continue;
                fieldString = entriesArray[position].get(ContractTimetableDatabase.SET_COLUMN_NAMES[i]);
                fieldView = (TextView) entryView.findViewById(ContractTimetableDatabase.SET_TIMETABLE_DB_ENTRY[i]);
                fieldView.setText(fieldString);
            }
        }

        return entryView;
    }

    void setEntriesArrayAndNotify(HolderTimetableEntry[] array) {
        entriesArray = array;
        notifyDataSetChanged();
    }

    void getDatabaseEntriesArrayAndNotify() {
        FragmentManager fm = utilFragment.getActivity().getSupportFragmentManager();
        UtilFragment utilFragment = (UtilFragment) fm.findFragmentByTag(Tag.Fragment.UTIL);
        assert (utilFragment != null);
        HelperTimetableDatabase helperTDB = utilFragment.getHelperTimetableDatabase();
        entriesArray = helperTDB.readAllTimetableDBEntry(HelperTimetableDatabase.SORT.DAY_THEN_START_TIME);
        notifyDataSetChanged();
    }
}
