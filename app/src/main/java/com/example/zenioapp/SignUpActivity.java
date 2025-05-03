package com.example.zenioapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private EditText firstName, lastName, email, password, age;
    private ImageView profileImage;
    private Uri imageUri;
    private ActivityResultLauncher<String> imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up back arrow click listener
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SplashActivity.class));
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
            finish();
        });

        // Initialize views
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        age = findViewById(R.id.age);
        profileImage = findViewById(R.id.profile_image);
        Button registerButton = findViewById(R.id.register_button);

        // Image picker setup
        imagePicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                imageUri = uri;
                profileImage.setImageURI(uri);
            }
        });

        profileImage.setOnClickListener(v -> imagePicker.launch("image/*"));

        registerButton.setOnClickListener(v -> {
            String fName = firstName.getText().toString().trim();
            String lName = lastName.getText().toString().trim();
            String emailStr = email.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String ageStr = age.getText().toString().trim();

            if (fName.isEmpty() || lName.isEmpty() || emailStr.isEmpty() || pass.isEmpty() || ageStr.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate age
            int userAge;
            try {
                userAge = Integer.parseInt(ageStr);
                if (userAge <= 0) {
                    Toast.makeText(SignUpActivity.this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(SignUpActivity.this, "Invalid age format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase sign-up
            mAuth.createUserWithEmailAndPassword(emailStr, pass)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            // Store user data in Firestore
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("firstName", fName);
                            userData.put("lastName", lName);
                            userData.put("email", emailStr);
                            userData.put("age", userAge);

                            db.collection("users").document(userId)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        // Upload profile image if selected
                                        if (imageUri != null) {
                                            StorageReference fileRef = storage.getReference()
                                                    .child("profile_images/" + userId + ".jpg");
                                            fileRef.putFile(imageUri)
                                                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                                                            .addOnSuccessListener(uri -> {
                                                                db.collection("users").document(userId)
                                                                        .update("profileImageUrl", uri.toString());
                                                                navigateToHome();
                                                            }))
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(SignUpActivity.this, "Image upload failed",
                                                                Toast.LENGTH_SHORT).show();
                                                        navigateToHome();
                                                    });
                                        } else {
                                            navigateToHome();
                                        }
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this,
                                            "Failed to save user data", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(SignUpActivity.this, "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void navigateToHome() {
        Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
        finish();
    }
}