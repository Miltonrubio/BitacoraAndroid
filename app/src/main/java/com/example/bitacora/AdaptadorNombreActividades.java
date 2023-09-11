package com.example.bitacora;


import static android.app.Activity.RESULT_OK;
import static android.app.PendingIntent.getActivity;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bitacora.databinding.ActivitySubirFotoBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.Manifest;


public class AdaptadorNombreActividades extends RecyclerView.Adapter<AdaptadorNombreActividades.ViewHolder> {

    String url = "http://192.168.1.124/android/mostrar.php";
    private static final int VIEW_TYPE_ERROR = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Context context;

    private List<JSONObject> filteredData;
    private List<JSONObject> data;


    public AdaptadorNombreActividades(List<JSONObject> data, Context context) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nombre_actividades, parent, false);
            return new ViewHolder(view);
        } else {

            View errorView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_error, parent, false);
            return new ViewHolder(errorView);
        }

    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            try {
                JSONObject jsonObject2 = filteredData.get(position);
                String ID_nombre_actividad = jsonObject2.optString("ID_nombre_actividad", "");
                String nombre_actividad = jsonObject2.optString("nombre_actividad", "");


                setTextViewText(holder.TextNombreDeActividad, nombre_actividad, "Nombre de actividad no disponible");

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Que desea hacer con:  " + nombre_actividad + " ?");

                        // Inflar el diseño personalizado con tres botones
                        View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_nombre_acitividad, null);

                        // Obtener referencias a los botones en el diseño personalizado
                        LinearLayout LayoutEditar = customView.findViewById(R.id.LayoutEditar);
                        LinearLayout LayoutEliminar = customView.findViewById(R.id.LayoutEliminar);

                        builder.setView(customView);

                        // Crear el diálogo
                        final AlertDialog dialogConBotones = builder.create();

                        // Configurar los botones según tus necesidades
                        LayoutEditar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Hacer visible el EditText
                                EditText editTextNombreActividad = customView.findViewById(R.id.editTextNombreActividad);
                                Button BotonActualizarNombre = customView.findViewById(R.id.BotonActualizarNombre);
                                editTextNombreActividad.setVisibility(View.VISIBLE);
                                BotonActualizarNombre.setVisibility(View.VISIBLE);


                                dialogConBotones.show(); // Muestra el diálogo

                                BotonActualizarNombre.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        LayoutEliminar.setVisibility(View.GONE);
                                        String nuevoNombreActividad = editTextNombreActividad.getText().toString();
                                        EditarNombreActividad(ID_nombre_actividad, nuevoNombreActividad, view.getContext(), holder, dialogConBotones);
                                    }
                                });

                            }
                        });


                        LayoutEliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Crear un AlertDialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setTitle("Confirmar Eliminación");
                                builder.setMessage("¿Estás seguro de que deseas eliminar esta actividad?");

                                // Agregar el botón de Aceptar
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Llamar al método para eliminar
                                        EliminarNombreActividad(ID_nombre_actividad, view.getContext(), holder);
                                        dialogConBotones.dismiss();
                                    }
                                });

                                // Agregar el botón de Cancelar
                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Cerrar el diálogo
                                        dialog.dismiss();
                                    }
                                });

                                // Mostrar el diálogo
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });


                        builder.setNegativeButton("Cancelar", null);

                        dialogConBotones.show(); // Muestra el diálogo
                    }
                });

            } finally {

            }
        }
    }

    @Override
    public int getItemCount() {

        //return filteredData.size();
        return filteredData.isEmpty() ? 1 : filteredData.size();

    }

    @Override
    public int getItemViewType(int position) {
        return filteredData.isEmpty() ? VIEW_TYPE_ERROR : VIEW_TYPE_ITEM;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        //   FrameLayout FrameActividades;

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
                String nombreActividad = item.optString("nombreActividad", "").toLowerCase();

                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(nombreActividad.contains(keyword))) {
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


    private void EliminarNombreActividad(String ID_nombre_actividad, Context context, ViewHolder holder) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Aquí puedes realizar acciones adicionales si es necesario
                Toast.makeText(context, "Exito", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "12");
                params.put("ID_nombre_actividad", ID_nombre_actividad);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void EditarNombreActividad(String ID_nombre_actividad, String nuevoNombreActividad, Context context, ViewHolder holder, AlertDialog dialog) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Aquí puedes realizar acciones adicionales si es necesario
                Toast.makeText(context, "Exito", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "7");
                params.put("ID_nombre_actividad", ID_nombre_actividad);
                params.put("nuevoNombreActividad", nuevoNombreActividad);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }

}

