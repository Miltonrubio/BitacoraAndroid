package com.bitala.bitacora;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
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
import com.bitala.bitacora.Adaptadores.AdaptadorRegistrosSaldos;
import com.bitala.bitacora.Adaptadores.AdaptadorSelectorSaldosNuevo;
import com.bitala.bitacora.Adaptadores.NuevoDownloadFileTask;

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

public class NuevoGastosFragment extends Fragment {


    public NuevoGastosFragment() {

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

    AdaptadorRegistrosSaldos adaptadorGastos;
    String fechaFinalSeleccionada = "";
    String fechaInicialSeleccionada = "";
    String nombre;
    SwipeRefreshLayout swipeRefreshLayout;


    String ID_saldo;
    AdaptadorSelectorSaldosNuevo adaptadorSelectorSaldosNuevo;

    ArrayList<String> idSeleccionados = new ArrayList<>();
    String pdfGastosCarta;
    String pdfGastosTicket;
    String ID_sesionIniciada;

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

        pdfGastosCarta = context.getResources().getString(R.string.pdfGastosCarta);
        pdfGastosTicket = context.getResources().getString(R.string.pdfGastosTicket);

        adaptadorGastos = new AdaptadorRegistrosSaldos(listaGastos, context);


        recyclerViewGastos.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewGastos.setAdapter(adaptadorGastos);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);

        ID_sesionIniciada = sharedPreferences.getString("ID_usuario", "");


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

                VerTodosLosGastos(ID_usuario);
            }
        });

        buttonTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                VerTodosLosGastos(ID_usuario);
            }
        });

        botonPDFGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idSeleccionados.clear();


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View customView = LayoutInflater.from(context).inflate(R.layout.modal_selector_de_saldos, null);
                builder.setView(Utils.ModalRedondeado(context, customView));
                AlertDialog dialogSelectorSaldos = builder.create();
                dialogSelectorSaldos.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogSelectorSaldos.show();

                TextView searchEditTextSelector = customView.findViewById(R.id.searchEditTextSelector);
                RecyclerView recyclerViewSelectorSaldos = customView.findViewById(R.id.recyclerViewSelectorSaldos);
                //    Utils.crearToastPersonalizado(context, listaGastos.toString());


                adaptadorSelectorSaldosNuevo = new AdaptadorSelectorSaldosNuevo(detallesGastoArray, view.getContext());
                recyclerViewSelectorSaldos.setLayoutManager(new LinearLayoutManager(context));

                Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                Button butonImprimirPDF = customView.findViewById(R.id.butonImprimirPDF);

                recyclerViewSelectorSaldos.setAdapter(adaptadorSelectorSaldosNuevo);
                adaptadorSelectorSaldosNuevo.setFilteredData(detallesGastoArray);
                adaptadorSelectorSaldosNuevo.filter("");
                adaptadorSelectorSaldosNuevo.notifyDataSetChanged();

                searchEditTextSelector.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adaptadorSelectorSaldosNuevo.filter(s.toString().toLowerCase());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                adaptadorSelectorSaldosNuevo.setOnItemClickListener(new AdaptadorSelectorSaldosNuevo.OnItemClickListener() {
                    @Override
                    public void onItemClick(String id) {

                        if (idSeleccionados.contains(id)) {
                            idSeleccionados.remove(id);
                        } else {
                            idSeleccionados.add(id);
                        }
                        adaptadorSelectorSaldosNuevo.notifyDataSetChanged();
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

                        if (idSeleccionados.size() > 0) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View customView = LayoutInflater.from(context).inflate(R.layout.opciones_imprimir_pdf_saldos, null);

                            builder.setView(Utils.ModalRedondeado(context, customView));
                            AlertDialog dialogTipoPDF = builder.create();
                            dialogTipoPDF.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogTipoPDF.show();


                            LinearLayout LayoutPDFcarta = customView.findViewById(R.id.LayoutPDFcarta);
                            LinearLayout LayoutTicket = customView.findViewById(R.id.LayoutTicket);

                            LayoutTicket.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // modalCargando = Utils.ModalCargando(context, builder);
                                    dialogSelectorSaldos.dismiss();
                                    dialogTipoPDF.dismiss();
                                    enviarDatosPDFTicket();
                                }
                            });


                            LayoutPDFcarta.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                  //  modalCargando = Utils.ModalCargando(context, builder);
                                    dialogSelectorSaldos.dismiss();
                                    dialogTipoPDF.dismiss();
                                    enviarDatosPorPostTamCarta();


                                }
                            });


                        } else {
                            Utils.crearToastPersonalizado(context, "No hay elementos seleccionados");
                        }
                    }
                });




/*
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

                adaptadorSeleccionarSaldos = new AdaptadorSeleccionarSaldos(listaGastos, view.getContext());
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
                            idSeleccionados.remove(id);
                        } else {
                            idSeleccionados.add(id);
                        }
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

                        if (idSeleccionados.size() > 0) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View customView = LayoutInflater.from(context).inflate(R.layout.opciones_imprimir_pdf_saldos, null);

                            builder.setView(Utils.ModalRedondeado(context, customView));
                            AlertDialog dialogSelectorSaldos = builder.create();
                            dialogSelectorSaldos.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogSelectorSaldos.show();


                            LinearLayout LayoutPDFcarta = customView.findViewById(R.id.LayoutPDFcarta);
                            LinearLayout LayoutTicket = customView.findViewById(R.id.LayoutTicket);

                            LayoutTicket.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    modalCargando = Utils.ModalCargando(context, builder);
                                    enviarDatosPDFTicket();
                                }
                            });


                            LayoutPDFcarta.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    modalCargando = Utils.ModalCargando(context, builder);
                                    enviarDatosPorPostTamCarta();
                                }
                            });


                        } else {
                            Utils.crearToastPersonalizado(context, "No hay elementos seleccionados");
                        }


                    }
                });

 */
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


        return view;

    }


    List<JSONObject> detallesGastoArray = new ArrayList<>();

    /*
    private void TomarTodosLosRegistrosDeSaldo(String ID_usuario) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONArray desgloseArray = jsonObject.getJSONArray("desglose_saldo_por_caja");

                        for (int j = 0; j < desgloseArray.length(); j++) {
                            detallesGastoArray.add(desgloseArray.getJSONObject(j));
                        }
                    }

                    if (detallesGastoArray.size() > 0) {
                        mostrarLayouts("ConDatos");
                    } else {
                        mostrarLayouts("SinDatos");
                    }

                    adaptadorGastos.notifyDataSetChanged();
                    adaptadorGastos.setFilteredData(detallesGastoArray);
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
                params.put("opcion", "80");
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }
*/


    private String convertirArrayListAJson(ArrayList<String> arrayList) {
        JSONArray jsonArray = new JSONArray(arrayList);
        return jsonArray.toString();
    }


    private void enviarDatosPorPostTamCarta() {

        //Utils.crearToastPersonalizado(context, idSeleccionados.toString());

        String lista = convertirArrayListAJson(idSeleccionados);

        Log.d("Lista: ", lista);

        Map<String, String> postData = new HashMap<>();
        postData.put("ID_usuario", ID_usuario);

        // Convertir el ArrayList a JSON
        postData.put("listaSeleccion", lista);
    //    new DownloadFileTask(context, postData).execute(pdfGastosCarta);
        new NuevoDownloadFileTask(context, postData).execute(pdfGastosCarta);
    }


    private void enviarDatosPDFTicket() {
        //Utils.crearToastPersonalizado(context, idSeleccionados.toString());

        String lista = convertirArrayListAJson(idSeleccionados);

        Log.d("Lista: ", lista);

        Map<String, String> postData = new HashMap<>();
        postData.put("ID_usuario", ID_usuario);
        postData.put("ID_encargado", ID_sesionIniciada);
        postData.put("listaSeleccion", lista);
      //  new DownloadFileTask(context, postData).execute(pdfGastosTicket);
        new NuevoDownloadFileTask(context, postData).execute(pdfGastosTicket);
    }


    private void VerGastosPorFecha(String ID_usuario, String fechaInicio, String fechaFin) {
        listaGastos.clear();
        detallesGastoArray.clear();
        modalCargando = Utils.ModalCargando(context, builder);
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONArray desgloseArray = jsonObject.getJSONArray("desglose_saldo_por_caja");

                        for (int j = 0; j < desgloseArray.length(); j++) {
                            detallesGastoArray.add(desgloseArray.getJSONObject(j));
                        }

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
                params.put("opcion", "83");
                params.put("ID_usuario", ID_usuario);
                params.put("fecha_inicioBusqueda", fechaInicio);
                params.put("fecha_finBusqueda", fechaFin);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void VerTodosLosGastos(String ID_usuario) {
        listaGastos.clear();
        detallesGastoArray.clear();
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
                        JSONArray desgloseArray = jsonObject.getJSONArray("desglose_saldo_por_caja");

                        listaGastos.add(jsonObject);

                        for (int j = 0; j < desgloseArray.length(); j++) {
                            detallesGastoArray.add(desgloseArray.getJSONObject(j));
                        }

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
                params.put("opcion", "80");
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