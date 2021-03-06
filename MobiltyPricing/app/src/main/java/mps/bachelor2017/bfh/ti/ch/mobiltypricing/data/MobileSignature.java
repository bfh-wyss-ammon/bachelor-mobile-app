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

import java.math.BigInteger;

import interfaces.HashValue;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.interfaces.DBField;
import signatures.Signature;


public class MobileSignature implements Signature {
    @Expose
    @HashValue
    @DBField(Name = "signature_u")
    private BigInteger u;
    @Expose
    @HashValue
    @DBField(Name = "signature_bigU1")
    private BigInteger bigU1;
    @Expose
    @HashValue
    @DBField(Name = "signature_bigU2")
    private BigInteger bigU2;
    @Expose
    @HashValue
    @DBField(Name = "signature_bigU3")
    private BigInteger bigU3;
    @Expose
    @HashValue

    @DBField(Name = "signature_zx")
    private BigInteger zx;
    @Expose
    @HashValue
    @DBField(Name = "signature_zr")
    private BigInteger zr;
    @Expose
    @HashValue
    @DBField(Name = "signature_ze")
    private BigInteger ze;
    @Expose
    @HashValue
    @DBField(Name = "signature_zbigR")
    private BigInteger zbigR;
    @Expose
    @HashValue
    @DBField(Name = "signature_c")
    private BigInteger c;

    public MobileSignature(){

    }

    @Override
    public BigInteger getU() {
        return u;
    }

    @Override
    public void setU(BigInteger u) {
        this.u = u;
    }

    @Override
    public BigInteger getBigU1() {
        return bigU1;
    }

    @Override
    public void setBigU1(BigInteger bigU1) {
        this.bigU1 = bigU1;
    }

    @Override
    public BigInteger getBigU2() {
        return bigU2;
    }

    @Override
    public void setBigU2(BigInteger bigU2) {
        this.bigU2 = bigU2;
    }

    @Override
    public BigInteger getBigU3() {
        return bigU3;
    }

    @Override
    public void setBigU3(BigInteger bigU3) {
        this.bigU3 = bigU3;
    }

    @Override
    public BigInteger getZx() {
        return zx;
    }

    @Override
    public void setZx(BigInteger zx) {
        this.zx = zx;
    }

    @Override
    public BigInteger getZr() {
        return zr;
    }

    @Override
    public void setZr(BigInteger zr) {
        this.zr = zr;
    }

    @Override
    public BigInteger getZe() {
        return ze;
    }

    @Override
    public void setZe(BigInteger ze) {
        this.ze = ze;
    }

    @Override
    public BigInteger getZbigR() {
        return zbigR;
    }

    @Override
    public void setZbigR(BigInteger zbigR) {
        this.zbigR = zbigR;
    }

    @Override
    public BigInteger getC() {
        return c;
    }

    @Override
    public void setC(BigInteger c) {
        this.c = c;
    }
}
