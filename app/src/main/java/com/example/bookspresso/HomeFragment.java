package com.example.bookspresso;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private DatabaseHelper databaseHelper;
    private List<Book> allBooks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseHelper = new DatabaseHelper(getContext());

        //Load books from DB
        new LoadBooksFromDBTask().execute();

        // Search books.
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null) {
                    filterBooks(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null) {
                    filterBooks(newText);
                }
                return false;
            }
        });
        return view;
    }

    // Load books from DB
    private class LoadBooksFromDBTask extends AsyncTask<Void, Void, List<Book>> {
        @Override
        protected List<Book> doInBackground(Void... voids) {
            SharedPreferences prefs = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);

            String userId = prefs.getString("userId", "");

            return userId.isEmpty() ? new ArrayList<>()
                                    : databaseHelper.getAllBooksForUser(userId);
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            if (books != null) {
                allBooks = books; // Save all books for filtering
                bookAdapter = new BookAdapter(getContext(),books);
                recyclerView.setAdapter(bookAdapter);
            } else {
                allBooks = new ArrayList<>(); // Initialize to avoid NullPointerException
            }
        }
    }

    // Update book in RecyclerView after editing the bok.
    public void updateBookInList(Book updatedBook) {
        for (int i = 0; i < allBooks.size(); i++) {
            if (allBooks.get(i).getId() == updatedBook.getId()) {
                allBooks.set(i, updatedBook);
                bookAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Book updatedBook = (Book) data.getSerializableExtra("updatedBook");
            if (updatedBook != null) {
                updateBookInList(updatedBook);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            Book updatedBook = (Book) intent.getSerializableExtra("updatedBook");
            if (updatedBook != null) {
                updateBookInList(updatedBook);
                intent.removeExtra("updatedBook");
            }
        }
    }

    // Filter books in Search bar
    private void filterBooks(String query) {
        if (allBooks == null) {
            allBooks = new ArrayList<>();
        }

        if (TextUtils.isEmpty(query)) {
            // Show all books if query is empty
            bookAdapter = new BookAdapter(getContext(),allBooks);
        } else {
            // Filter books by title or author
            List<Book> filteredBooks = new ArrayList<>();
            for (Book book : allBooks) {
                if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                    filteredBooks.add(book);
                }
            }
            bookAdapter = new BookAdapter(getContext(), filteredBooks);
        }
        recyclerView.setAdapter(bookAdapter);
    }
}
