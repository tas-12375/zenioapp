package com.example.zenioapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MeasureActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView heartRateResult;
    private TextView remainingTime;
    private int timeLeft = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        // Initialize toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        heartRateResult = findViewById(R.id.heart_rate_result);
        remainingTime = findViewById(R.id.remaining_time);

        // Generate random heart rate (60-100 bpm)
        Random random = new Random();
        int heartRate = random.nextInt(41) + 60; // Range: 60-100
        heartRateResult.setText(heartRate + " bpm");

        // Simulate measurement countdown
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (timeLeft > 0) {
                    remainingTime.setText("MEASURING LEFT " + timeLeft + "s");
                    timeLeft--;
                    new Handler(Looper.getMainLooper()).postDelayed(this, 1000);
                } else {
                    remainingTime.setText("MEASUREMENT COMPLETE");
                    saveMeasurement(heartRate);
                }
            }
        });

        // Save measurement after countdown
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            saveMeasurement(heartRate);
        }, 10000); // 10 seconds
    }

    private void saveMeasurement(int heartRate) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> measurement = new HashMap<>();
        measurement.put("value", heartRate);
        measurement.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users").document(userId)
                .collection("heart_rate_measurements")
                .add(measurement)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Measurement saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save measurement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}