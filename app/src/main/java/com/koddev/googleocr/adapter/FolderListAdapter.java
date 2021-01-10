package com.koddev.googleocr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.koddev.googleocr.R;
import com.koddev.googleocr.model.Bucket;

import java.io.File;
import java.util.List;

public class FolderListAdapter extends ArrayAdapter<Bucket> {

    public FolderListAdapter(Context context, List<Bucket> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Bucket bucket = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.folder_item, parent, false);
        }

        ImageView image = convertView.findViewById(R.id.image);
        TextView name = convertView.findViewById(R.id.name);

        Glide.with(getContext()).load(bucket.getLastImage()).into(image);
        name.setText(new File(bucket.getName()).getName());

        return convertView;
    }
}
