package com.example.krishiyog.shop;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.krishiyog.FirebaseManager;
import com.example.krishiyog.R;
import com.example.krishiyog.adapters.ProductAdapter;
import com.example.krishiyog.databinding.ActivityExploreCategoryProductBinding;
import com.example.krishiyog.models.ProductModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExploreCategoryProduct extends AppCompatActivity {

    ActivityExploreCategoryProductBinding binding;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityExploreCategoryProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        String categoryName = getIntent().getStringExtra("categoryName");

        binding.backArrow.setOnClickListener(view -> {
            onBackPressed();
        });

        if (categoryName != null) {
            fetchProductsByCategory(categoryName);
            binding.title.setText(categoryName);
        }

    }

    private void fetchProductsByCategory(String categoryName) {

        db.collection("products")
                .whereEqualTo("productCategory", categoryName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ProductModel> productModelList = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        ProductModel product = documentSnapshot.toObject(ProductModel.class);
                        productModelList.add(product);
                    }
                    setUpRecyclerView(productModelList);
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setUpRecyclerView(List<ProductModel> productModelList) {

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.setHasFixedSize(true);
        ProductAdapter adapter = new ProductAdapter(productModelList, ProductAdapter.LAYOUT_EXPLORE);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setAdapter(adapter);
    }
}