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
import com.example.krishiyog.adapters.CategoryAdapter;
import com.example.krishiyog.adapters.ProductAdapter;
import com.example.krishiyog.databinding.ActivityCartBinding;
import com.example.krishiyog.fragments.ShopFragment;
import com.example.krishiyog.models.CartModel;
import com.example.krishiyog.models.ProductModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Cart extends AppCompatActivity {

    ActivityCartBinding binding;
    List<CartModel> productModelsList;
    CartAdapter cartAdapter;
    FirebaseAuth auth;
    FirebaseFirestore db;

    private double totalMRP = 0.0;
    private double discountAmount = 0.0; // Example discount
    private double shippingCharges = 50.0; // Default shipping charges
    private double totalAmount = 0.0;

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

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.backBtn.setOnClickListener(view -> {
            finish();
        });

        binding.checkoutBtn.setOnClickListener(view -> {
            Intent i = new Intent(this, CheckoutActivity.class);
            i.putExtra("totalMrp", totalMRP);
            i.putExtra("discount", discountAmount);
            i.putExtra("delivery", shippingCharges);
            i.putExtra("finalMrp", totalAmount);
            startActivity(i);
            finish();
        });

        //product RecyclerView
        itemRecyclerView();


    }

    private void itemRecyclerView() {

        binding.cartRv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        productModelsList = new ArrayList<>();
        cartAdapter = new CartAdapter(productModelsList, this, false);
        binding.cartRv.setAdapter(cartAdapter);

        String userId = auth.getCurrentUser().getUid();

        //fetch the cartItem
        db.collection("cartItem")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get product quantities from the document
                        Map<String, Long> productIds = (Map<String, Long>) documentSnapshot.get("productList");

                        if (productIds != null) {
                            // Clear previous data if necessary
                            productModelsList.clear();
                            totalMRP = 0; // Reset total price

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
                                                String productSize = productDoc.getString("productSize");
                                                String productUnit = productDoc.getString("productUnit");

                                                // Retrieve the first image URL from imageUrls list
                                                List<String> imageUrls = (List<String>) productDoc.get("imageUrls");
                                                String imageUrl = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;

                                                // Calculate total price
                                                double itemPrice = Double.parseDouble(price);
                                                totalMRP += itemPrice * quantity; // Add the price for the quantity of this product

                                                // Create a new CartModel and add it to the list
                                                CartModel cartModel = new CartModel(imageUrl, productName, price, productId, quantity, productUnit, productSize);
                                                productModelsList.add(cartModel);

                                                // Notify the adapter of the new data
                                                cartAdapter.notifyDataSetChanged();

                                                updatePriceDetails("0",true); // Update price details whenever a product is added
                                            }
                                        }).addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error in getting product id: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                            updatePriceDetails("0",true);
                        }
                    } else {
                        Toast.makeText(this, "No product in cart", Toast.LENGTH_SHORT).show();
                        updatePriceDetails("0",true);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void updatePriceDetails(String removedPrice, Boolean isAdd) {

        // Assuming 'removedPrice' is the price of the product removed
        if (removedPrice != null && !removedPrice.isEmpty() && !removedPrice.equals("0")) {
            double priceValue = Double.parseDouble(removedPrice.replace("₹", ""));

            // Add or subtract the price based on the action
            if (isAdd) {
                totalMRP += priceValue;
            } else {
                totalMRP -= priceValue;
            }
        }

        // Calculate discount and shipping charges
        discountAmount = totalMRP * 0.05;

        if (productModelsList.isEmpty()) {
            shippingCharges = 0.0;
        } else if (totalMRP - discountAmount >= 500) {
            shippingCharges = 0.0; // Free shipping for orders over ₹500
        } else {
            shippingCharges = 50.0;
        }

        // Reset or calculate fields based on cart status
        if (productModelsList.isEmpty()) {
            totalMRP = 0.0;
            discountAmount = 0.0;
            shippingCharges = 0.0;
            totalAmount = 0.0;

            binding.checkoutBtn.setEnabled(false);
            binding.checkoutBtn.setAlpha(0.5f);

        } else {
            discountAmount = totalMRP * 0.05;
            shippingCharges = totalMRP - discountAmount >= 500 ? 0.0 : 50.0;
            totalAmount = totalMRP - discountAmount + shippingCharges;

            binding.checkoutBtn.setEnabled(true);
            binding.checkoutBtn.setAlpha(1.0f);
        }

        // Update UI fields
        binding.mrpPrice.setText("₹" + totalMRP);
        binding.discountAmount.setText("₹" + discountAmount);
        binding.shippingAmount.setText("₹" + shippingCharges);
        binding.totalPrice.setText("₹" + totalAmount);

        // Update item count
        int count = productModelsList.size();
        binding.itemCount.setText("Items (" + count + ")");

    }
}