package com.example.krishiyog.shop;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.krishiyog.R;
import com.example.krishiyog.adapters.CartAdapter;
import com.example.krishiyog.adapters.CategoryAdapter;
import com.example.krishiyog.adapters.ProductAdapter;
import com.example.krishiyog.databinding.ActivityCartBinding;
import com.example.krishiyog.models.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {

    ActivityCartBinding binding;
    List<ProductModel> productModelsList;
    CartAdapter cartAdapter;
    int[] categoryImg = new int[]{
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //prodcut RecyclerView
        itemRecyclerView();


    }

    private void itemRecyclerView() {

        binding.cartRv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.cartRv.hasFixedSize();
        productModelsList = new ArrayList<>();
        cartAdapter = new CartAdapter(productModelsList);

        binding.cartRv.setAdapter(cartAdapter);

        //Adding Trial Product for testing
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));



    }
}