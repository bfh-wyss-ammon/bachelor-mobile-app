package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Pascal on 13.10.2017.
 */

public class CheckInFragment extends Fragment implements StartAnimation.StartAnimationEvents {

    interface CheckInEvents {
        void checkInStart();
    }

    private CheckInEvents mEvents;

    public void setCheckInEvents(CheckInEvents events) {
        this.mEvents = events;
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_in, container, false);

        ((StartAnimation)view.findViewById(R.id.StartAnimation)).setStartAnimationEvents(this);

        return view;
    }

    @Override
    public void onSubmit() {
        if(mEvents != null) {
            mEvents.checkInStart();
        }
    }
}
