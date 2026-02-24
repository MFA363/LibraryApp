package com.example.libraryapp20

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(
    private val bookList: List<Book>,
    private val onBookListener: OnBookListener
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]

        holder.title.text = book.title ?: ""
        holder.author.text = book.author ?: ""
        holder.status.text = book.status ?: ""

        val statusText = book.status?.trim()?.lowercase() ?: ""

        if (statusText == "available") {
            holder.status.setTextColor(Color.parseColor("#4CAF50")) // Green
            holder.btnReserve.isEnabled = true
            holder.btnReserve.text = "RESERVE"
            holder.btnReserve.setBackgroundColor(Color.parseColor("#6200EE"))
        } else {
            holder.status.setTextColor(Color.RED)
            holder.btnReserve.isEnabled = false
            holder.btnReserve.text = "TAKEN"
            holder.btnReserve.setBackgroundColor(Color.RED)
        }
    }

    override fun getItemCount(): Int = bookList.size

    inner class BookViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.tvBookTitle)
        val author: TextView = itemView.findViewById(R.id.tvBookAuthor)
        val status: TextView = itemView.findViewById(R.id.tvBookStatus)
        val btnReserve: Button = itemView.findViewById(R.id.btnReserve)

        init {
            btnReserve.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val book = bookList[position]

                    if (book.status?.lowercase() == "available") {

                        // 🔔 Confirmation Dialog
                        AlertDialog.Builder(itemView.context)
                            .setTitle("Confirm Reservation")
                            .setMessage("Are you sure you want to reserve \"${book.title}\"?")
                            .setPositiveButton("Yes") { _, _ ->
                                onBookListener.onReserveClick(book)
                            }
                            .setNegativeButton("Cancel", null)
                            .show()

                    } else {
                        Toast.makeText(
                            itemView.context,
                            "This book is not available.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    interface OnBookListener {
        fun onReserveClick(book: Book)
    }
}
