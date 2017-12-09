package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.User;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.LoginTask;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.NetworkCheck;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomAppCompatActivity;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Helper;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.UserHandler;

/**
 * Created by Pascal on 28.11.2017.
 */

public class LoginActivity extends CustomAppCompatActivity implements TextWatcher, View.OnClickListener, LoginTask.LoginTaskListener, NetworkCheck.NetworkCheckEvents {

    private Button mButton;
    private EditText mUsername;
    private EditText mPassword;
    private ProgressBar mProgressBar;
    private TextView mErrorText;

    private boolean hasNetworkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        this.mButton = (Button) findViewById(R.id.loginBtn);
        this.mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.mButton.setEnabled(false);

        this.mUsername = (EditText) findViewById(R.id.accountId);
        this.mPassword = (EditText) findViewById(R.id.accountPwd);
        this.mErrorText = (TextView) findViewById(R.id.errorMessage);

        this.mUsername.addTextChangedListener(this);
        this.mPassword.addTextChangedListener(this);
        this.mButton.setOnClickListener(this);

        NetworkCheck networkCheck = new NetworkCheck(this, getApplicationContext());
        networkCheck.execute();

        startAnimate();
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
        if(required != this.mButton.isEnabled()) {
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


    private void showError(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        this.mPassword.setEnabled(true);
        this.mUsername.setEnabled(true);
        this.mProgressBar.setVisibility(View.INVISIBLE);
        this.mButton.setVisibility(View.INVISIBLE);
        this.mErrorText.setText(text + " Call Support!");
        this.mErrorText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        LoginTask loginTask = new LoginTask(this, getApplicationContext());
        loginTask.execute(new User(this.mUsername.getText().toString(), Helper.getHash(this.mPassword.getText().toString())));
        startAnimate();
    }

    @Override
    public void onLoginError() {
        showError("Error while login!");
    }

    @Override
    public void onAuthenticationError() {
        Toast.makeText(getApplicationContext(), "wrong username or password", Toast.LENGTH_SHORT).show();
        stopAnimate();
    }

    @Override
    public void onAlreadyLoggedInError() {
        showError("you are already loggedin on on an other device or you have an old session!");
    }

    @Override
    public void onLoginSuccessfully() {
        stopAnimate();
        Intent intent = new Intent(this, CheckInActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNetworkCheckSuccessfully() {
        hasNetworkConnection = true;
        stopAnimate();

    }

    @Override
    public void onNetworkCheckError(Exception error) {
        showError("No connection to backend.");
    }
}
