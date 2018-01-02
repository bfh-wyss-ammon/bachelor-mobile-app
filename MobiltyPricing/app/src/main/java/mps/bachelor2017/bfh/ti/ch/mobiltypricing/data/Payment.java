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

import interfaces.HashValue;

public class Payment {
    @Expose
    @HashValue
    private int summe;
    @Expose
    private MobileSignature signature;
    @Expose
    @HashValue
    private String signatureOnTuples;

    public Payment() {
    }


    public String getSignatureOnTuples() {
        return signatureOnTuples;
    }

    public void setSignatureOnTuples(String signatureOnTuples) {
        this.signatureOnTuples = signatureOnTuples;
    }

    public int getSumme() {
        return summe;
    }

    public void setSumme(int summe) {
        this.summe = summe;
    }

    public MobileSignature getSignature() {
        return signature;
    }

    public void setSignature(MobileSignature signature) {
        this.signature = signature;
    }
}