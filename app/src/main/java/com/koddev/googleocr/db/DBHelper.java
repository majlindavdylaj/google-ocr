package com.koddev.googleocr.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.koddev.googleocr.model.Document;

import java.sql.Blob;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "google_ocr.db";
    private static final int DB_VERSION = 3;

    private static final String D_TABLE = "Documents";

    private static final String id = "id";
    private static final String type = "type";
    private static final String json = "json";
    private static final String date = "date";
    private static final String image = "image";

    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.db = sqLiteDatabase;

        createDocumentsTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + D_TABLE);

        onCreate(db);
    }

    private void createDocumentsTable(){
        String APPS_TABLE = "CREATE TABLE " + D_TABLE + " (" +
                id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                type + " TEXT, " +
                json + " TEXT, " +
                date + " TEXT, " +
                image + " TEXT " +
                " )";

        db.execSQL(APPS_TABLE);
    }

    public boolean addDocument(Document document){
        db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(type, document.getType());
        contentValues.put(json, document.getJson());
        contentValues.put(date, document.getDate());
        contentValues.put(image, document.getImage());

        long result = db.insert(D_TABLE, null, contentValues);

        return result != -1;
    }

    public ArrayList<Document> getAllDocuments(){
        ArrayList<Document> documents = new ArrayList<>();
        db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + D_TABLE + " ORDER BY "+id+" DESC", null);

        if(cursor.moveToFirst()){
            do {

                Document document = new Document(
                        cursor.getString(cursor.getColumnIndex(type)),
                        cursor.getString(cursor.getColumnIndex(json)),
                        cursor.getString(cursor.getColumnIndex(date)),
                        cursor.getString(cursor.getColumnIndex(image))
                );
                document.setId(cursor.getInt(cursor.getColumnIndex(id)));

                documents.add(document);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return documents;
    }

    public boolean deleteDocument(String i) {
        db = getReadableDatabase();
        return db.delete(D_TABLE, id + "=?", new String[]{i}) > 0;
    }
}