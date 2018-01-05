/**
 * Copyright 2018 Pascal Ammon, Gabriel Wyss
 * <p>
 * Implementation eines anonymen Mobility Pricing Systems auf Basis eines Gruppensignaturschemas
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.animations.DriveAnimation;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.animations.SlideAnimation;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.TrackService;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.TrackServiceEvents;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.DatabaseHelper;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.UserHandler;


public class MainActivity extends AppCompatActivity implements ServiceConnection, SlideAnimation.SlideAnimationEvents, TrackServiceEvents {

    private ProgressBar mMainActivityProgressBar;
    private TextView mMainTextUser;
    private TextView mMainTextGps;
    private TextView mMainTextConnection;
    private DriveAnimation mDriveAnimation;
    private SlideAnimation mSlideAnimation;

    private boolean hasUser;
    private boolean hasGps;
    private boolean hasConnection;
    private TrackService mTrackService;
    private int status = 0; // status == 3: hasUser = hasGps = hasConnection = true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mMainActivityProgressBar = (ProgressBar) findViewById(R.id.MainActivityProgressBar);
        mMainTextUser = (TextView) findViewById(R.id.MainActivityTextUser);
        mMainTextGps = (TextView) findViewById(R.id.MainActivityTextGps);
        mMainTextConnection = (TextView) findViewById(R.id.MainActivityTextConnection);
        mDriveAnimation = (DriveAnimation) findViewById(R.id.MainActivityDriveAnimation);
        mSlideAnimation = (SlideAnimation) findViewById(R.id.MainActivitySlideAnimation);

        Intent intent = new Intent(this, TrackService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.mTrackService = ((TrackService.TrackBinder) service).getService();
        this.mTrackService.setCallbacks(this);
        showCheckIn();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Intent intent = new Intent(this, ErrorActivity.class);
        intent.putExtra("message", getString(R.string.ErroInMainActivity));
        intent.putExtra("level", 0);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mTrackService != null && mTrackService.isTracking()) {
            return;
        }

        hasUser = false;
        hasConnection = false;
        hasGps = false;

        if (!UserHandler.exist(getApplicationContext())) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            hasUser = true;
            handleStatusUpdate();
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            hasGps = true;
            handleStatusUpdate();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        queue.add(new CustomRequest(Request.Method.GET, Const.ProviderUrl + "/status", response -> {
            runOnUiThread(() -> {
               hasConnection = true;
               handleStatusUpdate();
            });
        }, error -> {
            Intent intent = new Intent(this, ErrorActivity.class);
            intent.putExtra("message", getString(R.string.ErroInMainActivity));
            intent.putExtra("messageDetail", getString(R.string.NoConnectionToProvider));
            intent.putExtra("level", 0);
            startActivity(intent);
        }, null, null));
    }

    private void handleStatusUpdate() {
        runOnUiThread(() -> {
            status = 0;
            if (hasUser) {
                status += 1;
                mMainTextUser.setTextColor(getColor(R.color.text));
            }
            if (hasConnection) {
                status += 1;
                mMainTextConnection.setTextColor(getColor(R.color.text));
            }
            if (hasGps) {
                status += 1;
                mMainTextGps.setTextColor(getColor(R.color.text));
            }
            mMainActivityProgressBar.setProgress(status);
            if(status == 3) {
                showCheckIn();
            }
            else if(mTrackService == null || !mTrackService.isTracking()) {
                mDriveAnimation.setVisibility(View.INVISIBLE);
                mSlideAnimation.setVisibility(View.INVISIBLE);
            }
        });
    }

    private synchronized void showCheckIn() {
        mSlideAnimation.setVisibility(View.VISIBLE);
        mSlideAnimation.registerCallbacks(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(this, ErrorActivity.class);
                        intent.putExtra("message", getString(R.string.MissingGPSPermissions));
                        intent.putExtra("messageDetail", getString(R.string.MissingGpsPermissionDesc));
                        intent.putExtra("level", 0);
                        startActivity(intent);
                    }
                    else {
                        hasGps = true;
                        handleStatusUpdate();
                    }
                }
            }
        }
    }

    @Override
    public void onRightSubmit() {
        mDriveAnimation.setVisibility(View.VISIBLE);
        mTrackService.start();
    }

    @Override
    public void onLeftSubmit() {
        mDriveAnimation.setVisibility(View.INVISIBLE);
        mTrackService.stop();
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
        getApplication().deleteDatabase(DatabaseHelper.DATABASE_NAME);
        startActivity(new Intent(this, LoginActivity.class));
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

    @Override
    public void onGpsSignalReported() {
        if (mTrackService.isTracking()) {
            mDriveAnimation.simulate();
            mMainTextGps.setText(R.string.GpsPermissionOrSignal);
            mMainTextConnection.setText(R.string.Connection);
            mMainTextConnection.setTextColor(getColor(R.color.text));
            mMainTextGps.setTextColor(getColor(R.color.text));
        }
    }

    @Override
    public void onPayed(int sum) {
        runOnUiThread(() -> {
            handleStatusUpdate();
            mSlideAnimation.setVisibility(View.VISIBLE);
            Toast.makeText(this.getApplicationContext(), getString(R.string.PaymentReceived) + " " + sum, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void missingGpsSignal(int timeUnityToFix) {
        runOnUiThread(() -> {
            mMainTextGps.setTextColor(getColor(R.color.colorAccent3));
            mMainTextGps.setText(getString(R.string.GpsPermissionErrorCount) + timeUnityToFix);
        });
    }

    @Override
    public void missingNetworkConnection(int timeUnityToFix) {
        runOnUiThread(() -> {
            mMainTextConnection.setTextColor(getColor(R.color.colorAccent3));
            mMainTextConnection.setText(getString(R.string.ConnectionErrorCount) + timeUnityToFix );
        });
    }
    //endregion
}
