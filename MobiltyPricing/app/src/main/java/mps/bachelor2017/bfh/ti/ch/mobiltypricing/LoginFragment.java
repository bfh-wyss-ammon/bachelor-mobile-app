package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.User;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.LoginTask;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Helper;

/**
 * Created by Pascal on 13.10.2017.
 */

public class LoginFragment extends Fragment implements TextWatcher, View.OnClickListener {

    interface LoginListener {
        void onLogin(String id, String pwd);
    }

    private Button mButton;
    private EditText mUsername;
    private EditText mPassword;
    private ProgressBar mProgressBar;

    private LoginListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        this.mButton = (Button) view.findViewById(R.id.loginBtn);
        this.mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        this.mButton.setEnabled(false);

        this.mUsername = (EditText) view.findViewById(R.id.accountId);
        this.mPassword = (EditText) view.findViewById(R.id.accountPwd);

        this.mUsername.addTextChangedListener(this);
        this.mPassword.addTextChangedListener(this);

        this.mButton.setOnClickListener(this);

        return view;
    }

    public void setOnLoginListener(LoginListener loginListener) {
        mListener = loginListener;
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

    public void animate() {
        this.mPassword.setEnabled(false);
        this.mUsername.setEnabled(false);
        this.mButton.setVisibility(View.INVISIBLE);
        this.mProgressBar.setVisibility(View.VISIBLE);
    }

    public void stopAnimate() {
        this.mPassword.setEnabled(true);
        this.mUsername.setEnabled(true);
        this.mProgressBar.setVisibility(View.INVISIBLE);
        this.mButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if(mListener != null) {
            mListener.onLogin(this.mUsername.getText().toString(), this.mPassword.getText().toString());
        }
    }
}
