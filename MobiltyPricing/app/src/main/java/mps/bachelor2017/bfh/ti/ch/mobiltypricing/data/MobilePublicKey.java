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

import java.io.Serializable;
import java.math.BigInteger;

import interfaces.HashValue;
import keys.PublicKey;

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
