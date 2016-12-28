package com.example.android.customnotifications;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MDNSScanService extends IntentService {

    public static final String ARGUMENT_BOOL_1 = "ARGUMENT_1";
    public static final String LOGTAG = "MDNSScanService";

    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private NsdManager mNsdManager;

    public MDNSScanService() {
        super("MDNSScanService");
    }

    // will be called asynchronously by Android
    @Override
    protected void onHandleIntent(Intent intent) {
        boolean arg1 = intent.getBooleanExtra(ARGUMENT_BOOL_1, false);
        initializeDiscoveryListener();
        initializeResolveListener();

        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);

        /* Working:
         *   --> _http._tcp
         *   --> _http._tcp.
         * NOT working:
         *   --> _http._tcp.local
         */
        String service = "_http._tcp";
        mNsdManager.discoverServices(service, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    private void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Log.d(LOGTAG, "onResolveFailed");
            }

            @Override
            public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
                Log.d(LOGTAG, "onServiceResolved");
                Log.d(LOGTAG, "  .getServiceName() : " + nsdServiceInfo.getServiceName());
                Log.d(LOGTAG, "  .getServiceType() : " + nsdServiceInfo.getServiceType());
                Log.d(LOGTAG, "  .getHost() : " + nsdServiceInfo.getHost());
                Log.d(LOGTAG, "  .getPort() : " + nsdServiceInfo.getPort());
                Log.d(LOGTAG, "  .getAttributes() : ");
                if(nsdServiceInfo.getAttributes()==null || nsdServiceInfo.getAttributes().size()<1 ) {
                    Log.d(LOGTAG, "  --> NONE!");
                } else {
                    for (Map.Entry<String, byte[]> pair : nsdServiceInfo.getAttributes().entrySet()) {
                        Log.d(LOGTAG, "  --> " + pair.getKey() + ":" + new String(pair.getValue()));
                    }
                }
            }
        };
    }

    public void initializeDiscoveryListener() {
        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(LOGTAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                Log.d(LOGTAG, "Service discovery success" + service);

                mNsdManager.resolveService(service, mResolveListener);
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(LOGTAG, "service lost" + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(LOGTAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(LOGTAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(LOGTAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

}