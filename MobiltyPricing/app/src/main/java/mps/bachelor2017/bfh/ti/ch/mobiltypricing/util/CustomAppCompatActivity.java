package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.TrackService;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.DriveEvents;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.NetworkCheck;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

/**
 * Created by Pascal on 28.11.2017.
 */

public class CustomAppCompatActivity extends AppCompatActivity implements ServiceConnection {
    protected TrackService mTrackService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, TrackService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void onServiceConnected() {

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.mTrackService = ((TrackService.TrackBinder) service).getService();
        onServiceConnected();
    }

    protected void onServiceDisconnected() {

    }
    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    @Override
    public void onStop() {
        unbindService(this);
        super.onStop();
    }
}
