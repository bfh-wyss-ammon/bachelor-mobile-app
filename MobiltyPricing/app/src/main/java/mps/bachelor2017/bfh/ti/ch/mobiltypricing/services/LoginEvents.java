package mps.bachelor2017.bfh.ti.ch.mobiltypricing.services;

import com.android.volley.VolleyError;

/**
 * Created by Pascal on 09.12.2017.
 */

public interface LoginEvents {
    void onError(VolleyError error);
    void onSuccessfully();
}
