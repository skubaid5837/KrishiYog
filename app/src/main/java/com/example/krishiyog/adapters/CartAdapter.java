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
import com.example.krishiyog.databinding.CardviewCheckoutBinding;
import com.example.krishiyog.databinding.CartProductCardviewBinding;
import com.example.krishiyog.models.CartModel;
import com.example.krishiyog.shop.Cart;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    List<CartModel> productModelArrayList;
    private boolean isCheckout;
    private LayoutInflater inflater;
    Cart cartActivity;

    public CartAdapter(List<CartModel> productModelArrayList, Cart cartActivity, boolean isCheckout) {
        this.productModelArrayList = productModelArrayList;
        this.cartActivity = cartActivity;
        this.isCheckout = isCheckout;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        if(isCheckout){
            CardviewCheckoutBinding binding = CardviewCheckoutBinding.inflate(inflater, parent, false);
            return  new ViewHolder(binding);
        }
        else {
            CartProductCardviewBinding binding = CartProductCardviewBinding.inflate(inflater, parent, false);
            return new ViewHolder(binding);
        }
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

        private CartProductCardviewBinding cartBinding;
        private CardviewCheckoutBinding checkoutBinding;
        String userId = FirebaseManager.getInstance().getAuth().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ViewHolder(CartProductCardviewBinding cartBinding) {
            super(cartBinding.getRoot());
            this.cartBinding = cartBinding;
        }

        public  ViewHolder(CardviewCheckoutBinding checkoutBinding) {
            super(checkoutBinding.getRoot());
            this.checkoutBinding = checkoutBinding;
        }

        public void bind(CartModel productModel) {
            if (isCheckout) {
                // Bind data for Checkout Activity
                int count = productModel.getQuantity();
                int price =count * Integer.parseInt(productModel.getProductPrice());
                checkoutBinding.productName.setText(productModel.getProductName());
                checkoutBinding.productPrice.setText("₹" + price);
                checkoutBinding.quantity.setText("Quantity (" + count + ")");
                Glide.with(itemView.getContext())
                        .load(productModel.getImageUrls())
                        .into(checkoutBinding.productImage);
            } else {
                // Bind data for Cart Activity
                cartBinding.productName.setText(productModel.getProductName());
                cartBinding.productPrice.setText("₹" + productModel.getProductPrice());
                cartBinding.productQuantity.setText(String.valueOf(productModel.getQuantity()));
                Glide.with(itemView.getContext())
                        .load(productModel.getImageUrls())
                        .into(cartBinding.productImg);

                // Handle cart-specific actions
                cartBinding.plusBtn.setOnClickListener(view -> {
                    int productQuantity = productModel.getQuantity() + 1;
                    productModel.setQuantity(productQuantity);
                    cartBinding.productQuantity.setText(String.valueOf(productQuantity));
                    updateProductQuantityInFirebase(productModel);
                    cartActivity.updatePriceDetails(productModel.getProductPrice(), true);
                    Toast.makeText(view.getContext(), "Added one item", Toast.LENGTH_SHORT).show();
                });

                cartBinding.minusBtn.setOnClickListener(view -> {
                    int quantity = productModel.getQuantity();
                    if (quantity > 1) {
                        quantity--;
                        productModel.setQuantity(quantity);
                        cartBinding.productQuantity.setText(String.valueOf(quantity));
                        updateProductQuantityInFirebase(productModel);
                        cartActivity.updatePriceDetails(productModel.getProductPrice(), false);
                    }
                });

                cartBinding.deleteItem.setOnClickListener(view -> {
                    int position = getAdapterPosition();
                    String productId = productModel.getProductId();

                    // Remove product from Firestore
                    db.collection("cartItem").document(userId)
                            .update("productList." + productId, FieldValue.delete())
                            .addOnSuccessListener(aVoid -> {

                                // Remove item from list and update RecyclerView
                                String removedPrice = productModel.getProductPrice();
                                removedPrice = removedPrice.replace("₹", "");
                                int price = Integer.parseInt(removedPrice);
                                price = price * productModel.getQuantity();
                                productModelArrayList.remove(position);
                                notifyItemRemoved(position);

                                // Call method to update item count in Cart activity
                                cartActivity.updatePriceDetails(String.valueOf(price), false);

                                Toast.makeText(view.getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(view.getContext(), "Error removing item", Toast.LENGTH_SHORT).show());
                });
            }
        }

        private void updateProductQuantityInFirebase(CartModel product) {
            FirebaseManager.getInstance().getFirestore().collection("cartItem")
                    .document(userId)
                    .update("productList." + product.getProductId(), product.getQuantity())
                    .addOnFailureListener(e -> Toast.makeText(cartActivity, "Failed to update quantity", Toast.LENGTH_SHORT).show());
        }
    }
}
