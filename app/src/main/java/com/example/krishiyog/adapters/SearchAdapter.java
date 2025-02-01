package com.example.krishiyog.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.krishiyog.databinding.SearchItemCvBinding;
import com.example.krishiyog.models.ProductModel;

import java.util.List;

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

        private final SearchItemCvBinding binding;

        public ViewHolder(SearchItemCvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ProductModel productModel) {
            binding.productName.setText(productModel.getProductName());
            binding.productPrice.setText("₹"+productModel.getProductPrice());
        }
    }

}
