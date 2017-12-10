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
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

import gson.BigIntegerTypeAdapter;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.ErrorActivity;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.InvoiceItems;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSignature;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileTuple;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.Payment;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.DatabaseHelper;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.UserHandler;
import util.HashHelper;
import util.SignHelper;

import static android.util.Base64.NO_WRAP;


/**
 * Created by Pascal on 13.10.2017.
 */

public class TrackService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public class TrackBinder extends Binder {
        public TrackService getService() {
            return TrackService.this;
        }
    }

    protected int warmUpTime = 12 * 1000; //used for check if run method is called for the first time
    protected long updateInterval = 10 * 1000;
    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    private final IBinder mBinder = new TrackBinder();
    private static final Gson gson = new Gson();

    private Timer timer;
    private Location mLastLocation;
    private Location mNewLocation;
    private DatabaseHelper dbHelper;
    private int syncCount = 0;
    private int periodeSynCount = 0;
    private boolean isTracking = false;
    private int gpsTimeUnityToFix = 5;
    private int networkTimeUnityToFix = 5;
    private RequestQueue queue;
    private List<String> hashes;

    private TrackServiceEvents mTrackServiceEvents;

    @Override
    public void onCreate() {
        setupApiConnection();
        setupLocationRequest();

        if(queue == null) {
            queue = Volley.newRequestQueue(getApplicationContext());
        }
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(getApplicationContext());
        }
    }

    //region Service Setup
    public void setCallbacks(TrackServiceEvents callbacks) {
        mTrackServiceEvents = callbacks;
    }

    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public boolean isTracking() {
        return isTracking;
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
        mNewLocation = location;
    }
    //endregion

    public boolean start() {
        if (isTracking) {
            return false;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          Log.v("TrackService", "Missing GPS Permissions");
          return false;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isTracking) {
                    return;
                }
                if(mNewLocation == null || mNewLocation == mLastLocation) {
                    gpsTimeUnityToFix -= 1;
                    mTrackServiceEvents.missingGpsSignal(gpsTimeUnityToFix);
                    if(gpsTimeUnityToFix == 0) {
                        Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
                        intent.putExtra("message", "Error in TrackService!");
                        intent.putExtra("messageDetail", "no gps signal");
                        intent.putExtra("level", 0);
                        startActivity(intent);
                        isTracking = false;
                        timer.cancel();
                    }
                    return;
                }
                else {
                    gpsTimeUnityToFix = 5;
                }



                mLastLocation = mNewLocation;

                MobileTuple tuple = new MobileTuple(UserHandler.getGroupId(getApplicationContext()), new BigDecimal(mNewLocation.getLatitude()).setScale(10, RoundingMode.HALF_UP), new BigDecimal(mNewLocation.getLongitude()).setScale(10, RoundingMode.HALF_UP), new Date());
                byte[] hash = HashHelper.getHash(tuple);
                SignHelper.sign(UserHandler.getSecretKey(getApplicationContext()), UserHandler.getPublicKey(getApplicationContext()), hash, tuple.getSignature());
                tuple.setHash(Base64.encodeToString(hash, NO_WRAP));

                if (!dbHelper.save(tuple)) {
                    isTracking = false;
                    timer.cancel();
                    Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
                    intent.putExtra("message", "Error in TrackService!");
                    intent.putExtra("messageDetail", "can't save locally!");
                    intent.putExtra("level", 0);
                    startActivity(intent);
                    return;
                }
                sendLocalTupleToProvider();
            }
        }, warmUpTime, updateInterval);
        isTracking = true;
        return true;
    }

    private synchronized void sendLocalTupleToProvider() {
        if(syncCount > 0) {
            return;
        }
        List<MobileTuple> tuples = dbHelper.getTuplesByStatus(MobileTuple.TupleStatus.LOCAL);
        syncCount = tuples.size();

        if(syncCount == 0 && !isTracking) {
            pay();
        }

        

        tuples.forEach(t -> {
            CustomRequest request = new CustomRequest(Request.Method.POST, Const.ProviderUrl + "/tuples", response -> {
                networkTimeUnityToFix = 5;
                dbHelper.setTupleStatus(t.getHash(), MobileTuple.TupleStatus.REMOTE); 
                mTrackServiceEvents.onGpsSignalReported();
                syncCount -= 1;
                if(syncCount == 0 && !isTracking) { // bezahlen
                    pay();
                }
            }, this::onTupleSendError, null, t);
            queue.add(request);
        });
    }

    private void pay() {
        hashes = new ArrayList<>();
        queue.add( new CustomRequest(Request.Method.GET, Const.ProviderUrl + "/invoicePeriodes/" +  UserHandler.getGroupId(getApplicationContext()),
                this::GetPeriodesSuccessful, this::GetPeriodesError, null, null));
    }


    private void GetPeriodesError(VolleyError volleyError) {
        volleyError.printStackTrace();
        Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
        intent.putExtra("message", "Error in TrackService!");
        intent.putExtra("messageDetail", "no periodes received!");
        intent.putExtra("level", 0);
        startActivity(intent);
    }

    private void GetPeriodesSuccessful(Object response) {
        periodeSynCount = 0;
        String[] periodes = gson.fromJson(response.toString(), String[].class);
        for (int i = 0; i < periodes.length; i++) {
            if (dbHelper.hasRemoteTupleInPeriode(periodes[i], i < periodes.length - 1 ? periodes[i + 1] : null)) {
                String url = Const.ProviderUrl + "/invoiceitems/" + UserHandler.getGroupId(getApplicationContext()) + "/" + periodes[i];
                periodeSynCount++;
                CustomRequest request = new CustomRequest(Request.Method.GET, url, this::GetPeriodeSuccessful, this::GetPeriodeError, null, null);
                queue.add(request);

            }
        }
    }

    private void GetPeriodeError(VolleyError volleyError) {
        volleyError.printStackTrace();
        Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
        intent.putExtra("message", "Error in TrackService!");
        intent.putExtra("messageDetail", "no periode received!");
        intent.putExtra("level", 0);
        startActivity(intent);
    }

    private void GetPeriodeSuccessful(Object response) {
        InvoiceItems invoiceItems = gson.fromJson(response.toString(), InvoiceItems.class);

        int summe = 0;
        for (String hash : dbHelper.getTuplesHashesStatus(MobileTuple.TupleStatus.REMOTE)) {
            if (invoiceItems.getItems().containsKey(hash)) {
                summe += invoiceItems.getItems().get(hash);
                hashes.add(hash);
            }
        }

        MobileSignature signature = new MobileSignature();
        Payment payment = new Payment();
        payment.setSignatureOnTuples(invoiceItems.getSignature());
        payment.setSumme(summe);

        SignHelper.sign( UserHandler.getSecretKey(getApplicationContext()), UserHandler.getPublicKey(getApplicationContext()), HashHelper.getHash(payment), signature);
        payment.setSignature(signature);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BigInteger.class, new BigIntegerTypeAdapter());
        CustomRequest request = new CustomRequest(Request.Method.POST,  Const.ProviderUrl + "/pay/" + invoiceItems.getSessionId(), this::PaySuccessful, this::PayError, null, payment);
        queue.add(request);
    }

    private void PayError(VolleyError volleyError) {
        Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
        intent.putExtra("message", "Error in TrackService!");
        intent.putExtra("messageDetail", "error during pay");
        intent.putExtra("level", 0);
        startActivity(intent);
        isTracking = false;
    }

    private void PaySuccessful(Object o) {
        hashes.forEach(hash -> dbHelper.setTupleStatus(hash, MobileTuple.TupleStatus.PAID));
        periodeSynCount--;
        if (periodeSynCount == 0) {
           mTrackServiceEvents.onPayed();
        }
    }

    private void onTupleSendError(VolleyError volleyError) {
        Log.v("TrackService", "Error while sync tuples");
        networkTimeUnityToFix -= 1;
        mTrackServiceEvents.missingNetworkConnection(networkTimeUnityToFix);
        syncCount = 0;
        
        if(gpsTimeUnityToFix == 0) {
            Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
            intent.putExtra("message", "Error in TrackService!");
            intent.putExtra("messageDetail", "no connection to provider");
            intent.putExtra("level", 0);
            startActivity(intent);
            isTracking = false;
            timer.cancel();
        }
        volleyError.printStackTrace();
    }


    public void stop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        isTracking = false;
        timer.cancel();
        sendLocalTupleToProvider();
    }
}
