package com.example.bookspresso;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, "BookSpressoDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table Books(" +
                "Id integer primary key autoincrement," +
                "Title text," +
                "Author text," +
                "Genre text," +
                "PublishedYear integer," +
                "ISBN text," +
                "PageNumber integer," +
                "Description text," +
                "Status text," +
                "RegisteredDate text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertBook(String title, String author, String genre, String year, String isbn,
                              int pageNumber, String description, String status, String registeredDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", title);
        values.put("Author", author);
        values.put("Genre", genre);
        values.put("PublishedYear", year);
        values.put("ISBN", isbn);
        values.put("PageNumber", pageNumber);
        values.put("Description", description);
        values.put("Status", status);
        values.put("RegisteredDate", registeredDate);

        long result = db.insert("Books", null, values);
        db.close();
        return result != -1;
    }
}