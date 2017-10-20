package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.reflect.Method;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.fragments.DriveFragment;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.fragments.LoginFragment;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.fragments.MissingPermissionFragment;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.TrackService;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.TrackServiceCallbacks;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

public class MainActivity extends AppCompatActivity implements TrackServiceCallbacks {

    public static final int DRIVING_NOTIFICATION = 001;
    public static final int NOPERMISSION_NOTIFICATION = 002;

    private Boolean mIsLoggedIn = false;

    private LoginFragment mLoginFragment;
    private DriveFragment mDriveFragment;
    private FragmentManager mFragmentManager;
    private TrackService mTrackService;
    private boolean mBound;

    @Override
    public boolean permissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PERMISSION_DENIED){
            Log.v("V","internet perm not ok");
            return false;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.v("V","gps perm not ok");
            return false;
        }

        //check if we really have mobile data connection
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            // TODO error handling?
        }
        if(!mobileDataEnabled) {
            Log.v("V","mobile data not ok");
            return false;
        }

        //check if wifi is enabled
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if(!wifi.isWifiEnabled()) {
            Log.v("V","wifi not ok");
            return false;
        }

        Log.v("V","was ok");
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // todo network and GPS check!
        if (!permissionCheck()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


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
    public void onStop(){
        // todo
        super.onStop();
    }

    @Override
    public void onError(String Message) {

    }

    @Override
    public void onStart() {
        // todo
        super.onStart();
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
}
