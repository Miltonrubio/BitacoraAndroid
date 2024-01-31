package com.bitala.bitacora.Adaptadores;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
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


public class AdaptadorMostrarSaldoParaUsuarios extends RecyclerView.Adapter<AdaptadorMostrarSaldoParaUsuarios.ViewHolder> {

    private Context context;

    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    String url;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mostrar_saldos_para_usuarios, parent, false);

        return new ViewHolder(view);
    }


    String claveSesionIniciada;

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            JSONObject jsonObject2 = filteredData.get(position);

            String ID_saldo = jsonObject2.optString("ID_saldo", "");
            String tipo_caja = jsonObject2.optString("tipo_caja");
            String saldo_restante = jsonObject2.optString("saldo_restante", "");


            holder.textCaja.setText("SALDO DE " + tipo_caja.toUpperCase());

            holder.textSaldo.setText(saldo_restante + " $");

            Log.d("JSON Object", jsonObject2.toString());
/*
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
            holder.recyclerViewSaldosActivos.setLayoutManager(gridLayoutManager);
            adaptadorMostrarNuevosSaldos = new AdaptadorMostrarNuevosSaldos(listaDesgloseSaldos, context);
            holder.recyclerViewSaldosActivos.setAdapter(adaptadorMostrarNuevosSaldos);


            listaDesgloseSaldos.clear();
            try {
                JSONArray jsonArray = new JSONArray();
                if (desglose_saldo_por_caja != null) {
                    jsonArray = new JSONArray(desglose_saldo_por_caja);
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    listaDesgloseSaldos.add(jsonObject);

                }

                adaptadorMostrarNuevosSaldos.notifyDataSetChanged();
                adaptadorMostrarNuevosSaldos.setFilteredData(listaDesgloseSaldos);
                adaptadorMostrarNuevosSaldos.filter("");

                if (listaDesgloseSaldos.size() > 0) {

                    holder.recyclerViewSaldosActivos.setVisibility(View.VISIBLE);
                } else {
                    holder.recyclerViewSaldosActivos.setVisibility(View.INVISIBLE);

                }

            }catch (JSONException e) {
                Log.e("AdaptadorMostrarSaldo", "Error al procesar JSON: " + e.getMessage());
                holder.recyclerViewSaldosActivos.setVisibility(View.INVISIBLE);
            }

*/

        } finally {

        }
    }


    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCaja;
        TextView textSaldo;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCaja = itemView.findViewById(R.id.textCaja);
            textSaldo = itemView.findViewById(R.id.textSaldo);
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
        notifyDataSetChanged();
    }


    public void setFilteredData(List<JSONObject> filteredData) {
        this.filteredData = new ArrayList<>(filteredData);
        notifyDataSetChanged();
    }


    public AdaptadorMostrarSaldoParaUsuarios(List<JSONObject> data, Context context) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        url = context.getResources().getString(R.string.urlApi);

        SharedPreferences sharedPreferences = context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        this.claveSesionIniciada = sharedPreferences.getString("clave", "");
        //   this.ID_usuarioActual = sharedPreferences.getString("ID_usuario", "");

    }

}

