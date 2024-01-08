
package com.bitala.bitacora;

import android.Manifest;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bitala.bitacora.Adaptadores.AdaptadorActividadesPorUsuario;
import com.bitala.bitacora.Adaptadores.DownloadFileTask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bitala.bitacora.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
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

    String url;


    private AdaptadorActividadesPorUsuario adaptadorActividades;
    private List<JSONObject> dataList = new ArrayList<>();

    List<JSONObject> datosDependiendoDeFecha = new ArrayList<>();


    int versionSDK = Build.VERSION.SDK_INT;


    public ActividadesPorUsuarioFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    Context context;
    RecyclerView rvActividadesUsuario;
    LottieAnimationView lottieNoActividades;
    LottieAnimationView lottieNoInternet;

    AlertDialog.Builder builder;

    AlertDialog modalCargando;

    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    String fechaActual = String.format("%04d-%02d-%02d", year, month, day);
    String rangoFecha = "hoy";


    TextView textSinActividades;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actividades_por_usuario, container, false);

        builder = new AlertDialog.Builder(view.getContext());
        builder.setCancelable(false);

        Button btnFiltrarDeHOY = view.findViewById(R.id.btnFiltrarDeHOY);
        Button btnFiltrarDeLaSemana = view.findViewById(R.id.btnFiltrarDeLaSemana);
        Button btnFiltrarPorMes = view.findViewById(R.id.btnFiltrarPorMes);
        Button btnFiltrarDelAnio = view.findViewById(R.id.btnFiltrarDelAnio);
        ImageView fabBotonFlotante = view.findViewById(R.id.fabBotonFlotante);
        rvActividadesUsuario = view.findViewById(R.id.rvActividadesUsuario);
        ImageView IVFotoDeUsuario = view.findViewById(R.id.IVFotoDeUsuario);
        textSinActividades = view.findViewById(R.id.textSinActividades);
        lottieNoActividades = view.findViewById(R.id.lottieNoActividades);
        lottieNoInternet = view.findViewById(R.id.lottieNoInternet);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);


        context = requireContext();
        url = context.getResources().getString(R.string.urlApi);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
            if (permissions.get(WRITE_EXTERNAL_STORAGE) && permissions.get(READ_EXTERNAL_STORAGE)) {
                Log.d("Permiso de almacenamiento: ", "Permiso concedido");
            } else {

                Utils.crearToastPersonalizado(context, "Permiso denegado");

            }
        });


        rvActividadesUsuario.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptadorActividades = new AdaptadorActividadesPorUsuario(dataList, context);
        rvActividadesUsuario.setAdapter(adaptadorActividades);


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


            String imagenUrl = "http://hidalgo.no-ip.info:5610/bitacora/fotos/fotos_usuarios/fotoperfilusuario" + ID_usuario + ".jpg";


            Glide.with(this)
                    .load(imagenUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.imagendefault)
                    .error(R.drawable.imagendefault)
                    .into(IVFotoDeUsuario);

            tvNombreActividad.setText(nombre);
            tvRolDeUsuario.setText(permisos);

            ActividadesPorUsuario(ID_usuario);


            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    ActividadesPorUsuario(ID_usuario);

                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }


        fabBotonFlotante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (versionSDK > Build.VERSION_CODES.S) {
                    if (datosDependiendoDeFecha.isEmpty() || datosDependiendoDeFecha.equals(null)) {

                        Utils.crearToastPersonalizado(context, "No hay actividades para generar el reporte");

                    } else {
                        generarPDF(rangoFecha);
                    }

                } else {
                    if (checkPermission()) {
                        if (datosDependiendoDeFecha.isEmpty() || datosDependiendoDeFecha.equals(null)) {

                            Utils.crearToastPersonalizado(context, "No hay actividades para generar el reporte");
                        } else {
                            generarPDF(rangoFecha);
                        }
                    } else {
                        requestPermissions();
                        Utils.crearToastPersonalizado(context, "No se pudo dar permisos");
                    }
                }
            }
        });


        btnFiltrarPorMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDatosDelMesActual();
            }
        });


        btnFiltrarDeHOY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDatosDelDiaDeHoy();
            }
        });

        btnFiltrarDelAnio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDatosDelAnioActual();
            }
        });

        btnFiltrarDeLaSemana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MostrarDatosPorSemana();
            }
        });

        return view;
    }


    private void generarPDF(String rango) {

        Map<String, String> postData = new HashMap<>();
        postData.put("opcion", "61");
        postData.put("ID_usuario", ID_usuario);
        postData.put("rango", rango);
        new DownloadFileTask(context, postData).execute(url);

    }


    private boolean checkPermission() {
        int writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);

        return writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (!checkPermission()) {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
        }
    }

    private void mostrarDatosDelDiaDeHoy() {
        reiniciarDatosDependiendoDeFecha();
        rangoFecha = "hoy";
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Los meses en Calendar van de 0 a 11
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        for (JSONObject jsonObject : dataList) {
            try {
                String fechaInicio = jsonObject.optString("fecha_inicio", "");
                if (fechaInicio.isEmpty() || fechaInicio.equals("null") || fechaInicio.equals(null)) {
                    datosDependiendoDeFecha.add(jsonObject);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date fecha = sdf.parse(fechaInicio);

                    Calendar actividadCalendar = Calendar.getInstance();
                    actividadCalendar.setTime(fecha);
                    int actividadYear = actividadCalendar.get(Calendar.YEAR);
                    int actividadMonth = actividadCalendar.get(Calendar.MONTH) + 1;
                    int actividadDay = actividadCalendar.get(Calendar.DAY_OF_MONTH);

                    if (year == actividadYear && month == actividadMonth && day == actividadDay) {
                        datosDependiendoDeFecha.add(jsonObject);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (datosDependiendoDeFecha.size() > 0) {
            MostrarAnimaciones("Actividades");
        } else {
            MostrarAnimaciones("SinActividades");
        }

        adaptadorActividades.setFilteredData(datosDependiendoDeFecha);
        adaptadorActividades.notifyDataSetChanged();
    }

    private void mostrarDatosDelMesActual() {

        reiniciarDatosDependiendoDeFecha();
        rangoFecha = "mes";
        Calendar calendar = Calendar.getInstance();

        // Resta 30 días a la fecha actual
        calendar.add(Calendar.DAY_OF_YEAR, -30);

        Date fechaHace30Dias = calendar.getTime();

        for (JSONObject jsonObject : dataList) {
            String fechaInicio = jsonObject.optString("fecha_inicio", "");
            if (fechaInicio.isEmpty() || fechaInicio.equals("null") || fechaInicio.equals(null)) {
                datosDependiendoDeFecha.add(jsonObject);
            } else {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date fecha = sdf.parse(fechaInicio);

                    if (fecha.after(fechaHace30Dias) || fecha.equals(fechaHace30Dias)) {
                        datosDependiendoDeFecha.add(jsonObject);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (datosDependiendoDeFecha.size() > 0) {

            MostrarAnimaciones("Actividades");
        } else {

            MostrarAnimaciones("SinActividades");
        }
        adaptadorActividades.setFilteredData(datosDependiendoDeFecha);
        adaptadorActividades.notifyDataSetChanged();
    }


    private void MostrarDatosPorSemana() {
        reiniciarDatosDependiendoDeFecha();
        rangoFecha = "semana";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date()); // Establece la fecha actual

        // Resta 7 días a la fecha actual
        calendar.add(Calendar.DAY_OF_YEAR, -8);

        Date fechaHace7Dias = calendar.getTime();

        for (JSONObject jsonObject : dataList) {
            String fechaInicio = jsonObject.optString("fecha_inicio", "");
            if (fechaInicio.isEmpty() || fechaInicio.equals("null") || fechaInicio.equals(null)) {
                datosDependiendoDeFecha.add(jsonObject);
            } else {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date fecha = sdf.parse(fechaInicio);

                    if (fecha.after(fechaHace7Dias) || fecha.equals(fechaHace7Dias)) {
                        datosDependiendoDeFecha.add(jsonObject);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (datosDependiendoDeFecha.size() > 0) {

            MostrarAnimaciones("Actividades");
        } else {

            MostrarAnimaciones("SinActividades");
        }
        adaptadorActividades.setFilteredData(datosDependiendoDeFecha);
        adaptadorActividades.notifyDataSetChanged();
    }


    private void mostrarDatosDelAnioActual() {
        reiniciarDatosDependiendoDeFecha();
        rangoFecha = "anio";
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        for (JSONObject jsonObject : dataList) {
            String fechaInicio = jsonObject.optString("fecha_inicio", "");
            if (fechaInicio.isEmpty() || fechaInicio.equals("null") || fechaInicio.equals(null)) {
                datosDependiendoDeFecha.add(jsonObject);
            } else {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date fecha = sdf.parse(fechaInicio);

                    Calendar actividadCalendar = Calendar.getInstance();
                    actividadCalendar.setTime(fecha);
                    int actividadYear = actividadCalendar.get(Calendar.YEAR);

                    if (year == actividadYear) {
                        datosDependiendoDeFecha.add(jsonObject);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (datosDependiendoDeFecha.size() > 0) {

            MostrarAnimaciones("Actividades");
        } else {

            MostrarAnimaciones("SinActividades");
        }
        adaptadorActividades.setFilteredData(datosDependiendoDeFecha);
        adaptadorActividades.notifyDataSetChanged();
    }


    private void reiniciarDatosDependiendoDeFecha() {
        datosDependiendoDeFecha.clear();
    }


/*



    public void generarPDF(List<JSONObject> responseData) {
        Document document = new Document();

        try {
            File pdfFile = new File(context.getExternalFilesDir(null), "Reporte De Actividades " + nombre + ".pdf");
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            PageEventHandler eventHandler = new PageEventHandler();
            writer.setPageEvent(eventHandler);

            document.open();

            Drawable drawable = getResources().getDrawable(R.drawable.logo);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
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

            Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED);
            document.add(image);
            document.add(spaceBelowImage);

            PdfPTable userInfoTable = new PdfPTable(2);
            userInfoTable.setWidthPercentage(55);

            BaseColor backgroundColor = BaseColor.YELLOW;

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

            document.add(userInfoTable);

            document.add(new Paragraph(" "));


            document.add(title);
            document.add(spaceBelowTitle);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            PdfPCell headerCell1 = new PdfPCell(new Paragraph("Nombre de la actividad"));
            headerCell1.setBackgroundColor(BaseColor.YELLOW); // Color de fondo
            headerCell1.setPadding(cellPadding);
            table.addCell(headerCell1);

            PdfPCell headerCell2 = new PdfPCell(new Paragraph("Descripción"));
            headerCell2.setBackgroundColor(BaseColor.YELLOW); // Color de fondo
            headerCell2.setPadding(cellPadding);
            table.addCell(headerCell2);

            PdfPCell headerCell3 = new PdfPCell(new Paragraph("Fecha de inicio"));
            headerCell3.setBackgroundColor(BaseColor.YELLOW); // Color de fondo
            headerCell3.setPadding(cellPadding);
            table.addCell(headerCell3);

            PdfPCell headerCell4 = new PdfPCell(new Paragraph("Fecha de finalizacion"));
            headerCell4.setBackgroundColor(BaseColor.YELLOW); // Color de fondo
            headerCell4.setPadding(cellPadding);
            table.addCell(headerCell4);


            PdfPCell headerCell6 = new PdfPCell(new Paragraph("Tiempo de actividad"));
            headerCell6.setBackgroundColor(BaseColor.YELLOW);
            headerCell6.setPadding(cellPadding);
            table.addCell(headerCell6);


            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String nombreActividad = jsonObject.getString("nombre_actividad");
                String descripcionActividad = jsonObject.getString("descripcionActividad");
                String fechaInicio = jsonObject.getString("fecha_inicio");
                String fechaFin = jsonObject.getString("fecha_fin");
                String estadoActividad = jsonObject.getString("estadoActividad");
                String motivocancelacion = jsonObject.getString("motivocancelacion");


                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy 'a las' h:mm a");


                PdfPCell cell1 = new PdfPCell(new Paragraph(nombreActividad));
                cell1.setPadding(cellPadding);
                table.addCell(cell1);

                PdfPCell cell2 = new PdfPCell(new Paragraph(descripcionActividad));
                cell2.setPadding(cellPadding);
                table.addCell(cell2);


                PdfPCell cell3 = new PdfPCell();
                cell3.setPadding(cellPadding);
                if (fechaInicio != null && !fechaInicio.isEmpty()) {
                    try {
                        Date date = inputFormat.parse(fechaInicio);
                        String formattedDate = outputFormat.format(date);
                        cell3.addElement(new Paragraph("Iniciado el " + formattedDate));
                    } catch (ParseException e) {

                        cell3.addElement(new Paragraph("Aun no se ha iniciado la actividad"));
                    }
                } else {
                    cell3.addElement(new Paragraph("Aun no se ha iniciado la actividad"));
                }
                table.addCell(cell3);


                PdfPCell cell4 = new PdfPCell();
                cell4.setPadding(cellPadding);
                if (fechaFin != null && !fechaFin.isEmpty()) {
                    try {
                        Date date = inputFormat.parse(fechaFin);
                        String formattedDate = outputFormat.format(date);

                        if (estadoActividad.equals("Cancelado")) {
                            cell4.addElement(new Paragraph("Se canceló la actividad el " + formattedDate, font));
                        } else {
                            cell4.addElement(new Paragraph("Finalizado el " + formattedDate));
                        }

                    } catch (ParseException e) {

                        cell4.addElement(new Paragraph("Aun no se ha finalizado la actividad"));
                    }
                } else {
                    cell4.addElement(new Paragraph("Aun no se ha finalizado la actividad"));

                }
                table.addCell(cell4);


                PdfPCell cell6 = new PdfPCell();
                cell6.setPadding(cellPadding);

                if (fechaInicio != null && !fechaInicio.isEmpty() && fechaFin != null && !fechaFin.isEmpty()) {
                    try {
                        Date dateInicio = inputFormat.parse(fechaInicio);
                        Date dateFin = inputFormat.parse(fechaFin);
                        long diffMillis = dateFin.getTime() - dateInicio.getTime();

                        long diffDays = diffMillis / (24 * 60 * 60 * 1000);
                        long diffHours = (diffMillis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
                        long diffMinutes = (diffMillis % (60 * 60 * 1000)) / (60 * 1000);

                        String diferenciaTexto = "";

                        if (diffDays == 1) {
                            diferenciaTexto += "1 día ";
                        } else if (diffDays > 1) {
                            diferenciaTexto += diffDays + " días ";
                        }

                        if (diffHours == 1) {
                            diferenciaTexto += "1 hora ";
                        } else if (diffHours > 1) {
                            diferenciaTexto += diffHours + " horas ";
                        }

                        if (diffMinutes == 1) {
                            diferenciaTexto += "1 minuto";
                        } else if (diffMinutes > 1) {
                            diferenciaTexto += diffMinutes + " minutos";
                        }

                        if (diferenciaTexto.isEmpty()) {
                            diferenciaTexto = "Menos de 1 minuto";
                        }

                        if (estadoActividad.equals("Cancelado")) {  // Crear una fuente con color rojo
                            cell6.addElement(new Paragraph("Motivo de cancelación: " + motivocancelacion));
                        } else {
                            cell6.addElement(new Paragraph(diferenciaTexto));
                        }

                    } catch (ParseException e) {


                        cell6.addElement(new Paragraph("Aun no se ha finalizado la actividad"));

                    }
                } else {
                    cell6.addElement(new Paragraph("Faltan datos para calcular la diferencia"));
                }

                table.addCell(cell6);

            }

            document.add(table);
            document.close();
            compartirPDF(pdfFile);

            Utils.crearToastPersonalizado(context, "Se creó el PDF correctamente");
        } catch (IOException | DocumentException | JSONException e) {
            Utils.crearToastPersonalizado(context, "No se pudo crear el PDF");

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

        Uri uri = FileProvider.getUriForFile(context, "com.bitala.bitacora.fileprovider", file);

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Compartir archivo PDF");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Compartir PDF"));
    }
*/

    private void ActividadesPorUsuario(String ID_usuario) {
        dataList.clear();
        modalCargando = Utils.ModalCargando(context, builder);
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dataList.add(jsonObject);
                    }
                    adaptadorActividades.notifyDataSetChanged();
                    adaptadorActividades.setFilteredData(dataList);
                    adaptadorActividades.filter("");
                    Log.d("Respuesta de api: ", response);

                } catch (JSONException e) {

                    MostrarAnimaciones("SinActividades");

                }
                mostrarDatosDelDiaDeHoy();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MostrarAnimaciones("SinInternet");
                Utils.crearToastPersonalizado(context, "No tienes conexion a internet");

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "2");
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void MostrarAnimaciones(String estado) {
        if (estado.equalsIgnoreCase("SinActividades")) {
            lottieNoInternet.setVisibility(View.GONE);
            rvActividadesUsuario.setVisibility(View.GONE);
            lottieNoActividades.setVisibility(View.VISIBLE);
            textSinActividades.setVisibility(View.VISIBLE);

        } else if (estado.equalsIgnoreCase("SinInternet")) {

            lottieNoInternet.setVisibility(View.VISIBLE);
            rvActividadesUsuario.setVisibility(View.GONE);
            lottieNoActividades.setVisibility(View.GONE);
            textSinActividades.setVisibility(View.GONE);

        } else {

            lottieNoInternet.setVisibility(View.GONE);
            rvActividadesUsuario.setVisibility(View.VISIBLE);
            textSinActividades.setVisibility(View.GONE);
            lottieNoActividades.setVisibility(View.GONE);
        }
        onLoadComplete();
    }


    public void onLoadComplete() {
        if (modalCargando.isShowing() && modalCargando != null) {
            modalCargando.dismiss();
        }
    }

}









