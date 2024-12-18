package com.example.krishiyog.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.krishiyog.R;
import com.example.krishiyog.adapters.CartAdapter;
import com.example.krishiyog.databinding.ActivityCheckoutBinding;
import com.example.krishiyog.databinding.DialogEditAddressBinding;
import com.example.krishiyog.models.CartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
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

        //fetch Address from db
        fetchAddress();

        //Edit address
        binding.editAddress.setOnClickListener(view -> {
            showEditAddressDialog();
        });

    }

    private void fetchAddress() {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, String> shippingAddress = (Map<String, String>) documentSnapshot.get("shippingAddress");
                        if (shippingAddress != null) {
                            String name = shippingAddress.get("name");
                            String address = shippingAddress.get("address");
                            String phone = shippingAddress.get("phone");

                            // Update the shipping address TextView
                            binding.shippingAddress.setText(name + "\n" + address + "\n" + phone);
                        } else {
                            binding.editAddress.setText("Add Address");
                        }
                    } else {
                        binding.shippingAddress.setText("Add Address");
                    }
                });
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
                });
    }

    private void showEditAddressDialog() {
        // Inflate the dialog layout
        DialogEditAddressBinding dialogEditAddressBinding  = DialogEditAddressBinding.inflate(LayoutInflater.from(this));

        // Pre-fill fields with existing data
        String[] currentAddress = binding.shippingAddress.getText().toString().split("\n");
        if (currentAddress.length >= 3) {
            dialogEditAddressBinding.etName.setText(currentAddress[0]); // Name
            dialogEditAddressBinding.etAddress.setText(currentAddress[1]); // Address
            dialogEditAddressBinding.etPhone.setText(currentAddress[2]); // Phone
        }

        // Show dialog
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogEditAddressBinding.getRoot())
                .create();

        alertDialog.show();

        // Save the address and update TextView
        dialogEditAddressBinding.btnSaveAddress.setOnClickListener(v -> {
            String name = dialogEditAddressBinding.etName.getText().toString().trim();
            String address = dialogEditAddressBinding.etAddress.getText().toString().trim();
            String phone = dialogEditAddressBinding.etPhone.getText().toString().trim();

            // Validate inputs
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            } else if (address.isEmpty() || address.length() < 20) {
                Toast.makeText(this, "Address must be at least 20 characters", Toast.LENGTH_SHORT).show();
            } else if (phone.isEmpty() || phone.length() != 10 || !phone.matches("\\d+")) {
                Toast.makeText(this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            } else {
                // Firebase: Save address data
                String userId = mAuth.getCurrentUser().getUid();
                // Get current user ID
                // Create a HashMap to store the address details
                Map<String, Object> addressData = new HashMap<>();
                addressData.put("name", name);
                addressData.put("address", address);
                addressData.put("phone", phone);

                // Save the data under the user's collection or document
                db.collection("users").document(userId).update("shippingAddress", addressData)
                        .addOnSuccessListener(unused -> {
                            // Update the shipping address TextView on success
                            binding.shippingAddress.setText(name + "\n" + address + "\n" + phone);
                            alertDialog.dismiss();
                            Toast.makeText(this, "Address updated successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Handle any errors
                            Toast.makeText(this, "Failed to update address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // Handle Cancel Button Click
        dialogEditAddressBinding.btnCancel.setOnClickListener(v -> alertDialog.dismiss());
    }
}