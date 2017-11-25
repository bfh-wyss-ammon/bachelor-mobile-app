package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.math.BigInteger;
import java.security.PublicKey;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobilePublicKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;

/**
 * Created by Pascal on 24.11.2017.
 */

public class UserHandler {
    public static boolean exist(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Const.PreferenceKey, 0);
        return settings.getBoolean(Const.HasUserKey, false);
    }

    public static void save(Context context, MobileSecretKey secretKey, MobileGroup group) {
        SharedPreferences settings = context.getSharedPreferences(Const.PreferenceKey, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt("groupId", group.getGroupId());

        editor.putString("publicKey_n", group.getPublicKey().getN().toString());
        editor.putString("publicKey_a", group.getPublicKey().getA().toString());
        editor.putString("publicKey_g", group.getPublicKey().getG().toString());
        editor.putString("publicKey_h", group.getPublicKey().getH().toString());
        editor.putString("publicKey_w", group.getPublicKey().getW().toString());
        editor.putString("publicKey_bigQ", group.getPublicKey().getBigQ().toString());
        editor.putString("publicKey_bigP", group.getPublicKey().getBigP().toString());
        editor.putString("publicKey_bigF", group.getPublicKey().getBigF().toString());
        editor.putString("publicKey_bigG", group.getPublicKey().getBigG().toString());
        editor.putString("publicKey_bigH", group.getPublicKey().getBigH().toString());

        editor.putString("secretKey_x", secretKey.getX().toString());
        editor.putString("secretKey_w", secretKey.getW().toString());
        editor.putString("secretKey_y", secretKey.getY().toString());
        editor.putString("secretKey_e", secretKey.getE().toString());
        editor.putString("secretKey_r", secretKey.getR().toString());
        editor.putString("secretKey_bigE", secretKey.getBigE().toString());
        editor.putString("secretKey_bigY", secretKey.getBigY().toString());
        editor.putString("secretKey_commitment", secretKey.getCommitment().toString());

        editor.putBoolean(Const.HasUserKey, true);
        editor.apply();
    }

    private static MobilePublicKey publicKey;

    public static MobilePublicKey getPublicKey(Context context) {
        if (publicKey == null) {
            SharedPreferences settings = context.getSharedPreferences(Const.PreferenceKey, 0);
            MobilePublicKey publicKey = new MobilePublicKey();
            publicKey.setN(new BigInteger(settings.getString("publicKey_n", "0")));
            publicKey.setA(new BigInteger(settings.getString("publicKey_a", "0")));
            publicKey.setG(new BigInteger(settings.getString("publicKey_g", "0")));
            publicKey.setH(new BigInteger(settings.getString("publicKey_h", "0")));
            publicKey.setW(new BigInteger(settings.getString("publicKey_w", "0")));
            publicKey.setBigQ(new BigInteger(settings.getString("publicKey_bigQ", "0")));
            publicKey.setBigP(new BigInteger(settings.getString("publicKey_bigP", "0")));
            publicKey.setBigF(new BigInteger(settings.getString("publicKey_bigF", "0")));
            publicKey.setBigG(new BigInteger(settings.getString("publicKey_bigG", "0")));
            publicKey.setBigH(new BigInteger(settings.getString("publicKey_bigH", "0")));
        }
        return publicKey;
    }

    private static int groupId = -1;

    public static int getGroupId(Context context) {
        if (groupId == -1) {
            SharedPreferences settings = context.getSharedPreferences(Const.PreferenceKey, 0);
            groupId = settings.getInt("groupId", -1);
        }
        return groupId;
    }

    private static MobileSecretKey secretKey;

    public static MobileSecretKey getSecretKey(Context context) {
        if (secretKey == null) {
            SharedPreferences settings = context.getSharedPreferences(Const.PreferenceKey, 0);
            MobileSecretKey secretKey = new MobileSecretKey();
            secretKey.setX(new BigInteger(settings.getString("secretKey_x", "0")));
            secretKey.setW(new BigInteger(settings.getString("secretKey_w", "0")));
            secretKey.setY(new BigInteger(settings.getString("secretKey_y", "0")));
            secretKey.setE(new BigInteger(settings.getString("secretKey_e", "0")));
            secretKey.setR(new BigInteger(settings.getString("secretKey_r", "0")));
            secretKey.setBigE(new BigInteger(settings.getString("secretKey_bigE", "0")));
            secretKey.setBigY(new BigInteger(settings.getString("secretKey_bigY", "0")));
            secretKey.setCommitment(new BigInteger(settings.getString("secretKey_commitment", "0")));
        }
        return secretKey;
    }

    public static void clear(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Const.PreferenceKey, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("groupId");
        editor.remove("publicKey_n");
        editor.remove("publicKey_a");
        editor.remove("publicKey_g");
        editor.remove("publicKey_h");
        editor.remove("publicKey_w");
        editor.remove("publicKey_bigQ");
        editor.remove("publicKey_bigP");
        editor.remove("publicKey_bigF");
        editor.remove("publicKey_bigG");
        editor.remove("publicKey_bigP");
        editor.remove("publicKey_bigF");
        editor.remove("publicKey_bigG");
        editor.remove("publicKey_bigH");
        editor.remove("secretKey_x");
        editor.remove("secretKey_w");
        editor.remove("secretKey_y");
        editor.remove("secretKey_e");
        editor.remove("secretKey_r");
        editor.remove("secretKey_bigE");
        editor.remove("secretKey_bigY");
        editor.remove("secretKey_commitment");
        editor.remove(Const.HasUserKey);
        editor.apply();
        secretKey = null;
        publicKey = null;
    }
}
