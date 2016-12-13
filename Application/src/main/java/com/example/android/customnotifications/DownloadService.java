package com.example.android.customnotifications;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DownloadService extends IntentService {

    public static final String ARGUMENT_MINUTES = "ARGUMENT_MINUTES";

    private int result = Activity.RESULT_CANCELED;

    public DownloadService() {
        super("DownloadService");
    }

    // will be called asynchronously by Android
    @Override
    protected void onHandleIntent(Intent intent) {
        int minutes = intent.getIntExtra(ARGUMENT_MINUTES, -1);
        Log.d("DownloadService", "onHandleIntent() -- minutes: " + minutes);
        while (minutes-- > 0) {
            try {
                Thread.sleep(1000);
                Log.d("DownloadService", "**** i: " + minutes);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        updateNotification("0", 1611);
    }

    private void updateNotification(String relais_state, int seconds_until_switchoff_counter) {
        Log.d("DownloadService", "updateNotification: " + relais_state + ", " + seconds_until_switchoff_counter);
        Intent intent = new Intent(this, ShowNotificationService.class);
        intent.putExtra(ShowNotificationService.ARGUMENT_RELAIS_STATE, relais_state);
        intent.putExtra(ShowNotificationService.ARGUMENT_SECONDS_UNTIL_SWITCHOFF_COUNTER, seconds_until_switchoff_counter);
        Log.d("updateNotification", "starting ShowNotificationService...");
        startService(intent);
    }
}