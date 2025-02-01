package com.example.krishiyog.fragments;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.krishiyog.R;
import com.example.krishiyog.adapters.CategoryAdapter;
import com.example.krishiyog.adapters.ProductAdapter;
import com.example.krishiyog.adapters.SearchAdapter;
import com.example.krishiyog.databinding.FragmentShopBinding;
import com.example.krishiyog.models.CategoriesModel;
import com.example.krishiyog.models.ProductModel;
import com.example.krishiyog.shop.Cart;
import com.example.krishiyog.shop.ExploreCategory;
import com.example.krishiyog.shop.ExploreProduct;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    List<ProductModel> searchList;
    CategoryAdapter categoryAdapter;
    ProductAdapter productAdapter;
    SearchAdapter searchAdapter;
    FragmentManager fragmentManager;

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

        //searchAdapter
        searchRecyclerView();


        return binding.getRoot();
    }

    private void searchRecyclerView() {
        searchList = new ArrayList<>();
        searchAdapter = new SearchAdapter(searchList);

        binding.searchResultRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.searchResultRv.setAdapter(searchAdapter);

        // Hide empty state initially
        binding.noProductFound.setVisibility(View.GONE);

        // Search input listener
        setupSearchFunctionality();
    }

    private void setupSearchFunctionality() {
        // Show filtered results when text changes
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    searchProducts(query);
                } else {
                    binding.searchResultRv.setVisibility(View.GONE);
                    binding.noProductFound.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Hide search results when touching outside
        binding.searchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                binding.searchResultRv.setVisibility(View.GONE);
                binding.noProductFound.setVisibility(View.GONE);
            }
        });

        binding.parent.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                binding.searchInput.clearFocus();
                binding.searchInput.setText("");
                hideKeyboard(binding.searchInput);
            }
            return false;
        });
    }

    private void searchProducts(String query) {

        String lowerCaseQuery = query.toLowerCase();

        db.collection("products")
                .whereArrayContains("searchKeywords", query.toLowerCase())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        searchList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ProductModel product = document.toObject(ProductModel.class);
                            searchList.add(product);
                        }

                        if (searchList.isEmpty()) {
                            binding.searchResultRv.setVisibility(View.GONE);
                            binding.noProductFound.setVisibility(View.VISIBLE);
                        } else {
                            binding.noProductFound.setVisibility(View.GONE);
                            binding.searchResultRv.setVisibility(View.VISIBLE);
                            searchAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "Search failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    // Method to hide the keyboard
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}