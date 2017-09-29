package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import settings.DefaultSettings;
import settings.Settings;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private EditText mUsername;
    private EditText mPassword;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mButton = (Button) findViewById(R.id.signInButton);
        this.mUsername = (EditText) findViewById(R.id.username);
        this.mPassword = (EditText) findViewById(R.id.username);

        int i = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == 0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 2);
        }

        this.mButton.setOnClickListener(v -> {
            Intent indent = new Intent(v.getContext(), LoginService.class);
            indent.putExtra("username", mUsername.getText());
            indent.putExtra("password", mPassword.getText());
            v.getContext().startService(indent);
        });
    }
}
