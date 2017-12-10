package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.UserHandler;

/**
 * Created by Pascal on 09.12.2017.
 */

public class ErrorActivity extends AppCompatActivity {

    private TextView mMessage;
    private TextView mMessageDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.error_activity);
        mMessage = (TextView) findViewById(R.id.ErrorActivityMessage);
        mMessageDetail = (TextView) findViewById(R.id.ErrorActivityMessageDetail);
        mMessage.setText(getIntent().getStringExtra("message"));
        mMessageDetail.setText(getIntent().getStringExtra("messageDetail"));
    }
}
