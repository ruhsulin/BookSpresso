package com.example.bookspresso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class BookDetailsActivity extends AppCompatActivity {
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        // declaring and mapping variables with UI
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvAuthor = findViewById(R.id.tvAuthor);
        TextView tvGenre = findViewById(R.id.tvGenre);
        TextView tvPublishedYear = findViewById(R.id.tvPublishedYear);
        TextView tvISBN = findViewById(R.id.tvISBN);
        TextView tvPageNumber = findViewById(R.id.tvPageNumber);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvStatus = findViewById(R.id.tvStatus);
        Button btnEdit = findViewById(R.id.btnEdit);

        //Get book data from Intent
        book = (Book) getIntent().getSerializableExtra("book");

        if (book != null) {
            tvTitle.setText(getString(R.string.title_format, book.getTitle()));
            tvAuthor.setText(getString(R.string.author_format, book.getAuthor()));
            tvGenre.setText(getString(R.string.genre_format, book.getGenre()));
            tvPublishedYear.setText(getString(R.string.published_year_format, book.getPublishedYear()));
            tvISBN.setText(getString(R.string.isbn_format, book.getISBN()));
            tvPageNumber.setText(getString(R.string.pages_format, book.getPageNumber()));
            tvDescription.setText(getString(R.string.description_format, book.getDescription()));
        }

        //click on Edit button
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailsActivity.this, MainActivity.class);
            intent.putExtra("editBook", book);
            startActivity(intent);
        });
    }
}