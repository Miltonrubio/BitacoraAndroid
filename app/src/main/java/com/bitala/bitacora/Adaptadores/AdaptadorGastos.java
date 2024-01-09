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
    List<JSONObject> listaDepositos = new ArrayList<>();
    AdaptadorDepositos adaptadorDepositos;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gastos_nuevo, parent, false);
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
            Double saldo_inicial = jsonObject2.optDouble("saldo_inicial", 0);
            String ID_saldo = jsonObject2.optString("ID_saldo", "");
            Double total_dinero_gastado = jsonObject2.optDouble("total_dinero_gastado", 0);
            Double nuevo_saldo = jsonObject2.optDouble("nuevo_saldo", 0);
            String gastos = jsonObject2.optString("gastos", "");
            String fecha_asignacion = jsonObject2.optString("fecha_asignacion", "");
            String status_saldo = jsonObject2.optString("status_saldo", "");
            Double total_dinero_agregado = jsonObject2.optDouble("total_dinero_agregado", 0);
            String caja = jsonObject2.optString("caja", "");
            String depositos = jsonObject2.optString("depositos", "");


            Double depositos_Cajagastos = jsonObject2.optDouble("depositos_Cajagastos", 0);
            Double depositos_CajaCapital = jsonObject2.optDouble("depositos_CajaCapital", 0);
            Double gastos_Cajagastos = jsonObject2.optDouble("gastos_Cajagastos", 0);
            Double gastos_CajaCapital = jsonObject2.optDouble("gastos_CajaCapital", 0);


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


            holder.saldo_Asign.setText("Saldo Inicial: " + saldo_inicial + " $");

            holder.saldo_restante.setText("Saldo restante: " + nuevo_saldo + " $");

            holder.tvCaja.setText("Caja: " + caja);

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

            if ( /* total_dinero_gastado.isEmpty() || */ total_dinero_gastado.equals("0")) {
                holder.totalGastado.setVisibility(View.GONE);
            } else {
                holder.totalGastado.setVisibility(View.VISIBLE);
                holder.totalGastado.setText("Saldo gastado: -" + total_dinero_gastado + " $");
            }

            if (/* total_dinero_agregado.isEmpty() ||*/ total_dinero_agregado.equals("0")) {
                holder.depositos.setVisibility(View.GONE);
            } else {
                holder.depositos.setVisibility(View.VISIBLE);
                holder.depositos.setText("Saldo agregado: +" + total_dinero_agregado + " $");
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


            // Aqui ira la logica pa los depositos


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


            adaptadorDepositos = new AdaptadorDepositos(listaDepositos, context);

            holder.recyclerViewDesgloseDepositos.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerViewDesgloseDepositos.setAdapter(adaptadorDepositos);

            listaDepositos.clear();

            try {
                JSONArray jsonArray = new JSONArray(depositos);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    listaDepositos.add(jsonObject);
                }

                adaptadorDepositos.notifyDataSetChanged();
                adaptadorDepositos.setFilteredData(listaDepositos);
                adaptadorDepositos.filter("");


                if (listaDepositos.size() > 0) {

                    holder.linearLayoutDepositos.setVisibility(View.VISIBLE);
                    holder.LayoutDepositos.setVisibility(View.VISIBLE);
                } else {
                    holder.linearLayoutDepositos.setVisibility(View.GONE);
                    holder.LayoutDepositos.setVisibility(View.GONE);

                }

            } catch (JSONException e) {

                holder.linearLayoutDepositos.setVisibility(View.GONE);
                holder.LayoutDepositos.setVisibility(View.GONE);

            }


            holder.IconoOcultarMostrarDepositos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.recyclerViewDesgloseDepositos.getVisibility() == View.VISIBLE) {
                        holder.recyclerViewDesgloseDepositos.setVisibility(View.GONE);
                        holder.IconoOcultarMostrarDepositos.setImageResource(R.drawable.mostrar);
                    } else {
                        holder.recyclerViewDesgloseDepositos.setVisibility(View.VISIBLE);
                        holder.IconoOcultarMostrarDepositos.setImageResource(R.drawable.ocultar);
                    }
                }
            });


            holder.ContenedorSaldo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.modal_desglose_cajas, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                    AlertDialog dialogOpcionesUsuarios = builder.create();
                    ColorDrawable back = new ColorDrawable(Color.BLACK);
                    back.setAlpha(150);
                    dialogOpcionesUsuarios.getWindow().setBackgroundDrawable(back);
                    dialogOpcionesUsuarios.getWindow().setDimAmount(0.8f);
                    dialogOpcionesUsuarios.show();


                    TextView saldo_asignado = customView.findViewById(R.id.saldo_asignado);
                    TextView saldoAgregado = customView.findViewById(R.id.saldoAgregado);
                    TextView saldoGastadoCapital = customView.findViewById(R.id.saldoGastadoCapital);
                    TextView saldototalCapital = customView.findViewById(R.id.saldototalCapital);


                    TextView saldo_asignado_Gastos = customView.findViewById(R.id.saldo_asignado_Gastos);
                    TextView saldoAgregadoGastos = customView.findViewById(R.id.saldoAgregadoGastos);
                    TextView saldoGastadoGastos = customView.findViewById(R.id.saldoGastadoGastos);
                    TextView saldototalGastos = customView.findViewById(R.id.saldototalGastos);

                    TextView saldo_restanteCapital = customView.findViewById(R.id.saldo_restanteCapital);
                    TextView saldo_restanteGastos = customView.findViewById(R.id.saldo_restanteGastos);


                    Double sumaCapital = 0.0;
                    Double sumaGastos = 0.0;

                    if (caja.equalsIgnoreCase("Gastos")) {

                        saldo_asignado.setVisibility(View.GONE);
                        saldo_asignado_Gastos.setVisibility(View.VISIBLE);
                        sumaGastos = depositos_Cajagastos + saldo_inicial;
                        sumaCapital = depositos_CajaCapital;

                    } else {
                        saldo_asignado.setVisibility(View.VISIBLE);
                        saldo_asignado_Gastos.setVisibility(View.GONE);
                        sumaGastos = depositos_Cajagastos;
                        sumaCapital = depositos_CajaCapital + saldo_inicial;
                    }


                    saldo_asignado.setText("Saldo inicial: " + saldo_inicial + " $");
                    saldoAgregado.setText("Saldo agregado: +" + depositos_CajaCapital + " $");
                    saldoGastadoCapital.setText("Saldo gastado: -" + gastos_CajaCapital + " $");


                    saldototalCapital.setText("Total asignado Capital: " + sumaCapital + " $");

                    saldototalGastos.setText("Total asignado Gastos: " + sumaGastos + " $");
                    saldo_asignado_Gastos.setText("Saldo inicial: " + saldo_inicial + " $");
                    saldoAgregadoGastos.setText("Saldo agregado: +" + depositos_Cajagastos + " $");
                    saldoGastadoGastos.setText("Saldo gastado: -" + gastos_Cajagastos + " $");


                    Double restanteGastos = sumaGastos - gastos_Cajagastos;


                    Double restanteCapital = sumaCapital - gastos_CajaCapital;

                    saldo_restanteGastos.setText("Saldo restante: "+ restanteGastos +" $");
                    saldo_restanteCapital.setText("Saldo restante: "+ restanteCapital+" $");
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
        TextView tvCaja;


        LinearLayout linearLayoutDepositos;
        LinearLayout LayoutDepositos;

        TextView SaldoDepositos;

        ImageView IconoOcultarMostrarDepositos;
        RecyclerView recyclerViewDesgloseDepositos;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCaja = itemView.findViewById(R.id.tvCaja);

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
            linearLayoutDepositos = itemView.findViewById(R.id.linearLayoutDepositos);
            depositos = itemView.findViewById(R.id.depositos);


            LayoutDepositos = itemView.findViewById(R.id.LayoutDepositos);
            SaldoDepositos = itemView.findViewById(R.id.SaldoDepositos);
            IconoOcultarMostrarDepositos = itemView.findViewById(R.id.IconoOcultarMostrarDepositos);
            recyclerViewDesgloseDepositos = itemView.findViewById(R.id.recyclerViewDesgloseDepositos);

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


    public AdaptadorGastos(List<JSONObject> data, Context context) {

        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        url = context.getResources().getString(R.string.urlApi);

    }


}

