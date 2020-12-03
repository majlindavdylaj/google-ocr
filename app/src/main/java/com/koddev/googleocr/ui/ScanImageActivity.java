package com.koddev.googleocr.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.koddev.googleocr.R;
import com.koddev.googleocr.helper.CustomCamera;

import java.util.concurrent.ExecutionException;

public class ScanImageActivity extends AppCompatActivity {

    private PreviewView preview_view;
    private ImageView scan;
    RelativeLayout cropped;

    public static FirebaseVisionText resultText;
    public static Bitmap myBitmap;

    private CustomCamera customCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_image);

        FirebaseApp.initializeApp(this);

        preview_view = findViewById(R.id.preview_view);
        scan = findViewById(R.id.scan);
        cropped = findViewById(R.id.cropped);

        customCamera = new CustomCamera(this, preview_view);
        customCamera.startCamera();

        scan.setOnClickListener(view -> {
            Bitmap bitmap = customCamera.getBitmap();
            myBitmap = cropImage(bitmap);
            startRecognizing(myBitmap);
        });
    }

    public void startRecognizing(Bitmap bitmap){
        if (bitmap != null){
            scan.setEnabled(false);
            scan.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

            detector.processImage(image).addOnSuccessListener(firebaseVisionText -> {
                scan.setEnabled(true);
                scan.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
                processResultText(firebaseVisionText);
            }).addOnFailureListener(e -> {
                scan.setEnabled(true);
                scan.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
                Toast.makeText(ScanImageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            });

        } else {
            Toast.makeText(this, "Select an Image First", Toast.LENGTH_LONG).show();
        }

    }

    private void processResultText(FirebaseVisionText resultText) {
        if (resultText.getTextBlocks().size() == 0){
            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show();
            return;
        }

        ScanImageActivity.resultText = resultText;

        startActivity(new Intent(this, ResultImageActivity.class));

        /*for (FirebaseVisionText.TextBlock textBlock : resultText.getTextBlocks()){
            //textView.append(textBlock.getText()+"\n");
            for (FirebaseVisionText.Line line : textBlock.getLines()){
                if ((line.getBoundingBox().bottom - line.getBoundingBox().top) > 15) {
                    textView.append(line.getText() + "\n");
                    //textView.append(line.getCornerPoints()[0].toString()+" - "+line.getText()+"\n");
                    //textView.append(line.getConfidence()+" - "+line.getText()+"\n");
                /*for (FirebaseVisionText.Element element : line.getElements()){
                    textView.append(element.getText()+"\n");
                }*/
                /*}
            }
        }*/

        //Toast.makeText(this, ""+resultText.getTextBlocks().size(), Toast.LENGTH_SHORT).show();
    }

    private Bitmap cropImage(Bitmap bitmap){
        return scaleCenterCrop(bitmap, cropped.getHeight(), cropped.getWidth());
    }

    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }
}