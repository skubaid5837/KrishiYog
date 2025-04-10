package com.example.krishiyog.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.krishiyog.databinding.SearchItemCvBinding;
import com.example.krishiyog.models.ProductModel;
import com.example.krishiyog.shop.Cart;
import com.example.krishiyog.shop.ProductDescription;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    List<ProductModel> productModelArrayList;

    public SearchAdapter(List<ProductModel> productModelArrayList){
        this.productModelArrayList = productModelArrayList;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SearchItemCvBinding binding = SearchItemCvBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        ProductModel productModel = productModelArrayList.get(position);
        holder.bind(productModel);
    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();

        private final SearchItemCvBinding binding;

        public ViewHolder(SearchItemCvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ProductModel productModel) {
            binding.productName.setText(productModel.getProductName());
            binding.productPrice.setText("₹"+productModel.getProductPrice());
//            Glide.with(itemView.getContext())
//                    .load(productModel.getImageUrls().get(0))
//                    .into(binding.productImg);
            Picasso.get().load(productModel.getImageUrls().get(0)).into(binding.productImg);

            binding.product.setOnClickListener(view -> {
                Intent i = new Intent(itemView.getContext(), ProductDescription.class);
                Bundle bundle = new Bundle();
                ArrayList<String> imagesList = new ArrayList<>(productModel.getImageUrls());
                bundle.putStringArrayList("imagesList", imagesList);
                bundle.putString("productName", productModel.getProductName());
                bundle.putString("productDescription", productModel.getProductDescription());
                bundle.putString("productSize", "("+productModel.getProductSize());
                bundle.putString("productUnit", productModel.getProductUnit()+")");
                bundle.putString("productPrice", productModel.getProductPrice());
                bundle.putString("productId", productModel.getProductId());
                i.putExtras(bundle);
                view.getContext().startActivity(i);
            });

            // Add to Cart or Go to Cart action
            binding.addToCartBtn.setOnClickListener(view -> {
                String userId = mAuth.getCurrentUser().getUid();
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
                    int newQuantity = cartData.getOrDefault(productModel.getProductId(), 0) + 1;
                    cartData.put(productModel.getProductId(), newQuantity);

                    // Set the updated product quantities in the cart document
                    transaction.set(db.collection("cartItem").document(userId), Collections.singletonMap("productList", cartData));

                    return null;
                }).addOnSuccessListener(aVoid -> {
                    Toast.makeText(itemView.getContext(), "Item added to cart", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(itemView.getContext(), "Item is Already in Cart", Toast.LENGTH_SHORT).show();
                });
            });

        }
    }

}
