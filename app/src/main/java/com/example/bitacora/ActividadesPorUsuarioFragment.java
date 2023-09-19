package com.example.bitacora;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActividadesPorUsuarioFragment extends Fragment {

    String urlApi = "http://192.168.1.124/android/mostrar.php";
    private AdaptadorActividades adaptadorActividades;
    private List<JSONObject> dataList = new ArrayList<>();

    public ActividadesPorUsuarioFragment() {
        // Required empty public constructor
    }

    public static ActividadesPorUsuarioFragment newInstance(String param1, String param2) {
        ActividadesPorUsuarioFragment fragment = new ActividadesPorUsuarioFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_actividades_por_usuario, container, false);
        RecyclerView rvActividadesUsuario;


        FloatingActionButton fabBotonFlotante = view.findViewById(R.id.fabBotonFlotante);

        rvActividadesUsuario = view.findViewById(R.id.rvActividadesUsuario);
        rvActividadesUsuario.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptadorActividades = new AdaptadorActividades(dataList, requireContext());
        rvActividadesUsuario.setAdapter(adaptadorActividades);

        Bundle bundle = getArguments();
        if (bundle != null) {

            String ID_usuario = bundle.getString("ID_usuario", "");
            String permisos = bundle.getString("permisos", "");
            String nombre = bundle.getString("nombre", "");
            String correo = bundle.getString("correo", "");
            String telefono = bundle.getString("telefono", "");
            String foto_usuario = bundle.getString("foto_usuario", "");


            TextView tvNombreActividad = view.findViewById(R.id.tvNombreActividad);
            ImageView IVFotoDeUsuario = view.findViewById(R.id.IVFotoDeUsuario);
            TextView tvRolDeUsuario = view.findViewById(R.id.tvRolDeUsuario);

            String imagenUrl = "http://192.168.1.124/android/fotos/fotos_usuarios/" + foto_usuario;


            Glide.with(this)
                    .load(imagenUrl)
                    .placeholder(R.drawable.imagendefault) // Imagen por defecto
                    .error(R.drawable.imagendefault) // Imagen por defecto en caso de error
                    .into(IVFotoDeUsuario);

            tvNombreActividad.setText(nombre);
            tvRolDeUsuario.setText(permisos);
            ActividadesPorUsuario(ID_usuario);
        }

        fabBotonFlotante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharePDF();
            }
        });
        return view;
    }

    private void sharePDF() {
        File pdfFile = new File(requireContext().getExternalFilesDir(null), "sample.pdf");
        Uri pdfUri = FileProvider.getUriForFile(requireContext(), "com.example.bitacora.fileprovider", pdfFile);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, pdfUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Compartir PDF"));
    }


    private Boolean checkPermission() {
        int permiso1 = ContextCompat.checkSelfPermission(requireContext().getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permiso2 = ContextCompat.checkSelfPermission(requireContext().getApplicationContext(), READ_EXTERNAL_STORAGE);

        return permiso1 == PackageManager.PERMISSION_GRANTED && permiso2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions((Activity) requireContext(), new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 200);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(requireContext(), "Permiso aceptado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


    private void ActividadesPorUsuario(String ID_usuario) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, urlApi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    dataList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dataList.add(jsonObject); // Agrega cada objeto JSON a la lista
                    }
                    adaptadorActividades.notifyDataSetChanged();
                    adaptadorActividades.setFilteredData(dataList);
                    adaptadorActividades.filter("");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "2");
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);
    }


}