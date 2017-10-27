package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import gson.BigIntegerTypeAdapter;

/**
 * Created by Pascal on 05.10.2017.
 */

public class CustomObjectRequest extends JsonObjectRequest {

    private static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BigInteger.class, new BigIntegerTypeAdapter());
        gson = builder.create();
    }

    private final String token;
    private final Object bodyToSend;

    public CustomObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Object bodyToSend) {
        super(method, url, jsonRequest, listener, errorListener);
        this.token = null;
        this.bodyToSend = bodyToSend;
        //this.setRetryPolicy(new DefaultRetryPolicy(8000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public CustomObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String token, Object bodyToSend) {
        super(method, url, jsonRequest, listener, errorListener);
        this.token = token;
        this.bodyToSend = bodyToSend;
        this.setRetryPolicy(new DefaultRetryPolicy(8000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public CustomObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String token, Object bodyToSend) {
        super(url, jsonRequest, listener, errorListener);
        this.token = token;
        this.bodyToSend = bodyToSend;
        //this.setRetryPolicy(new DefaultRetryPolicy(8000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public byte[] getBody() {
        if (bodyToSend != null) {
            return gson.toJson(bodyToSend).getBytes();
        }
        return null;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        if (token != null) {
            headers.put(Const.TokenHeader, token);
        }
        return headers;
    }
}
