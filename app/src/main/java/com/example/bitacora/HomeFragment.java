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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bitacora.Adaptadores.AdaptadorActividades;
import com.example.bitacora.Adaptadores.AdaptadorListaActividades;
import com.example.bitacora.Adaptadores.AdaptadorNombreActividades;
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

import okhttp3.internal.Util;

public class HomeFragment extends Fragment implements AdaptadorActividades.OnActivityActionListener, AdaptadorListaActividades.OnActivityActionListener {
    List<JSONObject> nombresActividades = new ArrayList<>();
    String permisos;
    String ID_usuario;
    String url;
    private RecyclerView recyclerView;
    private AdaptadorActividades adaptadorActividades;
    private List<JSONObject> dataList = new ArrayList<>();
    Context context;
    private EditText editTextBusqueda;
    private FloatingActionButton botonAgregarActividad;
    Button btnFinalizadas, btnPendientes;
    ConstraintLayout SinInternet;
    RelativeLayout LayoutContenido;
    ConstraintLayout SinActividades;
    TextView textViewBienvenida;
    String nombreSesionIniciada;

    //Para el modal de actividades
    AdaptadorListaActividades adaptadorListaActividades;
    ConstraintLayout LayoutSinInternet;
    ConstraintLayout LayoutConInternet;
    RecyclerView RecyclerViewTituloActividades;
    AlertDialog dialogActividades;

    SwipeRefreshLayout swipeRefreshLayout;


    AlertDialog.Builder builder;

    AlertDialog.Builder builderCargando;
    AlertDialog.Builder builderCargandoTituloActividades;

    AlertDialog modalCargando;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        context = requireContext();
        url = context.getResources().getString(R.string.urlApi);

        builderCargando = new AlertDialog.Builder(view.getContext());
        builderCargando.setCancelable(false);


        botonAgregarActividad = view.findViewById(R.id.botonAgregarActividad);
        textViewBienvenida = view.findViewById(R.id.textViewBienvenida);
        recyclerView = view.findViewById(R.id.recyclerViewFragmentArrastres);
        SinInternet = view.findViewById(R.id.SinInternet);
        LayoutContenido = view.findViewById(R.id.LayoutContenido);
        SinActividades = view.findViewById(R.id.SinActividades);
        btnFinalizadas = view.findViewById(R.id.btnFinalizadas);
        btnPendientes = view.findViewById(R.id.btnPendientes);
        editTextBusqueda = view.findViewById(R.id.searchEditTextArrastres);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Adaptadores
        adaptadorActividades = new AdaptadorActividades(dataList, context, this);
        adaptadorListaActividades = new AdaptadorListaActividades(nombresActividades, context, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adaptadorActividades);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        ID_usuario = sharedPreferences.getString("ID_usuario", "");
        permisos = sharedPreferences.getString("permisos", "");
        nombreSesionIniciada = sharedPreferences.getString("nombre", "");
        ActividadesPorUsuario(ID_usuario);

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

        btnPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActividadesPorUsuario(ID_usuario);
            }
        });
        botonAgregarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(view.getContext());
                View customView = LayoutInflater.from(context).inflate(R.layout.opciones_titulo_actividad, null);
                builder.setView(ModalRedondeado(view.getContext(), customView));


                RecyclerViewTituloActividades = customView.findViewById(R.id.RecyclerViewTituloActividades);
                LayoutSinInternet = customView.findViewById(R.id.LayoutSinInternet);
                LayoutConInternet = customView.findViewById(R.id.LayoutConInternet);

                dialogActividades = builder.create();
                dialogActividades.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogActividades.show();

                VerNombresActividades(customView.getContext());

                RecyclerViewTituloActividades.setLayoutManager(new LinearLayoutManager(getContext()));
                RecyclerViewTituloActividades.setAdapter(adaptadorListaActividades);


                dialogActividades.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        ActividadesPorUsuario(ID_usuario);
                    }
                });

            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                ActividadesPorUsuario(ID_usuario);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    private void VerNombresActividades(Context contextModal) {
        nombresActividades.clear();
        mostrarItemsNombreActividades("sinVista");
        modalCargando = Utils.ModalCargando(contextModal, builder);
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        nombresActividades.add(jsonObject);
                    }
                    if (nombresActividades.size() > 0) {
                        mostrarItemsNombreActividades("conDatos");

                    } else {

                     mostrarItemsNombreActividades("sinDatos");
                    }
                    adaptadorListaActividades.notifyDataSetChanged();
                    adaptadorListaActividades.setFilteredData(dataList);
                    adaptadorListaActividades.filter("");

                } catch (JSONException e) {

                    mostrarItemsNombreActividades("sinDatos");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mostrarItemsNombreActividades("sinDatos");
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


    private void mostrarItemsNombreActividades(String estado) {

        if (estado.equalsIgnoreCase("sinDatos")) {

            LayoutSinInternet.setVisibility(View.VISIBLE);
            LayoutConInternet.setVisibility(View.GONE);

        } else if (estado.equalsIgnoreCase("sinVista")) {

            LayoutSinInternet.setVisibility(View.GONE);
            LayoutConInternet.setVisibility(View.INVISIBLE);
        } else {

            LayoutSinInternet.setVisibility(View.GONE);
            LayoutConInternet.setVisibility(View.VISIBLE);

        }

        onLoadComplete();

    }


    private void ActividadesPorUsuario(String ID_usuario) {

        dataList.clear();
        modalCargando = Utils.ModalCargando(context, builderCargando);
        SinInternet.setVisibility(View.GONE);

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String estadoActividad = jsonObject.getString("estadoActividad");
                        if (!estadoActividad.equalsIgnoreCase("Finalizado")) {
                            dataList.add(jsonObject);
                        }
                    }

                    adaptadorActividades.notifyDataSetChanged();
                    adaptadorActividades.setFilteredData(dataList);
                    adaptadorActividades.filter("");
                    //  mostrarActividadesPendientes();

                    if (dataList.size() > 0) {

                        mostrarItemsActividades("ConResultados");
                    } else {

                        mostrarItemsActividades("sinDatos");
                    }

                } catch (JSONException e) {

                    mostrarItemsActividades("sinDatos");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                mostrarItemsActividades("sinInternet");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "2");
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(postrequest);
    }


    private void mostrarItemsActividades(String estado) {

        if (estado.equalsIgnoreCase("sinDatos")) {
            SinInternet.setVisibility(View.GONE);
            SinActividades.setVisibility(View.VISIBLE);
            LayoutContenido.setVisibility(View.GONE);
            textViewBienvenida.setText("¡ BIENVENIDO " + nombreSesionIniciada.toUpperCase() + " !");

        } else if (estado.equalsIgnoreCase("sinInternet")) {

            SinInternet.setVisibility(View.VISIBLE);
            LayoutContenido.setVisibility(View.GONE);
            SinActividades.setVisibility(View.GONE);
        } else {
            SinInternet.setVisibility(View.GONE);
            LayoutContenido.setVisibility(View.VISIBLE);
            SinActividades.setVisibility(View.GONE);

        }

        onLoadComplete();

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

    public void onCancelarActividadesActivity(String ID_actividad, String nuevoEstado, String motivoCancelacion) {
        CancelarActividades(ID_actividad, nuevoEstado, motivoCancelacion);
    }


    @Override
    public void onAgregarActividad(String idNombreActividad, String descripcion) {

//        Utils.crearToastPersonalizado(context, idNombreActividad + descripcion + ID_usuario);
        AgregarActividad(idNombreActividad, descripcion, ID_usuario);

    }


    private void AgregarActividad(String ID_nombre_actividad, String descripcionActividad, String ID_usuario) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialogActividades.dismiss();
                Utils.crearToastPersonalizado(context, "Insertado Correctamente");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.crearToastPersonalizado(context, "No se pudo insertar la actividad, revisa tu conexión");
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

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void EliminarActividad(String ID_actividad) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ActividadesPorUsuario(ID_usuario);

                Utils.crearToastPersonalizado(context, "Se eliminó la actividad");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "No se pudo eliminar, revisa tu conexión");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "18");
                params.put("ID_actividad", ID_actividad);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void EditarActividad(String ID_actividad, String ID_nombre_actividad, String descripcionActividad) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ActividadesPorUsuario(ID_usuario);
                Utils.crearToastPersonalizado(context, "Se actualizó la actividad");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "No se pudo actualizar, revisa tu conexión");

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
        Volley.newRequestQueue(context).add(postrequest);
    }

    private void MandarUbicacion(String ID_usuario, String ID_actividad, Double longitud, Double latitud) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ActividadesPorUsuario(ID_usuario);
                Utils.crearToastPersonalizado(context, "Se mando la ubicación correctamente");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "No se mando la ubicación, revisa tus permisos o la conexión");
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

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void ActualizarEstado(String ID_actividad, String nuevoEstado) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ActividadesPorUsuario(ID_usuario);
                Utils.crearToastPersonalizado(context, " Se actualizó el estado de la actividad");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, " No se pudo actualizar el estado de la actividad, revisa tu conexión");

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

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void CancelarActividades(String ID_actividad, String nuevoEstado, String motivoCancelacion) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ActividadesPorUsuario(ID_usuario);
                Utils.crearToastPersonalizado(context, "Cancelaste la actividad");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "No se pudo cancelar la actividad, revisa tu conexión");

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

        Volley.newRequestQueue(context).add(postrequest);
    }


    public void onLoadComplete() {
        if (modalCargando.isShowing() && modalCargando != null) {
            modalCargando.dismiss();
        }
    }


/*
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

                if (isAdded()) {
                    Toast.makeText(context, "No tienes conexión a internet", Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(context).add(postrequest);
    }
*/

   /* private void reiniciarDatosDependiendoDeFecha() {
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

                if (datosDependiendoDeFecha.size() > 0) {
                    LayoutContenido.setVisibility(View.VISIBLE);
                    SinInternet.setVisibility(View.GONE);
                } else {

                    SinInternet.setVisibility(View.VISIBLE);
                    LayoutContenido.setVisibility(View.GONE);
                }


            } catch (JSONException | ParseException e) {
                e.printStackTrace();

                SinInternet.setVisibility(View.VISIBLE);
                LayoutContenido.setVisibility(View.GONE);

            }
        }
        adaptadorActividades.setFilteredData(datosDependiendoDeFecha);
        adaptadorActividades.notifyDataSetChanged();
    }
*/
/*
    private void mostrarActividadesPendientes() {
        reiniciarDatosDependiendoDeFecha();

        for (JSONObject jsonObject : dataList) {
            try {
                String estadoActividad = jsonObject.getString("estadoActividad"); // Obtener el estado de la actividad


                if (estadoActividad.equals("Iniciado") || estadoActividad.equals("Pendiente")) {
                    datosDependiendoDeFecha.add(jsonObject);
                }


                if (dataList.size() > 0) {
                    SinInternet.setVisibility(View.GONE);
                    LayoutContenido.setVisibility(View.VISIBLE);
                    SinActividades.setVisibility(View.GONE);
                } else {
                    SinInternet.setVisibility(View.GONE);
                    SinActividades.setVisibility(View.VISIBLE);
                    LayoutContenido.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                SinInternet.setVisibility(View.GONE);
                SinActividades.setVisibility(View.VISIBLE);
                LayoutContenido.setVisibility(View.GONE);
            }
        }
        adaptadorActividades.setFilteredData(datosDependiendoDeFecha);
        adaptadorActividades.notifyDataSetChanged();
    }
*/

/*
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


 */
}

