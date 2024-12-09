package com.example.krishiyog.shop;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.krishiyog.R;
import com.example.krishiyog.adapters.ProductAdapter;
import com.example.krishiyog.databinding.ActivityExploreProductBinding;
import com.example.krishiyog.models.CategoriesModel;
import com.example.krishiyog.models.ProductModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ExploreProduct extends AppCompatActivity {

    ActivityExploreProductBinding binding;
    ArrayList<ProductModel> productModels;
    ProductAdapter productAdapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityExploreProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        //recycler view
        itemRecyclerView();

        //BackBtn
        binding.backArrow.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void itemRecyclerView() {
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.setHasFixedSize(true);
        productModels = new ArrayList<>();
        productAdapter = new ProductAdapter(productModels, ProductAdapter.LAYOUT_EXPLORE);
        binding.recyclerView.setAdapter(productAdapter);

        fetchItem();
    }

    private void fetchItem() {
        db.collection("products").addSnapshotListener((value, error) -> {
            if (error != null){
                Log.w("FirestoreData","Listened Failed.", error);
                return;
            }

            if (value != null){
                productModels.clear();
                for (QueryDocumentSnapshot documentSnapshot : value){
                    ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                    productModels.add(productModel);
                }
                productAdapter.notifyDataSetChanged(); // Notify adapter of data change
            }
        });
    }
}