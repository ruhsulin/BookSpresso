package com.example.bookspresso.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookspresso.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GoalFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goal, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Hide Floating Add Book button in Goals.
        FloatingActionButton fab = getActivity().findViewById(R.id.fabAddBook);
        if (fab != null) {
            fab.hide();
        }
    }

}
