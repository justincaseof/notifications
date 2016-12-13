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

    /**
     * This sample demonstrates notifications with custom content views.
     *
     * <p>On API level 16 and above a big content view is also defined that is used for the
     * 'expanded' notification. The notification is created by the NotificationCompat.Builder.
     * The expanded content view is set directly on the {@link android.app.Notification} once it has been build.
     * (See {@link android.app.Notification#bigContentView}.) </p>
     *
     * <p>The content views are inflated as {@link android.widget.RemoteViews} directly from their XML layout
     * definitions using {@link android.widget.RemoteViews#RemoteViews(String, int)}.</p>
     */
    private void createNotification() {
        // BEGIN_INCLUDE(notificationCompat)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        // END_INCLUDE(notificationCompat)

        // BEGIN_INCLUDE(intent)
        //Create Intent to launch this Activity again if the notification is clicked.
//        Intent i = new Intent(this, MainActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent intent = PendingIntent.getActivity(this, 0, i,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(intent);
        // END_INCLUDE(intent)

        // BEGIN_INCLUDE(ticker)
        // Sets the ticker text
        builder.setTicker(getResources().getString(R.string.custom_notification));

        // Sets the small icon for the ticker
        builder.setSmallIcon(R.drawable.ic_notification_timer);
        // END_INCLUDE(ticker)

        // BEGIN_INCLUDE(buildNotification)
        // Cancel the notification when clicked
        builder.setAutoCancel(false);

        // BEGIN_INCLUDE(actions)
//        Intent notifIntent = new Intent(MainActivity.this, Main2Activity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);
//        builder.addAction(
//            new NotificationCompat.Action(
//                    R.drawable.ic_stat_plus, "+ 30", pendingIntent));
        // END_INCLUDE(actions)

        // BEGIN_INCLUDE(content)
        // 1) small notification
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
//        final String time = DateFormat.getTimeInstance().format(new Date()).toString();
//        final String text = getResources().getString(R.string.collapsed, time);
//        contentView.setTextViewText(R.id.textView, text);
//        builder.setContent(contentView);
        // 2) big notification
//        RemoteViews bigContentView = new RemoteViews(getPackageName(), R.layout.notification_expanded);
//        builder.setCustomBigContentView(bigContentView);
        // BEGIN_INCLUDE(content)

        builder.setContent(contentView);
        contentView.setTextViewText(R.id.textView_ip, Configuration.target);
        contentView.setTextViewText(R.id.textView_seconds, "123");

        // Build the notification
        Notification notification = builder.build();

        // BEGIN_INCLUDE(on-notification button stuff)
        addButtonListener(contentView, R.id.button_set10min);
        addButtonListener(contentView, R.id.button_set30min);
        addButtonListener(contentView, R.id.button_set60min);
        addButtonListener(contentView, R.id.button_set90min);
        addButtonListener(contentView, R.id.button_on);
        addButtonListener(contentView, R.id.button_off);
        addConfigButtonListener(contentView, R.id.button_configure);
        // END_INCLUDE(on-notification button stuff)

        // BUG: notification won't show anything on it without the below line in some android versions
        //notification.contentView = contentView;
        // END_INCLUDE(buildNotification)

        // START_INCLUDE(notify)
        // Use the NotificationManager to show the notification
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0, notification);
        // END_INCLUDE(notify)
    }

    public static final String INTENT_SOURCE_RESOURCE_ID = "intentSourceResourceId";
    private void addButtonListener(RemoteViews contentView, int componentId) {
        Intent switchIntent = new Intent(this, switchButtonListener.class);
        switchIntent.putExtra(INTENT_SOURCE_RESOURCE_ID, ""+componentId);

        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, componentId, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(componentId, pendingSwitchIntent);
    }
    private void addConfigButtonListener(RemoteViews contentView, int buttonId) {
        Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
        PendingIntent pendingSwitchIntent = PendingIntent.getActivity(this, buttonId, settings, 0);
        contentView.setOnClickPendingIntent(buttonId, pendingSwitchIntent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);
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



    public static class switchButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            vibrate(context);

            String componentId = intent.getStringExtra(INTENT_SOURCE_RESOURCE_ID);
            try {
                int id = Integer.valueOf(componentId);
                switch (id) {
                    case R.id.button_set10min:
                        Log.d(LOGTAG, "10min");
                        break;
                    case R.id.button_set30min:
                        Log.d(LOGTAG, "30min");
                        break;
                    case R.id.button_set60min:
                        Log.d(LOGTAG, "60min");
                        break;
                    case R.id.button_set90min:
                        Log.d(LOGTAG, "90min");
                        break;
                    case R.id.button_on:
                        Log.d(LOGTAG, "on");
                    break;case R.id.button_off:
                        Log.d(LOGTAG, "off");
                        break;
                    default:
                        Log.d(LOGTAG, "Sender not registerd to set time.");
                        break;
                }
            } catch (Exception e) {
                Log.e(LOGTAG, "Illegal property '"+ INTENT_SOURCE_RESOURCE_ID +"': " + componentId);
            }
        }

        private void vibrate(Context context) {
            Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(100);
        }
    }

}
