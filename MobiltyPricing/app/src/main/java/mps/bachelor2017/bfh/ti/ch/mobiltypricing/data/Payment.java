package mps.bachelor2017.bfh.ti.ch.mobiltypricing.data;

import com.google.gson.annotations.Expose;

import interfaces.HashValue;

/**
 * Created by Pascal on 03.11.2017.
 */

public class Payment {
    @Expose
    @HashValue
    private int summe;
    @Expose
    private MobileSignature signature;
    @Expose
    @HashValue
    private String signatureOnTuples;

    public Payment() {
    }


    public String getSignatureOnTuples() {
        return signatureOnTuples;
    }

    public void setSignatureOnTuples(String signatureOnTuples) {
        this.signatureOnTuples = signatureOnTuples;
    }

    public int getSumme() {
        return summe;
    }

    public void setSumme(int summe) {
        this.summe = summe;
    }

    public MobileSignature getSignature() {
        return signature;
    }

    public void setSignature(MobileSignature signature) {
        this.signature = signature;
    }
}