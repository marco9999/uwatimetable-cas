package com.github.marco9999.uwatimetable;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by msatti on 16/04/16.
 */
public class AdapterTimetableList extends RecyclerView.Adapter<AdapterTimetableList.HolderView> {

    class HolderView extends RecyclerView.ViewHolder implements View.OnClickListener {
        View entryLayout;
        String entryID;

        HolderView(View entryLayout) {
            super(entryLayout);
            entryLayout.setOnClickListener(this);
            this.entryLayout = entryLayout;
        }

        void setEntryID(String id) {
            entryID = id;
        }

        String getEntryID() {
            return entryID;
        }

        @Override
        public void onClick(View v) {
            // Launch the details activity with the id parsed to it.
            String id = HolderView.this.getEntryID();
            Context context = v.getContext();
            Intent intent = new Intent(context, ActivityTimetableDetails.class);
            intent.putExtra(ActivityTimetableDetails.KEY_TIMETABLE_ENTRY_ID, id);
            context.startActivity(intent);
        }
    }

    private UtilFragment utilFragment;
    private HolderTimetableEntry[] entriesArray = {};

    AdapterTimetableList(UtilFragment utilFragment) {
        if (utilFragment == null) throw new IllegalArgumentException("utilFragment was null! Needs to be a valid object for callbacks.");
        this.utilFragment = utilFragment;
    }

    @Override
    public int getItemCount() {
        return entriesArray.length;
    }

    @Override
    public HolderView onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View entryView = layoutInflater.inflate(R.layout.fragment_timetable_list_entry, parent, false);
        return new HolderView(entryView);
    }

    @Override
    public void onBindViewHolder(HolderView holder, int position) {
        // Get array and R.id's to use based on getHasID(), and fill in the values.
        TextView fieldView;
        String fieldString;
        if (entriesArray[position].getHasID()) {
            // Assign viewholder its ID, based on the entry id.
            holder.setEntryID(entriesArray[position].get(ContractTimetableDatabase._ID));

            // Put in values.
            for (int i = 0; i < ContractTimetableDatabase.SET_COLUMN_NAMES_ID.length; i++) {
                //todo: implement code for displaying id's for debugging.
                // For now, skip over null fields (means it hasn't been implemented yet in the UI).
                if (ContractTimetableDatabase.SET_TIMETABLE_DB_ENTRY_ID[i] == null) continue;
                fieldString = entriesArray[position].get(ContractTimetableDatabase.SET_COLUMN_NAMES_ID[i]);
                fieldView = (TextView) holder.entryLayout.findViewById(ContractTimetableDatabase.SET_TIMETABLE_DB_ENTRY_ID[i]);
                fieldView.setText(fieldString);
            }
        }
        else {
            // Put in values.
            for (int i = 0; i < ContractTimetableDatabase.SET_COLUMN_NAMES.length; i++) {
                // For now, skip over null fields (means it hasn't been implemented yet in the UI).
                if (ContractTimetableDatabase.SET_TIMETABLE_DB_ENTRY[i] == null) continue;
                fieldString = entriesArray[position].get(ContractTimetableDatabase.SET_COLUMN_NAMES[i]);
                fieldView = (TextView) holder.entryLayout.findViewById(ContractTimetableDatabase.SET_TIMETABLE_DB_ENTRY[i]);
                fieldView.setText(fieldString);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        if (entriesArray[position].getHasID()) {
            return entriesArray[position].getContentValues().getAsInteger(ContractTimetableDatabase._ID);
        }
        else return -1; // Unknown ID.
    }

    void setEntriesArrayAndNotify(HolderTimetableEntry[] array) {
        entriesArray = array;
        notifyDataSetChanged();
    }
}
