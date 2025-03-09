package com.example.bookspresso.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookspresso.R;
import com.example.bookspresso.activities.BookDetailsActivity;
import com.example.bookspresso.models.Book;

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
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(context.getString(R.string.author_format, book.getAuthor()));
        holder.tvGenre.setText(context.getString(R.string.genre_format, book.getGenre()));
        holder.tvPublishedYear.setText(context.getString(R.string.published_year_format, book.getPublishedYear()));

        if (book.getImagePath() != null && !book.getImagePath().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(book.getImagePath()))
                    .into(holder.ivBookImage);
        } else {
            holder.ivBookImage.setImageResource(R.drawable.rounded_corner_image);
        }

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

            holder.tvStatus.setText("Borrowed by: " + borrowedTo + ", on " + borrowedDate);
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
        TextView tvTitle, tvAuthor, tvGenre, tvStatus, tvPublishedYear, tvDescription;
        ImageView ivBookImage;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPublishedYear = itemView.findViewById(R.id.tvPublishedYear);
            ivBookImage = itemView.findViewById(R.id.ivBookImage);
        }
    }
}
