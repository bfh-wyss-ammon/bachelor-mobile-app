package mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import demo.DemoSignature;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileTuple;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.DatabaseHelper;
import util.HashHelper;
import util.SignHelper;

/**
 * Created by Pascal on 05.10.2017.
 */

public class SyncTupleTask extends AsyncTask<MobileTuple, Void, Void> {



    public interface SendTupleTaskListener {
        void onTupleSendError();
        void onTupleSendSuccessfull();
    }

    private SendTupleTaskListener mListener;
    private Context mContext;
    private DatabaseHelper dbHelper;
    private MobileTuple dbTuple;

    public SyncTupleTask(SendTupleTaskListener listener, Context context) {
        this.mListener = listener;
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(MobileTuple... tuples) {
        dbHelper = new DatabaseHelper(mContext);
        if (tuples.length != 1) {
            return null;
        }
        dbTuple = tuples[0];
        RequestQueue queue = Volley.newRequestQueue(mContext);
        CustomRequest request = new CustomRequest(Request.Method.POST, Const.ProviderUrl + "/tuple", new TupleResponseListener(), new TupleConfirmErrorListener(), null, dbTuple);
        queue.add(request);
        return null;
    }

    private class TupleResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
           dbHelper.setTupleStatus(dbTuple.getHash(), MobileTuple.TupleStatus.REMOTE);
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
