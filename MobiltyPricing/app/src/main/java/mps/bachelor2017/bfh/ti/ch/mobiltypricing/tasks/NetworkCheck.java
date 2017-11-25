package mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomRequest;

/**
 * This class can be use to check the availablity from the the backend api's
 * Created by Pascal on 18.11.2017.
 */

public class NetworkCheck extends AsyncTask<Void, Void, Void> {
    public interface NetworkCheckEvents {
        void onNetworkCheckSuccessfully();
        void onNetworkCheckError(Exception error);
    }

    private  NetworkCheckEvents events;
    private Context context;
    private int[] staArr = new int[]{-1,-1};

    public NetworkCheck(NetworkCheckEvents networkCheckEvents, Context context) {
        events = networkCheckEvents;
         this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(events == null) {
            return null;
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        queue.add(new CustomRequest(Request.Method.GET, Const.AuthorityApiUrl + "/status", response -> {
            updateStatus(0, true);
        }, error -> {
            events.onNetworkCheckError(error);
        }, null, null));


        queue.add(new CustomRequest(Request.Method.GET, Const.ProviderUrl + "/status", response -> {
            updateStatus(1, true);
        }, error -> {
            events.onNetworkCheckError(error);
        }, null, null));

        return null;
    }

    private synchronized void updateStatus(int id, boolean state) {
        this.staArr[id] = state ? 1 : 0;

        boolean result = false;
        for(int i = 0; i < this.staArr.length; i++)
        {
            if(this.staArr[i] == -1) {
                return;
            }
            result = this.staArr[i] == 1;
        }
        if(result) {
            events.onNetworkCheckSuccessfully();
        }
    }
}
