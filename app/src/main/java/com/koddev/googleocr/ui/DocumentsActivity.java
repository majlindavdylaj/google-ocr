package com.koddev.googleocr.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import com.koddev.googleocr.R;
import com.koddev.googleocr.adapter.DocumentAdapter;
import com.koddev.googleocr.adapter.PDFAdapter;
import com.koddev.googleocr.db.DBHelper;
import com.koddev.googleocr.helper.Utils;
import com.koddev.googleocr.model.Document;

import java.util.List;

public class DocumentsActivity extends AppCompatActivity {

    private RecyclerView recycler_view_documents;
    private DocumentAdapter documentAdapter;
    private List<Document> documents;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        getSupportActionBar().setTitle("Documents");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Reading Documents...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        recycler_view_documents = findViewById(R.id.recycler_view_documents);

        new Handler().post(() -> {
            readDocuments();
            progressDialog.dismiss();
        });
    }

    private void readDocuments(){
        recycler_view_documents.setHasFixedSize(true);
        recycler_view_documents.setLayoutManager(new LinearLayoutManager(this));
        documents = new DBHelper(this).getAllDocuments();
        documentAdapter = new DocumentAdapter(this, documents, new DocumentAdapter.DocumentListener() {
            @Override
            public void onDocumentClick(Document document) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DocumentsActivity.this);
                alertDialog.setTitle(document.getType());
                alertDialog.setMessage(document.getJson());
                alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }

            @Override
            public void onDocumentDeleted() {
                Toast.makeText(DocumentsActivity.this, "Document delted!", Toast.LENGTH_SHORT).show();
                readDocuments();
            }
        });
        recycler_view_documents.setAdapter(documentAdapter);
    }
}