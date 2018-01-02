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

package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

import static android.util.Base64.NO_WRAP;


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

    public static boolean verifyProviderMessage(byte[] message, String signature, Context context) {


        try {
            Log.e("Helper - Signature", signature);
            Log.e("Helper - message", Base64.encodeToString(message, NO_WRAP));
            byte[] publicKeyEncoded =  Base64.decode(UserHandler.getProviderKey(context), NO_WRAP);
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyEncoded);
            KeyFactory keyFactory = KeyFactory.getInstance("DSA", "BC");
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
            Signature sig = Signature.getInstance("SHA256withDSA", "BC");
            sig.initVerify(pubKey);
            sig.update(message);
            byte[] decode = Base64.decode(signature, NO_WRAP);
            boolean verify = sig.verify(decode);
            return true;

        } catch (Exception ex) {
            Log.e("Helper", ex.getMessage());
            Log.e("Helper", "t", ex);
            ex.printStackTrace();
            return false;
        }
    }

}
