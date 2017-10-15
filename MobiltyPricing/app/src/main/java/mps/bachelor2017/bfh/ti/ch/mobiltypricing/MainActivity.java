package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.fragments.DriveFragment;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.fragments.LoginFragment;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.fragments.MissingPermissionFragment;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.TrackService;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

public class MainActivity extends AppCompatActivity {

    private Boolean mIsLoggedIn = false;

    private LoginFragment mLoginFragment;
    private DriveFragment mDriveFragment;
    private FragmentManager mFragmentManager;
    private TrackService mTrackService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // todo network and GPS check!
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == 0 || ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
     //   if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
     //       ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
     //   }

        setContentView(R.layout.activity_main);

        mIsLoggedIn = getSharedPreferences(Const.PreferenceKey, 0).getBoolean("status", false);

        mFragmentManager = getFragmentManager();

        // start service

        Intent intent = new Intent(this, TrackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TrackService.TrackBinder binder = (TrackService.TrackBinder) service;
            mTrackService = binder.getService();
            mBound = true;

            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            Fragment fragment = mIsLoggedIn ? getDriveFragment() : getLoginFragment();
            fragmentTransaction.add(R.id.hostLayout, fragment);
            fragmentTransaction.commit();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // todo error handling??
            mBound = false;
        }
    };

    private LoginFragment getLoginFragment() {
        mLoginFragment = new LoginFragment();
        mLoginFragment.onLogInSuccessfullListener = () -> {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.remove(mLoginFragment);
            fragmentTransaction.add(R.id.hostLayout, getDriveFragment());
            fragmentTransaction.commit();
            mLoginFragment = null;


        };
        return mLoginFragment;
    }

    public void makeNotify(){



    }
    private DriveFragment getDriveFragment() {
        mDriveFragment = new DriveFragment();
        mDriveFragment.onLogOutSuccessfullListener = () -> {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.remove(mDriveFragment);
            fragmentTransaction.add(R.id.hostLayout, getLoginFragment());
            fragmentTransaction.commit();
            mDriveFragment = null;
        };

        mDriveFragment.driveListener = new DriveFragment.DriveListener() {
            @Override
            public void start() {

                mTrackService.start();

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_stat_na)
                                .setContentTitle("Mobility Pricing is running")
                                .setOngoing(true)
                                .setContentText("You are currently driving.");


                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);



                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                int mNotificationId = 001;
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());


            }

            @Override
            public void stop() {
                mTrackService.stop();

                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(001);
            }
        };
        return mDriveFragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        for (int grantResult : grantResults) {
            if(grantResult == PERMISSION_DENIED) {
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                if(mLoginFragment != null) {
                    fragmentTransaction.remove(mLoginFragment);
                }
                if(mDriveFragment != null) {
                    fragmentTransaction.remove(mDriveFragment);
                }
                fragmentTransaction.add(R.id.hostLayout, new MissingPermissionFragment());
                fragmentTransaction.commit();
            }
        }
    }
}
