package com.example.android.customnotifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
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
        finish();
    }
    public void save(View view) {
        Log.d(LOGATG, "save");
        TextView url = (TextView)findViewById(R.id.editText_targetURL);
        String target = url.getText().toString();

        if(!target.equals(Configuration.TARGET_URL)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            confirmationListenerFor lala = new confirmationListenerFor(target, this);
            builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", lala)
                    .setNegativeButton("No", lala)
                    .show();
        } else {
            finish();
        }
    }

    public void exit(View view) {
        Log.d(LOGATG, "exit");
        NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);// Cancel the persistent notification.
        mNM.cancel(ShowNotificationService.NOTIFICATION_ID);
        finish();
    }

    private class confirmationListenerFor implements DialogInterface.OnClickListener {
        private String target;
        private ConfigActivity view;

        public confirmationListenerFor(String target, ConfigActivity view) {
            this.target = target;
            this.view = view;
        }
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Configuration.TARGET_URL = target;
                    view.finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // No button clicked
                    break;
            }
        }
    }

}
