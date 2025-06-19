package com.example.appform;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Register extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput, bioInput;
    private RadioGroup genderGroup;
    private Spinner citySpinner;
    private CheckBox agreementCheck;
    private ProgressBar progressBar;
    private ImageView profileImage;
    private Uri selectedImageUri = null;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Panggil setContentView lebih dulu

        // Inisialisasi UI
        profileImage = findViewById(R.id.profileImage);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        bioInput = findViewById(R.id.bioInput);
        genderGroup = findViewById(R.id.genderGroup);
        citySpinner = findViewById(R.id.citySpinner);
        agreementCheck = findViewById(R.id.agreementCheck);
        progressBar = findViewById(R.id.progressBar);
        Button registerButton = findViewById(R.id.registerButton);
        TextView loginLink = findViewById(R.id.loginLink);

        // Isi Spinner
        String[] cities = {"Jakarta", "Bandung", "Surabaya", "Yogyakarta", "Medan", "Makassar"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

        // Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        // Aksi klik gambar profil
        profileImage.setOnClickListener(v -> pickImage());

        // Aksi tombol daftar
        registerButton.setOnClickListener(v -> registerUser());

        // Aksi klik login
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, Login.class));
            finish();
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), UUID.randomUUID().toString() + ".jpg"));
                        UCrop.of(imageUri, destinationUri)
                                .withAspectRatio(1, 1)
                                .start(Register.this);
                    }
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    profileImage.setImageBitmap(bitmap);
                    selectedImageUri = resultUri;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable error = UCrop.getError(data);
            if (error != null) error.printStackTrace();
        }
    }

    private void registerUser() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String bio = bioInput.getText().toString().trim();
        String city = citySpinner.getSelectedItem().toString();

        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedGenderId);
        String gender = selectedGender != null ? selectedGender.getText().toString() : "";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(gender) || TextUtils.isEmpty(city) || TextUtils.isEmpty(bio)) {
            Toast.makeText(this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!agreementCheck.isChecked()) {
            Toast.makeText(this, "Anda harus menyetujui privasi", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        String userId = databaseRef.push().getKey();

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("id", userId);
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("password", password);
        userMap.put("gender", gender);
        userMap.put("city", city);
        userMap.put("bio", bio);
        userMap.put("photoUri", selectedImageUri != null ? selectedImageUri.toString() : "");

        if (userId != null) {
            databaseRef.child(userId).setValue(userMap)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, Home.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
