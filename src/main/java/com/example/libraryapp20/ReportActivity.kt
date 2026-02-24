package com.example.libraryapp20

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class ReportActivity : AppCompatActivity() {

    private lateinit var etIssue: EditText
    private lateinit var btnSubmit: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        etIssue = findViewById(R.id.etReportIssue)
        btnSubmit = findViewById(R.id.btnSubmitReport)

        btnSubmit.setOnClickListener { submitReport() }
    }

    private fun submitReport() {
        val issue = etIssue.text?.toString()?.trim() ?: ""

        if (issue.isEmpty()) {
            etIssue.error = "Please describe the issue and Put in your email address."
            return
        }

        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to report.", Toast.LENGTH_SHORT).show()
            return
        }

        // Get User Name from Session
        val sharedPref = getSharedPreferences("LibraryAppSession", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("userName", "Anonymous") ?: "Anonymous"

        val report = Report(
            userId = currentUser.uid,
            userName = userName,
            issue = issue,
            timestamp = Date()
        )

        db.collection("reports")
            .add(report)
            .addOnSuccessListener {
                Toast.makeText(this, "Report Sent! We will check it soon. or Maybe not, who know.", Toast.LENGTH_LONG).show()
                finish() // Go back to main menu
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to send report: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
