package com.example.krishiyog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.krishiyog.FirebaseManager;
import com.example.krishiyog.databinding.CommentCardviewBinding;
import com.example.krishiyog.models.CommentModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    List<CommentModel> commentModelList;

    public CommentAdapter(List<CommentModel> commentModelList) {
        this.commentModelList = commentModelList;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CommentCardviewBinding binding = CommentCardviewBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        CommentModel commentModel = commentModelList.get(position);
        holder.bind(commentModel);
    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    public void updateData(List<CommentModel> commentModelList) {
        this.commentModelList = commentModelList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CommentCardviewBinding binding;

        public ViewHolder(CommentCardviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CommentModel commentModel){

            binding.username.setText(commentModel.getUsername());
            binding.timestamp.setText(commentModel.getTimestamp());
            binding.location.setText(commentModel.getLocation());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                            .document(commentModel.getUserId())
                                    .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()){
                                                    String url = documentSnapshot.getString("profilePhotoUrl");
                                                    if (url != null){
                                                        Glide.with(itemView.getContext())
                                                                .load(url)
                                                                .into(binding.profileImage);
                                                    }
                                                }
                                            });


        }
    }
}
