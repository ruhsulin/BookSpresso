package com.example.bookspresso;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final String Table_Books = "Books";
    public DatabaseHelper(@Nullable Context context) {
        super(context, "BookSpressoDB", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + Table_Books + "(" +
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
        // Drop the old table if it exists
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Table_Books);
        // Recreate the table
        onCreate(sqLiteDatabase);

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

        long result = db.insert(Table_Books, null, values);
        db.close();
        return result != -1;
    }

    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * From " + Table_Books, null);

        if(cursor.moveToFirst()){
            do{
                Book book = new Book();
                book.setId(cursor.getInt(0));
                book.setTitle(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setGenre(cursor.getString(3));
                book.setPublishedYear(cursor.getInt(4));
                book.setISBN(cursor.getString(5));
                book.setPageNumber(cursor.getInt(6));
                book.setDescription(cursor.getString(7));
                book.setStatus(cursor.getString(8));
                book.setRegisteredDate(cursor.getString(9));

                bookList.add(book);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return bookList;
    }
}