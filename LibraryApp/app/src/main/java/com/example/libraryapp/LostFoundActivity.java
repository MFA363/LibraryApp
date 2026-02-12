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

public class LostFoundActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LostFoundAdapter adapter;
    private List<LostItem> itemList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation); // We can reuse the same layout with RecyclerView

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        itemList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewBooks); // Reusing ID from previous layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LostFoundAdapter(itemList, item -> {
            // Handle Claim Logic
            if(mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Login required to claim items. No stealing!", Toast.LENGTH_SHORT).show();
                return;
            }
            claimItem(item);
        });

        recyclerView.setAdapter(adapter);
        loadItems();
    }

    private void loadItems() {
        db.collection("lost_items").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            itemList.add(new LostItem(
                                    doc.getId(),
                                    doc.getString("description"),
                                    doc.getString("dateFound"),
                                    doc.getString("locationFound"),
                                    doc.getString("status")
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void claimItem(LostItem item) {
        db.collection("lost_items").document(item.getId())
                .update("status", "Claim Pending", "claimedBy", mAuth.getCurrentUser().getUid())
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Claim Submitted! Visit counter.", Toast.LENGTH_LONG).show();
                    loadItems();
                });
    }
}