package mps.bachelor2017.bfh.ti.ch.mobiltypricing.services;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.BigIntegerGsonTypeAdapter;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.User;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Helper;
import requests.JoinRequest;
import responses.JoinResponse;
import settings.DefaultSettings;
import util.JoinHelper;

/**
 * Created by Pascal on 29.09.2017.
 */

public class LoginService extends Service {

    public class LoginBinder extends Binder {
        public LoginService getService() {
            return LoginService.this;
        }
    }
    private final IBinder mBinder = new LoginBinder();


    public static String authorityUrl = "http://laptop:10000";
    private Gson gson;
    private RequestQueue queue;
    private User user;
    private MobileSecretKey secretKey;
    private DefaultSettings settings;
    private String token;
    private final String TokenHeader = "x-custom-token";

    @Override
    public void onCreate() {
        boolean i = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BigInteger.class, new BigIntegerGsonTypeAdapter());

        user = new User(intent.getStringExtra("username"), Helper.getHash(intent.getStringExtra("password")));
        settings = new DefaultSettings();

        queue = Volley.newRequestQueue(this);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, authorityUrl + "/login", null, new LoginResponseListener(), new ErrorListener()) {
            @Override
            public byte[] getBody() {
                return gson.toJson(user).getBytes();
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                token = response.headers.get(TokenHeader);
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(request);

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        queue.stop();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private class LoginResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            MobileGroup group = gson.fromJson(response.toString(), MobileGroup.class);
            secretKey = new MobileSecretKey();
            JoinHelper.init(settings, group.getPublicKey(), secretKey);
            JoinRequest joinRequest = new JoinRequest(secretKey);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, authorityUrl + "/membership", null, new groupJoinResponseListener(), new ErrorListener()) {
                @Override
                public byte[] getBody() {
                    return gson.toJson(joinRequest).getBytes();
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put(TokenHeader, token);
                    return headers;
                }
            };
            queue.add(request);

        }
    }

    private class groupJoinResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            JoinResponse joinResponse = gson.fromJson(((JSONObject) response).toString(), JoinResponse.class);
            secretKey.maintainResponse(joinResponse);
            // todo persist

            StringRequest request = new StringRequest(Request.Method.PUT, authorityUrl + "/membership", new groupConfirmResponseListener(), new ErrorListener()) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put(TokenHeader, token);
                    return headers;
                }
            };
            queue.add(request);
        }
    }

    private class groupConfirmResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Log.v("LoginService", "login done!");
        }
    }


    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            // todo error loggin
            Log.v("LoginService", error.getMessage());
        }
    }
}
