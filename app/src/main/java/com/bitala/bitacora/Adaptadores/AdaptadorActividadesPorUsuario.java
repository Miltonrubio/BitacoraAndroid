package com.bitala.bitacora.Adaptadores;


import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.DetallesActividadesFragment;
import com.bitala.bitacora.R;

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

    public AdaptadorActividadesPorUsuario(List<JSONObject> data, Context context) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);

        url = context.getResources().getString(R.string.urlApi);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividades, parent, false);
        return new ViewHolder(view);

    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        int drawableResId = 0;
        int colorIcono = 0;

        SharedPreferences sharedPreferences = context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        String permisosUsuario = sharedPreferences.getString("permisos", "");

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
            bundle.putString("motivocancelacion", motivocancelacion);


            setTextViewText(holder.textActividad, nombre_actividad.toUpperCase(), "Actividad no disponible");
            SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat formatoDeseado = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'a las' HH:mm", Locale.getDefault());

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
                holder.textStatus.setTextColor(ContextCompat.getColor(context, R.color.rojo));
                holder.textMotivoCancelacion.setVisibility(View.VISIBLE);
                holder.FrameActividades.setBackgroundResource(R.drawable.roundedbackgroundgris);
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

        ImageView ImagenDeEstado;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

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

/*
    private void VerNombresActividades(Context context) {
        nombresActividades.clear();
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String ID_nombre_actividad = jsonObject.getString("ID_nombre_actividad");
                        String nombre_actividad = jsonObject.getString("nombre_actividad");
                        nombresActividades.add(ID_nombre_actividad + ": " + nombre_actividad);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Hubo un error", Toast.LENGTH_SHORT).show();
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

