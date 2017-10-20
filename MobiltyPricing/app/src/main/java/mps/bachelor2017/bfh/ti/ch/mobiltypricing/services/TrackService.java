package mps.bachelor2017.bfh.ti.ch.mobiltypricing.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
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
import data.Tuple;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.MainActivity;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.R;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.DbTuple;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.SyncTupleTask;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.DatabaseHelper;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.TrackServiceCallbacks;


/**
 * Created by Pascal on 13.10.2017.
 */

public class TrackService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SyncTupleTask.SendTupleTaskListener {

    // Registered callbacks
    private TrackServiceCallbacks trackServiceCallbacks;

    private enum Status {
        initialized,
        connected,
        suspended,
        error
    }

    //used for check if run method is called for the first time
    private int warmUpCounter = 5;

    private final IBinder mBinder = new TrackBinder();

    public class TrackBinder extends Binder {
        public TrackService getService() {
            return TrackService.this;
        }
    }

    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    protected long updateInterval = 30000;

    private Timer timer;

    private Location mLocation;

    private MobileSecretKey mMobileSecretKey;
    private MobileGroup mMobileGroup;
    private DatabaseHelper dbHelper;

    private int syncCount = 0;

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
        if(dbHelper == null) {
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

    public void setCallbacks(TrackServiceCallbacks callbacks) {
        trackServiceCallbacks = callbacks;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("TrackService", "new location time:" + location.getTime() + " latitude:" + location.getLatitude() + " longitude" + location.getLongitude());
        mLocation = location;
    }

    private boolean gpsPermissionIsOk() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return false;
        return true;
    }


    public void start() {
        boolean internetOk = false;
        if (trackServiceCallbacks != null) {
            internetOk = trackServiceCallbacks.permissionCheck();
        }
        if (!(gpsPermissionIsOk() && internetOk)) {
            Log.v("TrackService", "permission denied");
            createNotification(MainActivity.NOPERMISSION_NOTIFICATION, "Can't start driving. No permission.", "Permission problem.", false, true);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!(gpsPermissionIsOk() && trackServiceCallbacks.permissionCheck())) {
                    stop();
                    createNotification(MainActivity.NOPERMISSION_NOTIFICATION, "Can't start driving. No permission.", "Permission problem.", false, true);
                    trackServiceCallbacks.onError("Permission error. Ensure App has Network and GPS Permissions.");

                    return;
                }
                if (mLocation == null)
                {
                    if (warmUpCounter > 0) {
                        warmUpCounter--;
                        Log.v("TrackService", "no location jet");
                    }
                    else {
                        Log.v("TrackService", "no location data. stop driving.");
                        stop();
                        createNotification(MainActivity.NOPERMISSION_NOTIFICATION, "Can't start driving. No location data.", "GPS problem", false, true);
                        trackServiceCallbacks.onError("No GPS signal, after " + warmUpCounter + ".");
                    }
                }
                else {
                    Tuple tuple = new Tuple();
                    tuple.setLatitude(new BigDecimal(mLocation.getLatitude()).setScale(10, RoundingMode.HALF_UP));
                    tuple.setLongitude(new BigDecimal(mLocation.getLongitude()).setScale(10, RoundingMode.HALF_UP));
                    tuple.setCreated(new Date());
                    tuple.setGroupId(mMobileGroup.getGroupId());
                    if(!dbHelper.insertTuple(tuple)) {
                        trackServiceCallbacks.onError("error during persist data!");
                    }
                    //todo find better handling
                    if(syncCount == 0) {
                        List<DbTuple> tuples = dbHelper.getNotUploadedTuples();
                        tuples.forEach(t -> {
                            SyncTupleTask snycTupleTask = new SyncTupleTask(TrackService.this, getApplicationContext(), mMobileGroup, mMobileSecretKey);
                            snycTupleTask.execute(t);
                        });
                    }
                }
            }
        }, 0, updateInterval);

        createNotification(MainActivity.DRIVING_NOTIFICATION, "Mobility Pricing is running", "You are currently driving", true, false);
    }

    public void stop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        timer.cancel();
        clearNotification(MainActivity.DRIVING_NOTIFICATION);
    }

    @Override
    public void onTupleSendError() {
        syncCount--;
        // todo error handling!
        Toast.makeText(getApplicationContext(), "tuple send error", Toast.LENGTH_SHORT).show();
        Log.v("TrackService", "tuple send error");
    }

    @Override
    public void onTupleSendSuccessfull() {
        syncCount--;
        Toast.makeText(getApplicationContext(), "tuple send successfull ;-)", Toast.LENGTH_SHORT).show();
        Log.v("TrackService", "tuple send successfull");
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

    public void clearNotification(int id) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
}
