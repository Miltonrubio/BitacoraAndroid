package com.example.bitacora.Adaptadores;


import static android.app.PendingIntent.getActivity;

import static com.example.bitacora.Utils.ModalRedondeado;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bitacora.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdaptadorNombreActividades extends RecyclerView.Adapter<AdaptadorNombreActividades.ViewHolder> {

    private List<JSONObject> filteredData;
    private List<JSONObject> data;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nombre_actividades, parent, false);
        return new ViewHolder(view);

    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            JSONObject jsonObject2 = filteredData.get(position);
            String ID_nombre_actividad = jsonObject2.optString("ID_nombre_actividad", "");
            String nombre_actividad = jsonObject2.optString("nombre_actividad", "");
            setTextViewText(holder.TextNombreDeActividad, nombre_actividad, "Nombre de actividad no disponible");


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_nombre_acitividad, null);

                    builder.setView(ModalRedondeado(view.getContext(), customView));
                    AlertDialog dialogConBotones = builder.create();
                    dialogConBotones.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogConBotones.show();


                    LinearLayout LayoutEditar = customView.findViewById(R.id.LayoutEditar);
                    LinearLayout LayoutEliminar = customView.findViewById(R.id.LayoutEliminar);


/*
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Que desea hacer con:  " + nombre_actividad + " ?");

                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_nombre_acitividad, null);

                    LinearLayout LayoutEditar = customView.findViewById(R.id.LayoutEditar);
                    LinearLayout LayoutEliminar = customView.findViewById(R.id.LayoutEliminar);

                    builder.setView(customView);

                    final AlertDialog dialogConBotones = builder.create();

                    builder.setNegativeButton("Cancelar", null);

                    dialogConBotones.show(); // Muestra el diálogo

*/

                    LayoutEditar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            EditText editTextNombreActividad = customView.findViewById(R.id.editTextNombreActividad);
                            editTextNombreActividad.setText(nombre_actividad);
                            Button BotonActualizarNombre = customView.findViewById(R.id.BotonActualizarNombre);
                            editTextNombreActividad.setVisibility(View.VISIBLE);
                            BotonActualizarNombre.setVisibility(View.VISIBLE);
                            LayoutEliminar.setVisibility(View.GONE);

                            dialogConBotones.show(); // Muestra el diálogo

                            BotonActualizarNombre.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    LayoutEliminar.setVisibility(View.GONE);
                                    String nuevoNombreActividad = editTextNombreActividad.getText().toString();

                                    actionListener.onEditActivity(ID_nombre_actividad, nuevoNombreActividad);
                                    dialogConBotones.dismiss();
                                }
                            });

                        }
                    });


                    LayoutEliminar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Confirmar Eliminación");
                            builder.setMessage("¿Estás seguro de que deseas eliminar esta actividad?");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    actionListener.onDeleteActivity(ID_nombre_actividad);
                                    dialogConBotones.dismiss();
                                }
                            });

                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
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

    public void setFilteredData(List<JSONObject> filteredData) {
        this.filteredData = new ArrayList<>(filteredData);
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
        void onEditActivity(String ID_nombre_actividad, String nuevoNombreActividad);

        void onDeleteActivity(String ID_nombre_actividad);
    }

    private OnActivityActionListener actionListener;
    Context context;

    public AdaptadorNombreActividades(List<JSONObject> data, Context context, OnActivityActionListener actionListener) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        this.actionListener = actionListener;
    }


}

