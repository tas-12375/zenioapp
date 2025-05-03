package com.example.zenioapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HeartRateActivity extends AppCompatActivity {

    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        // Initialize CameraManager for flash control
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0]; // Use the first camera (usually rear)
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Unable to access camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Turn on the flash
        turnOnFlash();

        // Simulate measurement process and navigate to MeasureActivity after 3 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            turnOffFlash();
            startActivity(new Intent(HeartRateActivity.this, MeasureActivity.class));
            finish();
        }, 3000);
    }

    private void turnOnFlash() {
        try {
            cameraManager.setTorchMode(cameraId, true);
            isFlashOn = true;
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Unable to turn on flash: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void turnOffFlash() {
        if (isFlashOn) {
            try {
                cameraManager.setTorchMode(cameraId, false);
                isFlashOn = false;
            } catch (CameraAccessException e) {
                Toast.makeText(this, "Unable to turn off flash: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOffFlash(); // Ensure flash is off when activity is destroyed
    }
}