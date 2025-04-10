package com.example.krishiyog;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.krishiyog.databinding.ActivityViewImageBinding;
import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {
    ActivityViewImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityViewImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        Picasso.get().load(getIntent().getStringExtra("imageUrl")).into(binding.imageView);
    }
}