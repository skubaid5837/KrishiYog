package com.example.krishiyog.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.krishiyog.LoginScreen;
import com.example.krishiyog.R;
import com.example.krishiyog.ViewImageActivity;
import com.example.krishiyog.chatBot.ChatBotScreen;
import com.example.krishiyog.databinding.DialogEditAddressBinding;
import com.example.krishiyog.databinding.FragmentProfileBinding;
import com.example.krishiyog.models.OrderModel;
import com.example.krishiyog.models.User;
import com.example.krishiyog.shop.OrderScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1;
    String profilePhotoUri;

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
        binding.profileName.setOnClickListener(view -> {
            showUpdateNameDialog();
        });

        binding.profileImage.setOnClickListener(view -> {
            showProfilePhotoOptions();
        });

        bindData();


        return binding.getRoot();

    }

    private void showProfilePhotoOptions() {
        String[] options = {"Change Profile Photo", "View Profile Photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose an option");
        builder.setItems(options, (dialogInterface, i) -> {
            if (i == 0) {
                // Change Profile Photo
                pickImageFromGallery();

            } else if (i == 1) {
                // View Profile Photo
                viewFullProfileImage();
            }
        });
        builder.show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Upload to Firebase Storage and update Firestore
            uploadProfilePhotoToFirebase(imageUri);
            binding.profileImage.setImageResource(R.drawable.ic_profile);
        }
    }

    private void uploadProfilePhotoToFirebase(Uri imageUri) {
        binding.imageProgressBar.setVisibility(View.VISIBLE);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String userId = mAuth.getCurrentUser().getUid();
        StorageReference fileRef = storageRef.child("profile/" + userId + ".jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    //Save image URL in Firestore under the 'users' collection
                    db.collection("users").document(userId)
                            .update("profilePhotoUrl", imageUrl)
                            .addOnSuccessListener(aVoid -> {
                                binding.imageProgressBar.setVisibility(View.GONE);
                            });
                }));
    }

    private void viewFullProfileImage() {
        Intent intent = new Intent(getContext(), ViewImageActivity.class);
        if (profilePhotoUri != null) {
            intent.putExtra("imageUrl", profilePhotoUri); // Get this from Firestore or user model
            startActivity(intent);
        }
    }

    private void updateNameInComment(String newname) {
        String userId = mAuth.getCurrentUser().getUid();
        String newName = newname ; // new name from dialog
                    // Then: Update in comments
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot postDoc : queryDocumentSnapshots) {
                        String postId = postDoc.getId();
                        Map<String, Object> name = new HashMap<>();
                        name.put("username", newName);
                        db.collection("posts")
                                        .document(postId)
                                                .update(name);

                        db.collection("posts")
                                .document(postId)
                                .collection("comments")
                                .whereEqualTo("userId", userId)
                                .get()
                                .addOnSuccessListener(commentSnapshots -> {
                                    for (DocumentSnapshot commentDoc : commentSnapshots) {
                                        commentDoc.getReference().update("username", newName);
                                    }
                                });
                    }
                });

    }

    private void showUpdateNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Your Name");

        final EditText input = new EditText(getContext());
        input.setHint("Enter new name");
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                changedProfileName(newName);
                updateNameInComment(newName);
            } else {
                Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void changedProfileName(String name) {
        Map<String, Object> newName = new HashMap<>();
        newName.put("name", name);
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .update(newName);
    }

    private void bindData() {
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .addSnapshotListener((documentSnapshot, e) -> {
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                String email = documentSnapshot.getString("email");
                                String userName = documentSnapshot.getString("name");
                                if(documentSnapshot.getString("profilePhotoUrl")!=null){
                                    String profilePhoto = documentSnapshot.getString("profilePhotoUrl");
                                    this.profilePhotoUri = profilePhoto;
                                }
                                binding.profileEmail.setText(email);
                                binding.profileName.setText(userName);
                                if (profilePhotoUri != null && !profilePhotoUri.trim().isEmpty()) {
                                    Picasso.get()
                                            .load(profilePhotoUri)
                                            .placeholder(R.drawable.ic_profile)
                                            .error(R.drawable.ic_profile)
                                            .into(binding.profileImage);
                                }else {
                                    binding.profileImage.setImageResource(R.drawable.ic_profile);
                                }
                            }
                        });
    }
    
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