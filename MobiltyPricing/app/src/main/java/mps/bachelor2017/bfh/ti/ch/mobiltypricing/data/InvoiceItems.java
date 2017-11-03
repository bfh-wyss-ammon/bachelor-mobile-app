package mps.bachelor2017.bfh.ti.ch.mobiltypricing.data;

import java.util.Map;

/**
 * Created by Pascal on 27.10.2017.
 */

public class InvoiceItems {

    private Map<String, Integer> items;
    private String signature;
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
