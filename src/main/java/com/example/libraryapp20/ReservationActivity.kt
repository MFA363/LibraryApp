package com.example.libraryapp20

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot

class ReservationActivity : AppCompatActivity(), BookAdapter.OnBookListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookAdapter
    private val bookList: MutableList<Book> = ArrayList()
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private var booksListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.recyclerViewBooks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BookAdapter(bookList, this)
        recyclerView.adapter = adapter

        loadBooksRealTime()
    }

    private fun loadBooksRealTime() {
        booksListener = db.collection("books")
            .addSnapshotListener { snapshots, error ->

                if (error != null) {
                    Toast.makeText(this, "Error loading books.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    bookList.clear()

                    for (document: QueryDocumentSnapshot in snapshots) {
                        val book = Book(
                            document.id,
                            document.getString("title"),
                            document.getString("author"),
                            document.getString("status")
                        )
                        bookList.add(book)
                    }

                    adapter.notifyDataSetChanged()
                }
            }
    }

    override fun onReserveClick(book: Book) {
        val userId = mAuth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "You must be logged in to reserve, Loser!", Toast.LENGTH_SHORT).show()
            return
        }

        val bookId = book.id
        if (bookId.isNullOrBlank()) {
            Toast.makeText(this, "Invalid book id.", Toast.LENGTH_SHORT).show()
            return
        }

        // 🔐 Extra Safety: Check availability before reserving (Transaction recommended)
        db.runTransaction { transaction ->
            val docRef = db.collection("books").document(bookId)
            val snapshot = transaction.get(docRef)

            val currentStatus = snapshot.getString("status")

            if (currentStatus == "Available") {
                transaction.update(docRef, mapOf(
                    "status" to "Reserved",
                    "reservedBy" to userId
                ))
            } else {
                throw Exception("Book already reserved, Dummy.")
            }
        }.addOnSuccessListener {
            Toast.makeText(this, "Book Reserved Successfully!", Toast.LENGTH_LONG).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Reservation Failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // ✅ Prevent memory leaks
        booksListener?.remove()
    }
}
