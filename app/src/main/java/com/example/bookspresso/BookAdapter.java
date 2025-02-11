package com.example.bookspresso;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> bookList;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    /**
     * Binds book data to the RecyclerView item and sets click listener.
     * When a book is clicked, it opens EditBookFragment for editing.
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText("Author: " + book.getAuthor());
        holder.tvGenre.setText("Genre: " + book.getGenre());
        holder.tvStatus.setText("Status: " + book.getStatus());

        // Handles book click to open the clicked book
        holder.itemView.setOnClickListener(v -> {
            FragmentTransaction transaction = ((AppCompatActivity) v.getContext())
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, EditBookFragment.newInstance(book));
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvGenre, tvStatus;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}