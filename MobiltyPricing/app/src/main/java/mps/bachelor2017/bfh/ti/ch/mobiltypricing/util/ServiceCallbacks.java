package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;

/**
 * Created by isabelcosta on 16.10.17.
 */

public interface ServiceCallbacks {
    boolean internetAndGPSPermissionIsOk();
    void createNotification(int id, String title, String text, boolean ongoing, boolean lightsAndVibrate);
    void stopDriving();
}
