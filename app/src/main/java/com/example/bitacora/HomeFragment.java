package com.example.bitacora;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;

public class HomeFragment extends Fragment implements AdaptadorActividades.OnActivityActionListener {

    private ArrayList<String> nombresActividades = new ArrayList<>();
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    String permisos;
    String ID_usuario;

    String url = "http://192.168.1.113/milton/bitacoraPHP/mostrar.php";
    private RecyclerView recyclerView;
    private AdaptadorActividades adaptadorActividades;
    private List<JSONObject> dataList = new ArrayList<>();
    private EditText editTextBusqueda;
    private FloatingActionButton botonAgregarActividad;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        VerNombresActividades();
        botonAgregarActividad = view.findViewById(R.id.botonAgregarActividad);
        recyclerView = view.findViewById(R.id.recyclerViewFragmentArrastres);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adaptadorActividades = new AdaptadorActividades(dataList, requireContext(), this);
        // adaptadorActividades = new AdaptadorActividades(dataList, requireContext());
        recyclerView.setAdapter(adaptadorActividades);
        editTextBusqueda = view.findViewById(R.id.searchEditTextArrastres);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        ID_usuario = sharedPreferences.getString("ID_usuario", "");
        permisos = sharedPreferences.getString("permisos", "");
        editTextBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adaptadorActividades.filter(s.toString().toLowerCase());
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        if (permisos.equals("SUPERADMIN")) {
            MostrarActividades();
        } else {
            ActividadesPorUsuario(ID_usuario);
        }


        botonAgregarActividad.setVisibility(View.VISIBLE);
        botonAgregarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Crear el AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.insertar_actividad, null);
                builder.setView(dialogView);
                final Spinner spinnerNmbreActividades = dialogView.findViewById(R.id.SpinnerActividades);
                final EditText editText2 = dialogView.findViewById(R.id.editText2);


                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresActividades);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerNmbreActividades.setAdapter(spinnerAdapter);

                if (nombresActividades != null && !nombresActividades.isEmpty()) {
                    String primerCliente = nombresActividades.get(0);
                    int posicionCliente = spinnerAdapter.getPosition(primerCliente);
                    spinnerNmbreActividades.setSelection(posicionCliente);
                } else {
                    Toast.makeText(requireContext(), "Hubo un error al obtener los datos", Toast.LENGTH_SHORT).show();
                }


                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtener el valor del Spinner después de que el usuario haya hecho una selección
                        String nombreActividad = spinnerNmbreActividades.getSelectedItem().toString();
                        String selectedID = obtenerIDDesdeNombre(nombreActividad);

                        // Verificar si se seleccionó el hint
                        if (!nombreActividad.equals("Selecciona una opción")) {
                            String descripcionActividad = editText2.getText().toString();
                            AgregarActividad(selectedID, descripcionActividad, ID_usuario);
                        } else {
                            // Mostrar un mensaje de error o realizar la acción deseada
                            Toast.makeText(requireContext(), "Debes seleccionar una actividad válida.", Toast.LENGTH_SHORT).show();
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


    private void ActividadesPorUsuario(String ID_usuario) {
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
                    adaptadorActividades.notifyDataSetChanged();
                    adaptadorActividades.setFilteredData(dataList);
                    adaptadorActividades.filter("");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editTextBusqueda.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "2");
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }


    private void MostrarActividades() {
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
                    adaptadorActividades.notifyDataSetChanged();
                    adaptadorActividades.setFilteredData(dataList);
                    adaptadorActividades.filter("");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editTextBusqueda.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "3");
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }


    private void AgregarActividad(String ID_nombre_actividad, String descripcionActividad, String ID_usuario) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(requireContext(), "Insertado Correctamente", Toast.LENGTH_SHORT).show();
                if (permisos.equals("SUPERADMIN")) {
                    MostrarActividades();
                } else {
                    ActividadesPorUsuario(ID_usuario);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(requireContext(), "No se insertò", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "4");
                params.put("ID_nombre_actividad", ID_nombre_actividad);
                params.put("descripcionActividad", descripcionActividad);
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }

    private void VerNombresActividades() {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    nombresActividades.clear(); // Limpia la lista antes de agregar los nuevos nombres
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String ID_nombre_actividad = jsonObject.getString("ID_nombre_actividad");
                        String nombre_actividad = jsonObject.getString("nombre_actividad");
                        nombresActividades.add(ID_nombre_actividad + ": " + nombre_actividad); // Agrega el ID y nombre de la actividad a la lista
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(requireContext(), "Hubo un error", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "11");
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }

    private String obtenerIDDesdeNombre(String nombreSeleccionado) {
        for (String actividad : nombresActividades) {
            if (actividad.equals(nombreSeleccionado)) {

                String[] partes = actividad.split(":");
                if (partes.length > 0) {
                    return partes[0].trim();
                }
            }
        }
        return null;
    }


    @Override
    public void onEditActivity(String ID_actividad, String ID_nombre_actividad, String descripcionActividad) {
        EditarActividad(ID_actividad, ID_nombre_actividad, descripcionActividad);
    }

    public void onMandarUbicacionActicity(String ID_usuario, String ID_actividad, Double longitud, Double latitud) {
        MandarUbicacion(ID_usuario, ID_actividad, longitud, latitud);
    }

    @Override
    public void onDeleteActivity(String ID_actividad) {
        EliminarActividad(ID_actividad);
    }


    public void onActualizarEstadoActivity(String ID_actividad, String nuevoEstado){
        ActualizarEstado(ID_actividad, nuevoEstado);
    }



    private void EliminarActividad(String ID_actividad) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (permisos.equals("SUPERADMIN")) {
                    MostrarActividades();
                } else {
                    ActividadesPorUsuario(ID_usuario);
                }
                Toast.makeText(requireContext(), "Eliminacion exitosa", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(requireContext(), "Hubo un error", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "18");
                params.put("ID_actividad", ID_actividad);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }


    private void EditarActividad(String ID_actividad, String ID_nombre_actividad, String descripcionActividad) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (permisos.equals("SUPERADMIN")) {
                    MostrarActividades();
                } else {
                    ActividadesPorUsuario(ID_usuario);
                }

                Toast.makeText(requireContext(), "Actualizacion exitosa", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(requireContext(), "Hubo un error", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "17");
                params.put("ID_nombre_actividad", ID_nombre_actividad);
                params.put("descripcionActividad", descripcionActividad);
                params.put("ID_actividad", ID_actividad);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }


    private void MandarUbicacion(String ID_usuario, String ID_actividad, Double longitud, Double latitud) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (permisos.equals("SUPERADMIN")) {
                    MostrarActividades();
                } else {
                    ActividadesPorUsuario(ID_usuario);
                }
                Toast.makeText(requireContext(), "Exito", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "8");
                params.put("ID_actividad", ID_actividad);
                params.put("ID_usuario", ID_usuario);
                params.put("longitud", longitud.toString());
                params.put("latitud", latitud.toString());
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }


    private void ActualizarEstado(String ID_actividad, String nuevoEstado) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (permisos.equals("SUPERADMIN")) {
                    MostrarActividades();
                } else {
                    ActividadesPorUsuario(ID_usuario);
                }
                Toast.makeText(requireContext(), "Exito", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "5");
                params.put("ID_actividad", ID_actividad);
                params.put("nuevoEstado", nuevoEstado);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }


}

