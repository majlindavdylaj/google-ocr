package com.koddev.googleocr.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.koddev.googleocr.R;
import com.koddev.googleocr.helper.Recognize;
import com.koddev.googleocr.helper.Utils;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ScanPdfActivity extends AppCompatActivity {

    private PDFView pdfView;
    private ImageView scan;

    public File currentFile;
    public PDDocument currentDocument;

    private ProgressDialog dialog;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_pdf);

        pdfView = findViewById(R.id.pdfview);
        scan = findViewById(R.id.scan);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        try {
            Intent intent = getIntent();
            String path = intent.getStringExtra("path");
            if (path != null){
                currentFile = new File(path);
                new RetrievePDF().execute(currentFile);
            } else {
                Uri uri = intent.getData();
                if (uri != null) {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    File file = Utils.convertToFile(inputStream);
                    currentFile = file;
                    new RetrievePDF().execute(file);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        getSupportActionBar().setTitle(currentFile.getName());

        pdfView.fromFile(currentFile)
                .onDraw(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                    }
                })
                .showMinimap(true)
                .load();

        scan.setOnClickListener(v -> {
            dialog.setMessage("Analyzing...");
            dialog.show();
            new Handler().post(() -> {
                try {
                    PDFRenderer renderer = new PDFRenderer(currentDocument);
                    bitmap = renderer.renderImage(0, 3f, Bitmap.Config.RGB_565);
                    dialog.dismiss();
                    new Recognize(ScanPdfActivity.this).startRecognizing(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    class RetrievePDF extends AsyncTask<File, Integer, PDDocument> {

        @Override
        protected PDDocument doInBackground(File... files) {

            try {

                return PDDocument.load(files[0]);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(PDDocument pdDocument) {
            currentDocument = pdDocument;
            dialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Reading file...");
            dialog.show();
        }
    }
}