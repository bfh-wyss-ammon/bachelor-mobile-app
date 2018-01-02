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

package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.services.TrackService;

public class CustomAppCompatActivity extends AppCompatActivity implements ServiceConnection {
    protected TrackService mTrackService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, TrackService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void onServiceConnected() {

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.mTrackService = ((TrackService.TrackBinder) service).getService();
        onServiceConnected();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    protected void onServiceDisconnected() {

    }


    @Override
    public void onStop() {
        unbindService(this);
        super.onStop();
    }
}
