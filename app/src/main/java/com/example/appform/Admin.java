package com.example.appform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.Map;

public class Admin extends AppCompatActivity {

    private LinearLayout userContainer;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin); // gunakan layout XML yang sudah kita sesuaikan

        userContainer = findViewById(R.id.recyclerView);
        FloatingActionButton fabDeleteAll = findViewById(R.id.fabDeleteAll);

        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        loadUserData();

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
        userContainer.removeAllViews(); // hapus tampilan lama
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Map<String, String> user = (Map<String, String>) userSnapshot.getValue();

                    if (user == null) continue;

                    View userView = LayoutInflater.from(Admin.this).inflate(R.layout.item_user, userContainer, false);

                    TextView nameText = userView.findViewById(R.id.nameText);
                    TextView emailText = userView.findViewById(R.id.emailText);

                    nameText.setText(user.get("name"));
                    emailText.setText(user.get("email"));

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
}
