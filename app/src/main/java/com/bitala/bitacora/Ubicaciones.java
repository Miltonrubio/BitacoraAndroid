package com.bitala.bitacora;


public class Ubicaciones {

    Double latitud_inicio, longitud_inicio, latitud_destino, longitud_destino;

    String titulo;

    public Ubicaciones(Double latitud_inicio, Double longitud_inicio, String titulo) {
        this.latitud_inicio = latitud_inicio;
        this.longitud_inicio = longitud_inicio;
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getLatitud_inicio() {
        return latitud_inicio;
    }

    public void setLatitud_inicio(Double latitud_inicio) {
        this.latitud_inicio = latitud_inicio;
    }

    public Double getLongitud_inicio() {
        return longitud_inicio;
    }

    public void setLongitud_inicio(Double longitud_inicio) {
        this.longitud_inicio = longitud_inicio;
    }

}
