package com.example.bitacora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

public class Utils {

    public static void crearToastPersonalizado(Context context, String mensaje) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_con_logo, null);

        ImageView logoImage = layout.findViewById(R.id.logo_image);
        TextView toastText = layout.findViewById(R.id.toast_text);
        logoImage.setImageResource(R.drawable.logo);
        toastText.setText(mensaje);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


    public static CardView ModalRedondeado(Context contextDialog, View viewModal) {

        CardView cardView = new CardView(contextDialog);
        cardView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        cardView.setBackgroundResource(R.drawable.roundedbackground_nombre_actividad);
        cardView.addView(viewModal);
        return cardView;
    }


}
