package com.bitala.bitacora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.bitala.bitacora.R;
import com.bitala.bitacora.databinding.ActivityBindingBinding;


public class Activity_Binding extends AppCompatActivity {

    ActivityBindingBinding binding;

    private boolean doubleBackToExitPressedOnce = false;
    String permisosUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        permisosUsuario = sharedPreferences.getString("permisos", "");
        int theme = getThemeByUserType(permisosUsuario);
        setTheme(theme);

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_binding);

        binding = ActivityBindingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


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


        if ("SUPERADMIN".equals(permisosUsuario)) {

            binding.bottomNavigationView.setSelectedItemId(R.id.menu_admin_agregar_usuarios);
            binding.bottomNavigationView.setBackgroundResource(R.color.vino);
        }
        else if(permisosUsuario.equalsIgnoreCase("OFICINISTA")){
            binding.bottomNavigationView.setSelectedItemId(R.id.menu_home);
            binding.bottomNavigationView.setBackgroundResource(R.color.vino);
        }

        else {
            binding.bottomNavigationView.setSelectedItemId(R.id.menu_home);
            binding.bottomNavigationView.setBackgroundResource(R.color.naranjita);


        }

    }

    public int getThemeByUserType(String userType) {
        int themeResId;

        switch (userType) {
            case "SUPERADMIN":
                themeResId = R.style.Base_Theme_BitacoraAdmin;
                break;
            case "OFICINISTA":
                themeResId = R.style.Base_Theme_BitacoraAdmin;
                break;
            default:
                themeResId = R.style.Base_Theme_Bitacora;
                break;
        }

        return themeResId;
    }

    private void setupMenu(String permisosUsuario) {
        if (!"SUPERADMIN".equals(permisosUsuario)) {
            binding.bottomNavigationView.getMenu().findItem(R.id.menu_admin_agregar_usuarios).setVisible(false);
            binding.bottomNavigationView.getMenu().findItem(R.id.menuadmin_actividades).setVisible(false);

            //   replaceFragment(new HomeFragment());
        } else {
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


    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layouts_fragments);

        if (currentFragment instanceof ActividadesFragment || currentFragment instanceof UsuarioFragment) {


            if (!"SUPERADMIN".equals(permisosUsuario)) {
                binding.bottomNavigationView.setSelectedItemId(R.id.menu_home);
            } else {
                binding.bottomNavigationView.setSelectedItemId(R.id.menu_admin_agregar_usuarios);
            }

        } else if (currentFragment instanceof HomeFragment || currentFragment instanceof CrudUsuariosFragment) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            } else {
                this.doubleBackToExitPressedOnce = true;
                Utils.crearToastPersonalizado(Activity_Binding.this, "Presiona atrás otra vez para salir");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        } else {
            super.onBackPressed();
        }
    }


}