package com.example.libraryapp

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.jvm.java


class MainActivity : AppCompatActivity() {
    private val isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.setupButton(R.id.btnProfile, ProfileActivity::class.java)
        this.setupButton(R.id.btnBookStatus, BookStatusActivity::class.java)
        this.setupButton(R.id.btnReservation, ReservationActivity::class.java)
        this.setupButton(R.id.btnLostFound, LostFoundActivity::class.java)
        this.setupButton(R.id.btnReport, ReportActivity::class.java)
        this.setupButton(R.id.btnLockers, LockerActivity::class.java)
        this.setupButton(R.id.btnFines, PaymentActivity::class.java)

        val btnLogin = findViewById<Button?>(R.id.btnLogin)
        btnLogin.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        })
    }

    private fun setupButton(btnId: Int, destinationActivity: Class<*>?) {
        val btn = findViewById<Button?>(btnId)
        btn.setOnClickListener(View.OnClickListener { v: View? ->
            if (isLoggedIn) {

                startActivity(Intent(this@MainActivity, destinationActivity))
            } else {

                Toast.makeText(this, "Please Login to use this feature.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
        })
    }
    override fun onResume() {
        super.onResume()
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val sharedPref = getSharedPreferences("LibraryAppSession", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val userRole: String = sharedPref.getString("userRole", "client")!!

        val btnLogin = findViewById<Button?>(R.id.btnLogin)

        if (isLoggedIn) {
            btnLogin.setText("Log Out")
            btnLogin.setBackgroundColor(getResources().getColor(R.color.holo_red_dark))
            btnLogin.setOnClickListener(View.OnClickListener { v: View? -> logout() })


            // Example: If Admin, show a hidden Admin Button
            if (userRole == "admin") {

            }
        } else {
            btnLogin.setText("Login / Admin Access")
            btnLogin.setBackgroundColor(getResources().getColor(R.color.holo_green_light))
            btnLogin.setOnClickListener(View.OnClickListener { v: View? ->
                startActivity(
                    Intent(
                        this@MainActivity,
                        LoginActivity::class.java
                    )
                )
            })
        }
    }

    private fun logout() {
        val sharedPref = getSharedPreferences(
            "LibraryAppSession",
            Context.MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        editor.clear() // Delete session data
        editor.apply()

        Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
        recreate() // Refresh the page
    }
}