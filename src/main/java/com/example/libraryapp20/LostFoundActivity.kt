package com.example.libraryapp20

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class LostFoundActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LostFoundAdapter
    private val itemList: MutableList<LostItem> = ArrayList()
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost_found)

        // 1. Initialize Firebase
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        itemList.clear()

        // 2. Setup RecyclerView
        recyclerView = findViewById(R.id.rvLostFound)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Initialize Adapter with Click Listener for "Claim" button
        adapter = LostFoundAdapter(itemList, object : LostFoundAdapter.OnItemClickListener {
            override fun onClaimClick(item: LostItem) {
                claimItem(item)
            }
        })
        recyclerView.adapter = adapter

        // 4. Fetch Data
        loadLostItems()
    }

    private fun loadLostItems() {
        // Fetch all items (optional: add filters as required)
        db.collection("lost_items")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    itemList.clear()
                    val result = task.result
                    if (result != null) {
                        for (doc: QueryDocumentSnapshot in result) {
                            val item = LostItem(
                                doc.id,
                                doc.getString("description"),
                                doc.getString("dateFound"),
                                doc.getString("locationFound"),
                                doc.getString("status")
                            )
                            itemList.add(item)
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Failed to load items.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Logic when user clicks "CLAIM"
    private fun claimItem(item: LostItem) {
        // 1. Check if User is Logged In
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login to claim items. No Stealing, Extra!", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Check if already pending/claimed
        val status = item.status ?: ""
        if (!status.equals("Unclaimed", ignoreCase = true)) {
            Toast.makeText(this, "Item is already being processed. Please head toward the Customer Service Counter.", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Update Database
        val itemId = item.id
        if (itemId.isNullOrBlank()) {
            Toast.makeText(this, "Invalid item id.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid

        db.collection("lost_items").document(itemId)
            .update("status", "Claim Pending", "claimedBy", userId)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Claim Sent! Please visit the Library counter.",
                    Toast.LENGTH_LONG
                ).show()
                loadLostItems() // Refresh list to show updated status
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error claiming item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
