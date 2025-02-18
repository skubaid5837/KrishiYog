package com.example.krishiyog.shop;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.krishiyog.R;
import com.example.krishiyog.adapters.CategoryAdapter;
import com.example.krishiyog.databinding.ActivityExploreCategoryBinding;
import com.example.krishiyog.models.CategoriesModel;

import java.util.ArrayList;

public class ExploreCategory extends AppCompatActivity {

    ActivityExploreCategoryBinding binding;
    ArrayList<CategoriesModel> categoriesModelList;
    CategoryAdapter categoryAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityExploreCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        //Category Recycler View
        categoryRecyclerView();

    }

    private void categoryRecyclerView() {

        binding.categoryGrid.setLayoutManager(new GridLayoutManager(this, 2));
        binding.categoryGrid.setHasFixedSize(true);
        categoriesModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoriesModelList, CategoryAdapter.LAYOUT_EXPLORE);
        binding.categoryGrid.setAdapter(categoryAdapter);

        for (int i=0 ; i<categories.length ; i++){
            CategoriesModel categoriesModel = new CategoriesModel(categories[i], categoryImg[i]);
            categoriesModelList.add(categoriesModel);
        }

        categoryAdapter.notifyDataSetChanged();
    }
}