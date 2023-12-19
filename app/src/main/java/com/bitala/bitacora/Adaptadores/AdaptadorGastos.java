package com.bitala.bitacora.Adaptadores;


import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.Utils;
import com.bitala.bitacora.R;
import com.itextpdf.text.pdf.parser.Line;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AdaptadorGastos extends RecyclerView.Adapter<AdaptadorGastos.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;
    AlertDialog.Builder builder;

    String url;
    List<JSONObject> listaDesgloseGastos = new ArrayList<>();
    AdaptadorDesgloseGastos adaptadorDesgloseGastos;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gastos, parent, false);
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
            String saldo_inicial = jsonObject2.optString("saldo_inicial", "");
            String ID_saldo = jsonObject2.optString("ID_saldo", "");
            String total_gastos = jsonObject2.optString("total_gastos", "");
            String nuevo_saldo = jsonObject2.optString("nuevo_saldo", "");
            String gastos = jsonObject2.optString("gastos", "");
            String fecha_asignacion = jsonObject2.optString("fecha_asignacion", "");
            String status_saldo = jsonObject2.optString("status_saldo", "");
            String total_depositos = jsonObject2.optString("total_depositos", "");


            //      holder.fecha.setText("Asignado el: " + fecha_asignacion);

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = inputFormat.parse(fecha_asignacion);
                SimpleDateFormat outputDayOfWeek = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
                String dayOfWeek = outputDayOfWeek.format(date);
                SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
                String formattedDate = outputFormat.format(date);

                holder.fecha.setText("Asignado el: " + dayOfWeek.toLowerCase() + " " + formattedDate.toLowerCase());

            } catch (ParseException e) {
                e.printStackTrace();
                holder.fecha.setText("No se encontro la fecha");
            }


            holder.saldo_Asign.setText("Saldo Asignado: " + saldo_inicial + "$");

            holder.saldo_restante.setText("Saldo actual: " + nuevo_saldo + "$");

            // holder.status_saldo.setText("Estatus de saldo: " + status_saldo);


            Drawable drawable;
            int colorIcono;
            if (status_saldo.equalsIgnoreCase("Finalizado")) {
                colorIcono = ContextCompat.getColor(context, R.color.black);
                drawable = ContextCompat.getDrawable(context, R.drawable.rounded_grisesito);

                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date date = inputFormat.parse(fecha_asignacion);
                    SimpleDateFormat outputDayOfWeek = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
                    String dayOfWeek = outputDayOfWeek.format(date);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
                    String formattedDate = outputFormat.format(date);

                    holder.fechaFin.setText("Fin: " + dayOfWeek.toLowerCase() + " " + formattedDate.toLowerCase());
                    holder.status_saldo.setText("FINALIZADO");
                    holder.fechaFin.setVisibility(View.VISIBLE);

                } catch (ParseException e) {
                    e.printStackTrace();
                    holder.status_saldo.setText("FINALIZADO");
                    holder.fechaFin.setVisibility(View.GONE);
                }

            } else {
                holder.status_saldo.setText(status_saldo.toUpperCase());
                holder.fechaFin.setVisibility(View.GONE);
                colorIcono = ContextCompat.getColor(context, R.color.verde);
                drawable = ContextCompat.getDrawable(context, R.drawable.rounded_verdecito);
            }

            holder.status_saldo.setTextColor(colorIcono);
            holder.ContenedorSaldo.setBackground(drawable);

            if (total_gastos.isEmpty() || total_gastos.equals("0")) {
                holder.totalGastado.setVisibility(View.GONE);
            } else {
                holder.totalGastado.setVisibility(View.VISIBLE);
                holder.totalGastado.setText("Saldo gastado: -" + total_gastos + "$");
            }

            if (total_depositos.isEmpty() || total_depositos.equals("0")) {
                holder.depositos.setVisibility(View.GONE);
            }else{
                holder.depositos.setVisibility(View.VISIBLE);
                holder.depositos.setText("Total depositos: +" + total_depositos + "$");
            }


            adaptadorDesgloseGastos = new AdaptadorDesgloseGastos(listaDesgloseGastos, context);
            holder.recyclerViewDesgloseGastos.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerViewDesgloseGastos.setAdapter(adaptadorDesgloseGastos);

            listaDesgloseGastos.clear();
            try {
                JSONArray jsonArray = new JSONArray(gastos);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    listaDesgloseGastos.add(jsonObject);
                }

                adaptadorDesgloseGastos.notifyDataSetChanged();
                adaptadorDesgloseGastos.setFilteredData(listaDesgloseGastos);
                adaptadorDesgloseGastos.filter("");

                if (listaDesgloseGastos.size() > 0) {

                    holder.SaldosGastados.setVisibility(View.VISIBLE);
                    holder.LayoutGastos.setVisibility(View.VISIBLE);
                } else {
                    holder.LayoutGastos.setVisibility(View.GONE);
                    holder.SaldosGastados.setVisibility(View.GONE);

                }


            } catch (JSONException e) {
                holder.LayoutGastos.setVisibility(View.GONE);
                holder.SaldosGastados.setVisibility(View.GONE);

            }


            holder.IconoOcultarMostrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.recyclerViewDesgloseGastos.getVisibility() == View.VISIBLE) {
                        holder.recyclerViewDesgloseGastos.setVisibility(View.GONE);
                        holder.IconoOcultarMostrar.setImageResource(R.drawable.mostrar);
                    } else {
                        holder.recyclerViewDesgloseGastos.setVisibility(View.VISIBLE);
                        holder.IconoOcultarMostrar.setImageResource(R.drawable.ocultar);
                    }
                }
            });


        } finally {
        }
    }


    @Override
    public int getItemCount() {

        return filteredData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fecha;
        TextView fechaFin;

        TextView saldo_Asign;
        TextView saldo_restante;
        TextView status_saldo;
        TextView SaldosGastados;

        TextView totalGastado;

        RecyclerView recyclerViewDesgloseGastos;

        ConstraintLayout ContenedorSaldo;

        ImageView IconoOcultarMostrar;


        LinearLayout LayoutGastos;

        TextView depositos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            SaldosGastados = itemView.findViewById(R.id.SaldosGastados);
            status_saldo = itemView.findViewById(R.id.status_saldo);
            saldo_restante = itemView.findViewById(R.id.saldo_restante);
            saldo_Asign = itemView.findViewById(R.id.saldo_Asign);
            fecha = itemView.findViewById(R.id.fecha);
            recyclerViewDesgloseGastos = itemView.findViewById(R.id.recyclerViewDesgloseGastos);
            totalGastado = itemView.findViewById(R.id.totalGastado);
            fechaFin = itemView.findViewById(R.id.fechaFin);
            ContenedorSaldo = itemView.findViewById(R.id.ContenedorSaldo);
            IconoOcultarMostrar = itemView.findViewById(R.id.IconoOcultarMostrar);
            LayoutGastos = itemView.findViewById(R.id.LayoutGastos);

            depositos = itemView.findViewById(R.id.depositos);
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


    public AdaptadorGastos(List<JSONObject> data, Context context) {

        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        url = context.getResources().getString(R.string.urlApi);

    }


}

