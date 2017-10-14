package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Pascal on 05.10.2017.
 */

public class Helper {
    private static MessageDigest digest;
    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.print("Credential.GetHash: Error" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getHash(String text) {
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(hash, Base64.NO_WRAP);
    }


    public static byte[] getHashAsByte(String text) {
        return digest.digest(text.getBytes(StandardCharsets.UTF_8));
    }
}
