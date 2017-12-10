package mps.bachelor2017.bfh.ti.ch.mobiltypricing.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.User;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomObjectRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.UserHandler;
import requests.JoinRequest;
import responses.JoinResponse;
import settings.DefaultSettings;
import util.JoinHelper;

/**
 * Created by Pascal on 09.12.2017.
 */

public class LoginService extends Service {

    public class LoginBinder extends Binder {
        public LoginService getService() {
            return LoginService.this;
        }
    }

    private final IBinder mBinder = new LoginBinder();
    private static final DefaultSettings settings = new DefaultSettings();
    private static final Gson gson = new Gson();

    private LoginEvents mLoginEvents;
    private String token;
    private RequestQueue queue;
    private MobileGroup group;
    private MobileSecretKey secretKey;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        queue = Volley.newRequestQueue(getApplicationContext());
    }

    public void registerCallbacks(LoginEvents loginEvents) {
        mLoginEvents = loginEvents;
    }

    public boolean startLogin(User user) {
        if (mLoginEvents == null) {
            Log.v("LoginSevice", "No callbacks registered!");
            return false;
        }
        if (user.getPassword() == null || user.getPassword().length() < 4 ||
                user.getId() == null || user.getId().length() < 4) {
            Log.v("LoginSevice", "wrong login data pwd or id to short!");
            return false;
        }

        CustomObjectRequest request = new CustomObjectRequest(Request.Method.POST, Const.AuthorityApiUrl + "/login", null, this::onPostLoginSuccessful, this::onLoginError, null, user) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                token = response.headers.get(Const.TokenHeader);
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(request);
        return true;
    }

    private void onPostLoginSuccessful(JSONObject response) {
        group = gson.fromJson(response.toString(), MobileGroup.class);
        secretKey = new MobileSecretKey();
        JoinHelper.init(settings, group.getPublicKey(), secretKey);
        JoinRequest joinRequest = new JoinRequest(secretKey);
        CustomObjectRequest request1 = new CustomObjectRequest(Request.Method.POST, Const.AuthorityApiUrl + "/memberships", null, this::onPostMembershipSuccessful, this::onPostMembershipError, token, joinRequest);
        queue.add(request1);
    }

    private void onPostMembershipSuccessful(JSONObject response) {
        JoinResponse joinResponse = gson.fromJson(response.toString(), JoinResponse.class);
        secretKey.maintainResponse(joinResponse);
        CustomRequest request2 = new CustomRequest(Request.Method.PUT, Const.AuthorityApiUrl + "/memberships", this::onPutMembershipSuccessful, this::onPutMembershipError, token, null);
        queue.add(request2);
    }

    private void onPutMembershipSuccessful(Object o) {
        UserHandler.save(getApplicationContext(), secretKey, group);
        mLoginEvents.onSuccessfully();
    }

    private void onLoginError(VolleyError volleyError) {
        Log.v("LoginSevice", "error in Post [/login");
        volleyError.printStackTrace();
        mLoginEvents.onError(volleyError);
    }

    private void onPostMembershipError(VolleyError volleyError) {
        Log.v("LoginSevice", "error in Post [/memberships");
        volleyError.printStackTrace();
        mLoginEvents.onError(volleyError);
    }

    private void onPutMembershipError(VolleyError volleyError) {
        Log.v("LoginSevice", "error in Put [/memberships");
        volleyError.printStackTrace();
        mLoginEvents.onError(volleyError);
    }

}
