package com.example.bookspresso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class BookDetailsActivity extends AppCompatActivity {
    private Book book;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        dbHelper = new DatabaseHelper(this);

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

        // Favorites Book
        ImageView btnFavorite = findViewById(R.id.btnFavorite);
        updateFavoriteIcon();
        btnFavorite.setOnClickListener(v -> toggleFavorite());

        // Edit Book
        ImageView btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailsActivity.this, EditBookActivity.class);
            intent.putExtra("editBook", book);
            startActivityForResult(intent, 1);
        });

        // Delete Book
        ImageView btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(view -> showDeleteConfirmationDialog());
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
                    .load(Uri.parse(book.getImagePath()))
                    .placeholder(R.drawable.normal_book_image)
                    .error(R.drawable.error_book_image)
                    .into(ivBookImage);
        } else {
            ivBookImage.setImageResource(R.drawable.normal_book_image);
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
        }
        else {
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

    // Delete Book Confirmation
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Book")
                .setMessage("Are you sure you want to delete this book?")
                .setPositiveButton("Yes", (dialog, which) -> deleteBook())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteBook() {
        if (book != null) {
            boolean isDeleted = dbHelper.deleteBook(book.getId());

            if (isDeleted) {
                Toast.makeText(this, "Book deleted successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to delete book", Toast.LENGTH_SHORT).show();
            }
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

    // Toggle for Favorite Book button
    private void toggleFavorite() {
        if (book == null) return;

        boolean isFavorite = dbHelper.isBookFavorite(book.getId());

        if (isFavorite) {
            dbHelper.removeBookFromFavorites(book.getId());
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.addBookToFavorites(book.getId());
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        }

        updateFavoriteIcon();
    }

    // Update Favorite Icon - fill - unfilled
    private void updateFavoriteIcon() {
        ImageView btnFavorite = findViewById(R.id.btnFavorite);

        if (dbHelper.isBookFavorite(book.getId())) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_full); // Full heart
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite); // Empty heart
        }
    }
}
