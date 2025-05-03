package com.example.zenioapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    private static final String[] TIPS = {
            "Eat a balanced diet rich in fruits and vegetables to support heart health.",
            "Regular exercise can help lower blood sugar levels and improve circulation.",
            "Limit salt intake to maintain healthy blood pressure levels.",
            "Stay hydrated to support overall cardiovascular health.",
            "Avoid smoking to reduce the risk of heart disease and high blood pressure.",
            "Monitor your sugar intake to prevent spikes that affect blood health.",
            "Get at least 7-8 hours of sleep to support heart and blood sugar regulation.",
            "Incorporate omega-3 fatty acids (like fish) for a healthy heart.",
            "Check your blood pressure regularly to catch issues early.",
            "Manage stress with meditation to improve heart and sugar levels."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize toolbar and notification icon
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView notificationIcon = findViewById(R.id.notification_icon);

        Button measureButton = findViewById(R.id.measure_button);
        Button bloodPressureButton = findViewById(R.id.blood_pressure_button);
        Button bloodSugarButton = findViewById(R.id.blood_sugar_button);
        Button tipButton = findViewById(R.id.tip_of_the_day_button);
        Button chatbotButton = findViewById(R.id.chatbot_button);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home as the selected item
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        measureButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, HeartRateActivity.class));
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
        });
        bloodPressureButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, AnalyticsActivity.class));
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
        });
        bloodSugarButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, AnalyticsActivity.class));
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
        });
        tipButton.setOnClickListener(v -> showTipDialog());
        chatbotButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ChatActivity.class));
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
        });

        notificationIcon.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_analytics) {
                startActivity(new Intent(HomeActivity.this, AnalyticsActivity.class));
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
    }

    private void showTipDialog() {
        Random random = new Random();
        String tip = TIPS[random.nextInt(TIPS.length)];
        new AlertDialog.Builder(this)
                .setTitle("Tip of the Day")
                .setMessage(tip)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}