package com.example.krishiyog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.krishiyog.databinding.ActivityWelcomeScreenBinding;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeScreen extends AppCompatActivity {

    ActivityWelcomeScreenBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityWelcomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        // Check if the user is logged in
        checkLoginStatus();

        //SignUp Btn
        binding.signBtn.setOnClickListener(view -> changeActivity(new Intent(WelcomeScreen.this, SignupScreen.class)));

        //Login Btn
        binding.loginBtn.setOnClickListener(view -> changeActivity(new Intent(WelcomeScreen.this, LoginScreen.class)));

    }

    private void checkLoginStatus() {
        if (mAuth.getCurrentUser() != null) {
            // User is signed in
            changeActivity(new Intent(WelcomeScreen.this, HomeScreen.class));
            finish(); // Close WelcomeScreen activity
        }
    }

    //Method to change activity
    private void changeActivity(Intent i){
        startActivity(i);
    }

}