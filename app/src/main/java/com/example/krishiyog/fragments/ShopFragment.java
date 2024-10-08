package com.example.krishiyog.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.krishiyog.R;
import com.example.krishiyog.adapters.CategoryAdapter;
import com.example.krishiyog.adapters.ProductAdapter;
import com.example.krishiyog.databinding.FragmentShopBinding;
import com.example.krishiyog.models.CategoriesModel;
import com.example.krishiyog.models.ProductModel;
import com.example.krishiyog.shop.Cart;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {

    FragmentShopBinding binding;
    ArrayList<SlideModel> slideModels = new ArrayList<>();
    ArrayList<CategoriesModel> categoriesModelList;
    List<ProductModel> productModelsList;
    CategoryAdapter categoryAdapter;
    ProductAdapter productAdapter;

    String[] categories = new String[]{
            "Fruits",
            "Herbs",
            "Plants",
            "Pesticides"
    };

    int[] categoryImg = new int[]{
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4
    };

    public ShopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //binding
        binding = FragmentShopBinding.inflate(inflater, container, false);

        //AdSlider Integration
        slideBanner();

        //Open Cart
        binding.cartBtn.setOnClickListener(view -> openCartActivity());

        //Category Recycler View
        categoryRecyclerView();

        //Product Recycler View
        productRecyclerView();


        return binding.getRoot();
    }

    private void openCartActivity() {
        Intent i = new Intent(getContext(), Cart.class);
        startActivity(i);
    }

    private void productRecyclerView() {

        binding.productRv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.categoriesRv.setHasFixedSize(true);
        productModelsList = new ArrayList<>();
        productAdapter = new ProductAdapter(productModelsList);

        binding.productRv.setAdapter(productAdapter);

        //Adding Trial Product for testing
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));
        productModelsList.add(new ProductModel(categoryImg[1], "Apple", "$200", "4.2"));


        productAdapter.notifyDataSetChanged();
    }

    private void categoryRecyclerView() {

        binding.categoriesRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.categoriesRv.setHasFixedSize(true);
        categoriesModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoriesModelList);
        binding.categoriesRv.setAdapter(categoryAdapter);

        for (int i=0 ; i<categories.length ; i++){
            CategoriesModel categoriesModel = new CategoriesModel(categories[i], categoryImg[i]);
            categoriesModelList.add(categoriesModel);
        }

        categoryAdapter.notifyDataSetChanged();

    }

    private void slideBanner() {
        slideModels.add(new SlideModel(R.drawable.img1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.img2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.img3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.img4, ScaleTypes.FIT));

        binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);
        binding.imageSlider.setSlideAnimation(AnimationTypes.DEPTH_SLIDE);
    }
}