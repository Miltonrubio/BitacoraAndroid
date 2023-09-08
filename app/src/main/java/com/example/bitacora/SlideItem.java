package com.example.bitacora;

public class SlideItem {

    private String image;

    private String  id_ser_venta;

    public SlideItem(String image, String id_ser_venta) {
        this.image = image;
        this.id_ser_venta = id_ser_venta;
    }

    public String getId_ser_venta() {
        return id_ser_venta;
    }

    public void setId_ser_venta(String id_ser_venta) {
        this.id_ser_venta = id_ser_venta;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String toString() {
        return image; // O cualquier otro atributo que quieras mostrar en el Toast
    }



}


