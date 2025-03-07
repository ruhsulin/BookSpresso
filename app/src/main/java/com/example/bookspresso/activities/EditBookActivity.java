package com.example.bookspresso.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bookspresso.R;
import com.example.bookspresso.database.DatabaseHelper;
import com.example.bookspresso.models.Book;
import com.example.bookspresso.utils.ToastHelper;

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

        setupToolbar();
        initializeUI();
        populateSpinner();

        // Get book data
        book = (Book) getIntent().getSerializableExtra("editBook");
        if (book != null) {
            setBookDetails(book);
            toggleBorrowFields(book.getStatus() == Book.BookStatus.BORROWED);
        }

        // Listen for changes in the spinner selection
        spinnerStatus.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                toggleBorrowFields(getSelectedStatus().equals(Book.BookStatus.BORROWED));
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Button listeners
        findViewById(R.id.btnSave).setOnClickListener(v -> saveBookChanges());
        findViewById(R.id.btnCancel).setOnClickListener(v -> navigateBack());
    }

    // Setup Toolbar
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Book");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Initialize UI
    private void initializeUI() {
        dbHelper = new DatabaseHelper(this);
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
    }

    // Populate Spinner: Books Status
    private void populateSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.book_status_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
    }

    // Gets selected book status from spinner
    private Book.BookStatus getSelectedStatus() {
        return Book.BookStatus.valueOf(
                spinnerStatus.getSelectedItem().toString().toUpperCase().replace(" ", "_"));
    }

    // Set Book Details
    private void setBookDetails(Book book) {
        etTitle.setText(book.getTitle());
        etAuthor.setText(book.getAuthor());
        etGenre.setText(book.getGenre());
        etPublishedYear.setText(String.valueOf(book.getPublishedYear()));
        etISBN.setText(book.getISBN());
        etPageNumber.setText(String.valueOf(book.getPageNumber()));
        etDescription.setText(book.getDescription());

        int spinnerPosition = ((ArrayAdapter<String>) spinnerStatus.getAdapter())
                .getPosition(book.getStatus().toString().replace("_", " "));
        spinnerStatus.setSelection(spinnerPosition);

        if (book.getStatus() == Book.BookStatus.BORROWED) {
            etBorrowedTo.setText(book.getBorrowedTo() != null ? book.getBorrowedTo() : "");
            etBorrowedDate.setText(book.getBorrowedDate() != null ? book.getBorrowedDate() : "");
        }
    }

    // Saved updated book details in database
    private void saveBookChanges() {
        if (!validateInputs()) return;

        book.setTitle(etTitle.getText().toString().trim());
        book.setAuthor(etAuthor.getText().toString().trim());
        book.setGenre(etGenre.getText().toString().trim());
        book.setPublishedYear(Integer.parseInt(etPublishedYear.getText().toString().trim()));
        book.setISBN(etISBN.getText().toString().trim());
        book.setPageNumber(Integer.parseInt(etPageNumber.getText().toString().trim()));
        book.setDescription(etDescription.getText().toString().trim());
        book.setStatus(getSelectedStatus());

        if (book.getStatus() == Book.BookStatus.BORROWED) {
            if (!validateBorrowDetails()) return;
            book.setBorrowedTo(etBorrowedTo.getText().toString().trim());
            book.setBorrowedDate(etBorrowedDate.getText().toString().trim());
        } else {
            book.setBorrowedTo("");
            book.setBorrowedDate("");
        }

        if (dbHelper.updateBook(book)) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedBook", book);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            ToastHelper.showToast(this,"Failed to update book!");
        }
    }

    // Validates borrowed books details when books status is Borrowed.
    private boolean validateBorrowDetails() {
        if (TextUtils.isEmpty(etBorrowedTo.getText())) {
            ToastHelper.showToast(this,"Please enter a name for Borrowed To!");
            return false;
        }
        if (TextUtils.isEmpty(etBorrowedDate.getText())) {
            etBorrowedDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        }
        return true;
    }

    // Validets input fields.
    private boolean validateInputs() {
        if (TextUtils.isEmpty(etTitle.getText())) {
            ToastHelper.showToast(this,"Please enter a book title!");
            return false;
        }
        if (TextUtils.isEmpty(etAuthor.getText())) {
            ToastHelper.showToast(this,"Please enter the author's name!");
            return false;
        }
        if (TextUtils.isEmpty(etGenre.getText())) {
            ToastHelper.showToast(this,"Please enter a genre!");
            return false;
        }
        if (TextUtils.isEmpty(etPublishedYear.getText())) {
            ToastHelper.showToast(this,"Please enter the published year!");
            return false;
        }
        return true;
    }

    // Show or hide the borrowed fields based on book status
    private void toggleBorrowFields(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        etBorrowedTo.setVisibility(visibility);
        etBorrowedDate.setVisibility(visibility);
    }

    // Back button functionality: go back arrow
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Navigate back: Cancel btn;
    private void navigateBack() {
        finish();
    }
}
