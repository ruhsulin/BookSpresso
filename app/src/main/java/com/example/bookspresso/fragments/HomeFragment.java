package com.example.bookspresso.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.bookspresso.R;
import com.example.bookspresso.activities.AllBooksActivity;
import com.example.bookspresso.database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        CardView cvTotalBooks = rootView.findViewById(R.id.cv_total_books);
        CardView cvReadBooks = rootView.findViewById(R.id.cv_read_book);
        CardView cvBorrowedBooks = rootView.findViewById(R.id.cv_borrowed_books);

        // get users' name
        TextView tvUserName = rootView.findViewById(R.id.tvUserName);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("BookSpresso", Context.MODE_PRIVATE);

        String email = sharedPreferences.getString("Email", "");
        if (!email.isEmpty()) {
            String usernamePart = email.split("@")[0];

            String username = usernamePart.substring(0, 1).toUpperCase() + usernamePart.substring(1).toLowerCase();

            tvUserName.setText("Welcome, " + username + "! 📚");
        }


        // When clicking "Total Books"
        cvTotalBooks.setOnClickListener(v -> openBookList("ALL"));

        // When clicking "Read Books"
        cvReadBooks.setOnClickListener(v -> openBookList("READ"));

        // When clicking "Borrowed Books"
        cvBorrowedBooks.setOnClickListener(v -> openBookList("BORROWED"));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatistics();

        // Show Floating ADD Button in Home.
        FloatingActionButton fab = getActivity().findViewById(R.id.fabAddBook);
        if (fab != null) {
            fab.show();
        }
    }

    private void openBookList(String filterType) {
        Intent intent = new Intent(requireContext(), AllBooksActivity.class);
        intent.putExtra("FILTER_TYPE", filterType);
        startActivity(intent);
    }

    private void updateStatistics() {
        if (rootView == null) {
            return;
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", "");

        if (userId.isEmpty()) {
            Toast.makeText(requireContext(), "User ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        // Get actual counts from DB
        int totalBooks = dbHelper.getTotalBooksCount(userId);
        int readBooks = dbHelper.getReadBooksCount(userId);
        int borrowedBooks = dbHelper.getBorrowedBooksCount(userId);

        // Update UI
        TextView tvTotalBooks = requireView().findViewById(R.id.tv_totalBooks_number);
        TextView tvReadBooks = requireView().findViewById(R.id.tv_readbooks_number);
        TextView tvBorrowedBooks = requireView().findViewById(R.id.tv_borrowedbooks_number);

        tvTotalBooks.setText(String.valueOf(totalBooks));
        tvReadBooks.setText(String.valueOf(readBooks));
        tvBorrowedBooks.setText(String.valueOf(borrowedBooks));
    }
}
