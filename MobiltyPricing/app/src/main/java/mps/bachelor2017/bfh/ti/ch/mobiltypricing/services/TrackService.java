package mps.bachelor2017.bfh.ti.ch.mobiltypricing.services;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import data.Tuple;
import interfaces.HashValue;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.SendTupleTask;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;


/**
 * Created by Pascal on 13.10.2017.
 */

public class TrackService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SendTupleTask.SendTupleTaskListener {

    private enum Status {
        initialized,
        connected,
        suspended,
        error
    }

    private final IBinder mBinder = new TrackBinder();

    public class TrackBinder extends Binder {
        public TrackService getService() {
            return TrackService.this;
        }
    }

    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    protected long updateInterval = 2000;

    private Timer timer;

    private Status mStatus;
    private Location mLocation;

    private MobileSecretKey mMobileSecretKey;
    private MobileGroup mMobileGroup;

    @Override
    public void onCreate() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
            this.mStatus = Status.initialized;
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
        this.mStatus = Status.connected;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("TrackService", "suspended");
        this.mStatus = Status.suspended;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("TrackService", "connection failed");
        this.mStatus = Status.error;
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.v("TrackService", "new location time:" + location.getTime() + " latitude:" + location.getLatitude() + " longitude" + location.getLongitude());
        mLocation = location;
    }

    public void start() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v("TrackService", "permission denied");
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mLocation == null) {
                    Log.v("TrackService", "no location jet");
                    return;
                }
                Tuple tuple = new Tuple();
                tuple.setLatitude(new BigDecimal(mLocation.getLatitude()).setScale(10, RoundingMode.HALF_UP));
                tuple.setLongitude(new BigDecimal(mLocation.getLongitude()).setScale(10, RoundingMode.HALF_UP));
                //todo: discuss if we should use the one from location..
                tuple.setCreated(new Date());
                SendTupleTask sendTupleTask = new SendTupleTask(TrackService.this, getApplicationContext(), mMobileGroup, mMobileSecretKey);
                sendTupleTask.execute(tuple);
                Log.v("TrackService", "timer yeah!");
            }
        }, 0, 30000);
    }

    public void stop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        timer.cancel();
    }

    @Override
    public void onTupleSendError() {
        // todo error handling!
        Toast.makeText(getApplicationContext(), "tuple send error", Toast.LENGTH_SHORT).show();
        Log.v("TrackService", "tuple send error");
    }

    @Override
    public void onTupleSendSuccessfull() {
        Toast.makeText(getApplicationContext(), "tuple send successfull ;-)", Toast.LENGTH_SHORT).show();
        Log.v("TrackService", "tuple send successfull");
    }
}
