package com.example.libraryapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class LockerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LockerAdapter adapter;
    private List<Locker> lockerList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation); // Reuse generic recycler layout

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        lockerList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewBooks);
        // Important: Grid Layout for Lockers (3 columns)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        adapter = new LockerAdapter();
        recyclerView.setAdapter(adapter);

        loadLockers();
    }

    private void loadLockers() {
        db.collection("lockers").orderBy("number").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        lockerList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            lockerList.add(new Locker(
                                    doc.getId(),
                                    doc.getString("number"),
                                    doc.getBoolean("isRented"),
                                    doc.getString("rentedBy")
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    // --- Inner Adapter Class ---
    private class LockerAdapter extends RecyclerView.Adapter<LockerAdapter.LockerViewHolder> {

        @NonNull
        @Override
        public LockerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_locker, parent, false);
            return new LockerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LockerViewHolder holder, int position) {
            Locker locker = lockerList.get(position);
            holder.number.setText("#" + locker.getNumber());

            if (locker.getIsRented()) {
                // RED = Rented
                holder.bg.setBackgroundColor(Color.parseColor("#F44336"));
                holder.itemView.setOnClickListener(v ->
                        Toast.makeText(LockerActivity.this, "Locker occupied", Toast.LENGTH_SHORT).show()
                );
            } else {
                // GREEN = Available
                holder.bg.setBackgroundColor(Color.parseColor("#4CAF50"));
                holder.itemView.setOnClickListener(v -> rentLocker(locker));
            }
        }

        @Override
        public int getItemCount() { return lockerList.size(); }

        class LockerViewHolder extends RecyclerView.ViewHolder {
            TextView number;
            RelativeLayout bg;
            LockerViewHolder(@NonNull View itemView) {
                super(itemView);
                number = itemView.findViewById(R.id.tvLockerNumber);
                bg = itemView.findViewById(R.id.lockerBackground);
            }
        }
    }

    private void rentLocker(Locker locker) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login first, in order to use this.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("lockers").document(locker.getId())
                .update("isRented", true, "rentedBy", mAuth.getCurrentUser().getUid())
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Locker #" + locker.getNumber() + " Rented!", Toast.LENGTH_SHORT).show();
                    loadLockers();
                });
    }
}