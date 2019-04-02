package com.morpheus.realtimelocationwithsocialmedia.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Usuario implements Parcelable
{
    private Integer id;
    private String nombre;
    private String direccion;
    private String correo;
    private String fecha;

    public Usuario(Integer id, String nombre, String direccion, String correo, String fecha)
    {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.correo = correo;
        this.fecha = fecha;
    }

    public Usuario(String nombre, String direccion, String correo)
    {
        this.nombre = nombre;
        this.direccion = direccion;
        this.correo = correo;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getDireccion()
    {
        return direccion;
    }

    public void setDireccion(String direccion)
    {
        this.direccion = direccion;
    }

    public String getCorreo()
    {
        return correo;
    }

    public void setCorreo(String correo)
    {
        this.correo = correo;
    }

    public String getFecha()
    {
        return fecha;
    }

    public void setFecha(String fecha)
    {
        this.fecha = fecha;
    }

    @NonNull
    @Override
    public String toString()
    {
        return String.format(Locale.getDefault(), "ID: %d, Nombre: %s, Dirección: %s, Correo: %s, Fecha: %d/%d/%d", getId(), getNombre(),
                getDireccion(), getCorreo(), getFecha());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeValue(this.id);
        dest.writeString(this.nombre);
        dest.writeString(this.direccion);
        dest.writeString(this.correo);
        dest.writeString(this.fecha);
    }

    protected Usuario(Parcel in)
    {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.nombre = in.readString();
        this.direccion = in.readString();
        this.correo = in.readString();
        this.fecha = in.readString();
    }

    public static final Parcelable.Creator<Usuario> CREATOR = new Parcelable.Creator<Usuario>()
    {
        @Override
        public Usuario createFromParcel(Parcel source)
        {
            return new Usuario(source);
        }

        @Override
        public Usuario[] newArray(int size)
        {
            return new Usuario[size];
        }
    };
}
