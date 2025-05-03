package com.example.zenioapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        setContentView(R.layout.activity_splash);

        // Apply fade-in animation to logo
        ImageView logo = findViewById(R.id.logo);
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        logo.startAnimation(fadeIn);

        // Set up button click listeners
        Button signUpButton = findViewById(R.id.sign_up_button);
        Button signInButton = findViewById(R.id.sign_in_button);

        signUpButton.setOnClickListener(v -> {
            // Navigate to Sign Up screen
            startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
            finish();
        });

        signInButton.setOnClickListener(v -> {
            // Navigate to Sign In screen
            startActivity(new Intent(SplashActivity.this, SignInActivity.class));
            finish();
        });
    }
}