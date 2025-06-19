package com.example.appform;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView welcomeText = findViewById(R.id.welcomeText);
        Button logoutButton = findViewById(R.id.logoutButton);

        welcomeText.setText("Selamat datang di Beranda!");

        logoutButton.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Login.class));
            finish();
        });
    }
}