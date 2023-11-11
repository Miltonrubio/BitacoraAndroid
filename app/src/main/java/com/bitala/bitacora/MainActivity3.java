package com.bitala.bitacora;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bitala.bitacora.R;

import java.io.File;
import java.io.IOException;

public class MainActivity3 extends AppCompatActivity {


    String tituloText = "Titulo del documento";
    String descripcionText = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque \n" +
            "laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae \n" +
            "vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit,\n" +
            " sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, \n" +
            "qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora \n" +
            "incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum \n" +
            "exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem \n" +
            "vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem \n" +
            " eum fugiat quo voluptas nulla pariatur?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        Button button = findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermission()) {
                    Toast.makeText(MainActivity3.this, "Permiso aceptado", Toast.LENGTH_SHORT).show();

                } else {
                    requestPermissions();

                }

                generarPDF();
            }
        });

    }

    private void generarPDF() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        TextPaint titulo = new TextPaint();
        TextPaint descripcion = new TextPaint();


        Bitmap bitmap, bitmapEscala;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(816, 1054, 1).create();
        PdfDocument.Page pagina1 = pdfDocument.startPage(pageInfo);


        Canvas canvas = pagina1.getCanvas();

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imagendefault);
        bitmapEscala = Bitmap.createScaledBitmap(bitmap, 80, 80, false);

        canvas.drawBitmap(bitmapEscala, 368, 20, paint);

        titulo.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titulo.setTextSize(20);
        canvas.drawText(tituloText, 10, 150, titulo);

        descripcion.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        descripcion.setTextSize(14);

        String[] arrDescripcion = descripcionText.split("\n");

        int y = 200;
        for (int i = 0; i < arrDescripcion.length; i++) {
            canvas.drawText(arrDescripcion[i], 10, y, descripcion);
            y += 15;
        }
        pdfDocument.finishPage(pagina1);
        File file = new File(getExternalFilesDir(null), "Archivo.pdf");
        compartirPDF(file);
        Uri contentUri = FileProvider.getUriForFile(MainActivity3.this, "com.example.bitacora.fileprovider", file);


        try {
            pdfDocument.writeTo(getContentResolver().openOutputStream(contentUri));
            Toast.makeText(MainActivity3.this, "Se creo el PDF correctamente", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(MainActivity3.this, "No se creo el PDF ", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
        pdfDocument.close();
    }


    private void compartirPDF(File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        Uri uri = FileProvider.getUriForFile(this, "com.example.bitacora.fileprovider", file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Compartir archivo PDF");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Compartir PDF"));
    }


    private boolean checkPermission() {
        int permiso1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permiso2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return permiso1 == PackageManager.PERMISSION_GRANTED && permiso2 == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity3.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 200);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantresults) {
        if (requestCode == 200) {
            if (grantresults.length > 0) {
                boolean writeStore = grantresults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantresults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStore && readStorage) {

                    Toast.makeText(MainActivity3.this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity3.this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        }

    }

}