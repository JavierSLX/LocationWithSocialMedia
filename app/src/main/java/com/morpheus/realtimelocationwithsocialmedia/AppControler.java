package com.morpheus.realtimelocationwithsocialmedia;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.morpheus.realtimelocationwithsocialmedia.Transmitter.BroadcastNetwork;

public class AppControler extends Application
{
    private static final String WIFI_STATE_CHANGE_ACTION = "android.net.wifi.WIFI_STATE_CHANGED";

    @Override
    public void onCreate()
    {
        super.onCreate();

        //Configuración inicial de Facebook
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);

        //Configuracion inicial del broadcast de conexion
        registerForNetworkChangeEvents(this);
    }

    public static void registerForNetworkChangeEvents(Context context)
    {
        BroadcastNetwork broadcastNetwork = new BroadcastNetwork();
        context.registerReceiver(broadcastNetwork, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        context.registerReceiver(broadcastNetwork, new IntentFilter(WIFI_STATE_CHANGE_ACTION));
    }
}
