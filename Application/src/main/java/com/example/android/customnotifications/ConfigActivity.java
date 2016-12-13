package com.example.android.customnotifications;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ConfigActivity extends Activity {

    private static final String LOGATG = "ConfigActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        TextView url = (TextView)findViewById(R.id.editText_targetURL);
        url.setText(Configuration.TARGET_URL);
    }

    public void abort(View view) {
        Log.d(LOGATG, "abort");
        NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);// Cancel the persistent notification.
        mNM.cancel(ShowNotificationService.NOTIFICATION_ID);
        finish();
    }
    public void save(View view) {
        Log.d(LOGATG, "save");
        TextView url = (TextView)findViewById(R.id.editText_targetURL);
        String target = url.getText().toString();
        Configuration.TARGET_URL = target;
    }
}
