package com.bitala.bitacora.Adaptadores;


import static android.app.PendingIntent.getActivity;

import static com.bitala.bitacora.Utils.ModalRedondeado;
import static com.bitala.bitacora.Utils.crearToastPersonalizado;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bitala.bitacora.DetallesActividadesFragment;
import com.bitala.bitacora.R;
import com.bitala.bitacora.SubirFotoActivity;
import com.bitala.bitacora.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.Manifest;


public class AdaptadorActividades extends RecyclerView.Adapter<AdaptadorActividades.ViewHolder> {
    private ArrayList<String> nombresActividades = new ArrayList<>();
    // String url = "http://hidalgo.no-ip.info:5610/bitacora/mostrar.php";

    String url;


    private Context context;
    String IDSesionIniciada;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividades, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        SharedPreferences sharedPreferences = context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        String permisosUsuario = sharedPreferences.getString("permisos", "");
        String nombresesioniniciada = sharedPreferences.getString("nombre", "");
        IDSesionIniciada = sharedPreferences.getString("ID_usuario", "");

        holder.textMotivoCancelacion.setVisibility(View.GONE);

        try {
            JSONObject jsonObject2 = filteredData.get(position);
            String ID_actividad = jsonObject2.optString("ID_actividad", "");
            String ID_nombre_actividad = jsonObject2.optString("ID_nombre_actividad", "");
            String ID_usuario = jsonObject2.optString("ID_usuario", "");
            String fecha_inicio = jsonObject2.optString("fecha_inicio", "");
            String fecha_fin = jsonObject2.optString("fecha_fin", "");
            String estadoActividad = jsonObject2.optString("estadoActividad", "");
            String nombre_actividad = jsonObject2.optString("nombre_actividad", "");
            String descripcionActividad = jsonObject2.optString("descripcionActividad", "");
            String permisos = jsonObject2.optString("permisos", "");
            String nombre = jsonObject2.optString("nombre", "");
            String correo = jsonObject2.optString("correo", "");
            String telefono = jsonObject2.optString("telefono", "");
            String foto_usuario = jsonObject2.optString("foto_usuario", "");
            String tipo_actividad = jsonObject2.optString("tipo_actividad", "");


            Bundle bundle = new Bundle();
            bundle.putString("ID_actividad", ID_actividad);
            bundle.putString("ID_usuario", ID_usuario);
            bundle.putString("estadoActividad", estadoActividad);
            bundle.putString("fecha_fin", fecha_fin);
            bundle.putString("fecha_inicio", fecha_inicio);
            bundle.putString("nombre_actividad", nombre_actividad);
            bundle.putString("descripcionActividad", descripcionActividad);
            bundle.putString("permisos", permisos);
            bundle.putString("nombre", nombre);
            bundle.putString("correo", correo);
            bundle.putString("telefono", telefono);
            bundle.putString("foto_usuario", foto_usuario);


            if (!permisosUsuario.equals("SUPERADMIN")) {
                holder.textIdActividad.setVisibility(View.GONE);
            }


            int colorRes;
            Drawable drawable;

            if (tipo_actividad.equalsIgnoreCase("OFICINAS") || tipo_actividad.equalsIgnoreCase("OCULTA")) {
               // drawable = ContextCompat.getDrawable(context, R.drawable.redondeadoconbordevino);
                colorRes = R.color.vino;
            } else {

             //   drawable = ContextCompat.getDrawable(context, R.drawable.roundedbackground_nombre_actividad);
                colorRes = R.color.naranjita;
            }


          //  holder.FrameActividades.setBackground(drawable);
            int color = ContextCompat.getColor(context, colorRes);
            holder.textActividad.setTextColor(color);


            setTextViewText(holder.textActividad, nombre_actividad.toUpperCase(), "Actividad no disponible");

            if (!estadoActividad.equalsIgnoreCase("Pendiente")) {

                SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat formatoDeseado = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy HH:mm", Locale.getDefault());
                SimpleDateFormat formatoFin = new SimpleDateFormat("'Finalizada el ' dd 'de' MMMM 'de' yyyy  HH:mm", Locale.getDefault());
                try {
                    Date fecha = formatoOriginal.parse(fecha_fin);
                    String fechaFormateada = formatoFin.format(fecha);

                    setTextViewText(holder.textFechaFin, fechaFormateada, "Fecha no disponible");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Fecha de Inicio

                try {
                    Date fecha = formatoOriginal.parse(fecha_inicio);
                    String fechaFormateada = formatoDeseado.format(fecha);

                    setTextViewText(holder.textFechaActividad, fechaFormateada, "Fecha no disponible");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                holder.textFechaFin.setVisibility(View.VISIBLE);


                int colorIcono = ContextCompat.getColor(context, R.color.black);
                int drawableResId = R.drawable.baseline_checklist_24;
                holder.ImagenDeEstado.setImageResource(drawableResId);
                holder.ImagenDeEstado.setColorFilter(colorIcono);
                holder.textStatus.setTextColor(colorIcono);

            } else {

                int colorIcono = ContextCompat.getColor(context, R.color.amarillo);
                int drawableResId = R.drawable.baseline_access_time_24;
                holder.ImagenDeEstado.setImageResource(drawableResId);
                holder.ImagenDeEstado.setColorFilter(colorIcono);
                holder.textStatus.setTextColor(colorIcono);

                setTextViewText(holder.textFechaActividad, "Aun no se inicia la actividad", "Fecha no disponible");
                holder.textFechaFin.setVisibility(View.GONE);
            }


            setTextViewText(holder.textStatus, estadoActividad.toUpperCase(), "Estado no disponible");
            actualizarEstadoYVista(holder, estadoActividad);

            setTextViewText(holder.textDetallesActividad, descripcionActividad, "Actividad sin descripcion");

            if (ID_actividad.isEmpty() || ID_actividad.equals("null")) {

                setTextViewText(holder.textIdActividad, "ID no disponible", "ID no disponible");
            } else {
                setTextViewText(holder.textIdActividad, "ID de actividad: " + ID_actividad, "ID no disponible");
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (estadoActividad.equalsIgnoreCase("Iniciado")) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View customView = LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_item, null);
                        builder.setView(ModalRedondeado(context, customView));

                        LinearLayout LayoutMandarUbicacion = customView.findViewById(R.id.LayoutMandarUbicacion);
                        LinearLayout LayoutMandarFoto = customView.findViewById(R.id.LayoutMandarFoto);
                        LinearLayout LayoutVerDetalles = customView.findViewById(R.id.LayoutVerDetalles);
                        LinearLayout LayoutFinalizarActividad = customView.findViewById(R.id.LayoutFinalizarActividad);
                        LinearLayout LayoutCancelarActividad = customView.findViewById(R.id.LayoutCancelarActividad);

                        AlertDialog dialogOpcionesDeActividad = builder.create();
                        dialogOpcionesDeActividad.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogOpcionesDeActividad.show();


                        LayoutMandarUbicacion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                View customView = LayoutInflater.from(context).inflate(R.layout.opciones_confirmacion, null);

                                Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                                TextView textViewTituloConfirmacion = customView.findViewById(R.id.textViewTituloConfirmacion);
                                Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                                textViewTituloConfirmacion.setText("¿Estas seguro de mandar tu ubicación?");
                                builder.setView(ModalRedondeado(context, customView));
                                AlertDialog dialogConfirmacion = builder.create();
                                dialogConfirmacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialogConfirmacion.show();

                                buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        obtenerUbicacion(context, ID_usuario, ID_actividad);
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
                        });


                        LayoutMandarFoto.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), SubirFotoActivity.class);
                                intent.putExtras(bundle);
                                v.getContext().startActivity(intent);
                            }
                        });


                        LayoutVerDetalles.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DetallesActividadesFragment detallesActividadesFragment = new DetallesActividadesFragment();
                                detallesActividadesFragment.setArguments(bundle);

                                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame_layouts_fragments, detallesActividadesFragment)
                                        .addToBackStack(null)
                                        .commit();

                                dialogOpcionesDeActividad.dismiss();
                            }
                        });


                        LayoutFinalizarActividad.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                consultarSaldoActivo(ID_usuario, ID_actividad, dialogOpcionesDeActividad);

/*
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                View customView = LayoutInflater.from(context).inflate(R.layout.confirmacion_con_clave, null);
                                builder.setView(ModalRedondeado(context, customView));
                                AlertDialog dialogConfirmacion = builder.create();
                                dialogConfirmacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialogConfirmacion.show();


                                TextView nuevomonto = customView.findViewById(R.id.nuevomonto);
                                TextInputLayout textInputLayout = customView.findViewById(R.id.textInputLayout);
                                EditText nuevoMonto = customView.findViewById(R.id.nuevoMonto);
                                EditText editTextClaveUsuario = customView.findViewById(R.id.editTextClaveUsuario);

                                TextView textView4= customView.findViewById(R.id.textView4);
                                textView4.setText("Para finalizar esta actividad debes ingresar el monto");

                                editTextClaveUsuario.setVisibility(View.GONE);
                                textInputLayout.setVisibility(View.VISIBLE);


                                Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                                Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);



                                buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        String selectedEstado = "Finalizado";
                                        //  ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                                        actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);
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

*/
                            }
                        });

                        LayoutCancelarActividad.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Inflar el diseño personalizado del diálogo
                                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.opcion_cancelar, null);

                                EditText editText = dialogView.findViewById(R.id.editText);
                                Button buttonCancelar = dialogView.findViewById(R.id.buttonCancelar);
                                Button buttonAceptar = dialogView.findViewById(R.id.buttonAceptar);

                                TextView tituloCancelacion = dialogView.findViewById(R.id.tituloCancelacion);
                                tituloCancelacion.setText("Agrega el motivo para cancelar la actividad " + nombre_actividad + ":");
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setView(ModalRedondeado(view.getContext(), dialogView));
                                AlertDialog dialogCancelacion = builder.create();
                                dialogCancelacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialogCancelacion.show();


                                buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        View customView = LayoutInflater.from(context).inflate(R.layout.opciones_confirmacion, null);
                                        TextView textViewTituloConfirmacion = customView.findViewById(R.id.textViewTituloConfirmacion);
                                        Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                                        Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                                        textViewTituloConfirmacion.setText("¿Estas seguro que deseas cancelar la actividad " + nombre_actividad + " ?");
                                        builder.setView(Utils.ModalRedondeado(context, customView));
                                        AlertDialog dialogConfirmacion = builder.create();
                                        dialogConfirmacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialogConfirmacion.show();


                                        buttonCancelar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogConfirmacion.dismiss();
                                            }
                                        });

                                        buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                String userInput = editText.getText().toString();
                                                if (userInput.isEmpty() || userInput.equals("")) {
                                                    Utils.crearToastPersonalizado(context, "Por favor ingresa el motivo de la cancelacion");
                                                } else {
                                                    String selectedEstado = "Cancelado";
                                                    actionListener.onCancelarActividadesActivity(ID_actividad, selectedEstado, userInput);
                                                    dialogCancelacion.dismiss();
                                                    dialogOpcionesDeActividad.dismiss();
                                                    dialogConfirmacion.dismiss();

                                                }
                                            }
                                        });

                                    }
                                });

                                buttonCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        dialogCancelacion.dismiss();
                                    }
                                });


                            }
                        });


                    } else if (estadoActividad.equalsIgnoreCase("Pendiente")) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View customView = LayoutInflater.from(context).inflate(R.layout.opciones_actividades, null);
                        builder.setView(ModalRedondeado(context, customView));
                        LinearLayout LayoutVerDetalles = customView.findViewById(R.id.LayoutVerDetalles);
                        LayoutVerDetalles.setVisibility(View.GONE);
                        LinearLayout LayoutEditar = customView.findViewById(R.id.LayoutEditar);
                        LinearLayout LayoutEliminar = customView.findViewById(R.id.LayoutEliminar);
                        LinearLayout LayoutActualizarEstado = customView.findViewById(R.id.LayoutActualizarEstado);
                        LinearLayout LayoutPendiente = customView.findViewById(R.id.LayoutPendiente);
                        LinearLayout LayoutIniciado = customView.findViewById(R.id.LayoutIniciado);
                        LinearLayout LayoutFinalizado = customView.findViewById(R.id.LayoutFinalizado);
                        EditText editextDescripcionActividad = customView.findViewById(R.id.editextDescripcionActividad);
                        Button BotonActualizarActividad = customView.findViewById(R.id.BotonActualizarActividad);

                        //Spinner de titulo de actividades

                        Spinner SpinnerNombreActividad = customView.findViewById(R.id.SpinnerNombreActividad);
                        nombresActividades.add(0, "Selecciona una opción");
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, nombresActividades);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        SpinnerNombreActividad.setAdapter(spinnerAdapter);
                        SpinnerNombreActividad.setSelection(0);


                        AlertDialog dialogOpcionesDeActividad = builder.create();
                        dialogOpcionesDeActividad.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogOpcionesDeActividad.show();

                        LayoutVerDetalles.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DetallesActividadesFragment detallesActividadesFragment = new DetallesActividadesFragment();
                                detallesActividadesFragment.setArguments(bundle);

                                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame_layouts_fragments, detallesActividadesFragment)
                                        .addToBackStack(null)
                                        .commit();

                                dialogOpcionesDeActividad.dismiss();
                            }
                        });


                        VerNombresActividades(context);
                        LayoutEditar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                LayoutVerDetalles.setVisibility(View.GONE);
                                editextDescripcionActividad.setText(descripcionActividad);
                                LayoutEditar.setVisibility(View.GONE);
                                LayoutEliminar.setVisibility(View.GONE);
                                LayoutActualizarEstado.setVisibility(View.GONE);
                                SpinnerNombreActividad.setVisibility(View.VISIBLE);
                                editextDescripcionActividad.setVisibility(View.VISIBLE);
                                BotonActualizarActividad.setVisibility(View.VISIBLE);
                                BotonActualizarActividad.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String valorActividadSpinner = SpinnerNombreActividad.getSelectedItem().toString();
                                        if (!valorActividadSpinner.equals("Selecciona una opción")) {
                                            String descripcionActividad = editextDescripcionActividad.getText().toString();
                                            String selectedID = obtenerIDDesdeNombre(valorActividadSpinner);

                                            actionListener.onEditActivity(ID_actividad, selectedID, descripcionActividad);
                                            dialogOpcionesDeActividad.dismiss();
                                        } else {
                                            Utils.crearToastPersonalizado(context, "Debes seleccionar una actividad válida.");
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
                                Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                                Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);

                                builder.setView(ModalRedondeado(context, customView));
                                AlertDialog dialogConfirmacion = builder.create();
                                dialogConfirmacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialogConfirmacion.show();

                                buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        actionListener.onDeleteActivity(ID_actividad);
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
                        });


                        LayoutActualizarEstado.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                LayoutVerDetalles.setVisibility(View.GONE);
                                LayoutActualizarEstado.setVisibility(View.GONE);
                                LayoutEditar.setVisibility(View.GONE);
                                LayoutEliminar.setVisibility(View.GONE);

                                if (estadoActividad.equals("Pendiente")) {
                                    LayoutFinalizado.setVisibility(View.GONE);
                                    LayoutIniciado.setVisibility(View.VISIBLE);
                                } else if (estadoActividad.equals("Iniciado")) {
                                    LayoutPendiente.setVisibility(View.GONE);
                                    LayoutFinalizado.setVisibility(View.VISIBLE);
                                }


                                LayoutPendiente.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String selectedEstado = "Pendiente";
                                        actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);
                                        dialogOpcionesDeActividad.dismiss();
                                    }
                                });

                                LayoutIniciado.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String selectedEstado = "Iniciado";
                                        //  ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                                        actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);
                                        dialogOpcionesDeActividad.dismiss();

                                    }
                                });

                                LayoutFinalizado.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String selectedEstado = "Finalizado";
                                        //  ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                                        actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);
                                        dialogOpcionesDeActividad.dismiss();
                                    }
                                });
                            }
                        });


                    }
                }
            });

/*
            if (estadoActividad.equals("Iniciado")) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Selecciona el nuevo estado de la actividad");

                        View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.spinner_dropdown_item, null);
                        LinearLayout LayoutMandarUbicacion = customView.findViewById(R.id.LayoutMandarUbicacion);
                        LinearLayout LayoutMandarFoto = customView.findViewById(R.id.LayoutMandarFoto);
                        LinearLayout LayoutVerDetalles = customView.findViewById(R.id.LayoutVerDetalles);
                        LinearLayout LayoutFinalizarActividad = customView.findViewById(R.id.LayoutFinalizarActividad);
                        LinearLayout LayoutCancelarActividad = customView.findViewById(R.id.LayoutCancelarActividad);


                        builder.setView(customView);
                        AlertDialog dialog;

                        dialog = builder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();


                        if (!IDSesionIniciada.equals(ID_usuario)) {
                            LayoutMandarUbicacion.setVisibility(View.GONE);
                            LayoutMandarFoto.setVisibility(View.GONE);
                            LayoutFinalizarActividad.setVisibility(View.GONE);
                            LayoutCancelarActividad.setVisibility(View.GONE);
                            LayoutVerDetalles.setVisibility(View.VISIBLE);
                        }


                        LayoutCancelarActividad.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Inflar el diseño personalizado del diálogo
                                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.opcion_cancelar, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setView(dialogView);

                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogConf, int which) {
                                        EditText editText = dialogView.findViewById(R.id.editText);

                                        String userInput = editText.getText().toString();
                                        if (userInput.isEmpty() || userInput.equals("")) {
                                            Toast.makeText(context, "Por favor ingresa el motivo de la cancelacion", Toast.LENGTH_SHORT).show();
                                        } else {
                                            String selectedEstado = "Cancelado";
                                            actionListener.onCancelarActividadesActivity(ID_actividad, selectedEstado, userInput);
                                            dialogConf.dismiss();
                                            dialog.dismiss();
                                        }
                                    }
                                });

                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                // Mostrar el diálogo
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });

                        LayoutFinalizarActividad.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setTitle("¿ESTAS SEGURO DE QUE DESEAS FINALIZAR ESTA ACTIVIDAD?");
                                builder.setMessage("\n\n\nRecuerda que no podras enviar evidencia una vez finalizada la actividad\n\n\n");

                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogConf, int which) {
                                        String selectedEstado = "Finalizado";
                                        //  ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                                        actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);
                                        dialogConf.dismiss();
                                        dialog.dismiss();
                                    }
                                });

                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                // Mostrar el diálogo
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        });


                        LayoutVerDetalles.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DetallesActividadesFragment detallesActividadesFragment = new DetallesActividadesFragment();
                                detallesActividadesFragment.setArguments(bundle);

                                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame_layouts_fragments, detallesActividadesFragment)
                                        .addToBackStack(null)
                                        .commit();

                                dialog.dismiss();
                            }
                        });

                        LayoutMandarUbicacion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setTitle("Confirmacion para mandar ubicaciòn");
                                builder.setMessage("¿Estás seguro de que deseas mandar tu ubicacion para esta actividad?");

                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override

                                    public void onClick(DialogInterface dialogConfir, int which) {

                                        obtenerUbicacion(context, ID_usuario, ID_actividad);
                                        dialogConfir.dismiss();
                                        dialog.dismiss();

                                    }
                                });

                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                // Mostrar el diálogo
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        });

                        LayoutMandarFoto.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // Crear un Intent y adjuntar el Bundle
                                Intent intent = new Intent(v.getContext(), SubirFotoActivity.class);
                                intent.putExtras(bundle);

                                // Iniciar el nuevo Activity
                                v.getContext().startActivity(intent);
                            }
                        });


                        builder.setNegativeButton("Cancelar", null);

                        dialog.show();


                    }
                });
            } else if (estadoActividad.equals("Pendiente")) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        VerNombresActividades(view.getContext());

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Que deseas hacer con " + descripcionActividad + " ?");
                        View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_actividades, null);

                        LinearLayout LayoutEditar = customView.findViewById(R.id.LayoutEditar);
                        LinearLayout LayoutEliminar = customView.findViewById(R.id.LayoutEliminar);
                        LinearLayout LayoutActualizarEstado = customView.findViewById(R.id.LayoutActualizarEstado);
                        LinearLayout LayoutVerDetalles = customView.findViewById(R.id.LayoutVerDetalles);

                        LayoutVerDetalles.setVisibility(View.GONE);
                        if (!IDSesionIniciada.equals(ID_usuario)) {
                            LayoutEditar.setVisibility(View.GONE);
                            LayoutEliminar.setVisibility(View.GONE);
                            LayoutActualizarEstado.setVisibility(View.GONE);
                            LayoutVerDetalles.setVisibility(View.VISIBLE);
                        }


                        Spinner SpinnerNombreActividad = customView.findViewById(R.id.SpinnerNombreActividad);
                        EditText editextDescripcionActividad = customView.findViewById(R.id.editextDescripcionActividad);
                        Button BotonActualizarActividad = customView.findViewById(R.id.BotonActualizarActividad);

                        LinearLayout LayoutPendiente = customView.findViewById(R.id.LayoutPendiente);
                        LinearLayout LayoutIniciado = customView.findViewById(R.id.LayoutIniciado);
                        LinearLayout LayoutFinalizado = customView.findViewById(R.id.LayoutFinalizado);
                        builder.setView(customView);
                        final AlertDialog dialog = builder.create();

                        nombresActividades.add(0, "Selecciona una opción");
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, nombresActividades);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        SpinnerNombreActividad.setAdapter(spinnerAdapter);
                        SpinnerNombreActividad.setSelection(0);

                        LayoutVerDetalles.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DetallesActividadesFragment detallesActividadesFragment = new DetallesActividadesFragment();
                                detallesActividadesFragment.setArguments(bundle);

                                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame_layouts_fragments, detallesActividadesFragment)
                                        .addToBackStack(null)
                                        .commit();

                                dialog.dismiss();
                            }
                        });

                        if (IDSesionIniciada.equals(ID_usuario) && estadoActividad.equals("Pendiente")) {


                            LayoutActualizarEstado.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    LayoutActualizarEstado.setVisibility(View.GONE);
                                    LayoutEditar.setVisibility(View.GONE);
                                    LayoutEliminar.setVisibility(View.GONE);

                                    if (estadoActividad.equals("Pendiente")) {
                                        LayoutFinalizado.setVisibility(View.GONE);
                                        LayoutIniciado.setVisibility(View.VISIBLE);
                                    } else if (estadoActividad.equals("Iniciado")) {
                                        LayoutPendiente.setVisibility(View.GONE);
                                        LayoutFinalizado.setVisibility(View.VISIBLE);
                                    }


                                    LayoutPendiente.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String selectedEstado = "Pendiente";
                                            actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);
                                            dialog.dismiss();
                                        }
                                    });

                                    LayoutIniciado.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String selectedEstado = "Iniciado";
                                            //  ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                                            actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);
                                            dialog.dismiss();

                                        }
                                    });

                                    LayoutFinalizado.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String selectedEstado = "Finalizado";
                                            //  ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                                            actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });

                            LayoutEditar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    editextDescripcionActividad.setText(descripcionActividad);
                                    LayoutEditar.setVisibility(View.GONE);
                                    LayoutEliminar.setVisibility(View.GONE);
                                    LayoutActualizarEstado.setVisibility(View.GONE);
                                    SpinnerNombreActividad.setVisibility(View.VISIBLE);
                                    editextDescripcionActividad.setVisibility(View.VISIBLE);
                                    BotonActualizarActividad.setVisibility(View.VISIBLE);
                                    BotonActualizarActividad.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String valorActividadSpinner = SpinnerNombreActividad.getSelectedItem().toString();
                                            if (!valorActividadSpinner.equals("Selecciona una opción")) {
                                                String descripcionActividad = editextDescripcionActividad.getText().toString();
                                                String selectedID = obtenerIDDesdeNombre(valorActividadSpinner);

                                                actionListener.onEditActivity(ID_actividad, selectedID, descripcionActividad);
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(view.getContext(), "Debes seleccionar una actividad válida.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            });

                            LayoutEliminar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setTitle("Confirmar Eliminación");
                                    builder.setMessage("¿Estás seguro de que deseas eliminar esta actividad?");

                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override

                                        public void onClick(DialogInterface dialogConfirmacion, int which) {
                                            //      EliminarActividad(ID_actividad, view.getContext());

                                            actionListener.onDeleteActivity(ID_actividad);
                                            dialogConfirmacion.dismiss();
                                            dialog.dismiss();
                                        }
                                    });

                                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });
                        }
                        builder.setNegativeButton("Cancelar", null);

                        dialog.show();
                    }
                });

            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Selecciona el nuevo estado de la actividad");

                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.spinner_dropdown_item, null);
                    LinearLayout LayoutMandarUbicacion = customView.findViewById(R.id.LayoutMandarUbicacion);
                    LinearLayout LayoutMandarFoto = customView.findViewById(R.id.LayoutMandarFoto);
                    LinearLayout LayoutVerDetalles = customView.findViewById(R.id.LayoutVerDetalles);
                    LinearLayout LayoutFinalizarActividad = customView.findViewById(R.id.LayoutFinalizarActividad);
                    LinearLayout LayoutCancelarActividad = customView.findViewById(R.id.LayoutCancelarActividad);


                    builder.setView(customView);
                    AlertDialog dialog;

                    dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();


                    if (!IDSesionIniciada.equals(ID_usuario)) {
                        LayoutMandarUbicacion.setVisibility(View.GONE);
                        LayoutMandarFoto.setVisibility(View.GONE);
                        LayoutFinalizarActividad.setVisibility(View.GONE);
                        LayoutCancelarActividad.setVisibility(View.GONE);
                        LayoutVerDetalles.setVisibility(View.VISIBLE);
                    }


                    LayoutCancelarActividad.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Inflar el diseño personalizado del diálogo
                            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.opcion_cancelar, null);

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setView(dialogView);

                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogConf, int which) {
                                    EditText editText = dialogView.findViewById(R.id.editText);

                                    String userInput = editText.getText().toString();
                                    if (userInput.isEmpty() || userInput.equals("")) {
                                        Toast.makeText(context, "Por favor ingresa el motivo de la cancelacion", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String selectedEstado = "Cancelado";
                                        actionListener.onCancelarActividadesActivity(ID_actividad, selectedEstado, userInput);
                                        dialogConf.dismiss();
                                        dialog.dismiss();
                                    }
                                }
                            });

                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            // Mostrar el diálogo
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });

                    LayoutFinalizarActividad.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("¿ESTAS SEGURO DE QUE DESEAS FINALIZAR ESTA ACTIVIDAD?");
                            builder.setMessage("\n\n\nRecuerda que no podras enviar evidencia una vez finalizada la actividad\n\n\n");

                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogConf, int which) {
                                    String selectedEstado = "Finalizado";
                                    //  ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                                    actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);
                                    dialogConf.dismiss();
                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            // Mostrar el diálogo
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                    });


                    LayoutVerDetalles.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DetallesActividadesFragment detallesActividadesFragment = new DetallesActividadesFragment();
                            detallesActividadesFragment.setArguments(bundle);

                            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.frame_layouts_fragments, detallesActividadesFragment)
                                    .addToBackStack(null)
                                    .commit();

                            dialog.dismiss();
                        }
                    });

                    LayoutMandarUbicacion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Confirmacion para mandar ubicaciòn");
                            builder.setMessage("¿Estás seguro de que deseas mandar tu ubicacion para esta actividad?");

                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override

                                public void onClick(DialogInterface dialogConfir, int which) {

                                    obtenerUbicacion(context, ID_usuario, ID_actividad);
                                    dialogConfir.dismiss();
                                    dialog.dismiss();

                                }
                            });

                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            // Mostrar el diálogo
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                    });

                    LayoutMandarFoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // Crear un Intent y adjuntar el Bundle
                            Intent intent = new Intent(v.getContext(), SubirFotoActivity.class);
                            intent.putExtras(bundle);

                            // Iniciar el nuevo Activity
                            v.getContext().startActivity(intent);
                        }
                    });


                    builder.setNegativeButton("Cancelar", null);

                    dialog.show();


                }
            });
        } else if (estadoActividad.equals("Pendiente")) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VerNombresActividades(view.getContext());

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Que deseas hacer con " + descripcionActividad + " ?");
                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_actividades, null);

                    LinearLayout LayoutEditar = customView.findViewById(R.id.LayoutEditar);
                    LinearLayout LayoutEliminar = customView.findViewById(R.id.LayoutEliminar);
                    LinearLayout LayoutActualizarEstado = customView.findViewById(R.id.LayoutActualizarEstado);
                    LinearLayout LayoutVerDetalles = customView.findViewById(R.id.LayoutVerDetalles);

                    LayoutVerDetalles.setVisibility(View.GONE);
                    if (!IDSesionIniciada.equals(ID_usuario)) {
                        LayoutEditar.setVisibility(View.GONE);
                        LayoutEliminar.setVisibility(View.GONE);
                        LayoutActualizarEstado.setVisibility(View.GONE);
                        LayoutVerDetalles.setVisibility(View.VISIBLE);
                    }


                    Spinner SpinnerNombreActividad = customView.findViewById(R.id.SpinnerNombreActividad);
                    EditText editextDescripcionActividad = customView.findViewById(R.id.editextDescripcionActividad);
                    Button BotonActualizarActividad = customView.findViewById(R.id.BotonActualizarActividad);

                    LinearLayout LayoutPendiente = customView.findViewById(R.id.LayoutPendiente);
                    LinearLayout LayoutIniciado = customView.findViewById(R.id.LayoutIniciado);
                    LinearLayout LayoutFinalizado = customView.findViewById(R.id.LayoutFinalizado);
                    builder.setView(customView);
                    final AlertDialog dialog = builder.create();

                    nombresActividades.add(0, "Selecciona una opción");
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, nombresActividades);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    SpinnerNombreActividad.setAdapter(spinnerAdapter);
                    SpinnerNombreActividad.setSelection(0);

                    LayoutVerDetalles.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DetallesActividadesFragment detallesActividadesFragment = new DetallesActividadesFragment();
                            detallesActividadesFragment.setArguments(bundle);

                            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.frame_layouts_fragments, detallesActividadesFragment)
                                    .addToBackStack(null)
                                    .commit();

                            dialog.dismiss();
                        }
                    });

                    if (IDSesionIniciada.equals(ID_usuario) && estadoActividad.equals("Pendiente")) {


                        LayoutActualizarEstado.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                LayoutActualizarEstado.setVisibility(View.GONE);
                                LayoutEditar.setVisibility(View.GONE);
                                LayoutEliminar.setVisibility(View.GONE);

                                if (estadoActividad.equals("Pendiente")) {
                                    LayoutFinalizado.setVisibility(View.GONE);
                                    LayoutIniciado.setVisibility(View.VISIBLE);
                                } else if (estadoActividad.equals("Iniciado")) {
                                    LayoutPendiente.setVisibility(View.GONE);
                                    LayoutFinalizado.setVisibility(View.VISIBLE);
                                }


                                LayoutPendiente.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String selectedEstado = "Pendiente";
                                        actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);
                                        dialog.dismiss();
                                    }
                                });

                                LayoutIniciado.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String selectedEstado = "Iniciado";
                                        //  ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                                        actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);
                                        dialog.dismiss();

                                    }
                                });

                                LayoutFinalizado.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String selectedEstado = "Finalizado";
                                        //  ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                                        actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });

                        LayoutEditar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editextDescripcionActividad.setText(descripcionActividad);
                                LayoutEditar.setVisibility(View.GONE);
                                LayoutEliminar.setVisibility(View.GONE);
                                LayoutActualizarEstado.setVisibility(View.GONE);
                                SpinnerNombreActividad.setVisibility(View.VISIBLE);
                                editextDescripcionActividad.setVisibility(View.VISIBLE);
                                BotonActualizarActividad.setVisibility(View.VISIBLE);
                                BotonActualizarActividad.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String valorActividadSpinner = SpinnerNombreActividad.getSelectedItem().toString();
                                        if (!valorActividadSpinner.equals("Selecciona una opción")) {
                                            String descripcionActividad = editextDescripcionActividad.getText().toString();
                                            String selectedID = obtenerIDDesdeNombre(valorActividadSpinner);

                                            actionListener.onEditActivity(ID_actividad, selectedID, descripcionActividad);
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(view.getContext(), "Debes seleccionar una actividad válida.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });

                        LayoutEliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setTitle("Confirmar Eliminación");
                                builder.setMessage("¿Estás seguro de que deseas eliminar esta actividad?");

                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override

                                    public void onClick(DialogInterface dialogConfirmacion, int which) {
                                        //      EliminarActividad(ID_actividad, view.getContext());

                                        actionListener.onDeleteActivity(ID_actividad);
                                        dialogConfirmacion.dismiss();
                                        dialog.dismiss();
                                    }
                                });

                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });
                    }
                    builder.setNegativeButton("Cancelar", null);

                    dialog.show();
                }
            });

        }

        */

        } finally {

        }
    }


    private void actualizarEstadoYVista(ViewHolder holder, String estadoActividad) {
        int colorVerde = ContextCompat.getColor(context, R.color.verde);

        holder.textStatus.setText(estadoActividad);
        if (estadoActividad.equals("Pendiente")) {
            int colorAmarillo = ContextCompat.getColor(context, R.color.amarillo);

            /*
            holder.EstadoPendiente.setVisibility(View.VISIBLE);
            holder.EstadoIniciado.setVisibility(View.INVISIBLE);
            holder.EstadoFinalizado.setVisibility(View.INVISIBLE);

             */
            holder.textStatus.setTextColor(colorAmarillo);

        } else if (estadoActividad.equals("Iniciado")) {
            int colorNegro = ContextCompat.getColor(context, R.color.black);
            /*

            holder.EstadoPendiente.setVisibility(View.INVISIBLE);
            holder.EstadoIniciado.setVisibility(View.VISIBLE);
            holder.EstadoFinalizado.setVisibility(View.INVISIBLE);

             */

            holder.textStatus.setTextColor(colorNegro);
        } else if (estadoActividad.equals("Finalizado")) {

            /*

            holder.EstadoPendiente.setVisibility(View.INVISIBLE);
            holder.EstadoIniciado.setVisibility(View.INVISIBLE);
            holder.EstadoFinalizado.setVisibility(View.VISIBLE);
             */

            holder.textStatus.setTextColor(colorVerde);
        } else if (estadoActividad.equals("Cancelado")) {

            int colorRojo = ContextCompat.getColor(context, R.color.rojo);
            holder.textStatus.setTextColor(colorRojo);


        } else {
            int colorRojo = ContextCompat.getColor(context, R.color.rojo);
            holder.textStatus.setTextColor(colorRojo);
        }
    }


    @Override
    public int getItemCount() {
        return filteredData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textFechaActividad, textStatus, textActividad, textDetallesActividad, textIdActividad, textFechaFin;
        FrameLayout FrameActividades;

        TextView errorMessageTextView;
        Button myButton;
        ImageView ImagenDeEstado;

        TextView textMotivoCancelacion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textFechaActividad = itemView.findViewById(R.id.textFechaActividad);
            textStatus = itemView.findViewById(R.id.textStatus);
            textActividad = itemView.findViewById(R.id.textActividad);
            FrameActividades = itemView.findViewById(R.id.FrameActividades);
            textDetallesActividad = itemView.findViewById(R.id.textDetallesActividad);
            textIdActividad = itemView.findViewById(R.id.textIdActividad);
            textMotivoCancelacion = itemView.findViewById(R.id.textMotivoCancelacion);
            /*
            EstadoFinalizado = itemView.findViewById(R.id.EstadoFinalizado);
            EstadoIniciado = itemView.findViewById(R.id.EstadoIniciado);
            EstadoPendiente = itemView.findViewById(R.id.EstadoPendiente);

             */
            ImagenDeEstado = itemView.findViewById(R.id.ImagenDeEstado);
            textFechaFin = itemView.findViewById(R.id.textFechaFin);
        }
    }

    public void filter(String query) {
        filteredData.clear();

        if (TextUtils.isEmpty(query)) {
            filteredData.addAll(data);
        } else {
            String[] keywords = query.toLowerCase().split(" ");

            for (JSONObject item : data) {
                String ID_actividad = item.optString("ID_actividad", "").toLowerCase();
                String nombre_actividad = item.optString("nombre_actividad", "").toLowerCase();
                String descripcionActividad = item.optString("descripcionActividad", "").toLowerCase();
                String estadoActividad = item.optString("estadoActividad", "").toLowerCase();
                String fecha_inicio = item.optString("fecha_inicio", "").toLowerCase();
                String fecha_fin = item.optString("fecha_fin", "").toLowerCase();


                String ID_usuario = item.optString("ID_usuario", "").toLowerCase();
                String ID_nombre_actividad = item.optString("ID_nombre_actividad", "").toLowerCase();
                String permisos = item.optString("permisos", "").toLowerCase();
                String correo = item.optString("correo", "").toLowerCase();
                String telefono = item.optString("telefono", "").toLowerCase();

                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(estadoActividad.contains(keyword) || descripcionActividad.contains(keyword) || nombre_actividad.contains(keyword) || ID_actividad.contains(keyword) ||
                            fecha_inicio.contains(keyword) || fecha_fin.contains(keyword) || ID_usuario.contains(keyword) || ID_nombre_actividad.contains(keyword) || permisos.contains(keyword) || telefono.contains(keyword) || correo.contains(keyword))) {
                        matchesAllKeywords = false;
                        break;
                    }
                }

                if (matchesAllKeywords) {
                    filteredData.add(item);
                }
            }
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

    private void obtenerUbicacion(Context context, String ID_usuario, String ID_actividad) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);

            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            //MandarUbicacion(ID_usuario, ID_actividad, longitude, latitude, context);
                            actionListener.onMandarUbicacionActicity(ID_usuario, ID_actividad, longitude, latitude);

                        } else {
                            crearToastPersonalizado(context, "No se pudo obtener la ubicación. Revisa la conexión o los permisos");

                        }
                    }
                });
    }

    private void VerNombresActividades(Context context) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    nombresActividades.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String ID_nombre_actividad = jsonObject.getString("ID_nombre_actividad");
                        String nombre_actividad = jsonObject.getString("nombre_actividad");
                        nombresActividades.add(ID_nombre_actividad + ": " + nombre_actividad);
                    }
                } catch (JSONException e) {
                    crearToastPersonalizado(context, "No se pudieron obtener los datos. Revisa la conexión");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                crearToastPersonalizado(context, "No se pudieron obtener los datos. Revisa la conexión");
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


    AlertDialog dialogCargando;

    AlertDialog.Builder builderCargando;

    private void consultarSaldoActivo(String ID_usuario, String ID_actividad, AlertDialog dialogOpcionesDeActividad) {


        dialogCargando = Utils.ModalCargando(context, builderCargando);

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("sin saldo activo")) {

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
                            //  ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                            actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);
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


                } else {


                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        String ID_saldo = jsonObject.getString("ID_saldo");
                        String saldo_actualizado = jsonObject.getString("saldo_actualizado");


                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View customView = LayoutInflater.from(context).inflate(R.layout.confirmacion_con_clave, null);
                        builder.setView(ModalRedondeado(context, customView));
                        AlertDialog dialogConfirmacion = builder.create();
                        dialogConfirmacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogConfirmacion.show();


                        CheckBox checkSinGastos = customView.findViewById(R.id.checkSinGastos);

                        TextView nuevomonto = customView.findViewById(R.id.nuevomonto);
                        nuevomonto.setVisibility(View.VISIBLE);
                        nuevomonto.setText("Tu saldo actual es de: " + saldo_actualizado);

                        TextInputLayout textInputLayout = customView.findViewById(R.id.textInputLayout);
                        EditText nuevoMonto = customView.findViewById(R.id.nuevoMonto);
                        EditText editTextClaveUsuario = customView.findViewById(R.id.editTextClaveUsuario);

                        TextView textView4 = customView.findViewById(R.id.textView4);
                        textView4.setText("Para finalizar esta actividad debes ingresar el monto  \n \nNo olvides subir tu comprobante");

                        editTextClaveUsuario.setVisibility(View.GONE);
                        textInputLayout.setVisibility(View.VISIBLE);


                        Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                        Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);

                        checkSinGastos.setVisibility(View.VISIBLE);

                        checkSinGastos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (checkSinGastos.isChecked()) {

                                    textView4.setText("Vas a finalizar esta actividad sin agregar un monto");
                                    textInputLayout.setVisibility(View.GONE);
                                    nuevomonto.setVisibility(View.GONE);
                                } else {

                                    textView4.setText("Para finalizar esta actividad debes ingresar el monto  \n \nNo olvides subir tu comprobante");
                                    textInputLayout.setVisibility(View.VISIBLE);
                                    nuevomonto.setVisibility(View.VISIBLE);
                                }

                            }
                        });


                        buttonAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (checkSinGastos.isChecked()) {

                                    String selectedEstado = "Finalizado";
                                    //  ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);

                                    dialogConfirmacion.dismiss();
                                    dialogOpcionesDeActividad.dismiss();
                                    actionListener.onActualizarEstadoActivity(ID_actividad, selectedEstado);

                                } else {
                                    String total_gastado = nuevoMonto.getText().toString();
                                    if (total_gastado.isEmpty() || total_gastado.equals("0")) {
                                        Utils.crearToastPersonalizado(context, "Debes ingresar un monto");

                                    } else {


                                        try {
                                            double totalGastadoDob = Double.parseDouble(total_gastado);
                                            double saldoActualizadoDob = Double.parseDouble(saldo_actualizado);

                                            if (totalGastadoDob > saldoActualizadoDob) {

                                                Utils.crearToastPersonalizado(context, "No puedes ingresar un monto mayor al saldo que tienes asignado");

                                            } else {

                                                dialogConfirmacion.dismiss();
                                                dialogOpcionesDeActividad.dismiss();
                                                actionListener.onAsignarMontoAActividad(total_gastado, ID_saldo, ID_actividad);
                                            }


                                        } catch (NumberFormatException e) {

                                        }

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

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utils.crearToastPersonalizado(context, "Hubo un error al cargar los datos");
                    }

                    CerrarModalCargando();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                crearToastPersonalizado(context, "No se pudieron obtener los datos. Revisa la conexión");
                CerrarModalCargando();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "52");
                params.put("ID_usuario", ID_usuario);
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


    public interface OnActivityActionListener {

        void onEditActivity(String ID_actividad, String ID_nombre_actividad, String descripcionActividad);

        void onDeleteActivity(String ID_actividad);

        void onMandarUbicacionActicity(String ID_usuario, String ID_actividad, Double longitud, Double latitud);

        void onActualizarEstadoActivity(String ID_actividad, String nuevoEstado);


        void onCancelarActividadesActivity(String ID_actividad, String nuevoEstado, String motivoCancelacion);

        void onAsignarMontoAActividad(String total_gastado, String ID_saldo, String ID_actividad);
    }

    private AdaptadorActividades.OnActivityActionListener actionListener;


    public AdaptadorActividades(List<JSONObject> data, Context context, AdaptadorActividades.OnActivityActionListener actionListener) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        this.actionListener = actionListener;
        url = context.getResources().getString(R.string.urlApi);


        builderCargando = new AlertDialog.Builder(context);
        builderCargando.setCancelable(false);
    }


}


