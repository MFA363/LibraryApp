package com.example.libraryapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // In a real app, you would fetch these strings from Database/Firebase
        TextView txtName = findViewById(R.id.txtName);
        TextView txtEmail = findViewById(R.id.txtEmail);
        TextView txtAddress = findViewById(R.id.txtAddress);
        TextView txtAge = findViewById(R.id.txtAge);
        TextView txtGender = findViewById(R.id.txtGender);


        // Mock Data for now
        txtName.setText("Name: Ali bin Abu");
        txtEmail.setText("Email: ali@student.com");
        txtAddress.setText("Address: 123 Jalan ABC");
        txtAge.setText("Age: 25");
        txtGender.setText("Gender: Male");

        // Add other fields (Address, Age, Gender) similarly...
    }
}
