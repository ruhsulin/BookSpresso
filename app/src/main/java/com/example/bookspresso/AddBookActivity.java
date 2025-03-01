package com.example.bookspresso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBookActivity extends AppCompatActivity {

    private Button btnAddBook, btnSelectImage, btnCancel;
    private EditText etTitle, etAuthor, etGenre, etPublishedYear, etISBN, etPageNumber, etDescription, etBorrowedTo, etBorrowedDate;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private ImageView ivBookImage;
    private Spinner spinnerStatus;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        btnAddBook = findViewById(R.id.btnAddBook);
        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etGenre = findViewById(R.id.etGenre);
        etPublishedYear = findViewById(R.id.etPublishedYear);
        etPageNumber = findViewById(R.id.etPageNumber);
        etISBN = findViewById(R.id.etIsbn);
        etDescription = findViewById(R.id.etDescription);
        etBorrowedTo = findViewById(R.id.etBorrowedTo);
        etBorrowedDate = findViewById(R.id.etBorrowedDate);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        ivBookImage = findViewById(R.id.ivBookImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnCancel = findViewById(R.id.btnCancel);

        databaseHelper = new DatabaseHelper(this);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Book");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back button
        }

        // Set up Spinner
        if (spinnerStatus != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.book_status_options,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStatus.setAdapter(adapter);
        }

        // Choose book image
        btnSelectImage.setOnClickListener(v -> openImagePicker());

        // Button to add book to DB
        btnAddBook.setOnClickListener(v -> addBookToDatabase());

        // Button Cancel
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(AddBookActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivBookImage.setImageURI(selectedImageUri);
        }
    }

    private void addBookToDatabase() {
        SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", "");

        if (userId.isEmpty()) {
            Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String genre = etGenre.getText().toString().trim();
        String publishedYear = etPublishedYear.getText().toString().trim();
        String isbn = etISBN.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String borrowedTo = etBorrowedTo.getText().toString().trim();
        String borrowedDate = etBorrowedDate.getText().toString().trim();
        String selectedStatus = spinnerStatus.getSelectedItem().toString();

        String imagePath = selectedImageUri != null ? selectedImageUri.toString() : null;

        int pageNumber;
        try {
            pageNumber = Integer.parseInt(etPageNumber.getText().toString().trim());
        } catch (NumberFormatException e) {
            pageNumber = 0;
        }

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        Book.BookStatus bookStatus = Book.BookStatus.valueOf(selectedStatus.toUpperCase().replace(" ", "_"));

        String registeredDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        boolean success = databaseHelper.insertBook(title, author, genre, publishedYear, isbn, pageNumber, description, bookStatus.name(), registeredDate, userId, borrowedTo, borrowedDate, imagePath);

        if (success) {
            Toast.makeText(this, "Book added successfully!", Toast.LENGTH_LONG).show();
            clearFields();

            // Redirect to AllBooksActivity
            Intent intent = new Intent(AddBookActivity.this, AllBooksActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Failed to add book", Toast.LENGTH_LONG).show();
        }
    }

    private void clearFields() {
        etTitle.setText("");
        etAuthor.setText("");
        etGenre.setText("");
        etPublishedYear.setText("");
        etPageNumber.setText("");
        etISBN.setText("");
        etDescription.setText("");
        spinnerStatus.setSelection(0);
    }

    // Go back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
