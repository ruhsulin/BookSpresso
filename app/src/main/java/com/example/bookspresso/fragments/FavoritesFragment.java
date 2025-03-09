package com.example.bookspresso.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookspresso.adapters.BookAdapter;
import com.example.bookspresso.R;
import com.example.bookspresso.database.DatabaseHelper;
import com.example.bookspresso.models.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class FavoritesFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private BookAdapter bookAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        recyclerView = rootView.findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadFavorites();

        return rootView;
    }

    private void loadFavorites() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", "");

        new LoadFavoritesTask(dbHelper, favoriteBooks -> {
            bookAdapter = new BookAdapter(requireContext(), favoriteBooks);
            recyclerView.setAdapter(bookAdapter);
        }).execute(userId);
    }

    private static class LoadFavoritesTask extends AsyncTask<String, Void, List<Book>> {
        private final DatabaseHelper dbHelper;
        private final OnFavoritesLoadedListener listener;

        public LoadFavoritesTask(DatabaseHelper dbHelper, OnFavoritesLoadedListener listener) {
            this.dbHelper = dbHelper;
            this.listener = listener;
        }

        @Override
        protected List<Book> doInBackground(String... userId) {
            return dbHelper.getFavoriteBooksForUser(userId[0]);
        }

        @Override
        protected void onPostExecute(List<Book> favoriteBooks) {
            if (listener != null) {
                listener.onFavoritesLoaded(favoriteBooks);
            }
        }

        public interface OnFavoritesLoadedListener {
            void onFavoritesLoaded(List<Book> books);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();

        // Hide Floating Add Book button in Favorites.
        FloatingActionButton fab = getActivity().findViewById(R.id.fabAddBook);
        if (fab != null) {
            fab.hide();
        }
    }
}