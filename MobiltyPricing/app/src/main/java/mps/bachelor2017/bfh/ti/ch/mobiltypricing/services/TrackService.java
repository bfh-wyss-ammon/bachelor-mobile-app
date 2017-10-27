package mps.bachelor2017.bfh.ti.ch.mobiltypricing.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileTuple;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.SyncTupleTask;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.TollTask;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.DatabaseHelper;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Error;
import util.HashHelper;
import util.SignHelper;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;
import static android.util.Base64.NO_WRAP;


/**
 * Created by Pascal on 13.10.2017.
 */

public class TrackService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SyncTupleTask.SendTupleTaskListener, TollTask.TollTaskListener {

    public interface TrackServiceEvents {
        void onStatusChanged(TrackServiceStatus status);
    }

    public enum TrackServiceStatus {
        NO_GPS_SIGNAL,
        NO_GPS_SIGNAL_PERMISSION,
        NO_WLAN_SIGNAL_PERMISSION,
        DRIVE,
        READY,
        ERROR_DURING_PERSISTING,
        ERROR_DURING_SYNC,
        GPS_SIGNAL_TRACKED,
        GPS_SIGNAL_SYNC,
        PAY_SUCCESSFULL,
        PAY_ERROR
    }

    public class TrackBinder extends Binder {
        public TrackService getService() {
            return TrackService.this;
        }
    }

    //used for check if run method is called for the first time
    protected int warmUpCounter = 5;
    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    protected long updateInterval = 5000;

    private Timer timer;
    private Location mLocation;
    private MobileSecretKey mMobileSecretKey;
    private MobileGroup mMobileGroup;
    private DatabaseHelper dbHelper;
    private TrackServiceEvents mEvent;
    private final IBinder mBinder = new TrackBinder();
    private int syncCount = 0;
    private boolean isTracking = false;

    @Override
    public void onCreate() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(updateInterval);
            mLocationRequest.setFastestInterval(updateInterval);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        SharedPreferences settings = getSharedPreferences(Const.PreferenceKey, 0);
        if (mMobileGroup == null) {
            mMobileGroup = new MobileGroup(settings);
        }
        if (mMobileSecretKey == null) {
            mMobileSecretKey = new MobileSecretKey(settings);
        }
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(getApplicationContext());
        }
    }

    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("TrackService", "suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("TrackService", "connection failed");
    }

    public void setCallbacks(TrackServiceEvents callbacks) {
        mEvent = callbacks;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("TrackService", "new location time:" + location.getTime() + " latitude:" + location.getLatitude() + " longitude" + location.getLongitude());
        mLocation = location;
    }

    private TrackServiceStatus getStatus() {
        boolean gpsPermission = hasGpsPermission();
        boolean networkPermission = hasNetworkPermission();
        return gpsPermission && networkPermission ? isTracking ? TrackServiceStatus.DRIVE : TrackServiceStatus.READY : !gpsPermission ? TrackServiceStatus.NO_GPS_SIGNAL_PERMISSION : TrackServiceStatus.NO_WLAN_SIGNAL_PERMISSION;
    }

    private boolean checkPermission() {
        TrackServiceStatus status = getStatus();
        if (status != TrackServiceStatus.READY && status != TrackServiceStatus.DRIVE) {
            mEvent.onStatusChanged(status);
            return false;
        }
        return true;
    }

    public void start() {
        if (!checkPermission()) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!checkPermission()) {
                    timer.cancel();
                    return;
                }
                if (mLocation == null) {
                    if (warmUpCounter > 0) {
                        warmUpCounter--;
                    } else {
                        mEvent.onStatusChanged(TrackServiceStatus.NO_GPS_SIGNAL);
                        timer.cancel();
                    }
                    return;
                }

                if (!isTracking) {
                    mEvent.onStatusChanged(TrackServiceStatus.DRIVE);
                    isTracking = true;
                }
                MobileTuple tuple = new MobileTuple(mMobileGroup.getGroupId(), new BigDecimal(mLocation.getLatitude()).setScale(10, RoundingMode.HALF_UP), new BigDecimal(mLocation.getLongitude()).setScale(10, RoundingMode.HALF_UP), new Date());
                SignHelper.sign(mMobileSecretKey, mMobileGroup.getPublicKey(), HashHelper.getHash(tuple), tuple.getSignature());
                tuple.setHash(Base64.encodeToString(HashHelper.getHash(tuple.getSignature()), NO_WRAP));

                if (!dbHelper.save(tuple)) {
                    isTracking = false;
                    mEvent.onStatusChanged(TrackServiceStatus.ERROR_DURING_PERSISTING);
                    timer.cancel();
                    return;
                }
                else {
                    mEvent.onStatusChanged(TrackServiceStatus.GPS_SIGNAL_TRACKED);
                }
                if (syncCount == 0) {
                    syncCount = -1;
                    List<MobileTuple> tuples = dbHelper.getTuplesByStatus(MobileTuple.TupleStatus.LOCAL);
                    syncCount = tuples.size();

                    tuples.forEach(t -> {
                        SyncTupleTask snycTupleTask = new SyncTupleTask(TrackService.this, getApplicationContext());
                        snycTupleTask.execute(t);
                    });
                }
            }
        }, 0, updateInterval);
    }

    public void stop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        isTracking = false;
        timer.cancel();
        TollTask tk = new TollTask(this, getApplicationContext(), mMobileGroup, mMobileSecretKey);
        tk.execute();
    }

    @Override
    public void onTupleSendError() {
        timer.cancel();
        syncCount = 0;
        mEvent.onStatusChanged(TrackServiceStatus.ERROR_DURING_SYNC);
    }

    @Override
    public void onTupleSendSuccessfull() {
        syncCount--;
        mEvent.onStatusChanged(TrackServiceStatus.GPS_SIGNAL_SYNC);
    }

    private boolean hasGpsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return false;
        return true;
    }

    @Override
    public void onTollError(Error error) {

        mEvent.onStatusChanged(TrackServiceStatus.PAY_ERROR);
    }

    @Override
    public void onTollSuccessfull() {
        mEvent.onStatusChanged(TrackServiceStatus.PAY_SUCCESSFULL);
    }

    private boolean hasNetworkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PERMISSION_DENIED) {
            return false;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // TODO error handling?
        }
        if (!mobileDataEnabled) {
            return false;
        }

        //check if wifi is enabled
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            return false;
        }
        return true;
    }
}
