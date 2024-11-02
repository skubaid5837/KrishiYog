package com.example.krishiyog.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.krishiyog.R;
import com.example.krishiyog.shop.ProductDescription;
import com.example.krishiyog.databinding.CardviewProductBinding;
import com.example.krishiyog.models.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    List<ProductModel> productModelArrayList;

    public ProductAdapter(List<ProductModel> productModelArrayList) {
        this.productModelArrayList = productModelArrayList;
    }

    @NonNull
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CardviewProductBinding binding = CardviewProductBinding.inflate(inflater, parent, false);
        return new ProductAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        ProductModel productModel = productModelArrayList.get(position);
        holder.bind(productModel);
    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final CardviewProductBinding binding;

        public ViewHolder(CardviewProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ProductModel productModel) {
            //binding.image.setImageResource(productModel.getImage());
            binding.productName.setText(productModel.getProductName());
            binding.productPrice.setText("₹"+productModel.getProductPrice());
            binding.productRating.setText(String.valueOf(productModel.getProductRating()));
            // Check if imageUrl is null before accessing it
            if (productModel.getImageUrls() != null && !productModel.getImageUrls().isEmpty()) {
                String firstImageUrl = productModel.getImageUrls().get(0);
                Glide.with(itemView.getContext())
                        .load(firstImageUrl)
                        .into(binding.image);
            } else {
                // Optionally set a placeholder image or hide the image view
                binding.image.setImageResource(com.denzcoskun.imageslider.R.drawable.default_placeholder); // Add a placeholder image
            }

            // Set the click listener
            binding.image.setOnClickListener(view -> {
                Intent i = new Intent(view.getContext(), ProductDescription.class);

                //Sending the product data to next activity
                Bundle bundle = new Bundle();
                ArrayList<String> imagesList = new ArrayList<>(productModel.getImageUrls());
                bundle.putStringArrayList("imagesList", imagesList);
                bundle.putString("productName", productModel.getProductName());
                bundle.putString("productDescription", productModel.getProductDescription());
                bundle.putString("productPrice", productModel.getProductPrice());
                bundle.putString("productId", productModel.getProductId());
                bundle.putString("productName", productModel.getProductName());
                bundle.putString("productName", productModel.getProductName());
                bundle.putString("productName", productModel.getProductName());
                bundle.putString("productId", productModel.getProductId());
                i.putExtras(bundle);
                view.getContext().startActivity(i);
            });

        }
    }

}
