package com.koddev.googleocr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koddev.googleocr.R;

import java.io.File;
import java.util.List;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.PDFViewHolder> {

    private Context context;
    private List<String> pdfs;
    protected PDFListener pdfListener;

    public PDFAdapter(Context context, List<String> pdfs, PDFListener pdfListener) {
        this.context = context;
        this.pdfs = pdfs;
        this.pdfListener = pdfListener;
    }

    @NonNull
    @Override
    public PDFViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PDFViewHolder(
                LayoutInflater.from(context).inflate(R.layout.pdf_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PDFViewHolder holder, int position) {

        String pdf = pdfs.get(position);

        holder.setData(pdf);

    }

    @Override
    public int getItemCount() {
        return pdfs.size();
    }

    public class PDFViewHolder extends RecyclerView.ViewHolder {

        private TextView name, size;
        private View item;

        public PDFViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView;

            name = itemView.findViewById(R.id.name);
            size = itemView.findViewById(R.id.size);
        }

        public void setData(String path){
            File file = new File(path);

            name.setText(file.getName());

            long bytes = file.length();
            double kilobytes = (bytes / 1024);
            double megabytes = (kilobytes / 1024);

            String strDouble = String.format("%.2f", megabytes);
            size.setText(strDouble + " MB");

            item.setOnClickListener(v -> {
                pdfListener.onPDFClick(path);
            });
        }
    }

    public interface PDFListener{
        void onPDFClick(String path);
    }

}
