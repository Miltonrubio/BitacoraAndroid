package com.example.bitacora;

import static com.example.bitacora.Utils.ModalRedondeado;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
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


    AlertDialog.Builder builderCargando;

    AlertDialog modalCargando;

    RelativeLayout ContenedorContenido;

    ConstraintLayout LayoutSinInternet;

    ConstraintLayout SinActividades;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actividades, container, false);
        botonAgregarActividad = view.findViewById(R.id.botonAgregarActividad);
        recyclerViewNombreActividades = view.findViewById(R.id.recyclerViewNombreActividades);
        editTextBusqueda = view.findViewById(R.id.searchEditTextArrastres);

        context = requireContext();
        url = context.getResources().getString(R.string.urlApi);


        ContenedorContenido = view.findViewById(R.id.ContenedorContenido);
        LayoutSinInternet = view.findViewById(R.id.LayoutSinInternet);
        SinActividades = view.findViewById(R.id.SinActividades);

        builderCargando = new AlertDialog.Builder(view.getContext());
        builderCargando.setCancelable(false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewNombreActividades.setLayoutManager(new LinearLayoutManager(context));
        adaptadorNombreActividades = new AdaptadorNombreActividades(dataList, context, this);
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


                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.insertar_nuevo_nombre_actividad, null);

                builder.setView(ModalRedondeado(view.getContext(), customView));
                AlertDialog dialogView = builder.create();
                dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogView.show();

                /*

                // Crear el AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                // Inflar el diseño personalizado para el AlertDialog
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.insertar_nuevo_nombre_actividad, null);
                builder.setView(dialogView);
                builder.setNegativeButton("Cancelar", null);

                // Mostrar el AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
*/

                EditText TextViewNombreActividad = dialogView.findViewById(R.id.TextViewNombreActividad);

                Button buttonCancelar = dialogView.findViewById(R.id.buttonCancelar);
                Button buttonAceptar = dialogView.findViewById(R.id.buttonAceptar);

                buttonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogView.dismiss();
                    }
                });

                buttonAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String nombreActividad = TextViewNombreActividad.getText().toString();

                        AgregarNombreActividad(nombreActividad);
                        dialogView.dismiss();
                    }
                });
            }
        });
    }

    private void MostrarActividades() {
        dataList.clear();
        modalCargando = Utils.ModalCargando(context, builderCargando);
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dataList.add(jsonObject); // Agrega cada objeto JSON a la lista
                    }
                    adaptadorNombreActividades.notifyDataSetChanged();
                    adaptadorNombreActividades.setFilteredData(dataList);
                    adaptadorNombreActividades.filter("");

                    if (dataList.size() > 0) {

                        mostrarLayout("conContenido");
                    } else {

                        mostrarLayout("SinActividades");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    mostrarLayout("SinActividades");

                }
                editTextBusqueda.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mostrarLayout("SinInternet");
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


    private void mostrarLayout(String estado) {
        if (estado.equalsIgnoreCase("SinActividades")) {
            SinActividades.setVisibility(View.VISIBLE);
            ContenedorContenido.setVisibility(View.GONE);
            LayoutSinInternet.setVisibility(View.GONE);
        } else if (estado.equalsIgnoreCase("SinInternet")) {
            SinActividades.setVisibility(View.GONE);
            ContenedorContenido.setVisibility(View.GONE);
            LayoutSinInternet.setVisibility(View.VISIBLE);

        } else {
            SinActividades.setVisibility(View.GONE);
            ContenedorContenido.setVisibility(View.VISIBLE);
            LayoutSinInternet.setVisibility(View.GONE);
        }

        onLoadComplete();
    }


    private void onLoadComplete() {
        if (modalCargando.isShowing() && modalCargando != null) {
            modalCargando.dismiss();
        }
    }

    private void AgregarNombreActividad(String nuevoNombreActividad) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                MostrarActividades();
                Utils.crearToastPersonalizado(context, "Se agregó la actividad" + nuevoNombreActividad);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.crearToastPersonalizado(context, "No se pudo agregar la actividad, revisa la conexión");
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
                MostrarActividades();
                Utils.crearToastPersonalizado(context, "Se editó la actividad " + nuevoNombreActividad);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "No se pudo modificar la actividad, revisa la conexión");

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
                Utils.crearToastPersonalizado(context, "Se eliminó con exito la actividad");

                MostrarActividades();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "No se pudo eliminar la actividad, revisa la conexión");
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