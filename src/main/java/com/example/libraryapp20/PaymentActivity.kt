package com.example.libraryapp20

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val btnOnline: Button = findViewById(R.id.btnPayOnline)
        btnOnline.setOnClickListener {
            // Membuka URL Maybank2u dalam pelayar melalui intent
            val url = "https://www.maybank2u.com.my/home/m2u/common/login.do"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
}
