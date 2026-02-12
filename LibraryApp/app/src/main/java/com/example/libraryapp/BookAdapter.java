package com.example.libraryapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private OnBookListener onBookListener;

    public BookAdapter(List<Book> bookList, OnBookListener onBookListener) {
        this.bookList = bookList;
        this.onBookListener = onBookListener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view, onBookListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.status.setText(book.getStatus());

        // Dynamic Styling based on Status
        if ("Available".equalsIgnoreCase(book.getStatus())) {
            holder.status.setTextColor(Color.parseColor("#4CAF50")); // Green
            holder.btnReserve.setEnabled(true);
            holder.btnReserve.setText("RESERVE");
            holder.btnReserve.setBackgroundColor(Color.parseColor("#6200EE"));
        } else {
            holder.status.setTextColor(Color.RED);
            holder.btnReserve.setEnabled(false); // Disable button
            holder.btnReserve.setText("TAKEN");
            holder.btnReserve.setBackgroundColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, status;
        Button btnReserve;

        public BookViewHolder(@NonNull View itemView, OnBookListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.tvBookTitle);
            author = itemView.findViewById(R.id.tvBookAuthor);
            status = itemView.findViewById(R.id.tvBookStatus);
            btnReserve = itemView.findViewById(R.id.btnReserve);

            btnReserve.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onReserveClick(bookList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnBookListener {
        void onReserveClick(Book book);
    }
}
