package com.example.android.customnotifications;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MDNSScanService extends IntentService {

    public static final String ARGUMENT_BOOL_1 = "ARGUMENT_1";
    public static final String LOGTAG = "MDNSScanService";

    public MDNSScanService() {
        super("MDNSScanService");
    }

    // will be called asynchronously by Android
    @Override
    protected void onHandleIntent(Intent intent) {
        boolean updateOnly = intent.getBooleanExtra(ARGUMENT_BOOL_1, false);

    }

}