package com.example.bookspresso;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private final List<Book> bookList;
    private final Context context;

    public BookAdapter(Context context, List<Book> bookList) {
        this.context = context;
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
     * When a book is clicked opens BookDetails activity.
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(context.getString(R.string.title_format, book.getAuthor()));
        holder.tvGenre.setText(context.getString(R.string.genre_format, book.getGenre()));

        // Display Borrowed Info
        if (book.getStatus() == Book.BookStatus.BORROWED) {
            String borrowedTo = book.getBorrowedTo();
            String borrowedDate = book.getBorrowedDate();

            // if null than writing unknown to handle null error.
            if (borrowedTo == null || borrowedTo.isEmpty()) {
                borrowedTo = "Unknown Person";
            }
            if (borrowedDate == null || borrowedDate.isEmpty()) {
                borrowedDate = "Unknown Date";
            }

            holder.tvStatus.setText("Borrowed by: " + borrowedTo + " on " + borrowedDate);
        } else {
            holder.tvStatus.setText("Status: " + book.getStatus().name());
        }

        // Open BookDetailsActivity on click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BookDetailsActivity.class);
            intent.putExtra("book", book);
            ((AppCompatActivity) context).startActivityForResult(intent, 2);
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
