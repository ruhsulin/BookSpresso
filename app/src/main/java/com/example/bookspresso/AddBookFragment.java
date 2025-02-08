package com.example.bookspresso;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookspresso.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBookFragment extends Fragment {

    Button btnAddBook;
    EditText etId, etTitle, etAuthor, etGenre, etPublishedYear, etISBN, etPageNumber, etDescription, etStatus, etRegisteredDate;
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
        etStatus = view.findViewById(R.id.etStatus);

        databaseHelper = new DatabaseHelper(requireContext());

        btnAddBook.setOnClickListener(v -> addBookToDatabase());
        return view;
    }
    private void addBookToDatabase() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", ""); // Merr ID e përdoruesit të loguar

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
        String status = etStatus.getText().toString().trim();

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

        // Get current date for RegisteredDate
        String registeredDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Insert into database
        boolean success = databaseHelper.insertBook(title, author, genre, publishedYear, isbn, pageNumber, description, status, registeredDate, userId);

        if (success) {
            Toast.makeText(requireContext(), "Book added successfully!", Toast.LENGTH_LONG).show();
            clearFields();
        } else {
            Toast.makeText(requireContext(), "Failed to add book", Toast.LENGTH_LONG).show();
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
        etStatus.setText("");
    }

}