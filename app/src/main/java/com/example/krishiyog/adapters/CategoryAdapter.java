package com.example.krishiyog.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.krishiyog.databinding.CardviewProductBinding;
import com.example.krishiyog.databinding.CategorieCardviewBinding;
import com.example.krishiyog.databinding.CategoryItemBinding;
import com.example.krishiyog.databinding.ExploreCardViewBinding;
import com.example.krishiyog.models.CategoriesModel;
import com.example.krishiyog.shop.ExploreCategoryProduct;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LAYOUT_EXPLORE = 1;
    public static final int LAYOUT_HOME = 2;
    private final int layoutType;
    ArrayList<CategoriesModel> categoriesModelArrayList;

    public CategoryAdapter(ArrayList<CategoriesModel> categoriesModelArrayList, int layoutType) {
        this.categoriesModelArrayList = categoriesModelArrayList;
        this.layoutType = layoutType;
    }

    @Override
    public int getItemViewType(int position) {
        return layoutType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == LAYOUT_EXPLORE) {
            CategoryItemBinding binding = CategoryItemBinding.inflate(inflater, parent, false);
            return new CategoryAdapter.ExploreViewHolder(binding);
        } else if (viewType == LAYOUT_HOME) {
            CategorieCardviewBinding binding = CategorieCardviewBinding.inflate(inflater, parent, false);
            return new CategoryAdapter.HomeViewHolder(binding);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CategoriesModel categoriesModel = categoriesModelArrayList.get(position);

        if (holder instanceof CategoryAdapter.ExploreViewHolder) {
            ((CategoryAdapter.ExploreViewHolder) holder).bind(categoriesModel);
        } else if (holder instanceof CategoryAdapter.HomeViewHolder) {
            ((CategoryAdapter.HomeViewHolder) holder).bind(categoriesModel);
        }
    }

    @Override
    public int getItemCount() {
        return categoriesModelArrayList.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {

        private final CategorieCardviewBinding binding;

        public HomeViewHolder(CategorieCardviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CategoriesModel categoriesModel) {
            binding.categoryImg.setImageResource(categoriesModel.getImage());
            binding.categoryText.setText(categoriesModel.getCategory());

            itemView.setOnClickListener(view -> {
                Context context = view.getContext();
                Intent intent = new Intent(context, ExploreCategoryProduct.class); // Replace with your next screen activity
                intent.putExtra("categoryName", categoriesModel.getCategory()); // Pass the category name or ID
                context.startActivity(intent);
            });
        }
    }

    public static class ExploreViewHolder extends RecyclerView.ViewHolder {

        private final CategoryItemBinding binding;

        public ExploreViewHolder(CategoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CategoriesModel categoriesModel) {
            binding.categoryImage.setImageResource(categoriesModel.getImage());
            binding.categoryName.setText(categoriesModel.getCategory());

            itemView.setOnClickListener(view -> {
                Context context = view.getContext();
                Intent intent = new Intent(context, ExploreCategoryProduct.class); // Replace with your next screen activity
                intent.putExtra("categoryName", categoriesModel.getCategory()); // Pass the category name or ID
                context.startActivity(intent);
            });

        }
    }

}
