package com.github.marco9999.uwatimetable;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Marco on 24/04/2016.
 */
public class DialogClearDatabase extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Android bug: https://code.google.com/p/android/issues/detail?id=17423
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set title.
        builder.setTitle(R.string.title_clearDatabase);

        // Add action buttons
        builder.setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int id) {
                    UtilFragment utilFragment = (UtilFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Tag.Fragment.UTIL);
                    assert (utilFragment != null);

                    // Clear database.
                    utilFragment.getHelperTimetableDatabase().recreateDB();

                    // Refresh UI
                    utilFragment.getAdapterTimetableList().getDatabaseEntriesArrayAndNotify();
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) { }
            });

        return builder.create();
    }

    @Override
    public void onDestroyView() {
        // Android bug: https://code.google.com/p/android/issues/detail?id=17423
        if (getDialog() != null && getRetainInstance()) getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
