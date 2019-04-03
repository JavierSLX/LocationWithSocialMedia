package com.morpheus.realtimelocationwithsocialmedia.Model.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.morpheus.realtimelocationwithsocialmedia.Model.Usuario;

public class Preferences
{
    private static final String USUARIO_NICK = "USUARIO_NICK";
    private static final String USUARIO_PASS = "USUARIO_PASS";
    private static final String USUARIO = "USUARIO";
    private Context context;
    private static Preferences preferences;

    private Preferences(Context context)
    {
        this.context = context;
    }

    public static Preferences getInstance(Context context)
    {
        if(preferences == null)
            preferences = new Preferences(context);

        return preferences;
    }

    private SharedPreferences getSettings()
    {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getUser()
    {
        return getSettings().getString(USUARIO_NICK, null);
    }
    public String getPass()
    {
        return getSettings().getString(USUARIO_PASS, null);
    }

    public Usuario getUsuario()
    {
        String json = getSettings().getString(USUARIO, null);
        Usuario usuario = null;
        if(json != null)
        {
            Gson gson = new Gson();
            usuario = gson.fromJson(json, Usuario.class);
        }

        return usuario;
    }

    public void setValuesUserCredencials(String user, String pass)
    {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(USUARIO_NICK, user);
        editor.putString(USUARIO_PASS, pass);
        editor.apply();
    }

    public void setValueUsuario(Usuario usuario)
    {
        SharedPreferences.Editor editor = getSettings().edit();
        Gson gson = new Gson();
        String json = gson.toJson(usuario);
        editor.putString(USUARIO, json);
        editor.apply();
    }
}
