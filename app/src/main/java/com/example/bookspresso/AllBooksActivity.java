package com.example.bookspresso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AllBooksActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private BookAdapter bookAdapter;
    private DatabaseHelper databaseHelper;
    private List<Book> allBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_books);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Book");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back button
        }

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        // Load all books from DB
        new LoadBooksFromDBTask().execute();

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBooks(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return false;
            }
        });
    }

    private class LoadBooksFromDBTask extends AsyncTask<Void, Void, List<Book>> {
        @Override
        protected List<Book> doInBackground(Void... voids) {
            SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            String userId = prefs.getString("userId", "");
            return userId.isEmpty() ? new ArrayList<>() : databaseHelper.getAllBooksForUser(userId);
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            if (books != null) {
                allBooks = books;
                bookAdapter = new BookAdapter(AllBooksActivity.this, allBooks);
                recyclerView.setAdapter(bookAdapter);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Book updatedBook = (Book) data.getSerializableExtra("updatedBook");
            if (updatedBook != null) {
                updateBookInList(updatedBook);
            }
        }
    }

    // Method to update the book in RecyclerView
    private void updateBookInList(Book updatedBook) {
        for (int i = 0; i < allBooks.size(); i++) {
            if (allBooks.get(i).getId() == updatedBook.getId()) {
                allBooks.set(i, updatedBook);
                bookAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    // Filter books based on search query
    private void filterBooks(String query) {
        if (allBooks == null) {
            allBooks = new ArrayList<>();
        }

        if (TextUtils.isEmpty(query)) {
            // Show all books if query is empty
            bookAdapter = new BookAdapter(this, allBooks);
        } else {
            // Filter books by title or author
            List<Book> filteredBooks = new ArrayList<>();
            for (Book book : allBooks) {
                if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                    filteredBooks.add(book);
                }
            }
            bookAdapter = new BookAdapter(this, filteredBooks);
        }
        recyclerView.setAdapter(bookAdapter);
    }

    // Back button functionality
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
