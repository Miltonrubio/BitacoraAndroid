package com.bitala.bitacora;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bitala.bitacora.R;

public class UsuarioFragment extends Fragment {

    public UsuarioFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);


        ConstraintLayout ContenedorCompleto= view.findViewById(R.id.ContenedorCompleto);
        TextView textNombreUsuario = view.findViewById(R.id.textNombreUsuario);
        TextView textRol = view.findViewById(R.id.textRol);
        TextView textTelefonoUsuario = view.findViewById(R.id.textTelefonoUsuario);
        ImageView ImagenSesionIniciada = view.findViewById(R.id.iconImageView);
context=requireContext();

        Button customButton = view.findViewById(R.id.cerrarSesion);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);

        String ID_usuario = sharedPreferences.getString("ID_usuario", "");
        String nombre = sharedPreferences.getString("nombre", "");
        String estado = sharedPreferences.getString("estado", "");
        String correo = sharedPreferences.getString("correo", "");
        String clave = sharedPreferences.getString("clave", "");
        String telefono = sharedPreferences.getString("telefono", "");
        String permisos = sharedPreferences.getString("permisos", "");


        String image = "http://hidalgo.no-ip.info:5610/bitacora/fotos/fotos_usuarios/fotoperfilusuario" + ID_usuario + ".jpg";

        Glide.with(context)
                .load(image)
                .skipMemoryCache(true) // Desactiva la caché en memoria
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Desactiva la caché en disco
                .placeholder(R.drawable.imagendefault)
                .error(R.drawable.imagendefault)
                .into(ImagenSesionIniciada);


        int colorRes;
        Drawable drawable;
        if (permisos.equalsIgnoreCase("SUPERADMIN") || permisos.equalsIgnoreCase("OFICINISTA")  ) {

            drawable = ContextCompat.getDrawable(context, R.color.vino);
            colorRes = R.color.vino;
        } else {

            drawable = ContextCompat.getDrawable(context, R.color.naranjita);
            colorRes = R.color.naranjita;
        }

        ContenedorCompleto.setBackground(drawable);
       // int color = ContextCompat.getColor(context, colorRes);
        //TextNombreDeActividad.setTextColor(color);


        textRol.setText(permisos);
        textTelefonoUsuario.setText("Telefono: " + telefono);
        textNombreUsuario.setText(nombre);

        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        return view;
    }

    private void cerrarSesion() {

        if (isAdded()) {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("correo");
            editor.remove("clave");
            editor.remove("rememberMe");
            editor.apply();

            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
    }

}