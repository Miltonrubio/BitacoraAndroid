package com.bitala.bitacora;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bitala.bitacora.Adaptadores.AdaptadorListaAsignarActividades;
import com.bitala.bitacora.Adaptadores.AdaptadorUsuarios;
import com.bitala.bitacora.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

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

public class CrudUsuariosFragment extends Fragment implements AdaptadorUsuarios.OnActivityActionListener {

    String url;
    private RecyclerView recyclerViewUsuarios;
    private AdaptadorUsuarios adaptadorUsuarios;
    private List<JSONObject> dataList = new ArrayList<>();
    private EditText editTextBusqueda;
    private FloatingActionButton botonAgregarActividad;

    Context context;

    RelativeLayout LayoutContenido;

    ConstraintLayout SinActividades;
    ConstraintLayout LayoutSinInternet;


    AlertDialog.Builder builderCargando;

    AlertDialog modalCargando;
    String permisosUsuario;
    String Sesion_UsuarioID;

    SwipeRefreshLayout swipeRefreshLayout;

    AlertDialog dialogActividades;
    String clave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crud_usuarios, container, false);

        botonAgregarActividad = view.findViewById(R.id.botonAgregarActividad);
        recyclerViewUsuarios = view.findViewById(R.id.recyclerViewUsuarios);
        editTextBusqueda = view.findViewById(R.id.searchEditTextArrastres);
        LayoutContenido = view.findViewById(R.id.LayoutContenido);
        SinActividades = view.findViewById(R.id.SinActividades);
        LayoutSinInternet = view.findViewById(R.id.LayoutSinInternet);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        context = requireContext();
        url = context.getResources().getString(R.string.urlApi);


        builderCargando = new AlertDialog.Builder(view.getContext());
        builderCargando.setCancelable(false);
        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        GridLayoutManager gridLayoutManagerDelantero = new GridLayoutManager(context, 2);
        recyclerViewUsuarios.setLayoutManager(gridLayoutManagerDelantero);
        adaptadorUsuarios = new AdaptadorUsuarios(dataList, context, this);
        recyclerViewUsuarios.setAdapter(adaptadorUsuarios);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        Sesion_UsuarioID = sharedPreferences.getString("ID_usuario", "");
        permisosUsuario = sharedPreferences.getString("permisos", "");
        clave = sharedPreferences.getString("clave", "");

        MostrarUsuarios();

        editTextBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adaptadorUsuarios.filter(s.toString().toLowerCase());
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        botonAgregarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                View customView = LayoutInflater.from(context).inflate(R.layout.insertar_nuevo_usuario, null);
                builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                dialogActividades = builder.create();
                dialogActividades.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogActividades.show();

                EditText textViewNombreUsuario = customView.findViewById(R.id.textViewNombreUsuario);
                EditText textViewCorreoUsuario = customView.findViewById(R.id.textViewCorreoUsuario);
                EditText TextViewClaveUsuario = customView.findViewById(R.id.TextViewClaveUsuario);
                EditText TextViewTelefonoUsuario = customView.findViewById(R.id.TextViewTelefonoUsuario);
                Spinner spinnerRolUsuario = customView.findViewById(R.id.spinnerRolUsuario);


                Button botonCancelar = customView.findViewById(R.id.botonCancelar);
                Button botonAgregarCliente = customView.findViewById(R.id.botonAgregarCliente);


                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                        R.array.opciones_rol, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Establece el ArrayAdapter en el Spinner
                spinnerRolUsuario.setAdapter(adapter);


                botonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogActividades.dismiss();
                    }
                });


                botonAgregarCliente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nombreUsuario = textViewNombreUsuario.getText().toString();
                        String correoUsuario = textViewCorreoUsuario.getText().toString();
                        String ClaveUsuario = TextViewClaveUsuario.getText().toString();
                        String TelefonoUsuario = TextViewTelefonoUsuario.getText().toString().replaceAll(" ", "");
                        String RolUsuario = spinnerRolUsuario.getSelectedItem().toString();

                        if (nombreUsuario.isEmpty() || correoUsuario.isEmpty() || ClaveUsuario.isEmpty() || TelefonoUsuario.isEmpty()) {

                            Utils.crearToastPersonalizado(context, "Tienes campos vacios, por favor rellenalos");


                        } else {

                            AgregarNuevoUsuario(nombreUsuario, correoUsuario, ClaveUsuario, TelefonoUsuario, RolUsuario, dialogActividades);
                        }
                    }
                });

                /*
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nombreUsuario = textViewNombreUsuario.getText().toString();
                        String correoUsuario = textViewCorreoUsuario.getText().toString();
                        String ClaveUsuario = TextViewClaveUsuario.getText().toString();
                        String TelefonoUsuario = TextViewTelefonoUsuario.getText().toString().replaceAll(" ", "");
                        String RolUsuario = spinnerRolUsuario.getSelectedItem().toString();

                        if (nombreUsuario.isEmpty() || correoUsuario.isEmpty() || ClaveUsuario.isEmpty() || TelefonoUsuario.isEmpty()) {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "No puedes ingresar campos vacios", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            AgregarNuevoUsuario(nombreUsuario, correoUsuario, ClaveUsuario, TelefonoUsuario, RolUsuario);
                        }
                    }
                });
             */


            }

        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MostrarUsuarios();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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


    private void animacionLupe(String estado) {
        if (estado.equals("Oculto")) {
            recyclerViewUsuarios.setVisibility(View.VISIBLE);
            SinActividades.setVisibility(View.GONE);
        } else {
            recyclerViewUsuarios.setVisibility(View.GONE);
            SinActividades.setVisibility(View.VISIBLE);
        }
    }


    private void MostrarUsuarios() {
        dataList.clear();
        modalCargando = Utils.ModalCargando(context, builderCargando);
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String permisos = jsonObject.getString("permisos");
                        String ID_usuario = jsonObject.getString("ID_usuario");

                        if (!permisos.equalsIgnoreCase("SUPERADMIN") || ID_usuario.equalsIgnoreCase(Sesion_UsuarioID)) {
                            dataList.add(jsonObject);
                        }
                    }
                    adaptadorUsuarios.notifyDataSetChanged();
                    adaptadorUsuarios.setFilteredData(dataList);
                    adaptadorUsuarios.filter("");

                    if (dataList.size() > 0) {
                        mostrarLayouts("conContenido");
                    } else {

                        mostrarLayouts("SinContenido");
                    }

                } catch (JSONException e) {

                    mostrarLayouts("SinContenido");


                }
                editTextBusqueda.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mostrarLayouts("SinInternet");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "13");
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }

    private void mostrarLayouts(String estado) {
        if (estado.equalsIgnoreCase("SinInternet")) {
            LayoutContenido.setVisibility(View.GONE);
            SinActividades.setVisibility(View.GONE);
            LayoutSinInternet.setVisibility(View.VISIBLE);

        } else if (estado.equalsIgnoreCase("SinContenido")) {

            LayoutContenido.setVisibility(View.GONE);
            SinActividades.setVisibility(View.VISIBLE);
            LayoutSinInternet.setVisibility(View.GONE);
        } else {

            LayoutContenido.setVisibility(View.VISIBLE);
            SinActividades.setVisibility(View.GONE);
            LayoutSinInternet.setVisibility(View.GONE);
        }

        onLoadComplete();
    }

    private void onLoadComplete() {
        if (modalCargando.isShowing() && modalCargando != null) {
            modalCargando.dismiss();
        }
    }


    private void AgregarNuevoUsuario(String nombreUsuario, String correoUsuario, String claveUsuario, String telefonoUsuario, String permisos, AlertDialog dialogActividades) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("Error: El correo, nombre o teléfono ya existen en la base de datos.")) {

                    Utils.crearToastPersonalizado(context, "No puedes insertar Datos repetidos");

                } else {
                    Utils.crearToastPersonalizado(context, "Agregado Correctamente");
                    dialogActividades.dismiss();
                    MostrarUsuarios();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "No se agrego, revisa la conexión");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "14");
                params.put("permisos", permisos);
                params.put("nombre", nombreUsuario);
                params.put("correo", correoUsuario);
                params.put("clave", claveUsuario);
                params.put("telefono", telefonoUsuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    @Override
    public void onEditarUsuarioActivity(String ID_usuario, String nombreUsuario, String correoUsuario, String claveUsuario, String telefonoUsuario, String rolUsuario) {
        EditarUsuario(ID_usuario, nombreUsuario, correoUsuario, claveUsuario, telefonoUsuario, rolUsuario);
    }


    @Override
    public void onEliminarUsuarioActivity(String ID_usuario, String nombre) {
        EliminarUsuario(ID_usuario, nombre);
    }


    private void EliminarUsuario(String ID_usuario, String nombre) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MostrarUsuarios();

                Utils.crearToastPersonalizado(context, "Se eliminó a " + nombre);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "No se pudo eliminó a " + nombre + " revisa la conexión");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "16");
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void EditarUsuario(String ID_usuario, String nombreUsuario, String correoUsuario, String claveUsuario, String telefonoUsuario, String rolUsuario) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MostrarUsuarios();
                Utils.crearToastPersonalizado(context, "Se actualizo a " + nombreUsuario);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.crearToastPersonalizado(context, "No se pudo actualizar a " + nombreUsuario + " revisa la conexión");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "15");
                params.put("ID_usuario", ID_usuario);
                params.put("permisos", rolUsuario);
                params.put("nombre", nombreUsuario);
                params.put("correo", correoUsuario);
                params.put("clave", claveUsuario);
                params.put("telefono", telefonoUsuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    @Override
    public void onAsignarActividadAUsuario(String idNombreActividad, String descripcion, String ID_usuario, String nombre, String token) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.crearToastPersonalizado(context, "Se le asignó la tarea a " + nombre);
                MostrarUsuarios();

                MandarNotificacionAUsuario(token, nombre, descripcion);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.crearToastPersonalizado(context, "No se pudo asignar la tarea a " + nombre + ", revisa la conexión");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "4");
                params.put("ID_nombre_actividad", idNombreActividad);
                params.put("descripcionActividad", descripcion);
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void MandarNotificacionAUsuario(String token, String nombreUsuario, String descripcion) {

        String mensaje = "Se te asignó una nueva actividad";
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Utils.crearToastPersonalizado(context, "Se envió una notificación al usuario");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.crearToastPersonalizado(context, "No se pudó enviar la notificación");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "27");
                params.put("TokenFIREBASE", token);
                params.put("TituloMensaje", mensaje);
                params.put("CuerpoMensaje", descripcion);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);

    }


    public void onCorregirSaldo(String ID_saldo, String nuevoSaldo, View view, String nombre, String ID_usuario) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Utils.crearToastPersonalizado(context, "Se actualizó el saldo para " + nombre);
                MostrarUsuarios();

                onConsultarSaldoActivo(ID_saldo, view, nombre, ID_usuario, null);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.crearToastPersonalizado(context, "No se pudo actualizar, revisa la conexión");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "55");
                params.put("nuevoSaldo", nuevoSaldo);
                params.put("ID_saldo", ID_saldo);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(postrequest);

    }


    private void CerrarModalCargando() {
        if (dialogCargando.isShowing() || dialogCargando != null) {
            dialogCargando.dismiss();
        }
    }


    String saldo_inicial;
    String nuevo_saldo;

    AlertDialog dialogCargando;


    AlertDialog dialogOpcionesUsuarios;

    public void onConsultarSaldoActivo(String ID_saldo, View view, String nombre, String ID_usuario, AlertDialog dialogOpcionesUsuarios) {

        this.dialogOpcionesUsuarios = dialogOpcionesUsuarios;

        dialogCargando = Utils.ModalCargando(context, builderCargando);
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("No se encontraron resultados")) {

                    //EN CASO DE QUE NO TENGA SALDO:

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.modal_asignar_saldo, null);
                    builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                    AlertDialog dialogAsignarSaldo = builder.create();
                    ColorDrawable back = new ColorDrawable(Color.BLACK);
                    back.setAlpha(150);
                    dialogAsignarSaldo.getWindow().setBackgroundDrawable(back);
                    dialogAsignarSaldo.getWindow().setDimAmount(0.8f);
                    dialogAsignarSaldo.show();


                    Button btnAsignarSaldo = customView.findViewById(R.id.btnAsignarSaldo);
                    TextView titulo = customView.findViewById(R.id.titulo);
                    EditText monto = customView.findViewById(R.id.monto);

                    titulo.setText("Asigna un saldo a " + nombre);

                    btnAsignarSaldo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.confirmacion_con_clave, null);
                            builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                            AlertDialog dialogConfirmacion = builder.create();
                            ColorDrawable back = new ColorDrawable(Color.BLACK);
                            back.setAlpha(150);
                            dialogConfirmacion.getWindow().setBackgroundDrawable(back);
                            dialogConfirmacion.getWindow().setDimAmount(0.8f);
                            dialogConfirmacion.show();


                            EditText editTextClaveUsuario = customView.findViewById(R.id.editTextClaveUsuario);
                            Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                            Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);


                            buttonCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogConfirmacion.dismiss();
                                }
                            });


                            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String montoTotal = monto.getText().toString();
                                    String claveIngresada = editTextClaveUsuario.getText().toString();

                                    if (montoTotal.isEmpty() || montoTotal.equals("0")) {

                                        Utils.crearToastPersonalizado(context, "Debes ingresar un monto");
                                    } else {


                                        if (!claveIngresada.equals(clave)) {
                                            Utils.crearToastPersonalizado(context, "Debes ingresar la clave correcta");
                                        } else {

                                            dialogConfirmacion.dismiss();
                                            dialogAsignarSaldo.dismiss();

                                            //AsignarSaldo(ID_usuario,  montoTotal, nombre);

                                            AsignarSaldo(ID_usuario, montoTotal, nombre);
                                        }

                                        //    Utils.crearToastPersonalizado(context, montoTotal + " " +ID_usuario + " " + nombre );
                                    }


                                    //   Utils.crearToastPersonalizado(context, "Clave: " + claveIngresada + " Monto: " + montoTotal);


                                }
                            });

                        }
                    });

                } else {
                    //EN CASO DE QUE TENGA UN SALDO ACTIVO:

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.modal_saldo, null);
                    builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                    AlertDialog dialogConsultarSaldo = builder.create();
                    ColorDrawable back = new ColorDrawable(Color.BLACK);
                    back.setAlpha(150);
                    dialogConsultarSaldo.getWindow().setBackgroundDrawable(back);
                    dialogConsultarSaldo.getWindow().setDimAmount(0.8f);
                    dialogConsultarSaldo.show();

                    TextView textViewSaldoRestante = customView.findViewById(R.id.textViewSaldoRestante);
                    TextView SaldoAsignado = customView.findViewById(R.id.SaldoAsignado);
                    ImageView buttonEditarSaldo = customView.findViewById(R.id.buttonEditarSaldo);
                    TextView fecha_asign = customView.findViewById(R.id.fecha_asign);
                    Button buttonFinalizarSaldo = customView.findViewById(R.id.buttonFinalizarSaldo);
                    ImageView botonAgregarActividad = customView.findViewById(R.id.botonAgregarActividad);


                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        saldo_inicial = jsonObject.getString("saldo_inicial");
                        nuevo_saldo = jsonObject.getString("nuevo_saldo");
                        String fecha_asignacion = jsonObject.getString("fecha_asignacion");

                        SaldoAsignado.setText("SALDO ASIGNADO: " + saldo_inicial + " $");
                        textViewSaldoRestante.setText("Saldo activo: " + nuevo_saldo + " $");
                        //          fecha_asign.setText("Saldo asignado el: " +fecha_asignacion);


                        try {
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            Date date = inputFormat.parse(fecha_asignacion);
                            SimpleDateFormat outputDayOfWeek = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
                            String dayOfWeek = outputDayOfWeek.format(date);
                            SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
                            String formattedDate = outputFormat.format(date);

                            fecha_asign.setText("Saldo asignado el: " + dayOfWeek.toLowerCase() + " " + formattedDate.toLowerCase());

                        } catch (ParseException e) {
                            fecha_asign.setText("No se encontro la fecha");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    botonAgregarActividad.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.confirmacion_con_clave, null);
                            builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                            AlertDialog dialogConfirmacion = builder.create();
                            ColorDrawable back = new ColorDrawable(Color.BLACK);
                            back.setAlpha(150);
                            dialogConfirmacion.getWindow().setBackgroundDrawable(back);
                            dialogConfirmacion.getWindow().setDimAmount(0.8f);
                            dialogConfirmacion.show();


                            EditText editTextClaveUsuario = customView.findViewById(R.id.editTextClaveUsuario);
                            EditText nuevoMonto = customView.findViewById(R.id.nuevoMonto);
                            TextInputLayout textInputLayout = customView.findViewById(R.id.textInputLayout);
                            TextView textView4 = customView.findViewById(R.id.textView4);
                            editTextClaveUsuario.setVisibility(View.VISIBLE);
                            textInputLayout.setVisibility(View.VISIBLE);
                            textView4.setText("Para agregarle saldo a " + nombre + " debes ingresar tu clave");

                            Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                            Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);

                            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View vistaModal) {

                                    String deposito = nuevoMonto.getText().toString();
                                    String claveIngresada = editTextClaveUsuario.getText().toString();

                                    if (deposito.isEmpty() || deposito.equals("0")) {
                                        Utils.crearToastPersonalizado(context, "Debes ingresar un monto");
                                    } else {

                                        if (!clave.equals(claveIngresada)) {
                                            Utils.crearToastPersonalizado(context, "La contraseña ingresada es incorrecta");
                                        } else {

                                            dialogConfirmacion.dismiss();

                                            dialogConsultarSaldo.dismiss();

                                            //          onCorregirSaldo(ID_saldo, correccionSaldo, view, nombre, ID_usuario);

                                            DepositarMasSaldo(ID_saldo, deposito, nombre);


                                        }

                                    }

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


                    buttonFinalizarSaldo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View vistaModal) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.confirmacion_con_clave, null);
                            builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                            AlertDialog dialogConfirmacion = builder.create();
                            ColorDrawable back = new ColorDrawable(Color.BLACK);
                            back.setAlpha(150);
                            dialogConfirmacion.getWindow().setBackgroundDrawable(back);
                            dialogConfirmacion.getWindow().setDimAmount(0.8f);
                            dialogConfirmacion.show();


                            EditText editTextClaveUsuario = customView.findViewById(R.id.editTextClaveUsuario);
                            Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                            Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);


                            buttonCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogConfirmacion.dismiss();
                                }
                            });


                            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String claveIngresada = editTextClaveUsuario.getText().toString();

                                    if (!clave.equals(claveIngresada)) {
                                        Utils.crearToastPersonalizado(context, "Debes ingresar la clave correcta");
                                    } else {
                                        dialogConfirmacion.dismiss();
                                        dialogConsultarSaldo.dismiss();
                                        dialogOpcionesUsuarios.dismiss();
                                        FinalizarSaldo(ID_saldo, view, nombre, ID_usuario);

                                    }

                                }
                            });
                        }
                    });

                    buttonEditarSaldo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View vistaEditar) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(vistaEditar.getContext());
                            View customView = LayoutInflater.from(vistaEditar.getContext()).inflate(R.layout.confirmacion_con_clave, null);
                            builder.setView(Utils.ModalRedondeado(vistaEditar.getContext(), customView));
                            AlertDialog dialogConfirmacion = builder.create();
                            ColorDrawable back = new ColorDrawable(Color.BLACK);
                            back.setAlpha(150);
                            dialogConfirmacion.getWindow().setBackgroundDrawable(back);
                            dialogConfirmacion.getWindow().setDimAmount(0.8f);
                            dialogConfirmacion.show();

                            TextInputLayout textInputLayout = customView.findViewById(R.id.textInputLayout);
                            textInputLayout.setVisibility(View.VISIBLE);
                            TextView nuevomonto = customView.findViewById(R.id.nuevomonto);
                            nuevomonto.setVisibility(View.VISIBLE);
                            EditText nuevoMonto = customView.findViewById(R.id.nuevoMonto);
                            nuevoMonto.setText(saldo_inicial);

                            EditText editTextClaveUsuario = customView.findViewById(R.id.editTextClaveUsuario);


                            Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                            Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);


                            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View vistaModal) {

                                    String correccionSaldo = nuevoMonto.getText().toString();
                                    String claveIngresada = editTextClaveUsuario.getText().toString();

                                    if (correccionSaldo.isEmpty() || correccionSaldo.equals("0")) {
                                        Utils.crearToastPersonalizado(context, "Debes ingresar un monto");
                                    } else {

                                        if (!clave.equals(claveIngresada)) {
                                            Utils.crearToastPersonalizado(context, "La contraseña ingresada es incorrecta");
                                        } else {

                                            dialogConfirmacion.dismiss();

                                            dialogConsultarSaldo.dismiss();

                                            onCorregirSaldo(ID_saldo, correccionSaldo, view, nombre, ID_usuario);


                                        }

                                    }

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


                }
                CerrarModalCargando();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.crearToastPersonalizado(context, "No se pudo obtener la informacion, revisa la conexión");
                CerrarModalCargando();

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "54");
                params.put("ID_saldo", ID_saldo);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void FinalizarSaldo(String ID_saldo, View view, String nombre, String ID_usuario) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Utils.crearToastPersonalizado(context, "Se finalizó el saldo para " + nombre);

                //   onConsultarSaldoActivo(ID_saldo, view, nombre, ID_usuario, null);

                MostrarUsuarios();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.crearToastPersonalizado(context, "No se pudo actualizar, revisa la conexión");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "56");
                params.put("ID_saldo", ID_saldo);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(postrequest);

    }


    private void AsignarSaldo(String ID_usuario, String saldo_asignado, String nombre) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Utils.crearToastPersonalizado(context, "Se le asigno el saldo a " + nombre);

                dialogOpcionesUsuarios.dismiss();
                MostrarUsuarios();


                //       onConsultarSaldoActivo(ID_saldo, view, nombre, ID_usuario);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.crearToastPersonalizado(context, "No se pudo actualizar, revisa la conexión");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "51");
                params.put("ID_usuario", ID_usuario);
                params.put("saldo_asignado", saldo_asignado);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(postrequest);

    }


    private void DepositarMasSaldo(String ID_saldo, String deposito, String nombre) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Utils.crearToastPersonalizado(context, "Aumento el saldo de " + nombre);

                dialogOpcionesUsuarios.dismiss();
                MostrarUsuarios();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.crearToastPersonalizado(context, "No se pudó actualizar, revisa la conexión");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "60");
                params.put("ID_saldo", ID_saldo);
                params.put("deposito", deposito);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(postrequest);

    }


}