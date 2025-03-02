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
        super(context, "BookSpressoDB", null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + Table_Books + "(" +
                "Id integer primary key autoincrement," + //0
                "Title text," + //1
                "Author text," + //2
                "Genre text," + //3
                "PublishedYear integer," + //4
                "ISBN text," + //5
                "PageNumber integer," + //6
                "Description text," + //7
                "Status text," + //8
                "RegisteredDate text," + //9
                "UserId Text," + //10
                "BorrowedTo Text," + //11
                "BorrowedDate Text," + //12
                "ImagePath Text)"); //13
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            sqLiteDatabase.execSQL("ALTER TABLE Books ADD COLUMN ImagePath TEXT");
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
                              String userId,
                              String borrowedTo,
                              String borrowedDate,
                              String imagePath) {
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
        values.put("BorrowedTo", borrowedTo);
        values.put("BorrowedDate", borrowedDate);
        values.put("ImagePath", imagePath);

        long result = db.insert(Table_Books, null, values);
        db.close();
        return result != -1;
    }

    // edit book
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
        // Handle borrowed fields properly
        if (book.getStatus() == Book.BookStatus.BORROWED) {
            values.put("BorrowedTo", book.getBorrowedTo() != null ? book.getBorrowedTo() : "");
            values.put("BorrowedDate", book.getBorrowedDate() != null ? book.getBorrowedDate() : "");
        } else {
            values.put("BorrowedTo", "");  // Clear value if not borrowed
            values.put("BorrowedDate", "");
        }

        int rowsAffected = db.update("Books", values, "Id = ?", new String[]{String.valueOf(book.getId())});
        db.close();

        return rowsAffected > 0;
    }

    // delete book
    public boolean deleteBook(int bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete("Books", "Id = ?", new String[]{String.valueOf(bookId)});
        db.close();
        return deletedRows > 0;
    }

    // get books where status == ALL
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

                // get Borrowed books info
                String borrowedTo = cursor.getString(11);
                String borrowedDate = cursor.getString(12);
                book.setImagePath(cursor.getString(13));

                book.setBorrowedTo(borrowedTo != null ? borrowedTo : "");
                book.setBorrowedDate(borrowedDate != null ? borrowedDate : "");

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
                new String[]{userId, "FINISHED"});

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
                book.setStatus(Book.BookStatus.FINISHED);
                book.setRegisteredDate(cursor.getString(9));

                bookList.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bookList;
    }

    // get books where status == Borrowed
    public List<Book> getBorrowedBooksForUser(String userId) {
        List<Book> bookList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Books WHERE UserId = ? AND Status = ?",
                new String[]{userId, "BORROWED"});

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
                book.setStatus(Book.BookStatus.BORROWED);
                book.setRegisteredDate(cursor.getString(9));

                // get Borrowed books info
                String borrowedTo = cursor.getString(11);
                String borrowedDate = cursor.getString(12);

                System.out.println("Retrieved from DB - BorrowedTo: " + borrowedTo + ", BorrowedDate: " + borrowedDate);

                book.setBorrowedTo(borrowedTo != null ? borrowedTo : "");
                book.setBorrowedDate(borrowedDate != null ? borrowedDate : "");

                bookList.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bookList;
    }

    // Book Statistics
    // get number of all books.
    public int getTotalBooksCount(String userId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM BOOKS WHERE UserId = ?", new String[]{userId});

        int count = 0;
        if(cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // Get count of Read books
    public int getReadBooksCount(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Books WHERE UserId = ? AND Status = ?", new String[]{userId, "FINISHED"});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // Get count of Borrowed books
    public int getBorrowedBooksCount(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Books WHERE UserId = ? AND Status = ?", new String[]{userId, "BORROWED"});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}
