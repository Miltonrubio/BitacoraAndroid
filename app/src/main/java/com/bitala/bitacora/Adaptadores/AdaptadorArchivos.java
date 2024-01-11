package com.bitala.bitacora.Adaptadores;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bitala.bitacora.R;
import com.bitala.bitacora.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdaptadorArchivos extends RecyclerView.Adapter<AdaptadorArchivos.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;


    String url;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_archivos, parent, false);
        return new ViewHolder(view);

    }

    public void setFilteredData(List<JSONObject> filteredData) {
        this.filteredData = new ArrayList<>(filteredData);
        notifyDataSetChanged();
    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            JSONObject jsonObject2 = filteredData.get(position);
            String ID_archivo = jsonObject2.optString("ID_archivo", "");
            String nombreVisible = jsonObject2.optString("nombreVisible", "");
            String nombre_archivo = jsonObject2.optString("nombre_archivo", "");


            holder.nombreArchivo.setText(nombreVisible);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!permisosUsuario.equalsIgnoreCase("SUPERADMIN")) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        View customView = LayoutInflater.from(context).inflate(R.layout.opciones_archivos, null);
                        builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                        AlertDialog dialogOpcionesArchivos = builder.create();
                        ColorDrawable back = new ColorDrawable(Color.BLACK);
                        back.setAlpha(150);
                        dialogOpcionesArchivos.getWindow().setBackgroundDrawable(back);
                        dialogOpcionesArchivos.getWindow().setDimAmount(0.8f);
                        dialogOpcionesArchivos.show();

                        LinearLayout LayoutVerArchivo = customView.findViewById(R.id.LayoutVerArchivo);
                        LinearLayout LayoutEliminar = customView.findViewById(R.id.LayoutEliminar);
                        LinearLayout LayoutEditar = customView.findViewById(R.id.LayoutEditar);


                        LayoutVerArchivo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                modalCargando = Utils.ModalCargando(context, builderCargando);

                                String urlPdf = pdfUrl + nombre_archivo;

                                new DownloadPdfTask().execute(urlPdf);


                            }
                        });


                        LayoutEliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                View customView = LayoutInflater.from(context).inflate(R.layout.opciones_confirmacion, null);
                                builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                                AlertDialog dialogConfirmacion = builder.create();
                                ColorDrawable back = new ColorDrawable(Color.BLACK);
                                back.setAlpha(150);
                                dialogConfirmacion.getWindow().setBackgroundDrawable(back);
                                dialogConfirmacion.getWindow().setDimAmount(0.8f);
                                dialogConfirmacion.show();


                                Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                                Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);


                                buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogConfirmacion.dismiss();
                                        dialogOpcionesArchivos.dismiss();

                                        actionListener.onEliminarArchivo(ID_archivo, nombre_archivo);
                                    }
                                });


                                buttonCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogConfirmacion.dismiss();
                                    }
                                });

                            }
                        });


                        LayoutEditar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                View customView = LayoutInflater.from(context).inflate(R.layout.opcion_cancelar, null);
                                builder.setView(Utils.ModalRedondeado(view.getContext(), customView));
                                AlertDialog dialogConfirmacion = builder.create();
                                ColorDrawable back = new ColorDrawable(Color.BLACK);
                                back.setAlpha(150);
                                dialogConfirmacion.getWindow().setBackgroundDrawable(back);
                                dialogConfirmacion.getWindow().setDimAmount(0.8f);
                                dialogConfirmacion.show();

                                TextView tituloCancelacion = customView.findViewById(R.id.tituloCancelacion);
                                TextView editText = customView.findViewById(R.id.editText);
                                Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                                Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);

                                tituloCancelacion.setText("Agrega el nuevo nombre del archivo");
                                editText.setHint("Escribe el nuevo nombre");

                                buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String nuevoNombreArchivo = editText.getText().toString().trim();
                                        if (nuevoNombreArchivo.isEmpty()) {
                                            Utils.crearToastPersonalizado(context, "Debes ingresar un nombre para este archivo");
                                        } else {
                                            dialogConfirmacion.dismiss();
                                            dialogOpcionesArchivos.dismiss();


                                            actionListener.onEditarArchivo(ID_archivo, nuevoNombreArchivo);
                                        }
                                    }
                                });


                                buttonCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogConfirmacion.dismiss();
                                    }
                                });


                            }
                        });

                    } else {


                        modalCargando = Utils.ModalCargando(context, builderCargando);

                        String urlPdf = pdfUrl + nombre_archivo;

                        new DownloadPdfTask().execute(urlPdf);


                    }
                }
            });


        } finally {
        }

    }


    private class DownloadPdfTask extends AsyncTask<String, Void, File> {
        @Override
        protected File doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Guardar el PDF en el almacenamiento local
                File pdfFile = savePdfToStorage(connection.getInputStream());
                return pdfFile;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(File pdfFile) {
            if (pdfFile != null) {
                // Abre el visor de PDF predeterminado cuando se hace clic en el PDF
                openPdfInDefaultViewer(pdfFile);
            } else {
                Utils.crearToastPersonalizado(context, "Error al descargar el PDF");
            }
        }
    }

    private File savePdfToStorage(InputStream inputStream) {
        try {
            // Crear un archivo temporal en el directorio de almacenamiento interno de la aplicación
            File directory = new File(context.getFilesDir(), "temp");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File pdfFile = new File(new File(directory, "tu_archivo.pdf").getAbsolutePath());

            // Guardar el contenido del PDF en el archivo
            FileOutputStream fos = new FileOutputStream(pdfFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.close();

            return pdfFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openPdfInDefaultViewer(File pdfFile) {
        // Crear una URI para el archivo PDF utilizando FileProvider
        Uri pdfUri = FileProvider.getUriForFile(context, "com.bitala.bitacora.fileprovider", pdfFile);

        // Crear una intención para abrir el visor de archivos predeterminado del sistema
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            // Intenta abrir el visor de PDF
            startActivity(context, intent, null);
        } catch (ActivityNotFoundException e) {

            Utils.crearToastPersonalizado(context, "No se encontró una aplicación para abrir PDF");
        }
        modalCargando.dismiss();
    }


    @Override
    public int getItemCount() {

        return filteredData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenArchivo;
        TextView nombreArchivo;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombreArchivo = itemView.findViewById(R.id.nombreArchivo);
            imagenArchivo = itemView.findViewById(R.id.imagenArchivo);

        }
    }


    public void filter(String query) {
        filteredData.clear();

        if (TextUtils.isEmpty(query)) {
            filteredData.addAll(data);
        } else {
            String[] keywords = query.toLowerCase().split(" ");

            for (JSONObject item : data) {
                String saldo_inicial = item.optString("saldo_inicial", "").toLowerCase();
                String ID_saldo = item.optString("ID_saldo", "").toLowerCase();
                String nuevo_saldo = item.optString("nuevo_saldo", "").toLowerCase();


                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(saldo_inicial.contains(keyword) || ID_saldo.contains(keyword)
                            || nuevo_saldo.contains(keyword)
                    )) {
                        matchesAllKeywords = false;
                        break;
                    }
                }

                if (matchesAllKeywords) {
                    filteredData.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }


    public interface OnActivityActionListener {
        void onEliminarArchivo(String ID_archivo, String nombreArchivo);

        void onEditarArchivo(String ID_archivo, String nuevoNombreArchivo);
    }

    private AdaptadorArchivos.OnActivityActionListener actionListener;


    public AdaptadorArchivos(List<JSONObject> data, Context context, AdaptadorArchivos.OnActivityActionListener actionListener) {

        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        url = context.getResources().getString(R.string.urlApi);
        this.actionListener = actionListener;
        pdfUrl = context.getResources().getString(R.string.pdfUrl);

        builderCargando = new AlertDialog.Builder(context);
        builderCargando.setCancelable(false);


        SharedPreferences sharedPreferences = context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        this.permisosUsuario = sharedPreferences.getString("permisos", "");


    }

    String permisosUsuario;

    String pdfUrl;
    AlertDialog modalCargando;
    AlertDialog.Builder builderCargando;
}

