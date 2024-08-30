package com.example.krishiyog.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.krishiyog.databinding.CategorieCardviewBinding;
import com.example.krishiyog.models.CategoriesModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    ArrayList<CategoriesModel> categoriesModelArrayList;

    public CategoryAdapter(ArrayList<CategoriesModel> categoriesModelArrayList) {
        this.categoriesModelArrayList = categoriesModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CategorieCardviewBinding binding = CategorieCardviewBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoriesModel categoriesModel = categoriesModelArrayList.get(position);
        holder.bind(categoriesModel);
    }

    @Override
    public int getItemCount() {
        return categoriesModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final CategorieCardviewBinding binding;

        public ViewHolder(CategorieCardviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CategoriesModel categoriesModel) {
            binding.categoryImg.setImageResource(categoriesModel.getImage());
            binding.categoryText.setText(categoriesModel.getCategory());
        }
    }

}
