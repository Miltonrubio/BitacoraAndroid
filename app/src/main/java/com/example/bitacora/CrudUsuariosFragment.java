package com.example.bitacora;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bitacora.Adaptadores.AdaptadorUsuarios;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrudUsuariosFragment extends Fragment implements AdaptadorUsuarios.OnActivityActionListener {

    String url = "http://hidalgo.no-ip.info:5610/bitacora/mostrar.php";
    private RecyclerView recyclerViewUsuarios;
    private AdaptadorUsuarios adaptadorUsuarios;
    private List<JSONObject> dataList = new ArrayList<>();
    private EditText editTextBusqueda;
    private FloatingActionButton botonAgregarActividad;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crud_usuarios, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        botonAgregarActividad = view.findViewById(R.id.botonAgregarActividad);
        recyclerViewUsuarios = view.findViewById(R.id.recyclerViewUsuarios);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));

        if (isAdded()) {
            adaptadorUsuarios = new AdaptadorUsuarios(dataList, requireContext(), this);
        }
        recyclerViewUsuarios.setAdapter(adaptadorUsuarios);
        editTextBusqueda = view.findViewById(R.id.searchEditTextArrastres);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        String ID_usuario = sharedPreferences.getString("ID_usuario", "");


        Handler handlerRecargarDatos = new Handler();
        Runnable runnableRecargarDatos;


        runnableRecargarDatos = new Runnable() {
            @Override
            public void run() {
                MostrarUsuarios();
                handlerRecargarDatos.postDelayed(this, 5 * 60 * 1000); // Ejecuta la tarea cada 5 minutos
            }
        };

        handlerRecargarDatos.postDelayed(runnableRecargarDatos, 5 * 60 * 1000); // Inicialmente, ejecuta la tarea después de 5 minutos


        MostrarUsuarios();

        editTextBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adaptadorUsuarios.filter(s.toString().toLowerCase());
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        botonAgregarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                // Inflar el diseño personalizado para el AlertDialog
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.insertar_nuevo_usuario, null);
                builder.setView(dialogView);

                // Obtener las referencias a los EditText dentro del diálogo
                final EditText textViewNombreUsuario = dialogView.findViewById(R.id.textViewNombreUsuario);
                final EditText textViewCorreoUsuario = dialogView.findViewById(R.id.textViewCorreoUsuario);
                final EditText TextViewClaveUsuario = dialogView.findViewById(R.id.TextViewClaveUsuario);
                final EditText TextViewTelefonoUsuario = dialogView.findViewById(R.id.TextViewTelefonoUsuario);
                final Spinner spinnerRolUsuario = dialogView.findViewById(R.id.spinnerRolUsuario);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                        R.array.opciones_rol, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Establece el ArrayAdapter en el Spinner
                spinnerRolUsuario.setAdapter(adapter);

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nombreUsuario = textViewNombreUsuario.getText().toString();
                        String correoUsuario = textViewCorreoUsuario.getText().toString();
                        String ClaveUsuario = TextViewClaveUsuario.getText().toString();
                        String TelefonoUsuario = TextViewTelefonoUsuario.getText().toString().replaceAll(" ", "");
                        String RolUsuario = spinnerRolUsuario.getSelectedItem().toString();

                        if (nombreUsuario.isEmpty() || correoUsuario.isEmpty() || ClaveUsuario.isEmpty() || TelefonoUsuario.isEmpty()) {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "No puedes ingresar campos vacios", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            AgregarNuevoUsuario(nombreUsuario, correoUsuario, ClaveUsuario, TelefonoUsuario, RolUsuario);
                        }
                    }
                });
                builder.setNegativeButton("Cancelar", null);

                // Mostrar el AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }

    private void MostrarUsuarios() {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    dataList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dataList.add(jsonObject); // Agrega cada objeto JSON a la lista
                    }
                    adaptadorUsuarios.notifyDataSetChanged();
                    adaptadorUsuarios.setFilteredData(dataList);
                    adaptadorUsuarios.filter("");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editTextBusqueda.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (isAdded()) {
                    Toast.makeText(requireContext(), "No tienes conexión a internet", Toast.LENGTH_SHORT).show();
                }

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "13");
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }


    private void AgregarNuevoUsuario(String nombreUsuario, String correoUsuario, String claveUsuario, String telefonoUsuario, String permisos) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("Error: El correo, nombre o teléfono ya existen en la base de datos.")) {

                    if (isAdded()) {
                        Toast.makeText(requireContext(), "No puedes insertar Datos repetidos", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Insertado Correctamente", Toast.LENGTH_SHORT).show();
                    }
                    MostrarUsuarios();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()) {
                    Toast.makeText(requireContext(), "No tienes conexión a internet", Toast.LENGTH_SHORT).show();
                }

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "14");
                params.put("permisos", permisos);
                params.put("nombre", nombreUsuario);
                params.put("correo", correoUsuario);
                params.put("clave", claveUsuario);
                params.put("telefono", telefonoUsuario);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }


    @Override
    public void onEditarUsuarioActivity(String ID_usuario, String nombreUsuario, String correoUsuario, String claveUsuario, String telefonoUsuario, String rolUsuario) {
        EditarUsuario(ID_usuario, nombreUsuario, correoUsuario, claveUsuario, telefonoUsuario, rolUsuario);
    }


    @Override
    public void onEliminarUsuarioActivity(String ID_usuario) {
        EliminarUsuario(ID_usuario);
    }


    private void EliminarUsuario(String ID_usuario) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MostrarUsuarios();

                if (isAdded()) {
                    Toast.makeText(requireContext(), "Exito", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()) {
                    Toast.makeText(requireContext(), "No tienes conexión a internet", Toast.LENGTH_SHORT).show();
                }

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "16");
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }


    private void EditarUsuario(String ID_usuario, String nombreUsuario, String correoUsuario, String claveUsuario, String telefonoUsuario, String rolUsuario) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (isAdded()) {
                    Toast.makeText(requireContext(), "Exito", Toast.LENGTH_SHORT).show();
                }
                MostrarUsuarios();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()) {
                    Toast.makeText(requireContext(), "No tienes conexión a internet", Toast.LENGTH_SHORT).show();
                }

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "15");
                params.put("ID_usuario", ID_usuario);
                params.put("permisos", rolUsuario);
                params.put("nombre", nombreUsuario);
                params.put("correo", correoUsuario);
                params.put("clave", claveUsuario);
                params.put("telefono", telefonoUsuario);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }

}