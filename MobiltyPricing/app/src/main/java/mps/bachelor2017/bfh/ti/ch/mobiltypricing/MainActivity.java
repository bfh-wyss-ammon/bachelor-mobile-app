package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobilePublicKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.User;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.LoginTask;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Helper;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private EditText mUsername;
    private EditText mPassword;
    private LoginTask loginTask;
    private TextView mAuthenticationStatus;
    private TextView mSecretKeyCalculated;
    private TextView mJoinResponse;
    private TextView mJoinReuqest;
    private TextView mConfirm;
    private Boolean mSuccesful = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(Const.PreferenceKey, 0);
        if(settings.getBoolean("status", false)) {
            showKeyDetail();
            return;
        }
        setContentView(R.layout.activity_main);


        this.mButton = (Button) findViewById(R.id.signInButton);
        this.mUsername = (EditText) findViewById(R.id.username);
        this.mPassword = (EditText) findViewById(R.id.password);
        this.mAuthenticationStatus = (TextView) findViewById(R.id.authenticationStatus);
        this.mSecretKeyCalculated = (TextView) findViewById(R.id.secretKeyCalculated);
        this.mJoinResponse = (TextView) findViewById(R.id.joinResponse);
        this.mJoinReuqest = (TextView) findViewById(R.id.joinRequest);
        this.mConfirm = (TextView) findViewById(R.id.confirmed);



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == 0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 2);
        }

        this.mButton.setOnClickListener(v -> {
            if(mSuccesful) {
                showKeyDetail();
            }
            else {
                this.mButton.setVisibility(View.INVISIBLE);
                loginTask = new LoginTask(this);
                loginTask.execute(new User(this.mUsername.getText().toString(), Helper.getHash(this.mPassword.getText().toString())));
            }
        });
    }

    private void showKeyDetail() {
        Intent i = new Intent(this, ActivityInfo.class);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loginTask != null) {
           loginTask.cancel(true);
        }
    }

    public void setAuthenticationStatus(boolean status, String failMessage) {
        if(status) {
            mAuthenticationStatus.setText("✓ authentication done");
            mAuthenticationStatus.setTextColor(Color.GREEN);
        }
        else {
            this.mButton.setVisibility(View.VISIBLE);
            mAuthenticationStatus.setText("X  " + failMessage);
            mAuthenticationStatus.setTextColor(Color.RED);
        }
    }
    public void setSecretKeyCalculated(boolean status) {
        if(status) {
            mSecretKeyCalculated.setText("✓ secretKey done");
            mSecretKeyCalculated.setTextColor(Color.GREEN);
        }
        else {
            this.mButton.setVisibility(View.VISIBLE);
            mSecretKeyCalculated.setText("X secretKey fail");
            mSecretKeyCalculated.setTextColor(Color.RED);
        }
    }
    public void setJoinResponse(boolean status) {
        if(status) {
            mJoinResponse.setText("✓ response done");
            mJoinResponse.setTextColor(Color.GREEN);
        }
        else {
            this.mButton.setVisibility(View.VISIBLE);
            mJoinResponse.setText("X response fail");
            mJoinResponse.setTextColor(Color.RED);
        }
    }
    public void setJoinRequest(boolean status) {
        if(status) {
            mJoinReuqest.setText("✓ reuqest done");
            mJoinReuqest.setTextColor(Color.GREEN);
        }
        else {
            this.mButton.setVisibility(View.VISIBLE);
            mJoinReuqest.setText("X reuqest fail");
            mJoinReuqest.setTextColor(Color.RED);
        }
    }

    public void setConfirm(boolean status, MobileGroup group, MobileSecretKey secretKey) {
        if(status) {
            SharedPreferences settings = getSharedPreferences(Const.PreferenceKey, 0);
            group.save(settings);
            secretKey.save(settings);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("status", true);
            editor.commit();

            mConfirm.setText("✓ confirmed");
            mConfirm.setTextColor(Color.GREEN);
            mSuccesful = true;
            this.mButton.setText("show key");
        }
        else {
            mConfirm.setText("X not confirmed");
            mConfirm.setTextColor(Color.RED);
        }
        this.mButton.setVisibility(View.VISIBLE);
    }
}
