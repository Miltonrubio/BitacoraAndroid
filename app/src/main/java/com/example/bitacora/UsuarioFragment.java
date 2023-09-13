package com.example.bitacora;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bitacora.MainActivity;
import com.example.bitacora.R;
import com.google.android.material.textfield.TextInputLayout;

public class UsuarioFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UsuarioFragment() {
        // Required empty public constructor
    }

    public static UsuarioFragment newInstance(String param1, String param2) {
        UsuarioFragment fragment = new UsuarioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);

        TextView tvNombreMecanico = view.findViewById(R.id.tvNombreMecanico);
        TextView tvCorreoMecanico = view.findViewById(R.id.tvCorreoMecanico);
        TextView tvEstadoMecanico = view.findViewById(R.id.tvEstadoMecanico);

        Button customButton= view.findViewById(R.id.customButton);

        Button customButtonMandarAPrueba= view.findViewById(R.id.customButtonMandarAPrueba);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);

        String ID_usuario = sharedPreferences.getString("ID_usuario", "");
        String nombre = sharedPreferences.getString("nombre", "");
        String estado = sharedPreferences.getString("estado", "");
        String correo = sharedPreferences.getString("correo", "");
        String permisos = sharedPreferences.getString("permisos", "");


        tvEstadoMecanico.setText(estado);
        tvCorreoMecanico.setText(correo);
        tvNombreMecanico.setText( ID_usuario + " - " +nombre);


        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              cerrarSesion();
            }
        });

        customButtonMandarAPrueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IrAPrueba();
            }
        });
        return view;
    }

    private void cerrarSesion() {
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

    public  void IrAPrueba(){
        Intent intent = new Intent(requireContext(), PruebaActivity.class);
        startActivity(intent);
    }

}