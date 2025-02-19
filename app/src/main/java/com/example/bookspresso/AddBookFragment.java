package com.example.bookspresso;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBookFragment extends Fragment {

    Button btnAddBook;
    EditText  etTitle, etAuthor, etGenre, etPublishedYear, etISBN, etPageNumber, etDescription, etBorrowedTo, etBorrowedDate;
    private Spinner spinnerStatus;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        btnAddBook = view.findViewById(R.id.btnAddBook);
        etTitle = view.findViewById(R.id.etTitle);
        etAuthor = view.findViewById(R.id.etAuthor);
        etGenre = view.findViewById(R.id.etGenre);
        etPublishedYear = view.findViewById(R.id.etPublishedYear);
        etPageNumber = view.findViewById(R.id.etPageNumber);
        etISBN = view.findViewById(R.id.etIsbn);
        etDescription = view.findViewById(R.id.etDescription);
        etBorrowedTo = view.findViewById(R.id.etBorrowedTo);
        etBorrowedDate = view.findViewById(R.id.etBorrowedDate);
        spinnerStatus = view.findViewById(R.id.spinnerStatus);

        databaseHelper = new DatabaseHelper(requireContext());

        // Set up Spinner
        if (spinnerStatus != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.book_status_options,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStatus.setAdapter(adapter);
        }

        // Button to add book to DB
        btnAddBook.setOnClickListener(v -> addBookToDatabase());

        return view;
    }

    // Saves Book to DB.
    private void addBookToDatabase() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", "");

        if (userId.isEmpty()) {
            Toast.makeText(requireContext(), "User ID not found!", Toast.LENGTH_SHORT).show();
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


        int pageNumber;
        try {
            pageNumber = Integer.parseInt(etPageNumber.getText().toString().trim());
        } catch (NumberFormatException e) {
            pageNumber = 0;
        }

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert status from String to Enum
        Book.BookStatus bookStatus = Book.BookStatus.valueOf(selectedStatus.toUpperCase().replace(" ", "_"));

        // Get current date for RegisteredDate
        String registeredDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Insert into database
        boolean success = databaseHelper.insertBook(title, author, genre, publishedYear, isbn, pageNumber, description, bookStatus.name(), registeredDate, userId, borrowedTo, borrowedDate);

        if (success) {
            Toast.makeText(requireContext(), "Book added successfully!", Toast.LENGTH_LONG).show();
            clearFields();
        } else {
            Toast.makeText(requireContext(), "Failed to add book", Toast.LENGTH_LONG).show();
        }
    }

    // Clearing Book Fields
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
}