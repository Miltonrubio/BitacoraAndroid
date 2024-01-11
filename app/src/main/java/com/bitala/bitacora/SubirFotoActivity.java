package com.bitala.bitacora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

//import com.android.volley.Request;
// import com.android.volley.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bitala.bitacora.Adaptadores.AdaptadorArchivos;
import com.bitala.bitacora.Adaptadores.AdaptadorListaActividades;
import com.bitala.bitacora.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SubirFotoActivity extends AppCompatActivity implements AdaptadorArchivos.OnActivityActionListener {


    private Handler sliderHandler = new Handler();

    private String url;

    ViewPager2 viewPager2;

    String rutaImagen;
    String ID_actividad, ID_usuario;
    Context context;

/*
    //Para api
    private CameraManager cameraManager;
    Bitmap bitmapDesdeGaleria;
    int PICK_IMAGE_REQUEST = 2;
    String imageKey = "fotoImagen";

    String nombreImagenKey = "nombreFoto";
*/

    LottieAnimationView lottieSinEvidencias;
    TextView textSinEvidencias;
    String descripcionActividad;
    RecyclerView RecyclerViewArchivos;
    TextView archivosEvidencias;
    AdaptadorArchivos adaptadorArchivos;
    List<JSONObject> listaArchivos = new ArrayList<>();
    AlertDialog modalCargando;
    AlertDialog.Builder builderCargando;
    String fotoUrl;

    List<SlideItem> slideItems = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_subir_foto);

        context = this;
        url = context.getResources().getString(R.string.urlApi);
        fotoUrl = context.getResources().getString(R.string.fotoUrl);
        Button btnGuardarFoto = findViewById(R.id.guardarFoto);
        TextView txtId = findViewById(R.id.txtId);
        TextView txtDesc = findViewById(R.id.txtDesc);
        Button fotoDesdeGaleria = findViewById(R.id.fotoDesdeGaleria);
        viewPager2 = findViewById(R.id.ViewPagerImagenes);
        lottieSinEvidencias = findViewById(R.id.lottieSinEvidencias);
        textSinEvidencias = findViewById(R.id.textSinEvidencias);

        RecyclerViewArchivos = findViewById(R.id.RecyclerViewArchivos);

        archivosEvidencias = findViewById(R.id.archivosEvidencias);

        Button btnDocumentos = findViewById(R.id.btnDocumentos);

        builderCargando = new AlertDialog.Builder(context);
        builderCargando.setCancelable(false);

        // Intent intent = getIntent();

        Bundle receivedBundle = getIntent().getExtras();

        if (receivedBundle != null) {
            ID_actividad = receivedBundle.getString("ID_actividad");
            ID_usuario = receivedBundle.getString("ID_usuario");
            String nombre_actividad = receivedBundle.getString("nombre_actividad");
            descripcionActividad = receivedBundle.getString("descripcionActividad");

            txtId.setText("Evidencias de actividad: \n" + nombre_actividad.toUpperCase());
            txtDesc.setText(descripcionActividad);

            CargarImagenes();
            MostrarArchivos();


            adaptadorArchivos = new AdaptadorArchivos(listaArchivos, context, this);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
            RecyclerViewArchivos.setLayoutManager(gridLayoutManager);
            RecyclerViewArchivos.setAdapter(adaptadorArchivos);

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int totalItems = RecyclerViewArchivos.getAdapter().getItemCount();

                    // Si hay un único elemento, ocupa todo el contenedor
                    if (totalItems == 1) {
                        return 2;
                    } else {
                        // Si el total de elementos es par, muestra 2 elementos por fila
                        // Si es impar, el último elemento ocupa todo el contenedor
                        return (totalItems % 2 == 0 || position != totalItems - 1) ? 1 : 2;
                    }
                }
            });


            /*
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int totalItems = RecyclerViewArchivos.getAdapter().getItemCount();

                    // Si hay un único elemento, ocupa todo el contenedor
                    if (totalItems == 1) {
                        return 2;
                    } else {
                        return (position % 3 == 2) ? 2 : 1;
                    }
                }
            });
*/

        }


        fotoDesdeGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirGaleria();
            }
        });

        btnGuardarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AbrirCamara();

            }
        });


        btnDocumentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirDocumentos();
            }
        });


    }


    private void MostrarArchivos() {
        modalCargando = Utils.ModalCargando(context, builderCargando);
        listaArchivos.clear();
        StringRequest stringRequest3 = new StringRequest(com.android.volley.Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject archivosObj = jsonArray.getJSONObject(i);

                                listaArchivos.add(archivosObj);
                            }

                            if (listaArchivos.size() > 0) {
                                archivosEvidencias.setVisibility(View.VISIBLE);
                                RecyclerViewArchivos.setVisibility(View.VISIBLE);
                            } else {

                                archivosEvidencias.setVisibility(View.GONE);
                                RecyclerViewArchivos.setVisibility(View.GONE);
                            }


                            adaptadorArchivos.notifyDataSetChanged();
                            adaptadorArchivos.setFilteredData(listaArchivos);
                            adaptadorArchivos.filter("");


                        } catch (JSONException e) {

                            archivosEvidencias.setVisibility(View.GONE);
                            RecyclerViewArchivos.setVisibility(View.GONE);
                        }
                        modalCargando.dismiss();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        Utils.crearToastPersonalizado(context, "No se pudieron cargar los archivos");
                        modalCargando.dismiss();
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("opcion", "67");
                params.put("ID_actividad", ID_actividad);
                return params;
            }
        };

        RequestQueue requestQueue3 = Volley.newRequestQueue(context);
        requestQueue3.add(stringRequest3);

    }


    private void AbrirDocumentos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 4);
    }


    private void MandarPDF(Uri pdfUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(pdfUri);
            byte[] pdfBytes = new byte[inputStream.available()];
            inputStream.read(pdfBytes);
            new SendFileTask().execute(pdfBytes, "documento.pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class SendFileTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            byte[] fileBytes = (byte[]) objects[0];
            String fileName = (String) objects[1];

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("opcion", "66")
                    .addFormDataPart("ID_actividad", ID_actividad)
                    .addFormDataPart("ID_usuario", ID_usuario)
                    .addFormDataPart("archivo", fileName,
                            RequestBody.create(MediaType.parse("multipart/form-data"), fileBytes))
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("Respuesta del servidor", responseData);
                } else {
                    Log.e("Error en la solicitud", String.valueOf(response.code()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            modalCargando.dismiss();
            Utils.crearToastPersonalizado(context, "Archivo enviado al servidor");

            MostrarArchivos();

            /*
            Intent intent = new Intent(context, Activity_Binding.class);
            startActivity(intent);

             */
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            modalCargando = Utils.ModalCargando(context, builderCargando);
            Uri selectedImageUri = data.getData();

            try {
                Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                MandarFoto2(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                modalCargando.dismiss();
            }
        }

        if (requestCode == 1 && resultCode == RESULT_OK) {

            modalCargando = Utils.ModalCargando(context, builderCargando);
            Bitmap imgBitmap = BitmapFactory.decodeFile(rutaImagen);
            MandarFoto2(imgBitmap);
        }

/*
        if (requestCode == 4 && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();

            String mimeType = getContentResolver().getType(selectedFileUri);

            if (mimeType != null && mimeType.startsWith("image")) {

                Utils.crearToastPersonalizado(context, "Solo puedes subir archivos pdf");
            } else if (mimeType != null && mimeType.equals("application/pdf")) {

                MandarPDF(selectedFileUri);
            } else {
                Utils.crearToastPersonalizado(context, "Solo puedes subir archivos pdf");
            }
        }
        */

        if (requestCode == 4 && resultCode == RESULT_OK && data != null) {

            modalCargando = Utils.ModalCargando(context, builderCargando);
            Uri selectedFileUri = data.getData();

            String mimeType = getContentResolver().getType(selectedFileUri);

            if (mimeType != null) {
                // Verificar si el tipo MIME es de texto, Word o PDF
                if (mimeType.startsWith("text") || mimeType.equals("application/msword") || mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") || mimeType.equals("application/pdf")) {
                    // Aquí puedes realizar acciones específicas para el tipo de archivo permitido
                    MandarPDF(selectedFileUri);
                } else {
                    Utils.crearToastPersonalizado(context, "Solo puedes subir archivos de texto, Word o PDF");
                    modalCargando.dismiss();
                }
            } else {
                Utils.crearToastPersonalizado(context, "No se pudo determinar el tipo de archivo");
                modalCargando.dismiss();
            }
        }


    }

    private void AbrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File imagenArchivo = null;
            try {
                imagenArchivo = crearImagen();
            } catch (IOException e) {
                Log.e("Error al obtener la imagen", e.toString());
            }
            if (imagenArchivo != null) {
                Uri fotoUri = FileProvider.getUriForFile(SubirFotoActivity.this, "com.bitala.bitacora.fileprovider", imagenArchivo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivityForResult(intent, 1);
            }
        }
    }


    private void AbrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }


    private void MandarFoto2(Bitmap imageBitmap) {
        new SendImageTask().execute(imageBitmap);
    }


    private File crearImagen() throws IOException {
        String nombreFoto = "image";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagenTemporal = File.createTempFile(nombreFoto, ".jpg", directorio);
        rutaImagen = imagenTemporal.getAbsolutePath();
        return imagenTemporal;
    }

    private File bitmapToFile(Bitmap bitmap, String fileName) {
        File file = new File(getCacheDir(), fileName);
        try {
            file.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
            byte[] bitmapData = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private class SendImageTask extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            Bitmap imageBitmap = bitmaps[0];

            OkHttpClient client = new OkHttpClient();

            String nombreArchivo = "imagen" + System.currentTimeMillis() + ".jpg";
            File imageFile = bitmapToFile(imageBitmap, "image.jpg");

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("opcion", "9")
                    .addFormDataPart("ID_actividad", ID_actividad)
                    .addFormDataPart("ID_usuario", ID_usuario)
                    .addFormDataPart("imagen", nombreArchivo,
                            RequestBody.create(MediaType.parse("image/jpeg"), imageFile))
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("Respuesta del servidor", responseData);
                } else {
                    Log.e("Error en la solicitud", String.valueOf(response.code()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            modalCargando.dismiss();
            Utils.crearToastPersonalizado(context, "Evidencia de " + descripcionActividad + " Enviada al servidor");
            /*
            Intent intent = new Intent(context, Activity_Binding.class);
            startActivity(intent);
        */
            CargarImagenes();
        }

    }

    private void CargarImagenes() {
        slideItems.clear();
        StringRequest stringRequest3 = new StringRequest(com.android.volley.Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (!TextUtils.isEmpty(response)) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject fotoObj = jsonArray.getJSONObject(i);
                                    String nombreFoto = fotoObj.getString("nombreFoto");

                                    String urlFotos = fotoUrl;

                                    slideItems.add(new SlideItem(urlFotos + nombreFoto));
                                }
                                viewPager2.setAdapter(new SlideAdapter(slideItems, viewPager2));
                                viewPager2.setClipToPadding(false);
                                viewPager2.setClipChildren(false);
                                viewPager2.setOffscreenPageLimit(4);
                                viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
                                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                                compositePageTransformer.addTransformer(new MarginPageTransformer(10));
                                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                                    @Override
                                    public void transformPage(@NonNull View page, float position) {
                                        float r = 1 - Math.abs(position);
                                        page.setScaleY(0.85f + 0.15f);
                                    }
                                });

                                viewPager2.setPageTransformer(compositePageTransformer);
                                viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                    public void onPageSelected(int position) {
                                        super.onPageSelected(position);
                                        sliderHandler.removeCallbacks(sliderRunnable);
                                        sliderHandler.postDelayed(sliderRunnable, 3000);
                                    }
                                });

                                if (slideItems.size() > 0) {

                                    verDatos("conContenido");
                                } else {

                                    verDatos("Oculto");
                                }


                            } catch (JSONException e) {
                                verDatos("Oculto");
                            }
                        } else {
                            verDatos("Oculto");
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        verDatos("Oculto");
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("opcion", "10");
                params.put("ID_actividad", ID_actividad);
                return params;
            }
        };

        RequestQueue requestQueue3 = Volley.newRequestQueue(context);
        requestQueue3.add(stringRequest3);

    }


    private void verDatos(String estado) {
        if (estado.equalsIgnoreCase("Oculto")) {

            viewPager2.setVisibility(View.GONE);
            textSinEvidencias.setVisibility(View.VISIBLE);
            lottieSinEvidencias.setVisibility(View.VISIBLE);
        } else {

            viewPager2.setVisibility(View.VISIBLE);
            textSinEvidencias.setVisibility(View.GONE);
            lottieSinEvidencias.setVisibility(View.GONE);
        }
    }


    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };


    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }


    @Override
    public void onEliminarArchivo(String ID_archivo, String nombreArchivo) {
        StringRequest stringRequest3 = new StringRequest(com.android.volley.Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Utils.crearToastPersonalizado(context, "Se eliminó el archivo " + nombreArchivo);
                        MostrarArchivos();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.crearToastPersonalizado(context, "No se pudo eliminar el archivo, revisa la conexión");
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("opcion", "68");
                params.put("ID_archivo", ID_archivo);
                return params;
            }
        };

        RequestQueue requestQueue3 = Volley.newRequestQueue(context);
        requestQueue3.add(stringRequest3);
    }


    @Override
    public void onEditarArchivo(String ID_archivo, String nuevoNombreArchivo) {

        StringRequest stringRequest3 = new StringRequest(com.android.volley.Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Utils.crearToastPersonalizado(context, "Se actualizo el archivo " + nuevoNombreArchivo);
                        MostrarArchivos();

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.crearToastPersonalizado(context, "No se pudó editar el archivo, revisa la conexión");
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("opcion", "69");
                params.put("ID_archivo", ID_archivo);
                params.put("nuevoNombreArchivo", nuevoNombreArchivo);
                return params;
            }
        };

        RequestQueue requestQueue3 = Volley.newRequestQueue(context);
        requestQueue3.add(stringRequest3);
    }

}
