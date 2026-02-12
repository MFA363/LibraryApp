package com.example.libraryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    // Firebase Declarations
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLoginAction);
        tvRegister = findViewById(R.id.tvRegister);

        // Login Button Click Listener
        btnLogin.setOnClickListener(v -> loginUser());

        // Register Text Click Listener
        tvRegister.setOnClickListener(v -> {
            // Redirect to Register Activity
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 1. Validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        // 2. Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login Success - Now check Role in Database
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserRole(user.getUid());
                    } else {
                        // Login Failed
                        Toast.makeText(LoginActivity.this, "Authentication Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole(String uid) {
        // Look up the user in the "users" collection in Firestore
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role"); // "client" or "admin"
                        String name = documentSnapshot.getString("name");

                        // 3. Save Login Session locally
                        saveLoginSession(role, name);

                        Toast.makeText(LoginActivity.this, "Welcome " + name, Toast.LENGTH_SHORT).show();

                        // 4. Return to Main Page
                        // We finish this activity so the user goes back to where they came from
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(LoginActivity.this, "Error fetching role: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void saveLoginSession(String role, String name) {
        SharedPreferences sharedPref = getSharedPreferences("LibraryAppSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userRole", role); // "admin" or "client"
        editor.putString("userName", name);
        editor.apply();
    }
}
