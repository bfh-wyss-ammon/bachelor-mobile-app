package mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.User;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomObjectRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomRequest;
import requests.JoinRequest;
import responses.JoinResponse;
import settings.DefaultSettings;
import util.JoinHelper;

/**
 * Created by Pascal on 05.10.2017.
 */

public class LoginTask extends AsyncTask<User, Void, Void> {

    public enum LoginStatus {
        LoginSuccessfull,
        AuthenticatenSuccessfull,
        SecretKeyCalculated,
        SendJoinRequest,
        ReceivedJoinResponse,
        AlreadyLoggedIn,
        AuthenticatenError,
        NetworkError,
        GroupJoinError,
        GroupConfirmError,
        OtherError
    }

    public interface OnStatusChangedListener {
        void onStatusChanged(LoginStatus status);
    }

    private OnStatusChangedListener mListener;
    private String token;
    private MobileSecretKey secretKey;
    private static final DefaultSettings settings = new DefaultSettings();
    private static final Gson gson = new Gson();
    private RequestQueue queue;
    private MobileGroup group;
    private Context mContext;

    public LoginTask(OnStatusChangedListener listener, Context context) {

        this.mListener = listener;
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(User... users) {
        if (users.length != 1) {
            return null;
        }

        User user = users[0];
        queue = Volley.newRequestQueue(mContext);
        CustomObjectRequest request = new CustomObjectRequest(Request.Method.POST, Const.AuthorityUrl + "/login", null, new LoginResponseListener(), new LoginErrorListener(), null, user) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                token = response.headers.get(Const.TokenHeader);
                return super.parseNetworkResponse(response);
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                100,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

    private class LoginResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            mListener.onStatusChanged(LoginStatus.AuthenticatenSuccessfull);
            group = gson.fromJson(response.toString(), MobileGroup.class);
            secretKey = new MobileSecretKey();
            mListener.onStatusChanged(LoginStatus.SecretKeyCalculated);

            JoinHelper.init(settings, group.getPublicKey(), secretKey);
            JoinRequest joinRequest = new JoinRequest(secretKey);
            mListener.onStatusChanged(LoginStatus.SendJoinRequest);
            CustomObjectRequest request = new CustomObjectRequest(Request.Method.POST, Const.AuthorityUrl + "/membership", null, new groupJoinResponseListener(), new GroupJoinErrorListener(), token, joinRequest);
            queue.add(request);

        }
    }
    private class groupJoinResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            JoinResponse joinResponse = gson.fromJson(response.toString(), JoinResponse.class);
            mListener.onStatusChanged(LoginStatus.ReceivedJoinResponse);
            secretKey.maintainResponse(joinResponse);
            CustomRequest request = new CustomRequest(Request.Method.PUT, Const.AuthorityUrl + "/membership", new GroupConfirmResponseListener(), new GroupConfirmErrorListener(), token, null);
            queue.add(request);
        }
    }

    private class GroupConfirmResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            SharedPreferences settings = mContext.getSharedPreferences(Const.PreferenceKey, 0);
            group.save(settings);
            secretKey.save(settings);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("status", true);
            editor.apply();

            mListener.onStatusChanged(LoginStatus.LoginSuccessfull);
            Log.v("LoginService", "login done!");
        }
    }

    private class GroupJoinErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            mListener.onStatusChanged(LoginStatus.GroupJoinError);
            error.printStackTrace();
        }
    }

    private class LoginErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if(error instanceof  AuthFailureError) {
                mListener.onStatusChanged(LoginStatus.AuthenticatenError);
            }
            else if(error instanceof NetworkError) {
                mListener.onStatusChanged(LoginStatus.NetworkError);
            }
            else {
                mListener.onStatusChanged(LoginStatus.AlreadyLoggedIn);
            }
            error.printStackTrace();
        }
    }

    private class GroupConfirmErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            mListener.onStatusChanged(LoginStatus.GroupConfirmError);
            error.printStackTrace();
        }
    }

}
