package com.example.bitacora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.bitacora.databinding.ActivityBindingBinding;
import com.example.bitacora.UsuarioFragment;

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

        if ("SUPERADMIN".equals(permisosUsuario)) {

            replaceFragment(new CrudUsuariosFragment());
            getMenuInflater().inflate(R.menu.menu_admin, binding.bottomNavigationView.getMenu());

            binding.bottomNavigationView.setOnItemSelectedListener(item -> {
                switch (item.getItemId()) {
                    case (R.id.menu_admin_usuarios):
                        replaceFragment(new UsuarioFragment());
                        break;
                    case (R.id.menu_admin_agregar_usuarios):
                        replaceFragment(new CrudUsuariosFragment());
                        break;
                    case (R.id.menuadmin_actividades):
                        replaceFragment(new ActividadesFragment());
                        break;
                }
                return true;
            });

        } else {
            replaceFragment(new HomeFragment());
            getMenuInflater().inflate(R.menu.menu, binding.bottomNavigationView.getMenu());

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


    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layouts_fragments, fragment);
        fragmentTransaction.commit();
    }

}