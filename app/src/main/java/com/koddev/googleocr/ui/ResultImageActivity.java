package com.koddev.googleocr.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.koddev.googleocr.R;

import static com.koddev.googleocr.ui.ScanImageActivity.resultText;

public class ResultImageActivity extends AppCompatActivity {

    private TextView result;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_image);

        result = findViewById(R.id.result);
        image = findViewById(R.id.image);

        image.setImageBitmap(ScanImageActivity.myBitmap);

        for (FirebaseVisionText.TextBlock textBlock : resultText.getTextBlocks()) {
            //textView.append(textBlock.getText()+"\n");

            for (FirebaseVisionText.Line line : textBlock.getLines()) {

                if ((line.getBoundingBox().bottom - line.getBoundingBox().top) > 12) {
                    result.append(line.getText() + "\n");
                    //textView.append(line.getCornerPoints()[0].toString()+" - "+line.getText()+"\n");
                    //textView.append(line.getConfidence()+" - "+line.getText()+"\n");
                    /*for (FirebaseVisionText.Element element : line.getElements()){
                        textView.append(element.getText()+"\n");
                    }*/
                }
            }
        }
    }
}