package com.koddev.googleocr.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.koddev.googleocr.db.DBHelper;
import com.koddev.googleocr.model.Bucket;
import com.koddev.googleocr.model.Document;
import com.tom_roush.pdfbox.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static String checkString(String s){
        if (s == null){
            s = "N/A";
        }
        return s;
    }

    public static boolean isValidDateFormat(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }

    public static boolean getSubStractYear(String date)
    {
        try
        {

            SimpleDateFormat sdf = new SimpleDateFormat ( "dd.MM.yyyy" );
            Date myDate = sdf.parse(date);

            Date currentTime = Calendar.getInstance().getTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTime);
            calendar.add(Calendar.YEAR , -16 );

            if (myDate.before(calendar.getTime())){
                return true;
            }
            return false;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<String> imagesFromGallery(Context context){
        Uri uri;
        Cursor cursor;
        int column_index_data;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String ablosutePathOfImage;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection, null,
                null, orderBy+" DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()){
            ablosutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(ablosutePathOfImage);
        }

        return listOfAllImages;
    }

    public static List<Bucket> getImageBuckets(Context mContext){
        List<Bucket> buckets = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String [] projection = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};
        String orderBy = MediaStore.Images.Media.DATE_ADDED+" DESC";

        buckets.add(new Bucket("All", getAllLastImage(mContext)));

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, orderBy);
        if(cursor != null){
            File file;
            while (cursor.moveToNext()){
                String bucketPath = cursor.getString(cursor.getColumnIndex(projection[0]));
                String fisrtImage = cursor.getString(cursor.getColumnIndex(projection[1]));
                file = new File(fisrtImage);
                if (file.exists() && !containsFolder(buckets, bucketPath)) {
                    buckets.add(new Bucket(bucketPath, fisrtImage));
                }
            }
            cursor.close();
        }
        return buckets;
    }

    public static List<String> getImagesByBucket(@NonNull String bucketPath, Context mContext){

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String [] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME+" =?";
        String orderBy = MediaStore.Images.Media.DATE_ADDED+" DESC";

        List<String> images = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(uri, projection, selection,new String[]{bucketPath}, orderBy);

        if(cursor != null){
            File file;
            while (cursor.moveToNext()){
                String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                file = new File(path);
                if (file.exists() && !images.contains(path)) {
                    images.add(path);
                }
            }
            cursor.close();
        }
        return images;
    }

    public static List<String> getAllImages(Context mContext){

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String [] projection = {MediaStore.Images.Media.DATA};
        //String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME+" =?";
        String orderBy = MediaStore.Images.Media.DATE_ADDED+" DESC";

        List<String> images = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null,null, orderBy);

        if(cursor != null){
            File file;
            while (cursor.moveToNext()){
                String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                file = new File(path);
                if (file.exists() && !images.contains(path)) {
                    images.add(path);
                }
            }
            cursor.close();
        }
        return images;
    }

    private static String getAllLastImage(Context mContext){

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String [] projection = {MediaStore.Images.Media.DATA};
        String orderBy = MediaStore.Images.Media.DATE_ADDED+" DESC";

        List<String> images = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null,null, orderBy);

        if(cursor != null){
            File file;
            while (cursor.moveToNext()){
                String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                file = new File(path);
                if (file.exists() && !images.contains(path)) {
                    return path;
                }
            }
            cursor.close();
        }
        return "";
    }

    public static boolean containsFolder(final List<Bucket> list, final String name){
        for(Bucket b : list) {
            if(b != null && b.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static Bitmap fixRotateImage(String photoPath, Bitmap bitmap){

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

    public static List<String> getAllPDFs(File dir) {
        List<String> folders =  new ArrayList<>();
        readPDFs(folders, dir);

        return folders;
    }

    private static void readPDFs(List<String> folders, File dir){
        File[] fileList = dir.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    readPDFs(folders, file);
                } else {
                    if (file.getName().endsWith(".pdf")) {
                        folders.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    public static File convertToFile(InputStream inputStream){
        File tempFile = null;
        try {
            tempFile = File.createTempFile("stream2file", ".tmp");
            tempFile.deleteOnExit();
            FileOutputStream out = new FileOutputStream(tempFile);
            IOUtils.copy(inputStream, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile;
    }

    public static void bitmapConvertToFile(final Bitmap bitmap, final Context context,
                                           final String type, String params) {
        FileOutputStream fileOutputStream = null;
        final Handler handler = new Handler();
        String currrentImage = "IMG" + System.currentTimeMillis() + ".png";
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory("ScannerApp"), "");
            if (!file.exists()) {
                file.mkdir();
            }
            final File bitmapFile = new File(file, currrentImage);
            fileOutputStream = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            MediaScannerConnection.scanFile(context, new String[]{bitmapFile.getAbsolutePath()}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {

                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    handler.post(() -> {
                        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
                        Document document = new Document(
                                type,
                                params,
                                date,
                                bitmapFile.getAbsolutePath()
                        );

                        if (new DBHelper(context).addDocument(document)){
                            Toast.makeText(context, "Document saved!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
