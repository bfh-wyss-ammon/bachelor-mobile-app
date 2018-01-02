package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.User;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.LoginEvents;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.LoginService;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Helper;

/**
 * Created by Pascal on 28.11.2017.
 */

public class LoginActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener, ServiceConnection, LoginEvents {

    private Button mButton;
    private EditText mUsername;
    private EditText mPassword;
    private ProgressBar mProgressBar;
    private TextView mErrorText;
    private LoginService mLoginService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        bindComponents();
        bindService();
    }


    // region Setup components
    private void bindComponents() {
        this.mButton = (Button) findViewById(R.id.loginBtn);
        this.mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.mButton.setEnabled(false);

        this.mUsername = (EditText) findViewById(R.id.accountId);
        this.mPassword = (EditText) findViewById(R.id.accountPwd);
        this.mErrorText = (TextView) findViewById(R.id.errorMessage);

        this.mUsername.addTextChangedListener(this);
        this.mPassword.addTextChangedListener(this);
        this.mButton.setOnClickListener(this);
    }

    private void bindService() {
        startAnimate();
        Intent intent = new Intent(this, LoginService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }
    // endregion

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.mLoginService = ((LoginService.LoginBinder) service).getService();
        this.mLoginService.registerCallbacks(this);
        stopAnimate();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Intent intent = new Intent(this, ErrorActivity.class);
        intent.putExtra("message", getString(R.string.ErrorInLoginActivity));
        intent.putExtra("level", 0);
        startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        boolean required = this.mUsername.getText().toString().length() > 3 && this.mPassword.getText().toString().length() > 3;
        if (required != this.mButton.isEnabled()) {
            this.mButton.setEnabled(required);
        }
    }

    private void startAnimate() {
        this.mPassword.setEnabled(false);
        this.mUsername.setEnabled(false);
        this.mButton.setVisibility(View.INVISIBLE);
        this.mProgressBar.setVisibility(View.VISIBLE);
    }

    private void stopAnimate() {
        this.mPassword.setEnabled(true);
        this.mUsername.setEnabled(true);
        this.mProgressBar.setVisibility(View.INVISIBLE);
        this.mButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        User user = new User(this.mUsername.getText().toString(), Helper.getHash(this.mPassword.getText().toString()));
        if (mLoginService.startLogin(user)) {
            startAnimate();
        } else {
            Intent intent = new Intent(this, ErrorActivity.class);
            intent.putExtra("message", getString(R.string.ErrorInLoginActivity));
            intent.putExtra("level", 0);
            startActivity(intent);
        }
    }

    @Override
    public void onError(VolleyError error) {
        if(error instanceof AuthFailureError) {
            Toast.makeText(getApplicationContext(), R.string.PasswordWrong, Toast.LENGTH_SHORT).show();
            stopAnimate();
        }
        else {
            Intent intent = new Intent(this, ErrorActivity.class);
            intent.putExtra("message", getString(R.string.ErrorInLoginActivity));
            if(error instanceof ServerError) {
                intent.putExtra("messageDetail", getString(R.string.AlreadyLoggedIn));
            }
            intent.putExtra("level", 0);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSuccessfully() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
