package com.example.bitacora;

import static com.example.bitacora.Utils.ModalRedondeado;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bitacora.Adaptadores.AdaptadorUsuarios;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrudUsuariosFragment extends Fragment implements AdaptadorUsuarios.OnActivityActionListener {

    String url;
    private RecyclerView recyclerViewUsuarios;
    private AdaptadorUsuarios adaptadorUsuarios;
    private List<JSONObject> dataList = new ArrayList<>();
    private EditText editTextBusqueda;
    private FloatingActionButton botonAgregarActividad;

    Context context;

    RelativeLayout LayoutContenido;

    ConstraintLayout SinActividades;
    ConstraintLayout LayoutSinInternet;


    AlertDialog.Builder builderCargando;

    AlertDialog modalCargando;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crud_usuarios, container, false);

        botonAgregarActividad = view.findViewById(R.id.botonAgregarActividad);
        recyclerViewUsuarios = view.findViewById(R.id.recyclerViewUsuarios);
        editTextBusqueda = view.findViewById(R.id.searchEditTextArrastres);

        LayoutContenido = view.findViewById(R.id.LayoutContenido);
        SinActividades = view.findViewById(R.id.SinActividades);
        LayoutSinInternet = view.findViewById(R.id.LayoutSinInternet);

        context = requireContext();
        url = context.getResources().getString(R.string.urlApi);


        builderCargando = new AlertDialog.Builder(view.getContext());
        builderCargando.setCancelable(false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        /*
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(context));
        adaptadorUsuarios = new AdaptadorUsuarios(dataList, context, this);
        recyclerViewUsuarios.setAdapter(adaptadorUsuarios);
        */

        GridLayoutManager gridLayoutManagerDelantero = new GridLayoutManager(context, 2);
        recyclerViewUsuarios.setLayoutManager(gridLayoutManagerDelantero);
        adaptadorUsuarios = new AdaptadorUsuarios(dataList, context, this);
        recyclerViewUsuarios.setAdapter(adaptadorUsuarios);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        String ID_usuario = sharedPreferences.getString("ID_usuario", "");


        MostrarUsuarios();

        editTextBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adaptadorUsuarios.filter(s.toString().toLowerCase());
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        botonAgregarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                // Crear el AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Inflar el diseño personalizado para el AlertDialog
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.insertar_nuevo_usuario, null);
                builder.setView(dialogView);

                AlertDialog dialog = builder.create();
                dialog.show();
                */


                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                View customView = LayoutInflater.from(context).inflate(R.layout.insertar_nuevo_usuario, null);
                builder.setView(ModalRedondeado(view.getContext(), customView));
                AlertDialog dialogActividades = builder.create();
                dialogActividades.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogActividades.show();

                EditText textViewNombreUsuario = customView.findViewById(R.id.textViewNombreUsuario);
                EditText textViewCorreoUsuario = customView.findViewById(R.id.textViewCorreoUsuario);
                EditText TextViewClaveUsuario = customView.findViewById(R.id.TextViewClaveUsuario);
                EditText TextViewTelefonoUsuario = customView.findViewById(R.id.TextViewTelefonoUsuario);
                Spinner spinnerRolUsuario = customView.findViewById(R.id.spinnerRolUsuario);


                Button botonCancelar = customView.findViewById(R.id.botonCancelar);
                Button botonAgregarCliente = customView.findViewById(R.id.botonAgregarCliente);


                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                        R.array.opciones_rol, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Establece el ArrayAdapter en el Spinner
                spinnerRolUsuario.setAdapter(adapter);


                botonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogActividades.dismiss();
                    }
                });


                botonAgregarCliente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nombreUsuario = textViewNombreUsuario.getText().toString();
                        String correoUsuario = textViewCorreoUsuario.getText().toString();
                        String ClaveUsuario = TextViewClaveUsuario.getText().toString();
                        String TelefonoUsuario = TextViewTelefonoUsuario.getText().toString().replaceAll(" ", "");
                        String RolUsuario = spinnerRolUsuario.getSelectedItem().toString();

                        if (nombreUsuario.isEmpty() || correoUsuario.isEmpty() || ClaveUsuario.isEmpty() || TelefonoUsuario.isEmpty()) {

                            Utils.crearToastPersonalizado(context, "Tienes campos vacios, por favor rellenalos");


                        } else {

                            AgregarNuevoUsuario(nombreUsuario, correoUsuario, ClaveUsuario, TelefonoUsuario, RolUsuario, dialogActividades);
                        }
                    }
                });

                /*
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nombreUsuario = textViewNombreUsuario.getText().toString();
                        String correoUsuario = textViewCorreoUsuario.getText().toString();
                        String ClaveUsuario = TextViewClaveUsuario.getText().toString();
                        String TelefonoUsuario = TextViewTelefonoUsuario.getText().toString().replaceAll(" ", "");
                        String RolUsuario = spinnerRolUsuario.getSelectedItem().toString();

                        if (nombreUsuario.isEmpty() || correoUsuario.isEmpty() || ClaveUsuario.isEmpty() || TelefonoUsuario.isEmpty()) {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "No puedes ingresar campos vacios", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            AgregarNuevoUsuario(nombreUsuario, correoUsuario, ClaveUsuario, TelefonoUsuario, RolUsuario);
                        }
                    }
                });
             */


            }

        });

    }


    private void MostrarUsuarios() {
        dataList.clear();
        modalCargando = Utils.ModalCargando(context, builderCargando);
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dataList.add(jsonObject); // Agrega cada objeto JSON a la lista
                    }
                    adaptadorUsuarios.notifyDataSetChanged();
                    adaptadorUsuarios.setFilteredData(dataList);
                    adaptadorUsuarios.filter("");

                    if (dataList.size() > 0) {
                        mostrarLayouts("conContenido");
                    } else {

                        mostrarLayouts("SinContenido");
                    }

                } catch (JSONException e) {

                    mostrarLayouts("SinContenido");


                }
                editTextBusqueda.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mostrarLayouts("SinInternet");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "13");
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }

    private void mostrarLayouts(String estado) {
        if (estado.equalsIgnoreCase("SinInternet")) {
            LayoutContenido.setVisibility(View.GONE);
            SinActividades.setVisibility(View.GONE);
            LayoutSinInternet.setVisibility(View.VISIBLE);

        } else if (estado.equalsIgnoreCase("SinContenido")) {

            LayoutContenido.setVisibility(View.GONE);
            SinActividades.setVisibility(View.VISIBLE);
            LayoutSinInternet.setVisibility(View.GONE);
        } else {

            LayoutContenido.setVisibility(View.VISIBLE);
            SinActividades.setVisibility(View.GONE);
            LayoutSinInternet.setVisibility(View.GONE);
        }

        onLoadComplete();
    }

    private void onLoadComplete() {
        if (modalCargando.isShowing() && modalCargando != null) {
            modalCargando.dismiss();
        }
    }


    private void AgregarNuevoUsuario(String nombreUsuario, String correoUsuario, String claveUsuario, String telefonoUsuario, String permisos, AlertDialog dialogActividades) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("Error: El correo, nombre o teléfono ya existen en la base de datos.")) {

                    Utils.crearToastPersonalizado(context, "No puedes insertar Datos repetidos");

                } else {
                    Utils.crearToastPersonalizado(context, "Insertado Correctamente");
                    dialogActividades.dismiss();
                    MostrarUsuarios();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "No se agrego, revisa la conexión");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "14");
                params.put("permisos", permisos);
                params.put("nombre", nombreUsuario);
                params.put("correo", correoUsuario);
                params.put("clave", claveUsuario);
                params.put("telefono", telefonoUsuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    @Override
    public void onEditarUsuarioActivity(String ID_usuario, String nombreUsuario, String correoUsuario, String claveUsuario, String telefonoUsuario, String rolUsuario) {
        EditarUsuario(ID_usuario, nombreUsuario, correoUsuario, claveUsuario, telefonoUsuario, rolUsuario);
    }


    @Override
    public void onEliminarUsuarioActivity(String ID_usuario, String nombre) {
        EliminarUsuario(ID_usuario, nombre);
    }


    private void EliminarUsuario(String ID_usuario, String nombre) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MostrarUsuarios();

                Utils.crearToastPersonalizado(context, "Se eliminó a " + nombre);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.crearToastPersonalizado(context, "No se pudo eliminó a " + nombre + " revisa la conexión");

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


    private void EditarUsuario(String ID_usuario, String nombreUsuario, String correoUsuario, String claveUsuario, String telefonoUsuario, String rolUsuario) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MostrarUsuarios();
                Utils.crearToastPersonalizado(context, "Se actualizo a " + nombreUsuario);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.crearToastPersonalizado(context, "No se pudo actualizar a " + nombreUsuario + " revisa la conexión");
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

}