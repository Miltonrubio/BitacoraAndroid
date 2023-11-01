package com.example.bitacora.Adaptadores;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class AdaptadorUbicaciones {

}/* extends RecyclerView.Adapter<AdaptadorUbicaciones.ViewHolder>{

    private OnItemClickListener onItemClickListener;

    private List<Ubicaciones> listaUbicaciones;

    public AdaptadorUbicaciones(List<Ubicaciones> listaUbicaciones) {
        this.listaUbicaciones = listaUbicaciones;
    }

    @NonNull
    @Override
    public AdaptadorUbicaciones.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new AdaptadorUbicaciones.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Ubicaciones ubicaciones = listaUbicaciones.get(position);
        Double latitud_destino = ubicaciones.getLatitud_inicio();
        Double longitud_destino = ubicaciones.getLongitud_inicio();


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });


        Geocoder geocoder = new Geocoder(holder.itemView.getContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitud_destino, longitud_destino, 1);

            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);

                String calle = address.getThoroughfare();
                String numero = address.getSubThoroughfare();
                String colonia = address.getSubLocality();

                StringBuilder direccionBuilder = new StringBuilder();
                if (calle != null) {
                    direccionBuilder.append(calle);
                    if (numero != null) {
                        direccionBuilder.append(" #").append(numero);
                    }
                }
                if (colonia != null) {
                    direccionBuilder.append(", ").append(colonia);
                }

                String direccion = direccionBuilder.toString();

                holder.direccionRuta.setText(direccion);
            } else {
                holder.direccionRuta.setText("No disponible");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return listaUbicaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView direccionRuta;

        ImageView imageViewRuta;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewRuta = itemView.findViewById(R.id.imageViewRuta);

            direccionRuta = itemView.findViewById(R.id.direccionRuta);
        }
    }


    public void actualizarLista(List<Ubicaciones> nuevaLista) {
        listaUbicaciones.clear();
        listaUbicaciones.addAll(nuevaLista);
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


}
*/