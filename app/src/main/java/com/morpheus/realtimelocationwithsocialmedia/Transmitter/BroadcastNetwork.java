package com.morpheus.realtimelocationwithsocialmedia.Transmitter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class BroadcastNetwork extends BroadcastReceiver
{
    public static final String NETWORK_AVAILABLE_ACTION = "com.morpheus.realtimelocationwithsocialmedia.NetworkAvailable";
    public static final String IS_NETWORK_AVAILABLE = "isNetworkAvailable";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //Guarda en un intent el estado de la conexion
        Intent networkStateIntent = new Intent(NETWORK_AVAILABLE_ACTION);
        networkStateIntent.putExtra(IS_NETWORK_AVAILABLE, isConnectedToInternet(context));
    }

    //Checa si hay una conexion
    private boolean isConnectedToInternet(Context context)
    {
        try
        {
            if(context != null)
            {
                ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }

            return false;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
