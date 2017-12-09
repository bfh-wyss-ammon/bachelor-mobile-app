package mps.bachelor2017.bfh.ti.ch.mobiltypricing.services;

/**
 * Created by Pascal on 01.12.2017.
 */

public interface ConnectionEvents {
    void onGpsPermissionMissing();
    void onNetworkPermissionMissing();
    void onHasGpsAndNetworkPermission();
}
