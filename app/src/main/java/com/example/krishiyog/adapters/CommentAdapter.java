package com.example.krishiyog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.krishiyog.FirebaseManager;
import com.example.krishiyog.R;
import com.example.krishiyog.databinding.CommentCardviewBinding;
import com.example.krishiyog.models.CommentModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

            String rawTimestamp = commentModel.getTimestamp(); // Example: "Fri, 09 Feb 2024 10:15:30 GMT"

            try {
                // Define input format matching your timestamp
                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                inputFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // Ensure correct parsing

                // Parse the timestamp into a Date object
                Date date = inputFormat.parse(rawTimestamp);

                // Define output format (without GMT)
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);
                outputFormat.setTimeZone(TimeZone.getDefault()); // Convert to device local timezone

                String formattedDate = outputFormat.format(date); // Format the date

                binding.timestamp.setText(formattedDate);
            } catch (Exception e) {
                e.printStackTrace();
                binding.timestamp.setText("Invalid Date");
            }

            binding.location.setText(commentModel.getLocation());
            binding.comment.setText(commentModel.getCommentText());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                            .document(commentModel.getUserId())
                                    .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()){
                                                    String url = documentSnapshot.getString("profilePhotoUrl");
                                                    if (url == "" || url == null){
                                                        Glide.with(itemView.getContext())
                                                                .load(com.denzcoskun.imageslider.R.drawable.default_placeholder)
                                                                .into(binding.profileImage);
                                                    }else {
                                                        Glide.with(itemView.getContext())
                                                                .load(url)
                                                                .into(binding.profileImage);
                                                    }
                                                }
                                            });
        }
    }
}
