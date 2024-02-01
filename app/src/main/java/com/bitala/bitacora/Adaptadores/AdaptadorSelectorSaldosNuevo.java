package com.bitala.bitacora.Adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.R;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdaptadorSelectorSaldosNuevo extends RecyclerView.Adapter<AdaptadorSelectorSaldosNuevo.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    String url;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selector_nuevos_saldos, parent, false);
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
            String fecha_asignacion_saldo = jsonObject2.optString("fecha_asignacion_saldo", "");
            String hora_asignacion_saldo = jsonObject2.optString("hora_asignacion_saldo", "");
            String tipo_caja = jsonObject2.optString("tipo_caja", "");
            String saldo_restante = jsonObject2.optString("saldo_restante", "");
            String total_adiciones = jsonObject2.optString("total_adiciones", "");
            String total_consumos = jsonObject2.optString("total_consumos", "");
            String consumos = jsonObject2.optString("consumos", "");
            String adiciones = jsonObject2.optString("adiciones", "");
            String ID_registro_saldo = jsonObject2.optString("ID_registro_saldo", "");
            String status_saldo = jsonObject2.optString("status_saldo", "");
            String fecha_fin = jsonObject2.optString("fecha_fin", "");
            String hora_fin = jsonObject2.optString("hora_fin", "");


            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdfOutput = new SimpleDateFormat("EEEE dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            SimpleDateFormat sdfInputHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            if (status_saldo.equalsIgnoreCase("Finalizado")) {
                holder.FechaFinalizacion.setVisibility(View.VISIBLE);
            } else {
                holder.FechaFinalizacion.setVisibility(View.GONE);
            }


            try {
                Date dateInicio = sdfInput.parse(fecha_asignacion_saldo);
                Date horaInicio = sdfInputHora.parse(hora_fin);
                Date dateTime = new Date(dateInicio.getTime() + horaInicio.getTime());
                String fechaFormateada = sdfOutput.format(dateTime);
                holder.FechaAsignacion.setText("Asignado el " + fechaFormateada);

            } catch (ParseException e) {
                holder.FechaAsignacion.setText("No se encontro la fecha");
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


            holder.iconoMarcado.setVisibility(View.VISIBLE);

            int estadoIconoActual = estadoIcono.containsKey(ID_saldo) ? estadoIcono.get(ID_saldo) : 0;

            if (estadoIconoActual == 1) {
                holder.iconoMarcado.setImageResource(R.drawable.check_solid);
                holder.iconoMarcado.setColorFilter(ContextCompat.getColor(context, R.color.verde), PorterDuff.Mode.SRC_IN);
                holder.iconoMarcado.setTag(1);
            } else {
                holder.iconoMarcado.setImageResource(R.drawable.xmark_solid);
                holder.iconoMarcado.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_IN);
                holder.iconoMarcado.setTag(0);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int nuevoEstadoIcono = (estadoIconoActual == 0) ? 1 : 0;

                        if (nuevoEstadoIcono == 1) {
                            holder.iconoMarcado.setImageResource(R.drawable.check_solid);
                            holder.iconoMarcado.setColorFilter(ContextCompat.getColor(context, R.color.verde), PorterDuff.Mode.SRC_IN);
                            holder.iconoMarcado.setTag(1);
                        } else {
                            holder.iconoMarcado.setImageResource(R.drawable.xmark_solid);
                            holder.iconoMarcado.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_IN);
                            holder.iconoMarcado.setTag(0);
                        }

                        estadoIcono.put(ID_saldo, nuevoEstadoIcono);

                        onItemClickListener.onItemClick(ID_saldo);
                    }
                }
            });

/*
            holder.iconoMarcado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        if (holder.iconoMarcado.getTag() == null || (int) holder.iconoMarcado.getTag() == 0) {
                            // Cambiar a la imagen check_solid
                            holder.iconoMarcado.setImageResource(R.drawable.check_solid);
                            holder.iconoMarcado.setTag(1);
                        } else {
                            // Cambiar a la imagen vacía (puedes establecer el recurso correcto aquí)
                            holder.iconoMarcado.setImageResource(R.drawable.xmark_solid);
                            holder.iconoMarcado.setTag(0);
                        }

                        onItemClickListener.onItemClick(ID_saldo);
                    }
                }
            });
*/

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
        TextView FechaFinalizacion;

        TextView FechaAsignacion;

        ConstraintLayout ContenedorSaldoActivo;

        TextView TextViewsaldo_restante;
        TextView TextViewtotal_adiciones;
        TextView TextViewtotal_consumos;
        TextView TextViewStatus;

        ImageView iconoMarcado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconoMarcado = itemView.findViewById(R.id.iconoMarcado);
            TextViewStatus = itemView.findViewById(R.id.TextViewStatus);
            TextViewsaldo_restante = itemView.findViewById(R.id.TextViewsaldo_restante);
            TextViewtotal_adiciones = itemView.findViewById(R.id.TextViewtotal_adiciones);
            TextViewtotal_consumos = itemView.findViewById(R.id.TextViewtotal_consumos);
            FechaFinalizacion = itemView.findViewById(R.id.FechaFinalizacion);
            FechaAsignacion = itemView.findViewById(R.id.FechaAsignacion);
            TextViewtipo_caja = itemView.findViewById(R.id.TextViewtipo_caja);
            TextViewsaldo_asignado = itemView.findViewById(R.id.TextViewsaldo_asignado);
            ContenedorSaldoActivo = itemView.findViewById(R.id.ContenedorSaldoActivo);
        }
    }


    public void filter(String query) {
        filteredData.clear();

        if (TextUtils.isEmpty(query)) {
            filteredData.addAll(data);
        } else {
            String[] keywords = query.toLowerCase().split(" ");

            for (JSONObject item : data) {

                String ID_saldo = item.optString("ID_saldo", "");
                String fecha_asignacion_saldo = item.optString("fecha_asignacion_saldo", "");
                String tipo_caja = item.optString("tipo_caja", "");


                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(tipo_caja.contains(keyword) || ID_saldo.contains(keyword)
                            || fecha_asignacion_saldo.contains(keyword)
                    )) {
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


    public interface OnItemClickListener {
        void onItemClick(String id);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    private Map<String, Integer> estadoIcono; // Mapa para mantener el estado del icono por ID

    public AdaptadorSelectorSaldosNuevo(List<JSONObject> data, Context context) {

        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        url = context.getResources().getString(R.string.urlApi);

        this.estadoIcono = new HashMap<>();
    }


}

