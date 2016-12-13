package com.example.android.customnotifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ShowNotificationService extends Service {

    public static final String ARGUMENT_1_KEY = "ARGUMENT_1_KEY";
    public static final String ARGUMENT_2_KEY = "ARGUMENT_2_KEY";



    private static final String LOGTAG = ShowNotificationService.class.getSimpleName();

    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    public static String NOTIFICATION = "MY_SERVICE_NOTIFICATION";
    public static int NOTIFICATION_ID = 1;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        ShowNotificationService getService() {
            return ShowNotificationService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOGTAG, "ShowNotificationService.onStartCommand --> id: " + startId + ", intent: " + intent);

        createNotification();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION_ID);

        // Tell the user we stopped.
        Toast.makeText(this, "we just stopped.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void createNotification() {
        Log.d(LOGTAG, "createNotification");

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
        contentView.setTextViewText(R.id.textView_seconds, "time: " + System.currentTimeMillis());

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
        nm.notify(NOTIFICATION_ID, notification);
        // END_INCLUDE(notify)
    }

    public static final String INTENT_SOURCE_RESOURCE_ID = "intentSourceResourceId";
    private void addButtonListener(RemoteViews contentView, int componentId) {
        Intent notificationButtonListenerIntent = new Intent(this, notificationButtonListener.class);
        notificationButtonListenerIntent.putExtra(INTENT_SOURCE_RESOURCE_ID, ""+componentId);

        PendingIntent pendingNotificationButtonListener =
                PendingIntent.getBroadcast(this,
                        componentId,
                        notificationButtonListenerIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(componentId, pendingNotificationButtonListener);
    }
    private void addConfigButtonListener(RemoteViews contentView, int buttonId) {
        Intent settings = new Intent(getApplicationContext(), notificationButtonListener.class);
        PendingIntent pendingSwitchIntent = PendingIntent.getActivity(this, buttonId, settings, 0);
        contentView.setOnClickPendingIntent(buttonId, pendingSwitchIntent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class notificationButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            vibrate(context);
            String componentId = intent.getStringExtra(INTENT_SOURCE_RESOURCE_ID);
            try {
                int id = Integer.valueOf(componentId);
                Intent setTimeIntent = null;
                switch (id) {
                    case R.id.button_set10min:
                        Log.d(LOGTAG, "10min");
                        setTimeIntent = new Intent(context, DownloadService.class);
                        setTimeIntent.putExtra(ShowNotificationService.ARGUMENT_1_KEY, "arg-1");
                        setTimeIntent.putExtra(ShowNotificationService.ARGUMENT_2_KEY, "arg-2");
                        context.startService(intent);
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
                    case R.id.button_configure:
                        showConfig(context);
                        break;
                    default:
                        Log.d(LOGTAG, "Sender not registerd to set time.");
                        break;
                }
                updateNotification(context);
            } catch (Exception e) {
                Log.e(LOGTAG, "Illegal property '"+ INTENT_SOURCE_RESOURCE_ID +"': " + componentId);
            }
        }

        private void updateNotification(Context context) {
            Intent intent = new Intent(context, ShowNotificationService.class);
            intent.putExtra(ShowNotificationService.ARGUMENT_1_KEY, "argument-1");
            intent.putExtra(ShowNotificationService.ARGUMENT_2_KEY, "argument-2");
            Log.d(LOGTAG, "updateNotification() -- starting ShowNotificationService...");
            context.startService(intent);
        }

        private void showConfig(Context context) {
            Intent config = new Intent(context, ConfigActivity.class);
            Log.d(LOGTAG, "showConfig() -- starting ConfigActivity...");
            context.startActivity(config);
        }

        private void vibrate(Context context) {
            Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(100);
        }
    }


}
