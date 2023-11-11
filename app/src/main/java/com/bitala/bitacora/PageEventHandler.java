package com.bitala.bitacora;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class PageEventHandler extends PdfPageEventHelper {
    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        // Agrega una nueva página al comienzo de cada página
        document.newPage();
    }
}