package com.example.libraryapp20

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // Declaration of Buttons (CardViews allow for clickable tiles)
    private lateinit var btnProfile: CardView
    private lateinit var btnBookStatus: CardView
    private lateinit var btnReservation: CardView
    private lateinit var btnLostFound: CardView
    private lateinit var btnReport: CardView
    private lateinit var btnLocker: CardView
    private lateinit var btnFines: CardView
    private lateinit var btnLogin: Button
    private lateinit var tvWelcomeUser: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Initialize Views (Find ID)
        btnProfile = findViewById(R.id.btnProfile)
        btnBookStatus = findViewById(R.id.btnBookStatus)
        btnReservation = findViewById(R.id.btnReservation)
        btnLostFound = findViewById(R.id.btnLostFound)
        btnReport = findViewById(R.id.btnReport)
        btnLocker = findViewById(R.id.btnLocker)
        btnFines = findViewById(R.id.btnFines)
        btnLogin = findViewById(R.id.btnLogin)
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser)

        // 2. Set Click Listeners

        // Profile
        btnProfile.setOnClickListener {
            if (isUserLoggedIn()) {
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
            } else {
                Toast.makeText(this, "Please login to view profile, Idiot!!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
        }

        // Book Status
        btnBookStatus.setOnClickListener {
            startActivity(Intent(this@MainActivity, BookStatusActivity::class.java))
        }

        // Reservation
        btnReservation.setOnClickListener {
            if (isUserLoggedIn()) {
                startActivity(Intent(this@MainActivity, ReservationActivity::class.java))
            } else {
                Toast.makeText(this, "Please login to do Reservation, Stupid!!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
        }

        // Lost & Found
        btnLostFound.setOnClickListener {
            startActivity(Intent(this@MainActivity, LostFoundActivity::class.java))
        }

        // Locker
        btnLocker.setOnClickListener {
            if (isUserLoggedIn()) {
                startActivity(Intent(this@MainActivity, LockerActivity::class.java))
            } else {
                Toast.makeText(this, "Please login to rent a locker, Imbecile!!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
        }

        // Fines (Payment)
        btnFines.setOnClickListener {
            startActivity(Intent(this@MainActivity, PaymentActivity::class.java))
        }

        // Report Issue
        btnReport.setOnClickListener {
            startActivity(Intent(this@MainActivity, ReportActivity::class.java))
        }
    }

    // This method runs every time the screen appears
    override fun onResume() {
        super.onResume()
        updateLoginUI()
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPref: SharedPreferences = getSharedPreferences("LibraryAppSession", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("isLoggedIn", false)
    }

    private fun updateLoginUI() {
        val sharedPref: SharedPreferences = getSharedPreferences("LibraryAppSession", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val userName = sharedPref.getString("userName", "Guest") ?: "Guest"

        if (isLoggedIn) {
            // User IS Logged In
            tvWelcomeUser.text = "Welcome, $userName"
            btnLogin.text = "LOGOUT"
            btnLogin.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))

            btnLogin.setOnClickListener {
                // Logout Logic
                with(sharedPref.edit()) {
                    clear()
                    apply()
                }
                Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
                recreate() // Refresh page to reset UI
            }

        } else {
            // User is NOT Logged In
            tvWelcomeUser.text = "Welcome, Guest"
            btnLogin.text = "LOGIN"
            btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.Jade))

            btnLogin.setOnClickListener {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
        }
    }
}
