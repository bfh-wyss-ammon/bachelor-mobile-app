package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.content.Intent;
import android.os.Bundle;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.animations.DriveAnimation;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.animations.StartAnimation;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.DriveEvents;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomAppCompatActivity;

/**
 * Created by Pascal on 01.12.2017.
 */

public class DriveActivity extends CustomAppCompatActivity implements StartAnimation.StartAnimationEvents, DriveEvents {

    private DriveAnimation driveAnimation;
    private StartAnimation mStartAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drive_activity);
        driveAnimation =(DriveAnimation)findViewById(R.id.driveAnimation);
        driveAnimation.simulate();

        mStartAnimation = (StartAnimation) findViewById(R.id.StopAnimation);
    }

    @Override
    protected void onServiceConnected() {
        mStartAnimation.setStartAnimationEvents(this);
        mTrackService.setCallbacks(this);
    }

    @Override
    public void onSubmit() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onGpsSignalError() {

    }

    @Override
    public void onGpsSignal() {
        driveAnimation.simulate();
    }

    @Override
    public void onDrive() {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onConnectionError() {

    }

    @Override
    public void onSend() {
    }

    @Override
    public void onFinished() {

    }

    @Override
    public void onConnected() {

    }
}
