package com.bitala.bitacora.Adaptadores;


import static androidx.core.content.ContextCompat.startActivity;

import static com.bitala.bitacora.Utils.crearToastPersonalizado;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DownloadFileTask extends AsyncTask<String, Void, Void> {

    public DownloadFileTask(Map<String, String> postData) {
        this.postData = postData;
    }

    private Map<String, String> postData;
    private Context context;

    public DownloadFileTask(Context context, Map<String, String> postData) {
        this.context = context;
        this.postData = postData;
    }

    protected Void doInBackground(String... params) {
        String fileUrl = params[0];
        String fileName = "documento.pdf"; // Nombre del archivo local

        try {
            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Configurar la conexión para la solicitud POST
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            OutputStream os = urlConnection.getOutputStream();
            os.write(getPostDataString(postData).getBytes("UTF-8"));
            os.flush();
            os.close();

            urlConnection.connect();

            // Crea el directorio de descarga si no existe
            //     String folderPath = Environment.getExternalStorageDirectory().toString();
            String folderPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
            java.io.File folder = new java.io.File(folderPath, "Download");
            if (!folder.exists()) {
                folder.mkdir();
            }

            // Crea el archivo local
            java.io.File file = new java.io.File(folder, fileName);
            file.createNewFile();

            // Descarga el archivo y guarda en el almacenamiento local
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();

            byte[] buffer = new byte[1024];
            int bufferLength;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bufferLength);
            }

            fileOutputStream.close();
            Log.d("DownloadFileTask", "Archivo descargado con éxito en " + file.getAbsolutePath());

            // Obtener la URI del archivo
            Uri fileUri = FileProvider.getUriForFile(context, "com.bitala.bitacora.fileprovider", file);


            /*
            // Compartir el archivo usando un Intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

             */


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(context, intent, null);
            } catch (ActivityNotFoundException e) {
                crearToastPersonalizado(context, "No se encontró una aplicación para abrir PDF");

            }


            //  context.startActivity(Intent.createChooser(shareIntent, "Compartir PDF"));

        } catch (IOException e) {
            Log.e("DownloadFileTask", "Error al descargar el archivo: " + e.getMessage());
        }

        return null;

    }

    private String getPostDataString(Map<String, String> params) throws IOException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        return result.toString();
    }
}

