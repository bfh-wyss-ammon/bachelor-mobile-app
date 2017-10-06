package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Created by Pascal on 05.10.2017.
 */

public class Helper {
    public static String getHash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            String blubr = Base64.encodeToString(hash, Base64.NO_WRAP);
            Log.v("hash", blubr);
            return blubr;
        } catch (Exception ex) {
            System.out.print("Credential.GetHash: Error" + ex.getMessage());
        }
        return "";
    }
}
