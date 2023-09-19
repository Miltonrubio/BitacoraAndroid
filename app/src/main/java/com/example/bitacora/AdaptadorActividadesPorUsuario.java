


package com.example.bitacora;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
/*
public class AdaptadorActividadesPorUsuario extends RecyclerView.Adapter<AdaptadorActividadesPorUsuario.ViewHolder>{

    private List<Actividades> listaActividades;

    public AdaptadorActividadesPorUsuario(List<Actividades> listaActividades) {
        this.listaActividades = listaActividades;
    }

    @NonNull
    @Override
    public AdaptadorActividadesPorUsuario.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividades, parent, false);
        return new AdaptadorActividadesPorUsuario.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Actividades actividades = listaActividades.get(position);



        holder.nombreChofer.setText(actividades.getDescripcionActividad());


        String imageUrl = "http://tallergeorgio.hopto.org:5613/tallergeorgio/imagenes/usuarios/"+fotoMecanico;
        if (!TextUtils.isEmpty(fotoMecanico)) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .error(R.drawable.chofer)
                    .into(holder.imageViewChofer);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.chofer)
                    .into(holder.imageViewChofer);
        }

        holder.unidadChofer.setText(choferes.getMarca() + " " +choferes.getModelo());


    }


    @Override
    public int getItemCount() {
        return listaActividades.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView unidadChofer, nombreChofer;

        ImageView imageViewChofer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewChofer = itemView.findViewById(R.id.imageViewChofer);

            nombreChofer = itemView.findViewById(R.id.nombreChofer);
            unidadChofer=  itemView.findViewById(R.id.unidadChofer);
        }
    }


    public void actualizarLista(List<Actividades> nuevaLista) {
        listaActividades.clear();
        listaActividades.addAll(nuevaLista);
        notifyDataSetChanged();
    }
}
*/

