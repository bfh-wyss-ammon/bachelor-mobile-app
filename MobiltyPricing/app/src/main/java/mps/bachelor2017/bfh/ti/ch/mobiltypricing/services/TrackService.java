package mps.bachelor2017.bfh.ti.ch.mobiltypricing.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.CheckInActivity;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.LoginActivity;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileTuple;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.SyncTupleTask;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.TollTask;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.DatabaseHelper;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Error;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.UserHandler;
import util.HashHelper;
import util.SignHelper;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import static android.util.Base64.NO_WRAP;


/**
 * Created by Pascal on 13.10.2017.
 */

public class TrackService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SyncTupleTask.SendTupleTaskListener, TollTask.TollTaskListener {

    public class TrackBinder extends Binder {
        public TrackService getService() {
            return TrackService.this;
        }
    }

    //used for check if run method is called for the first time
    protected int warmUpCounter = 5;
    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    protected long updateInterval = 10 * 1000;

    private Timer timer;
    private Location mLastLocation;
    private DatabaseHelper dbHelper;
    private DriveEvents mEvent;
    private final IBinder mBinder = new TrackBinder();
    private int syncCount = 0;
    private boolean isTracking = false;

    private ConnectionEvents mConnectionEvents;

    @Override
    public void onCreate() {
       setupApiConnection();
       setupLocationRequest();

        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(getApplicationContext());
        }
    }

    //region Service Setup
    public void setCallbacks(DriveEvents callbacks) {
        mEvent = callbacks;
    }

    public void registerConnectionEvents(ConnectionEvents connectionEvents) {
        mConnectionEvents = connectionEvents;
    }

    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private int gpsPermission = -2;
    private int networkPermission = -2;

    public int getGpsPermission() {
        return gpsPermission;
    }

    public void setGpsPermission(int gpsPermission) {
        this.gpsPermission = gpsPermission;

        if(mConnectionEvents != null && networkPermission == PERMISSION_GRANTED && gpsPermission == PERMISSION_GRANTED) {
            mConnectionEvents.onHasGpsAndNetworkPermission();
        }
    }

    public int getNetworkPermission() {
        return networkPermission;
    }

    public void setNetworkPermission(int networkPermission) {
        this.networkPermission = networkPermission;

        if(mConnectionEvents != null && networkPermission == PERMISSION_GRANTED && gpsPermission == PERMISSION_GRANTED) {
            mConnectionEvents.onHasGpsAndNetworkPermission();
        }
    }

    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mConnectionEvents != null) {
            // we try to get gps permission by activity
            mConnectionEvents.onGpsPermissionMissing();
        }
        else {
            setGpsPermission(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION));
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED && mConnectionEvents != null) {
            // we try to get internet permission by activity
            mConnectionEvents.onNetworkPermissionMissing();
        }
        else {
            setNetworkPermission(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET));
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    //endregion

    //region GPS
    private void setupApiConnection() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    private void setupLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(updateInterval);
            mLocationRequest.setFastestInterval(updateInterval);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("TrackService", "new location time:" + location.getTime() + " latitude:" + location.getLatitude() + " longitude" + location.getLongitude());
        mLastLocation = location;
    }
    //endregion

    private void startLocationUpdates() {
        if(mGoogleApiClient.isConnected() == false) {
            mEvent.onError();
            Log.v("TrackService", "can't start listening gps without connection to google api");
        }

        // the permission handling is handled by parent view
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           mEvent.onError();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void start() {
        startLocationUpdates();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mLastLocation == null) {
                    if (warmUpCounter > 0) {
                        warmUpCounter--;
                    } else {
                        mEvent.onGpsSignalError();
                        timer.cancel();
                    }
                    return;
                }

                if (!isTracking) {
                    isTracking = true;
                }
                MobileTuple tuple = new MobileTuple(UserHandler.getGroupId(getApplicationContext()), new BigDecimal(mLastLocation.getLatitude()).setScale(10, RoundingMode.HALF_UP), new BigDecimal(mLastLocation.getLongitude()).setScale(10, RoundingMode.HALF_UP), new Date());
                byte[] hash = HashHelper.getHash(tuple);
                SignHelper.sign(UserHandler.getSecretKey(getApplicationContext()), UserHandler.getPublicKey(getApplicationContext()), hash, tuple.getSignature());

                tuple.setHash(Base64.encodeToString(hash, NO_WRAP));

                if (!dbHelper.save(tuple)) {
                    isTracking = false;
                    Log.v("TrackService", "Error while saving tuple");
                    mEvent.onError();
                    timer.cancel();
                    return;
                }

                mEvent.onGpsSignal();

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
        TollTask tk = new TollTask(this, getApplicationContext());
        tk.execute();
    }

    @Override
    public void onTupleSendError() {
        Log.v("TrackService", "Error while send tuple");
    }

    @Override
    public synchronized void onTupleSendSuccessfull() {
        syncCount--;
        mEvent.onSend();
    }

    @Override
    public synchronized void onTollError(Error error) {
        Log.v("TrackService", "Error while toll");
        mEvent.onError();
    }

    @Override
    public void onTollSuccessfull() {
        mEvent.onFinished();
    }
}
