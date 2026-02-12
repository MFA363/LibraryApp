package com.example.libraryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktivity_payment);

        Button btnOnline = findViewById(R.id.btnPayOnline);

        btnOnline.setOnClickListener(v -> {
            // This creates a Web Intent to open the browser
            String url = "https://www.maybank2u.com.my/home/m2u/common/login.do";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
    }
}
