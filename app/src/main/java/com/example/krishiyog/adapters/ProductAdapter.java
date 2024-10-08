package com.example.krishiyog.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.krishiyog.shop.ProductDescription;
import com.example.krishiyog.databinding.CardviewProductBinding;
import com.example.krishiyog.models.ProductModel;
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
            binding.image.setImageResource(productModel.getImage());
            binding.productName.setText(productModel.getProductName());
            binding.productPrice.setText(productModel.getProductPrice());
            binding.productRating.setText(productModel.getProductRating());

            // Set the click listener
            binding.image.setOnClickListener(view -> {
                Intent i = new Intent(view.getContext(), ProductDescription.class);
                i.putExtra("productImage", productModel.getImage());
                i.putExtra("productName", productModel.getProductName());
                i.putExtra("productPrice", productModel.getProductPrice());
                view.getContext().startActivity(i);
            });

        }
    }

}
