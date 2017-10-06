package mps.bachelor2017.bfh.ti.ch.mobiltypricing.data;

import android.content.SharedPreferences;

import java.math.BigInteger;

import keys.PublicKey;

/**
 * Created by Pascal on 05.10.2017.
 */

public class MobilePublicKey implements PublicKey {
    private BigInteger n;
    private BigInteger a;
    private BigInteger g;
    private BigInteger h;
    private BigInteger w;
    private BigInteger bigQ;
    private BigInteger bigP;
    private BigInteger bigF;
    private BigInteger bigG;
    private BigInteger bigH;

    public MobilePublicKey() {
    }

    public MobilePublicKey(SharedPreferences settings) {
        n = new BigInteger(settings.getString("publicKey_n", "0"));
        a = new BigInteger(settings.getString("publicKey_a", "0"));
        g = new BigInteger(settings.getString("publicKey_g", "0"));
        h = new BigInteger(settings.getString("publicKey_h", "0"));
        w = new BigInteger(settings.getString("publicKey_w", "0"));
        bigQ = new BigInteger(settings.getString("publicKey_bigQ", "0"));
        bigP = new BigInteger(settings.getString("publicKey_bigP", "0"));
        bigF = new BigInteger(settings.getString("publicKey_bigF", "0"));
        bigG = new BigInteger(settings.getString("publicKey_bigG", "0"));
        bigH = new BigInteger(settings.getString("publicKey_bigH", "0"));
    }

    @Override
    public BigInteger getN() {
        return n;
    }

    @Override
    public void setN(BigInteger n) {
        this.n = n;
    }

    @Override
    public BigInteger getA() {
        return a;
    }

    @Override
    public void setA(BigInteger a) {
        this.a = a;
    }

    @Override
    public BigInteger getG() {
        return g;
    }

    @Override
    public void setG(BigInteger g) {
        this.g = g;
    }

    @Override
    public BigInteger getH() {
        return h;
    }

    @Override
    public void setH(BigInteger h) {
        this.h = h;
    }

    @Override
    public BigInteger getW() {
        return w;
    }

    @Override
    public void setW(BigInteger w) {
        this.w = w;
    }

    @Override
    public BigInteger getBigQ() {
        return bigQ;
    }

    @Override
    public void setBigQ(BigInteger bigQ) {
        this.bigQ = bigQ;
    }

    @Override
    public BigInteger getBigP() {
        return bigP;
    }

    @Override
    public void setBigP(BigInteger bigP) {
        this.bigP = bigP;
    }

    @Override
    public BigInteger getBigF() {
        return bigF;
    }

    @Override
    public void setBigF(BigInteger bigF) {
        this.bigF = bigF;
    }

    @Override
    public BigInteger getBigG() {
        return bigG;
    }

    @Override
    public void setBigG(BigInteger bigG) {
        this.bigG = bigG;
    }

    @Override
    public BigInteger getBigH() {
        return bigH;
    }

    @Override
    public void setBigH(BigInteger bigH) {
        this.bigH = bigH;
    }

    public void save(SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("publicKey_n", n.toString());
        editor.putString("publicKey_a", a.toString());
        editor.putString("publicKey_g", g.toString());
        editor.putString("publicKey_h", h.toString());
        editor.putString("publicKey_w", w.toString());
        editor.putString("publicKey_bigQ", bigQ.toString());
        editor.putString("publicKey_bigP", bigP.toString());
        editor.putString("publicKey_bigF", bigF.toString());
        editor.putString("publicKey_bigG", bigG.toString());
        editor.putString("publicKey_bigH", bigH.toString());
        editor.commit();
    }
}
