/*
 * Copyright (C) 2013 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.customnotifications;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class MainActivity extends Activity {
    private static final String LOGTAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);

        createNotification();
        this.finish();
    }

    /**
     * Create and show a notification with a custom layout.
     * This callback is defined through the 'onClick' attribute of the
     * 'Show Notification' button in the XML layout.
     *
     * @param v
     */
    public void showNotificationClicked(View v) {
        createNotification();
    }

    private void createNotification() {
        Intent intent = new Intent(this, ShowNotificationService.class);
        intent.putExtra(ShowNotificationService.ARGUMENT_RELAIS_STATE, "0");
        intent.putExtra(ShowNotificationService.ARGUMENT_SECONDS_UNTIL_SWITCHOFF_COUNTER, 0);
        Log.d(LOGTAG, "starting ShowNotificationService...");
        startService(intent);
    }


}
