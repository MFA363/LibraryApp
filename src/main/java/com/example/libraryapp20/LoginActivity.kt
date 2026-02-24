package com.example.libraryapp20

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.jvm.java


class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var progressBar: ProgressBar

    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Bind Views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLoginAction)
        tvRegister = findViewById(R.id.tvRegister)
        progressBar = findViewById(R.id.progressBar)

        // Login Button Click Listener
        btnLogin.setOnClickListener { loginUser() }

        // Register Text Click Listener
        tvRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = etEmail.text?.toString()?.trim() ?: ""
        val password = etPassword.text?.toString()?.trim() ?: ""

        // 1. Validation
        if (TextUtils.isEmpty(email)) {
            etEmail.error = "Email is required"
            return
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.error = "Password is required"
            return
        }

        // Show progress and disable UI
        setLoading(true)

        // 2. Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login Success - Now check Role in Database
                    val user = mAuth.currentUser
                    if (user != null) {
                        checkUserRole(user.uid)
                    } else {
                        setLoading(false)
                        Toast.makeText(this, "Authentication succeeded but user is null.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    setLoading(false)
                    val err = task.exception?.message ?: "Unknown error"
                    Toast.makeText(this, "Authentication Failed: $err", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserRole(uid: String) {
        // Look up the user in the "users" collection in Firestore
        db.collection("users").document(uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val role = documentSnapshot.getString("role")?.lowercase() ?: "client"
                    val name = documentSnapshot.getString("name") ?: "User"

                    // Save Login Session locally
                    saveLoginSession(role, name)

                    Toast.makeText(this, "Welcome $name", Toast.LENGTH_SHORT).show()

                    // Redirect depending on role
                    when (role) {
                        "admin" -> {
                            val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                            // Optionally clear back stack so user can't press back to Login
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        else -> {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }

                    // Finish this activity (we already cleared back stack above)
                    finish()
                } else {
                    setLoading(false)
                    Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Error fetching role: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveLoginSession(role: String, name: String) {
        val sharedPref = getSharedPreferences("LibraryAppSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", true)
            putString("userRole", role)
            putString("userName", name)
            apply()
        }
    }

    /**
     * Enable/disable input fields + show/hide progress bar while authentication is in progress.
     */
    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false
            tvRegister.isEnabled = false
            etEmail.isEnabled = false
            etPassword.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            btnLogin.isEnabled = true
            tvRegister.isEnabled = true
            etEmail.isEnabled = true
            etPassword.isEnabled = true
        }
    }
}
