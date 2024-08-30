package com.example.krishiyog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.krishiyog.databinding.ActivityHomeScreenBinding;
import com.example.krishiyog.fragments.CommunityFragment;
import com.example.krishiyog.fragments.HomeFragment;
import com.example.krishiyog.fragments.ProfileFragment;
import com.example.krishiyog.fragments.ShopFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreen extends AppCompatActivity {

    ActivityHomeScreenBinding binding;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fragmentManager = getSupportFragmentManager();


        //Set default Fragment.
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment()); // Load default fragment
        }

        binding.navbar.setOnItemSelectedListener(item -> {

            resetIcons();

            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                binding.navbar.getMenu().findItem(R.id.nav_home).setIcon(R.drawable.ic_home_fill);
                loadFragment(new HomeFragment());
                return true;
            }
            else if (itemId == R.id.nav_shop) {
                binding.navbar.getMenu().findItem(R.id.nav_shop).setIcon(R.drawable.ic_shop_filled);
                loadFragment(new ShopFragment());
                return true;
            }
            else if (itemId == R.id.nav_scan) {
                binding.navbar.getMenu().findItem(R.id.nav_scan).setIcon(R.drawable.ic_scan);
                return true;
            }
            else if (itemId == R.id.nav_community) {
                binding.navbar.getMenu().findItem(R.id.nav_community).setIcon(R.drawable.ic_community_filled);
                loadFragment(new CommunityFragment());
                return true;
            }
            else if (itemId == R.id.nav_profile) {
                binding.navbar.getMenu().findItem(R.id.nav_profile).setIcon(R.drawable.ic_profile_filled);
                loadFragment(new ProfileFragment());
                return true;
            }
            else {
                return false;
            }
        });

    }

    private void resetIcons() {
        binding.navbar.getMenu().findItem(R.id.nav_home).setIcon(R.drawable.ic_home);
        binding.navbar.getMenu().findItem(R.id.nav_shop).setIcon(R.drawable.ic_shop);
        binding.navbar.getMenu().findItem(R.id.nav_scan).setIcon(R.drawable.ic_scan);
        binding.navbar.getMenu().findItem(R.id.nav_community).setIcon(R.drawable.ic_community);
        binding.navbar.getMenu().findItem(R.id.nav_profile).setIcon(R.drawable.ic_profile);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame1, fragment);
        fragmentTransaction.commit();
    }

}