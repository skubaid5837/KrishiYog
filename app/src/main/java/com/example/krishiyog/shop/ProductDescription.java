package com.example.krishiyog.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.krishiyog.R;
import com.example.krishiyog.databinding.ActivityProductDescriptionBinding;
import com.example.krishiyog.models.ProductModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProductDescription extends AppCompatActivity {

    ActivityProductDescriptionBinding binding;
    ArrayList<SlideModel> slideModels = new ArrayList<>();
    ArrayList<String> imageUris;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String productId;

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

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();

        binding.closeBtn.setOnClickListener(view -> {
            finish();
        });

        //Linking the Data
        productId = bundle.getString("productId");
        binding.productDescription.setText(bundle.getString("productDescription"));
        binding.productName.setText(bundle.getString("productName"));
        binding.productPrice.setText("₹"+bundle.getString("productPrice"));
        if(bundle.getStringArrayList("imagesList") != null) {
            imageUris = bundle.getStringArrayList("imagesList");
            int size = imageUris.size();
            for(int i=0 ; i<size ; i++){
                slideModels.add(new SlideModel(imageUris.get(i), ScaleTypes.FIT));
            }

            // Set the image list directly to the ImageSlider
            binding.productImage.setImageList(slideModels);

            // Check if product is in cart
            checkIfProductInCart();

            // Add to Cart or Go to Cart action
            binding.addToCart.setOnClickListener(view -> {
                if ("Go to Cart".equals(binding.addToCart.getText().toString())) {
                    // Redirect to cart activity
                    startActivity(new Intent(ProductDescription.this, Cart.class));
                } else {
                    // Add to cart
                    addToCart();
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Fetch cart data to update button state
        checkIfProductInCart();
    }

    private void checkIfProductInCart() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("cartItem").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Integer> productIds = (Map<String, Integer>) documentSnapshot.get("productList");
                        if(productIds != null && productIds.containsKey(productId)) {
                            binding.addToCart.setText("Go to Cart");
                        }else {
                            binding.addToCart.setText("Add to Cart");
                        }
                    }else {
                        binding.addToCart.setText("Add to cart");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to check cart item", Toast.LENGTH_SHORT).show();
                    binding.addToCart.setText("Add to Cart");
                });
    }

    private void addToCart() {
        String userId = auth.getCurrentUser().getUid();
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(db.collection("cartItem").document(userId));
            Map<String, Integer> cartData;
            if(snapshot.exists()) {
                cartData = (Map<String, Integer>) snapshot.get("productList");
                if (cartData == null) {
                    cartData = new HashMap<>();
                }
            }else {
                cartData = new HashMap<>();
            }
            // Update the quantity for the product
            int newQuantity = cartData.getOrDefault(productId, 0) + 1;
            cartData.put(productId, newQuantity);

            // Set the updated product quantities in the cart document
            transaction.set(db.collection("cartItem").document(userId), Collections.singletonMap("productList", cartData));

            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Item added to cart", Toast.LENGTH_SHORT).show();
            binding.addToCart.setText("Go to Cart");
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
        });
    }
}