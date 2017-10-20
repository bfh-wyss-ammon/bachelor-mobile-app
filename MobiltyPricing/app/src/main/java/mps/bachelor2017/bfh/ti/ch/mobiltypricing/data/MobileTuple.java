package mps.bachelor2017.bfh.ti.ch.mobiltypricing.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.Date;

import data.Tuple;
import interfaces.HashValue;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.interfaces.DBClass;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.interfaces.DBField;
import signatures.Signature;

/**
 * Created by Pascal on 20.10.2017.
 */

@DBClass(Name = "tuple")
public class MobileTuple {
    @DBField(Name = "_id", PrimaryKey = true)
    private int id;

    @Expose
    @DBField(Name = "groupId")
    private int groupId;

    @Expose
    @HashValue
    @DBField(Name = "longitude")
    private BigDecimal longitude;

    @Expose
    @HashValue
    @DBField(Name = "latitude")
    private BigDecimal latitude;

    @Expose
    @HashValue
    @DBField(Name = "created")
    private Date created;

    @Expose
    private MobileSignature signature;

    @DBField(Name = "hash")
    private String hash;

    @DBField(Name = "uploaded")
    private Boolean uploaded;

    public MobileTuple() {

    }

    public MobileTuple(int groupId, BigDecimal latitude, BigDecimal longitude, Date created) {
        this.groupId = groupId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.created = created;
        this.uploaded = false;
        this.signature = new MobileSignature();
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public MobileSignature getSignature() {
        return signature;
    }

    public void setSignature(MobileSignature signature) {
        this.signature = signature;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Boolean getUploaded() {
        return uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }
}
