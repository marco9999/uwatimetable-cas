package com.github.marco9999.uwatimetable;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class FragmentTimetable extends Fragment {

    /////////////////////////
    // Fragment Functions. //
    /////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        if (rootView != null) {
            RecyclerView timetableView = (RecyclerView) rootView.findViewById(R.id.fragment_timetable_list);
            if (timetableView != null) {
                // Find the util Fragment.
                UtilFragment utilFragment = (UtilFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Tag.Fragment.UTIL);
                assert (utilFragment != null);
                // Set layout manager & adapter.
                timetableView.setLayoutManager(new LinearLayoutManager(getContext()));
                timetableView.setAdapter(utilFragment.getAdapterTimetableList());
                //timetableView.setNestedScrollingEnabled(false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get & set data and notify to update UI.
        UtilFragment utilFragment = (UtilFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Tag.Fragment.UTIL);
        assert (utilFragment != null);
        utilFragment.getAdapterTimetableList().getDatabaseEntriesArrayAndNotify();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_timetable, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_readFromCAS) {
            action_readFromCas();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ///////////////////////
    // Action Functions. //
    ///////////////////////

    private void action_readFromCas() {
        DialogEngineTimetableCAS dialog = new DialogEngineTimetableCAS();
        dialog.show(getActivity().getSupportFragmentManager(), Tag.Fragment.DIALOG_READFROMCAS);
    }
}
