package com.bitala.bitacora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bitala.bitacora.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SubirFotoUsuarioActivity extends AppCompatActivity {

    private String url;

    ImageView IMGFotoPerfil;

    String rutaImagen;
    String ID_usuario, nombre;
    Context context;
/*
    private CameraManager cameraManager;
    Bitmap bitmapDesdeGaleria;
    int PICK_IMAGE_REQUEST = 2;
    String imageKey = "fotoImagen";

    String nombreImagenKey = "nombreFoto";
*/

    ImageView imagenDesdeGaleriaIM;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = this;
        url = context.getResources().getString(R.string.urlApi);

        setContentView(R.layout.activity_subir_foto_usuario);

        Button btnGuardarFoto = findViewById(R.id.guardarFoto);
        TextView txtId = findViewById(R.id.txtId);
        Button fotoDesdeGaleria = findViewById(R.id.fotoDesdeGaleria);
        IMGFotoPerfil = findViewById(R.id.IMGFotoPerfil);
        imagenDesdeGaleriaIM = findViewById(R.id.imagenDesdeGaleriaIM);


        Bundle receivedBundle = getIntent().getExtras();

        if (receivedBundle != null) {
            ID_usuario = receivedBundle.getString("ID_usuario");
            nombre = receivedBundle.getString("nombre");

            txtId.setText("Actualizando a " + ID_usuario + " " + nombre);

            String imageUrl = "http://hidalgo.no-ip.info:5610/bitacora/fotos/fotos_usuarios/fotoperfilusuario" + ID_usuario + ".jpg";

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.imagendefault)
                    .error(R.drawable.imagendefault)
                    .skipMemoryCache(true) // Desactiva la caché en memoria
                    .diskCacheStrategy(DiskCacheStrategy.NONE);

            Glide.with(this)
                    .load(imageUrl)
                    .apply(options)
                    .into(IMGFotoPerfil);

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
                Uri fotoUri = FileProvider.getUriForFile(SubirFotoUsuarioActivity.this, "com.bitala.bitacora.fileprovider", imagenArchivo);
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

        new SubirFotoUsuarioActivity.SendImageTask().execute(imageBitmap);
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
                    .addFormDataPart("opcion", "25")
                    .addFormDataPart("ID_usuario", ID_usuario)
                    .addFormDataPart("imagen23", nombreArchivo,
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
            Utils.crearToastPersonalizado(context, "Se actualizó la imagen de " + nombre);
            Intent intent = new Intent(SubirFotoUsuarioActivity.this, Activity_Binding.class);
            startActivity(intent);
            finish();
        }
    }


}