package com.example.bookspresso;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditBookFragment extends Fragment {
    private EditText etTitle, etAuthor, etGenre, etPublishedYear, etISBN, etPageNumber, etDescription, etStatus;
    private Button btnUpdateBook;
    private DatabaseHelper databaseHelper;
    private Book book;

    // Empty Constructor
    public EditBookFragment() {
    }

    public static EditBookFragment newInstance(Book book) {
        EditBookFragment fragment = new EditBookFragment();

        Bundle args = new Bundle();

        args.putInt("id", book.getId());
        args.putString("title", book.getTitle());
        args.putString("author", book.getAuthor());
        args.putString("genre", book.getGenre());
        args.putInt("publishedYear", book.getPublishedYear());
        args.putString("isbn", book.getISBN());
        args.putInt("pageNumber", book.getPageNumber());
        args.putString("description", book.getDescription());
        args.putString("status", book.getStatus());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_book, container, false);

        databaseHelper = new DatabaseHelper(requireContext());

        // Bind UI elements
        etTitle = view.findViewById(R.id.etTitle);
        etAuthor = view.findViewById(R.id.etAuthor);
        etGenre = view.findViewById(R.id.etGenre);
        etPublishedYear = view.findViewById(R.id.etPublishedYear);
        etISBN = view.findViewById(R.id.etIsbn);
        etPageNumber = view.findViewById(R.id.etPageNumber);
        etDescription = view.findViewById(R.id.etDescription);
        etStatus = view.findViewById(R.id.etStatus);
        btnUpdateBook = view.findViewById(R.id.btnUpdateBook);

        // Retrieve book data from bundle
        if (getArguments() != null) {
            book = new Book();
            book.setId(getArguments().getInt("id"));
            book.setTitle(getArguments().getString("title"));
            book.setAuthor(getArguments().getString("author"));
            book.setGenre(getArguments().getString("genre"));
            book.setPublishedYear(getArguments().getInt("publishedYear"));
            book.setISBN(getArguments().getString("isbn"));
            book.setPageNumber(getArguments().getInt("pageNumber"));
            book.setDescription(getArguments().getString("description"));
            book.setStatus(getArguments().getString("status"));
            book.setRegisteredDate(getArguments().getString("registeredDate"));

            // Fill the fields with existing book data
            etTitle.setText(book.getTitle());
            etAuthor.setText(book.getAuthor());
            etGenre.setText(book.getGenre());
            etPublishedYear.setText(String.valueOf(book.getPublishedYear()));
            etISBN.setText(book.getISBN());
            etPageNumber.setText(String.valueOf(book.getPageNumber()));
            etDescription.setText(book.getDescription());
            etStatus.setText(book.getStatus());
        }

        btnUpdateBook.setOnClickListener(v -> updateBook());

        return view;
    }

    private void updateBook() {
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

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(author) || TextUtils.isEmpty(genre)) {
            Toast.makeText(requireContext(), "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Update book object
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setPublishedYear(Integer.parseInt(publishedYear));
        book.setISBN(isbn);
        book.setPageNumber(pageNumber);
        book.setDescription(description);
        book.setStatus(status);

        // Save to database
        boolean success = databaseHelper.updateBook(book);
        if (success) {
            Toast.makeText(requireContext(), "Book updated successfully!", Toast.LENGTH_LONG).show();
            requireActivity().onBackPressed(); // Go back to previous screen
        } else {
            Toast.makeText(requireContext(), "Failed to update book", Toast.LENGTH_LONG).show();
        }
    }
}
