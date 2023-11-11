package com.bitala.bitacora.Adaptadores;


import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.Utils;
import com.bitala.bitacora.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdaptadorListaActividades extends RecyclerView.Adapter<AdaptadorListaActividades.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;
    AlertDialog.Builder builder;


    public AdaptadorListaActividades(List<JSONObject> data, Context context) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nombre_actividades, parent, false);
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
            String ID_nombre_actividad = jsonObject2.optString("ID_nombre_actividad", "");
            String nombre_actividad = jsonObject2.optString("nombre_actividad", "");

            setTextViewText(holder.TextNombreDeActividad, nombre_actividad.toUpperCase(), "No se encontro esta actividad");


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    builder = new AlertDialog.Builder(view.getContext());
                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_describe_actividad, null);
                    TextView textViewTitulo = customView.findViewById(R.id.textViewTitulo);
                    Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                    Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                    EditText descripcionActividad = customView.findViewById(R.id.descripcionActividad);

                    textViewTitulo.setText(nombre_actividad.toUpperCase());


                    builder.setView(Utils.ModalRedondeado(context, customView));
                    final AlertDialog dialogConBotones = builder.create();
                    dialogConBotones.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogConBotones.show();


                    buttonCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogConBotones.dismiss();
                        }
                    });


                    buttonAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String descripcion = descripcionActividad.getText().toString();
                            if (descripcion.isEmpty()) {
                                Utils.crearToastPersonalizado(context, "Agrega una descripcion para tu actividad");
                            } else {
                                actionListener.onAgregarActividad(ID_nombre_actividad, descripcion);
                                dialogConBotones.dismiss();
                            }
                        }
                    });

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
        TextView TextNombreDeActividad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TextNombreDeActividad = itemView.findViewById(R.id.TextNombreDeActividad);
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


    public interface OnActivityActionListener {
        void onAgregarActividad(String idNombreActividad, String descripcion);

    }

    private OnActivityActionListener actionListener;

    public AdaptadorListaActividades(List<JSONObject> data, Context context, OnActivityActionListener actionListener) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        this.actionListener = actionListener; // Asigna el listener
    }


}

