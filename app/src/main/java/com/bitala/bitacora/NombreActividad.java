package com.bitala.bitacora;
public class NombreActividad {
    private String ID_nombre_actividad;
    private String nombre_actividad;

    public NombreActividad(String ID_nombre_actividad, String nombre_actividad) {
        this.ID_nombre_actividad = ID_nombre_actividad;
        this.nombre_actividad = nombre_actividad;
    }

    public String getID_nombre_actividad() {
        return ID_nombre_actividad;
    }

    public void setID_nombre_actividad(String ID_nombre_actividad) {
        this.ID_nombre_actividad = ID_nombre_actividad;
    }

    public String getNombre_actividad() {
        return nombre_actividad;
    }

    public void setNombre_actividad(String nombre_actividad) {
        this.nombre_actividad = nombre_actividad;
    }
}
