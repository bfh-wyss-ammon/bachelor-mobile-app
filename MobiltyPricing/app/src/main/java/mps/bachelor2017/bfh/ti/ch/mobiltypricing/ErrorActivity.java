/**
 * Copyright 2018 Pascal Ammon, Gabriel Wyss
 * <p>
 * Implementation eines anonymen Mobility Pricing Systems auf Basis eines Gruppensignaturschemas
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.util.UserHandler;

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
