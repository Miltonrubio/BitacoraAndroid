package com.example.bitacora;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdaptadorActividades extends RecyclerView.Adapter<AdaptadorActividades.ViewHolder> {

    String siguienteEstado = "";

    private static final int VIEW_TYPE_ERROR = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Context context;

    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    public AdaptadorActividades(List<JSONObject> data, Context context) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividades, parent, false);
            return new ViewHolder(view);
        } else {

            View errorView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_error, parent, false);
            return new ViewHolder(errorView);
        }

    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            try {
                JSONObject jsonObject2 = filteredData.get(position);
                String ID_actividad = jsonObject2.optString("ID_actividad", "");
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


                setTextViewText(holder.textNombreUsuario, nombre, "Nombre no disponible");

                setTextViewText(holder.textActividad, nombre_actividad, "Actividad no disponible");
                // Crear un objeto SimpleDateFormat para el formato deseado
                SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat formatoDeseado = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy  HH:mm", Locale.getDefault());

                try {
                    Date fecha = formatoOriginal.parse(fecha_inicio);
                    String fechaFormateada = formatoDeseado.format(fecha);

                    setTextViewText(holder.textFechaActividad, fechaFormateada, "Fecha no disponible");
                } catch (Exception e) {
                    e.printStackTrace();
                }



                setTextViewText(holder.textStatus, estadoActividad.toUpperCase(), "Estado no disponible");
                actualizarEstadoYVista(holder, estadoActividad);

                setTextViewText(holder.textTelefonoUsuario, telefono, "Telefono no disponible");
                setTextViewText(holder.textDetallesActividad, descripcionActividad, "Actividad no disponible");

                if (ID_actividad.isEmpty() || ID_actividad.equals("null")) {

                    setTextViewText(holder.textIdActividad, "ID no disponible", "ID no disponible");
                } else {
                    setTextViewText(holder.textIdActividad, "ID de actividad: " + ID_actividad, "ID no disponible");
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Selecciona el nuevo estado de la actividad");

                        // Inflar el diseño personalizado con tres botones
                        View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.spinner_dropdown_item, null);

                        // Obtener referencias a los botones en el diseño personalizado
                        LinearLayout LayoutPendiente = customView.findViewById(R.id.LayoutPendiente);
                        LinearLayout LayoutIniciado = customView.findViewById(R.id.LayoutIniciado);
                        LinearLayout LayoutFinalizado = customView.findViewById(R.id.LayoutFinalizado);

                        builder.setView(customView);

                        // Crear el diálogo
                        final AlertDialog dialog = builder.create();

                        // Configurar los botones según tus necesidades
                        LayoutPendiente.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String selectedEstado = "Pendiente";
                                ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                            }
                        });

                        LayoutIniciado.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String selectedEstado = "Iniciado";
                                ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                            }
                        });

                        LayoutFinalizado.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String selectedEstado = "Finalizado";
                                ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                            }
                        });

                        builder.setNegativeButton("Cancelar", null);

                        dialog.show(); // Muestra el diálogo
                    }
                });

            } finally {

            }
        }
    }

    private void actualizarEstadoYVista(ViewHolder holder, String estadoActividad) {
        int colorVerde = ContextCompat.getColor(context, R.color.verde);
        int fondoPersonalizado = R.drawable.roundedbackgroundgris;

        holder.textStatus.setText(estadoActividad);
        if (estadoActividad.equals("Pendiente")) {
            int colorAmarillo = ContextCompat.getColor(context, R.color.amarillo);

            holder.EstadoPendiente.setVisibility(View.VISIBLE);
            holder.EstadoIniciado.setVisibility(View.INVISIBLE);
            holder.EstadoFinalizado.setVisibility(View.INVISIBLE);
            holder.textStatus.setTextColor(colorAmarillo);
            siguienteEstado = "Iniciado";

        } else if (estadoActividad.equals("Iniciado")) {
            int colorNegro = ContextCompat.getColor(context, R.color.black);

            holder.EstadoPendiente.setVisibility(View.INVISIBLE);
            holder.EstadoIniciado.setVisibility(View.VISIBLE);
            holder.EstadoFinalizado.setVisibility(View.INVISIBLE);
            holder.textStatus.setTextColor(colorNegro);
            siguienteEstado = "Finalizado";

        } else if (estadoActividad.equals("Finalizado")) {
            holder.EstadoPendiente.setVisibility(View.INVISIBLE);
            holder.EstadoIniciado.setVisibility(View.INVISIBLE);
            holder.EstadoFinalizado.setVisibility(View.VISIBLE);
            holder.textStatus.setTextColor(colorVerde);
            siguienteEstado = "Pendiente";

        } else if (estadoActividad.equals("Cancelado")) {
            int colorRojo = ContextCompat.getColor(context, R.color.rojo);
            holder.textStatus.setTextColor(colorRojo);
            holder.FrameActividades.setBackgroundResource(fondoPersonalizado);
        } else {
            int colorRojo = ContextCompat.getColor(context, R.color.rojo);
            holder.textStatus.setTextColor(colorRojo);
            holder.FrameActividades.setBackgroundResource(fondoPersonalizado);
        }
    }


    @Override
    public int getItemCount() {

        //return filteredData.size();
        return filteredData.isEmpty() ? 1 : filteredData.size();

    }

    @Override
    public int getItemViewType(int position) {
        return filteredData.isEmpty() ? VIEW_TYPE_ERROR : VIEW_TYPE_ITEM;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textFechaActividad, textStatus, textTelefonoUsuario, textNombreUsuario, textActividad, textDetallesActividad, textIdActividad;
        FrameLayout FrameActividades;

        ImageView IMNoInternet, EstadoFinalizado, EstadoIniciado, EstadoPendiente;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textFechaActividad = itemView.findViewById(R.id.textFechaActividad);
            textStatus = itemView.findViewById(R.id.textStatus);
            IMNoInternet = itemView.findViewById(R.id.IMNoInternet);
            textTelefonoUsuario = itemView.findViewById(R.id.textTelefonoUsuario);
            textNombreUsuario = itemView.findViewById(R.id.textNombreUsuario);
            textActividad = itemView.findViewById(R.id.textActividad);
            FrameActividades = itemView.findViewById(R.id.FrameActividades);
            textDetallesActividad = itemView.findViewById(R.id.textDetallesActividad);
            textIdActividad = itemView.findViewById(R.id.textIdActividad);
            EstadoFinalizado = itemView.findViewById(R.id.EstadoFinalizado);
            EstadoIniciado = itemView.findViewById(R.id.EstadoIniciado);
            EstadoPendiente = itemView.findViewById(R.id.EstadoPendiente);


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
                String nombreActividad = item.optString("nombreActividad", "").toLowerCase();
                String descripcionActividad = item.optString("descripcionActividad", "").toLowerCase();
                String estadoActividad = item.optString("estadoActividad", "").toLowerCase();
                String fecha_inicio = item.optString("fecha_inicio", "").toLowerCase();
                String fecha_fin = item.optString("fecha_fin", "").toLowerCase();

                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(estadoActividad.contains(keyword) || descripcionActividad.contains(keyword) || nombreActividad.contains(keyword) || ID_actividad.contains(keyword) ||
                            fecha_inicio.contains(keyword) || fecha_fin.contains(keyword))) {
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

    private void ActualizarEstado(String ID_actividad, String nuevoEstado, Context context, ViewHolder holder, AlertDialog dialog) {
        String url = "http://192.168.1.125/android/mostrar.php";
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Aquí puedes realizar acciones adicionales si es necesario
                Toast.makeText(context, "Exito", Toast.LENGTH_SHORT).show();

                // Llama al método para actualizar el estado y la vista
                actualizarEstadoYVista(holder, nuevoEstado);

                // Cierra el diálogo después de la respuesta exitosa
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
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


}

