package mps.bachelor2017.bfh.ti.ch.mobiltypricing.data;

import android.content.SharedPreferences;

import java.io.Serializable;
import java.math.BigInteger;

import interfaces.HashValue;
import keys.PublicKey;

/**
 * Created by Pascal on 05.10.2017.
 */

public class MobilePublicKey implements PublicKey, Serializable {
    @HashValue
    private BigInteger n;
    @HashValue
    private BigInteger a;
    @HashValue
    private BigInteger g;
    @HashValue
    private BigInteger h;
    @HashValue
    private BigInteger w;
    @HashValue
    private BigInteger bigQ;
    @HashValue
    private BigInteger bigP;
    @HashValue
    private BigInteger bigF;
    @HashValue
    private BigInteger bigG;
    @HashValue
    private BigInteger bigH;

    public MobilePublicKey() {
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
}
