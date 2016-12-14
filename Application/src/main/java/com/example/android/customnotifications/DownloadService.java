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
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadService extends IntentService {

    public static final String ARGUMENT_UPDATE_ONLY = "ARGUMENT_UPDATE_ONLY";
    public static final String ARGUMENT_MINUTES = "ARGUMENT_MINUTES";
    public static final String ARGUMENT_SWITCH_ON = "ARGUMENT_SWITCH_ON";
    public static final String ARGUMENT_SWITCH_OFF = "ARGUMENT_SWITCH_OFF";
    public static final String LOGTAG = "\"DownloadService\"";

    public DownloadService() {
        super("DownloadService");
    }

    // will be called asynchronously by Android
    @Override
    protected void onHandleIntent(Intent intent) {
        boolean updateOnly = intent.getBooleanExtra(ARGUMENT_UPDATE_ONLY, false);
        boolean switchOn = intent.getBooleanExtra(ARGUMENT_SWITCH_ON, false);
        boolean switchOff = intent.getBooleanExtra(ARGUMENT_SWITCH_OFF, false);

        int minutes = intent.getIntExtra(ARGUMENT_MINUTES, -1);
        if (!updateOnly && minutes > -1) {
            try {
                setStatus(RelaisState.TIMER, minutes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(switchOn) {
            setStatus(RelaisState.ON, 0);
        }
        if(switchOff) {
            setStatus(RelaisState.OFF, 0);
        }

        Status status = getStatus();
        if(status!=null) {
            updateNotification(status.relais_state, status.seconds_until_switchoff_counter);
        } else {
            updateNotification(-1, -1);
        }
    }

    private void updateNotification(int relais_state, int seconds_until_switchoff_counter) {
        Log.d(LOGTAG, "updateNotification: " + relais_state + ", " + seconds_until_switchoff_counter);
        Intent intent = new Intent(this, ShowNotificationService.class);
        intent.putExtra(ShowNotificationService.ARGUMENT_RELAIS_STATE, relais_state);
        intent.putExtra(ShowNotificationService.ARGUMENT_SECONDS_UNTIL_SWITCHOFF_COUNTER, seconds_until_switchoff_counter);
        Log.d("updateNotification", "starting ShowNotificationService...");
        startService(intent);
    }

    private Status getStatus() {
        long start = System.currentTimeMillis();
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(Configuration.TARGET_URL).openConnection();
            urlConnection.setConnectTimeout(Configuration.REQUEST_TIMEOUT_MILLIS);
            urlConnection.setReadTimeout(Configuration.REQUEST_TIMEOUT_MILLIS);
            try {
                InputStream is = urlConnection.getInputStream();
                String response = readStream(is);
                Log.d(LOGTAG, "#############################");
                Log.d(LOGTAG, "# (took: " + (System.currentTimeMillis() - start) + "ms)");
                Log.d(LOGTAG, "# response: " + response);
                return new Status(response);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e(LOGTAG, "Error getting status: " + e.getMessage());
        }

        return null;
    }

    private Status setStatus(RelaisState relais_state, int remainingMinutes) {
        try {
            long start = System.currentTimeMillis();
            URL url = new URL(Configuration.TARGET_URL);
            HttpURLConnection urlConnection = null;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(Configuration.REQUEST_TIMEOUT_MILLIS);
            urlConnection.setReadTimeout(Configuration.REQUEST_TIMEOUT_MILLIS);
            try {
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                writeStatusContent(relais_state.ordinal(), remainingMinutes * 60, out);

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = readStream(in);
                Log.d(LOGTAG, "#############################");
                Log.d(LOGTAG, "# (took: " + (System.currentTimeMillis() - start) + "ms)");
                Log.d(LOGTAG, "# response: " + response);

                return new Status(response);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e(LOGTAG, "Error setting status: " + e.getMessage());
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

    private void writeStatusContent(int relais_state, int seconds_until_switchoff_counter, OutputStream out) throws IOException {
        // {"relais_state":0,"seconds_until_switchoff_counter":123}
        out.write(("{\"relais_state\":" + relais_state + ",\"seconds_until_switchoff_counter\":" + seconds_until_switchoff_counter + "}").getBytes());
        out.flush();
    }

    public static class Status {
        int relais_state;
        int seconds_until_switchoff_counter;
        public Status(String json) {
            Pattern pattern_relais_state = Pattern.compile(".*\"relais_state\":(\\d+)[,}].*");
            Pattern pattern_counter = Pattern.compile(".*\"seconds_until_switchoff_counter\":(\\d+)[,}].*");

            Matcher matcher_relais_state = pattern_relais_state.matcher(json);
            Matcher matcher_counter = pattern_counter.matcher(json);

            boolean match_relais_state = matcher_relais_state.matches();
            boolean match_counter = matcher_counter.matches();

            if (match_relais_state && match_counter) {
                relais_state = Integer.valueOf(matcher_relais_state.group(1));
                seconds_until_switchoff_counter = Integer.valueOf(matcher_counter.group(1));
            } else {
                throw new IllegalArgumentException("Invalid input");
            }
        }
    }

    private enum RelaisState {
        OFF,
        ON,
        TIMER
    }

}