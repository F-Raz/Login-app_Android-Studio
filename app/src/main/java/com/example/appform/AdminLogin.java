package com.example.appform;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdminLogin extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private ProgressBar progressBar;

    private final String ADMIN_EMAIL = "admin";
    private final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        progressBar = findViewById(R.id.progressBar);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> loginAdmin());
    }

    private void loginAdmin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
            Toast.makeText(this, "Login admin berhasil", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Admin.class));
            finish();
        } else {
            Toast.makeText(this, "Email atau password admin salah", Toast.LENGTH_SHORT).show();
        }

        progressBar.setVisibility(View.GONE);
    }
}