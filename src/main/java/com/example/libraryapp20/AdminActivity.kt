package com.example.libraryapp20

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlin.jvm.java

class AdminActivity : AppCompatActivity() {
    private lateinit var txtWelcome: TextView
    private lateinit var btnManageUsers: Button
    private lateinit var btnManageBooks: Button
    private lateinit var btnViewReports: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        txtWelcome = findViewById(R.id.txtWelcomeAdmin)
        btnManageUsers = findViewById(R.id.btnManageUsers)
        btnManageBooks = findViewById(R.id.btnManageBooks)
        btnViewReports = findViewById(R.id.btnViewReports)
        btnLogout = findViewById(R.id.btnAdminLogout)

        checkAdminAccess()

        btnManageUsers.setOnClickListener {
            Toast.makeText(this, "Manage Users Clicked", Toast.LENGTH_SHORT).show()
            // You can redirect to ManageUsersActivity later
        }

        btnManageBooks.setOnClickListener {
            Toast.makeText(this, "Manage Books Clicked", Toast.LENGTH_SHORT).show()
            // You can redirect to ManageBooksActivity later
        }

        btnViewReports.setOnClickListener {
            Toast.makeText(this, "View Reports Clicked", Toast.LENGTH_SHORT).show()
            // You can redirect to ReportsActivity later
        }

        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun checkAdminAccess() {
        val sharedPref: SharedPreferences =
            getSharedPreferences("LibraryAppSession", Context.MODE_PRIVATE)

        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val role = sharedPref.getString("userRole", "client")
        val name = sharedPref.getString("userName", "Admin")

        if (!isLoggedIn || role != "admin") {
            Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show()

            val intent = Intent(this,MainActivity::class.java)
            (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK).also { it.also { intent.flags = it } }
            startActivity(intent)
            finish()
        } else {
            txtWelcome.text = "Welcome, $name (Administrator). GET TO WORK!! "
        }
    }

    private fun logout() {

        // Clear SharedPreferences
        val sharedPref: SharedPreferences =
            getSharedPreferences("LibraryAppSession", Context.MODE_PRIVATE)

        with(sharedPref.edit()) {
            clear()
            apply()
        }

        // Firebase Sign Out
        FirebaseAuth.getInstance().signOut()

        Toast.makeText(this, "Admin Logged Out", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
