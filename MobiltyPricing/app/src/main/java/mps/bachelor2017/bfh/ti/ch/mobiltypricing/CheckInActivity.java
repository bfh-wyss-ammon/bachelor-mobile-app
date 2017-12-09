package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.animations.StartAnimation;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomAppCompatActivity;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.UserHandler;

/**
 * Created by Pascal on 01.12.2017.
 */

public class CheckInActivity extends CustomAppCompatActivity implements StartAnimation.StartAnimationEvents {
    private StartAnimation mStartAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_activity);
        mStartAnimation = (StartAnimation) findViewById(R.id.StartAnimation);
    }

    @Override
    public void onSubmit() {
        mTrackService.start();
        Intent intent = new Intent(this, DriveActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onServiceConnected() {
        mStartAnimation.setStartAnimationEvents(this);
    }

    //region menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    private void logout() {
        UserHandler.clear(getApplicationContext());
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    //endregion
}
