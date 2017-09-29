package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Base64;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;


import keys.PublicKey;
import keys.SecretKey;
import requests.JoinRequest;
import responses.JoinResponse;
import settings.DefaultSettings;
import settings.Settings;
import util.JoinHelper;

/**
 * Created by Pascal on 29.09.2017.
 */

public class LoginService extends Service {
    private String authorityUrl = "http://192.168.1.100:4567";
    private Gson gson;
    private RequestQueue queue;
    private User user;
    private MobileSecretKey secretKey;
    private DefaultSettings settings;
    private String token;
    private final String TokenHeader = "x-custom-token";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BigInteger.class, new BigIntegerGsonTypeAdapter());
        gson = builder.create();
        user = new User(intent.getStringExtra("username"), getHash(intent.getStringExtra("password")));
        queue = Volley.newRequestQueue(this);
        queue.add(new JsonObjectRequest(Request.Method.GET, authorityUrl + "/settings", null, new SettingsResponseListener(), new ErrorListener()));

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class SettingsResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            settings = gson.fromJson(((JSONObject) response).toString(), DefaultSettings.class);

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
        }

    }

    private class LoginResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Group group = gson.fromJson(((JSONObject) response).toString(), Group.class);
            secretKey = new MobileSecretKey();
            JoinHelper.init(settings, group.publicKey, secretKey);
            JoinRequest joinRequest = new JoinRequest(secretKey);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, authorityUrl + "/group", null, new groupJoinResponseListener(), new ErrorListener()) {
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

            StringRequest request = new StringRequest(Request.Method.PUT, authorityUrl + "/group", new groupConfirmResponseListener(), new ErrorListener()) {
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

    public static String getHash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            String blubr = Base64.encodeToString(hash, Base64.NO_WRAP);
            Log.v("hash", blubr);
            return blubr;
        } catch (Exception ex) {
            System.out.print("Credential.GetHash: Error" + ex.getMessage());
        }
        return "";
    }

    private class Group {
        private Integer groupId;
        private MobilePublicKey publicKey;

        public Integer getGroupId() {
            return groupId;
        }

        public void setGroupId(Integer groupId) {
            this.groupId = groupId;
        }

        public MobilePublicKey getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(MobilePublicKey publicKey) {
            this.publicKey = publicKey;
        }
    }

    private class MobileSecretKey implements SecretKey {
        private BigInteger x;
        private BigInteger w;
        private BigInteger y;
        private BigInteger e;
        private BigInteger r;
        private BigInteger bigE;
        private BigInteger bigY;
        private BigInteger commitment;

        @Override
        public BigInteger getX() {
            return x;
        }

        @Override
        public void setX(BigInteger x) {
            this.x = x;
        }

        @Override
        public BigInteger getW() {
            return w;
        }

        @Override
        public void setW(BigInteger w) {
            this.w = w;
        }

        @Override
        public BigInteger getY() {
            return y;
        }

        @Override
        public void setY(BigInteger y) {
            this.y = y;
        }

        @Override
        public BigInteger getE() {
            return e;
        }

        @Override
        public void setE(BigInteger e) {
            this.e = e;
        }

        @Override
        public BigInteger getR() {
            return r;
        }

        @Override
        public void setR(BigInteger r) {
            this.r = r;
        }

        @Override
        public BigInteger getBigE() {
            return bigE;
        }

        @Override
        public void setBigE(BigInteger bigE) {
            this.bigE = bigE;
        }

        @Override
        public BigInteger getBigY() {
            return bigY;
        }

        @Override
        public void setBigY(BigInteger bigY) {
            this.bigY = bigY;
        }

        @Override
        public BigInteger getCommitment() {
            return commitment;
        }

        @Override
        public void setCommitment(BigInteger commitment) {
            this.commitment = commitment;
        }

        @Override
        public void maintainResponse(JoinResponse joinResponse) {
            this.bigE = joinResponse.getEi();
            this.r = joinResponse.getRi().add(getR());
            this.e = joinResponse.getE();
            this.y = joinResponse.getYi();
            this.w = joinResponse.getWi();
        }
    }

    private class MobilePublicKey implements PublicKey {
        private BigInteger n;
        private BigInteger a;
        private BigInteger g;
        private BigInteger h;
        private BigInteger w;
        private BigInteger bigQ;
        private BigInteger bigP;
        private BigInteger bigF;
        private BigInteger bigG;
        private BigInteger bigH;

        @Override
        public BigInteger getN() {
            return n;
        }

        @Override
        public void setN(BigInteger n) {
            this.n = n;
        }

        @Override
        public BigInteger getA() {
            return a;
        }

        @Override
        public void setA(BigInteger a) {
            this.a = a;
        }

        @Override
        public BigInteger getG() {
            return g;
        }

        @Override
        public void setG(BigInteger g) {
            this.g = g;
        }

        @Override
        public BigInteger getH() {
            return h;
        }

        @Override
        public void setH(BigInteger h) {
            this.h = h;
        }

        @Override
        public BigInteger getW() {
            return w;
        }

        @Override
        public void setW(BigInteger w) {
            this.w = w;
        }

        @Override
        public BigInteger getBigQ() {
            return bigQ;
        }

        @Override
        public void setBigQ(BigInteger bigQ) {
            this.bigQ = bigQ;
        }

        @Override
        public BigInteger getBigP() {
            return bigP;
        }

        @Override
        public void setBigP(BigInteger bigP) {
            this.bigP = bigP;
        }

        @Override
        public BigInteger getBigF() {
            return bigF;
        }

        @Override
        public void setBigF(BigInteger bigF) {
            this.bigF = bigF;
        }

        @Override
        public BigInteger getBigG() {
            return bigG;
        }

        @Override
        public void setBigG(BigInteger bigG) {
            this.bigG = bigG;
        }

        @Override
        public BigInteger getBigH() {
            return bigH;
        }

        @Override
        public void setBigH(BigInteger bigH) {
            this.bigH = bigH;
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
