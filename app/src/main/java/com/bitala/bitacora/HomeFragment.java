package com.bitala.bitacora;

import static com.bitala.bitacora.Utils.ModalRedondeado;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bitala.bitacora.Adaptadores.AdaptadorActividades;
import com.bitala.bitacora.Adaptadores.AdaptadorListaActividades;
import com.bitala.bitacora.Adaptadores.AdaptadorMostrarSaldoParaUsuarios;
import com.bitala.bitacora.Adaptadores.AdaptadorSeleccionarCaja;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements AdaptadorActividades.OnActivityActionListener, AdaptadorListaActividades.OnActivityActionListener, AdaptadorSeleccionarCaja.OnActivityActionListener {

    List<JSONObject> nombresActividades = new ArrayList<>();
    String permisos;

    AdaptadorSeleccionarCaja.OnActivityActionListener actionListenerSeleccionarCaja;
    String ID_usuario;
    String url;
    private RecyclerView recyclerViewActividades;
    private AdaptadorActividades adaptadorActividades;
    private List<JSONObject> dataList = new ArrayList<>();
    Context context;

    private ImageView botonAgregarActividad;
    ConstraintLayout SinInternet;
    ConstraintLayout LayoutContenido;
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

    AlertDialog modalCargando;


    RelativeLayout ContenedorCompleto;
    TextView saldoActual;
    String ID_saldo;
    TextView saldoActualSinCont;

    RecyclerView recyclerViewSaldosActivos2;
    TextView SinSaldoActivoSinCont;

    TextView SinSaldoActivo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        context = requireContext();
        url = context.getResources().getString(R.string.urlApi);

        builderCargando = new AlertDialog.Builder(context);
        builderCargando.setCancelable(false);

        //   saldoActual = view.findViewById(R.id.saldoActual);

        actionListenerSeleccionarCaja = this;
        botonAgregarActividad = view.findViewById(R.id.botonAgregarActividad);
        textViewBienvenida = view.findViewById(R.id.textViewBienvenida);
        recyclerViewActividades = view.findViewById(R.id.recyclerViewActividades);
        SinInternet = view.findViewById(R.id.SinInternet);
        LayoutContenido = view.findViewById(R.id.LayoutContenido);
        SinActividades = view.findViewById(R.id.SinActividades);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        ContenedorCompleto = view.findViewById(R.id.ContenedorCompleto);


        SinSaldoActivo = view.findViewById(R.id.SinSaldoActivo);


        SinSaldoActivoSinCont = view.findViewById(R.id.SinSaldoActivoSinCont);


        //   recyclerViewSaldosActivos2 = view.findViewById(R.id.recyclerViewSaldosActivos2);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        ID_usuario = sharedPreferences.getString("ID_usuario", "");
        permisos = sharedPreferences.getString("permisos", "");
        nombreSesionIniciada = sharedPreferences.getString("nombre", "");

        ActividadesPorUsuario(ID_usuario);


        RecyclerView recyclerViewSaldosActivosUsuario = view.findViewById(R.id.recyclerViewSaldosActivosUsuario);

        RecyclerView recyclerViewSaldosActivosUsuarioSinCont = view.findViewById(R.id.recyclerViewSaldosActivosUsuarioSinCont);


        adaptadorMostrarSaldoParaUsuarios = new AdaptadorMostrarSaldoParaUsuarios(saldoDeUsuarios, context);
        recyclerViewSaldosActivosUsuario.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewSaldosActivosUsuario.setAdapter(adaptadorMostrarSaldoParaUsuarios);

        recyclerViewSaldosActivosUsuarioSinCont.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewSaldosActivosUsuarioSinCont.setAdapter(adaptadorMostrarSaldoParaUsuarios);


        //Adaptadores
        adaptadorActividades = new AdaptadorActividades(dataList, context, this);
        adaptadorListaActividades = new AdaptadorListaActividades(nombresActividades, context, this);
        recyclerViewActividades.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewActividades.setAdapter(adaptadorActividades);


        int colorRes;
        Drawable drawable;
        Drawable botonRedondo;

/*
        if (permisos.equalsIgnoreCase("OFICINISTA")) {

            drawable = ContextCompat.getDrawable(context, R.color.vino);
            colorRes = ContextCompat.getColor(context, R.color.vino);
            botonRedondo = ContextCompat.getDrawable(context, R.drawable.botonredondorojo);
        } else {

            drawable = ContextCompat.getDrawable(context, R.color.naranjita);
            botonRedondo = ContextCompat.getDrawable(context, R.drawable.botonredonndonaranja);


            colorRes = ContextCompat.getColor(context, R.color.naranjita);
        }

        ContenedorCompleto.setBackground(drawable);
        botonAgregarActividad.setBackground(botonRedondo);



        /*
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

         */

        botonAgregarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(view.getContext());
                View customView = LayoutInflater.from(context).inflate(R.layout.opciones_titulo_actividad, null);
                builder.setView(Utils.ModalRedondeado(view.getContext(), customView));

                //     AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                //   builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                dialogActividades = builder.create();
                ColorDrawable back = new ColorDrawable(Color.BLACK);
                back.setAlpha(150);
                dialogActividades.getWindow().setBackgroundDrawable(back);
                dialogActividades.getWindow().setDimAmount(0.8f);
                dialogActividades.show();


                RecyclerViewTituloActividades = customView.findViewById(R.id.RecyclerViewTituloActividades);
                LayoutSinInternet = customView.findViewById(R.id.LayoutSinInternet);
                LayoutConInternet = customView.findViewById(R.id.LayoutConInternet);

                EditText searchEditText = customView.findViewById(R.id.searchEditText);

                /*
                dialogActividades = builder.create();
                dialogActividades.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogActividades.show();
                */

                VerNombresActividades(customView.getContext());

                RecyclerViewTituloActividades.setLayoutManager(new LinearLayoutManager(getContext()));
                RecyclerViewTituloActividades.setAdapter(adaptadorListaActividades);


                searchEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adaptadorListaActividades.filter(s.toString().toLowerCase());
                    }


                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


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


    public void onSeleccionarTipoFin(String ID_actividad, AlertDialog dialogOpcionesDeActividad, String tituloActividad) {


        if (saldoDeUsuarios.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View customView = LayoutInflater.from(context).inflate(R.layout.modal_seleccionar_forma_fin, null);
            builder.setView(ModalRedondeado(context, customView));
            AlertDialog dialogSeleccionTipoFin = builder.create();
            dialogSeleccionTipoFin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogSeleccionTipoFin.show();


            LinearLayout btnConGastos = customView.findViewById(R.id.btnConGastos);
            LinearLayout btnSinGastos = customView.findViewById(R.id.btnSinGastos);


            btnConGastos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //    VerSaldos(ID_usuario);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View customView = LayoutInflater.from(context).inflate(R.layout.modal_seleccion_caja, null);
                    builder.setView(ModalRedondeado(context, customView));
                    AlertDialog dialogSeleccionarCaja = builder.create();
                    dialogSeleccionarCaja.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogSeleccionarCaja.show();


                    AdaptadorSeleccionarCaja adaptadorSeleccionarCaja;
                    RecyclerView recyclerSeleccionCaja = customView.findViewById(R.id.recyclerSeleccionCaja);
                    adaptadorSeleccionarCaja = new AdaptadorSeleccionarCaja(saldoDeUsuarios, context, ID_actividad,tituloActividad, actionListenerSeleccionarCaja, dialogSeleccionarCaja, dialogSeleccionTipoFin, dialogOpcionesDeActividad);
                    recyclerSeleccionCaja.setLayoutManager(new LinearLayoutManager(context));
                    recyclerSeleccionCaja.setAdapter(adaptadorSeleccionarCaja);


                    adaptadorSeleccionarCaja.notifyDataSetChanged();
                    adaptadorSeleccionarCaja.setFilteredData(saldoDeUsuarios);
                    adaptadorSeleccionarCaja.filter("");


                }
            });


            btnSinGastos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View customView = LayoutInflater.from(context).inflate(R.layout.opciones_confirmacion, null);
                    builder.setView(ModalRedondeado(context, customView));
                    AlertDialog dialogConfirmacion = builder.create();
                    dialogConfirmacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogConfirmacion.show();

                    TextView textViewTituloConfirmacion = customView.findViewById(R.id.textViewTituloConfirmacion);
                    textViewTituloConfirmacion.setText("¿Seguro deseas finalizar esta actividad? \nrecuerda que una vez hecho esto ya no podras mandar evidencias");
                    Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                    Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);

                    buttonAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String selectedEstado = "Finalizado";
                            onActualizarEstadoActivity(ID_actividad, selectedEstado);
                            dialogConfirmacion.dismiss();
                            dialogOpcionesDeActividad.dismiss();
                            dialogSeleccionTipoFin.dismiss();
                        }
                    });

                    buttonCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogConfirmacion.dismiss();
                        }
                    });


                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View customView = LayoutInflater.from(context).inflate(R.layout.opciones_confirmacion, null);
            builder.setView(ModalRedondeado(context, customView));
            AlertDialog dialogConfirmacion = builder.create();
            dialogConfirmacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogConfirmacion.show();

            TextView textViewTituloConfirmacion = customView.findViewById(R.id.textViewTituloConfirmacion);
            textViewTituloConfirmacion.setText("¿Seguro deseas finalizar esta actividad? \nrecuerda que una vez hecho esto ya no podras mandar evidencias");
            Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
            Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);

            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String selectedEstado = "Finalizado";
                    onActualizarEstadoActivity(ID_actividad, selectedEstado);
                    dialogConfirmacion.dismiss();
                    dialogOpcionesDeActividad.dismiss();
                }
            });

            buttonCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogConfirmacion.dismiss();
                }
            });

        }

    }


    List<JSONObject> saldoDeUsuarios = new ArrayList<>();
    AdaptadorMostrarSaldoParaUsuarios adaptadorMostrarSaldoParaUsuarios;

    private void VerSaldos(String ID_usuario) {
        saldoDeUsuarios.clear();
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    // Accede al array desglose_saldo_por_caja dentro del objeto principal
                    JSONArray jsonArray = jsonResponse.getJSONArray("desglose_saldo_por_caja");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String status_saldo = jsonObject.getString("status_saldo");
                        if (status_saldo.equalsIgnoreCase("Activo")) {
                            saldoDeUsuarios.add(jsonObject);
                        }
                    }

                    adaptadorMostrarSaldoParaUsuarios.notifyDataSetChanged();
                    adaptadorMostrarSaldoParaUsuarios.setFilteredData(saldoDeUsuarios);
                    adaptadorMostrarSaldoParaUsuarios.filter("");

                    SinSaldoActivoSinCont.setVisibility(View.GONE);
                    SinSaldoActivo.setVisibility(View.GONE);


                } catch (JSONException e) {
                    //  Utils.crearToastPersonalizado(context, "Datos: " + e.getMessage());
                    SinSaldoActivoSinCont.setVisibility(View.VISIBLE);
                    SinSaldoActivo.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "Algo fallo al cargar, revisa la conexion");
                SinSaldoActivoSinCont.setVisibility(View.VISIBLE);
                SinSaldoActivo.setVisibility(View.VISIBLE);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "77");
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }







    /*
    private void MostrarSaldoActivo(String ID_usuario) {
        listaSaldosActivos.clear();
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                adaptadorNuevosSaldos = new AdaptadorNuevosSaldos(dataList, context);
                recyclerViewSaldosActivos2.setLayoutManager(new LinearLayoutManager(context));
                recyclerViewSaldosActivos2.setAdapter(adaptadorNuevosSaldos);

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        listaSaldosActivos.add(jsonObject);
                    }

                    adaptadorNuevosSaldos.notifyDataSetChanged();
                    adaptadorNuevosSaldos.setFilteredData(dataList);
                    adaptadorNuevosSaldos.filter("");

                    if (listaSaldosActivos.size() > 0) {
                        recyclerViewSaldosActivos2.setVisibility(View.VISIBLE);

                    } else {
                        recyclerViewSaldosActivos2.setVisibility(View.GONE);

                    }


                } catch (JSONException e) {
                    recyclerViewSaldosActivos2.setVisibility(View.GONE);

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                recyclerViewSaldosActivos2.setVisibility(View.GONE);

                Utils.crearToastPersonalizado(context, "No se pudieron cargar los datos");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "77");
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }
    */

/*
    private void MostrarSaldoActivo(String ID_usuario) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Oa:", response);

                if (response.equalsIgnoreCase("sin saldo activo")) {

                    saldoActual.setText("No tienes saldo asignado");
                    saldoActualSinCont.setText("No tienes saldo asignado");

                } else {

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        // Obtén el primer elemento del array (asumiendo que solo hay uno)
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        ID_saldo = jsonObject.getString("ID_saldo");
                        Double saldo_actualizado = jsonObject.getDouble("saldo_actualizado");
                        String caja = jsonObject.getString("caja");
                        Double saldo_inicial = jsonObject.getDouble("saldo_inicial");
                        Double gastos_Cajagastos = jsonObject.getDouble("gastos_Cajagastos");
                        Double gastos_CajaCapital = jsonObject.getDouble("gastos_CajaCapital");
                        Double depositos_Cajagastos = jsonObject.getDouble("depositos_Cajagastos");
                        Double depositos_CajaCapital = jsonObject.getDouble("depositos_CajaCapital");


                        Double sumaGastos = 0.0;
                        Double sumaCapital = 0.0;

                        if (caja.equalsIgnoreCase("Gastos")) {
                            sumaGastos = saldo_inicial + depositos_Cajagastos - gastos_Cajagastos;
                            sumaCapital = depositos_CajaCapital - gastos_CajaCapital;


                        } else {
                            sumaCapital = saldo_inicial + depositos_CajaCapital - gastos_CajaCapital;
                            sumaGastos = depositos_Cajagastos - gastos_Cajagastos;

                        }

                        //      saldoActual.setText("Saldo actual: " + saldo_actualizado + "$");
                        //    saldoActualSinCont.setText("Saldo actual: " + saldo_actualizado + "$");


                        saldoActual.setText("Saldo actual Gastos: " + sumaGastos + "$  \nSaldo actual Capital: " + sumaCapital + " $");
                        saldoActualSinCont.setText("Saldo actual Gastos: " + sumaGastos + "$  \nSaldo actual Capital: " + sumaCapital + " $");


                    } catch (JSONException e) {
                        e.printStackTrace();
                        saldoActual.setText("Hubo un error al cargar el saldo, recarga los datos");

                        saldoActualSinCont.setText("Hubo un error al cargar el saldo, recarga los datos");
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                saldoActual.setText("Hubo un error al cargar el saldo, recarga los datos");
                saldoActualSinCont.setText("Hubo un error al cargar el saldo, recarga los datos");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "65");
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }
*/


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
                        String tipo_actividad = jsonObject.getString("tipo_actividad");


                        if (!tipo_actividad.equals("OCULTA")) {

                            if (permisos.equalsIgnoreCase("OFICINISTA")) {
                                nombresActividades.add(jsonObject);
                            } else {
                                if (!tipo_actividad.equalsIgnoreCase("OFICINAS")) {

                                    nombresActividades.add(jsonObject);
                                }

                            }

                        }
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

        VerSaldos(ID_usuario);
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
                        if (estadoActividad.equalsIgnoreCase("Pendiente") || estadoActividad.equalsIgnoreCase("Iniciado")) {
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
                params.put("opcion", "72");
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
    public void onEditActivity(String ID_actividad, String ID_nombre_actividad, String
            descripcionActividad) {
        EditarActividad(ID_actividad, ID_nombre_actividad, descripcionActividad);
    }

    public void onMandarUbicacionActicity(String ID_usuario, String ID_actividad, Double
            longitud, Double latitud) {
        MandarUbicacion(ID_usuario, ID_actividad, longitud, latitud);
    }

    @Override
    public void onDeleteActivity(String ID_actividad) {
        EliminarActividad(ID_actividad);
    }


    public void onActualizarEstadoActivity(String ID_actividad, String nuevoEstado) {
        ActualizarEstado(ID_actividad, nuevoEstado);
    }

    public void onCancelarActividadesActivity(String ID_actividad, String nuevoEstado, String
            motivoCancelacion) {
        CancelarActividades(ID_actividad, nuevoEstado, motivoCancelacion);
    }

    @Override
    public void onAgregarActividad(String idNombreActividad, String descripcion) {

//        Utils.crearToastPersonalizado(context, idNombreActividad + descripcion + ID_usuario);
        AgregarActividad(idNombreActividad, descripcion, ID_usuario);

    }


    @Override
    public void onAsignarMontoAActividad(String total_gastado, String ID_saldo, String
            ID_actividad, String valorCheck) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //        dialogActividades.dismiss();
                ActividadesPorUsuario(ID_usuario);
                Utils.crearToastPersonalizado(context, "Finalizada Correctamente");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.crearToastPersonalizado(context, "No se pudo finalizar la actividad, revisa tu conexión");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "63");
                params.put("total_gastado", total_gastado);
                params.put("ID_saldo", ID_saldo);
                params.put("ID_actividad", ID_actividad);
                params.put("tipo_caja", valorCheck);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);


    }

    private void AgregarActividad(String ID_nombre_actividad, String
            descripcionActividad, String ID_usuario) {
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


    private void EditarActividad(String ID_actividad, String ID_nombre_actividad, String
            descripcionActividad) {
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

    private void MandarUbicacion(String ID_usuario, String ID_actividad, Double
            longitud, Double latitud) {

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

    @Override
    public void onAsignarGastoAActividad(String ID_actividad, String ID_saldo, String dinero_gastado) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ActividadesPorUsuario(ID_usuario);
                Utils.crearToastPersonalizado(context, "Finalizaste correctamente la actividad");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "No se pudo actualizar la actividad, revisa tu conexión");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "79");
                params.put("ID_actividad", ID_actividad);
                params.put("dinero_gastado", dinero_gastado);
                params.put("ID_saldo", ID_saldo);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);


    }
}

