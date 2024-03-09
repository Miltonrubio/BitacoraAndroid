package com.bitala.bitacora.Adaptadores;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.bitala.bitacora.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class NuevoDownloadFileTask extends AsyncTask<String, Void, Void> {

    private Map<String, String> postData;
    private Context context;

    public NuevoDownloadFileTask(Context context, Map<String, String> postData) {
        this.context = context;
        this.postData = postData;

        this.builder = new AlertDialog.Builder(context);
        this.builder.setCancelable(false);


    }


    androidx.appcompat.app.AlertDialog.Builder builder;
    AlertDialog modalCargando;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        modalCargando = Utils.ModalCargando(context, builder);
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

            // Abrir el archivo PDF con la aplicación predeterminada
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Utils.crearToastPersonalizado(context, "No se encontró una aplicación para abrir PDF");
            }

        } catch (IOException e) {
            Log.e("DownloadFileTask", "Error al descargar el archivo: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        modalCargando.dismiss();
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
