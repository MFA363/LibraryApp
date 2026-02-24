package com.example.libraryapp20

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etAge: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var tvLoginLink: TextView

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Bind Views (including new ones)
        etName = findViewById(R.id.etRegName)
        etPhone = findViewById(R.id.etRegPhone)
        etAge = findViewById(R.id.etRegAge)
        etAddress = findViewById(R.id.etRegAddress)
        etEmail = findViewById(R.id.etRegEmail)
        etPassword = findViewById(R.id.etRegPassword)
        btnRegister = findViewById(R.id.btnRegisterAction)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        // Button Actions
        btnRegister.setOnClickListener { registerUser() }

        tvLoginLink.setOnClickListener {
            finish() // Go back to Login Activity
        }
    }

    private fun registerUser() {
        val name = etName.text?.toString()?.trim() ?: ""
        val phone = etPhone.text?.toString()?.trim() ?: ""
        val age = etAge.text?.toString()?.trim() ?: ""
        val address = etAddress.text?.toString()?.trim() ?: ""
        val email = etEmail.text?.toString()?.trim() ?: ""
        val password = etPassword.text?.toString()?.trim() ?: ""

        // 1. Validation
        if (TextUtils.isEmpty(name)) {
            etName.error = "Name is required"
            return
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.error = "Phone is required"
            return
        }
        if (TextUtils.isEmpty(age)) {
            etAge.error = "Age is required"
            return
        }
        if (TextUtils.isEmpty(address)) {
            etAddress.error = "Address is required"
            return
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.error = "Email is required"
            return
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.error = "Password is required"
            return
        }
        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return
        }

        // 2. Create User in Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Auth successful, now save profile to Firestore
                    val user = mAuth.currentUser
                    if (user != null) {
                        saveUserToFirestore(user.uid, name, phone, age, address, email)
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration succeeded but user is null.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration Failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveUserToFirestore(
        uid: String,
        name: String,
        phone: String,
        age: String,
        address: String,
        email: String
    ) {
        // Create a Map of user data
        val userMap: MutableMap<String, Any> = HashMap()
        userMap["uid"] = uid
        userMap["name"] = name
        userMap["phone"] = phone
        userMap["age"] = age
        userMap["homeAddress"] = address
        userMap["email"] = email
        userMap["role"] = "client" // Default role is Client
        userMap["fineAmount"] = 0.00 // Initialize fines
        userMap["gender"] = "Not Specified" // Optional, kept empty for now

        // Save to "users" collection
        db.collection("users").document(uid)
            .set(userMap)
            .addOnSuccessListener {
                // Success! Save session locally and go to Main Page
                saveSession(name, "client")

                Toast.makeText(this@RegisterActivity, "Account Created!", Toast.LENGTH_SHORT).show()

                // Navigate to Main Page (clearing previous activities)
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this@RegisterActivity,
                    "Error saving profile: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun saveSession(name: String, role: String) {
        val sharedPref = getSharedPreferences("LibraryAppSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", true)
            putString("userName", name)
            putString("userRole", role)
            apply()
        }
    }
}

