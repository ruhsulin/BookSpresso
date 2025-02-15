package com.example.bookspresso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class BookDetailsActivity extends AppCompatActivity {
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("BookSpresso");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
        }

        // Get book data from Intent
        book = (Book) getIntent().getSerializableExtra("book");

        if (book != null) {
            updateBookDetailsUI();
        }

        // Open EditBookActivity
        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailsActivity.this, EditBookActivity.class);
            intent.putExtra("editBook", book);
            startActivityForResult(intent, 1);
        });
    }

    // Updates UI with the book details
    private void updateBookDetailsUI() {
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvAuthor = findViewById(R.id.tvAuthor);
        TextView tvGenre = findViewById(R.id.tvGenre);
        TextView tvPublishedYear = findViewById(R.id.tvPublishedYear);
        TextView tvISBN = findViewById(R.id.tvISBN);
        TextView tvPageNumber = findViewById(R.id.tvPageNumber);
        TextView tvDescription = findViewById(R.id.tvDescription);

        tvTitle.setText(getString(R.string.title_format, book.getTitle()));
        tvAuthor.setText(getString(R.string.author_format, book.getAuthor()));
        tvGenre.setText(getString(R.string.genre_format, book.getGenre()));
        tvPublishedYear.setText(getString(R.string.published_year_format, book.getPublishedYear()));
        tvISBN.setText(getString(R.string.isbn_format, book.getISBN()));
        tvPageNumber.setText(getString(R.string.pages_format, book.getPageNumber()));
        tvDescription.setText(getString(R.string.description_format, book.getDescription()));
    }

    // Handle the result from EditBookActivity and update UI
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Book updatedBook = (Book) data.getSerializableExtra("updatedBook");
            if (updatedBook != null) {
                book = updatedBook;
                updateBookDetailsUI();

                // Send updated book back to HomeFragment
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedBook", updatedBook);
                setResult(RESULT_OK, resultIntent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        Book updatedBook = (Book) intent.getSerializableExtra("updatedBook");

        if (updatedBook != null) {
            book = updatedBook;
            updateBookDetailsUI();
            intent.removeExtra("updatedBook");
        }
    }

    // GoBack button functionality
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
