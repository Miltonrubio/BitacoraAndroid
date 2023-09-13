package com.example.bitacora;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class MainActivity2 extends Activity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = findViewById(R.id.imageView);
    }

    public void pickImageFromGallery(View view) {
        // Intent para abrir la galería de imágenes
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            // Obtiene la URI de la imagen seleccionada
            Uri selectedImageUri = data.getData();

            try {
                // Convierte la URI en un Bitmap y lo muestra en el ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(bitmap);

                // Envia la imagen a la API
                sendImageToAPI(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendImageToAPI(Bitmap bitmap) {
        try {
            String apiUrl = "http://192.168.1.114/milton/bitacoraPHP/mostrar.php";

            // Convierte el Bitmap en bytes
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Crea un cliente OkHttp
            OkHttpClient client = new OkHttpClient();

            // Construye el cuerpo de la solicitud multipart para enviar la imagen
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("opcion", "9")
                    .addFormDataPart("ID_actividad", "2")
                    .addFormDataPart("ID_usuario", "1")
                    .addFormDataPart("imagen", "image.jpg",
                            RequestBody.create(MediaType.parse("image/jpeg"), imageBytes))
                    .build();

            // Construye la solicitud POST
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(requestBody)
                    .build();

            // Ejecuta la solicitud
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                // Procesa la respuesta de la API aquí
                String responseBody = response.body().string();
                // Puedes mostrar o procesar la respuesta según tus necesidades
                // En este ejemplo, simplemente mostraremos la respuesta en un Toast
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity2.this, responseBody, Toast.LENGTH_SHORT).show();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity2.this, "Error al enviar la imagen a la API. Código de respuesta: " + response.code(), Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                Toast.makeText(MainActivity2.this, "Error al enviar la imagen a la API: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
