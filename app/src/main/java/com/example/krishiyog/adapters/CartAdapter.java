package com.example.krishiyog.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.krishiyog.databinding.CardviewProductBinding;
import com.example.krishiyog.databinding.CartProductCardviewBinding;
import com.example.krishiyog.models.ProductModel;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    List<ProductModel> productModelArrayList;

    public CartAdapter(List<ProductModel> productModelArrayList) {
        this.productModelArrayList = productModelArrayList;
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
        ProductModel productModel = productModelArrayList.get(position);
        holder.bind(productModel);
    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final CartProductCardviewBinding binding;

        public ViewHolder(CartProductCardviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        public void bind(ProductModel productModel) {
            binding.productImg.setImageResource(productModel.getImage());
            binding.productName.setText(productModel.getProductName());
            binding.productPrice.setText(productModel.getProductPrice());
            binding.plusBtn.setOnClickListener(view -> {
                Toast.makeText(view.getContext(), "Added one item", Toast.LENGTH_SHORT).show();
            });
        }

    }
}
