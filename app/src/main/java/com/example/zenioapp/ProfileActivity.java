package com.example.zenioapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView profileImage;
    private TextView firstNameText, lastNameText, emailText, ageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize toolbar (no setSupportActionBar to avoid action bar conflict)
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        // Initialize views
        profileImage = findViewById(R.id.profile_image);
        firstNameText = findViewById(R.id.first_name);
        lastNameText = findViewById(R.id.last_name);
        emailText = findViewById(R.id.email);
        ageText = findViewById(R.id.age);

        // Load user data
        loadUserData();

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            } else if (itemId == R.id.nav_analytics) {
                startActivity(new Intent(ProfileActivity.this, AnalyticsActivity.class));
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
    }

    private void loadUserData() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            db.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                firstNameText.setText(document.getString("firstName"));
                                lastNameText.setText(document.getString("lastName"));
                                emailText.setText(document.getString("email"));
                                Long age = document.getLong("age");
                                if (age != null) {
                                    ageText.setText(String.valueOf(age));
                                }
                                String profileImageUrl = document.getString("profileImageUrl");
                                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                    Picasso.get().load(profileImageUrl).into(profileImage);
                                }
                            } else {
                                Toast.makeText(ProfileActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to load user data: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(ProfileActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, SignInActivity.class));
            finish();
        }
    }
}