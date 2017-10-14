package mps.bachelor2017.bfh.ti.ch.mobiltypricing.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.R;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.User;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks.LoginTask;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Helper;

/**
 * Created by Pascal on 13.10.2017.
 */

public class LoginFragment extends Fragment implements LoginTask.OnStatusChangedListener {

    public interface OnLogInSuccessfullListener {
        public void onLogInSuccessfull();
    }

    public OnLogInSuccessfullListener onLogInSuccessfullListener;

    private Button mButton;
    private EditText mUsername;
    private EditText mPassword;
    private LoginTask loginTask;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);

        this.mButton = (Button) view.findViewById(R.id.signInButton);
        this.mUsername = (EditText) view.findViewById(R.id.username);
        this.mPassword = (EditText) view.findViewById(R.id.password);


        this.mButton.setOnClickListener(v -> {
            this.mButton.setVisibility(View.INVISIBLE);
            loginTask = new LoginTask(this, getActivity().getApplicationContext());
            loginTask.execute(new User(this.mUsername.getText().toString(), Helper.getHash(this.mPassword.getText().toString())));
        });

        return view;
    }

    @Override
    public void onStatusChanged(LoginTask.LoginStatus status) {
        getActivity().runOnUiThread(() -> {
            int messageId = -1;
            switch (status) {
                case AlreadyLoggedIn:
                    messageId = R.string.message_already_logged_in;
                    break;
                case NetworkError:
                    messageId = R.string.message_network_error;
                    break;
                case GroupJoinError:
                    messageId = R.string.message_group_join_error;
                    break;
                case LoginSuccessfull:
                    messageId = R.string.message_login_successfull;
                    break;
                case GroupConfirmError:
                    messageId = R.string.message_group_confirm_error;
                    break;
                default:
                    break;
            }
            if (messageId != -1) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(messageId), Toast.LENGTH_SHORT).show();
            }
            if (status == LoginTask.LoginStatus.AlreadyLoggedIn || status == LoginTask.LoginStatus.NetworkError || status == LoginTask.LoginStatus.GroupJoinError || status == LoginTask.LoginStatus.OtherError || status == LoginTask.LoginStatus.AuthenticatenError) {
                this.mButton.setVisibility(View.VISIBLE);
            } else if (status == LoginTask.LoginStatus.LoginSuccessfull) {
                if (onLogInSuccessfullListener != null) {
                    onLogInSuccessfullListener.onLogInSuccessfull();
                }
            }
        });
    }
}
