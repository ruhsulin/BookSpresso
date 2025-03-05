package com.example.bookspresso.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

        List<Book> favoriteBooks = dbHelper.getFavoriteBooksForUser(userId);
        BookAdapter bookAdapter = new BookAdapter(requireContext(), favoriteBooks);
        recyclerView.setAdapter(bookAdapter);
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