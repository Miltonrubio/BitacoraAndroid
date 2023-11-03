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
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bitacora.Adaptadores.AdaptadorNombreActividades;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActividadesFragment extends Fragment implements AdaptadorNombreActividades.OnActivityActionListener {

    String url;

    Context context;
    private RecyclerView recyclerViewNombreActividades;
    private AdaptadorNombreActividades adaptadorNombreActividades;
    private List<JSONObject> dataList = new ArrayList<>();
    private EditText editTextBusqueda;
    private FloatingActionButton botonAgregarActividad;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actividades, container, false);
        botonAgregarActividad = view.findViewById(R.id.botonAgregarActividad);
        recyclerViewNombreActividades = view.findViewById(R.id.recyclerViewNombreActividades);
        editTextBusqueda = view.findViewById(R.id.searchEditTextArrastres);

        context = requireContext();
        url = context.getResources().getString(R.string.urlApi);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewNombreActividades.setLayoutManager(new LinearLayoutManager(getContext()));

        if (isAdded()) {
            adaptadorNombreActividades = new AdaptadorNombreActividades(dataList, context, this);
        }
        recyclerViewNombreActividades.setAdapter(adaptadorNombreActividades);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        String ID_usuarioActual = sharedPreferences.getString("ID_usuario", "");


        MostrarActividades();

        editTextBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adaptadorNombreActividades.filter(s.toString().toLowerCase());
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
                View dialogView = inflater.inflate(R.layout.insertar_nuevo_nombre_actividad, null);
                builder.setView(dialogView);

                // Obtener las referencias a los EditText dentro del diálogo
                final EditText TextViewNombreActividad = dialogView.findViewById(R.id.TextViewNombreActividad);

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nombreActividad = TextViewNombreActividad.getText().toString();

                        AgregarNombreActividad(nombreActividad);
                    }
                });
                builder.setNegativeButton("Cancelar", null);

                // Mostrar el AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
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
                    adaptadorNombreActividades.notifyDataSetChanged();
                    adaptadorNombreActividades.setFilteredData(dataList);
                    adaptadorNombreActividades.filter("");

                } catch (JSONException e) {
                    e.printStackTrace();

                    if (isAdded()){
                        Toast.makeText(requireContext(), "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
                    }
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
                params.put("opcion", "11");
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void AgregarNombreActividad(String nuevoNombreActividad) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (isAdded()) {
                    Toast.makeText(context, "Insertado Correctamente", Toast.LENGTH_SHORT).show();
                }
                MostrarActividades();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()){
                    Toast.makeText(requireContext(), "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "6");
                params.put("nuevoNombreActividad", nuevoNombreActividad);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void EditarNombreActividad(String ID_nombre_actividad, String nuevoNombreActividad) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (isAdded()) {
                    Toast.makeText(context, "Editado con exito el " + ID_nombre_actividad, Toast.LENGTH_SHORT).show();
                }
                MostrarActividades();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()){
                    Toast.makeText(context, "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "7");
                params.put("ID_nombre_actividad", ID_nombre_actividad);
                params.put("nuevoNombreActividad", nuevoNombreActividad);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(postrequest);
    }


    private void EliminarNombreActividad(String ID_nombre_actividad) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (isAdded()) {
                    Toast.makeText(context, "Se elimino correctamente", Toast.LENGTH_SHORT).show();
                }
                MostrarActividades();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()) {
                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "12");
                params.put("ID_nombre_actividad", ID_nombre_actividad);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(postrequest);
    }


    @Override
    public void onEditActivity(String ID_nombre_actividad, String nuevoNombreActividad) {
        EditarNombreActividad(ID_nombre_actividad, nuevoNombreActividad);
    }

    @Override
    public void onDeleteActivity(String ID_nombre_actividad) {

        EliminarNombreActividad(ID_nombre_actividad);
    }
}