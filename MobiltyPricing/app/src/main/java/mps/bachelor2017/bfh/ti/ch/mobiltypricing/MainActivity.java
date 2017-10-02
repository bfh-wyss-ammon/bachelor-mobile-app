package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.Console;
import java.util.ArrayList;

import settings.DefaultSettings;
import settings.Settings;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private EditText mUsername;
    private EditText mPassword;
    private Gson gson = new Gson();
    private VelocityTracker mVelocityTracker = null;
    private GestureLibrary gestureLibrary = null;
    private GestureOverlayView gestureOverlayView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mButton = (Button) findViewById(R.id.signInButton);
        this.mUsername = (EditText) findViewById(R.id.username);
        this.mPassword = (EditText) findViewById(R.id.password);

        int i = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == 0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 2);
        }

        this.mButton.setVisibility(View.INVISIBLE);

        mPassword.addTextChangedListener(new MyTextWatcher());
        mUsername.addTextChangedListener(new MyTextWatcher());

        this.mButton.setOnClickListener(v -> {
            Intent indent = new Intent(v.getContext(), LoginService.class);
            indent.putExtra("username", mUsername.getText().toString());
            indent.putExtra("password", mPassword.getText().toString());
            v.getContext().startService(indent);
        });

        gestureOverlayView = (GestureOverlayView)findViewById(R.id.gestures);
        gestureOverlayView.setGestureVisible(false);
        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        gestureLibrary.load();

        gestureOverlayView.addOnGesturePerformedListener(new MyGesutrePerformedListener());
    }

    private class MyGesutrePerformedListener implements GestureOverlayView.OnGesturePerformedListener {

        @Override
        public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
            ArrayList<Prediction> prediction = gestureLibrary.recognize(gesture);
            if(prediction.size() > 0){
               Log.v("main", "detecd");
            }
        }
    }

    private class  MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setSignInButtonVisible();
        }
    }

    private void setSignInButtonVisible() {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        if(username != null && password != null && username.length() > 3 && password.length() > 3) {
            this.mButton.setVisibility(View.VISIBLE);
        }
        else {
            this.mButton.setVisibility(View.INVISIBLE);
        }

    }
}
