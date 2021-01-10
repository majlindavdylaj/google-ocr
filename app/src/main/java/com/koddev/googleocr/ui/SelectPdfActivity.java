package com.koddev.googleocr.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import com.koddev.googleocr.R;
import com.koddev.googleocr.adapter.GalleryAdapter;
import com.koddev.googleocr.adapter.PDFAdapter;
import com.koddev.googleocr.helper.Utils;

import java.io.File;
import java.util.List;

public class SelectPdfActivity extends AppCompatActivity {

    private RecyclerView recycler_view_pdf;
    private PDFAdapter pdfAdapter;
    private List<String> pdfs;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pdf);

        getSupportActionBar().setTitle("Documents");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Reading Documents...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        recycler_view_pdf = findViewById(R.id.recycler_view_pdf);

        new Handler().post(() -> {
            readPDFsFromStorage();
            progressDialog.dismiss();
        });
    }

    private void readPDFsFromStorage(){
        recycler_view_pdf.setHasFixedSize(true);
        recycler_view_pdf.setLayoutManager(new LinearLayoutManager(this));
        pdfs = Utils.getAllPDFs(Environment.getExternalStorageDirectory());
        pdfAdapter = new PDFAdapter(this, pdfs, path -> {
            Intent intent = new Intent(SelectPdfActivity.this, ScanPdfActivity.class);
            intent.putExtra("path", path);
            startActivity(intent);
        });
        recycler_view_pdf.setAdapter(pdfAdapter);
    }
}