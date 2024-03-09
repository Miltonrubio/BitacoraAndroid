package com.bitala.bitacora.Adaptadores;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdaptadorCorregirListaActividades extends RecyclerView.Adapter<AdaptadorCorregirListaActividades.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;
    AlertDialog.Builder builder;

    String url;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nombre_actividades, parent, false);
        return new ViewHolder(view);

    }


    public interface OnItemClickListener {
        void onItemClick(String ID_nombre_actividad, String nombre_actividad);
    }


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public void setFilteredData(List<JSONObject> filteredData) {
        this.filteredData = new ArrayList<>(filteredData);
        notifyDataSetChanged();
    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.FrameActividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    String ID_nombre_actividad = filteredData.get(position).optString("ID_nombre_actividad", "");
                    String nombre_actividad = filteredData.get(position).optString("nombre_actividad", "");
                    onItemClickListener.onItemClick(ID_nombre_actividad, nombre_actividad);
                }
            }
        });


        try {
            JSONObject jsonObject2 = filteredData.get(position);
            String ID_nombre_actividad = jsonObject2.optString("ID_nombre_actividad", "");
            String nombre_actividad = jsonObject2.optString("nombre_actividad", "");
            String tipo_actividad = jsonObject2.optString("tipo_actividad", "");

            setTextViewText(holder.TextNombreDeActividad, nombre_actividad.toUpperCase(), "No se encontro esta actividad");


            int colorRes;
            Drawable drawable;

            if (tipo_actividad.equalsIgnoreCase("OFICINAS") || tipo_actividad.equalsIgnoreCase("OCULTA")) {
                drawable = ContextCompat.getDrawable(context, R.drawable.redondeadoconbordevino);
                colorRes = R.color.vino;
            } else {

                drawable = ContextCompat.getDrawable(context, R.drawable.roundedbackground_nombre_actividad);
                colorRes = R.color.naranjita;
            }

            holder.FrameActividades.setBackground(drawable);
            int color = ContextCompat.getColor(context, colorRes);
            holder.TextNombreDeActividad.setTextColor(color);


        } finally {
        }
    }


    @Override
    public int getItemCount() {

        return filteredData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView TextNombreDeActividad;

        FrameLayout FrameActividades;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TextNombreDeActividad = itemView.findViewById(R.id.TextNombreDeActividad);
            FrameActividades = itemView.findViewById(R.id.FrameActividades);
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
                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(nombre_actividad.contains(keyword) || ID_actividad.contains(keyword))) {
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


    private void setTextViewText(TextView textView, String text, String defaultText) {
        if (text.equals(null) || text.equals("") || text.equals(":null") || text.equals("null") || text.isEmpty()) {
            textView.setText(defaultText);
        } else {
            textView.setText(text);
        }
    }


    public AdaptadorCorregirListaActividades(List<JSONObject> data, Context context) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);

        url = context.getResources().getString(R.string.urlApi);
    }


}

