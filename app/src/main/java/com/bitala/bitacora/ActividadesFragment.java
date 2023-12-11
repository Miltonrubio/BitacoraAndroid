package com.bitala.bitacora;

import android.content.Context;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bitala.bitacora.Adaptadores.AdaptadorNombreActividades;
import com.bitala.bitacora.R;
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

    SwipeRefreshLayout swipeRefreshLayout;
    String tipo_actividad;

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
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
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


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MostrarActividades();

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        botonAgregarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.insertar_nuevo_nombre_actividad, null);
                builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                AlertDialog dialogView = builder.create();
                dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogView.show();

                EditText TextViewNombreActividad = dialogView.findViewById(R.id.TextViewNombreActividad);

                RadioButton radioButtonGeneral= dialogView.findViewById(R.id.radioButtonGeneral);
                RadioButton radioButtonOficinistas= dialogView.findViewById(R.id.radioButtonOficinistas);
                radioButtonGeneral.setChecked(true);

                radioButtonOficinistas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        radioButtonOficinistas.setChecked(true);
                        radioButtonGeneral.setChecked(false);

                         tipo_actividad="OFICINAS";

                    }
                });


                radioButtonGeneral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        radioButtonOficinistas.setChecked(false);
                        radioButtonGeneral.setChecked(true);

                         tipo_actividad="GENERAL";

                    }
                });



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

                        AgregarNombreActividad(nombreActividad, tipo_actividad);
                        dialogView.dismiss();
                    }
                });
            }
        });
    }


    private void animacionLupe(String estado) {
        if (estado.equals("Oculto")) {
            recyclerViewNombreActividades.setVisibility(View.VISIBLE);
            SinActividades.setVisibility(View.GONE);
        } else {
            recyclerViewNombreActividades.setVisibility(View.GONE);
            SinActividades.setVisibility(View.VISIBLE);
        }
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

                        String tipo_actividad= jsonObject.getString("tipo_actividad");

                        if (!tipo_actividad.equals("OCULTA")){
                            dataList.add(jsonObject); // Agrega cada objeto JSON a la lista
                        }

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

    private void AgregarNombreActividad(String nuevoNombreActividad, String tipo_actividad) {
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
                params.put("tipo_actividad", tipo_actividad);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void EditarNombreActividad(String ID_nombre_actividad, String nuevoNombreActividad,String valorTipoActividad) {
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
                params.put("nuevoTipoActividad", valorTipoActividad);
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
    public void onEditActivity(String ID_nombre_actividad, String nuevoNombreActividad, String valorTipoActividad) {
        EditarNombreActividad(ID_nombre_actividad, nuevoNombreActividad, valorTipoActividad);
    }

    @Override
    public void onDeleteActivity(String ID_nombre_actividad) {

        EliminarNombreActividad(ID_nombre_actividad);
    }

    @Override
    public void onFilterData(Boolean estado) {
        if (estado) {
            animacionLupe("Oculto");
        } else {
            if ((editTextBusqueda.getText().toString().equals("") || editTextBusqueda.getText().toString().isEmpty())) {
                animacionLupe("Oculto");
            } else {
                animacionLupe("Visible");
            }
        }

    }
}