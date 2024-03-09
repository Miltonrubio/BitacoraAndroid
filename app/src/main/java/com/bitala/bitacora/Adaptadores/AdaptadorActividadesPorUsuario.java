package com.bitala.bitacora.Adaptadores;


import static com.bitala.bitacora.Utils.ModalRedondeado;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.DetallesActividadesFragment;
import com.bitala.bitacora.R;
import com.bitala.bitacora.Utils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AdaptadorActividadesPorUsuario extends RecyclerView.Adapter<AdaptadorActividadesPorUsuario.ViewHolder> {


    private Context context;

    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    String url;
    String claveUsuario;


    AdaptadorActividadesPorUsuario.OnActivityActionListener actionListener;

    public AdaptadorActividadesPorUsuario(List<JSONObject> data, Context context, AdaptadorActividadesPorUsuario.OnActivityActionListener actionListener) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        this.actionListener = actionListener;

        url = context.getResources().getString(R.string.urlApi);
        SharedPreferences sharedPreferences = context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        String permisosUsuario = sharedPreferences.getString("permisos", "");
        this.ID_usuarioActual = sharedPreferences.getString("ID_usuario", "");
        this.claveUsuario = sharedPreferences.getString("clave", "");
    }

    String ID_usuarioActual;

    public interface OnActivityActionListener {

        void onDeleteActivity(String ID_actividad);

        void onCancelarActividadesActivity(String ID_actividad, String nuevoEstado, String motivoCancelacion);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividades_admin, parent, false);
        return new ViewHolder(view);

    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        int drawableResId = 0;
        int colorIcono = 0;


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
            String motivocancelacion = jsonObject2.optString("motivocancelacion", "");
            String fecha_asignacion = jsonObject2.optString("fecha_asignacion", "");
            String nombreQuienAsigno = jsonObject2.optString("nombreQuienAsigno", "");
            String ID_admin_asig = jsonObject2.optString("ID_admin_asig", "");


            if (ID_admin_asig.equalsIgnoreCase("") || ID_admin_asig.equalsIgnoreCase("null") || ID_admin_asig.equalsIgnoreCase(null) || ID_admin_asig.isEmpty()) {
                holder.AsignadoPor.setVisibility(View.GONE);
            } else {
                holder.AsignadoPor.setVisibility(View.VISIBLE);
            }


            holder.AsignadoPor.setText("Asignadá por: " + nombreQuienAsigno);

            Bundle bundle = new Bundle();
            bundle.putString("ID_actividad", ID_actividad);
            bundle.putString("nombreQuienAsigno", nombreQuienAsigno);
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
            bundle.putString("motivocancelacion", motivocancelacion);
            bundle.putString("fecha_asignacion", fecha_asignacion);
            bundle.putString("nombreQuienAsigno", nombreQuienAsigno);
            bundle.putString("ID_admin_asig", ID_admin_asig);
            bundle.putString("ID_nombre_actividad", ID_nombre_actividad);


            setTextViewText(holder.textActividad, nombre_actividad.toUpperCase(), "Actividad no disponible");
            SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat formatoDeseado = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'a las' HH:mm", Locale.getDefault());


            try {
                Date fechaAsig = formatoOriginal.parse(fecha_asignacion);
                String fechafechaAsigFormateada = "Asignada: " + formatoDeseado.format(fechaAsig);


                if (fecha_asignacion.isEmpty() || fecha_asignacion.equalsIgnoreCase("") || fecha_asignacion.equalsIgnoreCase("null") || fecha_asignacion.equalsIgnoreCase(null)) {

                    holder.FechaAsignacion.setVisibility(View.GONE);
                } else {


                    if (ID_usuarioActual.equalsIgnoreCase("23") || ID_usuarioActual.equalsIgnoreCase("64") || ID_usuarioActual.equalsIgnoreCase("42") || ID_usuarioActual.equalsIgnoreCase("45") || ID_usuarioActual.equalsIgnoreCase("30")) {
                        holder.FechaAsignacion.setVisibility(View.VISIBLE);
                    } else {
                        holder.FechaAsignacion.setVisibility(View.GONE);
                    }

                }

                holder.FechaAsignacion.setText(fechafechaAsigFormateada);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                Date fecha = formatoOriginal.parse(fecha_inicio);
                String fechaFormateada = "Inicio: " + formatoDeseado.format(fecha);
                setTextViewText(holder.textFechaActividad, fechaFormateada, "Aun no se ha iniciado la actividad");
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                Date fecha = formatoOriginal.parse(fecha_fin);

                if (estadoActividad.equalsIgnoreCase("Cancelado")) {
                    String fechaFormateadafin = "Cancelación: " + formatoDeseado.format(fecha);
                    setTextViewText(holder.textFechaFin, fechaFormateadafin, "No se encontro la fecha la actividad");
                } else {

                    String fechaFormateadafin = "Finalizó: " + formatoDeseado.format(fecha);
                    setTextViewText(holder.textFechaFin, fechaFormateadafin, "Aun no se ha finalizado la actividad");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            setTextViewText(holder.textStatus, estadoActividad.toUpperCase(), "Estado no disponible");


            if (estadoActividad.equalsIgnoreCase("Cancelado")) {

                setTextViewText(holder.textMotivoCancelacion, motivocancelacion, "No se agrego motivo");
                holder.textMotivoCancelacion.setVisibility(View.VISIBLE);
                holder.FrameActividades.setBackgroundResource(R.drawable.roundedbackgroundgris);
                holder.textStatus.setTextColor(ContextCompat.getColor(context, R.color.rojo));
                colorIcono = ContextCompat.getColor(context, R.color.rojo);
                drawableResId = R.drawable.baseline_cancel_24;

            } else {

                holder.textMotivoCancelacion.setVisibility(View.GONE);
                holder.textStatus.setTextColor(ContextCompat.getColor(context, android.R.color.black));


                if (estadoActividad.equalsIgnoreCase("Pendiente")) {
                    holder.FrameActividades.setBackgroundResource(R.drawable.rounded_amarillo);
                    colorIcono = ContextCompat.getColor(context, R.color.amarillo);
                    drawableResId = R.drawable.baseline_access_time_24;
                    holder.FrameActividades.setVisibility(View.VISIBLE);

                }


                if (estadoActividad.equalsIgnoreCase("Iniciado")) {
                    holder.FrameActividades.setBackgroundResource(R.drawable.rounded_grisesito);
                    colorIcono = ContextCompat.getColor(context, R.color.black);
                    drawableResId = R.drawable.baseline_checklist_24;
                }


                if (estadoActividad.equalsIgnoreCase("Finalizado")) {
                    holder.FrameActividades.setBackgroundResource(R.drawable.rounded_verdecito);
                    colorIcono = ContextCompat.getColor(context, R.color.verde);
                    drawableResId = R.drawable.baseline_done_24;
                }

            }

            holder.ImagenDeEstado.setImageResource(drawableResId);
            holder.ImagenDeEstado.setColorFilter(colorIcono);
            holder.textStatus.setTextColor(colorIcono);

            setTextViewText(holder.textDetallesActividad, descripcionActividad, "Actividad no disponible");

            if (ID_actividad.isEmpty() || ID_actividad.equals("null")) {

                setTextViewText(holder.textIdActividad, "ID no disponible", "ID no disponible");
            } else {
                setTextViewText(holder.textIdActividad, "ID: " + ID_actividad, "ID no disponible");
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DetallesActividadesFragment detallesActividadesFragment = new DetallesActividadesFragment();
                    detallesActividadesFragment.setArguments(bundle);

                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_layouts_fragments, detallesActividadesFragment)
                            .addToBackStack(null)
                            .commit();

                }
            });


            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View customView = LayoutInflater.from(context).inflate(R.layout.nuevas_opciones_actividades_admin, null);
                    builder.setView(ModalRedondeado(context, customView));
                    AlertDialog dialogOpcionesDeActividad = builder.create();
                    dialogOpcionesDeActividad.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

/*
                    if (!(ID_usuarioActual.equalsIgnoreCase("23") || ID_usuarioActual.equalsIgnoreCase("64") || ID_usuarioActual.equalsIgnoreCase("42") || ID_usuarioActual.equalsIgnoreCase("45") || ID_usuarioActual.equalsIgnoreCase("30")) && estadoActividad.equalsIgnoreCase("Cancelado")) {


                    } else {

                        dialogOpcionesDeActividad.show();

                    }
                    */

                    dialogOpcionesDeActividad.show();

                    LinearLayout LayoutCancelarActividad = customView.findViewById(R.id.LayoutCancelarActividad);
                    LinearLayout LayoutEliminarActividad = customView.findViewById(R.id.LayoutEliminarActividad);


                    if (ID_usuarioActual.equalsIgnoreCase("23") || ID_usuarioActual.equalsIgnoreCase("64") || ID_usuarioActual.equalsIgnoreCase("42")|| ID_usuarioActual.equalsIgnoreCase("45")) {

                        LayoutEliminarActividad.setVisibility(View.VISIBLE);

                    } else {
                        LayoutEliminarActividad.setVisibility(View.GONE);
                    }


                    if (estadoActividad.equalsIgnoreCase("Cancelado")) {
                        LayoutCancelarActividad.setVisibility(View.GONE);

                    } else {
                        LayoutCancelarActividad.setVisibility(View.VISIBLE);
                    }


                    LayoutCancelarActividad.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View customView = LayoutInflater.from(context).inflate(R.layout.confirmacion_actividades_con_clave, null);
                            builder.setView(ModalRedondeado(context, customView));
                            AlertDialog dialogConfirmacionClave = builder.create();
                            dialogConfirmacionClave.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogConfirmacionClave.show();


                            EditText editTextClaveUsuario = customView.findViewById(R.id.editTextClaveUsuario);
                            Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                            Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                            EditText editTextMotivoCancelacion = customView.findViewById(R.id.editTextMotivoCancelacion);
                            TextView labelCancelacion = customView.findViewById(R.id.labelCancelacion);

                            editTextMotivoCancelacion.setVisibility(View.VISIBLE);
                            labelCancelacion.setVisibility(View.VISIBLE);


                            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    String motivoCancelacion = editTextMotivoCancelacion.getText().toString();
                                    String claveEscrita = editTextClaveUsuario.getText().toString();

                                    if (motivoCancelacion.isEmpty()) {
                                        Utils.crearToastPersonalizado(context, "Debes ingresar un motivo de cancelacion");
                                    } else {

                                        if (claveEscrita.isEmpty()) {

                                            Utils.crearToastPersonalizado(context, "Debes ingresar tu clave para realizar la cancelación");
                                        } else {
                                            if (!claveEscrita.equals(claveUsuario)) {

                                                Utils.crearToastPersonalizado(context, "Debes ingresar la contraseña correcta ");
                                            } else {

                                                dialogConfirmacionClave.dismiss();
                                                dialogOpcionesDeActividad.dismiss();
                                                actionListener.onCancelarActividadesActivity(ID_actividad, "Cancelado", motivoCancelacion);

                                            }

                                        }

                                    }

                                }
                            });


                            buttonCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogConfirmacionClave.dismiss();
                                }
                            });


                        }
                    });


                    LayoutEliminarActividad.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Utils.crearToastPersonalizado(context, "Recuerda que la eliminación es permanente");

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View customView = LayoutInflater.from(context).inflate(R.layout.confirmacion_actividades_con_clave, null);
                            builder.setView(ModalRedondeado(context, customView));
                            AlertDialog dialogConfirmacionClave = builder.create();
                            dialogConfirmacionClave.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogConfirmacionClave.show();


                            EditText editTextClaveUsuario = customView.findViewById(R.id.editTextClaveUsuario);
                            Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                            Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                            EditText editTextMotivoCancelacion = customView.findViewById(R.id.editTextMotivoCancelacion);
                            TextView labelCancelacion = customView.findViewById(R.id.labelCancelacion);

                            editTextMotivoCancelacion.setVisibility(View.GONE);
                            labelCancelacion.setVisibility(View.GONE);


                            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String claveEscrita = editTextClaveUsuario.getText().toString();


                                    if (claveEscrita.isEmpty()) {

                                        Utils.crearToastPersonalizado(context, "Debes ingresar tu clave para realizar la eliminación");
                                    } else {
                                        if (!claveEscrita.equals(claveUsuario)) {

                                            Utils.crearToastPersonalizado(context, "Debes ingresar la contraseña correcta ");
                                        } else {

                                            dialogConfirmacionClave.dismiss();
                                            dialogOpcionesDeActividad.dismiss();
                                            actionListener.onDeleteActivity(ID_actividad);

                                        }

                                    }

                                }
                            });


                            buttonCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogConfirmacionClave.dismiss();
                                }
                            });


                        }
                    });


                    return false;
                }
            });


                /*

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

                        Spinner SpinnerNombreActividad = customView.findViewById(R.id.SpinnerNombreActividad);
                        EditText editextDescripcionActividad = customView.findViewById(R.id.editextDescripcionActividad);
                        Button BotonActualizarActividad = customView.findViewById(R.id.BotonActualizarActividad);

                        LinearLayout LayoutPendiente = customView.findViewById(R.id.LayoutPendiente);
                        LinearLayout LayoutIniciado = customView.findViewById(R.id.LayoutIniciado);
                        LinearLayout LayoutFinalizado = customView.findViewById(R.id.LayoutFinalizado);

                        LayoutPendiente.setVisibility(View.GONE);
                        LayoutIniciado.setVisibility(View.GONE);
                        LayoutFinalizado.setVisibility(View.GONE);
                        BotonActualizarActividad.setVisibility(View.GONE);
                        LayoutEditar.setVisibility(View.GONE);
                        LayoutEliminar.setVisibility(View.GONE);
                        LayoutActualizarEstado.setVisibility(View.GONE);
                        editextDescripcionActividad.setVisibility(View.GONE);


                        builder.setView(customView);
                        final AlertDialog dialog = builder.create();

                        nombresActividades.add(0, "Selecciona una opción");
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, nombresActividades);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        SpinnerNombreActividad.setAdapter(spinnerAdapter);

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


                        builder.setNegativeButton("Cancelar", null);

                        dialog.show(); // Muestra el diálogo
                    }
                });
*/
        } finally {

        }

    }


    @Override
    public int getItemCount() {

        //return filteredData.size();
        return filteredData.size();

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textFechaActividad, textStatus, textActividad, textDetallesActividad, textIdActividad, textFechaFin, textMotivoCancelacion;
        FrameLayout FrameActividades;

        ImageView IMNoInternet; //, EstadoFinalizado, EstadoIniciado, EstadoPendiente, EstadoCancelado;
        TextView FechaAsignacion;

        ImageView ImagenDeEstado;

        TextView AsignadoPor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            FechaAsignacion = itemView.findViewById(R.id.FechaAsignacion);
            textFechaActividad = itemView.findViewById(R.id.textFechaActividad);
            textStatus = itemView.findViewById(R.id.textStatus);
            textActividad = itemView.findViewById(R.id.textActividad);
            FrameActividades = itemView.findViewById(R.id.FrameActividades);
            textDetallesActividad = itemView.findViewById(R.id.textDetallesActividad);
            textFechaFin = itemView.findViewById(R.id.textFechaFin);
            textIdActividad = itemView.findViewById(R.id.textIdActividad);
            ImagenDeEstado = itemView.findViewById(R.id.ImagenDeEstado);
            /*
            EstadoFinalizado = itemView.findViewById(R.id.EstadoFinalizado);
            EstadoIniciado = itemView.findViewById(R.id.EstadoIniciado);
            EstadoPendiente = itemView.findViewById(R.id.EstadoPendiente);
            EstadoCancelado = itemView.findViewById(R.id.EstadoCancelado);
            */
            textMotivoCancelacion = itemView.findViewById(R.id.textMotivoCancelacion);
            AsignadoPor = itemView.findViewById(R.id.AsignadoPor);
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



                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(estadoActividad.contains(keyword) || descripcionActividad.contains(keyword) || nombre_actividad.contains(keyword) || ID_actividad.contains(keyword) ||
                            fecha_inicio.contains(keyword) || fecha_fin.contains(keyword)

                          )) {
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

}

