package com.koddev.googleocr.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.koddev.googleocr.R;
import com.koddev.googleocr.db.DBHelper;
import com.koddev.googleocr.model.Document;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private Context context;
    private List<Document> documents;
    protected DocumentAdapter.DocumentListener documentListener;

    public DocumentAdapter(Context context, List<Document> documents, DocumentAdapter.DocumentListener documentListener) {
        this.context = context;
        this.documents = documents;
        this.documentListener = documentListener;
    }

    @NonNull
    @Override
    public DocumentAdapter.DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DocumentAdapter.DocumentViewHolder(
                LayoutInflater.from(context).inflate(R.layout.document_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentAdapter.DocumentViewHolder holder, int position) {

        Document document = documents.get(position);

        holder.setData(document);

    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public class DocumentViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name, date;
        private View item;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView;

            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
        }

        public void setData(Document document) {

            Glide.with(context).load(document.getImage()).into(image);
            name.setText(document.getType());
            date.setText("Scanned: " + document.getDate());

            item.setOnClickListener(v -> documentListener.onDocumentClick(document));

            item.setOnLongClickListener(v -> {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Delete this Document?");
                alertDialog.setMessage("Are you sure you want to delete this Document?");
                alertDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                alertDialog.setPositiveButton("Yes", (dialog, which) -> {
                    if (new DBHelper(context).deleteDocument(document.getId()+"")){
                        documentListener.onDocumentDeleted();
                    }
                }).create().show();
                return true;
            });
        }
    }

    public interface DocumentListener {
        void onDocumentClick(Document document);
        void onDocumentDeleted();
    }
}
