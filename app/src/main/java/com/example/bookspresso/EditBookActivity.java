package com.example.bookspresso;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EditBookActivity extends AppCompatActivity {
    private Book book;
    private EditText etTitle, etAuthor, etGenre, etPublishedYear, etISBN, etPageNumber, etDescription;
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
        Button btnSave = findViewById(R.id.btnSave);

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
