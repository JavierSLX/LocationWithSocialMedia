package com.morpheus.realtimelocationwithsocialmedia.Model.Internet;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.morpheus.realtimelocationwithsocialmedia.Transmitter.BroadcastNetwork;

public class ChequeoRed
{
    private static ChequeoRed chequeoRed;
    private Context context;

    private ChequeoRed(Context context)
    {
        this.context = context;
    }

    public static ChequeoRed getInstance(Context context)
    {
        if(chequeoRed != null)
            chequeoRed = new ChequeoRed(context);

        return chequeoRed;
    }

    public void chequeoInternet()
    {
        //Manda llamar el broadcast con una intencion y carga los elementos que le regresa
        IntentFilter intentFilter = new IntentFilter(BroadcastNetwork.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                boolean isNetworkAvailable = intent.getBooleanExtra(BroadcastNetwork.IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "Conectado" : "Desconectado";

                //Da aviso que no hay internet
                //Snackbar.make(view, "Estado conexión: " + networkStatus, Snackbar.LENGTH_LONG).show();
                Toast.makeText(context, "Estado conexión: " + networkStatus, Toast.LENGTH_SHORT).show();
            }
        }, intentFilter);
    }
}
