package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageButton;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.ConnectionEvents;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomAppCompatActivity;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.UserHandler;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;
import static junit.framework.Assert.fail;

/**
 * Created by Pascal on 06.10.2017.
 */

public class MainActivity extends CustomAppCompatActivity implements ConnectionEvents {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        this.mNetworkConnectionStatus = (ImageButton) findViewById(R.id.NetworkStatusButton);
        this.mGpsStatus = (ImageButton) findViewById(R.id.GpsStatusButton);
        this.gpsAnimatedVectorDrawable = (AnimatedVectorDrawable) getDrawable(R.drawable.ic_position_animated);
        this.networkAnimatedVectorDrawable = (AnimatedVectorDrawable) getDrawable(R.drawable.ic_network_animated);

        renderUserInterface();
    }

    @Override
    protected void onServiceConnected() {
        mTrackService.registerConnectionEvents(this);
        mTrackService.checkPermissions();
        runOnUiThread(this::renderUserInterface);
    }

    private void renderUserInterface() {
        if(mTrackService == null || mTrackService.getGpsPermission() == -2) { // wait for service...
            this.mGpsStatus.setImageDrawable(gpsAnimatedVectorDrawable);
            this.gpsAnimatedVectorDrawable.start();
        }
        else {
            if(mTrackService.getGpsPermission() == PERMISSION_DENIED) {
                this.mGpsStatus.setImageResource(R.drawable.ic_positional_map_fail);
            }
            else {
                this.mGpsStatus.setImageResource(R.drawable.ic_positional_map_oke);
            }
        }

        if(mTrackService == null || mTrackService.getNetworkPermission() == -2) { // wait for service...
            this.mNetworkConnectionStatus.setImageDrawable(networkAnimatedVectorDrawable);
            this.networkAnimatedVectorDrawable.start();
        }
        else {
            if(mTrackService.getNetworkPermission() == PERMISSION_DENIED) {
                this.mNetworkConnectionStatus.setImageResource(R.drawable.ic_network_fail);
            }
            else {
                this.mNetworkConnectionStatus.setImageResource(R.drawable.ic_network_oke);
            }
        }
    }

    @Override
    protected void onServiceDisconnected() {
        // todo handle
    }

    @Override
    public void onGpsPermissionMissing() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onNetworkPermissionMissing() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 2);
    }

    @Override
    public void onHasGpsAndNetworkPermission() {
        Intent intent = new Intent(this, UserHandler.exist(getApplicationContext()) ? CheckInActivity.class : LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if(Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
               mTrackService.setGpsPermission(grantResults[i]);
            }
            else if(Manifest.permission.INTERNET.equals(permissions[i])) {
                mTrackService.setNetworkPermission(grantResults[i]);
            }
        }
        runOnUiThread(this::renderUserInterface);
    }
}
