package com.example.bitacora;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ApiClient {

    public static void enviarDatos(Context context, String url, String opcion, String ID_actividad, String ID_usuario, String archivo) {
        // Inicializar la cola de solicitudes de Volley
        RequestQueue queue = Volley.newRequestQueue(context);

        // Crear un objeto JSON con los datos que quieres enviar
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("opcion", opcion);
            jsonBody.put("ID_actividad", ID_actividad);
            jsonBody.put("ID_usuario", ID_usuario);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Convertir la imagen en una cadena Base64
        String encodedImage = convertImageToBase64(archivo);

        // Agregar la cadena Base64 al objeto JSON
        try {
            jsonBody.put("archivo", encodedImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Crear la solicitud HTTP POST
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejar la respuesta del servidor
                        Toast.makeText(context, "Solicitud exitosa", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar errores de la solicitud
                        Toast.makeText(context, "Error en la solicitud: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Agregar la solicitud a la cola
        queue.add(request);
    }

    private static String convertImageToBase64(String filePath) {
        File imageFile = new File(filePath);
        if (imageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] byteArrayImage = baos.toByteArray();
            return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        } else {
            return "";
        }
    }
}
