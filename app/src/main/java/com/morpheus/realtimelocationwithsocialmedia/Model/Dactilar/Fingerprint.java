package com.morpheus.realtimelocationwithsocialmedia.Model.Dactilar;

import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.morpheus.realtimelocationwithsocialmedia.Model.Usuario;
import com.morpheus.realtimelocationwithsocialmedia.Model.Utils.Constants;
import com.morpheus.realtimelocationwithsocialmedia.Model.Utils.Preferences;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

@RequiresApi(Build.VERSION_CODES.M)
public class Fingerprint
{
    private Context context;
    private Intent intent;
    private AppCompatActivity activity;
    private KeyStore keyStore;
    private Cipher cipher;
    private static Fingerprint fingerprint;

    private Fingerprint(Context context, Intent intent)
    {
        this.context = context;
        this.intent = intent;
        this.activity = ((AppCompatActivity)context);
    }

    public static Fingerprint getInstance(Context context, Intent intent)
    {
        if(fingerprint == null)
            fingerprint = new Fingerprint(context, intent);

        return fingerprint;
    }

    public CancellationSignal reconocimientoDactilar()
    {
        //Se inicializa los Manager
        KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(context.KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager)context.getSystemService(context.FINGERPRINT_SERVICE);

        //Checa si tiene el sensor de huella instalado
        if(fingerprintManager.isHardwareDetected())
        {
            if(ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.USE_FINGERPRINT}, 1);

            //Checa si hay una huella registrada
            if(!fingerprintManager.hasEnrolledFingerprints())
                dialogoConfiguracionSeguridad("Registra una huella en Ajustes");
            else if(!keyguardManager.isKeyguardSecure())
                dialogoConfiguracionSeguridad("No está habilitado Lock Screen en Ajustes");
            else
            {
                //Genera la llave
                generateKey();

                //Verifica que la llave haya sido correcta
                if(cipherInit())
                {
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    Handler handler = new Handler(context, intent);
                    handler.startAuth(fingerprintManager, cryptoObject);

                    //Obtiene la señal para cancelar el escaneo de huella
                    return handler.getSignal();
                }
            }
        }

        return null;
    }

    //Hace la comprobacion de la llave
    private boolean cipherInit()
    {
        try
        {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        }catch (NoSuchAlgorithmException | NoSuchPaddingException e)
        {
            throw new RuntimeException("Fracaso al ordenar Cipher", e);
        }

        try
        {
            keyStore.load(null);
            SecretKey key = (SecretKey)keyStore.getKey(Constants.KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return true;
        }catch (KeyPermanentlyInvalidatedException e)
        {
            return false;
        }catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException | UnrecoverableKeyException | InvalidKeyException e)
        {
            throw new RuntimeException("Fracaso al iniciar Cipher");
        }
    }

    private void dialogoConfiguracionSeguridad(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Huella Dactilar");
        builder.setMessage(mensaje);
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                activity.startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS), 0);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    //Genera la llave dactilar
    private void generateKey()
    {
        try
        {
            keyStore = KeyStore.getInstance(Constants.DACTILAR_KEY);
        }catch (KeyStoreException e)
        {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator;
        try
        {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, Constants.DACTILAR_KEY);
        }catch (NoSuchAlgorithmException | NoSuchProviderException e)
        {
            throw new RuntimeException("Fracaso al generar la instancia KeyGenerator", e);
        }

        try
        {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(Constants.KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC).setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build());

            keyGenerator.generateKey();
        }catch (NoSuchAlgorithmException | CertificateException | IOException | InvalidAlgorithmParameterException e)
        {
            throw new RuntimeException(e);
        }
    }
}
