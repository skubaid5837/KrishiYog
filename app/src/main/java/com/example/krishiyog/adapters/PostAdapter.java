package com.example.krishiyog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.krishiyog.databinding.PostCardviewBinding;
import com.example.krishiyog.models.OrderModel;
import com.example.krishiyog.models.PostModel;

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
        holder.bind(postModel);
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

                binding.username.setText(postModel.getUsername());
                binding.locationDate.setText(postModel.getLocation()+" "+postModel.getDate());
                binding.likeCount.setText(postModel.getLikeCount());
                binding.postTitle.setText(postModel.getPostTitle());

                Glide.with(itemView.getContext())
                        .load(postModel.getProfileImage())
                        .into(binding.profileImage);

                // Check if imageUrl is null before accessing it
                if (postModel.getImages() != null && !postModel.getImages().isEmpty()) {
                    // Create an array of ImageViews for the bindings
                    ImageView[] imageViews = {binding.image1, binding.image2, binding.image3};

                    // Loop through images and set them using Glide
                    for (int i = 0; i < imageViews.length; i++) {
                        if (i < postModel.getImages().size()) {
                            // Load the image if it exists
                            Glide.with(itemView.getContext())
                                    .load(postModel.getImages().get(i))
                                    .into(imageViews[i]);
                            imageViews[i].setVisibility(View.VISIBLE); // Make sure it's visible
                        } else {
                            // Hide the extra ImageViews
                            imageViews[i].setVisibility(View.GONE);
                        }
                    }
                } else {
                    // Create an array of ImageViews for the bindings
                    ImageView[] imageViews = {binding.image1, binding.image2, binding.image3};

                    // Hide all ImageViews if no images are available
                    for (ImageView imageView : imageViews) {
                        imageView.setVisibility(View.GONE);
                    }
                }
            }
        }

}
