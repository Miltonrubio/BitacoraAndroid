package com.bitala.bitacora;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.bitala.bitacora.Adaptadores.AdaptadorGastos;
import com.bitala.bitacora.Adaptadores.AdaptadorSeleccionarSaldos;
import com.bitala.bitacora.Adaptadores.DownloadFileTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GastosFragment extends Fragment {


    public GastosFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    ConstraintLayout ContenedorContenido;
    ConstraintLayout ContenedorSinContenido;
    ConstraintLayout ContenedorSinInternet;

    Button buttonFiltrarPorFecha;
    Button buttonTodos;

    String ID_usuario;
    Context context;
    List<JSONObject> listaGastos = new ArrayList<>();

    AlertDialog modalCargando;
    AlertDialog.Builder builder;
    String url;

    AdaptadorGastos adaptadorGastos;
    String fechaFinalSeleccionada = "";
    String fechaInicialSeleccionada = "";
    String nombre;
    SwipeRefreshLayout swipeRefreshLayout;


    String ID_saldo;
    AdaptadorSeleccionarSaldos adaptadorSeleccionarSaldos;

    ArrayList<String> idSeleccionados = new ArrayList<>();
    String pdfGastos;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gastos, container, false);


        ContenedorSinContenido = view.findViewById(R.id.ContenedorSinContenido);
        ContenedorSinInternet = view.findViewById(R.id.ContenedorSinInternet);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        ContenedorContenido = view.findViewById(R.id.ContenedorContenido);

        Button buttonFiltrarPorFecha2 = view.findViewById(R.id.buttonFiltrarPorFecha2);
        Button buttonTodos2 = view.findViewById(R.id.buttonTodos2);

        buttonTodos = view.findViewById(R.id.buttonTodos);

        buttonFiltrarPorFecha = view.findViewById(R.id.buttonFiltrarPorFecha);
        ImageView botonPDFGastos = view.findViewById(R.id.botonPDFGastos);
        RecyclerView recyclerViewGastos = view.findViewById(R.id.recyclerViewGastos);
        TextView textView3 = view.findViewById(R.id.textView3);

        context = requireContext();
        url = context.getResources().getString(R.string.urlApi);
        builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        pdfGastos= context.getResources().getString(R.string.pdfGastos);

        adaptadorGastos = new AdaptadorGastos(listaGastos, context);




        recyclerViewGastos.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewGastos.setAdapter(adaptadorGastos);


        Bundle bundle = getArguments();
        if (bundle != null) {


            ID_usuario = bundle.getString("ID_usuario", "");
            nombre = bundle.getString("nombre", "");
            ID_saldo = bundle.getString("ID_saldo", "");
            VerTodosLosGastos(ID_usuario);


            textView3.setText("CONTROL DE GASTOS DE " + nombre.toUpperCase());
        }


        buttonTodos2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

/*
                View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_filtrado_caja, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                AlertDialog dialogOpcionesUsuarios = builder.create();
                ColorDrawable back = new ColorDrawable(Color.BLACK);
                back.setAlpha(150);
                dialogOpcionesUsuarios.getWindow().setBackgroundDrawable(back);
                dialogOpcionesUsuarios.getWindow().setDimAmount(0.8f);
                dialogOpcionesUsuarios.show();
*/

                VerTodosLosGastos(ID_usuario);
            }
        });

        buttonTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

/*
                View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_filtrado_caja, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                AlertDialog dialogOpcionesUsuarios = builder.create();
                ColorDrawable back = new ColorDrawable(Color.BLACK);
                back.setAlpha(150);
                dialogOpcionesUsuarios.getWindow().setBackgroundDrawable(back);
                dialogOpcionesUsuarios.getWindow().setDimAmount(0.8f);
                dialogOpcionesUsuarios.show();
*/

                VerTodosLosGastos(ID_usuario);
            }
        });


        buttonFiltrarPorFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View customView = LayoutInflater.from(context).inflate(R.layout.modal_seleccionar_fechas, null);
                builder.setView(Utils.ModalRedondeadoContornoVerde(context, customView));
                AlertDialog dialogSeleccionarFecha = builder.create();
                dialogSeleccionarFecha.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogSeleccionarFecha.show();

                TextView fechaInicial = customView.findViewById(R.id.fechaInicial);
                TextView fechaFinal = customView.findViewById(R.id.fechaFinal);
                Button btnBuscar = customView.findViewById(R.id.btnBuscar);


                fechaInicial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View customView = LayoutInflater.from(context).inflate(R.layout.modal_calendario, null);
                        builder.setView(Utils.ModalRedondeadoContornoVerde(context, customView));
                        AlertDialog dialogCalendario = builder.create();
                        dialogCalendario.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogCalendario.show();

                        Button btnCancelar = customView.findViewById(R.id.btnCancelar);
                        Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                        DatePicker datePickerFecha = customView.findViewById(R.id.datePickerFecha);

                        btnCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogCalendario.dismiss();
                            }
                        });


                        buttonAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int day = datePickerFecha.getDayOfMonth();
                                int month = datePickerFecha.getMonth(); // El mes comienza desde 0 (enero = 0, febrero = 1, etc.)
                                int year = datePickerFecha.getYear();

                                fechaInicialSeleccionada = year + "-" + (month + 1) + "-" + day; // El mes se incrementa en 1 para ajustar a la convención (enero = 1, febrero = 2, etc.)


                                try {
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                    Date date = inputFormat.parse(fechaInicialSeleccionada);
                                    SimpleDateFormat outputDayOfWeek = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
                                    String dayOfWeek = outputDayOfWeek.format(date);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
                                    String formattedDate = outputFormat.format(date);

                                    fechaInicial.setText(dayOfWeek.toLowerCase() + " " + formattedDate.toLowerCase());

                                } catch (ParseException e) {
                                }

                                dialogCalendario.dismiss();
                            }
                        });


                    }
                });


                fechaFinal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View customView = LayoutInflater.from(context).inflate(R.layout.modal_calendario, null);
                        builder.setView(Utils.ModalRedondeadoContornoVerde(context, customView));
                        AlertDialog dialogCalendario = builder.create();
                        dialogCalendario.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogCalendario.show();

                        Button btnCancelar = customView.findViewById(R.id.btnCancelar);
                        Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                        DatePicker datePickerFecha = customView.findViewById(R.id.datePickerFecha);

                        btnCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogCalendario.dismiss();
                            }
                        });


                        buttonAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int day = datePickerFecha.getDayOfMonth();
                                int month = datePickerFecha.getMonth(); // El mes comienza desde 0 (enero = 0, febrero = 1, etc.)
                                int year = datePickerFecha.getYear();

                                fechaFinalSeleccionada = year + "-" + (month + 1) + "-" + day; // El mes se incrementa en 1 para ajustar a la convención (enero = 1, febrero = 2, etc.)


                                try {
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                    Date date = inputFormat.parse(fechaFinalSeleccionada);
                                    SimpleDateFormat outputDayOfWeek = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
                                    String dayOfWeek = outputDayOfWeek.format(date);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
                                    String formattedDate = outputFormat.format(date);

                                    fechaFinal.setText(dayOfWeek.toLowerCase() + " " + formattedDate.toLowerCase());

                                } catch (ParseException e) {
                                }

                                dialogCalendario.dismiss();
                            }
                        });


                    }
                });


                btnBuscar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if ((fechaFinalSeleccionada.isEmpty() || fechaFinalSeleccionada.equals("")) || (fechaInicialSeleccionada.isEmpty() || fechaInicialSeleccionada.equals(""))) {

                            Utils.crearToastPersonalizado(context, "Debes seleccionar fecha de Inicio y final ");

                        } else {

                            VerGastosPorFecha(ID_usuario, fechaInicialSeleccionada, fechaFinalSeleccionada);
                            dialogSeleccionarFecha.dismiss();

                        }
                    }
                });


            }
        });


        buttonFiltrarPorFecha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View customView = LayoutInflater.from(context).inflate(R.layout.modal_seleccionar_fechas, null);
                builder.setView(Utils.ModalRedondeadoContornoVerde(context, customView));
                AlertDialog dialogSeleccionarFecha = builder.create();
                dialogSeleccionarFecha.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogSeleccionarFecha.show();

                TextView fechaInicial = customView.findViewById(R.id.fechaInicial);

                TextView fechaFinal = customView.findViewById(R.id.fechaFinal);
                Button btnBuscar = customView.findViewById(R.id.btnBuscar);


                fechaInicial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View customView = LayoutInflater.from(context).inflate(R.layout.modal_calendario, null);
                        builder.setView(Utils.ModalRedondeadoContornoVerde(context, customView));
                        AlertDialog dialogCalendario = builder.create();
                        dialogCalendario.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogCalendario.show();

                        Button btnCancelar = customView.findViewById(R.id.btnCancelar);
                        Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                        DatePicker datePickerFecha = customView.findViewById(R.id.datePickerFecha);

                        btnCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogCalendario.dismiss();
                            }
                        });


                        buttonAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int day = datePickerFecha.getDayOfMonth();
                                int month = datePickerFecha.getMonth(); // El mes comienza desde 0 (enero = 0, febrero = 1, etc.)
                                int year = datePickerFecha.getYear();

                                fechaInicialSeleccionada = year + "-" + (month + 1) + "-" + day; // El mes se incrementa en 1 para ajustar a la convención (enero = 1, febrero = 2, etc.)


                                try {
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                    Date date = inputFormat.parse(fechaInicialSeleccionada);
                                    SimpleDateFormat outputDayOfWeek = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
                                    String dayOfWeek = outputDayOfWeek.format(date);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
                                    String formattedDate = outputFormat.format(date);

                                    fechaInicial.setText(dayOfWeek.toLowerCase() + " " + formattedDate.toLowerCase());

                                } catch (ParseException e) {
                                }

                                dialogCalendario.dismiss();
                            }
                        });


                    }
                });


                fechaFinal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View customView = LayoutInflater.from(context).inflate(R.layout.modal_calendario, null);
                        builder.setView(Utils.ModalRedondeadoContornoVerde(context, customView));
                        AlertDialog dialogCalendario = builder.create();
                        dialogCalendario.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogCalendario.show();

                        Button btnCancelar = customView.findViewById(R.id.btnCancelar);
                        Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                        DatePicker datePickerFecha = customView.findViewById(R.id.datePickerFecha);

                        btnCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogCalendario.dismiss();
                            }
                        });


                        buttonAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int day = datePickerFecha.getDayOfMonth();
                                int month = datePickerFecha.getMonth(); // El mes comienza desde 0 (enero = 0, febrero = 1, etc.)
                                int year = datePickerFecha.getYear();

                                fechaFinalSeleccionada = year + "-" + (month + 1) + "-" + day; // El mes se incrementa en 1 para ajustar a la convención (enero = 1, febrero = 2, etc.)


                                try {
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                    Date date = inputFormat.parse(fechaFinalSeleccionada);
                                    SimpleDateFormat outputDayOfWeek = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
                                    String dayOfWeek = outputDayOfWeek.format(date);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
                                    String formattedDate = outputFormat.format(date);

                                    fechaFinal.setText(dayOfWeek.toLowerCase() + " " + formattedDate.toLowerCase());

                                } catch (ParseException e) {
                                }

                                dialogCalendario.dismiss();
                            }
                        });


                    }
                });


                btnBuscar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if ((fechaFinalSeleccionada.isEmpty() || fechaFinalSeleccionada.equals("")) || (fechaInicialSeleccionada.isEmpty() || fechaInicialSeleccionada.equals(""))) {

                            Utils.crearToastPersonalizado(context, "Debes seleccionar fecha de Inicio y final ");

                        } else {

                            VerGastosPorFecha(ID_usuario, fechaInicialSeleccionada, fechaFinalSeleccionada);
                            dialogSeleccionarFecha.dismiss();

                        }
                    }
                });


            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if ((!fechaInicialSeleccionada.equals("") || !fechaInicialSeleccionada.isEmpty()) && (!fechaFinalSeleccionada.isEmpty() || !fechaFinalSeleccionada.equals(""))) {
                    VerGastosPorFecha(ID_usuario, fechaInicialSeleccionada, fechaFinalSeleccionada);
                } else {
                    VerTodosLosGastos(ID_usuario);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        botonPDFGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View customView = LayoutInflater.from(context).inflate(R.layout.modal_selector_de_saldos, null);

                builder.setView(Utils.ModalRedondeado(context, customView));
                AlertDialog dialogSelectorSaldos = builder.create();
                dialogSelectorSaldos.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogSelectorSaldos.show();



                Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                Button butonImprimirPDF = customView.findViewById(R.id.butonImprimirPDF);
                RecyclerView recyclerViewSelectorSaldos = customView.findViewById(R.id.recyclerViewSelectorSaldos);
                TextView searchEditTextSelector = customView.findViewById(R.id.searchEditTextSelector);

                adaptadorSeleccionarSaldos = new AdaptadorSeleccionarSaldos(listaGastos,view.getContext());
                recyclerViewSelectorSaldos.setLayoutManager(new LinearLayoutManager(context));

                recyclerViewSelectorSaldos.setAdapter(adaptadorSeleccionarSaldos);
                adaptadorSeleccionarSaldos.setFilteredData(listaGastos);
                adaptadorSeleccionarSaldos.filter("");
                adaptadorSeleccionarSaldos.notifyDataSetChanged();


                searchEditTextSelector.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adaptadorSeleccionarSaldos.filter(s.toString().toLowerCase());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });





                adaptadorSeleccionarSaldos.setOnItemClickListener(new AdaptadorSeleccionarSaldos.OnItemClickListener() {
                    @Override
                    public void onItemClick(String id) {

                        if (idSeleccionados.contains(id)) {
                            // Si ya está en la lista, quitarlo
                            idSeleccionados.remove(id);

                            // Aquí puedes ajustar la visibilidad del icono según sea necesario
                            // Por ejemplo, si el iconoMarcado es una ImageView, puedes hacer algo como:
                            // iconoMarcado.setVisibility(View.GONE);
                        } else {
                            // Si no está en la lista, agregarlo
                            idSeleccionados.add(id);

                            // Aquí puedes ajustar la visibilidad del icono según sea necesario
                            // Por ejemplo, si el iconoMarcado es una ImageView, puedes hacer algo como:
                            // iconoMarcado.setVisibility(View.VISIBLE);
                        }

                        // Actualizar la interfaz de usuario según sea necesario
                        adaptadorSeleccionarSaldos.notifyDataSetChanged();
                    }
                });


                buttonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogSelectorSaldos.dismiss();
                    }
                });


                butonImprimirPDF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //   Utils.crearToastPersonalizado(context, idSeleccionados.toString());

                        if (idSeleccionados.size() > 0) {

                            enviarDatosPorPost();
                        } else {
                            Utils.crearToastPersonalizado(context, "No hay elementos seleccionados");
                        }

                    }
                });

                /*
                Map<String, String> postData = new HashMap<>();
                if ((!fechaInicialSeleccionada.equals("") || !fechaInicialSeleccionada.isEmpty()) && (!fechaFinalSeleccionada.isEmpty() || !fechaFinalSeleccionada.equals(""))) {

                    postData.put("opcion", "59");
                    postData.put("ID_usuario", ID_usuario);
                    postData.put("fechaInicio", fechaInicialSeleccionada);
                    postData.put("fechaFin", fechaFinalSeleccionada);
                } else {

                    postData.put("opcion", "59");
                    postData.put("ID_usuario", ID_usuario);
                    postData.put("fechaInicio", "");
                    postData.put("fechaFin", "");
                }


                new DownloadFileTask(context, postData).execute(url);

                 */

            }

        });


        return view;

    }

    private String convertirArrayListAJson(ArrayList<String> arrayList) {
        JSONArray jsonArray = new JSONArray(arrayList);
        return jsonArray.toString();
    }



    private void enviarDatosPorPost() {

        //Utils.crearToastPersonalizado(context, idSeleccionados.toString());

        String lista = convertirArrayListAJson(idSeleccionados);

        Log.d("Lista: ", lista);

        Map<String, String> postData = new HashMap<>();
        postData.put("ID_usuario", ID_usuario);

        // Convertir el ArrayList a JSON
        postData.put("listaSeleccion", lista);
        new DownloadFileTask(context, postData).execute(pdfGastos);
    }

    private void VerGastosPorFecha(String ID_usuario, String fechaInicio, String fechaFin) {
        listaGastos.clear();
        modalCargando = Utils.ModalCargando(context, builder);
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        listaGastos.add(jsonObject);
                    }

                    if (listaGastos.size() > 0) {
                        mostrarLayouts("ConDatos");

                    } else {

                        mostrarLayouts("SinDatos");
                    }

                    adaptadorGastos.notifyDataSetChanged();
                    adaptadorGastos.setFilteredData(listaGastos);
                    adaptadorGastos.filter("");

                } catch (JSONException e) {

                    mostrarLayouts("SinDatos");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mostrarLayouts("SinInternet");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "58");
                params.put("ID_usuario", ID_usuario);
                params.put("fechaInicio", fechaInicio);
                params.put("fechaFin", fechaFin);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void VerTodosLosGastos(String ID_usuario) {
        listaGastos.clear();
        fechaInicialSeleccionada = "";
        fechaFinalSeleccionada = "";
        modalCargando = Utils.ModalCargando(context, builder);
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String ID_saldoactivo = jsonObject.getString("ID_saldo");

                        listaGastos.add(jsonObject);
                        /*

                        if (mostrarTodos.equalsIgnoreCase("Si")) {

                            listaGastos.add(jsonObject);
                        } else {

                            if (ID_saldo.equalsIgnoreCase(ID_saldoactivo)) {

                                listaGastos.add(jsonObject);

                            } else {
                                mostrarLayouts("SinDatos");
                            }

                        }

                         */


                    }

                    if (listaGastos.size() > 0) {
                        mostrarLayouts("ConDatos");

                    } else {

                        mostrarLayouts("SinDatos");
                    }

                    adaptadorGastos.notifyDataSetChanged();
                    adaptadorGastos.setFilteredData(listaGastos);
                    adaptadorGastos.filter("");

                } catch (JSONException e) {

                    mostrarLayouts("SinDatos");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mostrarLayouts("SinInternet");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "57");
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }

    private void mostrarLayouts(String estado) {

        if (estado.equalsIgnoreCase("SinDatos")) {

            ContenedorSinContenido.setVisibility(View.VISIBLE);
            ContenedorContenido.setVisibility(View.GONE);
            ContenedorSinInternet.setVisibility(View.GONE);

        } else if (estado.equalsIgnoreCase("SinInternet")) {

            ContenedorSinContenido.setVisibility(View.GONE);
            ContenedorContenido.setVisibility(View.GONE);
            ContenedorSinInternet.setVisibility(View.VISIBLE);
        } else {

            ContenedorSinContenido.setVisibility(View.GONE);
            ContenedorContenido.setVisibility(View.VISIBLE);
            ContenedorSinInternet.setVisibility(View.GONE);

        }

        onLoadComplete();
    }


    private void onLoadComplete() {
        if (modalCargando.isShowing() && modalCargando != null) {
            modalCargando.dismiss();
        }
    }
}