/**
 *   Copyright 2018 Pascal Ammon, Gabriel Wyss
 *
 * 	 Implementation eines anonymen Mobility Pricing Systems auf Basis eines Gruppensignaturschemas
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package mps.bachelor2017.bfh.ti.ch.mobiltypricing.data;

import java.util.Map;

import interfaces.HashValue;

public class InvoiceItems {
    @HashValue
    private Map<String, Integer> items;

    private String signature;

    @HashValue
    private String sessionId;


    public InvoiceItems() {

    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
