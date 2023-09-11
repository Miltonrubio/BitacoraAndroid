package com.example.bitacora;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private ArrayList<String> nombresActividades = new ArrayList<>();

    String url = "http://192.168.1.124/android/mostrar.php";
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

        botonAgregarActividad = view.findViewById(R.id.botonAgregarActividad);
        recyclerView = view.findViewById(R.id.recyclerViewFragmentArrastres);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adaptadorActividades = new AdaptadorActividades(dataList, requireContext());
        recyclerView.setAdapter(adaptadorActividades);
        editTextBusqueda = view.findViewById(R.id.searchEditTextArrastres);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        String ID_usuario = sharedPreferences.getString("ID_usuario", "");
        String permisos = sharedPreferences.getString("permisos", "");
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


        if(permisos.equals("SUPERADMIN")){
            MostrarActividades();
            botonAgregarActividad.setVisibility(View.GONE);

        }else {
            ActividadesPorUsuario(ID_usuario);
            botonAgregarActividad.setVisibility(View.VISIBLE);

/*
            botonAgregarActividad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Obtener el contexto del adaptador
                    Context context = v.getContext();

                    // Crear el AlertDialog previo
                    AlertDialog.Builder opcionesBuilder = new AlertDialog.Builder(context);
                    opcionesBuilder.setTitle("Selecciona una opción");

                    // Opción 1: Subir foto
                    opcionesBuilder.setPositiveButton("Subir foto", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "Subida", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Opción 2: Actualizar actividad
                    opcionesBuilder.setNegativeButton("Actualizar actividad", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Crear el AlertDialog principal para actualizar la actividad
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            // Inflar el diseño personalizado para el AlertDialog principal
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.insertar_actividad, null);
                            builder.setView(dialogView);

                            // Obtener las referencias a los EditText dentro del diálogo
                            final EditText editText1 = dialogView.findViewById(R.id.editText1);
                            final EditText editText2 = dialogView.findViewById(R.id.editText2);

                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String nombreActividad = editText1.getText().toString();
                                    String descripcionActividad = editText2.getText().toString();

                                    AgregarActividad(nombreActividad, descripcionActividad, ID_usuario);
                                }
                            });
                            builder.setNegativeButton("Cancelar", null);

                            // Mostrar el AlertDialog principal
                            AlertDialog activityDialog = builder.create();
                            activityDialog.show();
                        }
                    });

                    // Mostrar el AlertDialog de opciones
                    AlertDialog opcionesDialog = opcionesBuilder.create();
                    opcionesDialog.show();
                }
            });
*/


            botonAgregarActividad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Crear el AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                    // Inflar el diseño personalizado para el AlertDialog
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.insertar_actividad, null);
                    builder.setView(dialogView);

                    // Obtener las referencias a los EditText dentro del diálogo

                    final Spinner spinnerNmbreActividades = dialogView.findViewById(R.id.SpinnerActividades);
                    final EditText editText2 = dialogView.findViewById(R.id.editText2);


                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresActividades);

                    // Especificar el diseño del Spinner cuando se despliega
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Asignar el adaptador al Spinner
                    spinnerNmbreActividades.setAdapter(spinnerAdapter);


                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String nombreActividad = spinnerNmbreActividades.getSelectedItem().toString();
                            String descripcionActividad = editText2.getText().toString();

                            AgregarActividad(nombreActividad, descripcionActividad, ID_usuario);
                        }
                    });
                    builder.setNegativeButton("Cancelar", null);

                    // Mostrar el AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }


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


    private void AgregarActividad(String nombreActividad, String descripcionActividad, String ID_usuario) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(requireContext(), "Insertado Correctamente", Toast.LENGTH_SHORT).show();
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
                params.put("nombreActividad", nombreActividad);
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
                        String nombreActividad = jsonObject.getString("nombreActividad");
                        nombresActividades.add(nombreActividad); // Agrega el nombre de la actividad a la lista
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

}

