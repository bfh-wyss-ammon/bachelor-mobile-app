package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileGroup;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSecretKey;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.Const;

/**
 * Created by Pascal on 06.10.2017.
 */

public class ActivityInfo extends AppCompatActivity {

    private MobileGroup group;
    private MobileSecretKey secretKey;
    private TextView mInfo;

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        SharedPreferences settings = getSharedPreferences(Const.PreferenceKey, 0);
        group = new MobileGroup(settings);
        secretKey = new MobileSecretKey(settings);

        this.mButton = (Button) findViewById(R.id.clearButton);
        this.mInfo = (TextView) findViewById(R.id.infoText);

        String info = "Your Key info :\n";
        info += "groupId:" + group.getGroupId() + "\n\n";
        info += "Secret Key bigY: \n" + secretKey.getBigY().toString().substring(0, 10) + "...\n\n";
        info += "Public Key bigP: \n" + group.getPublicKey().getBigP().toString().substring(0, 10) + "...\n";

        this.mInfo.setText(info);

        this.mButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("status", false);
            editor.commit();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });

    }


}
