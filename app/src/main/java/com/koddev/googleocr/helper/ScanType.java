package com.koddev.googleocr.helper;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.koddev.googleocr.ui.ResultImageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScanType {

    public static HashMap<String, String> params;

    public static void idUBT(FirebaseVisionText resultText){
        params = new HashMap<>();
        params.put("type", "ID UBT");

        List<FirebaseVisionText.Line> l = myLines(resultText);

        for(int j = 0; j < l.size(); j++) {

            String line = l.get(j).getText().toLowerCase();

            if (line.contains("emri") || line.contains("mbiemri")
                    || line.contains("name") || line.contains("surname")) {
                params.put("Full Name", l.get(j - 1).getText());
            }

            if (Utils.isValidDateFormat("d/M/yyyy", line.split(" ")[0])) {
                params.put("Birthday", line.split(" ")[0]);
            }

            if (line.matches("\\d{4}/\\d{4}")) {
                params.put("Register date", line);
            }

            if (line.contains("llogaria") || line.contains("account")) {
                params.put("Account", l.get(j - 1).getText());
            }

            if (line.length() == 9 && TextUtils.isDigitsOnly(line)) {
                params.put("ID", line);
            }
        }
    }

    public static void idKOSOVO(FirebaseVisionText resultText){
        params = new HashMap<>();
        params.put("type", "ID Kosovo");

        List<FirebaseVisionText.Line> l = myLines(resultText);

        for(int j = 0; j < l.size(); j++) {

            String line = l.get(j).getText().toLowerCase();

            if (line.contains("mbiemri") || line.contains("surname")) {
                params.put("Last Name", l.get(j + 1).getText());
            }

            if (line.contains("emri") || line.contains("name")) {
                params.put("First Name", l.get(j + 1).getText());
            }

            if (line.contains("nenshkrimi") || line.contains("signature")) {
                if (!(l.get(j - 1).getText()).contains(".")) {
                    params.put("Birth place", l.get(j - 1).getText());
                }
            }

            if (Utils.isValidDateFormat("dd.MM.yyyy", line) && Utils.getSubStractYear(line)) {
                params.put("Birthday", line);
            }
        }
    }

    public static void pdf(FirebaseVisionText resultText){
        params = new HashMap<>();
        params.put("type", "PDF");

        List<FirebaseVisionText.Line> l = myLines(resultText);

        for(int j = 0; j < l.size(); j++) {

            String line = l.get(j).getText().toLowerCase();

            if (line.contains("first name")) {
                params.put("First Name", l.get(j + 1).getText());
            }

            if (line.contains("last name")) {
                params.put("Last Name", l.get(j + 1).getText());
            }

            if (line.contains("phone number")) {
                params.put("Phone number", l.get(j + 1).getText());
            }

            if (line.contains("e-mail")) {
                params.put("E-mail", l.get(j + 1).getText());
            }

            if (line.contains("address")) {
                params.put("Address", l.get(j + 1).getText());
            }

            if (line.contains("postal code")) {
                params.put("Postal Code", l.get(j + 1).getText());
            }

            if (line.contains("city")) {
                params.put("City", l.get(j + 1).getText());
            }
        }
    }

    private static List<FirebaseVisionText.Line> myLines(FirebaseVisionText resultText){
        List<FirebaseVisionText.Line> myLines = new ArrayList<>();
        for (int i = 0; i < resultText.getTextBlocks().size(); i++) {
            List<FirebaseVisionText.Line> l = resultText.getTextBlocks().get(i).getLines();
            for(int j = 0; j < l.size(); j++) {
                myLines.add(l.get(j));
            }
        }

        return myLines;
    }
}
