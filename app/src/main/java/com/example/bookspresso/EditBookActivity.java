package com.example.bookspresso;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditBookActivity extends AppCompatActivity {
    private Book book;
    private EditText etTitle, etAuthor, etGenre, etPublishedYear, etISBN, etPageNumber, etDescription, etBorrowedTo, etBorrowedDate;
    private Spinner spinnerStatus;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Book");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back button
        }

        //Database instance
        dbHelper = new DatabaseHelper(this);

        // UI Elements
        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etGenre = findViewById(R.id.etGenre);
        etPublishedYear = findViewById(R.id.etPublishedYear);
        etISBN = findViewById(R.id.etISBN);
        etPageNumber = findViewById(R.id.etPageNumber);
        etDescription = findViewById(R.id.etDescription);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        etBorrowedTo = findViewById(R.id.etBorrowedTo);
        etBorrowedDate = findViewById(R.id.etBorrowedDate);
        Button btnSave = findViewById(R.id.btnSave);

        // Populate spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.book_status_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Get book data
        book = (Book) getIntent().getSerializableExtra("editBook");
        if (book != null) {
            etTitle.setText(book.getTitle());
            etAuthor.setText(book.getAuthor());
            etGenre.setText(book.getGenre());
            etPublishedYear.setText(String.valueOf(book.getPublishedYear()));
            etISBN.setText(book.getISBN());
            etPageNumber.setText(String.valueOf(book.getPageNumber()));
            etDescription.setText(book.getDescription());

            // Set spinner to current book status
            int spinnerPosition = adapter.getPosition(book.getStatus().toString().replace("_", " "));
            spinnerStatus.setSelection(spinnerPosition);


            // Set borrowed details if the book is already borrowed
            if (book.getStatus() == Book.BookStatus.BORROWED) {
                etBorrowedTo.setVisibility(View.VISIBLE);
                etBorrowedDate.setVisibility(View.VISIBLE);
                etBorrowedTo.setText(book.getBorrowedTo() != null ? book.getBorrowedTo() : "");
                etBorrowedDate.setText(book.getBorrowedDate() != null ? book.getBorrowedDate() : "");
            } else {
                etBorrowedTo.setVisibility(View.GONE);
                etBorrowedDate.setVisibility(View.GONE);
                etBorrowedTo.setText("");
                etBorrowedDate.setText("");
            }

            // Show borrowed fields only if the book is marked as BORROWED
            toggleBorrowFields(book.getStatus() == Book.BookStatus.BORROWED);
        }

        // Listen for changes in the spinner selection
        spinnerStatus.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = parent.getItemAtPosition(position).toString().toUpperCase().replace(" ", "_");
                toggleBorrowFields(selectedStatus.equals("BORROWED"));
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Load existing book details
        if (book != null) {
            etBorrowedTo.setText(book.getBorrowedTo());
            etBorrowedDate.setText(book.getBorrowedDate());

            // Show fields if the book is borrowed
            if (book.getStatus() == Book.BookStatus.BORROWED) {
                etBorrowedTo.setVisibility(View.VISIBLE);
                etBorrowedDate.setVisibility(View.VISIBLE);
            }
        }

        // Save button click
        btnSave.setOnClickListener(v -> {
            // Update book object with new data
            book.setTitle(etTitle.getText().toString());
            book.setAuthor(etAuthor.getText().toString());
            book.setGenre(etGenre.getText().toString());
            book.setPublishedYear(Integer.parseInt(etPublishedYear.getText().toString()));
            book.setISBN(etISBN.getText().toString());
            book.setPageNumber(Integer.parseInt(etPageNumber.getText().toString()));
            book.setDescription(etDescription.getText().toString());

            // Update status from spinner
            String selectedStatus = spinnerStatus.getSelectedItem().toString();
            book.setStatus(Book.BookStatus.valueOf(selectedStatus.toUpperCase().replace(" ", "_")));

            // If the book is borrowed, save borrower details
            if (book.getStatus() == Book.BookStatus.BORROWED) {
                String borrowedTo = etBorrowedTo.getText().toString().trim();
                String borrowedDate = etBorrowedDate.getText().toString().trim();

                if (borrowedTo.isEmpty()) {
                    Toast.makeText(EditBookActivity.this, "Please enter a name for Borrowed To!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (borrowedDate.isEmpty()) {
                    borrowedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                }

                book.setBorrowedTo(borrowedTo);
                book.setBorrowedDate(borrowedDate);
            } else {
                book.setBorrowedTo("");
                book.setBorrowedDate("");
            }

            // Update in database
            boolean updated = dbHelper.updateBook(book);
            if (updated) {
                // Send updated book back
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedBook", book);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(EditBookActivity.this, "Failed to update book!", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Show or hide the borrowed fields based on book status
    private void toggleBorrowFields(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        etBorrowedTo.setVisibility(visibility);
        etBorrowedDate.setVisibility(visibility);
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
