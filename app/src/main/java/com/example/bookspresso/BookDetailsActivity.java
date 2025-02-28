package com.example.bookspresso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
        ImageView btnEdit = findViewById(R.id.btnEdit);
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
        TextView tvBorrowedBy = findViewById(R.id.tvBorrowedBy);
        TextView tvStatus = findViewById(R.id.tvStatus);
        ImageView ivBookImage = findViewById(R.id.ivBookImage);

        tvTitle.setText(getString(R.string.empty_string, book.getTitle()));
        tvAuthor.setText(getString(R.string.author_format, book.getAuthor()));
        tvGenre.setText(getString(R.string.genre_format, book.getGenre()));
        tvPublishedYear.setText(getString(R.string.published_year_format, book.getPublishedYear()));
        tvISBN.setText(getString(R.string.isbn_format, book.getISBN()));
        tvPageNumber.setText(getString(R.string.pages_format, book.getPageNumber()));
        tvDescription.setText(getString(R.string.description_format, book.getDescription()));

        // Load book image using Glide
        if (book.getImagePath() != null && !book.getImagePath().isEmpty()) {
            Glide.with(this)
                    .load(book.getImagePath())
                    .placeholder(R.drawable.rounded_corner_image)
                    .error(R.drawable.ic_image)
                    .into(ivBookImage);
        } else {
            ivBookImage.setImageResource(R.drawable.default_book_image);
        }

        // Set book status
        tvStatus.setText(getString(R.string.status_format, book.getStatus().name()));

        // If the book is borrowed, show borrowed data
        if (book.getStatus() == Book.BookStatus.BORROWED) {
            String borrowedTo = book.getBorrowedTo();
            String borrowedDate = book.getBorrowedDate();

            if (borrowedTo == null || borrowedTo.isEmpty()) {
                borrowedTo = "Unknown Person";
            }
            if (borrowedDate == null || borrowedDate.isEmpty()) {
                borrowedDate = "Unknown Date";
            }

            tvBorrowedBy.setText("Borrowed by " + borrowedTo + " on " + borrowedDate);
            tvBorrowedBy.setVisibility(View.VISIBLE);
            tvStatus.setVisibility(View.GONE);
        } else {
            tvStatus.setText(getString(R.string.status_format, book.getStatus().name()));
            tvStatus.setVisibility(View.VISIBLE);
            tvBorrowedBy.setVisibility(View.GONE);
        }
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

                // Send updated book back to AllBooksActivity
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
