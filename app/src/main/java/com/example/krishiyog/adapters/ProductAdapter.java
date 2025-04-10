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
import com.example.krishiyog.databinding.ExploreCardViewBinding;
import com.example.krishiyog.shop.ProductDescription;
import com.example.krishiyog.databinding.CardviewProductBinding;
import com.example.krishiyog.models.ProductModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LAYOUT_EXPLORE = 1;
    public static final int LAYOUT_HOME = 2;

    List<ProductModel> productModelArrayList;
    private final int layoutType;

    public ProductAdapter(List<ProductModel> productModelArrayList, int layoutType) {
        this.productModelArrayList = productModelArrayList;
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
            ExploreCardViewBinding binding = ExploreCardViewBinding.inflate(inflater, parent, false);
            return new ExploreViewHolder(binding);
        } else if (viewType == LAYOUT_HOME) {
            CardviewProductBinding binding = CardviewProductBinding.inflate(inflater, parent, false);
            return new HomeViewHolder(binding);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductModel productModel = productModelArrayList.get(position);

        if (holder instanceof ExploreViewHolder) {
            ((ExploreViewHolder) holder).bind(productModel);
        } else if (holder instanceof HomeViewHolder) {
            ((HomeViewHolder) holder).bind(productModel);
        }
    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }

    //Explore ViewHolder
    public static class ExploreViewHolder extends RecyclerView.ViewHolder {
        private final ExploreCardViewBinding binding;

        public ExploreViewHolder(ExploreCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ProductModel productModel) {
            binding.productName.setText(productModel.getProductName());
            binding.productSize.setText("("+productModel.getProductSize());
            binding.productUnit.setText(productModel.getProductUnit()+")");
            binding.productPrice.setText("₹" + productModel.getProductPrice());

            if (productModel.getImageUrls() != null && !productModel.getImageUrls().isEmpty()) {
                String firstImageUrl = productModel.getImageUrls().get(0);
//                Glide.with(itemView.getContext())
//                        .load(firstImageUrl)
//                        .placeholder(R.drawable.pesticide)
//                        .into(binding.productImage);
                Picasso.get().load(firstImageUrl).into(binding.productImage);
            } else {
                binding.productImage.setImageResource(R.drawable.pesticide);
            }

            itemView.setOnClickListener(view -> {
                Intent i = new Intent(view.getContext(), ProductDescription.class);

                //Sending the product data to next activity
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
        }
    }

    //Home ViewHolder
    public static class HomeViewHolder extends RecyclerView.ViewHolder {

        private final CardviewProductBinding binding;

        public HomeViewHolder(CardviewProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ProductModel productModel) {
            binding.productName.setText(productModel.getProductName());
            binding.productSize.setText("("+productModel.getProductSize());
            binding.productUnit.setText(productModel.getProductUnit()+")");
            binding.productPrice.setText("₹"+productModel.getProductPrice());
            binding.productRating.setText(String.valueOf(productModel.getProductRating()));
            // Check if imageUrl is null before accessing it
            if (productModel.getImageUrls() != null && !productModel.getImageUrls().isEmpty()) {
                String firstImageUrl = productModel.getImageUrls().get(0);
//                Glide.with(itemView.getContext())
//                        .load(firstImageUrl)
//                        .into(binding.image);
                Picasso.get().load(firstImageUrl).into(binding.image);
            } else {
                // Optionally set a placeholder image or hide the image view
                binding.image.setImageResource(com.denzcoskun.imageslider.R.drawable.default_placeholder); // Add a placeholder image
            }

            // Set the click listener
            binding.cardView.setOnClickListener(view -> {
                Intent i = new Intent(view.getContext(), ProductDescription.class);

                //Sending the product data to next activity
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

        }
    }

}
