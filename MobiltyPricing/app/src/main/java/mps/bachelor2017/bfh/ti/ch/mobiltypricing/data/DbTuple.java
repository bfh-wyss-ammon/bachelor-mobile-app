package mps.bachelor2017.bfh.ti.ch.mobiltypricing.data;

import data.Tuple;

/**
 * Created by Pascal on 20.10.2017.
 */

public class DbTuple {
    private String Hash;
    private Boolean isUploaded;
    private Tuple tuple;

    public DbTuple(String hash, Boolean isUploaded, Tuple tuple) {
        Hash = hash;
        this.isUploaded = isUploaded;
        this.tuple = tuple;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

    public Boolean getUploaded() {
        return isUploaded;
    }

    public void setUploaded(Boolean uploaded) {
        isUploaded = uploaded;
    }

    public Tuple getTuple() {
        return tuple;
    }

    public void setTuple(Tuple tuple) {
        this.tuple = tuple;
    }
}
