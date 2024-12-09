package com.example.krishiyog.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.krishiyog.R;
import com.example.krishiyog.adapters.CartAdapter;
import com.example.krishiyog.databinding.ActivityCheckoutBinding;
import com.example.krishiyog.models.CartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    ActivityCheckoutBinding binding;
    FirebaseFirestore db;
    List<CartModel> cartModelList;
    CartAdapter cartAdapter;
    FirebaseAuth mAuth;
    Intent i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        i = getIntent();

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.originalPrice.setText("₹" + i.getDoubleExtra("totalMrp", 0.0));
        binding.deliveryPrice.setText("₹" + i.getDoubleExtra("delivery", 0.0));
        binding.discountPrice.setText("₹" + i.getDoubleExtra("discount", 0.0));
        binding.totalPrice.setText("₹" + i.getDoubleExtra("finalMrp", 0.0));

        //product RecyclerView
        itemRecyclerView();

    }

    private void itemRecyclerView() {

        binding.productRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.productRv.hasFixedSize();
        cartModelList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartModelList, null, true);
        binding.productRv.setAdapter(cartAdapter);

        String userId = mAuth.getCurrentUser().getUid();

        //fetch CartItem
        db.collection("cartItem")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get product quantities from the document
                        Map<String, Long> productIds = (Map<String, Long>) documentSnapshot.get("productList");

                        if (productIds != null) {
                            // Clear previous data if necessary
                            cartModelList.clear();

                            // Iterate through the product IDs and their quantities
                            for (Map.Entry<String, Long> entry : productIds.entrySet()) {
                                String productId = entry.getKey();
                                int quantity = entry.getValue().intValue();

                                db.collection("products").document(productId)
                                        .get()
                                        .addOnSuccessListener(productDoc -> {
                                            if (productDoc.exists()) {
                                                String productName = productDoc.getString("productName");
                                                String price = productDoc.getString("productPrice");

                                                // Retrieve the first image URL from imageUrls list
                                                List<String> imageUrls = (List<String>) productDoc.get("imageUrls");
                                                String imageUrl = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;

                                                // Create a new CartModel and add it to the list
                                                CartModel cartModel = new CartModel(imageUrl, productName, price, productId, quantity);
                                                cartModelList.add(cartModel);

                                                // Notify the adapter of the new data
                                                cartAdapter.notifyDataSetChanged();

                                            }
                                        }).addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error in getting product id: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    }else {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}