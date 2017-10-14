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

import data.Tuple;
import demo.DemoSignature;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomObjectRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Helper;
import util.HashHelper;
import util.SignHelper;

/**
 * Created by Pascal on 05.10.2017.
 */

public class SendTupleTask extends AsyncTask<Tuple, Void, Void> {

    public interface SendTupleTaskListener {
        void onTupleSendError();
        void onTupleSendSuccessfull();
    }

    private SendTupleTaskListener mListener;
    private Context mContext;
    private MobileGroup mGroup;
    private MobileSecretKey mMobileSecretKey;

    public SendTupleTask(SendTupleTaskListener listener, Context context, MobileGroup group, MobileSecretKey mobileSecretKey) {
        this.mListener = listener;
        this.mContext = context;
        this.mGroup = group;
        this.mMobileSecretKey = mobileSecretKey;
    }

    @Override
    protected Void doInBackground(Tuple... tuples) {
        if (tuples.length != 1) {
            return null;
        }

        Tuple tuple = tuples[0];
        RequestQueue queue = Volley.newRequestQueue(mContext);

        tuple.setGroupId(mGroup.getGroupId());

        DemoSignature signature = new DemoSignature();

        SignHelper.sign(mMobileSecretKey, mGroup.getPublicKey(), HashHelper.getHash(tuple), signature);
        tuple.setSignature(signature);

        CustomRequest request = new CustomRequest(Request.Method.POST, Const.ProviderUrl + "/tuple", new TupleResponseListener(), new TupleConfirmErrorListener(), null, tuple);

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

        return null;
    }

    private class TupleResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
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
