package mps.bachelor2017.bfh.ti.ch.mobiltypricing.services;

/**
 * Created by Pascal on 28.11.2017.
 */

public interface DriveEvents {
    void onGpsSignalError();

    void onGpsSignal();

    void onDrive();

    void onError();

    void onConnectionError();

    void onSend();

    void onFinished();

    void onConnected();
}
