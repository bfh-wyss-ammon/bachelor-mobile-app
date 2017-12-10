package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.TrackService;

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

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    protected void onServiceDisconnected() {

    }


    @Override
    public void onStop() {
        unbindService(this);
        super.onStop();
    }
}
