package mps.bachelor2017.bfh.ti.ch.mobiltypricing.data;

import android.content.SharedPreferences;

/**
 * Created by Pascal on 05.10.2017.
 */

public class MobileGroup {
    private Integer groupId;
    private MobilePublicKey publicKey;

    public MobileGroup() {

    }

    public MobileGroup(SharedPreferences settings) {
        groupId = settings.getInt("groupId", -1);
        publicKey = new MobilePublicKey(settings);
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public MobilePublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(MobilePublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void save(SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("groupId", groupId);
        editor.apply();
        publicKey.save(settings);
    }
}
