package com.example.krishiyog.adapters;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.krishiyog.R;
import com.example.krishiyog.community.ImagePreviewActivity;
import com.example.krishiyog.community.ViewPostScreen;
import com.example.krishiyog.databinding.PostCardviewBinding;
import com.example.krishiyog.models.CommentModel;
import com.example.krishiyog.models.OrderModel;
import com.example.krishiyog.models.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<PostModel> postModelList;

    public PostAdapter(List<PostModel> postModelList) {
        this.postModelList = postModelList;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        PostCardviewBinding binding = PostCardviewBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        PostModel postModel = postModelList.get(position);
        // Handle comment button click
        holder.binding.commentButton.setOnClickListener(v -> {
            if (holder.binding.commentSection.getVisibility() == View.GONE) {
                holder.binding.commentSection.setVisibility(View.VISIBLE);
                holder.binding.commentInput.requestFocus();
            } else {
                holder.binding.commentSection.setVisibility(View.GONE);
            }
        });

        // Handle send comment button click
        holder.binding.sendComment.setOnClickListener(v -> {
            String comment = holder.binding.commentInput.getText().toString().trim();
            if (!comment.isEmpty()) {
                sendCommentToDatabase(postModel.getPostId(), comment, holder); // Your function to save comment
                holder.binding.commentInput.setText(""); // Clear input
                holder.binding.commentSection.setVisibility(View.GONE);
            }
        });

        // Hide comment section if clicked outside
        holder.itemView.setOnClickListener(v -> {
            if (holder.binding.commentSection.getVisibility() == View.VISIBLE) {
                holder.binding.commentSection.setVisibility(View.GONE);
            }
        });
        holder.bind(postModel);
    }

    private void sendCommentToDatabase(String postId, String comment, ViewHolder holder) {
        FirebaseFirestore db =FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = documentSnapshot.getString("name");
                    // Generate unique ID
                    String commentId = db.collection("posts").document().collection("comments").document().getId();

                    CommentModel commentModel = new CommentModel(commentId, userId, userName, "Mumbai", comment, String.valueOf(new Date()),"2","1");

                    db.collection("posts").document(postId)
                            .collection("comments").document(commentId)
                            .set(commentModel)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(holder.itemView.getContext(), "Success", Toast.LENGTH_SHORT).show();
                            });

                });
    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public void updateData(List<PostModel> filteredList) {
        this.postModelList = filteredList; // Replace current data with new data
        notifyDataSetChanged();  // Notify RecyclerView to refresh
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private PostCardviewBinding binding;

            public ViewHolder(PostCardviewBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bind(PostModel postModel){

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String userId = mAuth.getCurrentUser().getUid();

                binding.username.setText(postModel.getUsername());
                binding.locationDate.setText(postModel.getLocation()+" "+postModel.getDate());
                binding.likeCount.setText(postModel.getLikeCount());
                binding.postTitle.setText(postModel.getPostTitle());

                itemView.setOnClickListener(view -> {
                    Intent i = new Intent(itemView.getContext(), ViewPostScreen.class);
                    i.putExtra("postId", postModel.getPostId());
                    itemView.getContext().startActivity(i);
                });

                Glide.with(itemView.getContext())
                        .load(postModel.getProfileImage())
                        .into(binding.profileImage);

                // Check if imageUrl is null before accessing it
                ImageView[] imageViews = {binding.image1, binding.image2, binding.image3};
                if (postModel.getImages() != null && !postModel.getImages().isEmpty()) {
                    // Create an array of ImageViews for the bindings

                    // Loop through images and set them using Glide
                    for (int i = 0; i < imageViews.length; i++) {
                        if (i < postModel.getImages().size()) {
                            // Load the image if it exists
                            Glide.with(itemView.getContext())
                                    .load(postModel.getImages().get(i))
                                    .into(imageViews[i]);
                            imageViews[i].setVisibility(View.VISIBLE); // Make sure it's visible

                            // Create a final variable for the current index
                            final int currentIndex = i;

                            // Set click listener for full screen
                            imageViews[i].setOnClickListener(view -> {
                                Intent intent = new Intent(itemView.getContext(), ImagePreviewActivity.class);
                                intent.putExtra("imageUrl", postModel.getImages().get(currentIndex));
                                itemView.getContext().startActivity(intent);
                            });
                        } else {
                            // Hide the extra ImageViews
                            imageViews[i].setVisibility(View.GONE);
                        }
                    }
                }
                else {
                    // Create an array of ImageViews for the bindings

                    // Hide all ImageViews if no images are available
                    for (ImageView imageView : imageViews) {
                        imageView.setVisibility(View.GONE);
                    }
                }

                //likes Icon
                binding.likeIcon.setOnClickListener(view -> {
                    String postId = postModel.getPostId();

                    // Reference to the user's likes field
                    db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
                        List<String> likedPosts = (List<String>) documentSnapshot.get("likes");

                        if (likedPosts != null && likedPosts.contains(postId)) {
                            // Post is already liked; perform unlike action
                            likedPosts.remove(postId);
                            db.collection("users").document(userId).update("likes", likedPosts);
                            int like = Integer.parseInt(postModel.getLikeCount()) - 1;
                            db.collection("posts").document(postId).update("likeCount", String.valueOf(like));

                            // Update UI
                            binding.likeIcon.setImageResource(R.drawable.ic_like_white); // Replace with your unselected drawable
                            int newLikeCount = Integer.parseInt(binding.likeCount.getText().toString()) - 1;
                            binding.likeCount.setText(String.valueOf(newLikeCount));
                        } else {
                            // Post is not liked; perform like action
                            if (likedPosts == null) {
                                likedPosts = new ArrayList<>();
                            }
                            likedPosts.add(postId);
                            db.collection("users").document(userId).update("likes", likedPosts);
                            int newLikeCount = Integer.parseInt(binding.likeCount.getText().toString()) + 1;
                            db.collection("posts").document(postId).update("likeCount", String.valueOf(newLikeCount));
                            // Update UI
                            binding.likeIcon.setImageResource(R.drawable.ic_like); // Replace with your selected drawable
                            binding.likeCount.setText(String.valueOf(newLikeCount));
                        }
                    });
                });

                // Check if the post is already liked when binding the view
                db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
                    List<String> likedPosts = (List<String>) documentSnapshot.get("likes");
                    if (likedPosts != null && likedPosts.contains(postModel.getPostId())) {
                        // Post is liked; update icon
                        binding.likeIcon.setImageResource(R.drawable.ic_like); // Replace with your selected drawable
                    } else {
                        // Post is not liked; update icon
                        binding.likeIcon.setImageResource(R.drawable.ic_like_white); // Replace with your unselected drawable
                    }
                });

            }
        }

}
