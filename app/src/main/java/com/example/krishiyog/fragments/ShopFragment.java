package com.example.krishiyog.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.krishiyog.shop.ExploreCategory;
import com.example.krishiyog.shop.ExploreProduct;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {

    FragmentShopBinding binding;
    FirebaseFirestore db;
    ArrayList<SlideModel> slideModels = new ArrayList<>();
    ArrayList<CategoriesModel> categoriesModelList;
    List<ProductModel> productModelsList;
    CategoryAdapter categoryAdapter;
    ProductAdapter productAdapter;

    String[] categories = new String[]{
            "Seed",
            "Growth Promoter",
            "Farm Equipments",
            "Insecticide",
            "Fungicide",
            "Fertilizer",
            "Gardening",
            "Organic Farming",
            "Herbicide",
            "Pesticide",
            "Cattle Farming"
    };

    int[] categoryImg = new int[]{
            R.drawable.seed,
            R.drawable.growth_promoter,
            R.drawable.farm_equipment,
            R.drawable.insecticide,
            R.drawable.fungicide,
            R.drawable.fertilizer,
            R.drawable.gardening,
            R.drawable.organic_farming,
            R.drawable.herbicide,
            R.drawable.pesticide,
            R.drawable.cattle_farming
    };

    public ShopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //binding
        binding = FragmentShopBinding.inflate(inflater, container, false);
        //Reference
        db = FirebaseFirestore.getInstance();

        //AdSlider Integration
        slideBanner();

        //Open Cart
        binding.cartBtn.setOnClickListener(view -> openCartActivity());

        //Open Explore Page
        binding.exploreProduct.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ExploreProduct.class);
            startActivity(intent);
        });

        //Open Category Explore Screen
        binding.categoryExplore.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), ExploreCategory.class);
            startActivity(i);
        });

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
        binding.productRv.setHasFixedSize(true);
        productModelsList = new ArrayList<>();
        productAdapter = new ProductAdapter(productModelsList, ProductAdapter.LAYOUT_HOME);

        binding.productRv.setAdapter(productAdapter);


        // Fetch products from Firestore
        fetchProducts();
    }

    private void fetchProducts() {
        db.collection("products").addSnapshotListener((value, error) -> {
            if (error != null){
                Log.w("FirestoreData","Listened Failed.", error);
                return;
            }

            if (value != null){
                productModelsList.clear();
                for (QueryDocumentSnapshot  documentSnapshot : value){
                    ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                    productModelsList.add(productModel);
                }
                productAdapter.notifyDataSetChanged(); // Notify adapter of data change
            }
        });
    }

    private void categoryRecyclerView() {

        binding.categoriesRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.categoriesRv.setHasFixedSize(true);
        binding.categoriesRv.setItemAnimator(null);
        categoriesModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoriesModelList, CategoryAdapter.LAYOUT_HOME);
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