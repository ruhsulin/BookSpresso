package com.example.bookspresso;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        LinearLayout llTotalBooks = view.findViewById(R.id.ll_totalBooks);
        LinearLayout llReadBooks = view.findViewById(R.id.ll_readedBooks);
        LinearLayout llBorrowedBooks = view.findViewById(R.id.ll_borrowedBooks);

        // When clicking "Total Books"
        llTotalBooks.setOnClickListener(v -> openBookList("ALL"));

        // When clicking "Read Books"
        llReadBooks.setOnClickListener(v -> openBookList("READ"));

        // When clicking "Borrowed Books"
        llBorrowedBooks.setOnClickListener(v -> openBookList("BORROWED"));

        return view;
    }

    private void openBookList(String filterType) {
        Intent intent = new Intent(requireContext(), AllBooksActivity.class);
        intent.putExtra("FILTER_TYPE", filterType);
        startActivity(intent);
    }
}
