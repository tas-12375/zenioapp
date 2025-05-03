package com.example.zenioapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;

public class AddMeasurementActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText valueInput;
    private String measurementType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_measurement);

        // Initialize toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get measurement type from intent
        measurementType = getIntent().getStringExtra("measurement_type");
        TextView title = findViewById(R.id.measurement_title);
        valueInput = findViewById(R.id.value_input);
        Button saveButton = findViewById(R.id.save_button);

        // Set title based on measurement type
        if ("blood_pressure".equals(measurementType)) {
            toolbar.setTitle("Add Blood Pressure Measurement");
            valueInput.setHint("Enter systolic value (mmHg)");
        } else if ("blood_sugar".equals(measurementType)) {
            toolbar.setTitle("Add Blood Sugar Measurement");
            valueInput.setHint("Enter value (mg/dL)");
        }

        // Save measurement
        saveButton.setOnClickListener(v -> saveMeasurement());
    }

    private void saveMeasurement() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String input = valueInput.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            return;
        }

        float value;
        try {
            value = Float.parseFloat(input);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid value entered", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String collection = "blood_pressure".equals(measurementType) ?
                "blood_pressure_measurements" : "blood_sugar_measurements";

        Map<String, Object> measurement = new HashMap<>();
        measurement.put("value", value);
        measurement.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users").document(userId)
                .collection(collection)
                .add(measurement)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Measurement saved", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save measurement: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}