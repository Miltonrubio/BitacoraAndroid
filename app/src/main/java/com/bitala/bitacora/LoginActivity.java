package com.bitala.bitacora;

import static com.bitala.bitacora.Utils.crearToastPersonalizado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.bitala.bitacora.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.Manifest;

import okhttp3.internal.Util;

public class LoginActivity extends AppCompatActivity {

    String url;

    private RequestQueue rq;
    Context context;

    TextView inputUsername, inputPassword;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    CheckBox checkBoxRememberMe;


    AlertDialog.Builder builder;
    AlertDialog dialogCargando;


    String ID_usuario;
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


        builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

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
            crearToastPersonalizado(context, "Tienes campos vacios, por favor rellenalos");

        } else {
            Login(correo, clave);
            dialogCargando = Utils.ModalCargando(context, builder);

        }
    }


    private void Login(String correo, String clave) {

        StringRequest requestLogin = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals(response)) {
                    if (response.equals("fallo")) {
                        crearToastPersonalizado(context, "Usuario o contraseña incorrecta");
                    } else {

                        try {
                            boolean rememberMe = checkBoxRememberMe.isChecked();
                            // Convertir la respuesta en un JSONArray
                            JSONArray jsonArray = new JSONArray(response);

                            // Procesar los datos del JSONArray si es necesario
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                 ID_usuario = jsonObject.getString("ID_usuario");
                                String nombre = jsonObject.getString("nombre");
                                String clave = jsonObject.getString("clave");
                                String telefono = jsonObject.getString("telefono");
                                String correo = jsonObject.getString("correo");
                                String permisos = jsonObject.getString("permisos");
                                generarToken(ID_usuario);
                                guardarCredenciales(ID_usuario, nombre, clave, telefono, correo, permisos, rememberMe);

                            }

                            Intent intent = new Intent(LoginActivity.this, Activity_Binding.class);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            crearToastPersonalizado(context, "Usuario o contraseña incorrecta");
                        }
                    }
                } else {
                    crearToastPersonalizado(context, "Hay problemas con el servidor, por favor intenta mas tarde.");
                }
                onLoadComplete();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    crearToastPersonalizado(context, "Hay errores al intentar conectar, por favor intenta mas tarde.");

                } else {
                    crearToastPersonalizado(context, "Hay problemas con el servidor, por favor intenta mas tarde.");

                }
                onLoadComplete();
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

    public void onLoadComplete() {
        if (dialogCargando.isShowing() && dialogCargando != null) {
            dialogCargando.dismiss();
        }
    }


    private void generarToken(String IDInicioSesion) {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {

                      ActualizarToken(IDInicioSesion, token);
                        Log.d("TOKEN FIRABSE", token);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        crearToastPersonalizado(context, "No se obtuvo el token, no recibiras notificaciones");
                    }
                });
    }


    private void ActualizarToken(String ID_usuario, String TokenFIREBASE) {
        StringRequest requestLogin = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                crearToastPersonalizado(context, "No se obtuvo el token, no recibiras notificaciones");
            }

        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("opcion", "28");
                params.put("ID_usuario", ID_usuario);
                params.put("TokenFIREBASE", TokenFIREBASE);
                return params;
            }
        };
        rq.add(requestLogin);
    }


}
