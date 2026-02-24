package com.example.libraryapp20

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class LockerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LockerAdapter
    private val lockerList: MutableList<Locker> = ArrayList()
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Reuse generic recycler layout
        setContentView(R.layout.activity_reservation)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.recyclerViewBooks)
        recyclerView.layoutManager = GridLayoutManager(this, 3) // 3 columns

        adapter = LockerAdapter()
        recyclerView.adapter = adapter

        loadLockers()
    }

    private fun loadLockers() {
        db.collection("lockers")
            .orderBy("number")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lockerList.clear()
                    val result = task.result
                    if (result != null) {
                        for (doc: QueryDocumentSnapshot in result) {
                            lockerList.add(
                                Locker(
                                    doc.id,
                                    doc.getString("number"),
                                    doc.getBoolean("isRented") ?: false,
                                    doc.getString("rentedBy")
                                )
                            )
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Failed to load lockers.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // --- Inner Adapter Class ---
    private inner class LockerAdapter : RecyclerView.Adapter<LockerAdapter.LockerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LockerViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_locker, parent, false)
            return LockerViewHolder(view)
        }

        override fun onBindViewHolder(holder: LockerViewHolder, position: Int) {
            val locker = lockerList[position]
            holder.number.text = "#${locker.number ?: ""}"

            if (locker.isRented == true) {
                // RED = Rented
                holder.bg.setBackgroundColor(Color.parseColor("#F44336"))
                holder.itemView.setOnClickListener {
                    Toast.makeText(this@LockerActivity, "Locker occupied", Toast.LENGTH_SHORT).show()
                }
            } else {
                // GREEN = Available
                holder.bg.setBackgroundColor(Color.parseColor("#4CAF50"))
                holder.itemView.setOnClickListener {
                    rentLocker(locker)
                }
            }
        }

        override fun getItemCount(): Int = lockerList.size

        inner class LockerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val number: TextView = itemView.findViewById(R.id.tvLockerNumber)
            val bg: RelativeLayout = itemView.findViewById(R.id.lockerBackground)
        }
    }

    private fun rentLocker(locker: Locker) {
        if (mAuth.currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        val lockerId = locker.id
        if (lockerId.isNullOrBlank()) {
            Toast.makeText(this, "Invalid locker id.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("lockers").document(lockerId)
            .update("isRented", true, "rentedBy", mAuth.currentUser!!.uid)
            .addOnSuccessListener {
                Toast.makeText(this, "Locker #${locker.number} Rented!", Toast.LENGTH_SHORT).show()
                loadLockers()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to rent locker: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
