package mps.bachelor2017.bfh.ti.ch.mobiltypricing.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.InvoiceItems;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSignature;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileTuple;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomObjectRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.CustomRequest;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.DatabaseHelper;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Error;
import signatures.Signature;
import util.HashHelper;
import util.SignHelper;

import static android.util.Base64.NO_WRAP;

/**
 * Created by Pascal on 27.10.2017.
 */

public class PeriodeTask extends AsyncTask<Void, Void, Void>  {

    public interface PeriodeTaskListener {
        void onInvoiceError(Error error);
        void onPaySuccessfull();
    }

    private static final Gson gson = new Gson();
    private PeriodeTaskListener mListener;
    private Context mContext;
    private RequestQueue queue;
    private DatabaseHelper dbHelper;
    private String periode;
    private String session;
    private MobileGroup mGroup;
    private MobileSecretKey mobileSecretKey;
    private List<String> hashes;


    public PeriodeTask(PeriodeTaskListener listener, MobileGroup group, Context context, MobileSecretKey mobileSecretKey, String periode) {
        mListener = listener;
        this.mContext = context;
        mGroup = group;
        this.periode = periode;
        this.mobileSecretKey = mobileSecretKey;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String url = Const.ProviderUrl + "/invoiceitems/" + periode;

        CustomRequest request = new CustomRequest(Request.Method.GET, url, new InvoiceItemsResponseListener(), new InvoiceItemsErrorListener(), null, null) {
            @Override
            protected Response<String> parseNetworkResponse(
                    NetworkResponse response) {
                session = response.headers.get(Const.SessionHeader);
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(request);
        return null;
    }

    class PayObject {
        private int summe;
        private String signature;

        public PayObject() {
        }

        public PayObject(int summe, String signature) {
            this.summe = summe;
            this.signature = signature;
        }

        public int getSumme() {
            return summe;
        }

        public void setSumme(int summe) {
            this.summe = summe;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }

    private class InvoiceItemsResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            InvoiceItems invoiceItems =  gson.fromJson(response.toString(), InvoiceItems.class);

            hashes = dbHelper.getTuplesHashesStatus(MobileTuple.TupleStatus.REMOTE);

            int summe = 0;

            for (String hash : hashes) {
                if(invoiceItems.getItems().containsKey(hash)) {
                    summe += invoiceItems.getItems().get(hash);
                }
            }

            MobileSignature signature = new MobileSignature();

            SignHelper.sign(mobileSecretKey, mGroup.getPublicKey(), HashHelper.getHash(invoiceItems.getSignature() + summe), signature);

            PayObject payObject = new PayObject(summe, Base64.encodeToString(HashHelper.getHash(signature), NO_WRAP));

            CustomRequest request = new CustomRequest(Request.Method.GET, Const.ProviderUrl + "/pay/" + session, new PayResponseListener(), new InvoiceItemsErrorListener(), null, payObject);
            queue.add(request);

        }
    }
    private class PayResponseListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            hashes.forEach(hash -> dbHelper.setTupleStatus(hash, MobileTuple.TupleStatus.PAID));
            mListener.onPaySuccessfull();
        }
    }
    private class PayErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            mListener.onInvoiceError(Error.PERIODES_LOAD_ERROR);
        }
    }

    private class InvoiceItemsErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            mListener.onInvoiceError(Error.PERIODES_LOAD_ERROR);
        }
    }
}
