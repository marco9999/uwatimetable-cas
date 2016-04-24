package com.github.marco9999.uwatimetable;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Marco on 24/04/2016.
 */
public class ProgressDialogEngineTimetableCAS extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Android bug: https://code.google.com/p/android/issues/detail?id=17423
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage(getString(R.string.progress_readingfromcas));
        dialog.setIndeterminate(true);

        return dialog;
    }

    @Override
    public void onDestroyView() {
        // Android bug: https://code.google.com/p/android/issues/detail?id=17423
        if (getDialog() != null && getRetainInstance()) getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

}
