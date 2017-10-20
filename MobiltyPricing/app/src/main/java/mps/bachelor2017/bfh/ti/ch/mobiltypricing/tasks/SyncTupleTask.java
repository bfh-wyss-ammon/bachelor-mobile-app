package mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import data.Tuple;
import demo.DemoSignature;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.DbTuple;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomObjectRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.DatabaseHelper;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Helper;
import util.HashHelper;
import util.SignHelper;

/**
 * Created by Pascal on 05.10.2017.
 */

public class SyncTupleTask extends AsyncTask<DbTuple, Void, Void> {

    public interface SendTupleTaskListener {
        void onTupleSendError();
        void onTupleSendSuccessfull();
    }

    private SendTupleTaskListener mListener;
    private Context mContext;
    private DatabaseHelper dbHelper;
    private DbTuple dbTuple;
    private MobileGroup mGroup;
    private MobileSecretKey mMobileSecretKey;

    public SyncTupleTask(SendTupleTaskListener listener, Context context, MobileGroup group, MobileSecretKey mobileSecretKey) {
        this.mListener = listener;
        this.mContext = context;
        this.mGroup = group;
        this.mMobileSecretKey = mobileSecretKey;
    }

    @Override
    protected Void doInBackground(DbTuple... tuples) {
        dbHelper = new DatabaseHelper(mContext);
        if (tuples.length != 1) {
            return null;
        }
        dbTuple = tuples[0];

        DemoSignature signature = new DemoSignature();

        SignHelper.sign(mMobileSecretKey, mGroup.getPublicKey(), HashHelper.getHash(dbTuple.getTuple()), signature);
        dbTuple.getTuple().setSignature(signature);

        RequestQueue queue = Volley.newRequestQueue(mContext);
        CustomRequest request = new CustomRequest(Request.Method.POST, Const.ProviderUrl + "/tuple", new TupleResponseListener(), new TupleConfirmErrorListener(), null, dbTuple.getTuple());
        queue.add(request);
        return null;
    }

    private class TupleResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
           dbHelper.setTupleIsUploaded(dbTuple.getHash());
           mListener.onTupleSendSuccessfull();
        }
    }


    private class TupleConfirmErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            mListener.onTupleSendError();
        }
    }

}
