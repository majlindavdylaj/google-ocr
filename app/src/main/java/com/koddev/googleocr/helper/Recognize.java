package com.koddev.googleocr.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.koddev.googleocr.ui.ResultImageActivity;
import com.koddev.googleocr.ui.ScanImageActivity;

public class Recognize {

    public static Bitmap myBitmap;

    private Context context;
    private ProgressDialog progressDialog;

    public Recognize(Context context){
        this.context = context;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Scanning...");
        progressDialog.setCancelable(false);
    }

    public void startRecognizing(Bitmap bitmap){
        progressDialog.show();
        if (bitmap != null){
            myBitmap = bitmap;
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

            detector.processImage(image).addOnSuccessListener(firebaseVisionText -> {
                processResultText(firebaseVisionText);
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
            });

        } else {
            progressDialog.dismiss();
            Toast.makeText(context, "Select an Image First", Toast.LENGTH_LONG).show();
        }

    }

    private void processResultText(FirebaseVisionText resultText) {
        if (resultText.getTextBlocks().size() == 0){
            progressDialog.dismiss();
            Toast.makeText(context, "No Text Found", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(context, ResultImageActivity.class);

        for (FirebaseVisionText.TextBlock textBlock : resultText.getTextBlocks()){
            for (FirebaseVisionText.Line line : textBlock.getLines()) {
                String s = line.getText().toLowerCase();
                if (s.contains("ubt")) {
                    ScanType.idUBT(resultText);
                    progressDialog.dismiss();
                    context.startActivity(intent);
                    return;
                } else if (s.contains("kosovo") || s.contains("kosovar")) {
                    ScanType.idKOSOVO(resultText);
                    progressDialog.dismiss();
                    context.startActivity(intent);
                    return;
                } else if(s.contains("personal informations")){
                    ScanType.pdf(resultText);
                    progressDialog.dismiss();
                    context.startActivity(intent);
                    return;
                }
            }
        }

        progressDialog.dismiss();
        Toast.makeText(context, "Can't recognize this type of format", Toast.LENGTH_SHORT).show();
    }
}
