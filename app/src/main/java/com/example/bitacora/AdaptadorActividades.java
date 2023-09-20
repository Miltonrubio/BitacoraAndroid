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


public class AdaptadorActividades extends RecyclerView.Adapter<AdaptadorActividades.ViewHolder> {

    private ArrayList<String> nombresActividades = new ArrayList<>();
    String siguienteEstado = "";
    String url = "http://192.168.1.124/android/mostrar.php";
    private static final int VIEW_TYPE_ERROR = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Context context;

    private List<JSONObject> filteredData;
    private List<JSONObject> data;
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;




    public AdaptadorActividades(List<JSONObject> data, Context context) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividades, parent, false);
            return new ViewHolder(view);
        } else {

            View errorView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_error, parent, false);
            return new ViewHolder(errorView);
        }

    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Context context = holder.itemView.getContext();

        // Accede a las SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);


        String permisosUsuario = sharedPreferences.getString("permisos", "");


        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            try {
                JSONObject jsonObject2 = filteredData.get(position);
                String ID_actividad = jsonObject2.optString("ID_actividad", "");
                String ID_nombre_actividad = jsonObject2.optString("ID_nombre_actividad", "");
                String ID_usuario = jsonObject2.optString("ID_usuario", "");
                String fecha_inicio = jsonObject2.optString("fecha_inicio", "");
                String fecha_fin = jsonObject2.optString("fecha_fin", "");
                String estadoActividad = jsonObject2.optString("estadoActividad", "");
                String nombre_actividad = jsonObject2.optString("nombre_actividad", "");
                String descripcionActividad = jsonObject2.optString("descripcionActividad", "");
                String permisos = jsonObject2.optString("permisos", "");
                String nombre = jsonObject2.optString("nombre", "");
                String correo = jsonObject2.optString("correo", "");
                String telefono = jsonObject2.optString("telefono", "");
                String foto_usuario= jsonObject2.optString("foto_usuario", "");

                Bundle bundle = new Bundle();
                bundle.putString("ID_actividad", ID_actividad);
                bundle.putString("ID_usuario", ID_usuario);
                bundle.putString("estadoActividad", estadoActividad);
                bundle.putString("fecha_fin", fecha_fin);
                bundle.putString("fecha_inicio", fecha_inicio);
                bundle.putString("nombre_actividad", nombre_actividad);
                bundle.putString("descripcionActividad", descripcionActividad);
                bundle.putString("permisos", permisos);
                bundle.putString("nombre", nombre);
                bundle.putString("correo", correo);
                bundle.putString("telefono", telefono);
                bundle.putString("foto_usuario", foto_usuario);



                if(!permisosUsuario.equals("SUPERADMIN")){
                    holder.textNombreUsuario.setVisibility(View.GONE);
                    holder.textIdActividad.setVisibility(View.GONE);
                    holder.textTelefonoUsuario.setVisibility(View.GONE);
                }

                setTextViewText(holder.textNombreUsuario, nombre, "Nombre no disponible");
                setTextViewText(holder.textActividad, nombre_actividad, "Actividad no disponible");
                // Crear un objeto SimpleDateFormat para el formato deseado
                SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat formatoDeseado = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy  HH:mm", Locale.getDefault());

                try {
                    Date fecha = formatoOriginal.parse(fecha_inicio);
                    String fechaFormateada = formatoDeseado.format(fecha);

                    setTextViewText(holder.textFechaActividad, fechaFormateada, "Fecha no disponible");
                } catch (Exception e) {
                    e.printStackTrace();
                }


                setTextViewText(holder.textStatus, estadoActividad.toUpperCase(), "Estado no disponible");
                actualizarEstadoYVista(holder, estadoActividad);

                setTextViewText(holder.textTelefonoUsuario, telefono, "Telefono no disponible");
                setTextViewText(holder.textDetallesActividad, descripcionActividad, "Actividad no disponible");

                if (ID_actividad.isEmpty() || ID_actividad.equals("null")) {

                    setTextViewText(holder.textIdActividad, "ID no disponible", "ID no disponible");
                } else {
                    setTextViewText(holder.textIdActividad, "ID de actividad: " + ID_actividad, "ID no disponible");
                }


                if (estadoActividad.equals("Finalizado")) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Selecciona el nuevo estado de la actividad");

                            View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.spinner_dropdown_item, null);

                            LinearLayout LayoutMandarUbicacion = customView.findViewById(R.id.LayoutMandarUbicacion);
                            LinearLayout LayoutMandarFoto = customView.findViewById(R.id.LayoutMandarFoto);
                            LinearLayout LayoutVerDetalles = customView.findViewById(R.id.LayoutVerDetalles);

                            builder.setView(customView);
                            final AlertDialog dialog = builder.create();


                            LayoutVerDetalles.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    DetallesActividadesFragment detallesActividadesFragment = new DetallesActividadesFragment();
                                    detallesActividadesFragment.setArguments(bundle);

                                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.frame_layouts_fragments, detallesActividadesFragment)
                                            .addToBackStack(null)
                                            .commit();

                                    dialog.dismiss();
                                }
                            });
                            LayoutMandarUbicacion.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                        // Crear un AlertDialog
                                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                        builder.setTitle("Confirmacion para mandar ubicaciòn");
                                        builder.setMessage("¿Estás seguro de que deseas mandar tu ubicacion para esta actividad?");

                                        // Agregar el botón de Aceptar
                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override

                                            public void onClick(DialogInterface dialog, int which) {

                                                obtenerUbicacion(context, ID_usuario, ID_actividad);
                                                dialog.dismiss();
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

                            LayoutMandarFoto.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    // Crear un Intent y adjuntar el Bundle
                                    Intent intent = new Intent(v.getContext(), SubirFotoActivity.class);
                                    intent.putExtras(bundle);

                                    // Iniciar el nuevo Activity
                                    v.getContext().startActivity(intent);
                                }
                            });


                            builder.setNegativeButton("Cancelar", null);

                            dialog.show(); // Muestra el diálogo
                        }
                    });
                } else {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            VerNombresActividades(view.getContext());

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Que deseas hacer con " + descripcionActividad + " ?");
                            View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_actividades, null);

                            LinearLayout LayoutEditar = customView.findViewById(R.id.LayoutEditar);
                            LinearLayout LayoutEliminar = customView.findViewById(R.id.LayoutEliminar);
                            LinearLayout LayoutActualizarEstado = customView.findViewById(R.id.LayoutActualizarEstado);
                            LinearLayout LayoutVerDetalles = customView.findViewById(R.id.LayoutVerDetalles);

                            Spinner SpinnerNombreActividad = customView.findViewById(R.id.SpinnerNombreActividad);
                            EditText editextDescripcionActividad = customView.findViewById(R.id.editextDescripcionActividad);
                            Button BotonActualizarActividad = customView.findViewById(R.id.BotonActualizarActividad);

                            LinearLayout LayoutPendiente = customView.findViewById(R.id.LayoutPendiente);
                            LinearLayout LayoutIniciado = customView.findViewById(R.id.LayoutIniciado);
                            LinearLayout LayoutFinalizado = customView.findViewById(R.id.LayoutFinalizado);
                            builder.setView(customView);
                            final AlertDialog dialog = builder.create();

                            nombresActividades.add(0, "Selecciona una opción");
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, nombresActividades);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            SpinnerNombreActividad.setAdapter(spinnerAdapter);

                            LayoutVerDetalles.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    DetallesActividadesFragment detallesActividadesFragment = new DetallesActividadesFragment();
                                    detallesActividadesFragment.setArguments(bundle);

                                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.frame_layouts_fragments, detallesActividadesFragment)
                                            .addToBackStack(null)
                                            .commit();

                                    dialog.dismiss();
                                }
                            });

                            LayoutActualizarEstado.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    LayoutActualizarEstado.setVisibility(View.GONE);
                                    LayoutEditar.setVisibility(View.GONE);
                                    LayoutEliminar.setVisibility(View.GONE);

                                    if (estadoActividad.equals("Pendiente")) {
                                        LayoutFinalizado.setVisibility(View.VISIBLE);
                                        LayoutIniciado.setVisibility(View.VISIBLE);
                                    } else if (estadoActividad.equals("Iniciado")) {
                                        LayoutPendiente.setVisibility(View.VISIBLE);
                                        LayoutFinalizado.setVisibility(View.VISIBLE);
                                    }


                                    LayoutPendiente.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String selectedEstado = "Pendiente";
                                            ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                                        }
                                    });

                                    LayoutIniciado.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String selectedEstado = "Iniciado";
                                            ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                                        }
                                    });

                                    LayoutFinalizado.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String selectedEstado = "Finalizado";
                                            ActualizarEstado(ID_actividad, selectedEstado, view.getContext(), holder, dialog);
                                        }
                                    });
                                }
                            });

                            LayoutEditar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    editextDescripcionActividad.setText(descripcionActividad);
                                    LayoutEditar.setVisibility(View.GONE);
                                    LayoutEliminar.setVisibility(View.GONE);
                                    LayoutActualizarEstado.setVisibility(View.GONE);
                                    SpinnerNombreActividad.setVisibility(View.VISIBLE);
                                    editextDescripcionActividad.setVisibility(View.VISIBLE);
                                    BotonActualizarActividad.setVisibility(View.VISIBLE);


                                    BotonActualizarActividad.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // Verificar si se seleccionó el hint
                                            String valorActividadSpinner = SpinnerNombreActividad.getSelectedItem().toString();
                                            if (!valorActividadSpinner.equals("Selecciona una opción")) {
                                                String descripcionActividad = editextDescripcionActividad.getText().toString();
                                                String selectedID = obtenerIDDesdeNombre(valorActividadSpinner);

                                                EditarActividad(ID_actividad, selectedID, descripcionActividad, view.getContext());

                                            } else {
                                                // Mostrar un mensaje de error o realizar la acción deseada
                                                Toast.makeText(view.getContext(), "Debes seleccionar una actividad válida.", Toast.LENGTH_SHORT).show();
                                            }
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

                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override

                                        public void onClick(DialogInterface dialog, int which) {
                                            EliminarActividad(ID_actividad, view.getContext());
                                            dialog.dismiss();
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

                            dialog.show(); // Muestra el diálogo
                        }
                    });

                }

            } finally {

            }
        }
    }

    private void actualizarEstadoYVista(ViewHolder holder, String estadoActividad) {
        int colorVerde = ContextCompat.getColor(context, R.color.verde);
        int fondoPersonalizado = R.drawable.roundedbackgroundgris;

        holder.textStatus.setText(estadoActividad);
        if (estadoActividad.equals("Pendiente")) {
            int colorAmarillo = ContextCompat.getColor(context, R.color.amarillo);

            holder.EstadoPendiente.setVisibility(View.VISIBLE);
            holder.EstadoIniciado.setVisibility(View.INVISIBLE);
            holder.EstadoFinalizado.setVisibility(View.INVISIBLE);
            holder.textStatus.setTextColor(colorAmarillo);
            siguienteEstado = "Iniciado";

        } else if (estadoActividad.equals("Iniciado")) {
            int colorNegro = ContextCompat.getColor(context, R.color.black);

            holder.EstadoPendiente.setVisibility(View.INVISIBLE);
            holder.EstadoIniciado.setVisibility(View.VISIBLE);
            holder.EstadoFinalizado.setVisibility(View.INVISIBLE);
            holder.textStatus.setTextColor(colorNegro);
            siguienteEstado = "Finalizado";

        } else if (estadoActividad.equals("Finalizado")) {
            holder.EstadoPendiente.setVisibility(View.INVISIBLE);
            holder.EstadoIniciado.setVisibility(View.INVISIBLE);
            holder.EstadoFinalizado.setVisibility(View.VISIBLE);
            holder.textStatus.setTextColor(colorVerde);
            siguienteEstado = "Pendiente";

        } else if (estadoActividad.equals("Cancelado")) {
            int colorRojo = ContextCompat.getColor(context, R.color.rojo);
            holder.textStatus.setTextColor(colorRojo);
            holder.FrameActividades.setBackgroundResource(fondoPersonalizado);
        } else {
            int colorRojo = ContextCompat.getColor(context, R.color.rojo);
            holder.textStatus.setTextColor(colorRojo);
            holder.FrameActividades.setBackgroundResource(fondoPersonalizado);
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
        TextView textFechaActividad, textStatus, textTelefonoUsuario, textNombreUsuario, textActividad, textDetallesActividad, textIdActividad;
        FrameLayout FrameActividades;

        ImageView IMNoInternet, EstadoFinalizado, EstadoIniciado, EstadoPendiente;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textFechaActividad = itemView.findViewById(R.id.textFechaActividad);
            textStatus = itemView.findViewById(R.id.textStatus);
            IMNoInternet = itemView.findViewById(R.id.IMNoInternet);
            textTelefonoUsuario = itemView.findViewById(R.id.textTelefonoUsuario);
            textNombreUsuario = itemView.findViewById(R.id.textNombreUsuario);
            textActividad = itemView.findViewById(R.id.textActividad);
            FrameActividades = itemView.findViewById(R.id.FrameActividades);
            textDetallesActividad = itemView.findViewById(R.id.textDetallesActividad);
            textIdActividad = itemView.findViewById(R.id.textIdActividad);
            EstadoFinalizado = itemView.findViewById(R.id.EstadoFinalizado);
            EstadoIniciado = itemView.findViewById(R.id.EstadoIniciado);
            EstadoPendiente = itemView.findViewById(R.id.EstadoPendiente);


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

    private void ActualizarEstado(String ID_actividad, String nuevoEstado, Context context, ViewHolder holder, AlertDialog dialog) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Aquí puedes realizar acciones adicionales si es necesario
                Toast.makeText(context, "Exito", Toast.LENGTH_SHORT).show();

                // Llama al método para actualizar el estado y la vista
                actualizarEstadoYVista(holder, nuevoEstado);
                notifyDataSetChanged();
                // Cierra el diálogo después de la respuesta exitosa
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
                params.put("opcion", "5");
                params.put("ID_actividad", ID_actividad);
                params.put("nuevoEstado", nuevoEstado);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }

    private void obtenerUbicacion(Context context, String ID_usuario, String ID_actividad) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            MandarUbicacion(ID_usuario, ID_actividad, longitude, latitude, context);

                        } else {
                            Toast.makeText(context, "No se pudo obtener la ubicación.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void MandarUbicacion(String ID_usuario, String ID_actividad, Double longitud, Double latitud, Context context) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                params.put("opcion", "8");
                params.put("ID_actividad", ID_actividad);
                params.put("ID_usuario", ID_usuario);
                params.put("longitud", longitud.toString());
                params.put("latitud", latitud.toString());
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void VerNombresActividades(Context context) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    nombresActividades.clear(); // Limpia la lista antes de agregar los nuevos nombres
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String ID_nombre_actividad = jsonObject.getString("ID_nombre_actividad");
                        String nombre_actividad = jsonObject.getString("nombre_actividad");
                        nombresActividades.add(ID_nombre_actividad + ": " + nombre_actividad); // Agrega el ID y nombre de la actividad a la lista
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Hubo un error", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "11");
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }

    private void EditarActividad(String ID_actividad, String ID_nombre_actividad, String descripcionActividad, Context context) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Actualizacion exitosa", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Hubo un error", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "17");
                params.put("ID_nombre_actividad", ID_nombre_actividad);
                params.put("descripcionActividad", descripcionActividad);
                params.put("ID_actividad", ID_actividad);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }
    private void EliminarActividad(String ID_actividad, Context context) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Eliminacion exitosa", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Hubo un error", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "18");
                params.put("ID_actividad", ID_actividad);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }

    private String obtenerIDDesdeNombre(String nombreSeleccionado) {
        for (String actividad : nombresActividades) {
            if (actividad.equals(nombreSeleccionado)) {
                // Dividir la cadena para obtener el ID (asumiendo que esté separado por ":")
                String[] partes = actividad.split(":");
                if (partes.length > 0) {
                    return partes[0].trim(); // Devuelve el ID (eliminando espacios en blanco)
                }
            }
        }
        return null; // Si no se encuentra el ID, puedes devolver null o un valor predeterminado
    }

}

