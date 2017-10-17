package mps.bachelor2017.bfh.ti.ch.mobiltypricing.data;

import android.content.SharedPreferences;

import java.math.BigInteger;

import keys.SecretKey;
import responses.JoinResponse;

/**
 * Created by Pascal on 05.10.2017.
 */
public class MobileSecretKey implements SecretKey {
    private BigInteger x;
    private BigInteger w;
    private BigInteger y;
    private BigInteger e;
    private BigInteger r;
    private BigInteger bigE;
    private BigInteger bigY;
    private BigInteger commitment;

    public MobileSecretKey() {
    }

    public MobileSecretKey(SharedPreferences settings) {
        x = new BigInteger(settings.getString("secretKey_x", "0"));
        w = new BigInteger(settings.getString("secretKey_w", "0"));
        y = new BigInteger(settings.getString("secretKey_y", "0"));
        e = new BigInteger(settings.getString("secretKey_e", "0"));
        r = new BigInteger(settings.getString("secretKey_r", "0"));
        bigE = new BigInteger(settings.getString("secretKey_bigE", "0"));
        bigY = new BigInteger(settings.getString("secretKey_bigY", "0"));
        commitment = new BigInteger(settings.getString("secretKey_commitment", "0"));
    }
    @Override
    public BigInteger getX() {
        return x;
    }

    @Override
    public void setX(BigInteger x) {
        this.x = x;
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
    public BigInteger getY() {
        return y;
    }

    @Override
    public void setY(BigInteger y) {
        this.y = y;
    }

    @Override
    public BigInteger getE() {
        return e;
    }

    @Override
    public void setE(BigInteger e) {
        this.e = e;
    }

    @Override
    public BigInteger getR() {
        return r;
    }

    @Override
    public void setR(BigInteger r) {
        this.r = r;
    }

    @Override
    public BigInteger getBigE() {
        return bigE;
    }

    @Override
    public void setBigE(BigInteger bigE) {
        this.bigE = bigE;
    }

    @Override
    public BigInteger getBigY() {
        return bigY;
    }

    @Override
    public void setBigY(BigInteger bigY) {
        this.bigY = bigY;
    }

    @Override
    public BigInteger getCommitment() {
        return commitment;
    }

    @Override
    public void setCommitment(BigInteger commitment) {
        this.commitment = commitment;
    }

    @Override
    public void maintainResponse(JoinResponse joinResponse) {
        this.bigE = joinResponse.getEi();
        this.r = joinResponse.getRi().add(getR());
        this.e = joinResponse.getE();
        this.y = joinResponse.getYi();
        this.w = joinResponse.getWi();
    }

    public void save(SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("secretKey_x", x.toString());
        editor.putString("secretKey_w", w.toString());
        editor.putString("secretKey_y", y.toString());
        editor.putString("secretKey_e", e.toString());
        editor.putString("secretKey_r", r.toString());
        editor.putString("secretKey_bigE", bigE.toString());
        editor.putString("secretKey_bigY", bigY.toString());
        editor.putString("secretKey_commitment", commitment.toString());
        editor.apply();
    }
}
