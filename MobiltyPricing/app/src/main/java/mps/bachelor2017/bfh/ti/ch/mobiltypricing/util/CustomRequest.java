package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pascal on 05.10.2017.
 */

public class CustomRequest extends JsonObjectRequest {

    private static final Gson gson = new Gson();

    private final String token;
    private final Object bodyToSend;

    public CustomRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String token, Object bodyToSend) {
        super(method, url, jsonRequest, listener, errorListener);
        this.token = token;
        this.bodyToSend = bodyToSend;
    }

    public CustomRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String token, Object bodyToSend) {
        super(url, jsonRequest, listener, errorListener);
        this.token = token;
        this.bodyToSend = bodyToSend;
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
