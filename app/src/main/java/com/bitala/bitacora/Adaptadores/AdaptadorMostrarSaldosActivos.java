package com.bitala.bitacora.Adaptadores;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.R;
import com.bitala.bitacora.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AdaptadorMostrarSaldosActivos extends RecyclerView.Adapter<AdaptadorMostrarSaldosActivos.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    String url;


    AdaptadorNuevoDesgloseDeGastos adaptadorDesgloseGastos;
    List<JSONObject> listaGastos = new ArrayList<>();
    AdaptadorDepositos adaptadorDepositos;
    List<JSONObject> listaAdiciones = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saldos_activos, parent, false);
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
            String saldo_asignado = jsonObject2.optString("saldo_asignado", "");
            String ID_saldo = jsonObject2.optString("ID_saldo", "");
            String tipo_caja = jsonObject2.optString("tipo_caja", "");
            String saldo_restante = jsonObject2.optString("saldo_restante", "");
            String total_adiciones = jsonObject2.optString("total_adiciones", "");
            String total_consumos = jsonObject2.optString("total_consumos", "");
            String fecha_asignacion_saldo = jsonObject2.optString("fecha_asignacion_saldo", "");
            String hora_asignacion_saldo = jsonObject2.optString("hora_asignacion_saldo", "");
            String desglose_gastos = jsonObject2.optString("desglose_gastos", "");
            String desglose_adiciones = jsonObject2.optString("desglose_adiciones", "");
            String ID_registro_saldo = jsonObject2.optString("ID_registro_saldo", "");


            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdfOutput = new SimpleDateFormat("EEEE dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            SimpleDateFormat sdfInputHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            try {
                Date dateInicio = sdfInput.parse(fecha_asignacion_saldo);
                Date horaInicio = sdfInputHora.parse(hora_asignacion_saldo);
                Date dateTime = new Date(dateInicio.getTime() + horaInicio.getTime());
                String fechaFormateada = sdfOutput.format(dateTime);

                holder.FechaAsignacion.setText("Asignado el " + fechaFormateada);

            } catch (ParseException e) {
                holder.FechaAsignacion.setText("No se encontro la fecha");
            }


            if (total_consumos.equalsIgnoreCase("0")) {
                holder.TextViewtotal_consumos.setVisibility(View.GONE);
            } else {

                holder.TextViewtotal_consumos.setVisibility(View.VISIBLE);
            }

            if (total_adiciones.equalsIgnoreCase("0")) {
                holder.TextViewtotal_adiciones.setVisibility(View.GONE);
            } else {

                holder.TextViewtotal_adiciones.setVisibility(View.VISIBLE);
            }


            if (tipo_caja.equalsIgnoreCase("Capital")) {

                holder.ContenedorSaldoActivo.setBackgroundResource(R.drawable.rounded_amarillo);
                holder.TextViewtipo_caja.setTextColor(ContextCompat.getColor(context, R.color.amarillo));
            } else {

                holder.ContenedorSaldoActivo.setBackgroundResource(R.drawable.roundedbackground_nombre_actividad);
                holder.TextViewtipo_caja.setTextColor(ContextCompat.getColor(context, R.color.naranjita));
            }


            try {
                double saldoAsignadoDouble = Double.parseDouble(saldo_asignado);
                double totalAdicionesDouble = Double.parseDouble(total_adiciones);

                double resultado = saldoAsignadoDouble + totalAdicionesDouble;

                String resultadoComoCadena = String.valueOf(resultado);

                holder.TextViewsaldo_asignado.setText("Saldo total: " + resultadoComoCadena + " $  \nSaldo inicial: " + saldo_asignado + "$");

            } catch (NumberFormatException e) {
                holder.TextViewsaldo_asignado.setText("Saldo inicial: " + saldo_asignado + " $");

            }


            holder.TextViewtotal_adiciones.setText("Total agregado: +" + total_adiciones + " $");
            holder.TextViewtotal_consumos.setText("Total gastado: -" + total_consumos + " $");
            holder.TextViewsaldo_restante.setText("Saldo restante: " + saldo_restante + " $");

            holder.TextViewtipo_caja.setText(tipo_caja.toUpperCase());


            holder.btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View customView = LayoutInflater.from(context).inflate(R.layout.agregar_mas_saldo_confirmacion, null);
                    builder.setView(Utils.ModalRedondeado(context, customView));
                    AlertDialog dialogCorregirSaldo = builder.create();
                    ColorDrawable back = new ColorDrawable(Color.BLACK);
                    back.setAlpha(150);
                    dialogCorregirSaldo.getWindow().setBackgroundDrawable(back);
                    dialogCorregirSaldo.getWindow().setDimAmount(0.8f);
                    dialogCorregirSaldo.show();

                    TextView Labelnuevomonto = customView.findViewById(R.id.Labelnuevomonto);
                    EditText nuevoMonto = customView.findViewById(R.id.nuevoMonto);
                    Labelnuevomonto.setText("Este es tu saldo anterior, agrega el saldo corregido");
                    TextView textView4 = customView.findViewById(R.id.textView4);
                    textView4.setText("CORRIGIENDO EL SALDO DE CAJA " + tipo_caja.toUpperCase());

                    Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                    Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                    EditText editTextClaveUsuario = customView.findViewById(R.id.editTextClaveUsuario);

                    nuevoMonto.setText(saldo_asignado);


                    buttonAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String saldoCorregido = nuevoMonto.getText().toString();
                            String claveIngresada = editTextClaveUsuario.getText().toString();

                            if (saldoCorregido.isEmpty()) {
                                Utils.crearToastPersonalizado(context, "Debes llenar los campos");
                            } else {
                                if (!claveIngresada.equals(claveSesionIniciada) || claveIngresada.isEmpty()) {
                                    Utils.crearToastPersonalizado(context, "Debes ingresar la contraseña correcta");

                                } else {
                                    dialogCorregirSaldo.dismiss();
                                    dialogSaldoAsignado.dismiss();
                                    dialogOpcionesUsuarios.dismiss();

                                    actionListener.onCorregirSaldo(ID_saldo, saldoCorregido, tipo_caja);

                                }
                            }
                        }
                    });


                    buttonCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogCorregirSaldo.dismiss();
                        }
                    });


                }
            });


            holder.butonFinalizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View customView = LayoutInflater.from(context).inflate(R.layout.confirmacion_con_clave, null);
                    builder.setView(Utils.ModalRedondeado(context, customView));
                    AlertDialog dialogConfirmacion = builder.create();
                    ColorDrawable back = new ColorDrawable(Color.BLACK);
                    back.setAlpha(150);
                    dialogConfirmacion.getWindow().setBackgroundDrawable(back);
                    dialogConfirmacion.getWindow().setDimAmount(0.8f);
                    dialogConfirmacion.show();

                    EditText editTextClaveUsuario = customView.findViewById(R.id.editTextClaveUsuario);

                    Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                    Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);


                    buttonAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String claveIngresada = editTextClaveUsuario.getText().toString();

                            if (!claveIngresada.equals(claveSesionIniciada) || claveIngresada.isEmpty()) {
                                Utils.crearToastPersonalizado(context, "Debes ingresar la contraseña correcta");

                            } else {
                                dialogConfirmacion.dismiss();
                                dialogOpcionesUsuarios.dismiss();
                                dialogSaldoAsignado.dismiss();

                                actionListener.onFinalizarUnSaldo(ID_registro_saldo, tipo_caja);
                            }
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


            holder.botonAgregarMasSaldo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View customView = LayoutInflater.from(context).inflate(R.layout.agregar_mas_saldo_confirmacion, null);
                    builder.setView(Utils.ModalRedondeado(context, customView));
                    AlertDialog dialogAgregarSaldo = builder.create();
                    ColorDrawable back = new ColorDrawable(Color.BLACK);
                    back.setAlpha(150);
                    dialogAgregarSaldo.getWindow().setBackgroundDrawable(back);
                    dialogAgregarSaldo.getWindow().setDimAmount(0.8f);
                    dialogAgregarSaldo.show();
/*
                    RadioButton radioGastos = customView.findViewById(R.id.radioGastos);
                    RadioButton radioCapital = customView.findViewById(R.id.radioCapital);
                    radioCapital.setVisibility(View.GONE);
                    radioGastos.setVisibility(View.GONE);

 */


                    TextView textView4 = customView.findViewById(R.id.textView4);
                    textView4.setText("AGREGAR MAS SALDO A CAJA " + tipo_caja.toUpperCase());

                    EditText nuevoMonto = customView.findViewById(R.id.nuevoMonto);
                    EditText editTextClaveUsuario = customView.findViewById(R.id.editTextClaveUsuario);

                    Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                    Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);


                    buttonAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String SaldoAAgregar = nuevoMonto.getText().toString();
                            String ClaveIngresada = editTextClaveUsuario.getText().toString();


                            if (SaldoAAgregar.isEmpty()) {
                                Utils.crearToastPersonalizado(context, "Debes llenar los campos");
                            } else {
                                if (!ClaveIngresada.equals(claveSesionIniciada) || ClaveIngresada.isEmpty()) {
                                    Utils.crearToastPersonalizado(context, "Debes ingresar la contraseña correcta");

                                } else {
                                    dialogAgregarSaldo.dismiss();
                                    dialogSaldoAsignado.dismiss();
                                    dialogOpcionesUsuarios.dismiss();
                                    actionListener.onAgregarMasSaldo(SaldoAAgregar, ID_saldo, ID_usuarioSesionIniciada, tipo_caja);
                                }
                            }
                        }
                    });

                    buttonCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogAgregarSaldo.dismiss();
                        }
                    });


                }
            });


            holder.ContenedorSaldoActivo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View customView = LayoutInflater.from(context).inflate(R.layout.modal_desglose_saldos, null);
                    builder.setView(Utils.ModalRedondeado(context, customView));
                    AlertDialog dialogGestionarSaldo = builder.create();
                    ColorDrawable back = new ColorDrawable(Color.BLACK);
                    back.setAlpha(150);
                    dialogGestionarSaldo.getWindow().setBackgroundDrawable(back);
                    dialogGestionarSaldo.getWindow().setDimAmount(0.8f);
                    dialogGestionarSaldo.show();

                    TextView textView12 = customView.findViewById(R.id.textView12);
                    textView12.setText("Desglose de saldos de Caja: " + tipo_caja);

                    RecyclerView recyclerViewGastos = customView.findViewById(R.id.recyclerViewGastos);
                    RecyclerView recyclerViewAdiciones = customView.findViewById(R.id.recyclerViewAdiciones);
                    LinearLayout layoutAdiciones = customView.findViewById(R.id.layoutAdiciones);
                    LinearLayout LayoutGastos = customView.findViewById(R.id.LayoutGastos);

                    ConstraintLayout ContenedorSinContenido = customView.findViewById(R.id.ContenedorSinContenido);
                    ConstraintLayout ContenedorConContenido = customView.findViewById(R.id.ContenedorConContenido);


                    adaptadorDesgloseGastos = new AdaptadorNuevoDesgloseDeGastos(listaGastos, context, dialogGestionarSaldo, dialogSaldoAsignado, dialogOpcionesUsuarios);
                    recyclerViewGastos.setLayoutManager(new LinearLayoutManager(context));
                    recyclerViewGastos.setAdapter(adaptadorDesgloseGastos);

                    listaGastos.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(desglose_gastos);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            listaGastos.add(jsonObject);
                        }

                        adaptadorDesgloseGastos.notifyDataSetChanged();
                        adaptadorDesgloseGastos.setFilteredData(listaGastos);
                        adaptadorDesgloseGastos.filter("");

                        if (listaGastos.size() > 0) {
                            LayoutGastos.setVisibility(View.VISIBLE);
                        } else {
                            LayoutGastos.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        LayoutGastos.setVisibility(View.GONE);
                    }


                    adaptadorDepositos = new AdaptadorDepositos(listaAdiciones, context);
                    recyclerViewAdiciones.setLayoutManager(new LinearLayoutManager(context));
                    recyclerViewAdiciones.setAdapter(adaptadorDepositos);

                    listaAdiciones.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(desglose_adiciones);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            listaAdiciones.add(jsonObject);
                        }

                        adaptadorDepositos.notifyDataSetChanged();
                        adaptadorDepositos.setFilteredData(listaAdiciones);
                        adaptadorDepositos.filter("");

                        if (listaAdiciones.size() > 0) {
                            layoutAdiciones.setVisibility(View.VISIBLE);
                        } else {
                            layoutAdiciones.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        layoutAdiciones.setVisibility(View.GONE);

                    }

                    if (listaAdiciones.size()>0 ||  listaGastos.size()>0 ){

                        ContenedorConContenido.setVisibility(View.VISIBLE);
                        ContenedorSinContenido.setVisibility(View.GONE);
                    }else{
                        ContenedorConContenido.setVisibility(View.GONE);
                        ContenedorSinContenido.setVisibility(View.VISIBLE);
                    }
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

        TextView TextViewtipo_caja;
        TextView TextViewsaldo_asignado;

        ImageView btnEditar;

        TextView FechaAsignacion;

        Button butonFinalizar;
        ImageView botonAgregarMasSaldo;
        ConstraintLayout ContenedorSaldoActivo;

        TextView TextViewsaldo_restante;
        TextView TextViewtotal_adiciones;
        TextView TextViewtotal_consumos;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TextViewsaldo_restante = itemView.findViewById(R.id.TextViewsaldo_restante);
            TextViewtotal_adiciones = itemView.findViewById(R.id.TextViewtotal_adiciones);
            TextViewtotal_consumos = itemView.findViewById(R.id.TextViewtotal_consumos);

            FechaAsignacion = itemView.findViewById(R.id.FechaAsignacion);
            TextViewtipo_caja = itemView.findViewById(R.id.TextViewtipo_caja);
            TextViewsaldo_asignado = itemView.findViewById(R.id.TextViewsaldo_asignado);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            butonFinalizar = itemView.findViewById(R.id.butonFinalizar);
            ContenedorSaldoActivo = itemView.findViewById(R.id.ContenedorSaldoActivo);
            botonAgregarMasSaldo = itemView.findViewById(R.id.botonAgregarMasSaldo);
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

    AlertDialog dialogSaldoAsignado;
    AlertDialog dialogOpcionesUsuarios;


    public interface OnActivityActionListener {
        void onFinalizarUnSaldo(String ID_registro_saldo, String nuevoTipoCaja);

        void onAgregarMasSaldo(String saldoAgregado, String ID_saldo, String ID_admin_asig, String tipo_caja);

        void onCorregirSaldo(String ID_saldo, String montoCorregido, String tipo_caja);
    }

    private AdaptadorMostrarSaldosActivos.OnActivityActionListener actionListener;

    String claveSesionIniciada;
    String ID_usuarioSesionIniciada;


    public AdaptadorMostrarSaldosActivos(List<JSONObject> data, Context context, AlertDialog dialogSaldoAsignado, AlertDialog dialogOpcionesUsuarios, AdaptadorMostrarSaldosActivos.OnActivityActionListener actionListener) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        url = context.getResources().getString(R.string.urlApi);
        this.dialogSaldoAsignado = dialogSaldoAsignado;
        this.dialogOpcionesUsuarios = dialogOpcionesUsuarios;
        this.actionListener = actionListener;

        SharedPreferences sharedPreferences = context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        this.ID_usuarioSesionIniciada = sharedPreferences.getString("ID_usuario", "");
        this.claveSesionIniciada = sharedPreferences.getString("clave", "");
/*
        builderCargando = new AlertDialog.Builder(context);
        builderCargando.setCancelable(false);

 */
    }


}

