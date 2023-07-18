package com.example.merobook.adpters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.merobook.R;
import com.example.merobook.models.Book;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private List<Book> bookList;
    private Context context;

    public BookAdapter(List<Book> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bookNameTextView.setText(book.getBookName());
        holder.authorNameTextView.setText(book.getAuthorName());
        Glide.with(context).load(book.getImageUrl()).into(holder.bookCoverImageView);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView bookCoverImageView;
        private TextView bookNameTextView;
        private TextView authorNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookCoverImageView = itemView.findViewById(R.id.book_cover_image_view);
            bookNameTextView = itemView.findViewById(R.id.book_name_text_view);
            authorNameTextView = itemView.findViewById(R.id.author_name_text_view);
        }
    }
}
