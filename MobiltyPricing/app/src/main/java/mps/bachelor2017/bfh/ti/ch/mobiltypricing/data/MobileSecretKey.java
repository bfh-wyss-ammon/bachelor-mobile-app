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
}
