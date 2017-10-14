package mps.bachelor2017.bfh.ti.ch.mobiltypricing.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.R;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;

/**
 * Created by Pascal on 13.10.2017.
 */

public class DriveFragment extends Fragment {
    public interface OnLogOutSuccessfullListener {
        public void onLogOutSuccessfull();
    }

    public interface DriveListener {
        public void start();
        public void stop();
    }

    private View view;
    private Button mDriveButton;
    private Button mLogOutButton;
    private boolean isDrving = false;

    public OnLogOutSuccessfullListener onLogOutSuccessfullListener;
    public DriveListener driveListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_drive, container, false);
        this.mLogOutButton = (Button) view.findViewById(R.id.logoutButton);
        this.mDriveButton = (Button) view.findViewById(R.id.driveButton);

        this.mDriveButton.setOnClickListener(v -> {
            if(driveListener != null) {
                if(isDrving) {
                    mDriveButton.setText(getResources().getText(R.string.start_driving));
                    driveListener.stop();
                    isDrving = false;
                }
                else {
                    mDriveButton.setText(getResources().getText(R.string.stop_driving));
                    driveListener.start();
                    isDrving = true;
                }
            }
            else {
                Log.v("DriveFragment", "needs drive listener");
            }
        });


        this.mLogOutButton.setOnClickListener(v -> {
            SharedPreferences settings = v.getContext().getSharedPreferences(Const.PreferenceKey, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("status", false);
            editor.commit();

            if(onLogOutSuccessfullListener != null) {
                onLogOutSuccessfullListener.onLogOutSuccessfull();
            }
        });

        return view;
    }
}
