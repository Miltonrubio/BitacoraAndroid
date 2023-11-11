package com.bitala.bitacora;

public class SlideItem {

    private String nombreFoto;

    public SlideItem(String image) {
        this.nombreFoto = image;
    }

    public String getImage() {
        return nombreFoto;
    }

    public void setImage(String image) {
        this.nombreFoto = image;
    }
}