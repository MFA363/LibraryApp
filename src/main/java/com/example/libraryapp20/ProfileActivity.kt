package com.example.libraryapp20

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtAddress: TextView
    private lateinit var txtAge: TextView
    private lateinit var txtGender: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Initialize Views
        txtName = findViewById(R.id.etProfileName)
        txtEmail = findViewById(R.id.etEmail)
        txtAddress = findViewById(R.id.etProfileAddress)
        txtAge = findViewById(R.id.etProfileAge)
        txtGender = findViewById(R.id.spinnerGender)

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userId = currentUser.uid

        database.child("Users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {

                        val name = snapshot.child("name").value?.toString() ?: "N/A"
                        val email = snapshot.child("email").value?.toString() ?: "N/A"
                        val address = snapshot.child("address").value?.toString() ?: "N/A"
                        val age = snapshot.child("age").value?.toString() ?: "N/A"
                        val gender = snapshot.child("gender").value?.toString() ?: "N/A"

                        txtName.text = "Name: $name"
                        txtEmail.text = "Email: $email"
                        txtAddress.text = "Home Address: $address"
                        txtAge.text = "Age: $age"
                        txtGender.text = "Gender: $gender"

                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Profile data not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
