package com.bitala.bitacora;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bitala.bitacora.Adaptadores.AdaptadorMostrarSaldosActivos;
import com.bitala.bitacora.Adaptadores.AdaptadorUsuarios;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrudUsuariosFragment extends Fragment implements AdaptadorUsuarios.OnActivityActionListener, AdaptadorMostrarSaldosActivos.OnActivityActionListener {

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


    AdaptadorMostrarSaldosActivos.OnActivityActionListener actionListenerMostrarSaldosActivos;
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

        actionListenerMostrarSaldosActivos = this;
        builderCargando = new AlertDialog.Builder(context);
        builderCargando.setCancelable(false);
        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        GridLayoutManager gridLayoutManagerDelantero = new GridLayoutManager(context, 2);
        recyclerViewUsuarios.setLayoutManager(gridLayoutManagerDelantero);
        adaptadorUsuarios = new AdaptadorUsuarios(dataList, context, this, actionListenerMostrarSaldosActivos);
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
                //   Spinner spinnerRolUsuario = customView.findViewById(R.id.spinnerRolUsuario);
                TextView RolUsuario = customView.findViewById(R.id.RolUsuario);


                RolUsuario.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        View customView = LayoutInflater.from(context).inflate(R.layout.modal_seleccionar_rol, null);
                        builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                        AlertDialog dialogSeleccionarRol = builder.create();
                        dialogSeleccionarRol.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogSeleccionarRol.show();
                        LinearLayout LayoutVENDEDOR = customView.findViewById(R.id.LayoutVENDEDOR);
                        LinearLayout LayoutCAJERO = customView.findViewById(R.id.LayoutCAJERO);
                        LinearLayout LayoutBECARIO = customView.findViewById(R.id.LayoutBECARIO);
                        LinearLayout LayoutChofer = customView.findViewById(R.id.LayoutChofer);
                        LinearLayout LayoutPOLICIA = customView.findViewById(R.id.LayoutPOLICIA);
                        LinearLayout LayoutOFICINISTA = customView.findViewById(R.id.LayoutOFICINISTA);
                        LinearLayout LayoutCARGADOR = customView.findViewById(R.id.LayoutCARGADOR);
                        LinearLayout LayoutVIGILANTE = customView.findViewById(R.id.LayoutVIGILANTE);
                        LinearLayout LayoutREPARTIDOR = customView.findViewById(R.id.LayoutREPARTIDOR);
                        LinearLayout LayoutVELADOR = customView.findViewById(R.id.LayoutVELADOR);
                        LinearLayout LayoutJEFEUNIDADES = customView.findViewById(R.id.LayoutJEFEUNIDADES);
                        LinearLayout LayoutBodeguero = customView.findViewById(R.id.LayoutBodeguero);
                        LinearLayout LayoutJefeSeguridad = customView.findViewById(R.id.LayoutJefeSeguridad);
                        LinearLayout LayoutSUPERADMIN = customView.findViewById(R.id.LayoutSUPERADMIN);

                        LayoutVENDEDOR.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("VENDEDOR");
                                dialogSeleccionarRol.dismiss();
                            }
                        });



                        LayoutCAJERO.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("CAJERO");
                                dialogSeleccionarRol.dismiss();
                            }
                        });



                        LayoutBECARIO.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("BECARIO");
                                dialogSeleccionarRol.dismiss();
                            }
                        });


                        LayoutChofer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("CHOFER");
                                dialogSeleccionarRol.dismiss();
                            }
                        });



                        LayoutPOLICIA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("POLICIA");
                                dialogSeleccionarRol.dismiss();
                            }
                        });


                        LayoutOFICINISTA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("OFICINISTA");
                                dialogSeleccionarRol.dismiss();
                            }
                        });


                        LayoutCARGADOR.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("CARGADOR");
                                dialogSeleccionarRol.dismiss();
                            }
                        });

                        LayoutVIGILANTE.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("VIGILANTE");
                                dialogSeleccionarRol.dismiss();
                            }
                        });

                        LayoutREPARTIDOR.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("REPARTIDOR");
                                dialogSeleccionarRol.dismiss();
                            }
                        });

                        LayoutVELADOR.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("VELADOR");
                                dialogSeleccionarRol.dismiss();
                            }
                        });

                        LayoutJEFEUNIDADES.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("JEFE DE UNIDADES");
                                dialogSeleccionarRol.dismiss();
                            }
                        });

                        LayoutBodeguero.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("BODEGUERO");
                                dialogSeleccionarRol.dismiss();
                            }
                        });

                        LayoutJefeSeguridad.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("JEFE DE SEGURIDAD");
                                dialogSeleccionarRol.dismiss();
                            }
                        });

                        LayoutSUPERADMIN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                RolUsuario.setText("SUPERADMIN");
                                dialogSeleccionarRol.dismiss();
                            }
                        });


                    }
                });


                Button botonCancelar = customView.findViewById(R.id.botonCancelar);
                Button botonAgregarCliente = customView.findViewById(R.id.botonAgregarCliente);

/*
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                        R.array.opciones_rol, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Establece el ArrayAdapter en el Spinner
                spinnerRolUsuario.setAdapter(adapter);
*/

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
                        // String RolUsuario = spinnerRolUsuario.getSelectedItem().toString();
                        String RolUsuarioSeleccionado = RolUsuario.getText().toString();

                        if (nombreUsuario.isEmpty() || correoUsuario.isEmpty() || ClaveUsuario.isEmpty() || TelefonoUsuario.isEmpty()) {

                            Utils.crearToastPersonalizado(context, "Tienes campos vacios, por favor rellenalos");


                        } else {


                            if(RolUsuarioSeleccionado.equalsIgnoreCase("Selecciona el tipo")){

                                Utils.crearToastPersonalizado(context, "Debes seleccionar el Rol");
                            }else {

                                AgregarNuevoUsuario(nombreUsuario, correoUsuario, ClaveUsuario, TelefonoUsuario, RolUsuarioSeleccionado, dialogActividades);
                            }


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
//                params.put("opcion", "13");
                params.put("opcion", "82");
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
    public void onAsignarActividadAUsuario(String idNombreActividad, String descripcion, String ID_usuario, String nombre, String token, String ID_admin_asig) {
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
                params.put("opcion", "71");
                params.put("ID_nombre_actividad", idNombreActividad);
                params.put("descripcionActividad", descripcion);
                params.put("ID_usuario", ID_usuario);
                params.put("ID_admin_asig", ID_admin_asig);

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


    private void CerrarModalCargando() {
        if (dialogCargando.isShowing() || dialogCargando != null) {
            dialogCargando.dismiss();
        }
    }


    Double saldo_inicial;
    Double nuevo_saldo;

    String caja;
    Double gastos_Cajagastos;
    Double gastos_CajaCapital;
    Double depositos_Cajagastos;
    Double depositos_CajaCapital;

    AlertDialog dialogCargando;


    String correccionCaja = "Gastos";


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


    public void AsignarSaldo(String ID_usuario, String saldo_asignado, String nombre, String tipo_caja) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Utils.crearToastPersonalizado(context, "Se le asigno el saldo a " + nombre);

                //    dialogOpcionesUsuarios.dismiss();
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
                /*
                params.put("opcion", "62");
                params.put("ID_usuario", ID_usuario);
                params.put("saldo_asignado", saldo_asignado);
                params.put("tipo_caja", tipo_caja);

                 */

                params.put("opcion", "73");
                params.put("ID_usuario", ID_usuario);
                params.put("ID_admin_asig", Sesion_UsuarioID);
                params.put("nuevoSaldoAsignado", saldo_asignado);
                params.put("nuevoTipoCaja", tipo_caja);

                return params;
            }
        };
        Volley.newRequestQueue(context).add(postrequest);

    }

    @Override
    public void onAgregarSaldoDeOtraCaja(String ID_admin_asig, String ID_registro_saldo, String nuevoTipoCaja, String nuevoSaldoAsignado, AlertDialog dialogAgregarSaldoDeOtraCaja, AlertDialog dialogSaldoAsignado, AlertDialog modalCargando, AlertDialog dialogOpcionesUsuarios) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                modalCargando.dismiss();
                if (response.equalsIgnoreCase("Se asignó saldo a esta caja")) {

                    MostrarUsuarios();
                    dialogAgregarSaldoDeOtraCaja.dismiss();
                    dialogSaldoAsignado.dismiss();
                    dialogOpcionesUsuarios.dismiss();
                } else {

                    Utils.crearToastPersonalizado(context, response);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "Algo fallo, revisa la conexion");

                modalCargando.dismiss();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "76");
                params.put("ID_admin_asig", ID_admin_asig);
                params.put("ID_registro_saldo", ID_registro_saldo);
                params.put("nuevoSaldoAsignado", nuevoSaldoAsignado);
                params.put("nuevoTipoCaja", nuevoTipoCaja);

                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }

    @Override
    public void onFinalizarAmbosSaldos(String ID_registro_saldo, String nombre) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                MostrarUsuarios();
                Utils.crearToastPersonalizado(context, "Se finalizo el saldo de " + nombre);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "Algo fallo, revisa la conexion");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "74");
                params.put("ID_registro_saldo", ID_registro_saldo);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    @Override
    public void onFinalizarUnSaldo(String ID_registro_saldo, String nuevoTipoCaja) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MostrarUsuarios();
                Utils.crearToastPersonalizado(context, "Se finalizó el saldo de " + nuevoTipoCaja);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "Error al finalizar un saldo");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "75");
                params.put("ID_registro_saldo", ID_registro_saldo);
                params.put("nuevoTipoCaja", nuevoTipoCaja);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    @Override
    public void onAgregarMasSaldo(String saldoAgregado, String ID_saldo, String ID_admin_asig, String tipo_caja) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MostrarUsuarios();
                Utils.crearToastPersonalizado(context, "Se agregò mas saldo a la caja " + tipo_caja);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "Error al finalizar un saldo");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "78");
                params.put("saldoAgregado", saldoAgregado);
                params.put("ID_saldo", ID_saldo);
                params.put("ID_admin_asig", ID_admin_asig);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    @Override
    public void onCorregirSaldo(String ID_saldo, String montoCorregido, String tipo_caja) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MostrarUsuarios();
                Utils.crearToastPersonalizado(context, "Se corrigio el saldo de la caja " + tipo_caja);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "Error al finalizar un saldo");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "84");
                params.put("ID_saldo", ID_saldo);
                params.put("montoCorregido", montoCorregido);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }

}