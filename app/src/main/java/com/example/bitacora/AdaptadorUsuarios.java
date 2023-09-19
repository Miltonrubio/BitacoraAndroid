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
import android.text.method.PasswordTransformationMethod;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
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


public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.ViewHolder> {

    String siguienteEstado = "";
    String url = "http://192.168.1.124/android/mostrar.php";
    private static final int VIEW_TYPE_ERROR = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Context context;

    private List<JSONObject> filteredData;
    private List<JSONObject> data;


    public AdaptadorUsuarios(List<JSONObject> data, Context context) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuarios, parent, false);
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
                String ID_usuario = jsonObject2.optString("ID_usuario", "");
                String correo = jsonObject2.optString("correo", "");
                String nombre = jsonObject2.optString("nombre", "");
                String permisos = jsonObject2.optString("permisos", "");
                String telefono= jsonObject2.optString("telefono", "");
                String clave= jsonObject2.optString("clave", "");
                String foto_usuario= jsonObject2.optString("foto_usuario", "");

                Bundle bundle = new Bundle();
                bundle.putString("ID_usuario", ID_usuario);
                bundle.putString("permisos", permisos);
                bundle.putString("nombre", nombre);
                bundle.putString("correo", correo);
                bundle.putString("telefono", telefono);
                bundle.putString("foto_usuario", foto_usuario);


                setTextViewText(holder.textCorreoUsuario, correo, "Correo no disponible");
                setTextViewText(holder.textRol, permisos, "Permisos no disponible");
                setTextViewText(holder.textTelefonoUsuario, telefono, "Telefono no disponible");
                setTextViewText(holder.textNombreUsuario, nombre, "Nombre no disponible");


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Que desea hacer con:  " + nombre + " ?");

                        View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_usuarios, null);

                        LinearLayout LayoutEditar = customView.findViewById(R.id.LayoutEditar);
                        LinearLayout LayoutEliminar = customView.findViewById(R.id.LayoutEliminar);
                        LinearLayout LayoutVerActividades = customView.findViewById(R.id.LayoutVerActividades);
                        builder.setView(customView);

                        final AlertDialog dialogConBotones = builder.create();
                        LayoutEditar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                LayoutEliminar.setVisibility(View.GONE);
                                // Hacer visible el EditText
                                EditText editTextNombreUsuario = customView.findViewById(R.id.editTextNombreUsuario);
                                EditText editTextCorreoUsuario = customView.findViewById(R.id.editTextCorreoUsuario);
                                EditText editTextClaveUsuario = customView.findViewById(R.id.editTextClaveUsuario);
                                EditText editTextTelefonoUsuario = customView.findViewById(R.id.editTextTelefonoUsuario);
                                Spinner opcionesActualizarRol= customView.findViewById(R.id.opcionesActualizarRol);
                                Button BotonActualizarUsuario = customView.findViewById(R.id.BotonActualizarUsuario);
                                ImageView btnMostrarClave= customView.findViewById(R.id.VerClave);


                                editTextNombreUsuario.setText(nombre);
                                editTextCorreoUsuario.setText(correo);
                                editTextTelefonoUsuario.setText(telefono);
                                editTextClaveUsuario.setText(clave);
                                editTextClaveUsuario.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                btnMostrarClave.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (editTextClaveUsuario.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                                            // Si la contraseña está oculta, mostrarla
                                            editTextClaveUsuario.setTransformationMethod(null);
                                        } else {
                                            // Si la contraseña se muestra, ocultarla
                                            editTextClaveUsuario.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                        }
                                    }
                                });


                                btnMostrarClave.setVisibility(View.VISIBLE);
                                editTextNombreUsuario.setVisibility(View.VISIBLE);
                                editTextCorreoUsuario.setVisibility(View.VISIBLE);
                                editTextClaveUsuario.setVisibility(View.VISIBLE);
                                editTextTelefonoUsuario.setVisibility(View.VISIBLE);
                                opcionesActualizarRol.setVisibility(View.VISIBLE);

                                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                                        R.array.opciones_rol, android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                opcionesActualizarRol.setAdapter(adapter);
                                BotonActualizarUsuario.setVisibility(View.VISIBLE);

                                dialogConBotones.show(); // Muestra el diálogo

                                BotonActualizarUsuario.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        String nuevoNombreUsuario = editTextNombreUsuario.getText().toString();
                                        String nuevoCorreoUsuario = editTextCorreoUsuario.getText().toString();
                                        String nuevaClaveUsuario = editTextClaveUsuario.getText().toString();
                                        String nuevOTelefonoUsuario = editTextTelefonoUsuario.getText().toString();


                                        String nuevoRolUsuario = opcionesActualizarRol.getSelectedItem().toString();


                                 EditarUsuario(ID_usuario, nuevoNombreUsuario,nuevoCorreoUsuario , nuevaClaveUsuario ,nuevOTelefonoUsuario ,nuevoRolUsuario, view.getContext(), holder, dialogConBotones);
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
                                        EliminarUsuario(ID_usuario, view.getContext(), holder);
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


                        LayoutVerActividades.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActividadesPorUsuarioFragment actividadesPorUsuarioFragment = new ActividadesPorUsuarioFragment();
                                actividadesPorUsuarioFragment.setArguments(bundle);

                                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame_layouts_fragments, actividadesPorUsuarioFragment)
                                        .addToBackStack(null)
                                        .commit();

                                dialogConBotones.dismiss();
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
        TextView textNombreUsuario, textCorreoUsuario, textRol, textTelefonoUsuario;
        FrameLayout FrameActividades;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textNombreUsuario = itemView.findViewById(R.id.textNombreUsuario);
            textCorreoUsuario = itemView.findViewById(R.id.textCorreoUsuario);
            textRol = itemView.findViewById(R.id.textRol);

            textTelefonoUsuario = itemView.findViewById(R.id.textTelefonoUsuario);


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
                String descripcionActividad = item.optString("descripcionActividad", "").toLowerCase();
                String estadoActividad = item.optString("estadoActividad", "").toLowerCase();
                String fecha_inicio = item.optString("fecha_inicio", "").toLowerCase();
                String fecha_fin = item.optString("fecha_fin", "").toLowerCase();

                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(estadoActividad.contains(keyword) || descripcionActividad.contains(keyword) || nombreActividad.contains(keyword) || ID_actividad.contains(keyword) ||
                            fecha_inicio.contains(keyword) || fecha_fin.contains(keyword))) {
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

    private void EditarUsuario(String ID_usuario, String nombreUsuario, String correoUsuario,String claveUsuario,String telefonoUsuario,String rolUsuario, Context context, AdaptadorUsuarios.ViewHolder holder, AlertDialog dialog) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Aquí puedes realizar acciones adicionales si es necesario
                Toast.makeText(context, "Exito", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
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
                params.put("opcion", "15");
                params.put("ID_usuario", ID_usuario);
                params.put("permisos", rolUsuario);
                params.put("nombre", nombreUsuario);
                params.put("correo", correoUsuario);
                params.put("clave", claveUsuario);
                params.put("telefono", telefonoUsuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }




    private void EliminarUsuario(String ID_usuario, Context context, AdaptadorUsuarios.ViewHolder holder) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Aquí puedes realizar acciones adicionales si es necesario
                Toast.makeText(context, "Exito", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();

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
                params.put("opcion", "16");
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


}

