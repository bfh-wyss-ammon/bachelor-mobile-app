package mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.InvoiceItems;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileTuple;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.TrackService;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomObjectRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.DatabaseHelper;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Error;

/**
 * Created by Pascal on 26.10.2017.
 */

public class TollTask extends AsyncTask<Void, Void, Void> implements SyncTupleTask.SendTupleTaskListener {

    public interface TollTaskListener {
        void onTollError(Error error);
        void onTollSuccessfull();
    }
    private static final Gson gson = new Gson();
    private TollTaskListener mListener;
    private Context mContext;
    private RequestQueue queue;
    private DatabaseHelper dbHelper;
    private MobileGroup mGroup;
    private MobileSecretKey mobileSecretKey;
    private int syncCount;

    public TollTask(TollTaskListener mListener, Context mContext, MobileGroup group, MobileSecretKey mobileSecretKey) {
        this.mListener = mListener;
        this.mContext = mContext;
        this.mGroup = group;
        this.mobileSecretKey = mobileSecretKey;
    }


    @Override
    protected Void doInBackground(Void... params) {
        dbHelper = new DatabaseHelper(mContext);
        queue = Volley.newRequestQueue(mContext);

        List<MobileTuple> tuples = dbHelper.getTuplesByStatus(MobileTuple.TupleStatus.LOCAL);
        syncCount = tuples.size();

        tuples.forEach(t -> {
            SyncTupleTask snycTupleTask = new SyncTupleTask(this, mContext);
            snycTupleTask.execute(t);
        });

        start();
        return null;
    }

    @Override
    public void onTupleSendError() {
        mListener.onTollError(Error.TUPLE_UPLOAD_ERROR);
    }

    @Override
    public void onTupleSendSuccessfull() {
        syncCount--;
        start();
    }

    private void start() {
        if(syncCount != 0) {
            return;
        }

        CustomRequest request = new CustomRequest(Request.Method.GET, Const.ProviderUrl + "/invoicePeriodes/" + mGroup.getGroupId(), new PeriodeResponseListener(), new PeriodeErrorListener(), null, null);
        queue.add(request);
    }


    private class PeriodeResponseListener implements Response.Listener, PeriodeTask.PeriodeTaskListener {
        @Override
        public void onResponse(Object response) {
            String[] periodes =  gson.fromJson(response.toString(), String[].class);

            List<PeriodeTask> tasks = new ArrayList<>();
            for (String periode : periodes) {
                if(dbHelper.hasRemoteTupleInPeriode(periode)) {
                    syncCount = +1;
                    tasks.add(new PeriodeTask(this, mGroup, mContext, mobileSecretKey, periode));
                }
            }
            tasks.forEach(task -> task.execute());
        }

        @Override
        public void onInvoiceError(Error error) {
            mListener.onTollError(error);
        }

        @Override
        public void onPaySuccessfull() {
            // wait all
            syncCount--;
            if(syncCount == 0) {
                mListener.onTollSuccessfull();
            }

        }
    }


    private class PeriodeErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            mListener.onTollError(Error.PERIODES_LOAD_ERROR);
        }
    }



}
