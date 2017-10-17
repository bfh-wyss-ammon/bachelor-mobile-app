package mps.bachelor2017.bfh.ti.ch.mobiltypricing.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.R;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;

/**
 * Created by Pascal on 13.10.2017.
 */

public class MissingPermissionFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_missing_permission, container, false);
    }
}
