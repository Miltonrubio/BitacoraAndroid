package com.example.bitacora;

public class Actividades {


    String descripcionActividad,fecha_inicio,fecha_fin, estadoActividad, ID_usuario, nombre_actividad, nombre, telefono;


    public Actividades(String descripcionActividad, String fecha_inicio, String fecha_fin, String estadoActividad, String ID_usuario, String nombre_actividad, String nombre, String telefono) {
        this.descripcionActividad = descripcionActividad;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.estadoActividad = estadoActividad;
        this.ID_usuario = ID_usuario;
        this.nombre_actividad = nombre_actividad;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getDescripcionActividad() {
        return descripcionActividad;
    }

    public void setDescripcionActividad(String descripcionActividad) {
        this.descripcionActividad = descripcionActividad;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public String getEstadoActividad() {
        return estadoActividad;
    }

    public void setEstadoActividad(String estadoActividad) {
        this.estadoActividad = estadoActividad;
    }

    public String getID_usuario() {
        return ID_usuario;
    }

    public void setID_usuario(String ID_usuario) {
        this.ID_usuario = ID_usuario;
    }

    public String getNombre_actividad() {
        return nombre_actividad;
    }

    public void setNombre_actividad(String nombre_actividad) {
        this.nombre_actividad = nombre_actividad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
