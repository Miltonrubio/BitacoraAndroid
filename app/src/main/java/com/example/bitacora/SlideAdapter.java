
package com.example.bitacora;


import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideViewHolder> {


    private List<SlideItem> slideItems;
    private ViewPager2 viewPager2;

    SlideAdapter(List<SlideItem> slideItems, ViewPager2 viewPager2) {
        this.slideItems = slideItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // return new SlideViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coches_container, parent, false));
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fotos_actividades, parent, false);
        SlideViewHolder viewHolder = new SlideViewHolder(view);


        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        holder.setImage(slideItems.get(position));

        if (position == slideItems.size() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return slideItems.size();
    }

    class SlideViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;

        SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        void setImage(SlideItem slideItem) {
            String fotoSlide = slideItem.getImage();

            if (!TextUtils.isEmpty(fotoSlide)) {
                Glide.with(itemView.getContext())
                        .load(fotoSlide)
                        .error(R.drawable.actividades_default)
                        .into(imageView);
            } else {
                Glide.with(itemView.getContext())
                        .load(R.drawable.actividades_default)
                        .into(imageView);
            }
        }

    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            slideItems.addAll(slideItems);
            notifyDataSetChanged();
        }
    };


    private void showAlertDialog(Context context, int position, SlideItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        String imageUri = item.getImage(); // URL de la imagen


        builder.setTitle("Confirmación");
        builder.setMessage("¿Quieres elegir esta imagen como principal?");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*
                Uri uri = Uri.parse(imageUri);
                String fileName = uri.getLastPathSegment();
                //        cambiarImagenPrincipal(context, fileName);

                 */
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

/*
    private void cambiarImagenPrincipal(Context context, String foto) {
        String url = "http://tallergeorgio.hopto.org:5611/georgioapp/georgioapi/Controllers/Apiback.php";

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "Se actualizo la foto", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(context, "Error al actualizar la foto", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "10");
                params.put("idventa", id_ser_Venta);
                params.put("foto", foto);
                return params;
            }
        };
        queue.add(request);

    }

*/

}
