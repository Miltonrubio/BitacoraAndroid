package com.bitala.bitacora.Adaptadores;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdaptadorMostrarNuevosSaldos extends RecyclerView.Adapter<AdaptadorMostrarNuevosSaldos.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    String url;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nuevo_saldo_usuarios, parent, false);
        return new ViewHolder(view);
    }

    public void setFilteredData(List<JSONObject> filteredData) {
        this.filteredData = new ArrayList<>(filteredData);
        notifyDataSetChanged();
    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            JSONObject jsonObject2 = filteredData.get(position);
            Double saldo_asignado = jsonObject2.optDouble("saldo_asignado", 0);
            String ID_saldo = jsonObject2.optString("ID_saldo", "");
            String tipo_caja = jsonObject2.optString("tipo_caja", "");
            String fecha_asignacion_saldo = jsonObject2.optString("fecha_asignacion_saldo", "");
            String hora_asignacion_saldo = jsonObject2.optString("hora_asignacion_saldo", "");
            String status_saldo = jsonObject2.optString("status_saldo", "");
            String ID_registro_saldo = jsonObject2.optString("ID_registro_saldo", "");
            String saldo_restante = jsonObject2.optString("saldo_restante", "");
            String total_consumos = jsonObject2.optString("total_consumos", "");
            String total_adiciones = jsonObject2.optString("total_adiciones", "");

            holder.textViewDineroRestante.setText(saldo_restante + " $");

            holder.textViewCaja.setText(tipo_caja.toUpperCase());




        } finally {
        }
    }


    @Override
    public int getItemCount() {

        return filteredData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewCaja;
        TextView textViewDineroRestante;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewCaja = itemView.findViewById(R.id.textViewCaja);
            textViewDineroRestante = itemView.findViewById(R.id.textViewDineroRestante);

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


    public AdaptadorMostrarNuevosSaldos(List<JSONObject> data, Context context) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        url = context.getResources().getString(R.string.urlApi);

    }


}

