package com.bitala.bitacora.Adaptadores;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.R;
import com.bitala.bitacora.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AdaptadorNuevosSaldos extends RecyclerView.Adapter<AdaptadorNuevosSaldos.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    String url;


    DesgloseConsumos adaptadorDesgloseConsumos;
    List<JSONObject> listaGastos = new ArrayList<>();
    AdaptadorDepositos adaptadorDepositos;
    List<JSONObject> listaAdiciones = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nuevos_saldos, parent, false);
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


            String saldo_asignado = jsonObject2.optString("saldo_asignado", "");
            String ID_saldo = jsonObject2.optString("ID_saldo", "");
            String tipo_caja = jsonObject2.optString("tipo_caja", "");
            String saldo_restante = jsonObject2.optString("saldo_restante", "");
            String total_adiciones = jsonObject2.optString("total_adiciones", "");
            String total_consumos = jsonObject2.optString("total_consumos", "");
            String fecha_asignacion_saldo = jsonObject2.optString("fecha_asignacion_saldo", "");
            String hora_asignacion_saldo = jsonObject2.optString("hora_asignacion_saldo", "");

            String consumos = jsonObject2.optString("consumos", "");
            String adiciones = jsonObject2.optString("adiciones", "");
            String ID_registro_saldo = jsonObject2.optString("ID_registro_saldo", "");
            String status_saldo = jsonObject2.optString("status_saldo", "");
            String fecha_fin = jsonObject2.optString("fecha_fin", "");
            String hora_fin = jsonObject2.optString("hora_fin", "");


            holder.TextViewStatus.setText(status_saldo.toUpperCase());

            if (status_saldo.equalsIgnoreCase("Activo")) {

                holder.TextViewStatus.setTextColor(ContextCompat.getColor(context, R.color.verde));
            } else {
                holder.TextViewStatus.setTextColor(ContextCompat.getColor(context, R.color.vino));

            }


            if (total_consumos.equalsIgnoreCase("0")) {
                holder.TextViewtotal_consumos.setVisibility(View.GONE);
            } else {

                holder.TextViewtotal_consumos.setVisibility(View.VISIBLE);
            }

            if (total_adiciones.equalsIgnoreCase("0")) {
                holder.TextViewtotal_adiciones.setVisibility(View.GONE);
            } else {

                holder.TextViewtotal_adiciones.setVisibility(View.VISIBLE);
            }


            if (tipo_caja.equalsIgnoreCase("Capital")) {

                holder.ContenedorSaldoActivo.setBackgroundResource(R.drawable.rounded_amarillo);
                holder.TextViewtipo_caja.setTextColor(ContextCompat.getColor(context, R.color.amarillo));
            } else {

                holder.ContenedorSaldoActivo.setBackgroundResource(R.drawable.roundedbackground_nombre_actividad);
                holder.TextViewtipo_caja.setTextColor(ContextCompat.getColor(context, R.color.naranjita));
            }


            try {
                double saldoAsignadoDouble = Double.parseDouble(saldo_asignado);
                double totalAdicionesDouble = Double.parseDouble(total_adiciones);

                double resultado = saldoAsignadoDouble + totalAdicionesDouble;

                String resultadoComoCadena = String.valueOf(resultado);

                holder.TextViewsaldo_asignado.setText("Saldo total: " + resultadoComoCadena + " $  \nSaldo inicial: " + saldo_asignado + "$");

            } catch (NumberFormatException e) {
                holder.TextViewsaldo_asignado.setText("Saldo inicial: " + saldo_asignado + " $");

            }


            holder.TextViewtotal_adiciones.setText("Total agregado: +" + total_adiciones + " $");
            holder.TextViewtotal_consumos.setText("Total gastado: -" + total_consumos + " $");
            holder.TextViewsaldo_restante.setText("Saldo restante: " + saldo_restante + " $");

            holder.TextViewtipo_caja.setText(tipo_caja.toUpperCase());


            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdfOutput = new SimpleDateFormat("EEEE dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            SimpleDateFormat sdfInputHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            try {
                Date dateInicio = sdfInput.parse(fecha_asignacion_saldo);

                Date horaInicio = sdfInputHora.parse(hora_asignacion_saldo);
                Date dateTime = new Date(dateInicio.getTime() + horaInicio.getTime());
                String fechaFormateada = sdfOutput.format(dateTime);

                holder.FechaAsignacion.setText("Asignado el " + fechaFormateada);

            } catch (ParseException e) {
                holder.FechaAsignacion.setText("No se encontro la fecha");
            }



            if (status_saldo.equalsIgnoreCase("Finalizado")){
                holder.FechaFinalizacion.setVisibility(View.VISIBLE);
            }else {
                holder.FechaFinalizacion.setVisibility(View.GONE);
            }

            try {
                Date fechaFin = sdfInput.parse(fecha_fin);
                Date horaFin = sdfInputHora.parse(hora_fin);
                Date dateTime = new Date(fechaFin.getTime() + horaFin.getTime());
                String fechaFormateada = sdfOutput.format(dateTime);

                holder.FechaFinalizacion.setText("Finalizado el " + fechaFormateada);

            } catch (ParseException e) {
                holder.FechaFinalizacion.setText("No se encontro la fecha");
            }




            holder.ContenedorSaldoActivo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View customView = LayoutInflater.from(context).inflate(R.layout.modal_desglose_saldos, null);
                    builder.setView(Utils.ModalRedondeado(context, customView));
                    AlertDialog dialogConsultarDesgloses = builder.create();
                    ColorDrawable back = new ColorDrawable(Color.BLACK);
                    back.setAlpha(150);
                    dialogConsultarDesgloses.getWindow().setBackgroundDrawable(back);
                    dialogConsultarDesgloses.getWindow().setDimAmount(0.8f);
                    dialogConsultarDesgloses.show();

                    ConstraintLayout ContenedorConContenido = customView.findViewById(R.id.ContenedorConContenido);
                    ConstraintLayout ContenedorSinContenido = customView.findViewById(R.id.ContenedorSinContenido);

                    TextView textSinContenido = customView.findViewById(R.id.textSinContenido);

                    TextView textView12 = customView.findViewById(R.id.textView12);
                    textView12.setText("DESGLOSE DE CAJA " + tipo_caja.toUpperCase());

                    if (tipo_caja.equalsIgnoreCase("Capital")) {

                        textView12.setTextColor(ContextCompat.getColor(context, R.color.amarillo));
                    } else {

                        textView12.setTextColor(ContextCompat.getColor(context, R.color.naranjita));
                    }

                    RecyclerView recyclerViewGastos = customView.findViewById(R.id.recyclerViewGastos);
                    RecyclerView recyclerViewAdiciones = customView.findViewById(R.id.recyclerViewAdiciones);
                    LinearLayout layoutAdiciones = customView.findViewById(R.id.layoutAdiciones);
                    LinearLayout LayoutGastos = customView.findViewById(R.id.LayoutGastos);


                    adaptadorDesgloseConsumos = new DesgloseConsumos(listaGastos, context, dialogConsultarDesgloses);
                    recyclerViewGastos.setLayoutManager(new LinearLayoutManager(context));
                    recyclerViewGastos.setAdapter(adaptadorDesgloseConsumos);

                    listaGastos.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(consumos);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            listaGastos.add(jsonObject);
                        }

                        adaptadorDesgloseConsumos.notifyDataSetChanged();
                        adaptadorDesgloseConsumos.setFilteredData(listaGastos);
                        adaptadorDesgloseConsumos.filter("");

                        if (listaGastos.size() > 0) {
                            LayoutGastos.setVisibility(View.VISIBLE);
                        } else {
                            LayoutGastos.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        LayoutGastos.setVisibility(View.GONE);
                    }


                    adaptadorDepositos = new AdaptadorDepositos(listaAdiciones, context);
                    recyclerViewAdiciones.setLayoutManager(new LinearLayoutManager(context));
                    recyclerViewAdiciones.setAdapter(adaptadorDepositos);

                    listaAdiciones.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(adiciones);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            listaAdiciones.add(jsonObject);
                        }

                        adaptadorDepositos.notifyDataSetChanged();
                        adaptadorDepositos.setFilteredData(listaAdiciones);
                        adaptadorDepositos.filter("");

                        if (listaAdiciones.size() > 0) {
                            layoutAdiciones.setVisibility(View.VISIBLE);

                        } else {
                            layoutAdiciones.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        layoutAdiciones.setVisibility(View.GONE);

                    }


                    if (listaAdiciones.size() > 0 || listaGastos.size() > 0) {
                        ContenedorConContenido.setVisibility(View.VISIBLE);
                        ContenedorSinContenido.setVisibility(View.GONE);

                    } else {
                        ContenedorConContenido.setVisibility(View.GONE);
                        ContenedorSinContenido.setVisibility(View.VISIBLE);
                        textSinContenido.setText("NO HAY MOVIMIENTOS PARA LA CAJA " + tipo_caja.toUpperCase());
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

        TextView TextViewtipo_caja;
        TextView TextViewsaldo_asignado;


        TextView FechaAsignacion;

        ConstraintLayout ContenedorSaldoActivo;

        TextView TextViewsaldo_restante;
        TextView TextViewtotal_adiciones;
        TextView TextViewtotal_consumos;
        TextView TextViewStatus;

        TextView FechaFinalizacion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TextViewStatus = itemView.findViewById(R.id.TextViewStatus);
            TextViewsaldo_restante = itemView.findViewById(R.id.TextViewsaldo_restante);
            TextViewtotal_adiciones = itemView.findViewById(R.id.TextViewtotal_adiciones);
            TextViewtotal_consumos = itemView.findViewById(R.id.TextViewtotal_consumos);

            FechaAsignacion = itemView.findViewById(R.id.FechaAsignacion);
            TextViewtipo_caja = itemView.findViewById(R.id.TextViewtipo_caja);
            TextViewsaldo_asignado = itemView.findViewById(R.id.TextViewsaldo_asignado);
            ContenedorSaldoActivo = itemView.findViewById(R.id.ContenedorSaldoActivo);
            FechaFinalizacion = itemView.findViewById(R.id.FechaFinalizacion);
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


    public AdaptadorNuevosSaldos(List<JSONObject> data, Context context /* , AdaptadorNuevosSaldos.OnActivityActionListener actionListener */) {
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

