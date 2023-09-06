package com.example.bitacora;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdaptadorActividades extends RecyclerView.Adapter<AdaptadorActividades.ViewHolder> {


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
                String nombreActividad = jsonObject2.optString("nombreActividad", "");
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
                bundle.putString("nombreActividad", nombreActividad);
                bundle.putString("descripcionActividad", descripcionActividad);
                bundle.putString("permisos", permisos);
                bundle.putString("nombre", nombre);
                bundle.putString("correo", correo);
                bundle.putString("telefono", telefono);

                setTextViewText(holder.textNombreUsuario, nombre, "Nombre no disponible");

                setTextViewText(holder.textActividad, nombreActividad, "Actividad no disponible");
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date_inicio = inputFormat.parse(fecha_inicio);

                    SimpleDateFormat outputFormatFecha = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new DateFormatSymbols(new Locale("es", "ES")));
                    String fecha_formateada = outputFormatFecha.format(date_inicio);


                    setTextViewText(holder.textFechaActividad, fecha_formateada, "Fecha no disponible");


                } catch (Exception e) {
                    e.printStackTrace();
                }

                setTextViewText(holder.textStatus, estadoActividad.toUpperCase(), "Estado no disponible");
                int colorVerde = ContextCompat.getColor(context, R.color.verde);

               int fondoPersonalizado = R.drawable.roundedbackgroundgris;
                if (estadoActividad.equals("pendiente")) {
                  int colorAmarillo = ContextCompat.getColor(context, R.color.amarillo);
                 holder.textStatus.setTextColor(colorAmarillo);
                } else if (estadoActividad.equals("iniciada")) {
                  int colorNegro = ContextCompat.getColor(context, R.color.black);
                   holder.textStatus.setTextColor(colorNegro);
                } else if (estadoActividad.equals("finalizado")) {
                    holder.textStatus.setTextColor(colorVerde);
                } else if (estadoActividad.equals("cancelado")) {
                  int colorRojo = ContextCompat.getColor(context, R.color.rojo);
                   holder.textStatus.setTextColor(colorRojo);
                   holder.FrameActividades.setBackgroundResource(fondoPersonalizado);
                } else {
                  int colorRojo = ContextCompat.getColor(context, R.color.rojo);
                  holder.textStatus.setTextColor(colorRojo);
                 holder.FrameActividades.setBackgroundResource(fondoPersonalizado);
                }

                setTextViewText(holder.textTelefonoUsuario, telefono, "Telefono no disponible");


                setTextViewText(holder.textDetallesActividad, descripcionActividad, "Actividad no disponible");

                /*
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DetallesArrastres detallesArrastres = new DetallesArrastres();
                        detallesArrastres.setArguments(bundle);

                        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.frame_layoutCoches, detallesArrastres)
                                .addToBackStack(null)
                                .commit();

                    }
                });
*/

            } finally {

            }
        } else if (getItemViewType(position) == VIEW_TYPE_ERROR && filteredData.isEmpty()) {
/*
            if (filteredData.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.noarrastres)
                        .into(holder.IVNoInternet);
            }

 */
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
        TextView textFechaActividad, textStatus, textTelefonoUsuario, textNombreUsuario, textActividad, textDetallesActividad;
        FrameLayout FrameActividades;

        ImageView IMNoInternet;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textFechaActividad = itemView.findViewById(R.id.textFechaActividad);
            textStatus = itemView.findViewById(R.id.textStatus);
            IMNoInternet = itemView.findViewById(R.id.IMNoInternet);
            textTelefonoUsuario = itemView.findViewById(R.id.textTelefonoUsuario);
            textNombreUsuario = itemView.findViewById(R.id.textNombreUsuario);
            textActividad = itemView.findViewById(R.id.textActividad);
            FrameActividades= itemView.findViewById(R.id.FrameActividades);
            textDetallesActividad= itemView.findViewById(R.id.textDetallesActividad);
        }
    }

    public void filter(String query) {
        filteredData.clear();

        if (TextUtils.isEmpty(query)) {
            filteredData.addAll(data);
        } else {
            String[] keywords = query.toLowerCase().split(" ");

            for (JSONObject item : data) {
                String nombre = item.optString("nombre", "").toLowerCase();
                String empresa = item.optString("empresa", "").toLowerCase();
                String telefono = item.optString("telefono", "").toLowerCase();
                String estatus = item.optString("estatus", "").toLowerCase();
                String placas = item.optString("placas", "").toLowerCase();
                String modelo = item.optString("modelo", "").toLowerCase();

                String direccion = item.optString("direccion", "").toLowerCase();
                String id = item.optString("id", "").toLowerCase();


                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(modelo.contains(keyword) || empresa.contains(keyword) || direccion.contains(keyword) || telefono.contains(keyword) || id.contains(keyword) || placas.contains(keyword) ||
                            nombre.contains(keyword) || estatus.contains(keyword))) {
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

