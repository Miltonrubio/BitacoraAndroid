package com.bitala.bitacora.Adaptadores;


import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
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


public class AdaptadorNombreActividades extends RecyclerView.Adapter<AdaptadorNombreActividades.ViewHolder> {

    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    String valorTipoActividad;

    String url;

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
            String tipo_actividad = jsonObject2.optString("tipo_actividad", "");


            setTextViewText(holder.TextNombreDeActividad, nombre_actividad, "Nombre de actividad no disponible");

            SharedPreferences sharedPreferences = context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
            String permisosUsuario = sharedPreferences.getString("permisos", "");

            int colorRes;
            Drawable drawable;
            /*
            if(permisosUsuario.equalsIgnoreCase("SUPERADMIN")){
                 drawable = ContextCompat.getDrawable(context, R.drawable.redondeadoconbordevino);
                colorRes = R.color.vino;

            }else {
                 drawable = ContextCompat.getDrawable(context, R.drawable.roundedbackground_nombre_actividad);
                colorRes = R.color.naranjita;
            }
            */

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


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_nombre_acitividad, null);

                    builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                    AlertDialog dialogConBotones = builder.create();
                    dialogConBotones.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogConBotones.show();


                    LinearLayout LayoutEditar = customView.findViewById(R.id.LayoutEditar);
                    LinearLayout LayoutEliminar = customView.findViewById(R.id.LayoutEliminar);
                    LinearLayout ContenedorRadios = customView.findViewById(R.id.ContenedorRadios);

                    RadioButton radioButtonGENERAL = customView.findViewById(R.id.radioButtonGENERAL);
                    RadioButton radioButtonOFICINAS = customView.findViewById(R.id.radioButtonOFICINAS);

                    if (tipo_actividad.equalsIgnoreCase("OFICINAS")) {
                        radioButtonOFICINAS.setChecked(true);
                        radioButtonGENERAL.setChecked(false);
                        valorTipoActividad="OFICINAS";

                    } else {
                        radioButtonGENERAL.setChecked(true);
                        radioButtonOFICINAS.setChecked(false);
                        valorTipoActividad="GENERAL";
                    }

                    radioButtonGENERAL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            valorTipoActividad="GENERAL";
                            radioButtonGENERAL.setChecked(true);
                            radioButtonOFICINAS.setChecked(false);
                        }
                    });

                    radioButtonOFICINAS.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            valorTipoActividad="OFICINAS";
                            radioButtonGENERAL.setChecked(false);
                            radioButtonOFICINAS.setChecked(true);
                        }
                    });



                    LayoutEditar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            EditText editTextNombreActividad = customView.findViewById(R.id.editTextNombreActividad);
                            editTextNombreActividad.setText(nombre_actividad);
                            Button BotonActualizarNombre = customView.findViewById(R.id.BotonActualizarNombre);
                            editTextNombreActividad.setVisibility(View.VISIBLE);
                            BotonActualizarNombre.setVisibility(View.VISIBLE);
                            ContenedorRadios.setVisibility(View.VISIBLE);
                            LayoutEditar.setVisibility(View.GONE);
                            LayoutEliminar.setVisibility(View.GONE);

                            Drawable drawableInterno;
                            if (tipo_actividad.equalsIgnoreCase("OFICINAS")) {
                                drawableInterno = ContextCompat.getDrawable(context, R.drawable.redondeadoconbordevino);
                            } else {
                                drawableInterno = ContextCompat.getDrawable(context, R.drawable.roundedbackground_nombre_actividad);
                            }

                            editTextNombreActividad.setBackground(drawableInterno);


                            dialogConBotones.show();

                            BotonActualizarNombre.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String nuevoNombreActividad = editTextNombreActividad.getText().toString();

                                    actionListener.onEditActivity(ID_nombre_actividad, nuevoNombreActividad, valorTipoActividad);
                                    dialogConBotones.dismiss();
                                }
                            });

                        }
                    });


                    LayoutEliminar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View customView = LayoutInflater.from(context).inflate(R.layout.opciones_confirmacion, null);
                            TextView textViewTituloConfirmacion = customView.findViewById(R.id.textViewTituloConfirmacion);
                            Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                            Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                            textViewTituloConfirmacion.setText("¿Estas seguro que deseas eliminar la actividad " + nombre_actividad + " ?");
                            builder.setView(Utils.ModalRedondeado(context, customView));
                            AlertDialog dialogConfirmacion = builder.create();
                            dialogConfirmacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogConfirmacion.show();

                            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    actionListener.onDeleteActivity(ID_nombre_actividad);
                                    dialogConBotones.dismiss();
                                    dialogConfirmacion.dismiss();
                                }
                            });

                            buttonCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogConfirmacion.dismiss();
                                }
                            });


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
            FrameActividades = itemView.findViewById(R.id.FrameActividades);
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
        if (filteredData.isEmpty()) {
            actionListener.onFilterData(false); // Indica que no hay resultados
        } else {
            actionListener.onFilterData(true); // Indica que hay resultados
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
        void onEditActivity(String ID_nombre_actividad, String nuevoNombreActividad, String valorTipoActividad);

        void onDeleteActivity(String ID_nombre_actividad);

        void onFilterData(Boolean estado);
    }

    private OnActivityActionListener actionListener;
    Context context;

    public AdaptadorNombreActividades(List<JSONObject> data, Context context, OnActivityActionListener actionListener) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        this.actionListener = actionListener;
        url = context.getResources().getString(R.string.urlApi);
    }


}

