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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.Utils;
import com.bitala.bitacora.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdaptadorListaAsignarActividades extends RecyclerView.Adapter<AdaptadorListaAsignarActividades.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;
    AlertDialog.Builder builder;

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
            String tipo_actividad = jsonObject2.optString("tipo_actividad", "");
            String nombre_actividad = jsonObject2.optString("nombre_actividad", "");

            int colorRes;
            Drawable drawable;
            if (tipo_actividad.equalsIgnoreCase("OFICINAS")) {
                drawable = ContextCompat.getDrawable(context, R.drawable.redondeadoconbordevino);
                colorRes = R.color.vino;
            } else {

                drawable = ContextCompat.getDrawable(context, R.drawable.roundedbackground_nombre_actividad);
                colorRes = R.color.naranjita;
            }

            holder.FrameActividades.setBackground(drawable);
            int color = ContextCompat.getColor(context, colorRes);
            holder.TextNombreDeActividad.setTextColor(color);
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
                     AlertDialog dialogDescripcion = builder.create();
                    dialogDescripcion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogDescripcion.show();


                    buttonCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogDescripcion.dismiss();
                        }
                    });


                    buttonAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String descripcion = descripcionActividad.getText().toString();
                            if (descripcion.isEmpty()) {
                                Utils.crearToastPersonalizado(context, "Agrega una descripcion para tu actividad");
                            } else {
                                actionListener.onAsignarActividadAUsuario(ID_nombre_actividad, descripcion, ID_usuario, nombre, token);

                                dialogDescripcion.dismiss();
                                dialogAsignarActividad.dismiss();
                                dialogOpcionesUsuarios.dismiss();
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

        FrameLayout FrameActividades;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TextNombreDeActividad = itemView.findViewById(R.id.TextNombreDeActividad);
            FrameActividades=itemView.findViewById(R.id.FrameActividades);
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
        void onAsignarActividadAUsuario(String idNombreActividad, String descripcion, String ID_usuario, String nombre, String token);

    }

    private OnActivityActionListener actionListener;

    String ID_usuario;
    AlertDialog dialogAsignarActividad;
    AlertDialog dialogOpcionesUsuarios;
    String nombre;
    String token;

    public AdaptadorListaAsignarActividades(List<JSONObject> data, Context context, OnActivityActionListener actionListener, String ID_usuario, AlertDialog dialogAsignarActividad, AlertDialog dialogOpcionesUsuarios, String nombre, String token) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        this.actionListener = actionListener;
        this.ID_usuario = ID_usuario;
        this.dialogAsignarActividad= dialogAsignarActividad;
        this.dialogOpcionesUsuarios= dialogOpcionesUsuarios;
        this.nombre= nombre;
        this.token=token;


    }


}

