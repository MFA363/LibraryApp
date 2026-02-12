package com.example.libraryapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ReservationActivity extends AppCompatActivity implements BookAdapter.OnBookListener {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        bookList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookAdapter(bookList, this);
        recyclerView.setAdapter(adapter);

        loadBooks();
    }

    private void loadBooks() {
        // Fetch all books from 'books' collection
        db.collection("books").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Map Firestore data to Book object
                            Book book = new Book(
                                    document.getId(),
                                    document.getString("title"),
                                    document.getString("author"),
                                    document.getString("status")
                            );
                            bookList.add(book);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading books.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onReserveClick(Book book) {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "You must be logged in to reserve. No stealing!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update Firestore: Change status to 'Reserved' and assign to User
        db.collection("books").document(book.getId())
                .update("status", "Reserved", "reservedBy", userId)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Book Reserved Successfully!", Toast.LENGTH_LONG).show();
                    loadBooks(); // Refresh the list to show the new status
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Reservation Failed.", Toast.LENGTH_SHORT).show()
                );
    }
}
