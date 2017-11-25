package mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gson.BigIntegerTypeAdapter;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.InvoiceItems;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSignature;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileTuple;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.Payment;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.TrackService;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomObjectRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.DatabaseHelper;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Error;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.UserHandler;
import util.HashHelper;
import util.SignHelper;

import static android.util.Base64.NO_WRAP;

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
    private int syncCount;
    private List<String> hashes;

    public TollTask(TollTaskListener mListener, Context mContext) {
        this.mListener = mListener;
        this.mContext = mContext;
        hashes = new ArrayList<>();
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
        if (syncCount != 0) {
            return;
        }

        CustomRequest request = new CustomRequest(Request.Method.GET, Const.ProviderUrl + "/invoicePeriodes/" +  UserHandler.getGroupId(mContext), new PeriodeResponseListener(), new ErrorListener(), null, null);
        queue.add(request);
    }

    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {

            error.printStackTrace();
        }
    }

    private class PeriodeResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            String[] periodes = gson.fromJson(response.toString(), String[].class);

            for (int i = 0; i < periodes.length; i++) {
                if (dbHelper.hasRemoteTupleInPeriode(periodes[i], i < periodes.length - 1 ? periodes[i + 1] : null)) {
                    String url = Const.ProviderUrl + "/invoiceitems/" + UserHandler.getGroupId(mContext) + "/" + periodes[i];
                    syncCount++;
                    CustomRequest request = new CustomRequest(Request.Method.GET, url, new ResponseListener(), new ErrorListener(), null, null);
                    queue.add(request);

                }
            }
        }

        private class ErrorListener implements Response.ErrorListener {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
            }
        }

        private class ResponseListener implements Response.Listener {
            @Override
            public void onResponse(Object response) {
                Log.v("test", response.toString());
                InvoiceItems invoiceItems = gson.fromJson(response.toString(), InvoiceItems.class);

                int summe = 0;
                for (String hash : dbHelper.getTuplesHashesStatus(MobileTuple.TupleStatus.REMOTE)) {
                    if (invoiceItems.getItems().containsKey(hash)) {
                        summe += invoiceItems.getItems().get(hash);
                        hashes.add(hash);
                    }
                }

                MobileSignature signature = new MobileSignature();
                Payment payment = new Payment();
                payment.setSignatureOnTuples(invoiceItems.getSignature());
                payment.setSumme(summe);


                SignHelper.sign( UserHandler.getSecretKey(mContext), UserHandler.getPublicKey(mContext), HashHelper.getHash(payment), signature);
                payment.setSignature(signature);
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(BigInteger.class, new BigIntegerTypeAdapter());
                Gson gson = builder.create();
                Log.v("blubr", gson.toJson(payment));

                CustomRequest request = new CustomRequest(Request.Method.POST,  Const.ProviderUrl + "/pay/" + invoiceItems.getSessionId(), new PayResponseListener(), new PayErrorListener(), null, payment);
                queue.add(request);

            }
        }
    }

    private class PayResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            hashes.forEach(hash -> dbHelper.setTupleStatus(hash, MobileTuple.TupleStatus.PAID));
            syncCount--;
            if (syncCount == 0) {
                mListener.onTollSuccessfull();

            }
        }
    }

    private class PayErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {

            error.printStackTrace();
        }
    }


}
