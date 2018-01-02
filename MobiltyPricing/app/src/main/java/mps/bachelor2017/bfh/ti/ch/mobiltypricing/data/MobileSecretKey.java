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

import java.math.BigInteger;

import keys.SecretKey;
import responses.JoinResponse;

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
