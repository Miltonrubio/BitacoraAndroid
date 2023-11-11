package com.bitala.bitacora;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bitala.bitacora.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PruebaActivity extends AppCompatActivity {

    private ImageView imageView;
    ImageView ImageViewImagenDesdeGaleria, CancelarSubida, AceptarSubida;

    private Button selectImageButton;
    Button BtnSubirDesdeGaleria;
    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 2;

    private String selectedImagePath;
    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);

        imageView = findViewById(R.id.imageView);
        selectImageButton = findViewById(R.id.selectImageButton);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PruebaActivity.this);
                builder.setTitle("Selecciona la imagen que deseas subir");

                View customView = LayoutInflater.from(PruebaActivity.this).inflate(R.layout.subirdesdegaleria, null);

                ImageViewImagenDesdeGaleria = customView.findViewById(R.id.ImageViewImagenDesdeGaleria);
                CancelarSubida = customView.findViewById(R.id.CancelarSubida);
                AceptarSubida = customView.findViewById(R.id.AceptarSubida);
                BtnSubirDesdeGaleria = customView.findViewById(R.id.BtnSubirDesdeGaleria);

                builder.setView(customView);
                AlertDialog dialog = builder.create();
                dialog.show();

                BtnSubirDesdeGaleria.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openGallery();
                    }
                });

                AceptarSubida.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedImageUri != null) {
                            MandarImagenDesdeGaleriaAlServidor(selectedImageUri);
                        } else {
                            Toast.makeText(PruebaActivity.this, "No se ha seleccionado ninguna imagen.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                ImageViewImagenDesdeGaleria.setImageBitmap(bitmap);

                BtnSubirDesdeGaleria.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void MandarImagenDesdeGaleriaAlServidor(Uri imageUri) {
        try {
            Log.d("URI de la imagen", imageUri.toString());
            // Obtener el Bitmap de la imagen seleccionada
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            // Convertir el Bitmap a una cadena Base64
            String encodedImage = convertBitmapToBase64(bitmap);

            // Aquí debes realizar la solicitud HTTP para enviar la imagen al servidor
            // Utiliza Retrofit, OkHttp u otra biblioteca para hacer la solicitud POST
            // y enviar la imagen al servidor

            // Ejemplo de cómo hacer una solicitud POST con OkHttp:
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("opcion", "9")
                    .addFormDataPart("ID_actividad", "2")
                    .addFormDataPart("ID_usuario", "1")
                    .addFormDataPart("imagen", "foto.jpg",
                            RequestBody.create(MediaType.parse("image/*"), encodedImage))
                    .build();

            Request request = new Request.Builder()
                    .url("http://192.168.1.113/milton/bitacoraPHP/mostrar.php")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseBody = response.body().string(); // Obtén el cuerpo de la respuesta como cadena

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PruebaActivity.this, responseBody, Toast.LENGTH_SHORT).show();
                                Log.d("RESPUESTA SERVIDOR:", responseBody);

                                // Verifica si la respuesta contiene el mensaje deseado
                                if (responseBody.contains("La imagen se ha subido y los datos se han registrado correctamente en la base de datos.")) {

                                    Toast.makeText(PruebaActivity.this, "Imagen "+bitmap+ " Subida", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(PruebaActivity.this, bitmap+ "No subida", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
