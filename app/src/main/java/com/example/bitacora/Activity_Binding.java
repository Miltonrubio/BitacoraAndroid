package com.example.bitacora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.bitacora.databinding.ActivityBindingBinding;


public class Activity_Binding extends AppCompatActivity {

    ActivityBindingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);

        binding = ActivityBindingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);

        String permisosUsuario = sharedPreferences.getString("permisos", "");

        setupMenu(permisosUsuario);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.menu_home):
                    replaceFragment(new HomeFragment());
                    break;
                case (R.id.menu_usuario):
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


        if (!"SUPERADMIN".equals(permisosUsuario)) {
            binding.bottomNavigationView.setSelectedItemId(R.id.menu_home);
        }else{
            binding.bottomNavigationView.setSelectedItemId(R.id.menu_admin_agregar_usuarios);
        }

    }



    private void setupMenu(String permisosUsuario) {
        if (!"SUPERADMIN".equals(permisosUsuario)) {
            binding.bottomNavigationView.getMenu().findItem(R.id.menu_admin_agregar_usuarios).setVisible(false);
            binding.bottomNavigationView.getMenu().findItem(R.id.menuadmin_actividades).setVisible(false);

         //   replaceFragment(new HomeFragment());
        }else{
            binding.bottomNavigationView.getMenu().findItem(R.id.menu_home).setVisible(false);

         //  replaceFragment(new CrudUsuariosFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layouts_fragments, fragment);
        fragmentTransaction.commit();

    }

}