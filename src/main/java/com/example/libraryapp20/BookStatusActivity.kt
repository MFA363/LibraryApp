package com.example.libraryapp20

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class BookStatusActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookAdapter
    private val bookList: MutableList<Book> = ArrayList()
    private val filteredList: MutableList<Book> = ArrayList() // For search functionality
    private lateinit var etSearch: EditText
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_status)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()

        // Setup Views
        etSearch = findViewById(R.id.etSearchBook)
        recyclerView = findViewById(R.id.rvBookStatus)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup Adapter
        // a no-op listener so the adapter can be reused without errors.
        adapter = BookAdapter(filteredList, object : BookAdapter.OnBookListener {
            override fun onReserveClick(book: Book) {
                // No action here because this screen is view-only.
            }
        })
        recyclerView.adapter = adapter

        // Load Data
        fetchAllBooks()

        // Setup Search Listener
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBooks(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) { }
        })
    }

    private fun fetchAllBooks() {
        db.collection("books").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    bookList.clear()
                    filteredList.clear()

                    val result = task.result
                    if (result != null) {
                        for (document: QueryDocumentSnapshot in result) {
                            val book = Book(
                                document.id,
                                document.getString("title"),
                                document.getString("author"),
                                document.getString("status")
                            )
                            bookList.add(book)
                            filteredList.add(book) // Initially, filtered list has everything
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Failed to load books.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun filterBooks(textInput: String) {
        filteredList.clear()
        val text = textInput.trim().lowercase()
        if (text.isEmpty()) {
            filteredList.addAll(bookList)
        } else {
            for (book in bookList) {
                val title = book.title?.lowercase() ?: ""
                val author = book.author?.lowercase() ?: ""
                if (title.contains(text) || author.contains(text)) {
                    filteredList.add(book)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }
}
