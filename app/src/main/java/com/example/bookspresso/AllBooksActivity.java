package com.example.bookspresso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

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
    private DatabaseHelper dbHelper;
    private List<Book> allBooks = new ArrayList<>();
    private String filterType;

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

        dbHelper = new DatabaseHelper(this);

        // Get filter type from intent
        filterType = getIntent().getStringExtra("FILTER_TYPE");

        // Load books based on filter
        loadBooks(filterType);

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

    private void loadBooks(String filter) {
        SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", "");

        if (userId.isEmpty()) {
            Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Book> bookList;

        switch (filter) {
            case "READ":
                bookList = dbHelper.getReadBooksForUser(userId);
                break;
            case "BORROWED":
                bookList = dbHelper.getAllBooksForUser(userId);
                break;
            default:
                bookList = dbHelper.getAllBooksForUser(userId);
                break;
        }
        if (bookList.isEmpty()) {
            Toast.makeText(this, "No books found!", Toast.LENGTH_SHORT).show();
        }

        updateRecyclerView(bookList);
    }

    private void updateRecyclerView(List<Book> bookList) {
        bookAdapter = new BookAdapter(this, bookList);
        recyclerView.setAdapter(bookAdapter);
        bookAdapter.notifyDataSetChanged();
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
