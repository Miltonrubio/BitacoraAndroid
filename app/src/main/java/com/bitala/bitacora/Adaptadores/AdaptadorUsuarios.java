package com.bitala.bitacora.Adaptadores;


import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bitala.bitacora.ActividadesPorUsuarioFragment;
import com.bitala.bitacora.GastosFragment;
import com.bitala.bitacora.SubirFotoUsuarioActivity;
import com.bitala.bitacora.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.bitala.bitacora.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itextpdf.text.pdf.parser.Line;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.Util;


public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.ViewHolder> {

    private Context context;

    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    String url;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuarios, parent, false);
        return new ViewHolder(view);
    }


    AdaptadorListaAsignarActividades adaptadorListaActividades;
    ConstraintLayout LayoutConInternet;
    ConstraintLayout LayoutSinInternet;

    String clave;

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            JSONObject jsonObject2 = filteredData.get(position);
            String ID_usuario = jsonObject2.optString("ID_usuario", "");
            String correo = jsonObject2.optString("correo", "");
            String nombre = jsonObject2.optString("nombre", "");
            String permisos = jsonObject2.optString("permisos", "");
            String telefono = jsonObject2.optString("telefono", "");
            String clave = jsonObject2.optString("clave", "");
            String foto_usuario = jsonObject2.optString("foto_usuario", "");
            String token = jsonObject2.optString("token", "");
            String ID_saldo = jsonObject2.optString("ID_saldo", "");
            String saldo_restante = jsonObject2.optString("saldo_restante", "");


            Bundle bundle = new Bundle();
            bundle.putString("ID_usuario", ID_usuario);
            bundle.putString("permisos", permisos);
            bundle.putString("nombre", nombre);
            bundle.putString("correo", correo);
            bundle.putString("telefono", telefono);
            bundle.putString("foto_usuario", foto_usuario);


            //  setTextViewText(holder.textCorreoUsuario, correo, "Correo no disponible");
            setTextViewText(holder.textRol, permisos, "Permisos no disponible");
            setTextViewText(holder.textTelefonoUsuario, telefono, "Telefono no disponible");
            setTextViewText(holder.textNombreUsuario, nombre.toUpperCase(), "Nombre no disponible");

            // setTextViewText(holder.saldo_restante, "Saldo actual: " + saldo_restante + "$", "No tiene saldo asignado");

            if (saldo_restante.isEmpty() || saldo_restante.equals(null) || saldo_restante.equals("null") || saldo_restante.equals("")) {

                holder.saldo_restante.setText("No tiene saldo activo");
            } else {
                holder.saldo_restante.setText("Saldo actual: " + saldo_restante + "$");
            }


            String image = "http://hidalgo.no-ip.info:5610/bitacora/fotos/fotos_usuarios/fotoperfilusuario" + ID_usuario + ".jpg";

            String uniqueKey = new ObjectKey(image).toString();


            Glide.with(holder.itemView.getContext())
                    .load(image)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .signature(new ObjectKey(uniqueKey))
                    .placeholder(R.drawable.imagendefault)
                    .error(R.drawable.imagendefault)
                    .into(holder.fotoDeUsuario);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_usuarios, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                    AlertDialog dialogOpcionesUsuarios = builder.create();
                    ColorDrawable back = new ColorDrawable(Color.BLACK);
                    back.setAlpha(150);
                    dialogOpcionesUsuarios.getWindow().setBackgroundDrawable(back);
                    dialogOpcionesUsuarios.getWindow().setDimAmount(0.8f);
                    dialogOpcionesUsuarios.show();


                    LinearLayout LayoutConsultarSaldo = customView.findViewById(R.id.LayoutConsultarSaldo);

                    LinearLayout LayoutEditar = customView.findViewById(R.id.LayoutEditar);
                    LinearLayout LayoutEliminar = customView.findViewById(R.id.LayoutEliminar);
                    LinearLayout LayoutActualizarFoto = customView.findViewById(R.id.LayoutActualizarFoto);
                    LinearLayout LayoutVerActividades = customView.findViewById(R.id.LayoutVerActividades);
                    LinearLayout LayoutAsignarActividad = customView.findViewById(R.id.LayoutAsignarActividad);
                    LinearLayout LayoutConsultarMovimiento = customView.findViewById(R.id.LayoutConsultarMovimiento);


                    LayoutConsultarMovimiento.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            GastosFragment gastosFragment = new GastosFragment();
                            gastosFragment.setArguments(bundle);

                            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.frame_layouts_fragments, gastosFragment)
                                    .addToBackStack(null)
                                    .commit();

                            dialogOpcionesUsuarios.dismiss();
                        }
                    });


                    LayoutConsultarSaldo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //     dialogCargando = Utils.ModalCargando(context, builderCargando);
                            actionListener.onConsultarSaldoActivo(ID_saldo, view, nombre, ID_usuario, dialogOpcionesUsuarios);


                            /*

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.modal_saldo, null);
                            builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                            AlertDialog dialogConsultarSaldo = builder.create();
                            ColorDrawable back = new ColorDrawable(Color.BLACK);
                            back.setAlpha(150);
                            dialogConsultarSaldo.getWindow().setBackgroundDrawable(back);
                            dialogConsultarSaldo.getWindow().setDimAmount(0.8f);
                            dialogConsultarSaldo.show();


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
                                            dialogConfirmacion.dismiss();
                                            dialogConsultarSaldo.dismiss();

                                            String montoTotal = monto.getText().toString();
                                            String claveIngresada = editTextClaveUsuario.getText().toString();


                                            Utils.crearToastPersonalizado(context, "Clave: " + claveIngresada + " Monto: " + montoTotal);


                                        }
                                    });

                                }
                            });

    */
                        }


                    });


                    LayoutActualizarFoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(v.getContext(), SubirFotoUsuarioActivity.class);
                            intent.putExtras(bundle);
                            v.getContext().startActivity(intent);
                        }
                    });

                    LayoutEditar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            View customView = LayoutInflater.from(context).inflate(R.layout.insertar_nuevo_usuario, null);

                            builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                            AlertDialog dialogEditar = builder.create();
                            ColorDrawable back = new ColorDrawable(Color.BLACK);
                            back.setAlpha(150);
                            dialogEditar.getWindow().setBackgroundDrawable(back);
                            dialogEditar.getWindow().setDimAmount(0.8f);
                            dialogEditar.show();


                            TextView titulo = customView.findViewById(R.id.titulo);
                            titulo.setText("Estas editanto a " + nombre.toUpperCase());
                            EditText textViewNombreUsuario = customView.findViewById(R.id.textViewNombreUsuario);
                            EditText textViewCorreoUsuario = customView.findViewById(R.id.textViewCorreoUsuario);
                            EditText TextViewClaveUsuario = customView.findViewById(R.id.TextViewClaveUsuario);
                            EditText TextViewTelefonoUsuario = customView.findViewById(R.id.TextViewTelefonoUsuario);
                            Spinner spinnerRolUsuario = customView.findViewById(R.id.spinnerRolUsuario);
                            Button botonAgregarCliente = customView.findViewById(R.id.botonAgregarCliente);
                            Button botonCancelar = customView.findViewById(R.id.botonCancelar);
                            //   ImageView btnMostrarClave = customView.findViewById(R.id.VerClave);
                            textViewNombreUsuario.setText(nombre);
                            textViewCorreoUsuario.setText(correo);
                            TextViewTelefonoUsuario.setText(telefono);
                            TextViewClaveUsuario.setText(clave);

                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                                    R.array.opciones_rol, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerRolUsuario.setAdapter(adapter);

                            botonCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogEditar.dismiss();
                                }
                            });

                            botonAgregarCliente.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String nuevoNombreUsuario = textViewNombreUsuario.getText().toString();
                                    String nuevoCorreoUsuario = textViewCorreoUsuario.getText().toString();
                                    String nuevaClaveUsuario = TextViewClaveUsuario.getText().toString();
                                    String nuevOTelefonoUsuario = TextViewTelefonoUsuario.getText().toString().replaceAll(" ", "");
                                    String nuevoRolUsuario = spinnerRolUsuario.getSelectedItem().toString();

                                    if (nuevoNombreUsuario.isEmpty() || nuevoCorreoUsuario.isEmpty() || nuevaClaveUsuario.isEmpty() || nuevOTelefonoUsuario.isEmpty() || nuevoRolUsuario.isEmpty()) {
                                        Utils.crearToastPersonalizado(context, "Tienes campos vacios, por favor rellenalos");
                                    } else {
                                        actionListener.onEditarUsuarioActivity(ID_usuario, nuevoNombreUsuario, nuevoCorreoUsuario, nuevaClaveUsuario, nuevOTelefonoUsuario, nuevoRolUsuario);
                                        dialogOpcionesUsuarios.dismiss();
                                        dialogEditar.dismiss();
                                    }

                                }
                            });

                        }
                    });


                    LayoutEliminar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View customView = LayoutInflater.from(context).inflate(R.layout.opciones_confirmacion, null);


                            builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                            AlertDialog dialogConfirmacion = builder.create();
                            ColorDrawable back = new ColorDrawable(Color.BLACK);
                            back.setAlpha(150);
                            dialogConfirmacion.getWindow().setBackgroundDrawable(back);
                            dialogConfirmacion.getWindow().setDimAmount(0.8f);
                            dialogConfirmacion.show();


                            TextView textViewTituloConfirmacion = customView.findViewById(R.id.textViewTituloConfirmacion);
                            Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                            Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                            textViewTituloConfirmacion.setText("¿Estas seguro que deseas eliminar a " + nombre + " ?");


                            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    actionListener.onEliminarUsuarioActivity(ID_usuario, nombre);
                                    dialogOpcionesUsuarios.dismiss();
                                    dialogConfirmacion.dismiss();
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


                    LayoutVerActividades.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActividadesPorUsuarioFragment actividadesPorUsuarioFragment = new ActividadesPorUsuarioFragment();
                            actividadesPorUsuarioFragment.setArguments(bundle);

                            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.frame_layouts_fragments, actividadesPorUsuarioFragment)
                                    .addToBackStack(null)
                                    .commit();

                            dialogOpcionesUsuarios.dismiss();
                        }
                    });

                    LayoutAsignarActividad.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.insertar_nuevo_nombre_actividad, null);


                            builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                            AlertDialog dialogAsignarActividad = builder.create();
                            ColorDrawable back = new ColorDrawable(Color.BLACK);
                            back.setAlpha(150);
                            dialogAsignarActividad.getWindow().setBackgroundDrawable(back);
                            dialogAsignarActividad.getWindow().setDimAmount(0.8f);
                            dialogAsignarActividad.show();


                            /*
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.insertar_nuevo_nombre_actividad, null);
                            builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                            AlertDialog dialogAsignarActividad = builder.create();
                            dialogAsignarActividad.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogAsignarActividad.show();

                            */


                            RadioButton radioButtonGeneral = customView.findViewById(R.id.radioButtonGeneral);
                            RadioButton radioButtonOficinistas = customView.findViewById(R.id.radioButtonOficinistas);
                            EditText TextViewNombreActividad = customView.findViewById(R.id.TextViewNombreActividad);
                            TextViewNombreActividad.setHint("Describe la actividad que va a realizar " + nombre);
                            radioButtonGeneral.setVisibility(View.GONE);
                            radioButtonOficinistas.setVisibility(View.GONE);

                            Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                            Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);


                            buttonCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogAsignarActividad.dismiss();
                                }
                            });


                            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String descripcionActividad = TextViewNombreActividad.getText().toString();


                                    if (descripcionActividad.isEmpty()) {
                                        Utils.crearToastPersonalizado(context, "Debes agregar la descripcion de la actividad");
                                    } else {
                                        dialogAsignarActividad.dismiss();

                                        //  Utils.crearToastPersonalizado(context, "Asi se mandaria: " + descripcionActividad);

                                        actionListener.onAsignarActividadAUsuario("45", descripcionActividad, ID_usuario, nombre, token);

                                        //String idNombreActividad, String descripcion, String ID_usuario, String nombre, String token);

                                    }


                                }
                            });


/*
                            RecyclerView RecyclerViewTituloActividades = customView.findViewById(R.id.RecyclerViewTituloActividades);

                            LayoutSinInternet=customView.findViewById(R.id.LayoutSinInternet);
                            LayoutConInternet=customView.findViewById(R.id.LayoutConInternet);


                            adaptadorListaActividades = new AdaptadorListaAsignarActividades(nombresActividades, context, actionListenerLista, ID_usuario, dialogAsignarActividad, dialogOpcionesUsuarios, nombre, token);
                            RecyclerViewTituloActividades.setLayoutManager(new LinearLayoutManager(view.getContext()));
                            RecyclerViewTituloActividades.setAdapter(adaptadorListaActividades);
                            VerNombresActividades();
*/
                        }
                    });

                }
            });

        } finally {

        }
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

    }





/*


   private void CerrarModalCargando() {
        if (dialogCargando.isShowing() || dialogCargando != null) {
            dialogCargando.dismiss();
        }
    }


    private void ConsultarSaldoActivo(String ID_saldo, View view, String nombre) {


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
                                    dialogConfirmacion.dismiss();
                                    dialogAsignarSaldo.dismiss();

                                    String montoTotal = monto.getText().toString();
                                    String claveIngresada = editTextClaveUsuario.getText().toString();


                                    Utils.crearToastPersonalizado(context, "Clave: " + claveIngresada + " Monto: " + montoTotal);


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
                    TextView textView3 = customView.findViewById(R.id.textView3);
                    Button buttonFinalizarSaldo = customView.findViewById(R.id.buttonFinalizarSaldo);


                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        saldo_inicial = jsonObject.getString("saldo_inicial");
                        nuevo_saldo = jsonObject.getString("nuevo_saldo");

                        SaldoAsignado.setText("SALDO ASIGNADO: " + saldo_inicial + " $");
                        textViewSaldoRestante.setText("Saldo restante: " + nuevo_saldo + "$");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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

                                        if (!clave.equals(claveIngresada)){
                                            Utils.crearToastPersonalizado(context,"La contraseña ingresada es incorrecta");
                                        }else {

                                            dialogConfirmacion.dismiss();

                                            dialogConsultarSaldo.dismiss();

                                            actionListener.onCorregirSaldo( ID_saldo,  correccionSaldo,  view,  nombre);


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

*/


/*

        private void CorregirSaldo(String ID_saldo, String nuevoSaldo, View view, String nombre) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Utils.crearToastPersonalizado(context, "Se actualizó el saldo para " + nombre);

                ConsultarSaldoActivo(ID_saldo, view, nombre);

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

    */


    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNombreUsuario, textCorreoUsuario, textRol, textTelefonoUsuario;

        TextView saldo_restante;
        ImageView fotoDeUsuario;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textNombreUsuario = itemView.findViewById(R.id.textNombreUsuario);
            //  textCorreoUsuario = itemView.findViewById(R.id.textCorreoUsuario);
            textRol = itemView.findViewById(R.id.textRol);
            fotoDeUsuario = itemView.findViewById(R.id.fotoDeUsuario);
            saldo_restante = itemView.findViewById(R.id.saldo_restante);
            textTelefonoUsuario = itemView.findViewById(R.id.textTelefonoUsuario);


        }
    }


    public void filter(String query) {
        filteredData.clear();

        if (TextUtils.isEmpty(query)) {
            filteredData.addAll(data);
        } else {
            String[] keywords = query.toLowerCase().split(" ");

            for (JSONObject item : data) {

                String ID_usuario = item.optString("ID_usuario", "").toLowerCase();
                String correo = item.optString("correo", "").toLowerCase();
                String nombre = item.optString("nombre", "").toLowerCase();
                String permisos = item.optString("permisos", "").toLowerCase();
                String telefono = item.optString("telefono", "").toLowerCase();

                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(ID_usuario.contains(keyword) || correo.contains(keyword) || nombre.contains(keyword) || permisos.contains(keyword) ||
                            telefono.contains(keyword))) {
                        matchesAllKeywords = false;
                        break;
                    }
                }

                if (matchesAllKeywords) {
                    filteredData.add(item);
                }
            }
        }

        if (filteredData.isEmpty()) {
            actionListener.onFilterData(false); // Indica que no hay resultados
        } else {
            actionListener.onFilterData(true); // Indica que hay resultados
        }


        notifyDataSetChanged();
    }


    public void setFilteredData(List<JSONObject> filteredData) {
        this.filteredData = new ArrayList<>(filteredData);
        notifyDataSetChanged();
    }


    private void setTextViewText(TextView textView, String text, String defaultText) {
        if (text.equals(null) || text.equals("") || text.equals(":null") || text.equals("null") || text.isEmpty()) {
            textView.setText(defaultText);
        } else {
            textView.setText(text);
        }
    }

    public interface OnActivityActionListener {
        void onEditarUsuarioActivity(String ID_usuario, String nombreUsuario, String correoUsuario, String claveUsuario, String telefonoUsuario, String rolUsuario);

        void onEliminarUsuarioActivity(String ID_usuario, String nombre);

        void onFilterData(Boolean estado);

        void onAsignarActividadAUsuario(String idNombreActividad, String descripcion, String ID_usuario, String nombre, String token);

        //    void onCorregirSaldo(String ID_saldo, String nuevoSaldo, View view, String nombre);

        void onConsultarSaldoActivo(String ID_saldo, View view, String nombre, String ID_usuario, AlertDialog dialogOpcionesUsuarios);


    }

    private AdaptadorUsuarios.OnActivityActionListener actionListener;


    public AdaptadorUsuarios(List<JSONObject> data, Context context, AdaptadorUsuarios.OnActivityActionListener actionListener) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        this.actionListener = actionListener;

        url = context.getResources().getString(R.string.urlApi);
        //     builderCargando = new AlertDialog.Builder(context);
        //   builderCargando.setCancelable(false);


        SharedPreferences sharedPreferences = context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        clave = sharedPreferences.getString("clave", "");

    }




    /*


    List<JSONObject> nombresActividades = new ArrayList<>();

    private void VerNombresActividades() {
        nombresActividades.clear();
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
                    adaptadorListaActividades.setFilteredData(nombresActividades);
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

     */
}

