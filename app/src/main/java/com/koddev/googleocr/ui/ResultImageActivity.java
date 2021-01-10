package com.koddev.googleocr.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.koddev.googleocr.R;
import com.koddev.googleocr.db.DBHelper;
import com.koddev.googleocr.helper.Recognize;
import com.koddev.googleocr.helper.ScanType;
import com.koddev.googleocr.helper.Utils;
import com.koddev.googleocr.model.Document;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultImageActivity extends AppCompatActivity {

    private ImageView image;
    private Button button_save;

    private LinearLayout layout_content;

    private HashMap<String, String> params;

    private ProgressDialog progressDialog;

    List<EditText> editTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_image);

        intiViews();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        params = ScanType.params;
        editTexts = new ArrayList<>();

        for(Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (key.equals("type")){
                getSupportActionBar().setTitle(value);
            } else {
                generateEditText(key, value);
            }
        }

        image.setImageBitmap(Recognize.myBitmap);

        button_save.setOnClickListener(v -> {
            progressDialog.setMessage("Saving...");
            progressDialog.show();
            new Handler().post(() -> {
                Bitmap bitmap = Recognize.myBitmap;
                String s = generateString();
                Utils.bitmapConvertToFile(bitmap, this,
                        getSupportActionBar().getTitle().toString(), s);
                progressDialog.dismiss();
            });

        });

    }

    private void intiViews(){
        image = findViewById(R.id.image);
        button_save = findViewById(R.id.button_save);
        layout_content = findViewById(R.id.layout_content);
    }

    private void generateEditText(String key, String value){
        MaterialEditText editText = new MaterialEditText(this);
        editText.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setHint(key);
        editText.setFloatingLabelText(key);
        editText.setSingleLine(true);
        editText.setTextColor(getColor(android.R.color.black));
        editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        editText.setPrimaryColor(getColor(R.color.colorPrimaryDark));
        editText.setUnderlineColor(getColor(R.color.colorPrimaryDark));

        editText.setText(value);

        layout_content.addView(editText);
        editTexts.add(editText);
    }

    private String generateString(){
        StringBuilder string = new StringBuilder();
        string.append("Type: ").append(getSupportActionBar().getTitle().toString()).append("\n");

        for (EditText editText : editTexts){
            string.append(editText.getHint().toString()).append(": ").append(editText.getText().toString()).append("\n");
        }

        return string.toString();
    }
}