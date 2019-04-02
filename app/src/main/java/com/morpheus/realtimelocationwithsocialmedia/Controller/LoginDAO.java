package com.morpheus.realtimelocationwithsocialmedia.Controller;

import android.content.Context;

import com.android.volley.Request;
import com.morpheus.realtimelocationwithsocialmedia.Model.RequestVolley;
import com.morpheus.realtimelocationwithsocialmedia.Model.Usuario;
import com.morpheus.realtimelocationwithsocialmedia.Model.Utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginDAO
{
    private Context context;
    private static LoginDAO dao;

    private LoginDAO(Context context)
    {
        this.context = context;
    }

    public static LoginDAO getInstance(Context context)
    {
        if(dao == null)
            dao = new LoginDAO(context);

        return dao;
    }

    //Obtiene de manera asincrona el objeto Usuario a partir de un WebService para el logeo manual
    public Request accessManual(String nick, String pass, final RequestVolley.OnResultElementListener<Usuario> listener)
    {
        String url = Constants.HOST + "login/getAccessManual.php";

        Map<String, String> parametros = new HashMap<>();
        parametros.put("nick", nick);
        parametros.put("pass", pass);

        RequestVolley.POST post = new RequestVolley.POST(context, url, parametros);
        return post.getResponse(new RequestVolley.OnRequestListener<String>()
        {
            @Override
            public void onSuccess(String result)
            {
                try
                {
                    JSONObject object = new JSONObject(result);
                    Usuario usuario = new Usuario(object.getInt("id"), object.getString("nombre"), object.getString("direccion"),
                            object.getString("correo"), object.getString("registro"));

                    listener.onSuccess(usuario);
                }catch (JSONException e)
                {
                    e.printStackTrace();
                    listener.onSuccess(null);
                }
            }

            @Override
            public void onFailed(String error, int responseCode)
            {
                listener.onFailed(error, responseCode);
            }
        }, "accessManual");
    }

    //Obtiene de manera asincrona el objeto Usuario  a partir de un WebService para el logeo por Social Media
    public Request accessSocialMedia(String correo, String metodo, final RequestVolley.OnResultElementListener<Usuario> listener)
    {
        String url = Constants.HOST + "login/getAccessSocial.php";

        Map<String, String> parametros = new HashMap<>();
        parametros.put("correo", correo);
        parametros.put("metodo", metodo);

        RequestVolley.POST post = new RequestVolley.POST(context, url, parametros);
        return post.getResponse(new RequestVolley.OnRequestListener<String>()
        {
            @Override
            public void onSuccess(String result)
            {
                try
                {
                    JSONObject object = new JSONObject(result);
                    Usuario usuario = new Usuario(object.getInt("id"), object.getString("nombre"), object.getString("direccion"),
                            object.getString("correo"), object.getString("registro"));

                    listener.onSuccess(usuario);
                }catch (JSONException e)
                {
                    e.printStackTrace();
                    listener.onSuccess(null);
                }
            }

            @Override
            public void onFailed(String error, int responseCode)
            {
                listener.onFailed(error, responseCode);
            }
        }, "accessSocialMedia");
    }
}
