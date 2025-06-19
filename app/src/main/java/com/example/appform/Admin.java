package com.example.appform;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.Map;

public class Admin extends AppCompatActivity {

    private LinearLayout userContainer;
    private DatabaseReference databaseRef;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        userContainer = findViewById(R.id.recyclerView);
        FloatingActionButton fabDeleteAll = findViewById(R.id.fabDeleteAll);
        backButton = findViewById(R.id.backButton);

        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        loadUserData();

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(Admin.this, MainActivity.class));
            finish();
        });

        fabDeleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Hapus Semua Data?")
                    .setMessage("Apakah Anda yakin ingin menghapus SEMUA data pengguna?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        databaseRef.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                userContainer.removeAllViews();
                                Toast.makeText(this, "Semua data berhasil dihapus", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void loadUserData() {
        userContainer.removeAllViews();

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Map<String, String> user = (Map<String, String>) userSnapshot.getValue();
                    if (user == null) continue;

                    View userView = LayoutInflater.from(Admin.this).inflate(R.layout.item_user, userContainer, false);
                    TextView nameText = userView.findViewById(R.id.nameText);
                    TextView emailText = userView.findViewById(R.id.emailText);
                    Button editButton = userView.findViewById(R.id.editButton);
                    Button deleteButton = userView.findViewById(R.id.deleteButton);

                    nameText.setText(user.get("name"));
                    emailText.setText(user.get("email"));

                    editButton.setOnClickListener(v -> showEditDialog(user));
                    deleteButton.setOnClickListener(v -> {
                        new AlertDialog.Builder(Admin.this)
                                .setTitle("Hapus Pengguna")
                                .setMessage("Yakin ingin menghapus pengguna ini?")
                                .setPositiveButton("Hapus", (dialog, which) -> {
                                    databaseRef.child(user.get("id")).removeValue();
                                    loadUserData();
                                })
                                .setNegativeButton("Batal", null)
                                .show();
                    });

                    userContainer.addView(userView);
                }

                if (snapshot.getChildrenCount() == 0) {
                    TextView emptyText = new TextView(Admin.this);
                    emptyText.setText("Belum ada data pengguna");
                    emptyText.setTextSize(16f);
                    emptyText.setPadding(16, 16, 16, 16);
                    userContainer.addView(emptyText);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Admin.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(Map<String, String> user) {
        View dialogView = LayoutInflater.from(Admin.this).inflate(R.layout.dialog_edit_user, null);
        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editEmail = dialogView.findViewById(R.id.editEmail);

        editName.setText(user.get("name"));
        editEmail.setText(user.get("email"));

        new AlertDialog.Builder(Admin.this)
                .setTitle("Edit Pengguna")
                .setView(dialogView)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String newName = editName.getText().toString().trim();
                    String newEmail = editEmail.getText().toString().trim();

                    if (!newName.isEmpty() && !newEmail.isEmpty()) {
                        databaseRef.child(user.get("id")).child("name").setValue(newName);
                        databaseRef.child(user.get("id")).child("email").setValue(newEmail);
                        loadUserData();
                    } else {
                        Toast.makeText(Admin.this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
