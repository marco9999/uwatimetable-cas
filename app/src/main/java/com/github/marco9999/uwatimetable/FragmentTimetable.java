package com.github.marco9999.uwatimetable;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

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

        // Find the util Fragment.
        UtilFragment utilFragment = (UtilFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Tag.Fragment.UTIL);
        assert (utilFragment != null);

        // Setup needed views (RecyclerView, Spinners).
        NestedScrollView rootView = (NestedScrollView) getView();
        if (rootView != null) {
            // Timetable List.
            RecyclerView timetableView = (RecyclerView) rootView.findViewById(R.id.fragment_timetable_list);
            if (timetableView != null) {

                // Set layout manager & adapter.
                timetableView.setLayoutManager(new LinearLayoutManager(getContext()));
                timetableView.setAdapter(utilFragment.getAdapterTimetableList());
                timetableView.setNestedScrollingEnabled(false);
            }

            // Day spinner.
            Spinner daySpinner = (Spinner) rootView.findViewById(R.id.spinner_day);
            if (daySpinner != null) {
                daySpinner.setAdapter(utilFragment.getAdapterSpinnerDay());
                daySpinner.setOnItemSelectedListener(utilFragment.getAdapterSpinnerDay());
            }


            // Week spinner.
            Spinner weekSpinner = (Spinner) rootView.findViewById(R.id.spinner_week);
            if (weekSpinner != null) {
                weekSpinner.setAdapter(utilFragment.getAdapterSpinnerWeek());
                weekSpinner.setOnItemSelectedListener(utilFragment.getAdapterSpinnerWeek());
            }

            // Set initial values if fragment is created for the first time.
            if (savedInstanceState == null) {
                if (daySpinner != null) {
                    daySpinner.setSelection(utilFragment.getAdapterSpinnerDay().getPosition(Util.getDayOfWeek()), false);
                }
                if (weekSpinner != null) {
                    weekSpinner.setSelection(utilFragment.getAdapterSpinnerWeek().getPosition(Util.getWeekOfYear()), false);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get & set data and notify to update UI.
        getDatabaseEntriesArrayAndNotify();
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
        else if (id == R.id.action_resetrange) {
            action_resetRange();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outBundle) {
        super.onSaveInstanceState(outBundle);
    }

    ///////////////////////
    // Action Functions. //
    ///////////////////////

    void getDatabaseEntriesArrayAndNotify() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        UtilFragment utilFragment = (UtilFragment) fm.findFragmentByTag(Tag.Fragment.UTIL);
        assert (utilFragment != null);

        assert (getView() != null);
        Spinner daySpinner = (Spinner) getView().findViewById(R.id.spinner_day);
        Spinner weekSpinner = (Spinner) getView().findViewById(R.id.spinner_week);
        if (daySpinner != null && weekSpinner != null) {
            String dayParam = daySpinner.getSelectedItem().toString();
            String weekParam = weekSpinner.getSelectedItem().toString();

            HolderTimetableEntry[] entriesArray = utilFragment.getHelperTimetableDatabase().readTimetableDBEntry(HelperTimetableDatabase.SORT.START_TIME, dayParam, weekParam);
            utilFragment.getAdapterTimetableList().setEntriesArrayAndNotify(entriesArray);
        }
    }

    private void action_readFromCas() {
        DialogEngineTimetableCAS dialog = new DialogEngineTimetableCAS();
        dialog.show(getActivity().getSupportFragmentManager(), Tag.Fragment.DIALOG_READFROMCAS);
    }

    private void action_resetRange() {
        // Find the util Fragment.
        UtilFragment utilFragment = (UtilFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Tag.Fragment.UTIL);
        assert (utilFragment != null);

        // Reset values to current day and week.
        NestedScrollView rootView = (NestedScrollView) getView();
        if (rootView != null) {
            Spinner daySpinner = (Spinner) rootView.findViewById(R.id.spinner_day);
            Spinner weekSpinner = (Spinner) rootView.findViewById(R.id.spinner_week);

            if (daySpinner != null) {
                daySpinner.setSelection(utilFragment.getAdapterSpinnerDay().getPosition(Util.getDayOfWeek()), true);
            }
            if (weekSpinner != null) {
                weekSpinner.setSelection(utilFragment.getAdapterSpinnerWeek().getPosition(Util.getWeekOfYear()), true);
            }
        }
    }
}
