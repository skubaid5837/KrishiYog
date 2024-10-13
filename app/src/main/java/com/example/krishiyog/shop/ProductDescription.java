package com.example.krishiyog.shop;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.krishiyog.R;
import com.example.krishiyog.databinding.ActivityProductDescriptionBinding;
import com.example.krishiyog.models.ProductModel;

public class ProductDescription extends AppCompatActivity {

    ActivityProductDescriptionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProductDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle bundle = getIntent().getExtras();

        //Linking the Data
        binding.productDescription.setText(bundle.getString("productDescription"));
        binding.productName.setText(bundle.getString("productName"));
        binding.productPrice.setText(bundle.getString("productPrice"));
        if(bundle.getStringArrayList("imagesList").get(0) != null) {
            String myString = bundle.getStringArrayList("imagesList").get(0);
            Glide.with(this)
                    .load(myString)
                    .into(binding.productImage);
        }
    }
}