package com.morpheus.realtimelocationwithsocialmedia.Model.Dactilar;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.morpheus.realtimelocationwithsocialmedia.Model.Usuario;
import com.morpheus.realtimelocationwithsocialmedia.Model.Utils.Preferences;

@TargetApi(Build.VERSION_CODES.M)
public class Handler extends FingerprintManager.AuthenticationCallback
{
    private Context context;
    private Intent intent;
    private CancellationSignal signal;

    public Handler(Context context, Intent intent)
    {
        this.context = context;
        this.intent = intent;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject)
    {
        signal = new CancellationSignal();
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED)
            return;

        manager.authenticate(cryptoObject, signal, 0, this, null);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result)
    {
        super.onAuthenticationSucceeded(result);

        Usuario usuario = Preferences.getInstance(context).getUsuario();
        if(usuario != null)
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable("USUARIO", usuario);
            intent.putExtras(bundle);
            context.startActivity(intent);

            //Cancela la lectura del lector de huella
            signal.cancel();
        }
        else
            Toast.makeText(context, "Haga otro login antes de usar la huella dactilar", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString)
    {
        super.onAuthenticationHelp(helpCode, helpString);
        Toast.makeText(context, "Ayuda al autenticar la huella", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationFailed()
    {
        super.onAuthenticationFailed();
        Toast.makeText(context, "Huella no registrada", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString)
    {
        super.onAuthenticationError(errorCode, errString);
        Toast.makeText(context, "Error al autenticar la huella", Toast.LENGTH_SHORT).show();
    }

    public CancellationSignal getSignal()
    {
        return signal;
    }
}
