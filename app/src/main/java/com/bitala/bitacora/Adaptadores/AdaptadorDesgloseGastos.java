package com.bitala.bitacora.Adaptadores;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import java.util.List;
import java.util.Locale;

public class AdaptadorDesgloseGastos extends RecyclerView.Adapter<AdaptadorDesgloseGastos.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;
    AlertDialog.Builder builder;

    String url;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_desglose_gastos, parent, false);
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

            //DATOS SALDO
            String fecha = jsonObject2.optString("fecha", "");

            //DATOS ACTIVIDADES


            String ID_actividad = jsonObject2.optString("ID_actividad", "");
            String ID_nombre_actividad = jsonObject2.optString("ID_nombre_actividad", "");
            String estadoActividad = jsonObject2.optString("estadoActividad", "");
            String motivocancelacion = jsonObject2.optString("motivocancelacion", "");
            String tipo_actividad = jsonObject2.optString("tipo_actividad", "");

            String fecha_inicio = jsonObject2.optString("fecha_inicio", "");
            String fecha_fin = jsonObject2.optString("fecha_fin", "");

            String ID_usuario = jsonObject2.optString("ID_usuario", "");

            String ID_gastos = jsonObject2.optString("ID_gastos", "");
            String dinero_gastado = jsonObject2.optString("dinero_gastado", "");
            String nombre_actividad = jsonObject2.optString("nombre_actividad", "");
            String descripcionActividad = jsonObject2.optString("descripcionActividad", "");
            String hora = jsonObject2.optString("hora", "");
            String tipo = jsonObject2.optString("tipo", "");


            Bundle bundle = new Bundle();
            bundle.putString("ID_usuario", ID_usuario);
            bundle.putString("ID_actividad", ID_actividad);
            bundle.putString("ID_nombre_actividad", ID_nombre_actividad);
            bundle.putString("nombre_actividad", nombre_actividad);


            bundle.putString("estadoActividad", estadoActividad);
            bundle.putString("motivocancelacion", motivocancelacion);
            bundle.putString("tipo_actividad", tipo_actividad);
            bundle.putString("fecha_inicio", fecha_inicio);
            bundle.putString("fecha_fin", fecha_fin);


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

            if (tipo.equals("deposito")) {
                holder.ContenedorDesglose.setBackgroundResource(R.drawable.rounded_verdecito);
                holder.dinero_gastado.setTextColor(ContextCompat.getColor(context, R.color.verdefuerte));
                holder.dinero_gastado.setText("Saldo agregado: +" + dinero_gastado + "$");
                holder.nombreActividad.setVisibility(View.GONE);
                holder.descripcionActividad.setVisibility(View.GONE);
            } else {
                holder.dinero_gastado.setTextColor(ContextCompat.getColor(context, R.color.rojo));
                holder.ContenedorDesglose.setBackgroundResource(R.drawable.redondeadoconbordevino);
                holder.dinero_gastado.setText("Gasto: -" + dinero_gastado + "$");
                holder.nombreActividad.setVisibility(View.VISIBLE);
                holder.descripcionActividad.setVisibility(View.VISIBLE);
            }

            holder.nombreActividad.setText(nombre_actividad);
            holder.descripcionActividad.setText(descripcionActividad);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (tipo.equals("deposito")) {

                    } else {

                        DetallesActividadesFragment detallesActividadesFragment = new DetallesActividadesFragment();
                        detallesActividadesFragment.setArguments(bundle);

                        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.frame_layouts_fragments, detallesActividadesFragment)
                                .addToBackStack(null)
                                .commit();

                    }
                }
            });


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
        TextView dinero_gastado;

        TextView fecha;

        TextView nombreActividad;
        TextView descripcionActividad;

        ConstraintLayout ContenedorDesglose;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dinero_gastado = itemView.findViewById(R.id.dinero_gastado);
            fecha = itemView.findViewById(R.id.fecha);
            nombreActividad = itemView.findViewById(R.id.nombreActividad);
            descripcionActividad = itemView.findViewById(R.id.descripcionActividad);

            ContenedorDesglose = itemView.findViewById(R.id.ContenedorDesglose);
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


    public AdaptadorDesgloseGastos(List<JSONObject> data, Context context) {

        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        url = context.getResources().getString(R.string.urlApi);

    }


}

