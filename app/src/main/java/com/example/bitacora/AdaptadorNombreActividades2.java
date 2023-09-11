package com.example.bitacora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bitacora.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorNombreActividades2 extends RecyclerView.Adapter<AdaptadorNombreActividades2.ViewHolder> implements Filterable {

    private List<NombreActividad> dataList; // Cambiar NombreActividad al tipo de entidad que uses en tu ViewModel
    private List<NombreActividad> filteredData;
    private Context context;

    public AdaptadorNombreActividades2(Context context) {
        this.context = context;
        this.dataList = new ArrayList<>();
        this.filteredData = new ArrayList<>();
    }

    public void submitList(List<NombreActividad> dataList) {
        this.dataList = dataList;
        this.filteredData = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nombre_actividades, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NombreActividad nombreActividad = filteredData.get(position);

        holder.bind(nombreActividad);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implementa tu lógica para manejar el clic en un elemento del RecyclerView
                // Puedes acceder a los datos de la actividad a través de nombreActividad
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterPattern = constraint.toString().toLowerCase().trim();
                List<NombreActividad> filteredList = new ArrayList<>();

                if (filterPattern.isEmpty()) {
                    filteredList = dataList;
                } else {
                    for (NombreActividad actividad : dataList) {
                        if (actividad.getNombre_actividad().toLowerCase().contains(filterPattern)) {
                            filteredList.add(actividad);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (List<NombreActividad>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView TextNombreDeActividad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TextNombreDeActividad = itemView.findViewById(R.id.TextNombreDeActividad);
        }

        public void bind(NombreActividad nombreActividad) {
            TextNombreDeActividad.setText(nombreActividad.getNombre_actividad());
        }
    }
}
