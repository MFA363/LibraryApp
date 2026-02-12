package com.example.libraryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;

public class ReportActivity extends AppCompatActivity {

    private EditText etIssue;
    private Button btnSubmit;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etIssue = findViewById(R.id.etReportIssue);
        btnSubmit = findViewById(R.id.btnSubmitReport);

        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private void submitReport() {
        String issue = etIssue.getText().toString().trim();

        if (TextUtils.isEmpty(issue)) {
            etIssue.setError("Please describe the issue");
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "You must be logged in to report.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get User Name from Session
        SharedPreferences sharedPref = getSharedPreferences("LibraryAppSession", Context.MODE_PRIVATE);
        String userName = sharedPref.getString("userName", "Anonymous");

        Report report = new Report(
                mAuth.getCurrentUser().getUid(),
                userName,
                issue,
                new Date()
        );

        db.collection("reports").add(report)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Report Sent! We will check it soon.", Toast.LENGTH_LONG).show();
                    finish(); // Go back to main menu
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to send report.", Toast.LENGTH_SHORT).show()
                );
    }
}