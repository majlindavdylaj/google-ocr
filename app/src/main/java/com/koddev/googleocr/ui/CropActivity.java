package com.koddev.googleocr.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.koddev.googleocr.R;
import com.koddev.googleocr.helper.Recognize;
import com.koddev.googleocr.helper.Utils;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tom_roush.pdfbox.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CropActivity extends AppCompatActivity {

    private CropImageView cropImageView;
    private ImageView scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        cropImageView = findViewById(R.id.cropImageView);
        scan = findViewById(R.id.scan);

        String path = getIntent().getStringExtra("photoPath");
        if (path != null){
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Bitmap fixedBitmap = Utils.fixRotateImage(path, bitmap);
            cropImageView.setImageBitmap(fixedBitmap);
        } else {
            Uri uri = getIntent().getData();
            if (uri != null) {
                cropImageView.setImageUriAsync(uri);
            } else {
                Toast.makeText(this, "Can't open this image", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        scan.setOnClickListener(v -> {
            Bitmap myBitmap = cropImageView.getCroppedImage();
            new Recognize(CropActivity.this).startRecognizing(myBitmap);
        });
    }
}