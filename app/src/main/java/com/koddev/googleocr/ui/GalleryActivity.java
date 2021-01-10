package com.koddev.googleocr.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.koddev.googleocr.R;
import com.koddev.googleocr.adapter.FolderListAdapter;
import com.koddev.googleocr.adapter.GalleryAdapter;
import com.koddev.googleocr.helper.Utils;
import com.koddev.googleocr.model.Bucket;

import java.io.File;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recycler_view_gallery;
    private TextView tv_folders;

    private GalleryAdapter galleryAdapter;
    private List<String> images;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recycler_view_gallery = findViewById(R.id.recycler_view_gallery);
        tv_folders = findViewById(R.id.folders);

        loadImagesFromGallery();

        createAlertDialog();

        tv_folders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

    }

    private void loadImagesFromGallery(){
        recycler_view_gallery.setHasFixedSize(true);
        recycler_view_gallery.setLayoutManager(new GridLayoutManager(this, 4));
        images = Utils.getAllImages(this);
        galleryAdapter = new GalleryAdapter(this, images, path -> {
            nextActivity(path);
        });

        recycler_view_gallery.setAdapter(galleryAdapter);

    }

    private void createAlertDialog(){

        builder = new AlertDialog.Builder(this);

        List<Bucket> folders = Utils.getImageBuckets(this);
        FolderListAdapter adapter = new FolderListAdapter(this, folders);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                images.clear();
                if (which == 0){
                    images = Utils.getAllImages(GalleryActivity.this);
                } else {
                    images = Utils.getImagesByBucket(new File(folders.get(which).getName()).getName(), GalleryActivity.this);
                }
                galleryAdapter = new GalleryAdapter(GalleryActivity.this, images, path -> {
                    nextActivity(path);
                });
                recycler_view_gallery.setAdapter(galleryAdapter);

                tv_folders.setText(folders.get(which).getName());
            }
        });
        dialog = builder.create();
    }

    private void nextActivity(String path){
        Intent intent = new Intent(this, CropActivity.class);
        intent.putExtra("photoPath", path);
        startActivity(intent);
    }
}