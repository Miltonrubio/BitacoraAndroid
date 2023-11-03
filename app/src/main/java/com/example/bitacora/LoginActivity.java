package com.example.bitacora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.Manifest;

public class LoginActivity extends AppCompatActivity {

    String url;

    private RequestQueue rq;
    Context context;

    TextView inputUsername, inputPassword;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    CheckBox checkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Si no se ha otorgado, solicitar el permiso cuando sea necesario.
        }
        context = this;
        url = context.getResources().getString(R.string.urlApi);

        rq = Volley.newRequestQueue(context);
        inputUsername = findViewById(R.id.correoET);
        inputPassword = findViewById(R.id.passwordET);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
        checkBoxRememberMe.setChecked(true);


        SharedPreferences sharedPreferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);
        if (rememberMe) {
            String savedUsername = sharedPreferences.getString("correo", "");
            String savedPassword = sharedPreferences.getString("clave", "");
            inputUsername.setText(savedUsername);
            inputPassword.setText(savedPassword);
            checkBoxRememberMe.setChecked(true);

            Intent intent = new Intent(LoginActivity.this, Activity_Binding.class);
            startActivity(intent);
            finish();
        }
    }

    public void onRequestLocation(View view) {
        // Verificar si el permiso de ubicación está otorgado nuevamente antes de usarlo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Acceder a la ubicación aquí, por ejemplo, iniciar una actividad para obtener la ubicación.
        } else {
            // Si el permiso no está otorgado, solicitarlo.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // Verifica si el permiso de la cámara se otorgó
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permiso de cámara: ", "Otorgado");
            } else {
                Log.d("Permiso de cámara: ", "Denegado");
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Verifica si el permiso de ubicación se otorgó
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permiso de ubicación: ", "Otorgado");
                // Puedes iniciar el seguimiento de ubicación o realizar acciones relacionadas con la ubicación aquí.
            } else {
                Log.d("Permiso de ubicación: ", "Denegado");
                // Puedes mostrar un mensaje al usuario indicando que algunas funciones de la aplicación no estarán disponibles sin el permiso de ubicación.
            }
        }
    }


    private void guardarCredenciales(String ID_usuario, String nombre, String clave, String telefono, String correo, String permisos, boolean rememberMe) {

        SharedPreferences sharedPreferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("correo", correo);
        editor.putString("ID_usuario", ID_usuario);
        editor.putString("nombre", nombre);
        editor.putString("telefono", telefono);
        editor.putString("permisos", permisos);
        editor.putString("clave", rememberMe ? clave : "");
        editor.putBoolean("rememberMe", rememberMe);
        editor.apply();
    }


    public void IniciarSession(View view) {
        IniciarSession();
    }

    private void IniciarSession() {
        String correo = inputUsername.getText().toString();
        String clave = inputPassword.getText().toString();


        if (correo.isEmpty() || clave.isEmpty()) {
            Toast.makeText(context, "LLENE TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show();
        } else {
            Login(correo, clave);
        }
    }


    private void Login(String correo, String clave) {

        StringRequest requestLogin = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals(response)) {
                    if (response.equals("fallo")) {
                        Toast.makeText(context, "USUARIO O CONTRASEÑA INCORRECTA", Toast.LENGTH_SHORT).show();
                    } else {

                        try {
                            boolean rememberMe = checkBoxRememberMe.isChecked();
                            // Convertir la respuesta en un JSONArray
                            JSONArray jsonArray = new JSONArray(response);

                            // Procesar los datos del JSONArray si es necesario
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String ID_usuario = jsonObject.getString("ID_usuario");
                                String nombre = jsonObject.getString("nombre");
                                String clave = jsonObject.getString("clave");
                                String telefono = jsonObject.getString("telefono");
                                String correo = jsonObject.getString("correo");
                                String permisos = jsonObject.getString("permisos");


                                guardarCredenciales(ID_usuario, nombre, clave, telefono, correo, permisos, rememberMe);

                            }

                            Intent intent = new Intent(LoginActivity.this, Activity_Binding.class);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Los datos son incorrectos", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "SERVIDORES EN MANTENIMIENTO... VUELVA A INTENTAR MAS TARDE ", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "ERROR AL CONECTAR", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "SERVIDORES EN MANTENIMIENTO, VUELVA A INTENTAR MAS TARDE ", Toast.LENGTH_LONG).show();
                }
            }

        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("opcion", "1");
                params.put("correo", correo);
                params.put("clave", clave);
                return params;
            }
        };
        rq.add(requestLogin);
    }


}
