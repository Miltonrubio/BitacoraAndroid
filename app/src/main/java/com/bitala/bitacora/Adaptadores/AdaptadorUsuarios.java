package com.bitala.bitacora.Adaptadores;


import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.ActividadesPorUsuarioFragment;
import com.bitala.bitacora.SubirFotoUsuarioActivity;
import com.bitala.bitacora.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.bitala.bitacora.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.ViewHolder> {

    private Context context;

    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuarios, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            JSONObject jsonObject2 = filteredData.get(position);
            String ID_usuario = jsonObject2.optString("ID_usuario", "");
            String correo = jsonObject2.optString("correo", "");
            String nombre = jsonObject2.optString("nombre", "");
            String permisos = jsonObject2.optString("permisos", "");
            String telefono = jsonObject2.optString("telefono", "");
            String clave = jsonObject2.optString("clave", "");
            String foto_usuario = jsonObject2.optString("foto_usuario", "");

            Bundle bundle = new Bundle();
            bundle.putString("ID_usuario", ID_usuario);
            bundle.putString("permisos", permisos);
            bundle.putString("nombre", nombre);
            bundle.putString("correo", correo);
            bundle.putString("telefono", telefono);
            bundle.putString("foto_usuario", foto_usuario);


            //  setTextViewText(holder.textCorreoUsuario, correo, "Correo no disponible");
            setTextViewText(holder.textRol, permisos, "Permisos no disponible");
            setTextViewText(holder.textTelefonoUsuario, telefono, "Telefono no disponible");
            setTextViewText(holder.textNombreUsuario, nombre.toUpperCase(), "Nombre no disponible");

            String image = "http://hidalgo.no-ip.info:5610/bitacora/fotos/fotos_usuarios/fotoperfilusuario" + ID_usuario + ".jpg";

            String uniqueKey = new ObjectKey(image).toString();


            Glide.with(holder.itemView.getContext())
                    .load(image)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .signature(new ObjectKey(uniqueKey))
                    .placeholder(R.drawable.imagendefault)
                    .error(R.drawable.imagendefault)
                    .into(holder.fotoDeUsuario);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_usuarios, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setView(Utils.ModalRedondeado(view.getContext(), customView));

                    AlertDialog dialogConBotones = builder.create();
                    dialogConBotones.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogConBotones.show();


                    LinearLayout LayoutEditar = customView.findViewById(R.id.LayoutEditar);
                    LinearLayout LayoutEliminar = customView.findViewById(R.id.LayoutEliminar);
                    LinearLayout LayoutActualizarFoto = customView.findViewById(R.id.LayoutActualizarFoto);
                    LinearLayout LayoutVerActividades = customView.findViewById(R.id.LayoutVerActividades);

                    LayoutActualizarFoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(v.getContext(), SubirFotoUsuarioActivity.class);
                            intent.putExtras(bundle);
                            v.getContext().startActivity(intent);
                        }
                    });
/*

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Que desea hacer con:  " + nombre + " ?");
                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_usuarios, null);

                    LinearLayout LayoutEditar = customView.findViewById(R.id.LayoutEditar);
                    LinearLayout LayoutEliminar = customView.findViewById(R.id.LayoutEliminar);
                    LinearLayout LayoutActualizarFoto = customView.findViewById(R.id.LayoutActualizarFoto);
                    LinearLayout LayoutVerActividades = customView.findViewById(R.id.LayoutVerActividades);
                    Button BotonActualizarFotoUsuario = customView.findViewById(R.id.BotonActualizarFotoUsuario);
                    builder.setView(customView);

                    final AlertDialog dialogConBotones = builder.create();


                    builder.setNegativeButton("Cancelar", null);

                    dialogConBotones.show(); // Muestra el diálogo
                        */

                    LayoutEditar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            View customView = LayoutInflater.from(context).inflate(R.layout.insertar_nuevo_usuario, null);
                            builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                            AlertDialog dialogEditar = builder.create();
                            dialogEditar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogEditar.show();


                            TextView titulo = customView.findViewById(R.id.titulo);
                            titulo.setText("Estas editanto a " + nombre.toUpperCase());
                            EditText textViewNombreUsuario = customView.findViewById(R.id.textViewNombreUsuario);
                            EditText textViewCorreoUsuario = customView.findViewById(R.id.textViewCorreoUsuario);
                            EditText TextViewClaveUsuario = customView.findViewById(R.id.TextViewClaveUsuario);
                            EditText TextViewTelefonoUsuario = customView.findViewById(R.id.TextViewTelefonoUsuario);
                            Spinner spinnerRolUsuario = customView.findViewById(R.id.spinnerRolUsuario);
                            Button botonAgregarCliente = customView.findViewById(R.id.botonAgregarCliente);
                            Button botonCancelar = customView.findViewById(R.id.botonCancelar);
                            //   ImageView btnMostrarClave = customView.findViewById(R.id.VerClave);
                            textViewNombreUsuario.setText(nombre);
                            textViewCorreoUsuario.setText(correo);
                            TextViewTelefonoUsuario.setText(telefono);
                            TextViewClaveUsuario.setText(clave);

                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                                    R.array.opciones_rol, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerRolUsuario.setAdapter(adapter);

                            botonCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogEditar.dismiss();
                                }
                            });

                            botonAgregarCliente.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String nuevoNombreUsuario = textViewNombreUsuario.getText().toString();
                                    String nuevoCorreoUsuario = textViewCorreoUsuario.getText().toString();
                                    String nuevaClaveUsuario = TextViewClaveUsuario.getText().toString();
                                    String nuevOTelefonoUsuario = TextViewTelefonoUsuario.getText().toString().replaceAll(" ", "");
                                    String nuevoRolUsuario = spinnerRolUsuario.getSelectedItem().toString();

                                    if (nuevoNombreUsuario.isEmpty() || nuevoCorreoUsuario.isEmpty() || nuevaClaveUsuario.isEmpty() || nuevOTelefonoUsuario.isEmpty() || nuevoRolUsuario.isEmpty()) {
                                        Utils.crearToastPersonalizado(context, "Tienes campos vacios, por favor rellenalos");
                                    } else {
                                        actionListener.onEditarUsuarioActivity(ID_usuario, nuevoNombreUsuario, nuevoCorreoUsuario, nuevaClaveUsuario, nuevOTelefonoUsuario, nuevoRolUsuario);
                                        dialogConBotones.dismiss();
                                        dialogEditar.dismiss();
                                    }

                                }
                            });



                            /*

                            LayoutVerActividades.setVisibility(View.GONE);
                            LayoutEliminar.setVisibility(View.GONE);
                            LayoutActualizarFoto.setVisibility(View.GONE);
                            // Hacer visible el EditText
                            EditText editTextNombreUsuario = customView.findViewById(R.id.editTextNombreUsuario);
                            EditText editTextCorreoUsuario = customView.findViewById(R.id.editTextCorreoUsuario);
                            EditText editTextClaveUsuario = customView.findViewById(R.id.editTextClaveUsuario);
                            EditText editTextTelefonoUsuario = customView.findViewById(R.id.editTextTelefonoUsuario);
                            Spinner opcionesActualizarRol = customView.findViewById(R.id.opcionesActualizarRol);
                            Button BotonActualizarUsuario = customView.findViewById(R.id.BotonActualizarUsuario);
                            ImageView btnMostrarClave = customView.findViewById(R.id.VerClave);

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

                            BotonActualizarUsuario.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String nuevoNombreUsuario = editTextNombreUsuario.getText().toString();
                                    String nuevoCorreoUsuario = editTextCorreoUsuario.getText().toString();
                                    String nuevaClaveUsuario = editTextClaveUsuario.getText().toString();
                                    String nuevOTelefonoUsuario = editTextTelefonoUsuario.getText().toString().replaceAll(" ", "");
                                    String nuevoRolUsuario = opcionesActualizarRol.getSelectedItem().toString();

                                    if (nuevoNombreUsuario.isEmpty() || nuevoCorreoUsuario.isEmpty() || nuevaClaveUsuario.isEmpty() || nuevOTelefonoUsuario.isEmpty() || nuevoRolUsuario.isEmpty()) {
                                        Toast.makeText(view.getContext(), "No puedes ingresar campos vacios", Toast.LENGTH_SHORT).show();
                                    } else {
                                        actionListener.onEditarUsuarioActivity(ID_usuario, nuevoNombreUsuario, nuevoCorreoUsuario, nuevaClaveUsuario, nuevOTelefonoUsuario, nuevoRolUsuario);
                                        dialogConBotones.dismiss();
                                    }

                                }
                            });

                        }
                    });


                    LayoutActualizarFoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), SubirFotoUsuarioActivity.class);
                            intent.putExtras(bundle);
                            v.getContext().startActivity(intent);
                            /*
                                                        LayoutVerActividades.setVisibility(View.GONE);

                            LayoutEliminar.setVisibility(View.GONE);
                            LayoutEditar.setVisibility(View.GONE);
                            BotonActualizarFotoUsuario.setVisibility(View.VISIBLE);


                            BotonActualizarFotoUsuario.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Crear un Intent y adjuntar el Bundle
                                    Intent intent = new Intent(v.getContext(), SubirFotoUsuarioActivity.class);
                                    intent.putExtras(bundle);
                                    v.getContext().startActivity(intent);
                                }
                            });
*/


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
                            textViewTituloConfirmacion.setText("¿Estas seguro que deseas eliminar a " + nombre + " ?");
                            builder.setView(Utils.ModalRedondeado(context, customView));
                            AlertDialog dialogConfirmacion = builder.create();
                            dialogConfirmacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogConfirmacion.show();

                            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
/*
                                    actionListener.onDeleteActivity(ID_actividad);
                                    dialogConfirmacion.dismiss();
                                    dialogOpcionesDeActividad.dismiss();
*/
                                    actionListener.onEliminarUsuarioActivity(ID_usuario, nombre);
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

/*
                            // Crear un AlertDialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Confirmar Eliminación");
                            builder.setMessage("¿Estás seguro de que deseas eliminar esta actividad?");

                            // Agregar el botón de Aceptar
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //     EliminarUsuario(ID_usuario, view.getContext(), holder);
                                    actionListener.onEliminarUsuarioActivity(ID_usuario, nombre);
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

 */
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
        TextView textNombreUsuario, textCorreoUsuario, textRol, textTelefonoUsuario;
        ImageView fotoDeUsuario;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textNombreUsuario = itemView.findViewById(R.id.textNombreUsuario);
            //  textCorreoUsuario = itemView.findViewById(R.id.textCorreoUsuario);
            textRol = itemView.findViewById(R.id.textRol);
            fotoDeUsuario = itemView.findViewById(R.id.fotoDeUsuario);

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

                String ID_usuario = item.optString("ID_usuario", "").toLowerCase();
                String correo = item.optString("correo", "").toLowerCase();
                String nombre = item.optString("nombre", "").toLowerCase();
                String permisos = item.optString("permisos", "").toLowerCase();
                String telefono = item.optString("telefono", "").toLowerCase();

                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(ID_usuario.contains(keyword) || correo.contains(keyword) || nombre.contains(keyword) || permisos.contains(keyword) ||
                            telefono.contains(keyword))) {
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
        void onEditarUsuarioActivity(String ID_usuario, String nombreUsuario, String correoUsuario, String claveUsuario, String telefonoUsuario, String rolUsuario);

        void onEliminarUsuarioActivity(String ID_usuario, String nombre);

        void onFilterData(Boolean estado);
    }

    private AdaptadorUsuarios.OnActivityActionListener actionListener;

    public AdaptadorUsuarios(List<JSONObject> data, Context context, AdaptadorUsuarios.OnActivityActionListener actionListener) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        this.actionListener = actionListener;
    }

}

