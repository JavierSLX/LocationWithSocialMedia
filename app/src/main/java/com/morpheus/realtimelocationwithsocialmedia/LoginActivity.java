package com.morpheus.realtimelocationwithsocialmedia;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
    private EditText edtUser, edtPass;
    private CheckBox chDatos;
    private Button btEntrar;
    private static final int REQUEST_CODE = 1;
    private GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SignInButton btGoogle = findViewById(R.id.btGoogle);

        //Configuracion visual del boton
        btGoogle.setSize(SignInButton.SIZE_WIDE);
        btGoogle.setColorScheme(SignInButton.COLOR_DARK);

        //Obtiene las opciones de Google
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        //Arma el api de Google
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();

        //Evento del boton
        btGoogle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            //Si se logeó correctamente
            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                Toast.makeText(this, account.getEmail() != null ? account.getEmail() : "Sin correo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Cuando fracasa la conexión con Google
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(this, "Error al conectar con Google", Toast.LENGTH_SHORT).show();
    }
}
