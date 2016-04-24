package com.github.marco9999.uwatimetable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Marco on 23/04/2016.
 */
public class UtilRetainFragment extends Fragment {
    // This fragment is set to retain across config changes, which means any object that references this fragment is able to cache it locally.
    // It is only destroyed when the whole program exits.

    private EngineTimetableCAS engineTC;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment.
        setRetainInstance(true);
    }

    public EngineTimetableCAS getEngineTimetableCAS() {
        if (engineTC == null) {
            engineTC = new EngineTimetableCAS(this);
        }

        if (engineTC.getStatus() == AsyncTask.Status.FINISHED) {
            engineTC = new EngineTimetableCAS(this);
        }

        return engineTC;
    }

    public UtilFragment getUtilFragment() {
        // NOTE: DO NOT CACHE RESULT!
        // This can be used by any object that needs access to context-sensitive data, which is handled by the UtilFragment.
        // If cached, it will become invalid when a config change happens, such as device rotation.

        // Find the data Fragment.
        UtilFragment utilFragment = (UtilFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Tag.Fragment.UTIL);
        assert (utilFragment != null);

        // Return the adapter.
        return utilFragment;
    }


}
