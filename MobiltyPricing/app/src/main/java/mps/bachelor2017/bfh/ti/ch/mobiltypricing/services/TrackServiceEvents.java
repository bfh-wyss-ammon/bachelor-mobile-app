package mps.bachelor2017.bfh.ti.ch.mobiltypricing.services;

/**
 * Created by Pascal on 28.11.2017.
 */

public interface TrackServiceEvents {
    void onGpsSignalReported();
    void onPayed();
    void missingGpsSignal(int timeUnityToFix); // timeUnityToFix: is a countdown to zero, starting at 5.
    void missingNetworkConnection(int timeUnityToFix); // timeUnityToFix: is a countdown to zero, starting at 5.
}
