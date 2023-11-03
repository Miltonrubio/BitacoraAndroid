package com.example.bitacora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SubirFotoActivity extends AppCompatActivity {


    private Handler sliderHandler = new Handler();

    private String urlApi = "http://hidalgo.no-ip.info:5610/bitacora/mostrar.php";

    ViewPager2 viewPager2;
    private CameraManager cameraManager;

    String rutaImagen;
    String idActividad, ID_usuario;
    Context context;


    //Para api
    Bitmap bitmapDesdeGaleria;
    int PICK_IMAGE_REQUEST = 2;
    String imageKey = "fotoImagen";

    String nombreImagenKey = "nombreFoto";


    LottieAnimationView lottieSinEvidencias;
    TextView textSinEvidencias;
    String descripcionActividad;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = this;
        setContentView(R.layout.activity_subir_foto);

        Button btnGuardarFoto = findViewById(R.id.guardarFoto);
        TextView txtId = findViewById(R.id.txtId);
        TextView txtDesc = findViewById(R.id.txtDesc);
        Button fotoDesdeGaleria = findViewById(R.id.fotoDesdeGaleria);
        viewPager2 = findViewById(R.id.ViewPagerImagenes);
        lottieSinEvidencias = findViewById(R.id.lottieSinEvidencias);
        textSinEvidencias = findViewById(R.id.textSinEvidencias);


        // Intent intent = getIntent();

        Bundle receivedBundle = getIntent().getExtras();

        if (receivedBundle != null) {
            idActividad = receivedBundle.getString("ID_actividad");
            ID_usuario = receivedBundle.getString("ID_usuario");
            String nombre_actividad = receivedBundle.getString("nombre_actividad");
            descripcionActividad = receivedBundle.getString("descripcionActividad");

            txtId.setText("Estas actualizando la actividad: " + nombre_actividad);
            txtDesc.setText(descripcionActividad);

            CargarImagenes();
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                MandarFoto2(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap imgBitmap = BitmapFactory.decodeFile(rutaImagen);
            MandarFoto2(imgBitmap);
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
                Uri fotoUri = FileProvider.getUriForFile(SubirFotoActivity.this, "com.example.bitacora.fileprovider", imagenArchivo);
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
            String url = "http://hidalgo.no-ip.info:5610/bitacora/mostrar.php";

            String nombreArchivo = "imagen" + System.currentTimeMillis() + ".jpg";
            File imageFile = bitmapToFile(imageBitmap, "image.jpg");

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("opcion", "9")
                    .addFormDataPart("ID_actividad", idActividad)
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
            Utils.crearToastPersonalizado(context, "Evidencia de " + descripcionActividad + " Enviada al servidor");
            Intent intent = new Intent(context, Activity_Binding.class);
            startActivity(intent);
        }
    }


    List<SlideItem> slideItems = new ArrayList<>();

    private void CargarImagenes() {
        slideItems.clear();
        StringRequest stringRequest3 = new StringRequest(com.android.volley.Request.Method.POST, urlApi,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (!TextUtils.isEmpty(response)) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject fotoObj = jsonArray.getJSONObject(i);
                                    String nombreFoto = fotoObj.getString("nombreFoto");

                                    String fotoUrl = "http://hidalgo.no-ip.info:5610/bitacora/fotos/";

                                    slideItems.add(new SlideItem(fotoUrl + nombreFoto));
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
                params.put("ID_actividad", idActividad);
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

}
