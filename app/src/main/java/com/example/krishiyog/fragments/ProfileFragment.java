package com.example.krishiyog.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.krishiyog.LoginScreen;
import com.example.krishiyog.R;
import com.example.krishiyog.chatBot.ChatBotScreen;
import com.example.krishiyog.databinding.DialogEditAddressBinding;
import com.example.krishiyog.databinding.FragmentProfileBinding;
import com.example.krishiyog.models.OrderModel;
import com.example.krishiyog.shop.OrderScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.logoutButton.setOnClickListener(view -> logout());
        binding.orderBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), OrderScreen.class);
            startActivity(i);
        });

        binding.chatBot.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), ChatBotScreen.class));
        });

        return binding.getRoot();

    }

//    private void test() {
//        binding.progressBar.setVisibility(View.VISIBLE);
//        db.collection("products")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        String productId = document.getId();  // Get product ID
//                        String productName = document.getString("productName"); // Get product name
//
//                        if (productName != null) {
//                            List<String> keywords = generateSearchKeywords(productName);
//
//                            // Update document with new searchKeywords field
//                            db.collection("products")
//                                    .document(productId)
//                                    .update("searchKeywords", keywords)
//                                    .addOnSuccessListener(aVoid -> {
//                                        Log.d("Update", "Updated product: " + productId);
//                                    })
//                                    .addOnFailureListener(e -> Log.e("Update", "Error updating product: " + productId, e));
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("Update", "Error fetching products", e);
//                    binding.progressBar.setVisibility(View.GONE);
//                });
//    }

//    private List<String> generateSearchKeywords(String productName) {
//        List<String> keywords = new ArrayList<>();
//        String[] words = productName.toLowerCase().split(" ");
//
//        // Add individual words
//        keywords.addAll(Arrays.asList(words));
//
//        // Add progressive combinations (e.g., "sk farming", "s k")
//        StringBuilder partial = new StringBuilder();
//        for (String word : words) {
//            partial.append(word).append(" ");
//            keywords.add(partial.toString().trim());
//        }
//
//        return keywords;
//    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginScreen.class);
        startActivity(intent);
        getActivity().finish();
    }
}