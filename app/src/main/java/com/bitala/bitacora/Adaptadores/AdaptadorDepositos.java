package com.bitala.bitacora.Adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.R;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdaptadorDepositos extends RecyclerView.Adapter<AdaptadorDepositos.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    String url;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_desglose_depositos, parent, false);
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

/*
            String ID_deposito = jsonObject2.optString("ID_deposito", "");
            String dinero_agregado = jsonObject2.optString("dinero_agregado", "");
            String fecha = jsonObject2.optString("fecha", "");
            String hora = jsonObject2.optString("hora", "");
            String ID_saldo = jsonObject2.optString("ID_saldo", "");
            String tipo_caja = jsonObject2.optString("tipo_caja", "");
            String nombre_admin_asig = jsonObject2.optString("nombre_admin_asig", "");
*/

            String ID_adicion = jsonObject2.optString("ID_adicion", "");
            String saldo_agregado = jsonObject2.optString("saldo_agregado", "");
            String fecha = jsonObject2.optString("fecha", "");
            String hora = jsonObject2.optString("hora", "");
            String nombre_admin_asig = jsonObject2.optString("nombre_admin_asig", "");


            Bundle bundle = new Bundle();
            bundle.putString("ID_deposito", ID_adicion);


            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = inputFormat.parse(fecha);
                SimpleDateFormat outputDayOfWeek = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
                String dayOfWeek = outputDayOfWeek.format(date);
                SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
                String formattedDate = outputFormat.format(date);

                // holder.fecha.setText(dayOfWeek.toLowerCase() + " " + formattedDate.toLowerCase());
                String horaFormateada = formatearHora(hora);
                holder.fecha.setText(formattedDate.toLowerCase() + " a las " + horaFormateada);
            } catch (ParseException e) {
                holder.fecha.setText("No se encontro la fecha");
            }

            //     holder.tipoCaja.setText("Caja: " + tipo_caja);
            holder.tipoCaja.setVisibility(View.GONE);

            holder.saldo_agregado.setText("Se agregó: + " + saldo_agregado + " $ de saldo");

            holder.adminAsign.setText("Asignado por: " + nombre_admin_asig);

        } finally {
        }
    }

    private String formatearHora(String horaDesdeAPI) {
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat formatoSalida = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            // Parsea la hora desde la cadena de texto de la API
            Date hora = formatoEntrada.parse(horaDesdeAPI);

            // Formatea la hora al nuevo formato
            return formatoSalida.format(hora);
        } catch (ParseException e) {
            e.printStackTrace();
            // Maneja la excepción si ocurre un error al parsear la hora
            return horaDesdeAPI; // O podrías devolver un valor por defecto
        }
    }

    @Override
    public int getItemCount() {

        return filteredData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView saldo_agregado;

        TextView fecha;

        TextView tipoCaja;

        TextView adminAsign;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            saldo_agregado = itemView.findViewById(R.id.saldo_agregado);
            fecha = itemView.findViewById(R.id.fecha);
            adminAsign = itemView.findViewById(R.id.adminAsign);
            tipoCaja = itemView.findViewById(R.id.tipoCaja);
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


    public AdaptadorDepositos(List<JSONObject> data, Context context) {

        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        url = context.getResources().getString(R.string.urlApi);

    }


}

