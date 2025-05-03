package com.example.zenioapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class AnalyticsActivity extends AppCompatActivity {

    private BarChart bloodPressureChart, bloodSugarChart;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        // Initialize charts
        bloodPressureChart = findViewById(R.id.blood_pressure_chart);
        bloodSugarChart = findViewById(R.id.blood_sugar_chart);

        // Load data and set up charts
        loadBloodPressureData();
        loadBloodSugarData();

        // Set up add measurement buttons
        findViewById(R.id.add_blood_pressure_button).setOnClickListener(v -> {
            Intent intent = new Intent(AnalyticsActivity.this, AddMeasurementActivity.class);
            intent.putExtra("measurement_type", "blood_pressure");
            startActivity(intent);
        });

        findViewById(R.id.add_blood_sugar_button).setOnClickListener(v -> {
            Intent intent = new Intent(AnalyticsActivity.this, AddMeasurementActivity.class);
            intent.putExtra("measurement_type", "blood_sugar");
            startActivity(intent);
        });

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_analytics);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(AnalyticsActivity.this, HomeActivity.class));
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(AnalyticsActivity.this, SettingsActivity.class));
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(AnalyticsActivity.this, ProfileActivity.class));
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_analytics) {
                return true;
            }
            return false;
        });
    }

    private void loadBloodPressureData() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .collection("blood_pressure_measurements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<BarEntry> entries = new ArrayList<>();
                    ArrayList<String> labels = new ArrayList<>();
                    int[] colors = new int[queryDocumentSnapshots.size()];
                    int index = 0;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Float value = document.getDouble("value").floatValue();
                        entries.add(new BarEntry(index, value));

                        // Determine label and color based on value
                        String label;
                        if (value < 90) {
                            label = "Low";
                            colors[index] = Color.YELLOW;
                        } else if (value < 120) {
                            label = "Normal";
                            colors[index] = Color.GREEN;
                        } else if (value < 130) {
                            label = "Elevated";
                            colors[index] = Color.YELLOW;
                        } else if (value < 140) {
                            label = "Stage 1";
                            colors[index] = Color.YELLOW;
                        } else if (value <= 180) {
                            label = "Stage 2";
                            colors[index] = Color.RED;
                        } else {
                            label = "Crisis";
                            colors[index] = Color.RED;
                        }
                        labels.add(label);
                        index++;
                    }

                    BarDataSet dataSet = new BarDataSet(entries, "Blood Pressure (Systolic)");
                    dataSet.setColors(colors);
                    dataSet.setValueTextSize(12f);

                    BarData barData = new BarData(dataSet);
                    barData.setBarWidth(0.15f);

                    bloodPressureChart.setData(barData);
                    bloodPressureChart.setFitBars(true);
                    bloodPressureChart.getDescription().setEnabled(false);
                    bloodPressureChart.getLegend().setEnabled(false);

                    XAxis xAxis = bloodPressureChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1f);
                    xAxis.setLabelCount(labels.size());

                    bloodPressureChart.invalidate();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load blood pressure data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadBloodSugarData() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .collection("blood_sugar_measurements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<BarEntry> entries = new ArrayList<>();
                    ArrayList<String> labels = new ArrayList<>();
                    int[] colors = new int[queryDocumentSnapshots.size()];
                    int index = 0;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Float value = document.getDouble("value").floatValue();
                        entries.add(new BarEntry(index, value));

                        // Determine label and color based on value
                        String label;
                        if (value < 70) {
                            label = "Hypoglycemia";
                            colors[index] = Color.YELLOW;
                        } else if (value <= 99) {
                            label = "Normal";
                            colors[index] = Color.GREEN;
                        } else if (value <= 125) {
                            label = "Prediabetes";
                            colors[index] = Color.YELLOW;
                        } else {
                            label = "Hyperglycemia";
                            colors[index] = Color.RED;
                        }
                        labels.add(label);
                        index++;
                    }

                    BarDataSet dataSet = new BarDataSet(entries, "Blood Sugar (mg/dL)");
                    dataSet.setColors(colors);
                    dataSet.setValueTextSize(12f);

                    BarData barData = new BarData(dataSet);
                    barData.setBarWidth(0.18f);

                    bloodSugarChart.setData(barData);
                    bloodSugarChart.setFitBars(true);
                    bloodSugarChart.getDescription().setEnabled(false);
                    bloodSugarChart.getLegend().setEnabled(false);

                    XAxis xAxis = bloodSugarChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1f);
                    xAxis.setLabelCount(labels.size());

                    bloodSugarChart.invalidate();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load blood sugar data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}