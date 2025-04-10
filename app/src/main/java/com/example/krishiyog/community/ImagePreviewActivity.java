package com.example.krishiyog.community;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.krishiyog.R;
import com.example.krishiyog.databinding.ActivityImagePreviewBinding;
import com.squareup.picasso.Picasso;

public class ImagePreviewActivity extends AppCompatActivity {

    ActivityImagePreviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityImagePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String imageUrl = getIntent().getStringExtra("imageUrl");

//        Glide.with(this)
//                .load(imageUrl)
//                .into(binding.fullScreenImage);
        Picasso.get().load(imageUrl).into(binding.fullScreenImage);

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });
    }

}