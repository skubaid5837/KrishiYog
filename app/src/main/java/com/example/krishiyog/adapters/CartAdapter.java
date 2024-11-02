package com.example.krishiyog.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.krishiyog.FirebaseManager;
import com.example.krishiyog.R;
import com.example.krishiyog.databinding.CardviewProductBinding;
import com.example.krishiyog.databinding.CartProductCardviewBinding;
import com.example.krishiyog.models.CartModel;
import com.example.krishiyog.models.ProductModel;
import com.example.krishiyog.shop.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    List<CartModel> productModelArrayList;
    Cart cartActivity;

    public CartAdapter(List<CartModel> productModelArrayList, Cart cartActivity) {
        this.productModelArrayList = productModelArrayList;
        this.cartActivity = cartActivity;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CartProductCardviewBinding binding = CartProductCardviewBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartModel productModel = productModelArrayList.get(position);
        holder.bind(productModel);
    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final CartProductCardviewBinding binding;
        String userId;

        public ViewHolder(CartProductCardviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        public void bind(CartModel productModel) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();

            userId = auth.getCurrentUser().getUid();

            binding.productName.setText(productModel.getProductName());
            binding.productPrice.setText("₹"+productModel.getProductPrice());
            binding.productQuantity.setText(String.valueOf(productModel.getQuantity()));

            // Handle Plus button click
            binding.plusBtn.setOnClickListener(view -> {
                int productQuantity = productModel.getQuantity() + 1;
                productModel.setQuantity(productQuantity);
                binding.productQuantity.setText(String.valueOf(productQuantity));
                updateProductQuantityInFirebase(productModel);
                cartActivity.updatePriceDetails(productModel.getProductPrice(), true);
                notifyDataSetChanged();
                Toast.makeText(view.getContext(), "Added one item", Toast.LENGTH_SHORT).show();
            });

            // Handle Minus button click
            binding.minusBtn.setOnClickListener(v -> {
                int quantity = productModel.getQuantity();
                if (quantity > 1) {
                    quantity--;
                    productModel.setQuantity(quantity);
                    binding.productQuantity.setText(String.valueOf(quantity));
                    updateProductQuantityInFirebase(productModel);
                    cartActivity.updatePriceDetails(productModel.getProductPrice(), false);
                    Toast.makeText(cartActivity, "Removed one item", Toast.LENGTH_SHORT).show();
                }
            });

            Glide.with(itemView.getContext())
                    .load(productModel.getImageUrls())
                    .into(binding.productImg);

            //Delete item from cart
            binding.deleteItem.setOnClickListener(view -> {

                int position = getAdapterPosition();
                String productId = productModel.getProductId();

                // Remove product from Firestore
                db.collection("cartItem").document(userId)
                        .update("productList." + productId, FieldValue.delete())
                        .addOnSuccessListener(aVoid -> {

                            // Remove item from list and update RecyclerView
                            String removedPrice = productModel.getProductPrice();
                            productModelArrayList.remove(position);
                            notifyItemRemoved(position);

                            // Call method to update item count in Cart activity
                            cartActivity.updatePriceDetails(removedPrice, false);

                            Toast.makeText(view.getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(view.getContext(), "Error removing item", Toast.LENGTH_SHORT).show());
            });
        }

        private void updateProductQuantityInFirebase(CartModel product) {
            FirebaseManager.getInstance().getFirestore().collection("cartItem")
                    .document(userId)
                    .update("productList." + product.getProductId(), product.getQuantity())
                    .addOnFailureListener(e -> Toast.makeText(cartActivity, "Failed to update quantity", Toast.LENGTH_SHORT).show());
        }
    }
}
