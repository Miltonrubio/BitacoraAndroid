
package com.example.bitacora;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.appcompat.app.AppCompatActivity;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActividadesPorUsuarioFragment extends Fragment {
    String ID_usuario;
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

            ID_usuario = bundle.getString("ID_usuario", "");
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
                if (checkPermission()) {
                    Toast.makeText(requireContext(), "Permiso aceptado", Toast.LENGTH_SHORT).show();

                    generarPDF(dataList);
                } else {
                    requestPermissions();

                }

            }
        });
        return view;
    }


    String tituloText = "Titulo del documento";

    /*
        private void generarPDF(String responseData) {
            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();
            TextPaint titulo = new TextPaint();
            TextPaint descripcion = new TextPaint();

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(816, 1054, 1).create();
            PdfDocument.Page pagina1 = pdfDocument.startPage(pageInfo);
            Canvas canvas = pagina1.getCanvas();

            Bitmap bitmap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.imagendefault);
            Bitmap bitmapEscala = Bitmap.createScaledBitmap(bitmap, 80, 80, false);

            canvas.drawBitmap(bitmapEscala, 368, 20, paint);

            titulo.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titulo.setTextSize(20);

            descripcion.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            descripcion.setTextSize(14);

            try {
                JSONArray jsonArray = new JSONArray(responseData);

                int y = 200;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // Obtiene los valores de los campos que deseas mostrar en el PDF
                    String nombreActividad = jsonObject.getString("nombre_actividad");
                    String descripcionActividad = jsonObject.getString("descripcionActividad");
                    String fechaInicio = jsonObject.getString("fecha_inicio");
                    String estadoActividad = jsonObject.getString("estadoActividad");

                    // Agrega los datos al PDF
                    canvas.drawText("Nombre de la actividad: " + nombreActividad, 10, y, titulo);
                    y += 25;
                    canvas.drawText("Descripción: " + descripcionActividad, 10, y, descripcion);
                    y += 25;
                    canvas.drawText("Fecha de inicio: " + fechaInicio, 10, y, descripcion);
                    y += 25;
                    canvas.drawText("Estado de la actividad: " + estadoActividad, 10, y, descripcion);
                    y += 40; // Espacio entre actividades
                }

                // Finaliza la página y guarda el PDF
                pdfDocument.finishPage(pagina1);

                // Guarda y comparte el PDF
                File file = new File(requireContext().getExternalFilesDir(null), "Archivo.pdf");
                compartirPDF(file);
                Uri contentUri = FileProvider.getUriForFile(requireContext(), "com.example.bitacora.fileprovider", file);
                pdfDocument.writeTo(requireContext().getContentResolver().openOutputStream(contentUri));
                pdfDocument.close();

                Toast.makeText(requireContext(), "Se creó el PDF correctamente", Toast.LENGTH_SHORT).show();

            } catch (JSONException | IOException e) {
                Toast.makeText(requireContext(), "No se pudo crear el PDF", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    */
    public void generarPDF(List<JSONObject> responseData) {
        Document document = new Document();

        try {
            File pdfFile = new File(requireContext().getExternalFilesDir(null), "ReporteDeActividades.pdf");
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            PageEventHandler eventHandler = new PageEventHandler();
            writer.setPageEvent(eventHandler);

            document.open();

            // Agrega la imagen desde la carpeta drawable
            Drawable drawable = getResources().getDrawable(R.drawable.logotaller);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleToFit(74, 74); // Ajusta la imagen al tamaño de la página
            image.setAlignment(Image.ALIGN_CENTER);

            // Agrega espacio debajo de la imagen
            Paragraph spaceBelowImage = new Paragraph(" "); // Párrafo vacío
            spaceBelowImage.setSpacingAfter(10); // Espacio de 10 unidades (ajusta según tu preferencia)

            // Agrega el título debajo de la imagen y cambia el color a azul
            Paragraph title = new Paragraph("Reporte de actividades de usuario");
            title.setAlignment(Paragraph.ALIGN_CENTER);

            // Agrega espacio debajo del título
            Paragraph spaceBelowTitle = new Paragraph(" "); // Párrafo vacío
            spaceBelowTitle.setSpacingAfter(10); // Espacio de 10 unidades (ajusta según tu preferencia)

            document.add(image);
            document.add(spaceBelowImage);
            document.add(title);
            document.add(spaceBelowTitle);

            JSONArray jsonArray = new JSONArray(responseData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String nombreActividad = jsonObject.getString("nombre_actividad");
                String descripcionActividad = jsonObject.getString("descripcionActividad");
                String fechaInicio = jsonObject.getString("fecha_inicio");
                String estadoActividad = jsonObject.getString("estadoActividad");

                document.add(new Paragraph("Nombre de la actividad: " + nombreActividad));
                document.add(new Paragraph("Descripción: " + descripcionActividad));
                document.add(new Paragraph("Fecha de inicio: " + fechaInicio));
                document.add(new Paragraph("Estado de la actividad: " + estadoActividad));
                document.add(new Paragraph("\n"));
            }

            document.close();
            compartirPDF(pdfFile);

            Toast.makeText(requireContext(), "Se creó el PDF correctamente", Toast.LENGTH_SHORT).show();

        } catch (IOException | DocumentException | JSONException e) {
            Toast.makeText(requireContext(), "No se pudo crear el PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    class PageEventHandler extends PdfPageEventHelper {
        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            // Agrega una nueva página al comienzo de cada página
            document.newPage();
        }
    }

    private void compartirPDF(File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        // Uri uri = FileProvider.getUriForFile(this, "com.example.bitacora.fileprovider", file);
        Uri uri = FileProvider.getUriForFile(requireContext(), "com.example.bitacora.fileprovider", file);

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Compartir archivo PDF");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Compartir PDF"));
    }


    private boolean checkPermission() {
        int permiso1 = ContextCompat.checkSelfPermission(requireActivity(), WRITE_EXTERNAL_STORAGE);
        int permiso2 = ContextCompat.checkSelfPermission(requireActivity(), READ_EXTERNAL_STORAGE);
        return permiso1 == PackageManager.PERMISSION_GRANTED && permiso2 == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {

        ActivityCompat.requestPermissions(requireActivity(), new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 200);

    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantresults) {
        if (requestCode == 200) {
            if (grantresults.length > 0) {
                boolean writeStore = grantresults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantresults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStore && readStorage) {

                    Toast.makeText(requireContext(), "Permiso concedido", Toast.LENGTH_SHORT).show();
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


