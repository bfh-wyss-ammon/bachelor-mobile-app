package mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
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
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.MainActivity;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.User;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.LoginService;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomRequest;
import requests.JoinRequest;
import responses.JoinResponse;
import settings.DefaultSettings;
import util.JoinHelper;

/**
 * Created by Pascal on 05.10.2017.
 */

public class LoginTask extends AsyncTask<User, Void, Void> {
    private MainActivity activity;
    private String token;
    private MobileSecretKey secretKey;
    private static final DefaultSettings settings = new DefaultSettings();
    private static final Gson gson = new Gson();
    private RequestQueue queue;
    private MobileGroup group;

    public LoginTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(User... users) {
        if (users.length != 1) {
            return null;
        }

        User user = users[0];
        queue = Volley.newRequestQueue(this.activity.getApplicationContext());
        CustomRequest request = new CustomRequest(Request.Method.POST, Const.AuthorityUrl + "/login", null, new LoginResponseListener(), new LoginErrorListener(), null, user) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                token = response.headers.get(Const.TokenHeader);
                return super.parseNetworkResponse(response);
            }
        };

        queue.add(request);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

    private class LoginResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            activity.runOnUiThread(() -> activity.setAuthenticationStatus(true, ""));
            group = gson.fromJson(response.toString(), MobileGroup.class);
            secretKey = new MobileSecretKey();
            activity.runOnUiThread(() -> activity.setSecretKeyCalculated(true));

            JoinHelper.init(settings, group.getPublicKey(), secretKey);
            JoinRequest joinRequest = new JoinRequest(secretKey);
            activity.runOnUiThread(() -> activity.setJoinRequest(true));
            CustomRequest request = new CustomRequest(Request.Method.POST, Const.AuthorityUrl + "/membership", null, new groupJoinResponseListener(), new GroupJoinErrorListener(), token, joinRequest);

            queue.add(request);

        }
    }
    private class groupJoinResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            JoinResponse joinResponse = gson.fromJson(((JSONObject) response).toString(), JoinResponse.class);
            activity.runOnUiThread(() -> activity.setJoinResponse(true));
            secretKey.maintainResponse(joinResponse);
            // todo persist

            StringRequest request = new StringRequest(Request.Method.PUT, Const.AuthorityUrl + "/membership", new GroupConfirmResponseListener(), new GroupConfirmErrorListener()) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put(Const.TokenHeader, token);
                    return headers;
                }
            };
            queue.add(request);
        }
    }

    private class GroupConfirmResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            activity.runOnUiThread(() -> activity.setConfirm(true, group, secretKey));
            Log.v("LoginService", "login done!");
        }
    }

    private class GroupJoinErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            activity.runOnUiThread(() -> activity.setJoinResponse(true));
            error.printStackTrace();
        }
    }

    private class LoginErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if(error instanceof  AuthFailureError) {
                activity.runOnUiThread(() -> activity.setAuthenticationStatus(false, "authentication error"));
            }
            else {
                activity.runOnUiThread(() -> activity.setAuthenticationStatus(false, "you are already logged in"));
            }
            error.printStackTrace();
        }
    }

    private class GroupConfirmErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            activity.runOnUiThread(() -> activity.setConfirm(false, null, null));
            error.printStackTrace();
        }
    }

}
