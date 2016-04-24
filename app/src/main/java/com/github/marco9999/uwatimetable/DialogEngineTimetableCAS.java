package com.github.marco9999.uwatimetable;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Marco on 24/04/2016.
 */
public class DialogEngineTimetableCAS extends DialogFragment implements TextWatcher {

    private Button loginButton;
    private EditText username;
    private EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Android bug: https://code.google.com/p/android/issues/detail?id=17423
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Set title.
        builder.setTitle(R.string.title_readingfromcas);

        // Inflate and set the layout for the dialog.
        @SuppressLint("InflateParams") // Pass null as the parent view because its going in the dialog layout.
        View rootView = inflater.inflate(R.layout.dialog_readfromcas_login, null);
        builder.setView(rootView)
                // Add action buttons.
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        UtilRetainFragment utilRetainFragment = (UtilRetainFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Tag.Fragment.UTIL_RETAIN);
                        assert (utilRetainFragment != null);

                        // Get user credentials from EditText's.
                        Dialog dialog = DialogEngineTimetableCAS.this.getDialog();
                        assert (dialog != null);


                        // Run the CAS engine on an AsyncTask thread.
                        EngineTimetableCAS.UserDetails userDetails = new EngineTimetableCAS.UserDetails(username.getText().toString(), password.getText().toString());
                        utilRetainFragment.getEngineTimetableCAS().execute(userDetails);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogEngineTimetableCAS.this.getDialog().cancel();
                    }
                });

        // Needed for checking text lengths later, in order to enable login button.
        username = (EditText) rootView.findViewById(R.id.readfromcas_username);
        password = (EditText) rootView.findViewById(R.id.readfromcas_password);
        assert (username != null && password != null);
        username.addTextChangedListener(this);
        password.addTextChangedListener(this);

        // Create dialog.
        AlertDialog dialog = builder.create();

        // Set login button initially to disabled, until text is entered in both fields.
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // Set login button to false initially, only enable when input for both fields has been entered.
                loginButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                if (username.getText().length() > 0 && password.getText().length() > 0) {
                    loginButton.setEnabled(true);
                }
                else {
                    loginButton.setEnabled(false);
                }
            }
        });

        return dialog;
    }

    @Override
    public void onDestroyView() {
        // Android bug: https://code.google.com/p/android/issues/detail?id=17423
        if (getDialog() != null && getRetainInstance()) getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (username.getText().length() > 0 && password.getText().length() > 0) {
            loginButton.setEnabled(true);
        }
        else {
            loginButton.setEnabled(false);
        }
    }
}
