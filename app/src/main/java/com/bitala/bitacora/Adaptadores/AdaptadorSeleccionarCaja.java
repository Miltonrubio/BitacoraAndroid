package com.bitala.bitacora.Adaptadores;


import static com.bitala.bitacora.Utils.ModalRedondeado;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.R;
import com.bitala.bitacora.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdaptadorSeleccionarCaja extends RecyclerView.Adapter<AdaptadorSeleccionarCaja.ViewHolder> {

    private Context context;

    private List<JSONObject> filteredData;
    private List<JSONObject> data;

    String url;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seleccion_caja, parent, false);

        return new ViewHolder(view);
    }


    String claveSesionIniciada;

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            JSONObject jsonObject2 = filteredData.get(position);

            String ID_saldo = jsonObject2.optString("ID_saldo", "");
            String tipo_caja = jsonObject2.optString("tipo_caja");
            String saldo_restante = jsonObject2.optString("saldo_restante", "");


            if (tipo_caja.equalsIgnoreCase("Capital")) {

                holder.ContenedorSeleccion.setBackgroundResource(R.drawable.rounded_azulito);
                holder.TVtipoCaja.setTextColor(ContextCompat.getColor(context, R.color.azulito));
                int colorIcono = ContextCompat.getColor(context, R.color.azulito);
                holder.iconoCaja.setColorFilter(colorIcono);
            } else {

                holder.ContenedorSeleccion.setBackgroundResource(R.drawable.rounded_verde);
                holder.TVtipoCaja.setTextColor(ContextCompat.getColor(context, R.color.verdefuerte));
                int colorIcono = ContextCompat.getColor(context, R.color.verdefuerte);
                holder.iconoCaja.setColorFilter(colorIcono);
            }


            holder.TVtipoCaja.setText("CAJA " + tipo_caja.toUpperCase());

            holder.TVSaldo.setText("Saldo restante: " + saldo_restante + " $");

            holder.ContenedorSeleccion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View customView = LayoutInflater.from(context).inflate(R.layout.modal_agregar_monto, null);
                    builder.setView(ModalRedondeado(context, customView));
                    AlertDialog dialogAgregarMonto = builder.create();
                    dialogAgregarMonto.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogAgregarMonto.show();

                    TextView titulo = customView.findViewById(R.id.titulo);
                    titulo.setText("ASIGNAR MONTO A ACTIVIDAD: \n" + tituloActividad.toUpperCase());

                    EditText EditTextMonto = customView.findViewById(R.id.EditTextMonto);
                    Button btnCancelar = customView.findViewById(R.id.btnCancelar);
                    Button btnAceptar = customView.findViewById(R.id.btnAceptar);


                    btnAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String montoIngresado = EditTextMonto.getText().toString();

                            if (montoIngresado.isEmpty() || montoIngresado.equals("0")) {

                                Utils.crearToastPersonalizado(context, "Debes ingresar un monto");

                            } else {

                                try {
                                    double monto = Double.parseDouble(montoIngresado);
                                    double saldoRestante = Double.parseDouble(saldo_restante);

                                    if (monto > saldoRestante) {

                                        Utils.crearToastPersonalizado(context, "El monto no puede ser mayor a tu saldo");
                                    } else {

                                        dialogAgregarMonto.dismiss();
                                        dialogSeleccionarCaja.dismiss();
                                        dialogSeleccionTipoFin.dismiss();
                                        dialogOpcionesDeActividad.dismiss();
                                        actionListener.onAsignarGastoAActividad(ID_actividad, ID_saldo, montoIngresado);
                                    }

                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    });


                    btnCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogAgregarMonto.dismiss();
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
        TextView TVSaldo;
        TextView TVtipoCaja;
        ImageView iconoCaja;
        ConstraintLayout ContenedorSeleccion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TVSaldo = itemView.findViewById(R.id.TVSaldo);
            ContenedorSeleccion = itemView.findViewById(R.id.ContenedorSeleccion);
            TVtipoCaja = itemView.findViewById(R.id.TVtipoCaja);
            iconoCaja = itemView.findViewById(R.id.iconoCaja);
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
        notifyDataSetChanged();
    }


    public void setFilteredData(List<JSONObject> filteredData) {
        this.filteredData = new ArrayList<>(filteredData);
        notifyDataSetChanged();
    }


    public interface OnActivityActionListener {
        void onAsignarGastoAActividad(String ID_actividad, String ID_saldo, String dinero_gastado);
    }

    private AdaptadorSeleccionarCaja.OnActivityActionListener actionListener;

    String ID_actividad;
    AlertDialog dialogSeleccionarCaja;
    AlertDialog dialogSeleccionTipoFin;
    AlertDialog dialogOpcionesDeActividad;
    String tituloActividad;

    public AdaptadorSeleccionarCaja(List<JSONObject> data, Context context, String ID_actividad, String tituloActividad, AdaptadorSeleccionarCaja.OnActivityActionListener actionListener, AlertDialog dialogSeleccionarCaja, AlertDialog dialogSeleccionTipoFin, AlertDialog dialogOpcionesDeActividad) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        url = context.getResources().getString(R.string.urlApi);

        SharedPreferences sharedPreferences = context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        this.claveSesionIniciada = sharedPreferences.getString("clave", "");
        this.actionListener = actionListener;
        //   this.ID_usuarioActual = sharedPreferences.getString("ID_usuario", "");
        this.ID_actividad = ID_actividad;
        this.dialogSeleccionarCaja = dialogSeleccionarCaja;
        this.dialogSeleccionTipoFin = dialogSeleccionTipoFin;
        this.dialogOpcionesDeActividad = dialogOpcionesDeActividad;
        this.tituloActividad = tituloActividad;
    }

}

