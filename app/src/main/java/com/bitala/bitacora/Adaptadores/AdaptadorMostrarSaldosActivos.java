package com.bitala.bitacora.Adaptadores;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bitala.bitacora.R;
import com.bitala.bitacora.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdaptadorMostrarSaldosActivos extends RecyclerView.Adapter<AdaptadorMostrarSaldosActivos.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    String url;


    AdaptadorNuevoDesgloseDeGastos adaptadorDesgloseGastos;
    List<JSONObject> listaGastos = new ArrayList<>();
    AdaptadorDepositos adaptadorDepositos;
    List<JSONObject> listaAdiciones = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saldos_activos, parent, false);
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
            String desglose_gastos = jsonObject2.optString("desglose_gastos", "");
            String desglose_adiciones = jsonObject2.optString("desglose_adiciones", "");


            holder.TextViewsaldo_asignado.setText("Saldo inicial: " + saldo_asignado + " $");
            holder.TextViewtotal_adiciones.setText("Total agregado: +" + total_adiciones + " $");
            holder.TextViewtotal_consumos.setText("Total gastado: -" + total_consumos + " $");
            holder.TextViewsaldo_restante.setText("Saldo restante: " + saldo_restante + " $");

            holder.TextViewtipo_caja.setText(tipo_caja.toUpperCase());

            holder.FechaAsignacion.setText(fecha_asignacion_saldo);


            holder.butonFinalizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    asfgasfasf


                }
            });


            holder.botonAgregarMasSaldo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


            holder.ContenedorSaldoActivo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View customView = LayoutInflater.from(context).inflate(R.layout.modal_desglose_saldos, null);
                    builder.setView(Utils.ModalRedondeado(context, customView));
                    AlertDialog dialogGestionarSaldo = builder.create();
                    ColorDrawable back = new ColorDrawable(Color.BLACK);
                    back.setAlpha(150);
                    dialogGestionarSaldo.getWindow().setBackgroundDrawable(back);
                    dialogGestionarSaldo.getWindow().setDimAmount(0.8f);
                    dialogGestionarSaldo.show();

                    TextView textView12 = customView.findViewById(R.id.textView12);
                    textView12.setText("Desglose de saldos de Caja: " + tipo_caja);

                    RecyclerView recyclerViewGastos = customView.findViewById(R.id.recyclerViewGastos);
                    RecyclerView recyclerViewAdiciones = customView.findViewById(R.id.recyclerViewAdiciones);
                    LinearLayout layoutAdiciones = customView.findViewById(R.id.layoutAdiciones);
                    LinearLayout LayoutGastos = customView.findViewById(R.id.LayoutGastos);


                    adaptadorDesgloseGastos = new AdaptadorNuevoDesgloseDeGastos(listaGastos, context, dialogGestionarSaldo, dialogSaldoAsignado, dialogOpcionesUsuarios);
                    recyclerViewGastos.setLayoutManager(new LinearLayoutManager(context));
                    recyclerViewGastos.setAdapter(adaptadorDesgloseGastos);

                    listaGastos.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(desglose_gastos);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            listaGastos.add(jsonObject);
                        }

                        adaptadorDesgloseGastos.notifyDataSetChanged();
                        adaptadorDesgloseGastos.setFilteredData(listaGastos);
                        adaptadorDesgloseGastos.filter("");

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
                        JSONArray jsonArray = new JSONArray(desglose_adiciones);
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


                }
            });


        } finally {
        }
    }


    AlertDialog modalCargando;
    AlertDialog.Builder builder;


    private void FinalizarUnSaldo(String ID_usuario) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "75");
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    List<JSONObject> listaDesgloses = new ArrayList<>();

    @Override
    public int getItemCount() {

        return filteredData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView TextViewtipo_caja;
        TextView TextViewsaldo_asignado;

        ImageView btnEditar;

        TextView FechaAsignacion;

        Button butonFinalizar;
        ImageView botonAgregarMasSaldo;
        ConstraintLayout ContenedorSaldoActivo;

        TextView TextViewsaldo_restante;
        TextView TextViewtotal_adiciones;
        TextView TextViewtotal_consumos;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TextViewsaldo_restante = itemView.findViewById(R.id.TextViewsaldo_restante);
            TextViewtotal_adiciones = itemView.findViewById(R.id.TextViewtotal_adiciones);
            TextViewtotal_consumos = itemView.findViewById(R.id.TextViewtotal_consumos);

            FechaAsignacion = itemView.findViewById(R.id.FechaAsignacion);
            TextViewtipo_caja = itemView.findViewById(R.id.TextViewtipo_caja);
            TextViewsaldo_asignado = itemView.findViewById(R.id.TextViewsaldo_asignado);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            butonFinalizar = itemView.findViewById(R.id.butonFinalizar);
            ContenedorSaldoActivo = itemView.findViewById(R.id.ContenedorSaldoActivo);
            botonAgregarMasSaldo = itemView.findViewById(R.id.botonAgregarMasSaldo);
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

    AlertDialog dialogSaldoAsignado;
    AlertDialog dialogOpcionesUsuarios;

    public AdaptadorMostrarSaldosActivos(List<JSONObject> data, Context context, AlertDialog dialogSaldoAsignado, AlertDialog dialogOpcionesUsuarios) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        url = context.getResources().getString(R.string.urlApi);
        this.dialogSaldoAsignado = dialogSaldoAsignado;
        this.dialogOpcionesUsuarios = dialogOpcionesUsuarios;

/*
        builderCargando = new AlertDialog.Builder(context);
        builderCargando.setCancelable(false);

 */
    }


}

