package com.example.krishiyog.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.krishiyog.LoginScreen;
import com.example.krishiyog.R;
import com.example.krishiyog.databinding.FragmentProfileBinding;
import com.example.krishiyog.models.OrderModel;
import com.example.krishiyog.shop.OrderScreen;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    private FirebaseAuth mAuth;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();

        binding.logOutBtn.setOnClickListener(view -> logout());
        binding.myOrder.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), OrderScreen.class);
            startActivity(i);
        });

        return binding.getRoot();

    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginScreen.class);
        startActivity(intent);
        getActivity().finish();
    }
}