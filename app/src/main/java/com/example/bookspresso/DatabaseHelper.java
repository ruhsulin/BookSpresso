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
        super(context, "BookSpressoDB", null, 5);
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
                "RegisteredDate text," +
                "UserId Text," +
                "BorrowedTo Text," +
                "BorrowedDate Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            sqLiteDatabase.execSQL("ALTER TABLE Books ADD COLUMN BorrowedTo TEXT");
            sqLiteDatabase.execSQL("ALTER TABLE Books ADD COLUMN BorrowedDate  TEXT");
        }
    }

    public boolean insertBook(String title,
                              String author,
                              String genre,
                              String year,
                              String isbn,
                              int pageNumber,
                              String description,
                              String status,
                              String registeredDate,
                              String userId) {
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
        values.put("UserId", userId);

        long result = db.insert(Table_Books, null, values);
        db.close();
        return result != -1;
    }

    public List<Book> getAllBooksForUser(String userId) {
        List<Book> bookList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Books WHERE UserId = ?", new String[]{userId});

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
                book.setStatus(Book.BookStatus.valueOf(cursor.getString(8).toUpperCase()));
                book.setRegisteredDate(cursor.getString(9));

                bookList.add(book);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bookList;
    }

    // get books where status == READ
    public List<Book> getReadBooksForUser(String userId) {
        List<Book> bookList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Books WHERE UserId = ? AND Status = ?",
                new String[]{userId, "READING"});

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId(cursor.getInt(0));
                book.setTitle(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setGenre(cursor.getString(3));
                book.setPublishedYear(cursor.getInt(4));
                book.setISBN(cursor.getString(5));
                book.setPageNumber(cursor.getInt(6));
                book.setDescription(cursor.getString(7));
                book.setStatus(Book.BookStatus.READING); // Ensure it matches Enum value
                book.setRegisteredDate(cursor.getString(9));

                bookList.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bookList;
    }
    public boolean updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("Title", book.getTitle());
        values.put("Author", book.getAuthor());
        values.put("Genre", book.getGenre());
        values.put("PublishedYear", book.getPublishedYear());
        values.put("ISBN", book.getISBN());
        values.put("PageNumber", book.getPageNumber());
        values.put("Description", book.getDescription());
        values.put("Status", book.getStatus().name());

        int rowsAffected = db.update("Books", values, "Id = ?", new String[]{String.valueOf(book.getId())});
        db.close();

        return rowsAffected > 0;
    }
}