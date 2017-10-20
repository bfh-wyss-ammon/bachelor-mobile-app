package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;

/**
 * Created by isabelcosta on 16.10.17.
 */

public interface TrackServiceCallbacks {
    boolean permissionCheck();
    void onStop();
    void onError(String Message);
    void onStart();
}
