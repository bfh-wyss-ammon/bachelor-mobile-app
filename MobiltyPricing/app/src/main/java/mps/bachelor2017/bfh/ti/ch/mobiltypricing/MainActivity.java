package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.User;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.TrackService;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.LoginTask;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.NetworkCheck;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Helper;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.UserHandler;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;
import static junit.framework.Assert.fail;

/**
 * Created by Pascal on 06.10.2017.
 */

public class MainActivity extends AppCompatActivity implements NetworkCheck.NetworkCheckEvents, LoginFragment.LoginListener, LoginTask.LoginTaskListener, CheckInFragment.CheckInEvents, TrackService.TrackServiceEvents {

    private boolean hasNetworkConnection = false;
    private boolean hasGpsPermission = false;
    private  AnimatedVectorDrawable gpsAnimatedVectorDrawable;
    private AnimatedVectorDrawable networkAnimatedVectorDrawable;
    private ImageButton mNetworkConnectionStatus;
    private ImageButton mGpsStatus;
    private LoginFragment mLoginFragment;
    private CheckInFragment mCheckInFragment;
    private DriveFragment mDriveFragment;

    private boolean error;
    private TrackService mTrackService;
    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TrackService.TrackBinder binder = (TrackService.TrackBinder) service;
            mTrackService = binder.getService();
            mBound = true;
            mTrackService.setCallbacks(MainActivity.this);

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
        setContentView(R.layout.activity_entry);

        initUserInterfaceComponents();
        ensureGpsPermission();
        ensureNetworkConnection();

        // start service
        Intent intent = new Intent(this, TrackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initUserInterfaceComponents() {
        this.mNetworkConnectionStatus = (ImageButton) findViewById(R.id.NetworkStatusButton);
        this.mGpsStatus = (ImageButton) findViewById(R.id.GpsStatusButton);
        this.mNetworkConnectionStatus.setOnClickListener(v -> {
            if(!hasNetworkConnection)
                ensureNetworkConnection();
        });

        this.mGpsStatus.setOnClickListener(v -> {
            if(!hasGpsPermission)
                ensureGpsPermission();
        });

        this.gpsAnimatedVectorDrawable = (AnimatedVectorDrawable) getDrawable(R.drawable.ic_position_animated);
        this.networkAnimatedVectorDrawable = (AnimatedVectorDrawable) getDrawable(R.drawable.ic_network_animated);
    }

    private void ensureGpsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setGpsLoading();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            this.hasGpsPermission = true;
            setGpsSuccessfully();
        }
    }

    private void ensureNetworkConnection() {
        setNetworkLoading();
        NetworkCheck networkCheck = new NetworkCheck(this, getApplicationContext());
        networkCheck.execute();
    }

    private synchronized void handleStatusChange() {
        if(hasGpsPermission && hasNetworkConnection) {
            if(UserHandler.exist(getApplicationContext())) {
                if(mCheckInFragment == null) {
                    mCheckInFragment = new CheckInFragment();
                    mCheckInFragment.setCheckInEvents(this);
                }
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.MainArea, mCheckInFragment);
                fragmentTransaction.commit();

            }
            else {
                if(mLoginFragment == null) {
                    mLoginFragment = new LoginFragment();
                    mLoginFragment.setOnLoginListener(this);
                }
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.MainArea, mLoginFragment);
                fragmentTransaction.commit();
            }
        }
    }

    //region Login & logout
    private void logout() {
        UserHandler.clear(getApplicationContext());
        handleStatusChange();
    }

    @Override
    public void onLogin(String id, String pwd) {
        mLoginFragment.animate();
        LoginTask loginTask = new LoginTask(this, getApplicationContext());


        loginTask.execute(new User(id, Helper.getHash(pwd)));
    }

    @Override
    public void onLoginError() {
        Toast.makeText(getApplicationContext(), "general login error. please contact service desk.", Toast.LENGTH_SHORT).show();
        mLoginFragment.stopAnimate();
    }

    @Override
    public void onAuthenticationError() {
        Toast.makeText(getApplicationContext(), "login error. wrong password or id", Toast.LENGTH_SHORT).show();
        mLoginFragment.stopAnimate();
    }

    @Override
    public void onAlreadyLoggedInError() {
        Toast.makeText(getApplicationContext(), "you are already loggedIn!", Toast.LENGTH_SHORT).show();
        mLoginFragment.stopAnimate();
    }

    @Override
    public void onLoginSuccessfully() {
        Toast.makeText(getApplicationContext(), "login successfully", Toast.LENGTH_SHORT).show();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(mLoginFragment);
        fragmentTransaction.commit();
        mLoginFragment.onDestroy();
        mLoginFragment = null;
    }

    //endregion

    //region Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if(grantResults[i] == PERMISSION_DENIED) {
                if(Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                    setGpsError();
                }
                else  if(Manifest.permission.INTERNET.equals(permissions[i])) {
                    setNetworkError();
                }
            }
            else {
                if(Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                    setGpsSuccessfully();
                }
            }
        }
    }

    @Override
    public void onNetworkCheckSuccessfully() {
        setNetworkSuccessfully();
    }

    @Override
    public void onNetworkCheckError(Exception error) {
        runOnUiThread(this::setNetworkError);
    }

    private void setNetworkLoading() {
        runOnUiThread(() -> {
            this.mNetworkConnectionStatus.setImageDrawable(networkAnimatedVectorDrawable);
            this.networkAnimatedVectorDrawable.start();
        });
    }

    private void setGpsLoading() {
        runOnUiThread(() -> {
            this.mGpsStatus.setImageDrawable(gpsAnimatedVectorDrawable);
            this.gpsAnimatedVectorDrawable.start();
        });
    }

    private void setNetworkSuccessfully() {
        runOnUiThread(() -> {
            this.mNetworkConnectionStatus.setImageResource(R.drawable.ic_network_oke);
        });
        this.hasNetworkConnection = true;
        handleStatusChange();
    }

    private void setGpsSuccessfully() {
        runOnUiThread(() -> {
            this.mGpsStatus.setImageResource(R.drawable.ic_positional_map_oke);
        });
        this.hasGpsPermission = true;
        handleStatusChange();
    }

    private void setGpsError() {
        runOnUiThread(() -> {
            this.mGpsStatus.setImageResource(R.drawable.ic_positional_map_oke);
        });
        this.hasGpsPermission = false;
        handleStatusChange();
    }

    private void setNetworkError() {
        runOnUiThread(() -> {
            this.mNetworkConnectionStatus.setImageResource(R.drawable.ic_network_fail);
            Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_SHORT).show();
        });
        this.hasNetworkConnection = false;
        handleStatusChange();
    }
    // endregion

    //region drive
    @Override
    public void checkInStart() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(mCheckInFragment);
        fragmentTransaction.commit();
        mTrackService.startLocationUpdates();
    }

    private void onDriveError() {
        this.hasGpsPermission = false;
        if(mDriveFragment != null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(mDriveFragment);
            fragmentTransaction.commit();
            handleStatusChange();
        }
    }

    @Override
    public void onGpsSignalError() {
        Toast.makeText(getApplicationContext(), "no gps signal!", Toast.LENGTH_LONG).show();
        onDriveError();
    }

    @Override
    public void onGpsSignal() {
        mTrackService.start();


    }

    @Override
    public void onDrive() {
        if(mDriveFragment == null) {
            mDriveFragment = new DriveFragment();
        }
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.MainArea, mDriveFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onError() {
        Toast.makeText(getApplicationContext(), "Error while drive. please contact support!", Toast.LENGTH_LONG).show();
        onDriveError();
    }

    @Override
    public void onSend() {
        mDriveFragment.animate();
    }

    @Override
    public void onFinished() {
        Toast.makeText(getApplicationContext(), "thanks for driving!", Toast.LENGTH_LONG).show();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(mDriveFragment);
        fragmentTransaction.commit();
        handleStatusChange();
    }


    //endregion



}
