package com.morpheus.realtimelocationwithsocialmedia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.morpheus.realtimelocationwithsocialmedia.Model.Usuario;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Usuario usuario = getIntent().getParcelableExtra("USUARIO");
        ((TextView)findViewById(R.id.txtCuenta)).setText(usuario.getNombre());
    }
}
