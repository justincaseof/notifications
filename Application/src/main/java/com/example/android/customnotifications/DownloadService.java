package com.example.android.customnotifications;

import android.app.Activity;
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
import java.net.URLConnection;

public class DownloadService extends IntentService {

    public static final String ARGUMENT_MINUTES = "ARGUMENT_MINUTES";
    public static final String LOGTAG = "\"DownloadService\"";

    private int result = Activity.RESULT_CANCELED;

    public DownloadService() {
        super("DownloadService");
    }

    // will be called asynchronously by Android
    @Override
    protected void onHandleIntent(Intent intent) {
        int minutes = intent.getIntExtra(ARGUMENT_MINUTES, -1);
        Log.d(LOGTAG, "onHandleIntent() -- minutes: " + minutes);

        try {
            setStatus(minutes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateNotification(getStatus(), 1611);

    }

    private void updateNotification(String relais_state, int seconds_until_switchoff_counter) {
        Log.d(LOGTAG, "updateNotification: " + relais_state + ", " + seconds_until_switchoff_counter);
        Intent intent = new Intent(this, ShowNotificationService.class);
        intent.putExtra(ShowNotificationService.ARGUMENT_RELAIS_STATE, relais_state);
        intent.putExtra(ShowNotificationService.ARGUMENT_SECONDS_UNTIL_SWITCHOFF_COUNTER, seconds_until_switchoff_counter);
        Log.d("updateNotification", "starting ShowNotificationService...");
        startService(intent);
    }

    public String getStatus() {
        try {
            long start = System.currentTimeMillis();
            URLConnection con = new URL(Configuration.target).openConnection();
            InputStream is = con.getInputStream();

            String response = readStream(is);
            Log.d(LOGTAG, "#############################");
            Log.d(LOGTAG, "# (took: " + (System.currentTimeMillis()-start) + "ms)");
            Log.d(LOGTAG, "# response: " + response);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // TODO: disconnect
        }

        return null;
    }

    private String readStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder buf = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            buf.append(line);
        }
        String response = buf.toString();
        return response;
    }

    private void setStatus(int remainingMinutes) throws Exception {
        long start = System.currentTimeMillis();
        URL url = new URL(Configuration.target);
        HttpURLConnection urlConnection = null;
        urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            //FIXME: DEBUG: writeStatusContent(2, remainingMinutes*60, out);
            writeStatusContent(2, remainingMinutes, out);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String response = readStream(in);
            Log.d(LOGTAG, "#############################");
            Log.d(LOGTAG, "# (took: " + (System.currentTimeMillis()-start) + "ms)");
            Log.d(LOGTAG, "# response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

    }

    private void writeStatusContent(int relais_state, int seconds_until_switchoff_counter, OutputStream out) throws IOException {
        // {"relais_state":0,"seconds_until_switchoff_counter":123}
        out.write(("{\"relais_state\":" + relais_state + ",\"seconds_until_switchoff_counter\":" + seconds_until_switchoff_counter + "}").getBytes());
        out.flush();
    }

}