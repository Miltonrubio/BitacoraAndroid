package com.bitala.bitacora.Adaptadores;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdaptadorRegistrosSaldos extends RecyclerView.Adapter<AdaptadorRegistrosSaldos.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    String url;

    AdaptadorNuevosSaldos adaptadorNuevosSaldos;
    List<JSONObject> listaDesgloseDeSaldos = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registros_saldos, parent, false);
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


            String fecha_asignacion = jsonObject2.optString("fecha_asignacion", "");
            String ID_registro_saldo = jsonObject2.optString("ID_registro_saldo", "");
            String hora_asignacion = jsonObject2.optString("hora_asignacion", "");
            String status = jsonObject2.optString("status", "");
            String desglose_saldo_por_caja = jsonObject2.optString("desglose_saldo_por_caja", "");


            holder.textView12.setText("SALDO ID #" + ID_registro_saldo);
            holder.textViewFecha.setText("Asignado el " + fecha_asignacion);


            adaptadorNuevosSaldos = new AdaptadorNuevosSaldos(listaDesgloseDeSaldos, context);

            holder.recyclerViewSaldosActivos.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerViewSaldosActivos.setAdapter(adaptadorNuevosSaldos);
            listaDesgloseDeSaldos.clear();
            try {
                JSONArray jsonArray = new JSONArray(desglose_saldo_por_caja);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    listaDesgloseDeSaldos.add(jsonObject);
                }

                if (listaDesgloseDeSaldos.size() > 0) {
                    holder.recyclerViewSaldosActivos.setVisibility(View.VISIBLE);
                } else {
                    holder.recyclerViewSaldosActivos.setVisibility(View.GONE);
                }

                adaptadorNuevosSaldos.notifyDataSetChanged();
                adaptadorNuevosSaldos.setFilteredData(listaDesgloseDeSaldos);
                adaptadorNuevosSaldos.filter("");

            } catch (JSONException e) {

                holder.recyclerViewSaldosActivos.setVisibility(View.GONE);
            }


        } finally {
        }
    }


    @Override
    public int getItemCount() {

        return filteredData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewFecha;
        RecyclerView recyclerViewSaldosActivos;

        TextView textView12;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textView12 = itemView.findViewById(R.id.textView12);
            recyclerViewSaldosActivos = itemView.findViewById(R.id.recyclerViewSaldosActivos);
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

    String claveSesionIniciada;
    String ID_usuarioSesionIniciada;


    public AdaptadorRegistrosSaldos(List<JSONObject> data, Context context /* , AdaptadorNuevosSaldos.OnActivityActionListener actionListener */) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        url = context.getResources().getString(R.string.urlApi);
        //  this.actionListener = actionListener;

        SharedPreferences sharedPreferences = context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        this.ID_usuarioSesionIniciada = sharedPreferences.getString("ID_usuario", "");
        this.claveSesionIniciada = sharedPreferences.getString("clave", "");

    }


}

