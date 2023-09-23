
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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActividadesPorUsuarioFragment extends Fragment {

    String ID_usuario, permisos, nombre, correo, telefono, foto_usuario;

    String urlApi = "http://192.168.1.113/milton/bitacoraPHP/mostrar.php";


    private AdaptadorActividades adaptadorActividades;
    private List<JSONObject> dataList = new ArrayList<>();

    List<JSONObject> datosDependiendoDeFecha = new ArrayList<>();

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


       ImageView IVFotoDeUsuario= view.findViewById(R.id.IVFotoDeUsuario);


        Bundle bundle = getArguments();
        if (bundle != null) {

            ID_usuario = bundle.getString("ID_usuario", "");
            permisos = bundle.getString("permisos", "");
            nombre = bundle.getString("nombre", "");
            correo = bundle.getString("correo", "");
            telefono = bundle.getString("telefono", "");
            foto_usuario = bundle.getString("foto_usuario", "");


            TextView tvNombreActividad = view.findViewById(R.id.tvNombreActividad);
            TextView tvRolDeUsuario = view.findViewById(R.id.tvRolDeUsuario);

            String imagenUrl = "http://192.168.1.113/milton/bitacoraPHP/fotos/fotos_usuarios/fotoperfilusuario"+ID_usuario+".jpg";


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

                    if (datosDependiendoDeFecha.isEmpty() || datosDependiendoDeFecha.equals(null)) {

                        generarPDF(dataList);
                    } else {

                        generarPDF(datosDependiendoDeFecha);
                    }

                } else {
                    requestPermissions();

                }

            }
        });


        Button btnFiltrarDeHOY = view.findViewById(R.id.btnFiltrarDeHOY);
        Button btnFiltrarPorMes = view.findViewById(R.id.btnFiltrarPorMes);
        Button btnFiltrarDelAnio = view.findViewById(R.id.btnFiltrarDelAnio);


        btnFiltrarPorMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lógica para mostrar los datos del mes actual
                mostrarDatosDelMesActual();
            }
        });


        btnFiltrarDeHOY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lógica para mostrar los datos del mes actual
                mostrarDatosDelDiaDeHoy();
            }
        });

        btnFiltrarDelAnio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lógica para mostrar los datos del mes actual
                mostrarDatosDelAnioActual();
            }
        });

        return view;
    }

    private void mostrarDatosDelDiaDeHoy() {
        reiniciarDatosDependiendoDeFecha(); // Reiniciar la lista
        // Obtiene la fecha actual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Los meses en Calendar van de 0 a 11
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        for (JSONObject jsonObject : dataList) {
            try {
                // Analiza la fecha de inicio de la actividad en el JSON
                String fechaInicio = jsonObject.getString("fecha_inicio");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fecha = sdf.parse(fechaInicio);

                // Verifica si la actividad está en el día de hoy
                Calendar actividadCalendar = Calendar.getInstance();
                actividadCalendar.setTime(fecha);
                int actividadYear = actividadCalendar.get(Calendar.YEAR);
                int actividadMonth = actividadCalendar.get(Calendar.MONTH) + 1;
                int actividadDay = actividadCalendar.get(Calendar.DAY_OF_MONTH);

                if (year == actividadYear && month == actividadMonth && day == actividadDay) {
                    datosDependiendoDeFecha.add(jsonObject);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        // Actualiza la lista de datos en el adaptador y notifica el cambio
        adaptadorActividades.setFilteredData(datosDependiendoDeFecha);
        adaptadorActividades.notifyDataSetChanged();


    }


    private void mostrarDatosDelMesActual() {
        reiniciarDatosDependiendoDeFecha(); // Reiniciar la lista
        // Obtiene la fecha actual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Los meses en Calendar van de 0 a 11

        for (JSONObject jsonObject : dataList) {
            try {
                // Analiza la fecha de inicio de la actividad en el JSON
                String fechaInicio = jsonObject.getString("fecha_inicio");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fecha = sdf.parse(fechaInicio);

                // Verifica si la actividad está en el mes actual
                Calendar actividadCalendar = Calendar.getInstance();
                actividadCalendar.setTime(fecha);
                int actividadYear = actividadCalendar.get(Calendar.YEAR);
                int actividadMonth = actividadCalendar.get(Calendar.MONTH) + 1;

                if (year == actividadYear && month == actividadMonth) {
                    datosDependiendoDeFecha.add(jsonObject);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        // Actualiza la lista de datos en el adaptador y notifica el cambio
        adaptadorActividades.setFilteredData(datosDependiendoDeFecha);
        adaptadorActividades.notifyDataSetChanged();
    }

    private void mostrarDatosDelAnioActual() {
        reiniciarDatosDependiendoDeFecha(); // Reiniciar la lista
        // Obtiene la fecha actual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        for (JSONObject jsonObject : dataList) {
            try {
                // Analiza la fecha de inicio de la actividad en el JSON
                String fechaInicio = jsonObject.getString("fecha_inicio");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fecha = sdf.parse(fechaInicio);

                // Verifica si la actividad está en el año actual
                Calendar actividadCalendar = Calendar.getInstance();
                actividadCalendar.setTime(fecha);
                int actividadYear = actividadCalendar.get(Calendar.YEAR);

                if (year == actividadYear) {
                    datosDependiendoDeFecha.add(jsonObject);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        // Actualiza la lista de datos en el adaptador y notifica el cambio
        adaptadorActividades.setFilteredData(datosDependiendoDeFecha);
        adaptadorActividades.notifyDataSetChanged();
    }


    private void reiniciarDatosDependiendoDeFecha() {
        datosDependiendoDeFecha.clear();
    }


    public void generarPDF(List<JSONObject> responseData) {
        Document document = new Document();

        try {
            File pdfFile = new File(requireContext().getExternalFilesDir(null), "ReporteDeActividades "+ nombre+".pdf");
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            PageEventHandler eventHandler = new PageEventHandler();
            writer.setPageEvent(eventHandler);

            document.open();

            Drawable drawable = getResources().getDrawable(R.drawable.logo);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleToFit(74, 74);
            image.setAlignment(Image.ALIGN_CENTER);

            Paragraph spaceBelowImage = new Paragraph(" ");
            spaceBelowImage.setSpacingAfter(10);

            Paragraph title = new Paragraph("Reporte de actividades de usuario " + nombre);
            title.setAlignment(Paragraph.ALIGN_CENTER);

            Paragraph spaceBelowTitle = new Paragraph(" ");
            spaceBelowTitle.setSpacingAfter(10);

            float cellPadding = 10f;
            float cellPaddingUser = 5f;



            document.add(image);
            document.add(spaceBelowImage);



            // Crear una nueva tabla para la información del usuario
            PdfPTable userInfoTable = new PdfPTable(2);
            userInfoTable.setWidthPercentage(55);

            BaseColor backgroundColor = BaseColor.LIGHT_GRAY;

            PdfPCell headerCell = new PdfPCell(new Paragraph("Información del usuario"));
            headerCell.setBackgroundColor(backgroundColor);
            headerCell.setPadding(cellPadding);
            headerCell.setColspan(2);
            userInfoTable.addCell(headerCell);


            userInfoTable.addCell("ID de usuario:");
            PdfPCell userIdCell = new PdfPCell(new Paragraph(ID_usuario));
            userIdCell.setPadding(cellPaddingUser);
            userInfoTable.addCell(userIdCell);

            userInfoTable.addCell("Permisos:");
            PdfPCell permissionsCell = new PdfPCell(new Paragraph(permisos));
            permissionsCell.setPadding(cellPaddingUser);
            userInfoTable.addCell(permissionsCell);

            userInfoTable.addCell("Nombre:");
            PdfPCell nameCell = new PdfPCell(new Paragraph(nombre));
            nameCell.setPadding(cellPaddingUser);
            userInfoTable.addCell(nameCell);

            userInfoTable.addCell("Correo:");
            PdfPCell emailCell = new PdfPCell(new Paragraph(correo));
            emailCell.setPadding(cellPaddingUser);
            userInfoTable.addCell(emailCell);

            userInfoTable.addCell("Teléfono:");
            PdfPCell phoneCell = new PdfPCell(new Paragraph(telefono));
            phoneCell.setPadding(cellPaddingUser);
            userInfoTable.addCell(phoneCell);

// Agregar la tabla de información del usuario al documento
            document.add(userInfoTable);

// Agregar una separación entre las dos tablas (opcional, ajusta según tus preferencias)
            document.add(new Paragraph(" "));


            document.add(title);
            document.add(spaceBelowTitle);


// Crear una tabla con 4 columnas
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);


// Agregar encabezados de columna con fondo de color
            PdfPCell headerCell1 = new PdfPCell(new Paragraph("Nombre de la actividad"));
            headerCell1.setBackgroundColor(BaseColor.LIGHT_GRAY); // Color de fondo
            headerCell1.setPadding(cellPadding);
            table.addCell(headerCell1);

            PdfPCell headerCell2 = new PdfPCell(new Paragraph("Descripción"));
            headerCell2.setBackgroundColor(BaseColor.LIGHT_GRAY); // Color de fondo
            headerCell2.setPadding(cellPadding);
            table.addCell(headerCell2);

            PdfPCell headerCell3 = new PdfPCell(new Paragraph("Fecha de inicio"));
            headerCell3.setBackgroundColor(BaseColor.LIGHT_GRAY); // Color de fondo
            headerCell3.setPadding(cellPadding);
            table.addCell(headerCell3);

            PdfPCell headerCell4 = new PdfPCell(new Paragraph("Estado de la actividad"));
            headerCell4.setBackgroundColor(BaseColor.LIGHT_GRAY); // Color de fondo
            headerCell4.setPadding(cellPadding);
            table.addCell(headerCell4);

// Iterar sobre los datos y agregar filas a la tabla
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String nombreActividad = jsonObject.getString("nombre_actividad");
                String descripcionActividad = jsonObject.getString("descripcionActividad");
                String fechaInicio = jsonObject.getString("fecha_inicio");
                String estadoActividad = jsonObject.getString("estadoActividad");

                // Agregar una fila con los datos y configurar el espacio en las celdas
                PdfPCell cell1 = new PdfPCell(new Paragraph(nombreActividad));
                cell1.setPadding(cellPadding);
                table.addCell(cell1);

                PdfPCell cell2 = new PdfPCell(new Paragraph(descripcionActividad));
                cell2.setPadding(cellPadding);
                table.addCell(cell2);

                PdfPCell cell3 = new PdfPCell(new Paragraph(fechaInicio));
                cell3.setPadding(cellPadding);
                table.addCell(cell3);

                PdfPCell cell4 = new PdfPCell(new Paragraph(estadoActividad));
                cell4.setPadding(cellPadding);
                table.addCell(cell4);
            }

// Agregar la tabla al documento
            document.add(table);

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
            document.newPage();
        }
    }

    private void compartirPDF(File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
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
                mostrarDatosDelDiaDeHoy();
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









