package com.morpheus.realtimelocationwithsocialmedia;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class AppControler extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
    }
}
