package com.example.android.customnotifications;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ConfigActivity extends Activity {

    private static final String LOGATG = "ConfigActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
    }

    public void abort(View view) {
        Log.d(LOGATG, "abort");
    }
    public void save(View view) {
        Log.d(LOGATG, "abort");
    }
}
