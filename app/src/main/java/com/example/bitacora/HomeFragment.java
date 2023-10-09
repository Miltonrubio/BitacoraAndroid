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

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;

public class HomeFragment extends Fragment implements AdaptadorActividades.OnActivityActionListener {

    private ArrayList<String> nombresActividades = new ArrayList<>();
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    String permisos;
    String ID_usuario;
    String url = "http://hidalgo.no-ip.info:5610/bitacora/mostrar.php";
    private RecyclerView recyclerView;
    private AdaptadorActividades adaptadorActividades;
    private List<JSONObject> dataList = new ArrayList<>();

    List<JSONObject> datosDependiendoDeFecha = new ArrayList<>();
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

        Button btnFinalizadas = view.findViewById(R.id.btnFinalizadas);
        Button btnPendientes = view.findViewById(R.id.btnPendientes);

        LinearLayout LayoutBotones = view.findViewById(R.id.LayoutBotones);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (isAdded()) {

            adaptadorActividades = new AdaptadorActividades(dataList, requireContext(), this);
        }
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

        Handler handlerRecargarDatos = new Handler();
        Runnable runnableRecargarDatos;

        runnableRecargarDatos = new Runnable() {
            @Override
            public void run() {
                ActividadesPorUsuario(ID_usuario);

                VerNombresActividades();
                handlerRecargarDatos.postDelayed(this, 5 * 60 * 1000); // Ejecuta la tarea cada 5 minutos
            }
        };

        handlerRecargarDatos.postDelayed(runnableRecargarDatos, 5 * 60 * 1000); // Inicialmente, ejecuta la tarea después de 5 minutos


        ActividadesPorUsuario(ID_usuario);

        VerNombresActividades();

        btnPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActividadesPorUsuario(ID_usuario);
            }
        });

        btnFinalizadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActividadesFinalizadas(ID_usuario);
            }
        });

        botonAgregarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActividadesPorUsuario(ID_usuario);

                VerNombresActividades();

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

                    if (isAdded()) {
                        Toast.makeText(requireContext(), "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
                    }
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
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "Debes seleccionar una actividad válida.", Toast.LENGTH_SHORT).show();
                            }
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

    private void reiniciarDatosDependiendoDeFecha() {
        datosDependiendoDeFecha.clear();
    }

    private void mostrarDatosDelDiaDeHoy() {
        reiniciarDatosDependiendoDeFecha();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        for (JSONObject jsonObject : dataList) {
            try {
                String fechaInicio = jsonObject.getString("fecha_fin");
                String estadoActividad = jsonObject.getString("estadoActividad"); // Obtener el estado de la actividad

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fecha = sdf.parse(fechaInicio);

                Calendar actividadCalendar = Calendar.getInstance();
                actividadCalendar.setTime(fecha);
                int actividadYear = actividadCalendar.get(Calendar.YEAR);
                int actividadMonth = actividadCalendar.get(Calendar.MONTH) + 1;
                int actividadDay = actividadCalendar.get(Calendar.DAY_OF_MONTH);

                if (estadoActividad.equals("Finalizado") && actividadYear == year && actividadMonth == month && actividadDay == day) {
                    datosDependiendoDeFecha.add(jsonObject);
                }

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        adaptadorActividades.setFilteredData(datosDependiendoDeFecha);
        adaptadorActividades.notifyDataSetChanged();
    }


    private void mostrarActividadesPendientes() {
        reiniciarDatosDependiendoDeFecha();

        for (JSONObject jsonObject : dataList) {
            try {
                String estadoActividad = jsonObject.getString("estadoActividad"); // Obtener el estado de la actividad


                if (estadoActividad.equals("Iniciado") || estadoActividad.equals("Pendiente")) {
                    datosDependiendoDeFecha.add(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adaptadorActividades.setFilteredData(datosDependiendoDeFecha);
        adaptadorActividades.notifyDataSetChanged();
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

                        dataList.add(jsonObject);
                    }
                    adaptadorActividades.notifyDataSetChanged();
                    adaptadorActividades.setFilteredData(dataList);
                    adaptadorActividades.filter("");
                    mostrarActividadesPendientes();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()){
                    Toast.makeText(requireContext(), "No tienes conexión a internet", Toast.LENGTH_SHORT).show();
                }

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


    private void ActividadesFinalizadas(String ID_usuario) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    dataList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        dataList.add(jsonObject);
                    }
                    adaptadorActividades.notifyDataSetChanged();
                    adaptadorActividades.setFilteredData(dataList);
                    adaptadorActividades.filter("");
                    mostrarDatosDelDiaDeHoy();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()){
                    Toast.makeText(requireContext(), "No tienes conexión a internet", Toast.LENGTH_SHORT).show();
                }

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


    private void AgregarActividad(String ID_nombre_actividad, String descripcionActividad, String ID_usuario) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Insertado Correctamente", Toast.LENGTH_SHORT).show();
                }

                ActividadesPorUsuario(ID_usuario);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (isAdded()) {
                    Toast.makeText(requireContext(), "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
                }
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
                    nombresActividades.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String ID_nombre_actividad = jsonObject.getString("ID_nombre_actividad");
                        String nombre_actividad = jsonObject.getString("nombre_actividad");
                        nombresActividades.add(ID_nombre_actividad + ": " + nombre_actividad);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()) {
                    Toast.makeText(requireContext(), "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
                }
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


    public void onActualizarEstadoActivity(String ID_actividad, String nuevoEstado) {
        ActualizarEstado(ID_actividad, nuevoEstado);
    }
    public void onCancelarActividadesActivity(String ID_actividad, String nuevoEstado,String motivoCancelacion) {
        CancelarActividades(ID_actividad, nuevoEstado, motivoCancelacion);
    }




    private void EliminarActividad(String ID_actividad) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ActividadesPorUsuario(ID_usuario);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Se eliminó la actividad", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (isAdded()) {
                    Toast.makeText(requireContext(), "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
                }
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
                ActividadesPorUsuario(ID_usuario);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Se actualizó la actividad", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()) {
                    Toast.makeText(requireContext(), "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
                }
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
                ActividadesPorUsuario(ID_usuario);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Se mando la ubicación correctamente", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()) {
                    Toast.makeText(requireContext(), "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
                }
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
                ActividadesPorUsuario(ID_usuario);

                if (isAdded()) {
                    Toast.makeText(requireContext()," Se actualizó el estado de la actividad", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()) {
                    Toast.makeText(requireContext(), "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
                }
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


    private void CancelarActividades(String ID_actividad, String nuevoEstado,String motivoCancelacion) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ActividadesPorUsuario(ID_usuario);

                if (isAdded()) {
                    Toast.makeText(requireContext(),"Se canceló la actividad", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()) {
                    Toast.makeText(requireContext(), "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "29");
                params.put("ID_actividad", ID_actividad);
                params.put("nuevoEstado", nuevoEstado);
                params.put("motivocancelacion", motivoCancelacion);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }


}

