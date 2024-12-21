package com.example.krishiyog.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.krishiyog.R;
import com.example.krishiyog.adapters.PostAdapter;
import com.example.krishiyog.adapters.ProductAdapter;
import com.example.krishiyog.community.AddPostScreen;
import com.example.krishiyog.databinding.FragmentCommunityBinding;
import com.example.krishiyog.databinding.FragmentShopBinding;
import com.example.krishiyog.models.OrderModel;
import com.example.krishiyog.models.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommunityFragment extends Fragment {

    FragmentCommunityBinding binding;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    List<PostModel> postModelList;
    PostAdapter adapter;

    public CommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCommunityBinding.inflate(getLayoutInflater());
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        
        //fetch Post in Rv
        setRecyclerView();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void setRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postModelList = new ArrayList<>();
        adapter = new PostAdapter(postModelList);
        binding.recyclerView.setAdapter(adapter);

        fetchPosts();

        binding.fabAskCommunity.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), AddPostScreen.class));
        });

    }

    private void fetchPosts() {
        db.collection("posts")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {

                        postModelList.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            PostModel post = document.toObject(PostModel.class);
                            if (post != null && post.isStatus()) {
                                // Add order to the list if it belongs to the current user
                                postModelList.add(post);
                            }
                        }

                        // Update the RecyclerView data
                        adapter.updateData(postModelList);
                    }
                });
    }
}