/**
 * Copyright 2018 Pascal Ammon, Gabriel Wyss
 * <p>
 * Implementation eines anonymen Mobility Pricing Systems auf Basis eines Gruppensignaturschemas
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package mps.bachelor2017.bfh.ti.ch.mobiltypricing.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.Date;

import interfaces.HashValue;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.interfaces.DBClass;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.interfaces.DBField;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;


@DBClass(Name = "tuple")
public class MobileTuple {
    public enum TupleStatus {
        LOCAL,
        REMOTE,
        PAID
    }


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
    @DBField(Name = Const.DbTupleCreatedField)
    private Date created;

    @Expose
    private MobileSignature signature;

    @DBField(Name = "hash")
    private String hash;

    @DBField(Name = Const.DbTupleStatusField)
    private TupleStatus status;

    public MobileTuple() {

    }

    public MobileTuple(int groupId, BigDecimal latitude, BigDecimal longitude, Date created) {
        this.groupId = groupId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.created = created;
        this.status = TupleStatus.LOCAL;
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

    public TupleStatus getStatus() {
        return status;
    }

    public void setStatus(TupleStatus status) {
        this.status = status;
    }
}
