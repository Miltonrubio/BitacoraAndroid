package com.bitala.bitacora.Adaptadores;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.DetallesActividadesFragment;
import com.bitala.bitacora.GastosFragment;
import com.bitala.bitacora.Utils;
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

public class AdaptadorSeleccionarSaldos extends RecyclerView.Adapter<AdaptadorSeleccionarSaldos.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    String url;

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
            String fecha_asignacion = jsonObject2.optString("fecha_asignacion", "");
            String status_saldo = jsonObject2.optString("status_saldo", "");
            Double total_dinero_agregado = jsonObject2.optDouble("total_dinero_agregado", 0);
            String caja = jsonObject2.optString("caja", "");


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

            holder.status_saldo.setTextColor(colorIcono);
            holder.ContenedorSaldo.setBackground(drawable);

            holder.LayoutGastos.setVisibility(View.GONE);
            holder.linearLayoutDepositos.setVisibility(View.GONE);

            holder.iconoMarcado.setVisibility(View.VISIBLE);

            int estadoIconoActual = estadoIcono.containsKey(ID_saldo) ? estadoIcono.get(ID_saldo) : 0;

            // Establecer la etiqueta en función del estadoIcono actual del iconoMarcado
            if (estadoIconoActual == 1) {
                holder.iconoMarcado.setImageResource(R.drawable.check_solid);
                holder.iconoMarcado.setTag(1);
            } else {
                holder.iconoMarcado.setImageResource(R.drawable.xmark_solid); // Establece el recurso correcto aquí
                holder.iconoMarcado.setTag(0);
            }


            holder.iconoMarcado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int nuevoEstadoIcono = (estadoIconoActual == 0) ? 1 : 0;

                        // Cambiar la imagen y la etiqueta en función del nuevo estadoIcono
                        if (nuevoEstadoIcono == 1) {
                            holder.iconoMarcado.setImageResource(R.drawable.check_solid);
                            holder.iconoMarcado.setTag(1);
                        } else {
                            holder.iconoMarcado.setImageResource(R.drawable.xmark_solid);
                            holder.iconoMarcado.setTag(0);
                        }

                        // Actualizar el estadoIcono en el mapa
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

        ImageView iconoMarcado;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCaja = itemView.findViewById(R.id.tvCaja);
            iconoMarcado = itemView.findViewById(R.id.iconoMarcado);
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
                String saldo_inicial = item.optString("saldo_inicial", "").toLowerCase();
                String ID_saldo = item.optString("ID_saldo", "").toLowerCase();
                String nuevo_saldo = item.optString("nuevo_saldo", "").toLowerCase();
                String fecha_asignacion = item.optString("fecha_asignacion", "").toLowerCase();
                String total_dinero_agregado = item.optString("total_dinero_agregado", "").toLowerCase();
                String status_saldo = item.optString("status_saldo", "").toLowerCase();


                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(saldo_inicial.contains(keyword) || ID_saldo.contains(keyword)
                            || nuevo_saldo.contains(keyword) || fecha_asignacion.contains(keyword)
                            || status_saldo.contains(keyword) || total_dinero_agregado.contains(keyword)


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

    public AdaptadorSeleccionarSaldos(List<JSONObject> data, Context context) {

        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        url = context.getResources().getString(R.string.urlApi);

        this.estadoIcono = new HashMap<>();
    }


}

