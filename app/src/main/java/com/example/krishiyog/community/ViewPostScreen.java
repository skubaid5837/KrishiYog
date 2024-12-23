package com.example.krishiyog.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.krishiyog.R;
import com.example.krishiyog.adapters.CommentAdapter;
import com.example.krishiyog.databinding.ActivityViewPostScreenBinding;
import com.example.krishiyog.models.CommentModel;
import com.example.krishiyog.models.PostModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ViewPostScreen extends AppCompatActivity {

    ActivityViewPostScreenBinding binding;
    private String postId;
    FirebaseFirestore db;
    ArrayList<SlideModel> slideModels = new ArrayList<>();
    List<CommentModel> commentModelList;
    CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityViewPostScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        //bind data
        getPostData();

        //Recycler View
        setRecyclerView();
    }

    private void setRecyclerView() {
        binding.commentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.commentRv.hasFixedSize();
        commentModelList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentModelList);
        binding.commentRv.setAdapter(commentAdapter);

        fetchComment();
    }

    private void fetchComment() {
        db.collection("posts")
                .document(postId)
                .collection("comments")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(!documentSnapshot.isEmpty()){
                        commentModelList.clear();
                        for (DocumentSnapshot snapshot : documentSnapshot){
                            CommentModel commentModel = snapshot.toObject(CommentModel.class);
                            if(commentModel != null){
                                commentModelList.add(commentModel);
                            }
                        }
                        commentAdapter.updateData(commentModelList);
                    }
                });
    }

    private void getPostData() {
        db.collection("posts")
                .document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        PostModel postModel = documentSnapshot.toObject(PostModel.class);
                        if (postModel != null) {
                            bindPostData(postModel);
                        }
                    }
                });
    }

    private void bindPostData(PostModel postModel) {
        binding.username.setText(postModel.getUsername());
        binding.likeCount.setText(postModel.getLikeCount());
        binding.postDescription.setText(postModel.getPostDescription());
        Glide.with(this)
                .load(postModel.getProfileImage())
                .into(binding.profileImage);
        binding.date.setText(postModel.getLocation()+" "+postModel.getDate());

        for(int i=0 ; i<postModel.getImages().size() ; i++){
            slideModels.add(new SlideModel(postModel.getImages().get(i), ScaleTypes.FIT));
        }
        binding.postImages.setImageList(slideModels);
        binding.postImages.setSlideAnimation(AnimationTypes.DEPTH_SLIDE);

    }

}