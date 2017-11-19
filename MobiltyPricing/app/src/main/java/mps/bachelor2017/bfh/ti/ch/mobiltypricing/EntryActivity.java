package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.fragments.MissingPermissionFragment;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.NetworkCheck;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;
import static junit.framework.Assert.fail;

/**
 * Created by Pascal on 06.10.2017.
 */

public class EntryActivity extends AppCompatActivity implements NetworkCheck.NetworkCheckEvents {
    private ImageButton mNetworkConnectionStatus;
    private ImageButton mGpsStatus;

    private boolean hasConnectionToBackend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        this.mNetworkConnectionStatus = (ImageButton) findViewById(R.id.NetworkStatusButton);
        this.mGpsStatus = (ImageButton) findViewById(R.id.GpsStatusButton);

        this.mNetworkConnectionStatus.setOnClickListener(v -> {
            checkNetwork();
        });

        this.mGpsStatus.setOnClickListener(v -> {
            checkGPSPermission();
        });


        checkGPSPermission();
        checkNetwork();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.host, new DriveFragment());
        fragmentTransaction.commit();
    }


    private void checkGPSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) getDrawable(R.drawable.ic_position_animated);
            this.mGpsStatus.setImageDrawable(avd);
            avd.start();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            setGpsSuccessfully();
        }
    }

    private void checkNetwork() {
        AnimatedVectorDrawable avd = (AnimatedVectorDrawable) getDrawable(R.drawable.ic_network_animated);
        this.mNetworkConnectionStatus.setImageDrawable(avd);
        avd.start();

        NetworkCheck networkCheck = new NetworkCheck(this, getApplicationContext());
        networkCheck.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if(grantResults[i] == PERMISSION_DENIED) {
                if(Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                    setGpsError();
                }
                else  if(Manifest.permission.INTERNET.equals(permissions[i])) {
                    setNetworkError();
                }
            }
            else {
                if(Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                    setGpsSuccessfully();
                }
            }
        }
    }

    private void setNetworkSuccessfully() {
        runOnUiThread(() -> {
            this.mNetworkConnectionStatus.setImageResource(R.drawable.ic_network_oke);
        });
    }

    private void setGpsSuccessfully() {
        runOnUiThread(() -> {
            this.mGpsStatus.setImageResource(R.drawable.ic_positional_map_oke);
        });
    }

    private void setNetworkError() {
        runOnUiThread(() -> {
            this.mNetworkConnectionStatus.setImageResource(R.drawable.ic_network_fail);
            Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setGpsError() {
        runOnUiThread(() -> {
            this.mGpsStatus.setImageResource(R.drawable.ic_positional_map_fail);
            Toast.makeText(getApplicationContext(), "GPS Signal Error!", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void onSuccessfully() {
        setNetworkSuccessfully();
    }

    @Override
    public void onError(Exception error) {
        runOnUiThread(() -> {
            setNetworkError();
        });
    }
}
