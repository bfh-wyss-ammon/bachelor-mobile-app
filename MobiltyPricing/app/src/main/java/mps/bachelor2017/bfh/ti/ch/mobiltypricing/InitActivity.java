package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Pascal on 24.11.2017.
 */

public class InitActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        this.setFinishOnTouchOutside(false);
    }

    @Override
    public void onBackPressed() {

    }
}
