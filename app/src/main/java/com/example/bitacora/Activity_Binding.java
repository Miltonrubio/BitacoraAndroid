package com.example.bitacora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.bitacora.databinding.ActivityBindingBinding;
import com.example.validacion.UsuarioFragment;

public class Activity_Binding extends AppCompatActivity {

    ActivityBindingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);

        SharedPreferences sharedPreferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);

        String permisosUsuario = sharedPreferences.getString("permisos", "");

        binding = ActivityBindingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


            replaceFragment(new HomeFragment());
            binding.bottomNavigationView.setOnItemSelectedListener(item -> {
                switch (item.getItemId()) {

                    case (R.id.menu_home):
                        replaceFragment(new HomeFragment());
                        break;
                    case (R.id.menu_usuario):
                        replaceFragment(new UsuarioFragment());
                        break;
                }
                return true;
            });


    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layoutCoches, fragment);
        fragmentTransaction.commit();

    }

}