package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.reflect.Method;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.fragments.DriveFragment;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.fragments.LoginFragment;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.fragments.MissingPermissionFragment;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.TrackService;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.TrackService.TrackServiceStatus;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;
import static mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.TrackService.TrackServiceStatus.*;

public class MainActivity extends AppCompatActivity implements TrackService.TrackServiceEvents {

    private Boolean mIsLoggedIn = false;
    private LoginFragment mLoginFragment;
    private DriveFragment mDriveFragment;
    private FragmentManager mFragmentManager;
    private TrackService mTrackService;
    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TrackService.TrackBinder binder = (TrackService.TrackBinder) service;
            mTrackService = binder.getService();
            mBound = true;
            mTrackService.setCallbacks(MainActivity.this);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIsLoggedIn = getSharedPreferences(Const.PreferenceKey, 0).getBoolean("status", false);
        mFragmentManager = getFragmentManager();

        // start service
        Intent intent = new Intent(this, TrackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == 0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
    }

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
            }

            @Override
            public void stop() {
                mTrackService.stop();
            }
        };
        return mDriveFragment;
    }

    @Override
    public void onStatusChanged(TrackServiceStatus status) {

        switch (status) {
            case NO_GPS_SIGNAL:
            case NO_GPS_SIGNAL_PERMISSION:
            case NO_WLAN_SIGNAL_PERMISSION:
            case ERROR_DURING_PERSISTING:
            case ERROR_DURING_SYNC: createNotification(1, "Mobility Pricing", "Error, check permissions and/or network connection", true, false); break;
            case DRIVE: break;
            case READY: break;
            case GPS_SIGNAL_TRACKED:
            case GPS_SIGNAL_SYNC:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PERMISSION_DENIED) {
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                if (mLoginFragment != null) {
                    fragmentTransaction.remove(mLoginFragment);
                }
                if (mDriveFragment != null) {
                    fragmentTransaction.remove(mDriveFragment);
                }
                fragmentTransaction.add(R.id.hostLayout, new MissingPermissionFragment());
                fragmentTransaction.commit();
            }
        }
    }

    public void clearNotification(int id) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    public void createNotification(int id, String title, String text, boolean ongoing, boolean lightsAndVibrate) {

        NotificationCompat.Builder mBuilder;
        if (lightsAndVibrate) {

            mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_stat_na)
                            .setContentTitle(title)
                            .setOngoing(ongoing)
                            .setContentText(text)
                            .setLights(0xFF0000FF, 100, 3000)
                            .setPriority(Notification.PRIORITY_DEFAULT)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        } else {
            mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_stat_na)
                            .setContentTitle(title)
                            .setOngoing(ongoing)
                            .setContentText(text);
        }


        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);


        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(id, mBuilder.build());
    }
}
