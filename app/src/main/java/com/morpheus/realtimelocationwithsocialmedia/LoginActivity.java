package com.morpheus.realtimelocationwithsocialmedia;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.morpheus.realtimelocationwithsocialmedia.Controller.LoginDAO;
import com.morpheus.realtimelocationwithsocialmedia.Model.Dactilar.Fingerprint;
import com.morpheus.realtimelocationwithsocialmedia.Model.Internet.ChequeoRed;
import com.morpheus.realtimelocationwithsocialmedia.Model.Utils.Preferences;
import com.morpheus.realtimelocationwithsocialmedia.Model.Utils.RequestVolley;
import com.morpheus.realtimelocationwithsocialmedia.Model.Usuario;
import com.morpheus.realtimelocationwithsocialmedia.Transmitter.BroadcastNetwork;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, FacebookCallback<LoginResult>
{
    private EditText edtUser, edtPass;
    private CheckBox chkDatos;
    private static final int REQUEST_CODE = 1;
    private GoogleApiClient apiClient;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private ProgressDialog progressDialog;
    private LoginDAO dao;
    private Preferences preferences;
    private CancellationSignal signal;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        chkDatos = findViewById(R.id.chkDatos);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Cargando");
        progressDialog.setMessage("Un momento por favor...");
        progressDialog.setCanceledOnTouchOutside(false);

        dao = LoginDAO.getInstance(this);
        preferences = Preferences.getInstance(this);

        //Checa si hay datos guardados por default
        if(preferences.getUser() != null && preferences.getPass() != null)
        {
            edtUser.setText(preferences.getUser());
            edtPass.setText(preferences.getPass());
        }

        //Activa el escaner de huella digital
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Intent intent = new Intent(this, MainActivity.class);
            signal = Fingerprint.getInstance(this, intent).reconocimientoDactilar();
        }

        //______________________________ GOOGLE
        loginWithGoogle();

        //_________________________________ FACEBOOK
        loginWithFacebook();

        //Eventos
        findViewById(R.id.btEntrar).setOnClickListener(clickButtonEntrar);

        //Chequeo de internet (NO FUNCIONA)
        //ChequeoRed.getInstance(this).chequeoInternet();
        chequeoInternet();
    }

    public void chequeoInternet()
    {
        //Manda llamar el broadcast con una intencion y carga los elementos que le regresa
        IntentFilter intentFilter = new IntentFilter(BroadcastNetwork.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver()
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

    @Override
    protected void onResume()
    {
        super.onResume();

        profileTracker.startTracking();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        profileTracker.stopTracking();
    }

    //Objeto de evento del botón
    View.OnClickListener clickButtonEntrar = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            String nick = edtUser.getText().toString().trim();
            String pass = edtPass.getText().toString().trim();

            if(nick.length() > 0 && pass.length() > 0)
                loginManual(nick, pass);
            else
                Toast.makeText(LoginActivity.this, "Llene todos los campos por favor", Toast.LENGTH_SHORT).show();
        }
    };

    //Método que permite el logeo por cuenta de Google
    private void loginWithGoogle()
    {
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

    //Método que permite el logeo por cuenta de Facebook
    private void loginWithFacebook()
    {
        LoginButton btFacebook = findViewById(R.id.btFacebook);

        //Si hay un logeo con Facebook anterior, lo cierra
        if(AccessToken.getCurrentAccessToken() != null)
            LoginManager.getInstance().logOut();

        //Crea el manejador de peticiones y la configuracion del botón
        callbackManager = CallbackManager.Factory.create();
        btFacebook.setReadPermissions("public_profile", "email");
        btFacebook.registerCallback(callbackManager, this);

        //Arma el perfil si se logeo correctamente el usuario
        profileTracker = new ProfileTracker()
        {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile)
            {
                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        try
                        {
                            String cuenta = object.getString("email") != null ? object.getString("email") : object.getString("name");

                            loginCorrectSocial(cuenta, "Facebook");
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error al obtener info de Facebook", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Bundle params = new Bundle();
                params.putString("fields", "id,name,link,email");

                request.setParameters(params);
                request.executeAsync();
            }
        };
    }

    //Metodo que permite el logeo manual
    private void loginManual(final String nick, final String pass)
    {
        progressDialog.show();
        dao.accessManual(nick, pass, new RequestVolley.OnResultElementListener<Usuario>()
        {
            @Override
            public void onSuccess(Usuario result)
            {
                progressDialog.dismiss();
                if(result != null)
                {
                    //Guarda las credenciales
                    if(chkDatos.isChecked())
                        preferences.setValuesUserCredencials(nick, pass);

                    //Guarda el usuario (necesario por un login dactilar futuro)
                    preferences.setValueUsuario(result);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("USUARIO", result);
                    intent.putExtras(bundle);

                    startActivity(intent);
                    signal.cancel();
                    finish();
                }
                else
                    Toast.makeText(LoginActivity.this, "Usuario/Contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(String error, int responseCode)
            {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, error + " " + responseCode, Toast.LENGTH_SHORT).show();
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
                String cuenta = account.getEmail() != null ? account.getEmail() : "Sin correo";

                loginCorrectSocial(cuenta, "Google");
            }
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //Metodo que cambia de actividad cuando se logea correctamente
    private void loginCorrectSocial(String cuenta, String metodo)
    {
        compareWithBD(cuenta, metodo);
    }

    //Metodo que checa si existe la cuenta en la BD cuando viene por social media
    private void compareWithBD(final String cuenta, String metodo)
    {
        progressDialog.show();
        dao.accessSocialMedia(cuenta, metodo, new RequestVolley.OnResultElementListener<Usuario>()
        {
            @Override
            public void onSuccess(Usuario result)
            {
                progressDialog.dismiss();
                if(result != null)
                {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("USUARIO", result);
                    intent.putExtras(bundle);

                    startActivity(intent);
                    signal.cancel();
                    finish();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Usuario no registrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(String error, int responseCode)
            {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, error + " " + responseCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Cuando fracasa la conexión con Google
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(this, "Error al conectar con Google", Toast.LENGTH_SHORT).show();
    }

    //Inicio de sesion correcto con Facebook
    @Override
    public void onSuccess(LoginResult loginResult)
    {
        Log.i(LoginActivity.class.getSimpleName(), "Inicio de sesión correcto");
    }

    //Inicio cancelado con Facebook
    @Override
    public void onCancel()
    {
        Toast.makeText(this, "Se ha cancelado el inicio de sesión en Facebook", Toast.LENGTH_SHORT).show();
    }

    //Error de inicio de sesion con Facebook
    @Override
    public void onError(FacebookException error)
    {
        Toast.makeText(this, "Error al iniciar sesión con Facebook", Toast.LENGTH_SHORT).show();
    }
}
